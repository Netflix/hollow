/*
 * Assigns appropriate widgets to divs in index.html and drives the user interaction
 */

function VmsServerInfoTab(dashboard) {
    var serverInfoView = this;
    var expandedTabs = true;
    this.cycleSummaryView = null;
    this.cycleStatusView = null;
    this.cycleReplayView = null;
    this.cyclePublishView = null;
    this.cycleValidationView = null;
    this.cycleInputSearchView = null;
    this.cycleErrorsView = null; // same file, see below
    this.cycleDataInjectionView = null; // same file, see below
    this.cycleErrorCodesView = null; // same file, see below
    this.cycleInputDataView = null; // same file, see below
    this.cycleCircuitBreakerView = null; // same file, see below
    this.propertiesTabView = null; // same file, see below
    this.cycleErrorCodesTabView = null; // same file, see below
    this.dataInjectionTabView = null; // same file, see below

    this.nflxEnvironment = dashboard.nflxEnvironment;
    this.dataNameSpace = dashboard.dataNameSpace;
    this.vipAddress = null; // TODO set default

    this.elasticSearchHost = null;
    this.vmsCycleId = null;
    this.vmsIndex = null;
    this.vmsCycleDate = null;
    this.initialized = false;
    this.nflxRegion = "us-east-1";

    // --------------------------------------------------------------------
    // Set params from vms REST queries and create tabs
    // --------------------------------------------------------------------
    this.initialize = function() {
        serverInfoView.elasticSearchHost = $("#id-elasticsearchhost-box").val();

        $.ajax({
            async : false,
            type : 'GET',
            url : '/REST/vms/elasticsearchadmin?query=elasticsearchhost',
            success : function(data) {
                var hostname = new String(data);
                // if (hostname.indexOf("ec") == 0) {
                    $("#id-elasticsearchhost-box").val(data);
                    serverInfoView.elasticSearchHost = hostname;
                //}
            }
        });

        $.ajax({
            async : false,
            type : 'GET',
            url : '/REST/vms/elasticsearchadmin?query=vip-address',
            success : function(data) {
                serverInfoView.vipAddress = new String(data);
            }
        });

        $.ajax({
            async : false,
            type : 'GET',
            url : '/REST/vms/elasticsearchadmin?query=nflx-region',
            success : function(data) {
                var nflxReg = new String(data);
                if (nflxReg) {
                    serverInfoView.nflxRegion = nflxReg;
                }
            }
        });

        $("#id-hostname-apply-btn").button();
        $("#id-search-error-btn").button();

        // default tab
        serverInfoView.createServerInfoTab();
    };

    // --------------------------------------------------------------------
    // Server Information Tab
    // --------------------------------------------------------------------
    this.createServerInfoTab = function() {
        if (this.vipAddress == null) {
            this.vipAddress = this.nflxEnvironment == "test" ? "berlin" : "berlin";
        }

        $("#id-cycle-prev-btn").button({
            text : "Prev",
            icons : {
                primary : "ui-icon-carat-1-w"
            }
        }).click(function() {
            var iSelected = $("#id-vms-cycle-select option:selected").index();
            var arrCycles = serverInfoView.getIndexCycles();
            if (iSelected < arrCycles.length - 1) {
                iSelected++;
            }
            serverInfoView.cycleSummaryView.cycleSummarytableWidget.updateHighlight(arrCycles[iSelected]);
            $("#id-vms-cycle-select").val(arrCycles[iSelected]).change();
        });

        $("#id-cycle-next-btn").button({
            text : "Next",
            icons : {
                primary : "ui-icon-carat-1-e"
            }
        }).click(function() {
            var iSelected = $("#id-vms-cycle-select option:selected").index();
            var arrCycles = serverInfoView.getIndexCycles();
            if (iSelected > 0) {
                iSelected--;
            }
            serverInfoView.cycleSummaryView.cycleSummarytableWidget.updateHighlight(arrCycles[iSelected]);
            $("#id-vms-cycle-select").val(arrCycles[iSelected]).change();
        });

        var selectWidget = new SelectOptionsWidget("#id-vms-vip-select", null, serverInfoView.formatVIP, true, "vms-" + this.vipAddress + "-cyc");
        selectWidget.doSort = true;

        var statsDao = new StatsDAO(selectWidget, true);
        $("#id-vms-vip-select").change(function() {
            var cycSelectWidget = new SelectOptionsWidget("#id-vms-index-select", "name", serverInfoView.formatIndex, true);
            var cycStatsDao = new StatsDAO(cycSelectWidget);
            var indexType = new String($("#id-vms-vip-select").val());

            var hasVipChanged = typeof(serverInfoView.vipAddress) == "string";
            serverInfoView.vipAddress = indexType.substring(4, indexType.length - 4);
            cycStatsDao.updateJsonFromSearch(indexType, indexType);

            dashboard.refreshBlobInfo();
            if (hasVipChanged==true) {
                // disable for now since NAC's REST is blocked from EC2 instances
                //dashboard.changeDashboard();
            }
        });

        $("#id-vms-index-select").change(function() {
            serverInfoView.vmsIndex = new String($("#id-vms-index-select").val());
            serverInfoView.createCycleTimeStampsTable();
            $("#id-cycle-timestamp-div").show();

            var cycSelectWidget = new SelectOptionsWidget("#id-vms-cycle-select", null, null, true);
            var cycleWidgetExecutor = new KeyValueWidgetExecutor(cycSelectWidget, 'currentCycle', true);
            cycleWidgetExecutor.searchQuery.indexName = $("#id-vms-index-select").val();
            cycleWidgetExecutor.searchQuery.indexType = "vmsserver";
            cycleWidgetExecutor.searchQuery.add("eventInfo.tag:TransformCycleBegin");
            cycleWidgetExecutor.searchQuery.size = VipAddressHolder.prototype.getSummaryQuerySize();
            cycleWidgetExecutor.searchQuery.sort = "eventInfo.timestamp:desc";
            cycleWidgetExecutor.updateJsonFromSearch();
        });

        statsDao.updateJsonFromSearch("vms");
        serverInfoView.createRolesInformation();

        this.cycleReplayView = new ReplayCycleView(serverInfoView);
        // get the live timer going (once and only once)
        this.cycleReplayView.setupLiveControl();
        this.cycleReplayView.startRealTimeStatsTimer();
        this.cycleStatusView = new ServerCycleStatusTab(serverInfoView);
        this.cyclePublishView = new ServerPublishTab(serverInfoView);
        this.cycleInputSearchView = new ServerInputSearchTab(serverInfoView);
        this.propertiesTabView = new CyclePropertiesTab(serverInfoView);
        this.cycleErrorCodesTabView = new CycleErrorCodesTab(serverInfoView);
        this.cycleInputDataView = new CycleInputDataInformation(serverInfoView);
        this.cycleCircuitBreakerView = new CircuitBreakerTab(serverInfoView);
        this.dataInjectionTabView = new DataInjectionTab(serverInfoView);
        this.cycleErrorsView = new CycleErrorTab(serverInfoView);

        // ------------------------------------------------------------
        // update lazy tabs when activated
        $("#cycle-stats-tabs").on("tabsbeforeactivate", function(e, ui) {
            var id = ui.newPanel.attr('id');
            serverInfoView.refreshLazyTab(id, {
                forceRefresh : false
            });
        });

        $("#cycle-stats-tabs").on("tabsactivate", function(e, ui) {
            var id = ui.newPanel.attr('id');
            if (id == "expander-tab") {
                serverInfoView.toggleExpandTabs("slide");
                $("#cycle-stats-tabs").tabs("option", "active", 0);
            }
        });

        // do not expand by default
        serverInfoView.toggleExpandTabs("none");
    };


    this.toggleExpandTabs = function(effect) {
        var lowUseTabs = ["#cycle-circuitbreaker-tab", "#cycle-input-validation-tab", "#cycle-error-codes-tab",
                            "#cycle-input-search-tab", "#data-injection-tab"];
        if(expandedTabs == true) {
            $.each(lowUseTabs, function(i, tabRef) {
                if(effect == "none") {
                    $('[href=' + tabRef + ']').closest('li').hide();
                } else {
                    $('[href=' + tabRef + ']').closest('li').hide( { effect: effect, duration: 200 });
                }
            });
            expandedTabs = false;
        } else {
            $.each(lowUseTabs, function(i, tabRef) {
                $('[href=' + tabRef + ']').closest('li').show({ effect: effect, duration: 200 });
            });
            expandedTabs = true;
        }
        $("#cycle-stats-tabs").tabs();
    }

    this.getIndexCycles = function() {
        var cycleList = new Array();
        $("#id-vms-cycle-select option").each(function(index) {
            cycleList.push($(this).val());
        });
        return cycleList;
    };

    $("#id-vms-cycle-select").change(function() {
        serverInfoView.refresh();
    });

    $("#id-cycle-refresh-btn").click(function() {
        $("#id-vms-vip-select").change(); 
        // serverInfoView.refresh();
    });

    function initializeInputValidationTab(serverInfoView) {
        serverInfoView.cycleValidationView = new InputValidationTab(serverInfoView);
    }

    this.refresh = function() {
        var h = window.innerHeight;
        // alert("h=" +h + ", d=" + $(document).height());
        $("#id-cycle-timestamp-div").height(h - 100);
        serverInfoView.clearLazyLoadElements();
        serverInfoView.vmsCycleId = new String($("#id-vms-cycle-select").val());
        $("#id-cycle-id-txt").text("Cycle: " + serverInfoView.vmsCycleId);
        serverInfoView.cycleIdToDate();
        $("#id-cycle-date-txt").html(serverInfoView.vmsCycleDate.toLocaleString());

        // ------------------------------------------------------------
        // recreate tabs
        // this.cycleDataInjectionView = new DataInjectionTab(serverInfoView);
        // this.cycleErrorCodesView = new CycleErrorCodesTab(serverInfoView);

        // ------------------------------------------------------------
        // refresh live tab
        this.cycleErrorsView.refresh();
        this.cycleReplayView.refresh();

        // ------------------------------------------------------------
        // Refresh lazy load tabs
        var activeTabIndex = $("#cycle-stats-tabs").tabs("option", "active");
        var activeTabObj = $('#cycle-stats-tabs .ui-tabs-panel:eq(' + activeTabIndex + ')');
        var tabName = activeTabObj.attr("id");
        serverInfoView.refreshLazyTab(tabName, {
            forceRefresh : true
        });

        // default view
        this.cycleStatusView.refresh();
    };

    this.cycleIdToDate = function() {
        var cycleStr = new String(serverInfoView.vmsCycleId);
        var year = parseInt(cycleStr.substring(0, 4));
        var month = parseInt(cycleStr.substring(4, 6)) - 1;
        var day = parseInt(cycleStr.substring(6, 8));
        var hr = parseInt(cycleStr.substring(8, 10));
        var min = parseInt(cycleStr.substring(10, 12));
        var sec = parseInt(cycleStr.substring(12, 14));

        serverInfoView.vmsCycleDate = new Date(Date.UTC(year, month, day, hr, min, sec));
    };

    this.formatVIP = function(vipcyc) {
        var vipStr = new String(vipcyc);
        return vipStr.substring(4, vipStr.length - 4);
    };

    this.formatIndex = function(indexname) {
        var indexStr = new String(indexname);
        var dateSegment = indexStr.substring(9 + serverInfoView.vipAddress.length, indexStr.length);
        return dateSegment.substring(0, 4) + "-" + dateSegment.substring(4, 6) + "-" + dateSegment.substring(6, 8);
    };

    this.cycleIdSelector = function(cycle) {
        $("#id-vms-cycle-select").val(cycle).change();
    };

    this.createRolesInformation = function() {
        $.get("/REST/vms/rolesinformation", function(data) {
            var widget = new JsonViewWidget("#id-role-information-locations");
            widget.applyParserData(data);
        });
    };

    // --------------------------------------------------------------------
    // Part of Default Cycle View
    // --------------------------------------------------------------------
    this.createCycleTimeStampsTable = function() {
        this.cycleSummaryView = new ServerCycleSummaryTab(serverInfoView);
        this.cycleSummaryView.initialize();
    };

    this.clearLazyLoadElements = function() {
        $("#cycle-input-validation-results").html("");
        serverInfoView.propertiesTabView.clear();
        serverInfoView.cyclePublishView.clear();
        serverInfoView.cycleInputSearchView.clear();
        serverInfoView.cycleInputDataView.clear();
        serverInfoView.cycleCircuitBreakerView.clear();
        serverInfoView.cycleErrorCodesTabView.clear();
        serverInfoView.dataInjectionTabView.clear();
    };

    this.refreshLazyTab = function(id, action) {
        if (id != "cycle-replay-tab") {
            if (!this.cycleReplayView.pauseRealTimeRefresh) {
                $("#id-startlive-btn").click();
            }
        }

        if(id == "cycle-status-tab") {
            if(!this.cycleStatusView.autoUpdateFlag) {
                this.cycleStatusView.autoUpdateFlag = true;
                this.cycleStatusView.autoUpdate();
            }
        } else {
            this.cycleStatusView.autoUpdateFlag = false;
        }

        if (id == "cycle-circuitbreaker-tab") {
            if (action.forceRefresh || !serverInfoView.cycleCircuitBreakerView.initialized()) {
                serverInfoView.cycleCircuitBreakerView.refresh();
            }
        }else if (id == "cycle-publish-tab") {
            if (action.forceRefresh || !serverInfoView.cyclePublishView.initialized()) {
                serverInfoView.cyclePublishView.refresh();
            }
        } else if (id == "cycle-input-search-tab") {
            if (action.forceRefresh || !serverInfoView.cycleInputSearchView.initialized()) {
                serverInfoView.cycleInputSearchView.refresh();
            }
        } else if (id == "cycle-input-validation-tab") {
            if (action.forceRefresh || $("#cycle-input-validation-results").html() == "") {
                initializeInputValidationTab(serverInfoView);
            }
        } else if (id == "cycle-inputdata-tab") {
            if (action.forceRefresh || !serverInfoView.cycleInputDataView.initialized()) {
                serverInfoView.cycleInputDataView.refresh();
            }
        } else if (id == "cycle-properties-tab") {
            if (action.forceRefresh || !serverInfoView.propertiesTabView.initialized()) {
                serverInfoView.propertiesTabView.refresh();
            }
        } else if (id == "cycle-error-codes-tab") {
            if (action.forceRefresh || !serverInfoView.cycleErrorCodesTabView.initialized()) {
                serverInfoView.cycleErrorCodesTabView.refresh();
            }
        } else if (id == "data-injection-tab") {
            if (action.forceRefresh || !serverInfoView.dataInjectionTabView.initialized()) {
                serverInfoView.dataInjectionTabView.refresh();
            }
        }
    }
}// serverInfoView


// --------------------------------------------------------------------
// PropertiesTab
// --------------------------------------------------------------------
function CyclePropertiesTab(serverInfoView) {
    var refFn = this;
    this.init = false;
    this.htmlDiv = "#id-properties-table";

    this.initialized = function() {
        return this.init;
    };

    this.clear = function() {
        $(this.htmlDiv).html("");
        this.init = false;
    };

    this.refresh = function() {
        var tableFields = ["propertyName", "propertyValue"];
        var tableWidget = new DataTableWidget(this.htmlDiv, "cycle-properties-table", tableFields);
        var widgetExecutor = new RegexSearchWidgetExecutor(tableWidget, RegexParserMapper.prototype.getPropertiesRegexInfo());
        var query = widgetExecutor.searchQuery;
        query.indexName = serverInfoView.vmsIndex;
        query.indexType = "vmsserver";
        query.size = "200";
        query.sort = "eventInfo.timestamp:desc";
        query.add(serverInfoView.vmsCycleId).add("eventInfo.tag:PropertyValue");
        widgetExecutor.updateJsonFromSearch();
        this.init = true;
    };
}

//--------------------------------------------------------------------
//CircuitBreakerTab
//--------------------------------------------------------------------
function CircuitBreakerTab(serverInfoView) {
 var refFn = this;
 this.init = false;
 this.htmlDiv = "#id-cycle-circuitbreaker-locations";

 $("#id-workerid-select").change(function() {
     refFn.refresh();
 });

 this.initialized = function() {
     return this.init;
 };

 this.clear = function() {
     $(this.htmlDiv).html("");
     this.init = false;
 };

 this.refresh = function() {
     var tableWidget = new DataTableWidget(this.htmlDiv, "circuitbreaker-stats-table", 
                                            [ "partitionId", "ServerCache", "Country", "RuleName", "ResultCode", "FailedIDs"]);

     var widgetExecutor = new SearchWidgetExecutor(tableWidget, new CircuitBreakerDataModel());
     widgetExecutor.searchQuery.indexName = serverInfoView.vmsIndex;
     widgetExecutor.searchQuery.indexType = "vmsserver";
     widgetExecutor.searchQuery.size = "500";
     widgetExecutor.searchQuery.sort = "eventInfo.timestamp:desc";
     widgetExecutor.searchQuery.add(serverInfoView.vmsCycleId);

     widgetExecutor.searchQuery.add("CircuitBreakerFailedValidation");
     widgetExecutor.updateJsonFromSearch();
     this.init = true;
 };
}


// --------------------------------------------------------------------
// CycleErrorTab
// --------------------------------------------------------------------
function CycleErrorTab(serverInfoView) {
    var refFn = this;
    var reqWidth = 0;
    var fields = ["timestamp","message"]
    this.tableWidget = new DataTableWidget("#id-cycle-error-locations", "id-table-error-results", fields);
    this.tableWidget.reformatCellDataFunc = new JavaExceptionFormatter("com.netflix.videometadata.").forma
    
    $("#id-loglevel-select").change(function() {
        refFn.refresh();
    });

    this.refresh = function() {
        var fieldKeys = [ "key", "doc_count" ];
        var errCodesWidget = new ClickableTableWidget("#id-cycle-error-list", "id-cycle-error-table", fieldKeys, [ "Tag", "Num"], 0,
                function(fieldValue) {
                    $("#id-search-error-box").val(fieldValue);
                    $("#id-search-error-btn").button().click();
                });
        $("#id-search-error-box").val("");
        $("#id-cycle-error-locations").html("");
        var query = new SearchQuery();
        query.indexName = serverInfoView.vmsIndex;
        query.add("eventInfo.currentCycle:" + serverInfoView.vmsCycleId);
        query.add($("#id-loglevel-select").val());
        query.aggregate = "eventInfo.tag";
        var searchDao = new SearchAggregationDAO(errCodesWidget, query, true);
        searchDao.updateJsonFromSearch();
    }

    $("#id-search-error-btn").button().click(function() {
        var availWidth = $("#id-cycle-error-container").width();
        var usedWidth = $("#id-cycle-error-table").width();
        refFn.reqWidth = availWidth - usedWidth - 10;
        // alert('a=' + availWidth + ', u=' + usedWidth + ', r=' + refFn.reqWidth);
        $("#id-cycle-error-locations").width(refFn.reqWidth);

        var searchQuery = new SearchQuery();
        searchQuery.indexName = $("#id-vms-index-select").val();
        searchQuery.size = "200";
        searchQuery.add("eventInfo.currentCycle:" + serverInfoView.vmsCycleId);
        searchQuery.add( $("#id-search-error-box").val());
        searchQuery.add($("#id-loglevel-select").val());
        searchQuery.sort = "eventInfo.timestamp:desc";
        var searchdao = new FieldModelSearchDAO(refFn.tableWidget, searchQuery, fields, true);
        searchdao.updateJsonFromSearch();
    });

    refFn.refresh();
}

// --------------------------------------------------------------------
// CycleErrorCodesTab
// --------------------------------------------------------------------
function CycleErrorCodesTab(serverInfoView) {
    var refFn = this;
    this.init = false;
    this.htmlDiv = "#id-cycle-errorcode-list";

    this.initialized = function() {
        return this.init;
    };

    this.clear = function() {
        $(this.htmlDiv).html("");
        this.init = false;
    };

    this.refresh = function() {
        var errorCodeWidgetExecutor = new SearchWidgetExecutor(new JsonViewWidget("#id-cycle-errorcode-locations"));
        errorCodeWidgetExecutor.searchQuery.indexName = serverInfoView.vmsIndex;
        errorCodeWidgetExecutor.searchQuery.indexType = "ErrorCodes";
        errorCodeWidgetExecutor.searchQuery.add("eventInfo.currentCycle:" + serverInfoView.vmsCycleId);
        errorCodeWidgetExecutor.updateJsonFromSearch();

        $("#id-search-errorcode-btn").button().click(function() {
            var errorCodeWidgetExecutor = new SearchWidgetExecutor(new JsonViewWidget("#id-cycle-errorcode-locations"));
            errorCodeWidgetExecutor.searchQuery.indexName = $("#id-vms-index-select").val();
            errorCodeWidgetExecutor.searchQuery.indexType = "ErrorCodes";
            errorCodeWidgetExecutor.searchQuery.size = "100";
            errorCodeWidgetExecutor.searchQuery.add("eventInfo.currentCycle:" + serverInfoView.vmsCycleId);
            errorCodeWidgetExecutor.searchQuery.add($("#id-search-errorcode-box").val());
            errorCodeWidgetExecutor.searchQuery.fields = "inputData.idErrorCode,inputData.idClassName";
            errorCodeWidgetExecutor.updateJsonFromSearch();
        });
        this.init = true;
    }
}

// --------------------------------------------------------------------
// Coldstart/Events Tab
// --------------------------------------------------------------------
function CycleInputDataInformation(serverInfoView) {
    this.init = false;

    this.initialized = function() {
        return this.init;
    };

    this.clear = function() {
        $("#id-cycle-input-shardinfo-locations").html("");
        $("#id-cycle-output-shardinfo-locations").html("");
        this.init = false;
    };

    this.setPropertiesForQueryObject = function(query, indexType, num) {
        query.indexName = serverInfoView.vmsIndex;
        query.indexType = indexType;
        query.size = num;
        query.add(serverInfoView.vmsCycleId);
    };

    this.refresh = function() {
        // Render the "Inputs to the transformer" table
        var transformerInputFields = [ "Input", "Version"];
        var transformerInputsWidget = new DataTableWidget("#id-cycle-input-data-versions", "id-table-cycle-input-data-versions", transformerInputFields);
        var transformerInputsWidgetExecutor = new RegexSearchWidgetExecutor(transformerInputsWidget, RegexParserMapper.prototype.getCinderInputDataVersionsRegexInfo());
        this.setPropertiesForQueryObject(transformerInputsWidgetExecutor.searchQuery, "vmsserver", "50");
        transformerInputsWidgetExecutor.searchQuery.add("eventInfo.tag:CinderInputDataVersions");
        transformerInputsWidgetExecutor.updateJsonFromSearch();

        // Render the "Inputs to the converter" table
        var converterInputFields = [ "Input", "Keybase", "Type", "Version", "EventId", "EventCheckpoint", "FileName", "PublishTime" ];
        var converterInputsWidget = new DataTableWidget("#id-cycle-input-shardinfo-locations", "id-table-input-shardinfo-results", converterInputFields);
        var converterInputsWidgetExecutor = new RegexSearchWidgetExecutor(converterInputsWidget, RegexParserMapper.prototype.getInputDataRegexInfo());
        this.setPropertiesForQueryObject(converterInputsWidgetExecutor.searchQuery, "vmsserver", "50");
        converterInputsWidgetExecutor.searchQuery.add("eventInfo.tag:InputDataVersionIds");
        converterInputsWidgetExecutor.updateJsonFromSearch();

        this.init = true;
    };
}


// --------------------------------------------------------------------
// DataInjectionTab
// --------------------------------------------------------------------
function DataInjectionTab(serverInfoView) {
    this.init = false;

    this.initialized = function() {
        return this.init;
    };

    this.clear = function() {
        $("#id-datainjection-results").html("");
        $("#id-datainjection-locations").html("");
        this.init = false;
    };

    this.refresh = function() {
        var tableWidget = new DataTableWidget("#id-datainjection-results", "id-datainjection-table-error-results", [ "timestamp", "partitionId", "ec2InstanceId", "className", "message" ]);
        // custom formatting of cell content
        tableWidget.reformatCellDataFunc = new CodePreFormatter("com.netflix.videometadata.server.datainjection.", "import").format;

        var dataInjectionWidget = new JsonDisplayWidget("#id-datainjection-locations", "#id-datainjection-results");
        dataInjectionWidget.displayWidget = tableWidget;
        var dataInjectionWidgetExecutor = new KeyValueWidgetExecutor(dataInjectionWidget, 'errorCode');
        dataInjectionWidgetExecutor.searchQuery.indexName = serverInfoView.vmsIndex;
        dataInjectionWidgetExecutor.searchQuery.add("eventInfo.currentCycle:" + serverInfoView.vmsCycleId);
        dataInjectionWidgetExecutor.searchQuery
                .add("(eventInfo.errorCode:DataInjectionInfo OR eventInfo.errorCode:DataInjectionDebug OR eventInfo.errorCode:DataInjectionScriptInfo OR eventInfo.errorCode:DataInjectionScriptDebug)");
        // AND NOT DeserializeInputDataError
        dataInjectionWidgetExecutor.searchQuery.size = "300";
        dataInjectionWidgetExecutor.searchQuery.sort = "eventInfo.timestamp:asc";
        dataInjectionWidgetExecutor.updateJsonFromSearch();
        this.init = true;
    }
}
