/**
 * ResponseModels hold the data, to be rendered by Widgets
 */

// --------------------------------------------------------------------
// ResponseModelFactory
// --------------------------------------------------------------------
function ResponseModelsFactory() {
}

ResponseModelsFactory.prototype.getModel = function(name, args) {
    if (name == "IndexTypeResponseModel") {
        return new IndexTypeResponseModel();
    } else if (name == "IndexAliasesModel") {
        return new IndexAliasesModel();
    } else if (name == "RegexModel") {
        return new RegexSourceModel(args);
    } else if (name == "TimeSeriesModel") {
        return new TimeSeriesModel(args);
    } else if (name == "KeyValueModel") {
        return new KeyValueModel(args);
    } else if (name == "InputDataVersionModel") {
        return new InputDataVersionModel(args);
    } else {
        alert("JS error ResponseModelsFactory -- Unknown mode: " + name);
    }
    return new IndexTypeResponseModel();
};

function IndexTypeResponseModel() {
    this.dataModel = new Object();
    this.jsonData = new Object;

    this.addHitInfo = function(jsonHitData) {
        // console.log("jsondata=" + JSON.stringify(jsonHitData));
        this.jsonData = jsonHitData;
        var fnresults = this.dataModel;

        var index = jsonHitData._index;
        var type = jsonHitData._type;
        var value = jsonHitData._source;
        if (value == null) {
            value = jsonHitData.fields;
        }
        var key = index + "/" + type;
        // console.log("key = " + key);

        if (fnresults.hasOwnProperty(key)) {
            fnresults[key].push(value);
        } else {
            fnresults[key] = new Array();
            fnresults[key].push(value);
        }
    };

    this.getDataModel = function() {
        return this.dataModel;
    };
}

function IndexAliasesModel() {
    this.dataModel = new Object();
    this.jsonData = new Object;

    this.addHitInfo = function(jsonHitData) {
        var indexName = jsonHitData.toString();
        console.log("jsondata=" + indexName);
        var fnresults = this.dataModel;
        var n = indexName.lastIndexOf("_");
        if (n == -1) {
            console.log("Incompatible Index Format: " + indexName);
            return;
        }
        var key = indexName.substring(0, n);
        var value = indexName;
        // console.log("key = " + key);
        if (fnresults.hasOwnProperty(key)) {
            fnresults[key].push(value);
        } else {
            fnresults[key] = new Array();
            fnresults[key].push(value);
        }
    };

    this.getDataModel = function() {
        return this.dataModel;
    };
}

function IndexStatsModel(keysOnly) {
    this.keysOnly = keysOnly;
    this.dataModel = new Object();

    this.addHitInfo = function(jsonHitData) {
        var indexName = jsonHitData.name;

        // console.log("jsondata=" + indexName);
        var fnresults = this.dataModel;
        var n = indexName.lastIndexOf("_");
        if (n == -1) {
            console.log("Incompatible Index Format: " + indexName);
            return;
        }

        var key = indexName.substring(0, n);
        var value = jsonHitData;
        // console.log("key = " + key);
        if (fnresults.hasOwnProperty(key)) {
            fnresults[key].push(value);
        } else {
            fnresults[key] = new Array();
            fnresults[key].push(value);
        }

    };

    this.getDataModel = function() {
        var fnresults = this.dataModel;
        var resKeys = Object.keys(this.dataModel);

        for (var i = 0; i < resKeys.length; i++) {
            var iname = resKeys[i];
            if (fnresults.hasOwnProperty(iname)) {
                var valArr = fnresults[iname];
                valArr.sort(function(o1, o2) {
                    return (Number(o2.version_id) - Number(o1.version_id));
                });
                fnresults[iname] = valArr;
            }
        }

        return (!this.keysOnly ? fnresults : resKeys);
    };
}

function KeyValueModel(keysOnly) {
    this.keysOnly = keysOnly;
    this.dataModel = new Object();

    this.addHitInfo = function(dataKey, jsonHitData) {
        var key = dataKey;
        var value = jsonHitData;
        var fnresults = this.dataModel;
        // console.log("key = " + key);
        if (fnresults.hasOwnProperty(key)) {
            fnresults[key].push(value);
        } else {
            fnresults[key] = new Array();
            fnresults[key].push(value);
        }
    };

    this.getDataModel = function() {
        return (!this.keysOnly ? this.dataModel : Object.keys(this.dataModel));
    };
}

function ShardInfoInputModel() {
    this.dataModel = new Array();

    this.addHitInfo = function(jsonData) {
        var sourceData = jsonData._source;
        var inputData = sourceData.inputData;

        for ( var mutationGroup in inputData) {
            if (inputData.hasOwnProperty(mutationGroup)) {
                var hit = inputData[mutationGroup];
                var shards = hit.shards;
                var shardInfo = shards[0];
                for ( var accessInfo in shardInfo) {
                    var mutationGroupInfo = new Object();
                    var val = shardInfo[accessInfo];
                    var sourceInfo = val.sourceManifestEntry;
                    var fileAccessItem = val.fileAccessItem;
                    var srcS3Info = sourceInfo.persistenceManifest;

                    mutationGroupInfo["Group"] = mutationGroup;
                    mutationGroupInfo["Source"] = new String(sourceInfo.dataSource).toLowerCase();
                    mutationGroupInfo["keybase"] = sourceInfo.keybase;
                    mutationGroupInfo["s3Key"] = srcS3Info == null ? "" : srcS3Info.remoteItemName.substring(sourceInfo.keybase.length, srcS3Info.remoteItemName.length);
                    if (mutationGroupInfo.Source == "coldstart") {
                        mutationGroupInfo["version"] = sourceInfo.coldstartVersion;
                        mutationGroupInfo["published"] = transformLongToDate(Number(srcS3Info.uploadPublishTimestamp));
                    } else {
                        mutationGroupInfo["version"] = fileAccessItem.simpleDBVersionString;
                        mutationGroupInfo["published"] = transformLongToDate(Number(fileAccessItem.simpleDBVersionString));
                    }
                    mutationGroupInfo["startMutation"] = sourceInfo.startMessageId;
                    mutationGroupInfo["endMutation"] = sourceInfo.endMessageId;
                    if(sourceInfo.endMessageId == "-1") {
                        mutationGroupInfo["endMutation"] = sourceInfo.startMessageId;
                    }
                    mutationGroupInfo["Mutations"] = sourceInfo.endMessageId - sourceInfo.startMessageId;
                    mutationGroupInfo["pinned"] = sourceInfo.pinned;
                    this.dataModel.push(mutationGroupInfo);
                }
            }
        }
    };

    this.getDataModel = function() {
        return this.dataModel;
    };
}

function ShardInfoOutputModel() {
    this.dataModel = new Array();

    this.addHitInfo = function(jsonData) {
        var sourceData = jsonData._source;
        var inputData = sourceData.inputData;

        for ( var mutationGroup in inputData) {
            if (inputData.hasOwnProperty(mutationGroup)) {
                var hit = inputData[mutationGroup];
                var shards = hit.shards;
                for ( var partitionId in shards) {
                    if (!shards.hasOwnProperty(partitionId)) {
                        continue;
                    }
                    var shardInfo = shards[partitionId];

                    for ( var accessInfo in shardInfo) {
                        var mutationGroupInfo = new Object();
                        var val = shardInfo[accessInfo];
                        var sourceInfo = val.sourceManifestEntry;
                        var fileAccessItem = val.fileAccessItem;

                        var sizeMB = fileAccessItem.s3NumBytes / (1024 * 1024);
                        mutationGroupInfo["partitionId"] = partitionId;
                        mutationGroupInfo["MutationGroup"] = mutationGroup;
                        mutationGroupInfo["dataSource"] = new String(sourceInfo.dataSource).toLowerCase();
                        mutationGroupInfo["keybase"] = fileAccessItem.simpleDBKeybase;
                        mutationGroupInfo["version"] = fileAccessItem.simpleDBVersionString;
                        mutationGroupInfo["s3Key"] = fileAccessItem.s3ItemName.substring(fileAccessItem.simpleDBKeybase.length, fileAccessItem.s3ItemName.length);
                        mutationGroupInfo["s3BucketName"] = fileAccessItem.s3BucketName;
                        mutationGroupInfo["size(MB)"] = sizeMB.toFixed(4);

                        this.dataModel.push(mutationGroupInfo);
                    }
                }
            }
        }
    };

    this.getDataModel = function() {
        return this.dataModel;
    };
}

function ShardIndexData(mutationGroup) {
    this.mutationGroup = mutationGroup;
    this.coldstartVersion = "";
    this.coldstartStartMutationId = 0;
    this.coldstartEndMutationId = 0;
    this.eventFileVersion = "";
    this.eventStartMutationId = 0;
    this.eventEndMutationId = 0;

}

function ShardIndexSearchModel() {
    this.indexModel = new Object();

    this.addHitInfo = function(jsonData) {
        var sourceData = jsonData._source;
        var inputData = sourceData.inputData;

        for ( var mutationGroup in inputData) {
            if (inputData.hasOwnProperty(mutationGroup)) {
                if (mutationGroup in this.indexModel) {
                    continue;
                }

                var indexData = new ShardIndexData(mutationGroup);

                var hit = inputData[mutationGroup];
                var shards = hit.shards;
                for ( var partitionId in shards) {
                    if (!shards.hasOwnProperty(partitionId) || (mutationGroup in this.indexModel)) {
                        continue;
                    }
                    var shardInfo = shards[partitionId];

                    for ( var accessInfo in shardInfo) {
                        var val = shardInfo[accessInfo];

                        var sourceInfo = val.sourceManifestEntry;
                        var fileAccessItem = val.fileAccessItem;
                        var dataSource = new String(sourceInfo.dataSource).toLowerCase();

                        if (dataSource == "events") {
                            indexData.eventFileVersion = fileAccessItem.simpleDBVersionString;
                            indexData.eventStartMutationId = sourceInfo.startMutationId;
                            indexData.eventEndMutationId = sourceInfo.endMutationId;
                        } else {
                            indexData.coldstartVersion = sourceInfo.coldstartVersion;
                            indexData.coldstartStartMutationId = sourceInfo.startMutationId;
                            indexData.coldstartEndMutationId = sourceInfo.endMutationId;
                        }

                    }
                }
                this.indexModel[mutationGroup] = indexData;
            }
        }
    };

    this.getDataModel = function() {
        return this.indexModel;
    };
}

function IndexMappingModel() {
    this.dataModel = new Object();
    this.jsonData = new Object;

    this.addHitInfo = function(entityName, jsonSchema) {
        // console.log("entity=" + entityName + ", schema=" + jsonSchema);
        var fnresults = this.dataModel;
        var key = entityName;
        var value = jsonSchema;
        // console.log("key = " + key);
        if (fnresults.hasOwnProperty(key)) {
            fnresults[key].push(value);
        } else {
            fnresults[key] = new Array();
            fnresults[key].push(value);
        }
    };

    this.getDataModel = function() {
        return this.dataModel;
    };
}

function transformLongToDate(timestamp) {
    var date = new Date(timestamp);
    var dateString = date.toLocaleTimeString("en-US", {
        hour12 : false
    });
    result = (date.getMonth() + 1) + "/" + date.getDate() + " " + dateString;
    return result;
}

function FieldsModel(fieldNames, timeStampParserFunc) {
    this.dataModel = new Array();
    this.fieldNames = fieldNames;
    this.maxSize = null;
    this.timeStampParserFn = timeStampParserFunc;

    this.addHitInfo = function(jsonHitData) {
        var row = new Object();
        var fields = this.fieldNames;
        for (var index = 0; index < fields.length; index++) {
            var result = jsonHitData[fields[index]];
            if (result != null) {
                // hard-coded
                var fieldName = new String(fields[index]);
                if (fieldName.toLowerCase() == "timestamp") {
                    if (!this.timeStampParserFn) {
                        var date = new Date(result);
                        var dateString = date.toLocaleTimeString("en-US", {
                            hour12 : false
                        });
                        result = (date.getMonth() + 1) + "/" + date.getDate() + " " + dateString;
                    } else {
                        result = this.timeStampParserFn(result);
                    }
                }
                row[fields[index]] = result;
            }
        }
        if (this.maxSize != null) {
            if (this.dataModel.length == this.maxSize) {
                this.dataModel.shift();
            }
        }
        this.dataModel.push(row);
    };

    this.getRowFieldEqualTo = function(fieldName, value) {
        if (this.fieldNames.indexOf(fieldName) == -1) {
            return null;
        }
        var key = fieldName;
        var val = value;
        for (var index = 0; index < this.dataModel.length; index++) {
            var obj = this.dataModel[index];
            if (obj[key] == val) {
                return obj;
            }
        }
        return null;
    };

    this.filter = function(fieldName, value) {
        var result = new Array(); 
        if (this.fieldNames.indexOf(fieldName) == -1) {
            return null;
        }
        var key = fieldName;
        var val = value;
        for (var index = 0; index < this.dataModel.length; index++) {
            var obj = this.dataModel[index];
            if (obj[key] == val) {
                result.push(obj);
            }
        }
        return result;
    };
    
    this.rowFieldEquals = function(fieldName, value) {
        var obj = this.getRowFieldEqualTo(fieldName, value);
        if(obj == null) {
            return false;
        }
        return true;
    };

    this.rowFieldContains = function(fieldName, value) {
        if (this.fieldNames.indexOf(fieldName) == -1) {
            return false;
        }
        var key = fieldName;
        var val = value;
        for (var index = 0; index < this.dataModel.length; index++) {
            var obj = this.dataModel[index];
            if (obj[key].indexOf(val) > 0) {
                return true;
            }
        }
        return false;
    };    

    this.getDataModel = function() {
        return this.dataModel;
    };
}

function DataOperator(dataModel) {
    this.inpDataModel = dataModel;
    this.outDataModel = null;

    // assumes inp is an array (e.g. output of RegexSourceModel),
    // out is a map of groupedByItem to array,
    this.groupBy = function(fieldName) {
        this.outDataModel = new Object();
        var key = fieldName;

        for (var index = 0; index < this.inpDataModel.length; index++) {
            var obj = this.inpDataModel[index];
            if (obj.hasOwnProperty(key)) {
                var groupByVal = obj[key];
                if (this.outDataModel.hasOwnProperty(groupByVal)) {
                    this.outDataModel[groupByVal].push(obj);
                } else {
                    var result = new Array();
                    result.push(obj);
                    this.outDataModel[groupByVal] = result;
                }
            }
        }
        return new DataOperator(this.outDataModel);
    };

    // assumes inp is an array (e.g. output of RegexSourceModel),
    // out is a map of keyField to valueField 
    this.extractField = function(keyField, valueField) {
        this.outDataModel = new Object();

        for (var index = 0; index < this.inpDataModel.length; index++) {
            var obj = this.inpDataModel[index];

            if (obj.hasOwnProperty(keyField)) {
                this.outDataModel[obj[keyField]] = obj[valueField];
            }
        }
        return new DataOperator(this.outDataModel);
    };

    // assumes inp is an array (e.g. output of RegexSourceModel),
    // out is an array of objects
    this.sort = function(fieldName, comparatorFn) {
        if(!comparatorFn) {
            comparatorFn = function(a, b) {
                return a[fieldName] - b[fieldName];
            }
        }
        this.inpDataModel.sort(comparatorFn);
        return this.inpDataModel;
    }
    
    // assumes input is map from id ->array, with a number field, e.g, output of groupBy
    // output is key-value
    this.min = function(fieldName) {
        this.outDataModel = new Object();
        for ( var key in this.inpDataModel) {
            if (this.inpDataModel.hasOwnProperty(key)) {
                var valArray = this.inpDataModel[key];
                if (valArray.length == 0) {
                    continue;
                }
                var firstObj = valArray[0];
                var minVal = Number(firstObj[fieldName]);

                for (var index = 0; index < valArray.length; index++) {
                    var obj = valArray[index];
                    if (Number(obj[fieldName]) < minVal) {
                        minVal = Number(obj[fieldName]);
                    }
                }
                this.outDataModel[key] = minVal;
            }
        }
        return new DataOperator(this.outDataModel);
    };

    // assumes input as key-value (e.g., output from min)
    // output is a {mean: Number, sd: Number}
    this.stats = function() {
        var totalNum = 0;
        var sum = 0;
        var sq_sum = 0;

        for ( var key in this.inpDataModel) {
            if (this.inpDataModel.hasOwnProperty(key)) {
                var val = Number(this.inpDataModel[key]);
                totalNum++;
                sum += val;
                sq_sum += (val * val);
            }
        }

        if (totalNum == 0) {
            return {
                mean : 0.0,
                sd : 0.0
            };
        }
        var avg = sum / totalNum;
        var variance = (sq_sum / totalNum) - (avg * avg);
        return {
            mean : avg,
            sd : Math.sqrt(variance)
        };
    };

    // assumes input as key-value (e.g., output from min)
    // output is % difference from mean
    this.meanDiffPercent = function() {
        var stats = this.stats();
        var avg = stats.mean;
        this.outDataModel = new Object();

        for ( var key in this.inpDataModel) {
            if (this.inpDataModel.hasOwnProperty(key)) {
                var val = Number(this.inpDataModel[key]);
                var vdiff = 100.0 * (val - avg) / (avg);
                this.outDataModel[key] = vdiff;
            }
        }
        return new DataOperator(this.outDataModel);
    };

    // assumes input as key-value (e.g., output from min)
    // assumes keys can be sorted correctly without a function
    // output is % difference from previous value
    this.prevDiff = function(usePercent) {
        this.outDataModel = new Object();
        var keys = new Array();

        for ( var key in this.inpDataModel) {
            if (this.inpDataModel.hasOwnProperty(key)) {
                keys.push(key);
            }
        }

        keys.sort();
        var prevVal = 0.0;
        if (keys.length > 0) {
            prevVal = Number(this.inpDataModel[keys[0]]);
        }

        for (var index = 0; index < keys.length; index++) {
            var srtKey = keys[index];
            var valStr = this.inpDataModel[srtKey];
            if (!valStr) {
                continue;
            }
            var val = Number(valStr);
            var vdiff = (val - prevVal);
            if(usePercent) {
                vdiff = 100.0 * (val - prevVal) / (prevVal);
            }
            this.outDataModel[srtKey] = vdiff;
            prevVal = val;

        }
        return new DataOperator(this.outDataModel);
    };
}

function RegexSourceModel(args) {
    this.sourceField = args.sourceField;
    this.fieldsRegex = args.fieldsRegex;
    this.dataModel = new Array();

    this.clear = function() {
        this.dataModel = new Array();
    }

    this.addHitInfo = function(jsonHitData) {
        var timestamp = jsonHitData._source.eventInfo["timestamp"]; // hard-coded
        var cycleId = jsonHitData._source.eventInfo["currentCycle"];
        var instanceId = jsonHitData._source.eventInfo["instanceId"];
        var logline = jsonHitData._source[this.sourceField];
        var tokens = logline.split(" ");

        var date = new Date(timestamp);
        var dateString = date.toLocaleTimeString();

        var row = new Object();
        row["timestamp"] = dateString;
        row["cycleId"] = cycleId;
        row["instanceId"] = instanceId;

        for ( var i in tokens) {
            var token = tokens[i];
            for ( var field in this.fieldsRegex) {
                var regexInfo = this.fieldsRegex[field];
                var result = regexInfo[0].exec(token);
                if (result != null) {
                    row[field] = result[regexInfo[1]];
                    break;
                }
            }
        }

        // hardcoded
        var sizeInBytes = row["filesize(bytes)"];
        var durationInMS = row["duration(ms)"];

        if (sizeInBytes && durationInMS) {
            var mbps = (sizeInBytes * 8 * 1000) / (durationInMS * 1000 * 1000);
            row["Mbps"] = mbps.toFixed(2);
        }

        this.dataModel.push(row);
    };

    this.getDataModel = function() {
        return this.dataModel;
    };
}


function CircuitBreakerDataModel( ) {
    this.dataModel = new Array();
    this.RULE_NAME = "_Rule name";
    
    this.addHitInfo = function(jsonHitData) {
        var timestamp = jsonHitData._source.eventInfo["timestamp"]; // hard-coded
        var cycleId = jsonHitData._source.eventInfo["currentCycle"];
        var logline = jsonHitData._source["message"];
        var st =  logline.indexOf("Details=");
        var nd =  logline.indexOf(":");
        if(st == -1 || nd == -1) {
            return;
        }
        var details = logline.substring(st+8, nd);
        var tokens = details.split("_");
        if(tokens.length != 2) {
            return;
        }
        var serverCache = tokens[0];
        var country = tokens[1];
        st = logline.indexOf("{");
        if(st == -1) {
            return;
        }

        var date = new Date(timestamp);
        var dateString = date.toLocaleTimeString();

        var mapString = logline.substring(st);
        tokens = mapString.split(",");

        var currRuleId = 0;
        for ( var indx in tokens) {
            var keyValue = tokens[indx].split("=");
            st = keyValue[0].indexOf(this.RULE_NAME);
            if(st == -1) {
                continue;
            }

            var row = new Object();
            row["ServerCache"] = serverCache;
            row["Country"] = country;
            row["timestamp"] = dateString;
            row["partitionId"] = partitionId;
            row["cycleId"] = cycleId;
            row["RuleName"] = keyValue[1];
            indx++; // not defensive
            keyValue = tokens[indx].split("=");
            var resultCode = keyValue[1];
            row["ResultCode"] = resultCode;
            if(resultCode !== "Passed") {
                indx++;
                keyValue = tokens[indx].split("=");
                row["FailedIDs"] = keyValue[1].replace(/;/g," ");
                this.dataModel.push(row);
            }
            currRuleId++;
        }
    };

    this.getDataModel = function() {
        return this.dataModel;
    };
}


function TimeSeriesModel(displayname) {
    this.displayName = displayname;
    this.dataPoints = [];
    this.cumulative = false;
    this.sum = 0;
    this.timeZoneOffset = 0;

    var date = new Date();
    this.timeZoneOffset = date.getTimezoneOffset() * 60 * 1000;

    this.setCumulative = function() {
        this.cumulative = true;
    };

    this.clear = function() {
        this.dataPoints = new Array();
    };

    this.toLocalMillis = function(utcMillis) {
        return (utcMillis - this.timeZoneOffset);
    };

    this.addHitInfo = function(timestamp, val) {
        var value = val;
        this.sum += value;
        if (this.cumulative == true) {
            value = this.sum;
        }
        this.dataPoints.push([ this.toLocalMillis(timestamp), value ]);
    };

    this.getDataModel = function() {
        return {
            name : this.displayName,
            timeSeries : this.dataPoints
        };
    };
}

function InputDataVersionModel() {
    this.dataModel = [];

    this.addHitInfo = function(jsonHitData) {
        // this is the format PlainTextViewWidget expects
        this.dataModel = [{value: jsonHitData._source.message}];
    }

    this.getDataModel = function() {
        return this.dataModel;
    }
}
