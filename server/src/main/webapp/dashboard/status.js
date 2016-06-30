function ServerCycleStatusTab(dashboard) {
    var cycleSummaryTab = this;
    this.cycleSummarytableWidget = null;
    this.graphWidth = 0;
    this.autoUpdateFlag = false;
    this.progressWidget = new ProgressBarWidget("#id-cycle-transform-progress", "#id-cycle-transform-progress-label");
    $("#id-cycle-transform-progress > div").css({ 'background': '#a6bf82' });
    $('#id-cycle-transform-progress').height(18);


    this.refresh = function() {
        cycleSummaryTab.createCycleDurationAtlasIFrame();
        cycleSummaryTab.createCycleWarnTable();
        cycleSummaryTab.updateProgressBar();
        cycleSummaryTab.createSystemInfoTable();
    }


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
            var warnCodesWidget = new ClickableTableWidget("#id-cycle-warn-aggregate", "id-cycle-warn-agg-table", fieldKeys, [ "tag", "Count"], -1);
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
            $("#id-cycle-refresh-btn").addClass("hidden");
            // cycleSummaryTab.updateProgressBar();
            cycleSummaryTab.createCycleWarnTable();
            cycleSummaryTab.checkForNewCycle();
            //!! setTimeout(cycleSummaryTab.autoUpdate, 5000);
        }
    }

    this.refreshOnLatestCycle = function(data) {
        if(data && data.length == 1) {
            var obj = data[0];
            var latestCycleId = obj["eventInfo.currentCycle"];
            if(latestCycleId != dashboard.vmsCycleId) {
               cycleSummaryTab.autoUpdateFlag = false;
               $("#id-cycle-refresh-btn").removeClass("hidden");
               // cycleSummaryTab.initialize();
            }
        }
    }

    this.createSystemInfoTable = function() {
        var tableFields = ["instanceId", "JarVersion"];
        var tableHeader = ["Instance", "Jar version"];
        var tableWidget = new ClickableTableWidget("#id-cycle-system-info", "id-cycle-system-info-table", tableFields, tableHeader);
        tableWidget.textAlign = "style='text-align: center'";
        var widgetExecutor = new RegexSearchWidgetExecutor(tableWidget, RegexParserMapper.prototype.getJarVersionRegexInfo());
        var query = widgetExecutor.searchQuery;
        query.indexName = dashboard.vmsIndex;
        query.indexType = "vmsserver";
        query.size = "1";
        query.add(dashboard.vmsCycleId).add("tag:TransformCycleBegin");
        widgetExecutor.updateJsonFromSearch();
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

}//status.js
