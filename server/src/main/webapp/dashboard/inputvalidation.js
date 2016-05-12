function InputValidationTab(dashboard) {
    // ------------------------------------------------------------
    var editor;

    this.clear = function() {
        $("#cycle-input-validation-results").html("");
    };

    function clickMap(map) {
        var cursor = editor.getSearchCursor(map + "\":", 0);
        if (!cursor.find(false))
            return;
        editor.setSelection(cursor.from(), cursor.to());
        editor.scrollIntoView({
            from : cursor.from(),
            to : cursor.to()
        });
    }

    function clickRule(map, ruleId) {
        var cursor = editor.getSearchCursor(map, 0);
        if (!cursor.find(false))
            return;
        editor.setSelection(cursor.from(), cursor.to());
        editor.scrollIntoView({
            from : cursor.from(),
            to : cursor.to()
        });
    }

    var inputValidationWidget = new InputValidationWidget("#cycle-input-validation-widget", "#cycle-input-validation-results",
            "#cycle-input-validation-map-filter", "#cycle-input-validation-error-filter", dashboard.vmsIndex, dashboard.vmsCycleId, clickMap, clickRule);

    $("#cycle-input-validation-show-specification-dialog").dialog({
        autoOpen : false,
        height : 700,
        width : 1000,
        modal : true
    });

    $("#cycle-input-validation-specification-source-div").html("");
    $('<textarea id="cycle-input-validation-specification-source"></textarea>').appendTo("#cycle-input-validation-specification-source-div");

    function showInputValidationSpecification(data) {
        $("#cycle-input-validation-specification-source").val("");
        if (data == null || data.length == 0) {
            return;
        }
        $("#cycle-input-validation-specification-source").val(data[0].specification);
        editor = CodeMirror.fromTextArea(document.getElementById("cycle-input-validation-specification-source"), {
            mode : "groovy",
            lineNumbers : true,
            readOnly : true
        });
        editor.setSize(800, 800);
    }
    var helper = new HelperValidationWidget(showInputValidationSpecification, dashboard.vmsIndex, "ValidatorSpecification", dashboard.vmsCycleId, null);

    $("#cycle-input-validation-show-specification").off("click");
    $("#cycle-input-validation-show-specification").button().click(function() {
        if ($("#cycle-input-validation-specification-source-div").is(':visible')) {
            $("#cycle-input-validation-specification-source-div").hide("fast");
        } else {
            $("#cycle-input-validation-specification-source-div").show("fast");
        }
    });

    $("#cycle-input-validation-search").off("click");
    $("#cycle-input-validation-search").button().click(function() {
        $("#cycle-input-validation-search-dialog").dialog("open");
    });

    $("#cycle-input-validation-example").off("click");
    $("#cycle-input-validation-example").button().click(function() {
        var cursor = editor.getSearchCursor("BEEHIVE.VideoDate.json", 0);
        if (!cursor.find(false))
            return;
        editor.setSelection(cursor.from(), cursor.to());
        editor.scrollIntoView({
            from : cursor.from(),
            to : cursor.to()
        });
    });

    function showSearchResults(key, data) {
        $("#cycle-input-validation-results").html("");
        if (data == null || data.length == 0) {
            return;
        }

        $("<div/>", {
            text : "Results for key:" + key
        }).appendTo("#cycle-input-validation-results");

        $.each(data, function(i, inputData) {
            var map = inputData["inputData.map"][0];
            var ruleId = inputData["inputData.ruleId"][0];
            var error = inputData["inputData.error"][0];
            var level = inputData["inputData.level"][0];
            var count = inputData["inputData.count"][0];

            $("<div/>", {
                text : "Map:" + map + ", Rule:" + ruleId + ", Level:" + level + ", Error:" + error,
                class : "ui-widget-content",
                style : (level == "Error" ? "background: #F5A9A9 url() no-repeat right top;" : "")
            }).appendTo("#cycle-input-validation-results");

        });
    }

    $("#cycle-input-validation-search-dialog").dialog(
            {
                autoOpen : false,
                height : 150,
                width : 300,
                modal : true,
                buttons : {
                    "Search" : function() {
                        var key = $("#cycle-input-validation-search-dialog-key").val();
                        var helper = new HelperValidationWidget(function(data) {
                            showSearchResults(key, data);
                        }, dashboard.vmsIndex, "Validator", dashboard.vmsCycleId, [ "keys:" + key ],
                                "inputData.ruleId,inputData.map,inputData.error,inputData.level,inputData.count", "map");
                        $(this).dialog("close");
                    },
                    Cancel : function() {
                        $(this).dialog("close");
                    }
                },
                close : function() {
                }
            });

}
