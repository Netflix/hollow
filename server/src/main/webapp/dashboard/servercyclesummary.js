function ServerCycleSummaryTab(dashboard) {
    var cycleSummaryTab = this;
    this.cycleSummarytableWidget = null;
    this.graphWidth = 0;
    this.autoUpdateFlag = false;
    this.progressWidget = new ProgressBarWidget("#id-cycle-transform-progress", "#id-cycle-transform-progress-label");
    $("#id-cycle-transform-progress > div").css({ 'background': '#a6bf82' });
    $('#id-cycle-transform-progress').height(18);

    this.getAtlasEndMinusNowTimeMinutes = function() {
        var curr = Date.now();
        var elapsedMillis = curr - dashboard.vmsCycleDate - 3600 * 1000;
        if (elapsedMillis < 0.0) {
            elapsedMillis = 0.0;
        }
        return Math.floor(elapsedMillis / (1000 * 60));
    };

    // this is the default view, load at startup
    this.createCycleDurationAtlasIFrame = function() {
        var cluster = createClusterName(dashboard.nflxEnvironment, dashboard.dataNameSpace, dashboard.vipAddress);
        if(cycleSummaryTab.graphWidth == 0) {
            cycleSummaryTab.graphWidth=$("#id-vms-server-dashboard").width();
            if(cycleSummaryTab.graphWidth > 100) {
                cycleSummaryTab.graphWidth = cycleSummaryTab.graphWidth - 60; // atlas does not return the exact width
            }
        }
        var hostName = "http://atlas-global." + dashboard.nflxEnvironment + ".netflix.net/";
        var path = "api/v1/graph?q=name,(,"
        path += "vms.transformer.ReadInputDataDuration,vms.transformer.ProcessDataDuration,vms.transformer.WriteOutputDataDuration,vms.transformer.WaitForNextCycleDuration";
        path += ",),:in,nf.cluster," + cluster + ",:eq,:and,:sum,(,name,),:by,60000,:div,:stack";
        path += "&e=now-" + cycleSummaryTab.getAtlasEndMinusNowTimeMinutes() + "m&s=e-2h";
        path += "&w=" + cycleSummaryTab.graphWidth + "&h=270&ylabel=Duration(mins)&plot=area"; //w = 460

        console.log("atlas duration query: " + hostName + path);
        $("#id-vms-server-dashboard").html("");
        $("#id-vms-server-dashboard").prepend("<div style='float:right;'> <img id='transformer-durations' style='max-width:100%;' src='" + hostName + path + "' /> </div>");
    };

    this.createCycleWarnTable = function() {
            var fieldKeys = [ "key", "doc_count" ];
            var warnCodesWidget = new ClickableTableWidget("#id-cycle-warn-aggregate", "id-cycle-warn-agg-table", fieldKeys, [ "tag", "Count"], 0);
            warnCodesWidget.showHeader = false;
            var query = new SearchQuery();
            query.indexName = dashboard.vmsIndex;
            query.add("eventInfo.currentCycle:" + dashboard.vmsCycleId);
            query.add("(logLevel:warn OR logLevel:error)");
            query.aggregate = "tag";
            var searchDao = new SearchAggregationDAO(warnCodesWidget, query, true);
            searchDao.updateJsonFromSearch();
    }

    this.updateProgressBar = function() {
        var regexSourceModel = ResponseModelsFactory.prototype.getModel("RegexModel", {
            sourceField : "message",
            fieldsRegex : RegexParserMapper.prototype.getProgressRegexInfo()
        });

        var searchDao = new SearchDAO(regexSourceModel, cycleSummaryTab.progressWidget, true);
        searchDao.searchQuery = new SearchQuery();
        searchDao.searchQuery.size = "1";
        searchDao.searchQuery.indexName = dashboard.vmsIndex;
        searchDao.searchQuery.add("eventInfo.currentCycle:" + dashboard.vmsCycleId);
        searchDao.searchQuery.add("tag:TransformProgress");
        searchDao.searchQuery.sort = "eventInfo.timestamp:desc";
        searchDao.updateJsonFromSearch();
    }

    this.autoUpdate = function() {
        if(cycleSummaryTab.autoUpdateFlag) {
            if(cycleSummaryTab.progressWidget.value != 100) { // ==
                // cycleSummaryTab.autoUpdateFlag = false;
                // return;
                cycleSummaryTab.updateProgressBar();
            }
            // cycleSummaryTab.updateProgressBar();
            cycleSummaryTab.createCycleWarnTable();
            cycleSummaryTab.checkForNewCycle();
            setTimeout(cycleSummaryTab.autoUpdate, 5000);
        }
    }

    this.refresh = function() {
        cycleSummaryTab.createCycleDurationAtlasIFrame();
        cycleSummaryTab.createCycleWarnTable();
        cycleSummaryTab.updateProgressBar();
    }

    this.refreshOnLatestCycle = function(data) {
        if(data && data.length == 1) {
            var obj = data[0];
            var latestCycleId = obj["eventInfo.currentCycle"];
            if(latestCycleId != dashboard.vmsCycleId) {
               cycleSummaryTab.autoUpdateFlag = false;
               // cycleSummaryTab.initialize();
            }
        }
    }
    
    this.checkForNewCycle = function() {
        var callbackFn = new CallbackWidget(cycleSummaryTab.refreshOnLatestCycle);
        var fieldList = ["eventInfo.currentCycle" ];
        var searchDao = new FieldModelSearchDAO(callbackFn, new SearchQuery(), fieldList, true);
        searchDao.searchQuery.size = "1";
        searchDao.searchQuery.indexType = "vmsserver";
        searchDao.searchQuery.indexName = dashboard.vmsIndex;
        searchDao.searchQuery.fields = fieldList;
        searchDao.searchQuery.add("tag:TransformCycleBegin");
        searchDao.searchQuery.sort = "eventInfo.timestamp:desc";
        searchDao.updateJsonFromSearch();
    }

    this.initialize = function() {
        var refFn = this;
        this.workerPublishMbps = null;
        this.stateEngineSize = null;
        this.topNodeCounts = null;
        this.statsMbps = null;
        $("#id-cycle-timestamp-div").html("");

        this.getIndexSearchQuery = function(purpose, commaSeparatedFieldNames) {
            var query = new SearchQuery();
            query.indexName = dashboard.vmsIndex;
            query.sort = "eventInfo.timestamp:desc";
            query.size = VipAddressHolder.prototype.getSummaryQuerySize();

            if(!commaSeparatedFieldNames) {
                query.fields = null;
            } else {
                query.fields = commaSeparatedFieldNames;
            }
            if (purpose == "CacheFail") {
                query.indexType = "vmsserver";
                query.add("tag:TransformCycleFailed");
            } else if (purpose == "CacheSuccess") {
                query.indexType = "vmsserver";
                query.add("tag:TransformCycleSuccess");
            } else if (purpose == "CycleInfo") {
                query.indexType = "vmsserver";
                query.add("tag:TransformCycleBegin");
            } else if (purpose == "TopNodes") {
                query.indexType = "vmsserver";
                query.add("tag:TransformInfo").add("topNodes");
            } else if (purpose == "BlobPublishFail") {
                query.indexType = "vmsserver";
                query.add("tag:BlobPlubishStatus").add("false");
            } else if (purpose == "S3Errors") {
                query.add("org.jets3t.service.S3ServiceException");
            } else if (purpose == "WorkerPublish") {
                query.indexType = "vmsserver";
                query.add("tag:PublishedBlob").add("\"netflix.vms.hollowblob." + dashboard.vipAddress + ".all.snapshot\"");
            } else if (purpose == "StateEnginePublish") {
                query.indexType = "vmsserver";
                query.add("tag:PublishedBlob").add("\"netflix.vms.hollowblob." + dashboard.vipAddress + ".all.snapshot\"").add("\"us-east-1\"");
            }  else if (purpose == "hollowPublishRegion") {
                query.add("tag:AnnouncementSuccess");
            }
            return query;
        };

        this.styleRowBackground = function(rowInfo) {
            this.cacheFailModel = refFn.cacheFailDAO.responseModel;
            this.s3FailModel = refFn.s3FailDAO.responseModel;
            this.cacheSuccessModel = refFn.cacheSuccessDAO.responseModel;
            this.blobStatusArrayModel = refFn.blobPublishDAO.regexSourceModel.dataModel;
            this.stateEnginePublishModel = refFn.stateEnginePublishDAO.responseModel.dataModel;
            this.hollowPublishRegionModel = refFn.hollowPublishRegionDAO.responseModel;
            // this can be expensive if too many publish failures, usually 0
            this.blobErrorMapModel = new DataOperator(this.blobStatusArrayModel).groupBy("version").inpDataModel;

            var greenColor = "#E0FFE0";
            var yellowColor = "#FFFFBD";
            var redColor = "#FF9999";
            var orangeColor = "#FFA500";
            var whiteColor = "#FFFFFF";

            var currCycle = new String(rowInfo.currentCycle);
            var rowStyle = "<tr style='background-color:#FFFFFF; color:#769d3e'>";
            var html = "";
            var cycleSuccess = false;
            var cycleFail = false;
            var publishErrors = false;

            if (this.cacheSuccessModel.rowFieldEquals("eventInfo.currentCycle", currCycle)) {
                cycleSuccess = true;
            }

            if (this.blobErrorMapModel.hasOwnProperty(currCycle)) {
                publishErrors = true;
            }

            if (this.s3FailModel.rowFieldEquals("eventInfo.currentCycle", currCycle)) {
                publishErrors = true;
            }

            if (this.cacheFailModel.rowFieldEquals("eventInfo.currentCycle", currCycle)) {
                cycleFail = true;
            }

            var hollowAnnounced = [false, false, false];
            var regions = ["us-east-1", "us-west-2", "eu-west-1"];
            hollowDisplayString = "";
            showNonAvailability = false;

            // returns filtered array
            var announceResult = this.hollowPublishRegionModel.filter("eventInfo.currentCycle", currCycle);
            if(announceResult.length > 0) {
                for(i=0;i<regions.length;i++) {
                    for(var ires = 0; ires < announceResult.length; ires++) {
                        if(announceResult[ires].message.indexOf(regions[i]) > 0) {
                            hollowAnnounced[i] = true;
                            break;
                        }
                    }
                    if(!hollowAnnounced[i]) {
                        hollowDisplayString += regions[i] + " ";
                        showNonAvailability = true;
                    }
                }
            } else {
            	// Even if no hollow publish events for a cycle are found, it means that this cycle was not announced to any regions.
            	showNonAvailability = true;
            }

            if (cycleFail) {
                html += "<td><img src='images/x.png'></td>";
            } else if (cycleSuccess) {
            	if(showNonAvailability) {
            		html += "<td><img src='images/incomplete.png'></td>";
            		rowStyle = "<tr style='background-color:" + orangeColor + "; color:black'>";
            	}else {
	                html += "<td><img src='images/ok.png'></td>";
	                rowStyle = "<tr style='background-color:" + whiteColor + "'; color:black>";
            	}
            } else if (!cycleFail && !cycleSuccess) {
                html += "<td><img src='images/incomplete.png'></td>";
            }

            if (publishErrors) {
                html += "<td><img src='images/x.png'></td>";
                rowStyle = "<tr style='background-color:" + yellowColor + "'; color:black>";
            } else if (!refFn.workerPublishMbps[currCycle]) {
                html += "<td><img src='images/incomplete.png'></td>";
            } else {
                html += "<td><img src='images/ok.png'></td>";
            }

            
            if(showNonAvailability) {
                html += "<td>" + hollowDisplayString +  "</td>";
            } else if (!cycleSuccess) {
                html += "<td><img src='images/incomplete.png'></td>";
            } else {
                html += ("<td>None</td>");
            }

            if (!refFn.workerPublishMbps[currCycle]) {
                html += "<td style='text-align:right'><img src='images/incomplete.png'></td>";
            } else {
                html += "<td style='text-align:right'>" + refFn.workerPublishMbps[currCycle].toFixed(0) + "</td>";
                if (refFn.workerPublishMbps[currCycle] < refFn.statsMbps.mean - 2 * refFn.statsMbps.sd) {
                    rowStyle = "<tr style='background-color:" + yellowColor + "; color:black'>";
                }
            }

            var publishDataSizes = refFn.stateEngineSize;

            if (!publishDataSizes[currCycle]) {
                html += "<td style='text-align:right'><img src='images/incomplete.png'></td>";
            } else {
                var val = publishDataSizes[currCycle].toFixed(4);
                var valStr = "";
                if (val < 0) {
                    valStr = val.toString().substring(0, 5);
                } else {
                    valStr = "+" + val.toString().substring(0, 4);
                }
                if(valStr == "+0.00" || valStr == "-0.00" || valStr == "+-0.0") {
                    valStr = "";
                } else {
                    valStr = valStr + "%";
                }
                html += "<td style='text-align:right'>" + valStr + "</td>";
            }

            var topNodeCountDelta = refFn.topNodeCounts;
            var currCycleNum = Number(currCycle);
            var topNodeForCycle = topNodeCountDelta[currCycle];

            if (!topNodeCountDelta[currCycle]) {
                if(topNodeForCycle == 0) {
                    html += "<td style='text-align:right'> </td>";
                } else {
                    html += "<td style='text-align:right'><img src='images/incomplete.png'></td>";
                }
            } else {
                html += "<td style='text-align:right'>" + topNodeCountDelta[currCycle] + "</td>";
            }

            if (cycleFail) {
                rowStyle = "<tr style='background-color:" + redColor + "'>; color:black";
            }

            return {
                trow : rowStyle,
                tcols : html
            };
        };

        this.populateCycleTimeStampsTable = function() {
            refFn.computeStateEngineSize();
            refFn.computeTopNodeCounts();
            cycleSummaryTab.cycleSummarytableWidget = new ClickableTableWidget("#id-cycle-timestamp-div", "id-cycle-timestamp-table", [ "currentCycle",
                    "timestamp", "custom", "custom", "custom", "custom", "custom", "custom", "custom"], [ "Cycle id", "Time", "Success", "S3 access", 
                    "Unpublished regions", "S3 upload Mbps",
                    "Snapshot change", "Topnodes change"], 0, dashboard.cycleIdSelector, refFn.styleRowBackground);
            var searchFieldModelDAO = new FieldModelSearchDAO(cycleSummaryTab.cycleSummarytableWidget, refFn.getIndexSearchQuery("CycleInfo"), [
                    "timestamp", "message", "currentCycle" ], true);
            searchFieldModelDAO.updateJsonFromSearch();
        };

        this.cacheFailDAO = new FieldModelSearchDAO(null, refFn.getIndexSearchQuery("CacheFail", "eventInfo.currentCycle"), [ "eventInfo.currentCycle" ],
                true);
        this.cacheSuccessDAO = new FieldModelSearchDAO(null, refFn.getIndexSearchQuery("CacheSuccess", "eventInfo.currentCycle"),
                [ "eventInfo.currentCycle" ], true);
        this.s3FailDAO = new FieldModelSearchDAO(null, refFn.getIndexSearchQuery("S3Errors", "eventInfo.currentCycle"), [ "eventInfo.currentCycle" ], true);
        this.hollowPublishRegionDAO = new FieldModelSearchDAO(null, refFn.getIndexSearchQuery("hollowPublishRegion", "eventInfo.currentCycle,message"), 
                ["eventInfo.currentCycle", "message"], true);
        var s3PublishRegex = RegexParserMapper.prototype.getBlobPublishRegexInfo();
        var s3PublishRegexInfo = ResponseModelsFactory.prototype.getModel("RegexModel", {
            sourceField : "message",
            fieldsRegex : s3PublishRegex
        });
        this.stateEnginePublishDAO = new SearchDAO(s3PublishRegexInfo, null, true);
        this.stateEnginePublishDAO.searchQuery = refFn.getIndexSearchQuery("StateEnginePublish");

        var topNodeRegex = RegexParserMapper.prototype.getTopNodesRegexInfo();
        var topNodeRegexInfo = ResponseModelsFactory.prototype.getModel("RegexModel", {
            sourceField : "message",
            fieldsRegex : topNodeRegex
        });

        this.topNodeCountDAO = new SearchDAO(topNodeRegexInfo, null, true);
        this.topNodeCountDAO.searchQuery = refFn.getIndexSearchQuery("TopNodes");

        this.fillParallelModelCaches = function() {
            var daoExecutor = new ParallelDAOExecutor(refFn.populateCycleTimeStampsTable);
            daoExecutor.add(refFn.stateEnginePublishDAO);
            daoExecutor.add(refFn.topNodeCountDAO);
            daoExecutor.add(refFn.cacheFailDAO);
            daoExecutor.add(refFn.s3FailDAO);
            daoExecutor.add(refFn.cacheSuccessDAO);
            daoExecutor.add(refFn.hollowPublishRegionDAO);
            daoExecutor.run();
        };

        this.computeStateEngineSize = function() {
            var dataop = new DataOperator(refFn.stateEnginePublishDAO.responseModel.dataModel);
            var stateEngineGroupByVersion = dataop.groupBy("version");
            refFn.stateEngineSize = stateEngineGroupByVersion.min("filesize(bytes)").prevDiff(true).inpDataModel;
        };

        this.computeTopNodeCounts= function() {
            var dataop = new DataOperator(refFn.topNodeCountDAO.responseModel.dataModel);
            var topNodes = dataop.extractField("cycleId", "topNodes");
            refFn.topNodeCounts = topNodes.prevDiff(false).inpDataModel;
        };

        this.blobPublishDAO = new RegexSearchWidgetExecutor(new EventChainingWidget(this.fillParallelModelCaches), RegexParserMapper.prototype
                .getBlobStatusRegexInfo());
        this.blobPublishDAO.searchQuery = refFn.getIndexSearchQuery("BlobPublishFail");

        this.workerPublishDAO = null;

        this.fillS3FailModel = function() {
            var workerPublishGroupByVersion = new DataOperator(refFn.workerPublishDAO.regexSourceModel.dataModel).groupBy("version");
            var workerPublishMinMbps = workerPublishGroupByVersion.min("Mbps");
            refFn.workerPublishMbps = workerPublishMinMbps.inpDataModel;
            refFn.statsMbps = workerPublishMinMbps.stats();
            refFn.blobPublishDAO.updateJsonFromSearch();
        };

        this.workerPublishDAO = new RegexSearchWidgetExecutor(new EventChainingWidget(this.fillS3FailModel), RegexParserMapper.prototype
                .getBlobPublishRegexInfo());
        this.workerPublishDAO.searchQuery = refFn.getIndexSearchQuery("WorkerPublish");
        this.workerPublishDAO.updateJsonFromSearch();
    };
}//ServerCycleSummaryTab

function createClusterName(nflxEnvironment, dataNameSpace, vipAddress) {
    var cluster = "vmstransformer-" + vipAddress + "-" + dataNameSpace;
    return cluster;
}
