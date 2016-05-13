function ServerInputSearchTab(serverInfoView) {
    var debugTab = this;
    this.init = false;

    $("#input-search-radio-view").buttonset();
    $("#id-run-search-btn").button();

    this.initialized = function() {
        return this.init;
    };

    this.clear = function() {
        $("#id-indexname-results").html("");
        this.init = false;
    };

    this.refresh = function() {
        var debugWidget = new JsonDisplayWidget("#id-indexname-locations", null); // "#id-indexname-results"
        debugWidget.displayWidget = new InputSearchBuilderWidget(serverInfoView);

        var shardInfoExecutor = new SearchWidgetExecutor(debugWidget, new ShardIndexSearchModel());
        shardInfoExecutor.searchQuery.indexName = serverInfoView.vmsIndex;
        shardInfoExecutor.searchQuery.indexType = "ShardInfo";
        shardInfoExecutor.searchQuery.add("eventInfo.currentCycle:" + serverInfoView.vmsCycleId);
        shardInfoExecutor.updateJsonFromSearch();

        this.init = true;
    };
}

function InputSearchBuilderWidget(serverInfoView) {
    var searchResultWidget = this;
    this.prevVersionData = null;
    $("#id-get-search-url").val("");

    this.displayWidget = new JsonDisplayWidget("#id-search-indexname-types", "#id-search-indexname-results");
    $("#input-search-tree-view").click(function() {
        searchResultWidget.displayWidget.treeView = true;
    });

    $("#input-search-text-view").click(function() {
        searchResultWidget.displayWidget.treeView = false;
    });

    $("#id-run-search-btn").button().click(function() {
        $("#id-search-indexname-types").html("");
        $("#id-search-indexname-results").html("");
        var parser = ResponseModelsFactory.prototype.getModel("IndexTypeResponseModel");
        var searchDao = new SearchDAO(parser, searchResultWidget.displayWidget, true);
        var getMethodSearch = new GetJsonFromSearch($("#id-get-search-url").val(), searchDao);
        getMethodSearch.updateJsonFromSearch();
    });

    $(":checkbox").change(function() {
        searchResultWidget.applyParserData(null);
    });

    $("#id-input-search-query").keyup(function() {
        searchResultWidget.applyParserData(null);
    });

    $("#id-search-fields").keyup(function() {
        searchResultWidget.applyParserData(null);
    });

    $("#id-search-size").keyup(function() {
        searchResultWidget.applyParserData(null);
    });

    this.applyParserData = function(versionData) {
        this.clear();
        if (versionData == null && this.prevVersionData == null) {
            alert("please select a MuatationGroup first");
            return;
        }
        if (versionData == null) {
            versionData = this.prevVersionData;
        }
        this.prevVersionData = versionData;

        var searchQuery = new SearchQuery();
        var mutationGroup = new String(versionData.mutationGroup).toLocaleLowerCase();
        var index = serverInfoView.nflxEnvironment + "_" + mutationGroup + "_*";
        var searchColdStarts = $("#id-search-coldstart").prop('checked');
        var searchEvents = $("#id-search-events").prop('checked');
        var versionQuery = new String("");

        if (searchColdStarts) {
            versionQuery += "(splitterS3Version:" + versionData.coldstartVersion;
        }

        if (searchEvents && versionData.eventStartMutationId > 0) {
            if (searchColdStarts) {
                versionQuery += " OR ";
            }
            versionQuery += "(resolvedMutationId:[";
            versionQuery += versionData.eventStartMutationId + " TO ";
            versionQuery += versionData.eventEndMutationId + "] AND vmsVipName:";
            versionQuery += serverInfoView.vipAddress + ")";
        }
        if (searchColdStarts) {
            versionQuery += ")";
        }

        if ($("#id-input-search-query").val().length > 0) {
            searchQuery.add($("#id-input-search-query").val());
        }

        searchQuery.indexName = index;
        searchQuery.add(versionQuery);

        if ($("#id-search-size").val().length > 0) {
            searchQuery.size = $("#id-search-size").val();
        }

        if ($("#id-search-fields").val().length > 0) {
            searchQuery.fields = $("#id-search-fields").val();
        }

        var queryURL = UrlMapper.prototype.getIndexHost() + searchQuery.toQueryString();
        $("#id-get-search-url").val(queryURL);
    };

    this.clear = function() {
        $("#id-search-indexname-types").html("");
        $("#id-search-indexname-results").html("");
    };

    this.refresh = function() {

    };
}
