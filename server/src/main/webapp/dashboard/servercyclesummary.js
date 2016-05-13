function ServerCycleSummaryTab(dashboard) {
    var cycleSummaryTab = this;
    this.cycleSummarytableWidget = null;
    this.graphWidth = 0;

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
        $("#id-vms-server-dashboard").prepend("<div class='shadow' style='float:right;'> <img id='transformer-durations' style='max-width:100%;' src='" + hostName + path + "' /> </div>");
    };

    this.createCycleDurationAtlasIFrame_EmbedNotWorking = function() {
        var cluster = createClusterName(dashboard.nflxEnvironment, dashboard.dataNameSpace, dashboard.vipAddress);
        var hostName = "http://atlasui-global." + dashboard.nflxEnvironment + ".netflix.net/";
        var path = "atlas/embed#url=http%3A%2F%2Fatlas-main." + dashboard.nflxRegion + "." + dashboard.nflxEnvironment + ".netflix.net";
        path += "%3A7001%2Fapi%2Fv1%2Fgraph%3Fq%3Dnf.cluster%2C" + cluster;
        path += "%2C%3Aeq%2Cname%2C(%2C";
        path += "vms.transformer.ProcessDataDuration%2Cvms.transformer.ReadInputDataDuration%2Cvms.transformer.WaitForNextCycleDuration%2vms.transformer.WriteOutputDataDuration%2C)";
        path += "%2C%3Ain%2C%3Aand%2C%3Amax%2C(%2Cname%2C)%2C%3Aby%2C0.0000167%2C%3Amul%2C%3Aarea%2C%24(name)%2C%3Alegend%26l%3D0.1%26ylabel%3DDuration(mins)%26stack%3D1%26";
        path += "e%3Dnow-" + cycleSummaryTab.getAtlasEndMinusNowTimeMinutes() + "m%26s%3De-2h";
        path += "%26w%3D489%26h%3D300&ylabel=Duration(mins)&plot=area";

        console.log("atlas duration query: " + hostName + path);
        $("#id-vms-server-dashboard").html("");
        var iframe = new IFrameWidget("#id-vms-server-dashboard", "id-vms-server-dashboard-iframe", hostName, path);
        iframe.initialize("95%");
    };
    
    
    this.refresh = function() {
        cycleSummaryTab.createCycleDurationAtlasIFrame();
    };

    this.initialize = function() {
        var refFn = this;
        this.workerPublishMbps = null;
        this.stateEngineSize = null;
        this.statsMbps = null;
        $("#id-cycle-timestamp-div").html("");

        this.getIndexSearchQuery = function(purpose, commaSeparatedFieldNames) {
            var query = new SearchQuery();
            query.indexName = dashboard.vmsIndex;
            if (!commaSeparatedFieldNames) {
                query.size = "300"; // 24*60/5
            } else {
                query.fields = commaSeparatedFieldNames;
                query.size = "720"; // 24*(60/2)
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
                query.sort = "eventInfo.timestamp:desc";
            } else if (purpose == "BlobPublishFail") {
                query.indexType = "vmsserver";
                query.add("tag:BlobPlubishStatus").add("false");
            } else if (purpose == "S3Errors") {
                query.add("org.jets3t.service.S3ServiceException");
            } else if (purpose == "WorkerPublish") {
                query.size = "600";
                query.indexType = "vmsserver";
                query.add("tag:PublishedBlob").add("netflix.vms.hollowblob." + dashboard.vipAddress + ".all.snapshot");
            } else if (purpose == "StateEnginePublish") {
                query.indexType = "vmsserver";
                query.add("tag:PublishedBlob").add("netflix.vms.hollowblob." + dashboard.vipAddress + ".all.snapshot").add("us-east-1");
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

            var currCycle = new String(rowInfo.currentCycle);
            var rowStyle = "<tr style='background-color:#FFFFFF'>";
            var html = "";
            var cycleSuccess = false;
            var cycleFail = false;
            var publishErrors = false;
            // html += "<td><img src='images/ok.png'></td>";
            // html += "<td><img src='images/x.png'></td>";

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
            		rowStyle = "<tr style='background-color:" + orangeColor + "'>";
            	}else {
	                html += "<td><img src='images/ok.png'></td>";
	                rowStyle = "<tr style='background-color:" + greenColor + "'>";
            	}
            } else if (!cycleFail && !cycleSuccess) {
                html += "<td><img src='images/incomplete.png'></td>";
            }

            if (publishErrors) {
                html += "<td><img src='images/x.png'></td>";
                rowStyle = "<tr style='background-color:" + yellowColor + "'>";
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
                    rowStyle = "<tr style='background-color:" + yellowColor + "'>";
                }
            }

            if (!refFn.stateEngineSize[currCycle]) {
                html += "<td style='text-align:right'><img src='images/incomplete.png'></td>";
            } else {
                var val = refFn.stateEngineSize[currCycle].toFixed(4);
                var valStr = "";
                if (val < 0) {
                    valStr = val.toString().substring(0, 5);
                } else {
                    valStr = "+" + val.toString().substring(0, 4);
                }
                html += "<td style='text-align:right'>" + valStr + "%</td>";
            }

            if (cycleFail) {
                rowStyle = "<tr style='background-color:" + redColor + "'>";
            }

            return {
                trow : rowStyle,
                tcols : html
            };
        };

        this.populateCycleTimeStampsTable = function() {
            refFn.computeStateEngineSize();

            cycleSummaryTab.cycleSummarytableWidget = new ClickableTableWidget("#id-cycle-timestamp-div", "id-cycle-timestamp-table", [ "currentCycle",
                    "timestamp", "custom", "custom", "custom", "custom", "custom", "custom"], [ "Cycle id", "Time", "Success", "S3 access", 
                    "Unpublished regions", "S3 upload Mbps",
                    "Snapshot change"], 0, dashboard.cycleIdSelector, refFn.styleRowBackground);
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

        this.fillParallelModelCaches = function() {
            var daoExecutor = new ParallelDAOExecutor(refFn.populateCycleTimeStampsTable);
            daoExecutor.add(refFn.cacheFailDAO);
            daoExecutor.add(refFn.s3FailDAO);
            daoExecutor.add(refFn.cacheSuccessDAO);
            daoExecutor.add(refFn.stateEnginePublishDAO);
            daoExecutor.add(refFn.hollowPublishRegionDAO);
            daoExecutor.run();
        };

        this.computeStateEngineSize = function() {
            var stateEngineGroupByVersion = new DataOperator(refFn.stateEnginePublishDAO.responseModel.dataModel).groupBy("version");
            refFn.stateEngineSize = stateEngineGroupByVersion.min("filesize(bytes)").prevDiffPercent().inpDataModel;
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
