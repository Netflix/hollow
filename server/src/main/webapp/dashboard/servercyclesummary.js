function ServerCycleSummaryTab(dashboard) {
    var cycleSummaryTab = this;
    this.cycleSummarytableWidget = null;
    this.graphWidth = 0;
    this.expandedView = false;
    this.fullData = false;
    this.hidableColumns =  [3, 5, 6, 7];

    $("#id-summary-toggle-btn").click(function() {
        cycleSummaryTab.toggleHidableColumns();
    });

    $("#id-summary-toggle-btn").addClass("vtag-right");

    this.hideColumns = function() {
        for(var i=0; i < cycleSummaryTab.hidableColumns.length; i++) {
            $("table[id='id-cycle-timestamp-table']").hideColumn(cycleSummaryTab.hidableColumns[i]);
        }
    }

    this.toggleHidableColumns = function() {
        cycleSummaryTab.expandedView = !cycleSummaryTab.expandedView;
        if(cycleSummaryTab.expandedView && !cycleSummaryTab.fullData) {
            cycleSummaryTab.initialize();
        }

        if(cycleSummaryTab.expandedView) {
            $("#id-cycles-container").animate({width:"56%"});
            $("#cycle-stats-tabs").width("40%");

            $("#id-atlas-durations").width("36%");
            $("#id-vms-server-rightpane").width("60%");
            for(var i=0; i < cycleSummaryTab.hidableColumns.length; i++) {
                $("table[id='id-cycle-timestamp-table']").showColumn(cycleSummaryTab.hidableColumns[i]);
            }
            $("#id-summary-toggle-btn").removeClass("vtag-right");
            $("#id-summary-toggle-btn").addClass("vtag-left");
        } else {
            cycleSummaryTab.hideColumns();
            $("#id-cycles-container").width("30%");
            $("#id-atlas-durations").width("56%");
            $("#id-vms-server-rightpane").width("40%");
            $("#cycle-stats-tabs").animate({width:"67%"});

            $("#id-summary-toggle-btn").removeClass("vtag-left");
            $("#id-summary-toggle-btn").addClass("vtag-right");
        }
    }

    this.initialize = function() {
        var refFn = this;
        this.workerPublishMbps = null;
        this.stateEngineSize = null;
        this.topNodeCounts = null;
        this.statsMbps = null;
        $("#id-cycle-timestamp-div").html('<div style="text-align: center;"><img src="images/spinner160.gif" align="middle"/></div>');

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
                query.add("eventInfo.tag:TransformCycleFailed");
            } else if (purpose == "CacheSuccess") {
                query.indexType = "vmsserver";
                query.add("eventInfo.tag:TransformCycleSuccess");
            } else if (purpose == "HideCycles") {
                query.indexType = "vmsserver";
                query.add("eventInfo.tag:HideCycleFromDashboard");
            } else if (purpose == "CycleInfo") {
                query.indexType = "vmsserver";
                query.add("eventInfo.tag:TransformCycleBegin").add("jarVersion");
            } else if (purpose == "TopNodes") {
                query.indexType = "vmsserver";
                query.add("eventInfo.tag:TransformInfo").add("topNodes");
            } else if (purpose == "BlobPublishFail") {
                query.indexType = "vmsserver";
                query.add("eventInfo.tag:BlobPlubishStatus").add("false");
            } else if (purpose == "S3Errors") {
                query.add("org.jets3t.service.S3ServiceException");
            } else if (purpose == "WorkerPublish") {
                query.indexType = "vmsserver";
                query.add("eventInfo.tag:PublishedBlob").add("\"netflix.vms.hollowblob." + dashboard.vipAddress + ".all.snapshot\"");
            } else if (purpose == "StateEnginePublish") {
                query.indexType = "vmsserver";
                query.add("eventInfo.tag:PublishedBlob").add("\"netflix.vms.hollowblob." + dashboard.vipAddress + ".all.snapshot\"").add("\"us-east-1\"");
            }  else if (purpose == "hollowPublishRegion") {
                query.add("eventInfo.tag:AnnouncementSuccess");
            }
            return query;
        };

        this.styleRowBackground = function(rowInfo, row, numRows) {
            this.cacheFailModel = refFn.cacheFailDAO.responseModel;
            this.s3FailModel = refFn.s3FailDAO.responseModel;
            this.hideCycleModel = refFn.hideCycleDAO.responseModel;
            this.cacheSuccessModel = refFn.cacheSuccessDAO.responseModel;
            this.blobStatusArrayModel = refFn.blobPublishDAO.regexSourceModel.dataModel;
            this.stateEnginePublishModel = refFn.stateEnginePublishDAO.responseModel.dataModel;
            this.hollowPublishRegionModel = refFn.hollowPublishRegionDAO.responseModel;
            // this can be expensive if too many publish failures, usually 0
            this.blobErrorMapModel = new DataOperator(this.blobStatusArrayModel).groupBy("version").inpDataModel;

            var redColor = "#FF9999";
            var greenColor = "#E0FFE0";
            var blueColor = "#E0E0FF";
            var yellowColor = "#FFFFBD";
            var orangeColor = "#FFA500";
            var whiteColor = "#FFFFFF";

            var currCycle = new String(rowInfo.currentCycle);
            var rowStyle = "<tr style='background-color:#FFFFFF; color:#769d3e'>";
            var html = "";
            var cycleSuccess = false;
            var cycleFail = false;
            var publishErrors = false;

            if (this.hideCycleModel != null && this.hideCycleModel.rowFieldEquals("eventInfo.currentCycle", currCycle)) {
                return {
                    trow : "SKIP",
                    tcols : ""
                };
            }

            if (this.cacheSuccessModel.rowFieldEquals("eventInfo.currentCycle", currCycle)) {
                cycleSuccess = true;
            }

            if (this.blobErrorMapModel.hasOwnProperty(currCycle)) {
                publishErrors = true;
            }

            if (this.s3FailModel != null && this.s3FailModel.rowFieldEquals("eventInfo.currentCycle", currCycle)) {
                publishErrors = true;
            }

            if (this.cacheFailModel.rowFieldEquals("eventInfo.currentCycle", currCycle)) {
                cycleFail = true;
            }

            var hollowAnnounced = [false, false, false];
            var regions = ["us-east-1", "us-west-2", "eu-west-1"];
            hollowDisplayString = "";
            showNonAvailability = false;

             // fast-lane does not publish every cycle, and announces without delay
            if(dashboard.vipAddress.indexOf("_override") == -1) {
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
            }

            if (cycleFail) {
                html += "<td><img src='images/x.png'></td>";
            } else if (cycleSuccess) {
                if (showNonAvailability && hollowDisplayString.length > 0) {
                    // Announcing to all regions not yet complete
                    html += "<td><img src='images/waiting.png'></td>";
                    rowStyle = "<tr style='background-color:" + orangeColor + "; color:black'>";
                } else if (showNonAvailability && hollowDisplayString.length == 0) {
                    // No announcements after cycle success indicates no changes
                    html += "<td><img src='images/ok.png'></td>";
	                rowStyle = "<tr style='background-color:" + blueColor + "; color:black'>";
                } else {
                    // Announced in all regions
                    html += "<td><img src='images/ok.png'></td>";
                    rowStyle = "<tr style='background-color:" + whiteColor + "; color:black'>";
            	}
            } else if (!cycleFail && !cycleSuccess) {
                if(row == 0) {
                    html += "<td><img src='images/incomplete.png'></td>";
                } else {
                    html += "<td><img src='images/restarted.png'></td>";
                }
            }

            if (publishErrors) {
                html += "<td><img src='images/x.png'></td>";
                rowStyle = "<tr style='background-color:" + yellowColor + "; color:black'>";
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
                html += ("<td> </td>");
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

            if (publishDataSizes == null || !publishDataSizes[currCycle]) {
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
            var topNodeForCycle =topNodeCountDelta == null ? null : topNodeCountDelta[currCycle];

            if (topNodeCountDelta == null || !topNodeCountDelta[currCycle]) {
                if(topNodeForCycle == 0) {
                    html += "<td style='text-align:right'> </td>";
                } else {
                    html += "<td style='text-align:right'><img src='images/incomplete.png'></td>";
                }
            } else {
                html += "<td style='text-align:right'>" + topNodeCountDelta[currCycle] + "</td>";
            }

            if (cycleFail) {
                rowStyle = "<tr style='background-color:" + redColor + "; color:black'>";
            }

            return {
                trow : rowStyle,
                tcols : html
            };
        };

        this.populateCycleTimeStampsTable = function() {
            if(cycleSummaryTab.expandedView) {
                refFn.computeStateEngineSize();
                refFn.computeTopNodeCounts();
                cycleSummaryTab.fullData = true;
            }
            cycleSummaryTab.cycleSummarytableWidget = new ClickableTableWidget("#id-cycle-timestamp-div", "id-cycle-timestamp-table", [ "currentCycle",
                    "timestamp", "custom", "custom", "custom", "custom", "custom", "custom", "custom"], [ "Cycle id", "Time", "Success", "S3 access", 
                    "Regions lagging", "S3 upload Mbps",
                    "Snapshot change", "pUnits change"], 0, dashboard.cycleIdSelector, refFn.styleRowBackground);
            var searchFieldModelDAO = new FieldModelSearchDAO(cycleSummaryTab.cycleSummarytableWidget, refFn.getIndexSearchQuery("CycleInfo"), [
                    "timestamp", "message", "currentCycle" ], true);
            
            if(!cycleSummaryTab.expandedView) {
                cycleSummaryTab.cycleSummarytableWidget.endBuildTableFunc = cycleSummaryTab.hideColumns;
            }
            searchFieldModelDAO.updateJsonFromSearch();
        };

        this.cacheFailDAO = new FieldModelSearchDAO(null, refFn.getIndexSearchQuery("CacheFail", "eventInfo.currentCycle"), [ "eventInfo.currentCycle" ], true);
        this.cacheSuccessDAO = new FieldModelSearchDAO(null, refFn.getIndexSearchQuery("CacheSuccess", "eventInfo.currentCycle"),[ "eventInfo.currentCycle" ], true);
        this.s3FailDAO = new FieldModelSearchDAO(null, refFn.getIndexSearchQuery("S3Errors", "eventInfo.currentCycle"), [ "eventInfo.currentCycle" ], true);
        this.hideCycleDAO = new FieldModelSearchDAO(null, refFn.getIndexSearchQuery("HideCycles", "eventInfo.currentCycle"), [ "eventInfo.currentCycle" ], true);
        this.hollowPublishRegionDAO = new FieldModelSearchDAO(null, refFn.getIndexSearchQuery("hollowPublishRegion", "eventInfo.currentCycle,message"), ["eventInfo.currentCycle", "message"], true);
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
            if(cycleSummaryTab.expandedView) {
                daoExecutor.add(refFn.stateEnginePublishDAO);
                daoExecutor.add(refFn.topNodeCountDAO);
                daoExecutor.add(refFn.s3FailDAO);
            }
            daoExecutor.add(refFn.hideCycleDAO);
            daoExecutor.add(refFn.cacheFailDAO);
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
