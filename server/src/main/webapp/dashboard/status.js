function ServerCycleStatusTab(dashboard) {
    new InfoTile("cycle-warn-aggregate");
    new InfoTile("cycle-system-info");
    new InfoTile("fastlane-ids");
    new InfoTile("cycle-oldest-coldstart");
    new InfoTile("pinned-titles-ids");


    var cycleSummaryTab = this;
    this.graphWidth = 0;
    this.autoUpdateFlag = false;
    this.progressWidget = new ProgressBarWidget("#id-cycle-transform-progress", "#id-cycle-transform-progress-label");
    $("#id-cycle-transform-progress > div").css({ 'background': '#a6bf82' });
    $('#id-cycle-transform-progress').height(18);
    var csTableFields = [ "group", "date" ];
    this.csTable = new ClickableTableWidget("#id-cycle-oldest-coldstart", "iid-cycle-oldest-coldstart-table", csTableFields, csTableFields, -1);
    this.csTable.textAlign = "style='text-align: center'";
    this.csTable.clearPrevious = false;

    var tableFields = ["instanceId", "JarVersion"];
    var tableHeader = ["Instance", "Jar version"];
    this.systemInfoTable = new ClickableTableWidget("#id-cycle-system-info", "id-cycle-system-info-table", tableFields, tableHeader);
    // this.systemInfoTable.clearPrevious = true;
    this.systemInfoTable.textAlign = "style='text-align: center'";
    this.systemInfoTable.clearPrevious = false;

    this.warnCodesWidget = new ClickableTableWidget("#id-cycle-warn-aggregate", "id-cycle-warn-agg-table", [ "key", "doc_count" ], [ "tag", "Count"], -1);

    this.refresh = function() {
        cycleSummaryTab.createCycleDurationAtlasIFrame();
        cycleSummaryTab.createCycleWarnTable();
        cycleSummaryTab.progressWidget.value = 0;
        cycleSummaryTab.createSystemInfoTable();
        cycleSummaryTab.findOldestColdstart();
        cycleSummaryTab.createFastlaneView();
        cycleSummaryTab.createPinnedTitlesView();
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
        path += "vms.transformer.ReadInputDataDuration,vms.transformer.ProcessDataDuration,vms.transformer.WriteOutputDataDuration,vms.transformer.WaitForPublishWorkflowDuration,vms.transformer.WaitForNextCycleDuration,vms.transformer.P1_ReadInputDataDuration,vms.transformer.P2_ProcessDataDuration,vms.transformer.P3_WriteOutputDataDuration,vms.transformer.P4_WaitForPublishWorkflowDuration,vms.transformer.P5_WaitForNextCycleDuration";
        path += ",),:in,nf.cluster," + cluster + ",:eq,:and,:sum,(,name,),:by,60000,:div,:stack";
        path += "&e=now-" + cycleSummaryTab.getAtlasEndMinusNowTimeMinutes() + "m&s=e-2h";
        path += "&w=" + cycleSummaryTab.graphWidth + "&h=270&ylabel=Duration(mins)&plot=area"; //w = 460

        console.log("atlas duration query: " + hostName + path);
        $("#id-vms-server-dashboard").html("");
        $("#id-vms-server-dashboard").prepend("<div style='float:right;'> <img id='transformer-durations' style='max-width:100%;' src='" + hostName + path + "' /> </div>");
    };

    this.createCycleWarnTable = function() {
            cycleSummaryTab.warnCodesWidget.showHeader = false;
            var query = new SearchQuery();
            query.indexName = dashboard.vmsIndex;
            query.add("eventInfo.currentCycle:" + dashboard.vmsCycleId);
            query.add("(eventInfo.logLevel:warn OR eventInfo.logLevel:error)");
            query.aggregate = "eventInfo.tag";
            var searchDao = new SearchAggregationDAO(cycleSummaryTab.warnCodesWidget, query, true);
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
        searchDao.searchQuery.add("eventInfo.tag:TransformProgress");
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
            setTimeout(cycleSummaryTab.autoUpdate, 5000);
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
        var widgetExecutor = new RegexSearchWidgetExecutor(cycleSummaryTab.systemInfoTable, RegexParserMapper.prototype.getJarVersionRegexInfo());
        var query = widgetExecutor.searchQuery;
        query.indexName = dashboard.vmsIndex;
        query.indexType = "vmsserver";
        query.size = "1";
        query.add(dashboard.vmsCycleId).add("eventInfo.tag:TransformCycleBegin");
        widgetExecutor.updateJsonFromSearch();
    }

    this.checkForNewCycle = function() {
        const callbackFn = new CallbackWidget(cycleSummaryTab.refreshOnLatestCycle);
        const fieldList = ["eventInfo.currentCycle" ];
        const searchDao = new FieldModelSearchDAO(callbackFn, new SearchQuery(), fieldList, true);
        searchDao.searchQuery.size = "1";
        searchDao.searchQuery.indexType = "vmsserver";
        searchDao.searchQuery.indexName = dashboard.vmsIndex;
        searchDao.searchQuery.fields = fieldList;
        searchDao.searchQuery.add("eventInfo.tag:TransformCycleBegin");
        searchDao.searchQuery.sort = "eventInfo.timestamp:desc";
        searchDao.updateJsonFromSearch();
    }


    // find the latest fast_lane cycle within +/- 15mins
    this.createFastlaneView = function() {
         $("#id-fastlane-ids").text("");
        const callbackFn = new CallbackWidget(function(data) {$("#id-fastlane-ids").text(cycleSummaryTab.formatMessage(data))});
        const fieldList = ["message" ];
        const searchDao = new FieldModelSearchDAO(callbackFn, new SearchQuery(), fieldList, true);
        var index = dashboard.vmsIndex;
        if(index.search("override") == -1) {
            index = "vms-" + dashboard.vipAddress + "_override" + index.substring(index.length - 13, index.length);
            searchDao.searchQuery.startTime = (dashboard.vmsCycleDate - 900 * 1000);
            searchDao.searchQuery.endTime = searchDao.searchQuery.startTime + (900 * 1000);
        }

        searchDao.searchQuery.size = "1";
        searchDao.searchQuery.indexType = "vmsserver";
        searchDao.searchQuery.indexName =  index;
        searchDao.searchQuery.fields = fieldList;
        searchDao.searchQuery.add("eventInfo.tag:CycleFastlaneIds");
        searchDao.searchQuery.sort = "eventInfo.timestamp:desc";
        searchDao.updateJsonFromSearch();
    }

    this.formatMessage = function(data) {
        if(data && data.length == 1) {
            var idstring = data[0].message;
            if(idstring.length > 2) {
                const ieq = idstring.indexOf("=");
                if(ieq != -1) {
                    idstring = idstring.substr(ieq+1);
                }
                idstring = idstring.substring(1, idstring.length-1);
                idstring = idstring.replace(/,/g , ", ");
                return idstring;
            }
        }
        return "";
    }

    // find the latest fast_lane cycle within +/- 15mins
    this.createPinnedTitlesView = function() {
         $("#id-pinned-titles-ids").text("");
        const callbackFn = new CallbackWidget(function(data) {$("#id-pinned-titles-ids").text(cycleSummaryTab.formatMessage(data))});
        const fieldList = ["message" ];
        const searchDao = new FieldModelSearchDAO(callbackFn, new SearchQuery(), fieldList, true);
        var index = dashboard.vmsIndex;
        if(index.search("override") == -1) {
            index = "vms-" + dashboard.vipAddress + "_override" + index.substring(index.length - 13, index.length);
            searchDao.searchQuery.startTime = (dashboard.vmsCycleDate - 900 * 1000);
            searchDao.searchQuery.endTime = searchDao.searchQuery.startTime + (900 * 1000);
        }

        searchDao.searchQuery.size = "1";
        searchDao.searchQuery.indexType = "vmsserver";
        searchDao.searchQuery.indexName =  index;
        searchDao.searchQuery.fields = fieldList;
        searchDao.searchQuery.add("eventInfo.tag:CyclePinnedTitles").add("pinned").add("titles");
        searchDao.searchQuery.sort = "eventInfo.timestamp:desc";
        searchDao.updateJsonFromSearch();
    }

    this.populateOldestColdstartTable = function(data) {
        if(!data || data.length == 0) {
            return;
        }
        var dataOp = new DataOperator(data);
        var sortedData = dataOp.sort("coldstartVersionId", function(a, b) {
                return Number(a["coldstartVersionId"]) - Number(b["coldstartVersionId"]);
            });

        const tableData = new Array();
        for( var i = 0; i < data.length; i++) {
            const csObj = sortedData[i];
            const date = new Date(Number(csObj["coldstartVersionId"]));
            const diffMillis = dashboard.vmsCycleDate - date;

            if(diffMillis > 3600*3*1000) {
                const dateString = (date.getMonth()+1) + '/' + date.getDate() + " " + date.toLocaleTimeString('en-US', {hour: '2-digit', minute:'2-digit'});
                // "id-cycle-oldest-coldstart"
                cycleSummaryTab.csTable.showHeader = false;
                const val = {
                    "group" : csObj["mutationGroup"],
                    "date" : dateString
                }
                tableData.push(val);
            }
        }
        cycleSummaryTab.csTable.applyParserData(tableData);
    }

    this.findOldestColdstart = function() {
        // mutationGroup=PERSON_BIO latestEventId=1125975801 coldstartVersionId=1463012034071 coldstartKeybase=dummyValue coldstartS3Filename=anotherDummyValue
        var callbackFn = new CallbackWidget(cycleSummaryTab.populateOldestColdstartTable);
        var widgetExecutor = new RegexSearchWidgetExecutor(callbackFn, RegexParserMapper.prototype.getInputDataRegexInfo());
        
        var query = widgetExecutor.searchQuery;
        query.indexName = dashboard.vmsIndex;
        query.indexType = "vmsserver";
        query.size = 50;
        query.add(dashboard.vmsCycleId).add("eventInfo.tag:InputDataVersionIds");
        widgetExecutor.updateJsonFromSearch();
    }

}//status.js
