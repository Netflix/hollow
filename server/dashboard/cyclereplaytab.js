function ReplayCycleView(dashboard) {
    var replayCycleTab = this;
    this.autoUpdateWidgets = [];
    this.autoUpdateDAO = [];
    this.timeStepForAutoUpdate = 10; // seconds
    this.liveStartTime = 0;
    this.alignedEndTime = null;
    this.alignBoundary = 5; // seconds
    this.maxRunTime = 300; // seconds
    this.startReplayDate = null;
    this.pauseRealTimeRefresh = true;

    this.refresh = function() {
        // ------------------------------------------------------------
        // create live tab
        var stquery = new SearchQuery();
        stquery.indexName = dashboard.vmsIndex;
        stquery.indexType = "vmsserver";
        stquery.add("eventInfo.currentCycle:" + dashboard.vmsCycleId);

        var starter = new IndexStartTimeDAO(stquery, replayCycleTab.createLiveUpdateTab);
        starter.updateJsonFromSearch();
    };

    this.createLiveUpdateTab = function(startTime) {
        replayCycleTab.liveStartTime = replayCycleTab.alignTimeToBoundary(startTime, -1);
        replayCycleTab.autoUpdateWidgets = new Array();
        replayCycleTab.autoUpdateDAO = new Array();

        replayCycleTab.createSplitterEventsTable();
        replayCycleTab.createGlobalServerLogTable();
        replayCycleTab.createTransformationsGraph();
        replayCycleTab.createWorkerPublishGraph();
        replayCycleTab.createS3PublishGraph();
        replayCycleTab.createS3CopyGraph();
    };

    this.setupLiveControl = function() {
        $("#id-startlive-btn").button({
            label : "play",
            text : false,
            icons : {
                primary : "ui-icon-play"
            }
        }).click(function() {
            var options = {
                text : false,
                label : "play",
                icons : {
                    primary : "ui-icon-play"
                }
            };

            if ($(this).text() == "pause") {
                replayCycleTab.pauseRealTimeRefresh = true;
            } else if ($(this).text() == "play") {
                options = {
                    text : false,
                    label : "pause",
                    icons : {
                        primary : "ui-icon-pause"
                    }
                };

                replayCycleTab.timeStepForAutoUpdate =10;
                replayCycleTab.pauseRealTimeRefresh = false;
                replayCycleTab.startReplayDate = new Date();
            }
            $(this).button("option", options);
            return false;
        });

        $("#id-slowlive-btn").button({
            label : "slow",
            text : false,
            icons : {
                primary : "ui-icon-seek-prev"
            }
        }).click(function() {
            replayCycleTab.timeStepForAutoUpdate = 5;
        });

        $("#id-slowerlive-btn").button({
            label : "slower",
            text : false,
            icons : {
                primary : " ui-icon-arrowthick-1-w"
            }
        }).click(function() {
            replayCycleTab.timeStepForAutoUpdate = 1;
        });

        $("#id-fastlive-btn").button({
            label : "fast",
            text : false,
            icons : {
                primary : "ui-icon-seek-next"
            }
        }).click(function() {
            replayCycleTab.timeStepForAutoUpdate = 30;
        });

        $("#id-fasterlive-btn").button({
            label : "faster",
            text : false,
            icons : {
                primary : " ui-icon-arrowthick-1-e"
            }
        }).click(function() {
            replayCycleTab.timeStepForAutoUpdate = 60;
        });
    };

    this.createTransformationsGraph = function() {
        var graphWidget = new TimeSeriesGraphWidget("#id-transformations-live", 0, "Finished Builders", "");
        replayCycleTab.autoUpdateWidgets.push(graphWidget);

        var searchCountDAO = new SearchCountDAO(graphWidget, new SearchQuery(replayCycleTab.liveStartTime, replayCycleTab.timeStepForAutoUpdate),
                "transformer", false);
        searchCountDAO.searchQuery.searchType = "count";
        searchCountDAO.searchQuery.indexName = dashboard.vmsIndex;
        searchCountDAO.searchQuery.indexType = "vmsserver";
        searchCountDAO.searchQuery.add("eventInfo.currentCycle:" + dashboard.vmsCycleId);
        searchCountDAO.searchQuery.add("AnnouncementSuccess");
        replayCycleTab.autoUpdateDAO.push(searchCountDAO);
        ;
    };

    this.getTimeRangeQueryInstance = function(indexType, searchType) {
        var query = new SearchQuery(replayCycleTab.liveStartTime, replayCycleTab.timeStepForAutoUpdate);
        query.add("eventInfo.currentCycle:" + dashboard.vmsCycleId);
        query.indexName = dashboard.vmsIndex;
        query.indexType = indexType;
        if (!searchType) { // null or undefined
        } else {
            query.searchType = searchType;
        }
        return query;
    };

    this.createWorkerPublishGraph = function() {
        var graphWidget = new TimeSeriesGraphWidget("#id-worker-publish-live", 0, "StateEngine Published", "");
        replayCycleTab.autoUpdateWidgets.push(graphWidget);

        var searchCountDAO = new SearchCountDAO(graphWidget, this.getTimeRangeQueryInstance("vmsserver", "count"), "workers finished", false);
        searchCountDAO.searchQuery.add("eventInfo.currentCycle:" + dashboard.vmsCycleId);
        searchCountDAO.searchQuery.add("tag:PublishedBlob").add("shard");
        replayCycleTab.autoUpdateDAO.push(searchCountDAO);
    };

    this.createS3PublishGraph = function() {
        var graphWidget = new TimeSeriesGraphWidget("#id-s3-publish-live", 0, "S3 Blobs Published", "");
        replayCycleTab.autoUpdateWidgets.push(graphWidget);

        var searchCountDAO = new SearchCountDAO(graphWidget, this.getTimeRangeQueryInstance("vmsserver", "count"), "combiner", false);
        searchCountDAO.searchQuery.add("tag:PublishedBlob");
        replayCycleTab.autoUpdateDAO.push(searchCountDAO);
    };

    this.createS3CopyGraph = function() {
        var graphWidget = new TimeSeriesGraphWidget("#id-s3-copy-live", 0, "S3 Blobs Copied", "");
        replayCycleTab.autoUpdateWidgets.push(graphWidget);

        var searchCountDAO = new SearchCountDAO(graphWidget, this.getTimeRangeQueryInstance("vmsserver", "count"), "combiner", false);
        searchCountDAO.searchQuery.add("tag:CopiedBlobAcrossRegions");
        replayCycleTab.autoUpdateDAO.push(searchCountDAO);
    };

    this.createSplitterEventsTable = function() {
        var tableWidget = new DataTableWidget("#id-resolve-events-live", "splitter-events-table", [ "timestamp", "message" ]);
        tableWidget.toDataTable = false;
        tableWidget.clearPrevious = false;
        replayCycleTab.autoUpdateWidgets.push(tableWidget);

        var searchFieldModelDAO = new FieldModelSearchDAO(tableWidget, this.getTimeRangeQueryInstance("Splitter"), [ "timestamp", "message" ], false);
        searchFieldModelDAO.searchQuery.sort = "eventInfo.timestamp";
        searchFieldModelDAO.searchQuery.size = "7";
        searchFieldModelDAO.timestampParserFunc = replayCycleTab.parseTimeStamp;
        replayCycleTab.autoUpdateDAO.push(searchFieldModelDAO);
    };

    this.createGlobalServerLogTable = function() {
        var tableWidget = new DataTableWidget("#id-global-tail-div", "id-global-tail-table", [ "timestamp", "message" ]);
        tableWidget.toDataTable = false;
        tableWidget.clearPrevious = false;
        replayCycleTab.autoUpdateWidgets.push(tableWidget);

        var searchFieldModelDAO = new FieldModelSearchDAO(tableWidget, this.getTimeRangeQueryInstance(null), [ "timestamp", "message" ],
                false);
        searchFieldModelDAO.searchQuery.size = "5";
        searchFieldModelDAO.searchQuery.sort = "eventInfo.timestamp:desc";
        replayCycleTab.autoUpdateDAO.push(searchFieldModelDAO);
    };

    this.parseTimeStamp = function(timestamp) {
        var date = new Date(timestamp);
        var dateString = date.toLocaleTimeString("en-US", {
            hour12 : false
        });
        return dateString;
    };

    this.alignEndTimeToCurrentTimeBoundary = function(addOrSubtract) {
        var tmillis = new Date().getTime();
        var tseconds = addOrSubtract >= 0 ? Math.ceil(tmillis / 1000) : Math.floor(tmillis / 1000);
        var delta = ((tseconds % 60) % this.alignBoundary);
        this.alignedEndTime = (tseconds + addOrSubtract * delta) * 1000;
    };

    this.alignTimeToBoundary = function(tmillis, addOrSubtract) {
        var tseconds = addOrSubtract >= 0 ? Math.ceil(tmillis / 1000) : Math.floor(tmillis / 1000);
        var delta = ((tseconds % 60) % this.alignBoundary);
        return (tseconds + addOrSubtract * delta) * 1000;
    };

    this.startRealTimeStatsTimer = function() {
        replayCycleTab.refreshRealTimeStats();
        setTimeout(replayCycleTab.startRealTimeStatsTimer, 2000);
    };

    this.refreshRealTimeStats = function() {
        if (replayCycleTab.pauseRealTimeRefresh == true) {
            return;
        }

        var runTime = new Date().getTime() - replayCycleTab.startReplayDate.getTime();
        if (runTime / 1000 > replayCycleTab.maxRunTime) {
            $("#id-startlive-btn").click();

            $("#id-live-dialog").dialog({
                resizable : false,
                height : 140,
                modal : true,
                buttons : {
                    Continue : function() {
                        $("#id-startlive-btn").click();
                        $(this).dialog("close");
                    },
                    Stop : function() {
                        $(this).dialog("close");
                    }
                }
            });
        }

        replayCycleTab.alignEndTimeToCurrentTimeBoundary(1);

        for (var iwidget = 0; iwidget < replayCycleTab.autoUpdateWidgets.length; iwidget++) {
            replayCycleTab.autoUpdateWidgets[iwidget].refresh();
        }

        for (var idao = 0; idao < replayCycleTab.autoUpdateDAO.length; idao++) {
            replayCycleTab.autoUpdateDAO[idao].searchQuery.startTime = replayCycleTab.liveStartTime;
            replayCycleTab.autoUpdateDAO[idao].searchQuery.intervalSeconds = replayCycleTab.timeStepForAutoUpdate;
            replayCycleTab.autoUpdateDAO[idao].searchQuery.setNextEndTimePeriod(replayCycleTab.alignedEndTime);
            replayCycleTab.autoUpdateDAO[idao].updateJsonFromSearch();
            replayCycleTab.autoUpdateDAO[idao].searchQuery.live = true;
        }
    };

}// ReplayCycle

