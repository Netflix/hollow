// MinerDashboard
function MinerDashboard() {
    var miner = this;
    this.nflxEnvironment = "test";

    // --------------------------------------------------------------------
    this.createIndexesTab = function() {

        var fieldKeys = [ "coldstartVersion", "keybase", "custom" ];
        var versionsWidget = new ClickableTableWidget("#mid-index-versions", "mid-index-versions-table", fieldKeys, [ "Version", "Keybase", "Date" ], 0,
                null, function(row) {
                    var datestr = new Date(Number(row["coldstartVersion"])).toLocaleString();
                    return {
                        trow : "<tr>",
                        tcols : "<td>" + datestr + "</td>"
                    };
                });

        versionsWidget.onRowClick = function(rowData, row) {
            $("#mid-search-query").val("");
            var keybase = rowData["keybase"];
            var prefix = "com.netflix.vms.okeanos.";
            var st = keybase.indexOf(prefix);

            if (st == 0) {
                var end = keybase.indexOf(".", st + prefix.length + 1);
                var vip = keybase.substring(st + prefix.length, end);
                $("#mid-search-vip").val(vip);
            } else {
                $("#mid-search-vip").val("");
            }

            row.addClass("highlightrow");
            $("#mid-search-version").val(versionsWidget.getSelectedRows("coldstartVersion").toString());
        };

        var tableWidget = new ClickableTableWidget("#mid-index-list", "index-stats-table", [ "name", "count" ], [ "Index", "Documents" ], 0, function(
                indexName, e) {
            $("#mid-search-index-name").val(indexName);
            $("#mid-indexname-box").val(indexName);
            $("#mid-search-index-type").val("");
            $("#mid-search-query").val("");
            miner.populateIndexVersions(versionsWidget, indexName);
        });

        miner.clearAll();
        var jsonWidget = new JsonDisplayWidget("#mid-index-types", "#mid-index-list");
        jsonWidget.onClick = function(label) {
            $("#mid-index-versions").html("");
            $("#mid-indexname-locations").html("");
            $("#mid-indexname-results").html("");
            $("#mid-search-query").val("");
        };
        jsonWidget.displayWidget = tableWidget;
        var statsDao = new StatsDAO(jsonWidget);
        var envTypes = (miner.nflxEnvironment == "test") ? "test*,int" : "prod";
        console.log("envType = " + envTypes);
        statsDao.updateJsonFromSearch(envTypes);
    };

    this.populateIndexVersions = function(versionsWidget, indexName) {
        $("#mid-index-versions").html("");
        var searchDao = new FieldModelSearchDAO(versionsWidget, miner.getInputVersionsQuery(indexName), [ "coldstartVersion", "keybase" ], true);
        searchDao.updateJsonFromSearch();
    };

    this.getInputVersionsQuery = function(indexName) {
        var query = new SearchQuery();
        query.indexName = miner.nflxEnvironment + "_keybaseversions_0";
        query.size = "50";
        query.fields = "coldstartVersion,keybase";
        query.sort = "timeStamp:desc";
        query.add("indexName:" + indexName);
        return query;
    };

    this.clearAll = function() {
        $("#mid-index-types").html("");
        $("#mid-index-list").html("");
        $("#mid-index-versions").html("");
    };

    // --------------------------------------------------------------------
    this.createSchemaTab = function() {
        var widget = new JsonDisplayWidget("#mid-indexname-locations", "#mid-indexname-results");
        widget.updateLabel = $("#mid-search-index-type");

        $("#mid-indexname-btn").button().click(function() {
            var schemaDao = new MappingDAO($("#mid-indexname-box").val(), widget);
            schemaDao.updateJsonFromSearch();
            $("#mid-search-index-name").val($("#mid-indexname-box").val());
        });
    };

    // --------------------------------------------------------------------
    this.createDebugTab = function() {
        this.debugWidget = new JsonDisplayWidget("#mid-search-locations", "#mid-search-results");
        var debugTab = this;
        $("#mid-radio-view").buttonset();

        $("#mid-build-search-btn").button().click(function() {
            var searchQuery = new SearchQuery();
            if ($("#mid-search-index-name").val().length > 0) {
                searchQuery.indexName = $("#mid-search-index-name").val();
                if ($("#mid-search-index-type").length > 0) {
                    searchQuery.indexType = $("#mid-search-index-type").val();
                }
            }

            if ($("#mid-search-fields").val().length > 0) {
                searchQuery.fields = $("#mid-search-fields").val();
            }

            if ($("#mid-search-size").val().length > 0) {
                searchQuery.size = $("#mid-search-size").val();
            }

            if ($("#mid-search-version").val().length > 1) {
                var versions = $("#mid-search-version").val().split(",");
                var versionQry = "(";
                versionQry += "splitterS3Version:" + versions[0];
                for (var i = 1; i < versions.length; i++) {
                    versionQry += " OR splitterS3Version:" + versions[i];
                }
                versionQry += ")";
                searchQuery.add(versionQry);
            }

            if ($("#mid-search-vip").val().length > 0) {
                searchQuery.add("vmsVipName:" + $("#mid-search-vip").val());
            }

            if ($("#mid-search-query").val().length > 0) {
                searchQuery.add($("#mid-search-query").val());
            }

            var queryURL = UrlMapper.prototype.getIndexHost() + searchQuery.toQueryString();
            $("#mid-get-search-url").val(queryURL);
        });

        $("#mid-run-search-btn").button().click(function() {
            $("#mid-search-locations").html("");
            $("#mid-search-results").html("");
            var parser = ResponseModelsFactory.prototype.getModel("IndexTypeResponseModel");
            var searchDao = new SearchDAO(parser, debugTab.debugWidget, true);
            var getMethodSearch = new GetJsonFromSearch($("#mid-get-search-url").val(), searchDao);
            getMethodSearch.updateJsonFromSearch();
        });

        $("#tree-view").click(function() {
            debugTab.debugWidget.treeView = true;
            debugTab.debugWidget.displayResult(null);
        });

        $("#text-view").click(function() {
            debugTab.debugWidget.treeView = false;
            debugTab.debugWidget.displayResult(null);
        });
    };

    // --------------------------------------------------------------------
    this.refresh = function() {
        this.createIndexesTab();
        this.createSchemaTab();
        this.createDebugTab();
    };

    this.initialize = function() {
        $.ajax({
            async : false,
            type : 'GET',
            url : '/REST/vms/elasticsearchadmin?query=nflx-env',
            success : function(data) {
                var nflxEnv = new String(data);
                if (nflxEnv == "test" || nflxEnv == "int" || nflxEnv == "prod") {
                    miner.nflxEnvironment = nflxEnv;
                    $("#id-env-txt").html("Env:<b>" + nflxEnv.toUpperCase() + "</b>");
                }
            }
        });

        $("#mid-hostname-apply-btn").button();
        miner.refresh();

        $.get("/REST/vms/elasticsearchadmin?query=elasticsearchhost", function(data) {
            if (data.length > 1)
                $("#mid-elasticsearchhost-box").val(data);
        });

        $("#mid-hostname-apply-btn").click(function() {
            miner.refresh();
        });
    };

}// MinerDashboard

