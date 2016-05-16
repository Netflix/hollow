/**
 * Parses search results from vmsiops for various query patterns. Also, includes a search query
 * builder for elastic-search
 */
function GetJsonFromSearch(getMethodUrl, refDao) {
    this.refDao = refDao;
    this.getUrl = getMethodUrl;

    // Ajax request to pull data from remote source and save in
    // this.data Waits until the request is completed.
    this.updateJsonFromSearch = function() {
        var search_string = this.getUrl;
        var refdao = this.refDao;
        refdao.widget.clear();
        console.log("search_string: " + search_string);
        var jqxhr = $.getJSON(search_string, function(data) {
            refdao.handleJson(data);
        });
    };
}

function ParallelDAOExecutor(functionToInvoke) {
    this.execDAOs = new Array();
    this.functionToInvoke = functionToInvoke;
    this.numCompleted = 0;
    var refFn = this;

    this.add = function(dao) {
        this.execDAOs.push(dao);
    };

    this.checkForCompletion = function() {
        refFn.numCompleted++;
        if (refFn.numCompleted == refFn.execDAOs.length) {
            refFn.functionToInvoke();
        }
    };

    this.run = function() {
        for (var i = 0; i < this.execDAOs.length; i++) {
            refFn.execDAOs[i].widget = new EventChainingWidget(refFn.checkForCompletion);
            refFn.execDAOs[i].updateJsonFromSearch();
        }
    };
}

function SearchDAO(model, widget, refresh) {
    this.protoHostPort = UrlMapper.prototype.getIndexHost();
    this.responseModel = model;
    this.doRefresh = refresh;
    this.widget = widget;
    this.searchQuery = null;

    // Extract fields as required, and update widget
    this.handleJson = function(jsonData) {
        var hits = parseInt(jsonData.hits.total);
        if (hits < 1) {
            this.widget.applyParserData(null);
        } else {
            var responseModel = this.responseModel;
            $.each(jsonData.hits.hits, function(i, hit) {
                responseModel.addHitInfo(hit);
            });
            this.widget.applyParserData(responseModel.getDataModel());
        }
        if (this.doRefresh == true) {
            this.widget.refresh();
            this.responseModel.clear(); //RegexModel
        }
    };

    // Ajax request to pull data from remote source and save in
    // this.data Waits until the request is completed.
    this.updateJsonFromSearch = function(queryObject) {
        var queryObj = this.searchQuery == null ? queryObject : this.searchQuery;
        var search_string = this.protoHostPort + queryObj.toQueryString();
        var refdao = this;
        console.log("searchDAO search_string:" + search_string);

        var jqxhr = $.getJSON(search_string, function(data) {
            refdao.handleJson(data);
        });
    };
}// searchDAO

// NOTE: Key has to be from eventInfo
function KeyValueSearchDAO(widget, eventInfoKey, refresh, keysOnly) {
    this.protoHostPort = UrlMapper.prototype.getIndexHost();
    this.responseModel = new KeyValueModel(keysOnly);
    this.keyPath = eventInfoKey;
    this.doRefresh = refresh;
    this.widget = widget;

    // Extract fields as required, and update widget
    this.handleJson = function(jsonData) {
        var hits = parseInt(jsonData.hits.total);
        if (hits < 1) {
            this.widget.clear();
            return;
        }

        var responseModel = this.responseModel;
        var refdao = this;
        $.each(jsonData.hits.hits, function(i, hit) {
            var key = hit._source.eventInfo[refdao.keyPath];
            var value = new Object();
            value = hit._source.eventInfo;
            value.message = hit._source.message;
            // var idateStr = new Date(time_ms).toLocaleString();
            value.timestamp = new Date(hit._source.eventInfo.timestamp).toLocaleString();
            responseModel.addHitInfo(key, value);
        });

        this.widget.applyParserData(responseModel.getDataModel());
        if (this.doRefresh == true) {
            this.widget.refresh();
        }
    };

}// KeyValueSearchDAO


// if logged using objectNode, root is inputData, followed by the objectName that has an array of key-value
// "_source": {
// "inputData": {
//     "properties": [
//         { 
//           "refreshMode":"CYCLIC",
//           "roleName":"DataPipelineWorker",
//           "propertyName":"vms.cyclic.cluster.adminCacheHolderEnabled.boolean",
//           "propertyValue":"false"
//         },
//         {
//            ..
//         }
function FieldArrayModelDAO(widget, queryObject, fieldNames, refresh) {
    this.protoHostPort = UrlMapper.prototype.getIndexHost();
    this.fieldNames = fieldNames;
    this.doRefresh = refresh;
    this.widget = widget;
    this.searchQuery = queryObject;
    this.responseModel = null;
    this.timestampParserFunc = null;
    this.dataNode = null; // set this, in the above example, it is "properties"

    // Extract fields as required, and update widget
    this.handleJson = function(jsonData) {
        var hits = parseInt(jsonData.hits.total);
        if (hits < 1) {
            if (this.widget != null) {
                this.widget.clear();

                if (this.doRefresh) {
                    this.widget.refresh();
                }
            }
            return;
        }

        var refDao = this;
        $.each(jsonData.hits.hits, function(i, hit) {
            var srcNode = hit._source;
            var inpData = srcNode.inputData;
            var partitionId = srcNode.eventInfo.partitionId;
            var instanceId = srcNode.eventInfo.ec2InstanceId;

            var arrayRoot = inpData[refDao.dataNode];
             $.each(arrayRoot, function(j, fieldsObj) {
                var value = new Object();
                value["partitionId"] = partitionId;
                value["ec2InstanceId"]  = instanceId;
                for ( var fieldName in fieldsObj) {
                    var fieldValArray = fieldsObj[fieldName];
                    if (fieldValArray != null && fieldValArray.length > 0) {
                        value[fieldName] = fieldValArray;
                    }
                }
                refDao.responseModel.addHitInfo(value);
             });
        });

        if (this.widget != null) {
            this.widget.applyParserData(this.responseModel.getDataModel());
        }
        if (this.doRefresh == true && this.widget != null) {
            this.widget.refresh();
        }
    };

    this.updateJsonFromSearch = function() {
        var refdao = this;
        $(refdao.widget.divId).html("<h2>Loading information..</h2> <img src='images/spinner.gif'>");
        var search_string = this.protoHostPort + refdao.searchQuery.toQueryString();
        this.responseModel = new FieldsModel(this.fieldNames, this.timestampParserFunc);
        console.log("fieldmodel_search_string: " + search_string);
        var jqxhr = $.getJSON(search_string, function(data) {
            refdao.handleJson(data);
        });
    };

}// FieldArrayModelDAO




function FieldModelSearchDAO(widget, queryObject, fieldNames, refresh) {
    this.protoHostPort = UrlMapper.prototype.getIndexHost();
    this.fieldNames = fieldNames;
    this.doRefresh = refresh;
    this.widget = widget;
    this.searchQuery = queryObject;
    this.responseModel = null;
    this.timestampParserFunc = null;

    // Extract fields as required, and update widget
    this.handleJson = function(jsonData) {
        var hits = parseInt(jsonData.hits.total);
        if (hits < 1) {
            if (this.widget != null) {
                this.widget.clear();

                if (this.doRefresh) {
                    this.widget.refresh();
                }
            }
            return;
        }

        var refdao = this;
        $.each(jsonData.hits.hits, function(i, hit) {
        var value = new Object();
            // collapse eventInfoFields with message
            if (hit.fields == null) {
                value = hit._source.eventInfo;
                value.message = hit._source.message;
            } else {
                var fieldsObj = hit.fields;
                for ( var fieldName in fieldsObj) {
                    var fieldValArray = fieldsObj[fieldName];
                    if (fieldValArray.length > 0) {
                        value[fieldName] = fieldValArray[0];
                    }
                }
            }
            refdao.responseModel.addHitInfo(value);
        });

        if (this.widget != null) {
            this.widget.applyParserData(this.responseModel.getDataModel());
        }
        if (this.doRefresh == true && this.widget != null) {
            this.widget.refresh();
        }
    };

    this.updateJsonFromSearch = function() {
        var refdao = this;
        var search_string = this.protoHostPort + refdao.searchQuery.toQueryString();
        this.responseModel = new FieldsModel(this.fieldNames, this.timestampParserFunc);
        console.log("fieldmodel_search_string:" + search_string);
        var jqxhr = $.getJSON(search_string, function(data) {
            refdao.handleJson(data);
        });
    };

}

function SearchCountDAO(widget, queryObject, timeSeriesName, refresh) {
    this.protoHostPort = UrlMapper.prototype.getIndexHost();
    this.timeSeriesModel = new TimeSeriesModel(timeSeriesName);
    this.doRefresh = refresh;
    this.widget = widget;
    this.searchQuery = queryObject;
    this.prevCount = 0;
    this.consecutiveZeroCount = 0;
    this.MAX_CONSECUTOVE_ZERO_COUNT = 10;
    this.stop = false;

    this.reset = function() {
        this.timeSeriesModel = new TimeSeriesModel(timeSeriesName);
        this.stop = false;
    };

    // Extract fields as required, and update widget
    this.handleJson = function(timestamp, jsonData) {
        var count = jsonData.hits.total;
        if (count > 0 && (count - this.prevCount == 0)) {
            this.consecutiveZeroCount++;
            var factor = queryObject.live ? 10 : 1;
            if (this.consecutiveZeroCount == this.MAX_CONSECUTOVE_ZERO_COUNT * factor) {
                this.stop = true;
            }
        } else {
            this.consecutiveZeroCount = 0;
        }
        this.prevCount = count;
        this.timeSeriesModel.addHitInfo(timestamp, count);
        this.widget.applyParserData(this.timeSeriesModel.getDataModel());
        if (this.doRefresh == true) {
            this.widget.refresh();
        }
    };

    // Ajax request to pull data from remote source and save in
    // this.data Waits until the request is completed.
    this.updateJsonFromSearch = function() {
        if (this.stop) {
            this.widget.applyParserData(this.timeSeriesModel.getDataModel());
            return;
        }

        var refdao = this;
        var timeStampToUse = this.searchQuery.getTimeStamp();
        var search_string = this.protoHostPort + refdao.searchQuery.toQueryString();
        console.log("count_search_string:" + search_string);
        var jqxhr = $.getJSON(search_string, function(data) {
            console.log("Data:" + JSON.stringify(data));
            refdao.handleJson(timeStampToUse, data);
        });
    };
}// SearchCountDAO

function IndexStartTimeDAO(query, callback) {
    this.protoHostPort = UrlMapper.prototype.getIndexHost();
    this.callback = callback;
    this.searchQuery = query;

    this.searchQuery.size = "1";
    this.searchQuery.setSort("eventInfo.timestamp");

    // Extract fields as required, and update widget
    this.handleJson = function(timestamp, jsonData) {
        if (jsonData.hits.total == 0) {
            alert("Could not get timeStamp, please refresh after a minute");
            return;
        }

        var timeStampToUpdate = jsonData.hits.hits[0]._source.eventInfo.timestamp;
        if (this.callback && typeof (this.callback) === "function") {
            this.callback(timeStampToUpdate);
        }
    };

    // Ajax request to pull data from remote source and save in
    // this.data Waits until the request is completed.
    this.updateJsonFromSearch = function() {
        var refdao = this;
        var search_string = this.protoHostPort + refdao.searchQuery.toQueryString();
        console.log("index-start search_string:" + search_string);
        var jqxhr = $.getJSON(search_string, function(data) {
            console.log("Data:" + JSON.stringify(data));
            refdao.handleJson(refdao.searchQuery.getTimeStamp(), data);
        });
    };
}// SearchCountDAO

function StatsDAO(widget, keysOnly) {
    this.responseModel = new IndexStatsModel(keysOnly);
    this.widget = widget;
    this.indexTypeKey = null;

    // Extract fields as required, and update widget
    this.handleJson = function(statsJson) {
        var responseModel = this.responseModel;
        // console.log(statsJson.indices);
        for ( var iname in statsJson.indices) {
            if (!statsJson.indices.hasOwnProperty(iname)) {
                continue;
            }
            var hit = statsJson.indices[iname];
            var n = iname.lastIndexOf("_");
            var time_ms = 0;
            // vms-cycle uses: 2014010914530340 -> 2014/01/09/ 14:53-0340
            if (n != -1) {
                time_ms = Number(iname.substring(n + 1, iname.length));
                // console.log("n=" +n + ", time=" + timeMS);
                // idate = new Date(timeMS).toLocaleString();
            }
            var idateStr = new Date(time_ms).toLocaleString();
            if (iname.substring(0, 3) === "vms") {
                var cycleId = iname.substring(n + 1, iname.length);
                idateStr = cycleId.substring(0, 4) + "/" + cycleId.substring(4, 6) + "/" + cycleId.substring(6, 8) + " " + cycleId.substring(8, 10) + ":"
                        + cycleId.substring(10, 12) + " UTC";
            }

            var indexStats = {
                name : iname,
                count : hit.total.docs.count,
                size : hit.total.store.size_in_bytes,
                version_id : time_ms,
                date : idateStr
            };
            // console.log("mapping [" + iname + "]=" +
            // JSON.stringify(indexStats));
            responseModel.addHitInfo(indexStats);
        }

        var dataModel = responseModel.getDataModel();
        if (this.indexTypeKey == null) {
            this.widget.applyParserData(dataModel);
        } else {
            var sortedData = dataModel[this.indexTypeKey].sort(function(a, b) {
                return b.version_id - a.version_id;
            });
            this.widget.applyParserData(sortedData);
        }
    };

    // Ajax request to pull data from remote source and save in
    // this.data Waits until the request is completed.
    this.updateJsonFromSearch = function(indexPrefix, indexKey) {
        this.indexTypeKey = indexKey;
        var hostName = UrlMapper.prototype.getIndexHost();
        var search_string = (!indexPrefix ? hostName + "_stats" : hostName + indexPrefix + "*/_stats");
        var refdao = this;
        console.log("statsDAO search_string:" + search_string);
        var jqxhr = $.getJSON(search_string, function(data) {
            refdao.handleJson(data);
        });
    };
}// statsDAO

function MappingDAO(indexname, widget) {
    this.protoHostPortQuery = UrlMapper.prototype.getIndexHost() + indexname + "/_mapping";
    this.responseModel = new IndexMappingModel();
    this.widget = widget;

    // Extract fields as required, and update widget
    this.handleJson = function(jsonData) {
        var responseModel = this.responseModel;
        var indexInfo = jsonData[indexname];
        var mappingData = indexInfo.mappings;
        for ( var index in mappingData) {
            if (mappingData.hasOwnProperty(index)) {
                var obj = mappingData[index];
                responseModel.addHitInfo(index, obj);
            }
        }
        this.widget.applyParserData(responseModel.getDataModel());
    };

    // Ajax request to pull data from remote source and save in
    // this.data Waits until the request is completed.
    this.updateJsonFromSearch = function() {
        var search_string = this.protoHostPortQuery;
        var refdao = this;
        console.log("search_string: " + search_string);
        var jqxhr = $.getJSON(search_string, function(data) {
            refdao.handleJson(data);
        });
    }
}// mappingDAO

function AliasesDAO(widget) {
    this.protoHostPortQuery = UrlMapper.prototype.getIndexHost() + "_stats";
    this.responseModel = new IndexAliasesModel();
    this.widget = widget;

    // Extract fields as required, and update widget
    this.handleJson = function(jsonData) {
        var responseModel = this.responseModel;
        $.each(jsonData, function(i, hit) {
            responseModel.addHitInfo(i);
            // console.log("mapping [" + i + "]=" + JSON.stringify(hit));
        });

        this.widget.applyParserData(responseModel.getDataModel());
    }

    // Ajax request to pull data from remote source and save in
    // this.data Waits until the request is completed.
    this.updateJsonFromSearch = function() {
        var search_string = this.protoHostPortQuery;
        var refdao = this;
        console.log("search_string: " + search_string);
        var jqxhr = $.getJSON(search_string, function(data) {
            refdao.handleJson(data);
        });
    }
}// aliasesDAO

function SearchAggregationDAO(widget, queryObject, refresh) {
    this.protoHostPort = UrlMapper.prototype.getIndexHost();
    this.doRefresh = refresh;
    this.widget = widget;
    this.searchQuery = queryObject;
    this.responseModel = null;
    this.timestampParserFunc = null;

    // Extract fields as required, and update widget
    this.handleJson = function(jsonData) {
        var hits = parseInt(jsonData.hits.total);
        if (hits < 1) {
            if (this.widget != null) {
                this.widget.clear();

                if (this.doRefresh) {
                    this.widget.refresh();
                }
            }
            return;
        }

        var refdao = this;
        $.each(jsonData.aggregations.aggregate.buckets, function(i, bucket) {
            refdao.responseModel.addHitInfo(bucket);
        });

        if (this.widget != null) {
            this.widget.applyParserData(this.responseModel.getDataModel());
        }
        if (this.doRefresh == true && this.widget != null) {
            this.widget.refresh();
        }
    };

    this.updateJsonFromSearch = function() {
        var refdao = this;
        var search_string = this.protoHostPort + refdao.searchQuery.toQueryString();
        this.responseModel = new FieldsModel(["key", "doc_count"], this.timestampParserFunc);
        console.log("fieldmodel_search_string:" + search_string);
        var jqxhr = $.getJSON(search_string, function(data) {
            refdao.handleJson(data);
        });
    };

}// search aggregation


/*
 * @alias SearchQuery Object that produces the query string for Lucene @param starttime_iso8601
 * Start time for the query. Query is always time based @param search_type For search_type=count, or
 * can be null (ignored)
 */
function SearchQuery(starttime_millis, interval_seconds, search_type) {
    this.searchTerms = [];
    this.startTime = null;
    this.endTime = null;
    this.sort = null;
    this.live = false;
    this.intervalSeconds = interval_seconds;
    this.searchType = search_type;
    this.size = null;
    this.numEndPeriod = 0;
    this.indexName = null;
    this.indexType = null;
    this.fields = null;
    this.aggregate = null;

    if (!starttime_millis) {
        this.startTime = null;
        this.endTime = null;
    } else {
        this.startTime = starttime_millis;
        this.endTime = this.startTime + this.intervalSeconds * 1000;
    }

    this.isPastEndTime = function(ts) {
        return (this.startTime > ts); // not endtime
    };

    this.setNextEndTimePeriod = function(max_endtime) {
        this.numEndPeriod++;
        this.endTime += this.intervalSeconds * 1000;

        if (!max_endtime) { // takes care of null and undefined
        } else {
            if (this.endTime > max_endtime) {
                this.endTime = max_endtime;
            }
        }
        return this;
    };

    this.add = function(term) {
        if (term.length > 0) {
            this.searchTerms.push(term);
        }
        return this;
    };

    this.setSize = function(num) {
        this.size = num;
        return this;
    };

    this.setSort = function(name) {
        this.sort = name;
    };

    this.getTimeStamp = function() {
        return !this.endTime ? 0 : this.endTime;
    };

    this.toQueryString = function() {
        var searchString = "";

        if (this.indexName) {
            searchString += this.indexName;
            if (this.indexType) {
                searchString += "/" + this.indexType;
            }
        }

        searchString += "/_search?";
        var query = "";
        for (var i = 0; i < this.searchTerms.length; i++) {
            if (i != 0) {
                query += "%20AND%20";
            }
            query += this.searchTerms[i];
        }

        if (query.length > 0) {
            searchString += "q=";
            searchString += query;
        }

        if (this.startTime != null) {
            var timestamp = "eventInfo.timestamp:[";
            timestamp += this.startTime;
            timestamp += " TO ";
            timestamp += this.endTime;
            timestamp += "]";

            searchString += " AND " + timestamp;
        }

        if (this.fields != null && this.fields.length > 0) {
            searchString += "&fields=" + this.fields;
        }

        if(this.aggregate != null) {
            searchString += '&source={"aggregations":{"aggregate":{"terms": {"field":"' + this.aggregate + '", "size": 0, "order":{ "_count":"desc"}}}}}}}}}';
            this.searchType = "count";
        }

        if (this.searchType != null) {
            searchString += "&search_type=" + this.searchType;
        }

        if (this.size != null) {
            searchString += "&size=" + this.size;
        }

        if (this.sort != null) {
            searchString += "&sort=" + this.sort;
        }
        searchString += "&pretty=true";
        console.log("Inside SearchQuery " + searchString);

        return searchString;
    };

    this.ms_to_iso8601 = function(milliseconds) {
        /*
         * From: logstash.js From:
         * https://developer.mozilla.org/en/JavaScript/Reference/global_objects/date#Example.3a_ISO_8601_formatted_dates
         */
        var d = new Date(milliseconds);
        function pad(n) {
            return n < 10 ? '0' + n : n;
        }
        return d.getUTCFullYear() + '-' + pad(d.getUTCMonth() + 1) + '-' + pad(d.getUTCDate()) + 'T' + pad(d.getUTCHours()) + ':' + pad(d.getUTCMinutes())
                + ':' + pad(d.getUTCSeconds()) + 'Z';
    };
}// SearchQuery

