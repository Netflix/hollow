function ServerPublishTab(serverInfoView) {
    this.init = false;

    this.initialized = function() {
        return this.init;
    };

    this.refresh = function() {
        this.updatePublishFailTable();
        this.updateS3PublishTable();
        this.init = true;
    };

    this.clear = function() {
        $("#id-publish-failure-table").html("");
        $("#id-s3-publish-table").html("");
        this.init = false;
    };

    this.setPropertiesForQueryObject = function(query, indexType, num, sortBy) {
        query.indexName = serverInfoView.vmsIndex;
        query.indexType = indexType;
        query.size = num;
        query.sort = sortBy;
        query.add(serverInfoView.vmsCycleId); // ! "eventInfo.currentCycle:" due to async publish
    };

    this.updatePublishFailTable = function() {
        var tableWidget = new DataTableWidget("#id-publish-failure-table", "publish-failure-table", [ "cycleId", "partitionId", "keybase", "version",
                "timestamp" ]);
        tableWidget.emptyTableMessage = "(no s3 publish failures)";
        var widgetExecutor = new RegexSearchWidgetExecutor(tableWidget, RegexParserMapper.prototype.getBlobStatusRegexInfo());

        this.setPropertiesForQueryObject(widgetExecutor.searchQuery, "vmsserver", "50", "eventInfo.timestamp:desc");
        widgetExecutor.searchQuery.add("eventInfo.tag:BlobPlubishStatus").add("false");
        widgetExecutor.updateJsonFromSearch();
    };

    this.updateS3PublishTable = function() {
        // var tableWidget = new DataTableWidget("#id-s3-publish-table", "s3-publish-table", [ "cycleId", "keybase", "region", "version", "filesize(bytes)",
        //        "timestamp", "duration(ms)", "Mbps" ]);
        var tableWidget = new DataTableWidget("#id-s3-publish-table", "s3-publish-table", [ "keybase", "region", "filesize(bytes)",
                "timestamp", "duration(ms)", "Mbps" ]);
        var widgetExecutor = new RegexSearchWidgetExecutor(tableWidget, RegexParserMapper.prototype.getBlobPublishRegexInfo());

        this.setPropertiesForQueryObject(widgetExecutor.searchQuery, "vmsserver", "50", "eventInfo.timestamp:desc");
        widgetExecutor.searchQuery.add("\"dataVersion=" + serverInfoView.vmsCycleId + "\"");
        widgetExecutor.searchQuery.add("eventInfo.tag:PublishedBlob")
        widgetExecutor.updateJsonFromSearch();
    };
}
