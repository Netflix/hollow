/**
 * Widgets are reusable components responsible for populating the html (view) of divs from a model
 * While the model can be arbitrality created, general use-case is for models to be generated from
 * search queries in VMSIops
 */

function Widgets() {
}
Widgets.counter = 0;

// --------------------------------------------------------------------
// WidgetExecutors
// --------------------------------------------------------------------
function SearchWidgetExecutor(widget, model) {
    this.widget = widget;
    this.searchQuery = new SearchQuery();
    this.responseModel = !model ? ResponseModelsFactory.prototype.getModel("IndexTypeResponseModel") : model;

    this.updateJsonFromSearch = function() {
        var searchDao = new SearchDAO(this.responseModel, this.widget, true);
        var queryURL = UrlMapper.prototype.getIndexHost() + this.searchQuery.toQueryString();
        var getSearchMethod = new GetJsonFromSearch(queryURL, searchDao);

        getSearchMethod.updateJsonFromSearch();
    };
}

function KeyValueWidgetExecutor(widget, keypath, keysOnly) {
    this.widget = widget;
    this.keyPath = keypath;
    this.keysOnly = keysOnly;
    this.searchQuery = new SearchQuery();

    this.updateJsonFromSearch = function() {
        var searchDao = new KeyValueSearchDAO(this.widget, this.keyPath, true, this.keysOnly);
        var queryURL = UrlMapper.prototype.getIndexHost() + this.searchQuery.toQueryString();
        var getSearchMethod = new GetJsonFromSearch(queryURL, searchDao);
        getSearchMethod.updateJsonFromSearch();
    };
}

function RegexSearchWidgetExecutor(widget, regexMap) {
    this.widget = widget;
    this.regexMap = regexMap;
    this.searchQuery = new SearchQuery();
    this.regexSourceModel = ResponseModelsFactory.prototype.getModel("RegexModel", {
        sourceField : "message",
        fieldsRegex : this.regexMap
    });

    this.updateJsonFromSearch = function() {
        var searchDao = new SearchDAO(this.regexSourceModel, this.widget, true);
        var queryURL = UrlMapper.prototype.getIndexHost() + this.searchQuery.toQueryString();
        var getSearchMethod = new GetJsonFromSearch(queryURL, searchDao);

        getSearchMethod.updateJsonFromSearch();
    };
}

// --------------------------------------------------------------------
// DataTableWidget
// --------------------------------------------------------------------
function DataTableWidget(divId, tableId, fields) {
    this.divId = divId;
    this.tableId = tableId;
    this.fieldNames = fields;
    this.html = "";
    this.toDataTable = true;
    this.clearPrevious = true;
    this.jsTreeArray = new Array();
    this.reformatCellDataFunc = null;
    this.emptyTableMessage = null; // show message, instead of empty table
    
    // provide a custom function for more elaborate reformating, when you need it, e.g.
    // datatableWidget.reformatCellDataFunc = function(cellValue, row, col) {
    //    return cellValue.replace(/(?:\r\n|\r|\n)/g, '<br />');
    // }

    this.getType = function() {
        return "DataTableWidget";
    };

    this.clear = function() {
        $(this.divId).html("no results"); // <img src='images/spinner.gif'>
        this.html = "";
        this.jsTreeArray = new Array();
    };

    this.applyParserData = function(model) {
        console.log("fields = " + this.fieldNames.toString());
        if (this.clearPrevious) {
            this.clear();
        }
        this.buildTable(model);
    };

    this.buildTable = function(tableDataModel) {
        var numRows = 0;
        if (tableDataModel != null) {
            numRows = tableDataModel.length;
        }
        if(numRows == 0 && this.emptyTableMessage != null) {
            this.html = this.emptyTableMessage;
            return;
        }

        this.html = "<table id='" + this.tableId + "' class='prettytable'><thead><tr>";
        for ( var i in this.fieldNames) {
            this.html += "<th>" + this.fieldNames[i] + "</th>";
        }
        this.html += "</tr></thead><tbody>\n";

        for (var row = 0; row < numRows; row++) {
            this.html += "<tr>";
            tableRow = tableDataModel[row];
            // JSON.stringify(tableRow));
            for (var col = 0; col < this.fieldNames.length; col++) {
                var cellValue = "";
                if (tableRow.hasOwnProperty(this.fieldNames[col])) {
                    cellValue = new String(tableRow[this.fieldNames[col]]);
                    var cellHtml = this.reformatCellDataFunc == null ? cellValue : this.reformatCellDataFunc(cellValue, row, col);
                    this.html += "<td>" + cellHtml + "</td>";
                }
            }
            this.html += "</tr>\n";
        }
        this.html += "</tbody></table>";
    };

    this.refresh = function() {
        // console.log(this.html);
        $(this.divId).html(this.html);

        if (this.toDataTable) {
            $("#" + this.tableId).dataTable({
                "bJQueryUI" : true,
                "iDisplayLength" : 25,
                "aLengthMenu" : [ 10, 25, 50, 100, 500 ],
                "sDom" : '<"H"lifp><t>',
                "scrollX": true,
                "sPaginationType" : "full_numbers"
            });
        }
    };
}

function ClickableTableWidget(divId, tableId, fields, titles, clickableColumn, clickEventFunction, rowIndicatorFunction) {
    this.divId = divId;
    this.tableId = tableId;
    this.fieldNames = fields;
    this.titleNames = titles;
    this.clickableColumn = clickableColumn;
    this.html = "";
    this.clearPrevious = false;
    this.showHeader = true;
    this.clickEventFunc = !clickEventFunction ? null : clickEventFunction;
    this.rowIndicatorFunc = !rowIndicatorFunction ? null : rowIndicatorFunction;
    this.onRowClick = null;
    this.endBuildTableFunc = null;
    this.textAlign = null;
    var refFn = this;

    this.getType = function() {
        return "ClickableTableWidget";
    };

    this.clear = function() {
        $(this.divId).html("");
        this.html = "";
    };

    this.applyParserData = function(model) {
        if (this.clearPrevious) {
            this.clear();
        }
        this.buildTable(model);
        if(this.endBuildTableFunc != null) {
            this.endBuildTableFunc();
        }
    };

    this.clearHighlight = function() {
        var tableCellId = "#" + this.tableId + " td";
        $(tableCellId).removeClass("highlightcell");
        var tableRowId = "#" + this.tableId + " tr";
        $(tableRowId).removeClass("highlightrow");
    };

    // find text in clickable column and highlight it
    this.updateHighlight = function(val) {
        refFn.clearHighlight();
        var tableRowId = "#" + this.tableId + " tr";
        $(tableRowId).each(function() {
            if ($(this).find('td').eq(refFn.clickableColumn).text() == val) {
                $(this).addClass("highlightrow");
                return;
            }
        });
    };

    this.buildTable = function(tableDataModel) {
        var numRows = 0;
        if (tableDataModel != null) {
            numRows = tableDataModel.length;
        }

        this.html = "<table id='" + this.tableId + "' class='clickabletable'>";
        if(refFn.showHeader) {
            this.html += "<thead><tr>";
            for ( var i in this.titleNames) {
                this.html += "<th>" + this.titleNames[i] + "</th>";
            }
            this.html += "</tr></thead>";
        }

        this.html += "<tbody>\n";

        for (var row = 0; row < numRows; row++) {
            tableRow = tableDataModel[row];

            var customAdditions = null;
            if (!this.rowIndicatorFunc) {
                this.html += "<tr>";
            } else {
                customAdditions = this.rowIndicatorFunc(tableRow, row, numRows);
                this.html += customAdditions.trow;
            }

            for (var col = 0; col < this.fieldNames.length; col++) {
                if (this.fieldNames[col] == "custom") {
                    continue; // will be filled in rowIndicatorFunc
                }
                var cellValue = "";
                if (tableRow.hasOwnProperty(this.fieldNames[col])) {
                    cellValue = tableRow[this.fieldNames[col]];
                }
                this.html += "<td ";

                if (col == this.clickableColumn) {
                    this.html += "style='cursor: pointer'";
                } else {
                    if(refFn.textAlign) {
                        this.html += refFn.textAlign;
                    }
                }
                this.html += ">" + cellValue + "</td>";
            }
            if (customAdditions != null) {
                this.html += customAdditions.tcols;
            }
            this.html += "</tr>\n";
        }
        this.html += "</tbody></table>";
        $(this.divId).html(this.html);

        var ref = this;
        var tableRowId = "#" + this.tableId + " tr";
        $(tableRowId).click(function(e) {
            $(tableRowId).removeClass("highlightrow");
            $(this).addClass("highlightrow");
            if(ref.onRowClick != null) {
                ref.onRowClick(tableDataModel[$(this).index()], $(tableRowId));
            }
        });

        var tableCellId = "#" + this.tableId + " td";

        $(tableCellId).click(function(e) {
            var selectedLabel = $(this).text();
            var column_num = parseInt($(this).index());
            if (ref.clickableColumn == column_num) {
                $(tableCellId).removeClass("highlightcell");
                $(this).addClass("highlightcell");
                if(ref.clickEventFunc != null) {
                    ref.clickEventFunc(selectedLabel);
                }
            }
            // e.stopPropagation();
        });

    };

    this.refresh = function() {
        // will loose click event if toDatatable
    };
}

// --------------------------------------------------------------------
// JsonDisplayWidget
// --------------------------------------------------------------------
function JsonDisplayWidget(divtable, divname) {
    this.results = new Object();
    this.jsonData = null;
    this.logSearchData = null;
    this.divTable = divtable;
    this.divName = divname;
    this.activeLabel = null;
    this.updateLabel = null;
    this.displayWidget = null;
    this.treeView = true;
    this.html = "";
    this.backgroundColorSetterFunc = null;
    this.tableId = "search-result-labels-" + Widgets.counter;
    Widgets.counter += 1;

    this.getType = function() {
        return "JsonDisplayWidget";
    };

    this.clear = function() {
        $(this.divTable).html("");
        $(this.divName).text("");
        this.html = "";
        if(this.displayWidget != null) {
            this.displayWidget.clear();
        }
    };

    this.applyParserData = function(model) {

        this.results = model;
        var keys = Object.keys(model);
        keys.sort();
        var table = "<table id=" + this.tableId + " cellpadding='5'>";

        for (var i = 0; i < keys.length; i++) {
            var key = keys[i];
            var bgcolor = "#FFFFFF";
            if(this.backgroundColorSetterFunc != null) {
                bgcolor = this.backgroundColorSetterFunc(key);
            }
            table += "<tr style='background-color:" + bgcolor + "'>";
            // table += "<td class='ui-widget-content'><p style='cursor: pointer'>" + key + "</p></td></tr>";
            table += "<td class='clickablecell'>" + key + "</td></tr>";
        }
        table += "</table>";
        $(this.divTable).html(table);
        $(this.divName).text("");

        this.html = table;
        var ref = this;
        var tableCellId = "#" + this.tableId + " td";

        $(tableCellId).click(function(e) {
            var selectedLabel = $(this).text();
            $(tableCellId).removeClass("highlightrow"); //ui-widget-header
            $(this).addClass("highlightrow");
            ref.displayResult(selectedLabel);
            e.stopPropagation();
        });
    };

    this.displayResult = function(label) {
        if (label == null && this.activeLabel == null) {
            alert("label == null && activeLabel == null");
            return;
        }
        if (label == null) {
            label = this.activeLabel;
        }
        if (!this.results.hasOwnProperty(label)) {
            alert("unkown property " + label);
            return;
        }

        this.activeLabel = label;
        if (this.results[label] == null) {
            return;
        }

        if (this.displayWidget == null) {
            if (this.treeView == true) {
                $(this.divName).JSONView({
                    data : this.results[label]
                });
            } else {
                $(this.divName).text(JSON.stringify(this.results[label]));
            }
        } else {
            this.displayWidget.applyParserData(this.results[label]);
            this.displayWidget.refresh();
        }

        if (this.updateLabel) {
            this.updateLabel.val(label);
        }
    };

    this.refresh = function() {
        // TODO kills event
        // $(this.divTable).html(this.html);
        // $(this.divName).text("");
    };
}

// --------------------------------------------------------------------
// JsonViewWidget
// --------------------------------------------------------------------
function JsonViewWidget(divname, useTextView) {
    this.divName = divname;
    this.dataModel = null;
    this.useTextView = useTextView;

    this.clear = function() {
        $(this.divId).html("");
    };

    this.applyParserData = function(model) {
        $(this.divName).html("");
        this.dataModel = model;
        this.refresh();
    };

    this.refresh = function() {
        if (!this.useTextView) {
            $(this.divName).JSONView({
                data : this.dataModel
            });
        } else {
            $(this.divName).text(JSON.stringify(this.dataModel));
        }
    };
}

//--------------------------------------------------------------------
//PlainTextViewWidget
//--------------------------------------------------------------------
function PlainTextViewWidget(divname, field) {
 this.divName = divname;
 this.dataModel = null;
 this.fieldName = field;

 this.clear = function() {
     $(this.divId).html("");
 };

 // same model as table:  rows of key-value maps
 this.applyParserData = function(model) {
     $(this.divName).html("");
     this.dataModel = model[0];
     this.refresh();
 };

 this.refresh = function() {
     $(this.divName).text(this.dataModel[this.fieldName]);
 };

}

// --------------------------------------------------------------------
// EventChainingWidget is not a widget, but the "refresh" is a proxy for
// invoking a function
// --------------------------------------------------------------------
function EventChainingWidget(functionToInvoke) {
    this.functionToInvoke = functionToInvoke;

    this.clear = function() {
    };

    this.applyParserData = function(model) {
    };

    this.refresh = function() {
        this.functionToInvoke();
    };
}


// --------------------------------------------------------------------
// CallbackWidget is not a widget, but the "refresh" is a proxy for
// invoking a function
// --------------------------------------------------------------------
function CallbackWidget(functionToInvoke) {
    this.functionToInvoke = functionToInvoke;
    this.modelData = null;
    var refFn = this;

    this.clear = function() {
    };

    this.applyParserData = function(model) {
        refFn.modelData = model;
    };

    this.refresh = function() {
        this.functionToInvoke(refFn.modelData);
    };

}



// --------------------------------------------------------------------
// SelectOptionsWidget
// selectId is from html
// if field is specified, use that one from dataModel
// else, create an empty entry in drop_down and use every model row
// --------------------------------------------------------------------
function SelectOptionsWidget(selectId, field, formatFunction, dochange, defaultoption) {
    this.divId = selectId;
    this.html = "";
    this.fieldName = !field ? null : field;
    this.formatFunc = !formatFunction ? null : formatFunction;
    this.defaultOption = !defaultoption ? null : defaultoption;
    this.doChange = !dochange ? false : dochange;
    this.doSort = false;

    this.getType = function() {
        return "SelectOptionsWidget";
    };

    this.clear = function() {
        $(this.divId).html("");
        this.html = "";
    };

    this.applyParserData = function(model) {
        this.buildSelect(model);
        this.refresh();
        if (this.doChange) {
            $(selectId).val(this.defaultOption).change();
        }
    };

    this.buildSelect = function(tableDataModel) {
        var numRows = 0;
        if (tableDataModel != null) {
            numRows = tableDataModel.length;
        }

        if(this.doSort) {
            tableDataModel.sort();
        }

        this.html = "<select id='" + this.divId + "' class='ui-widget-content'>";
        // this.html += "<option></option>";

        for (var row = 0; row < numRows; row++) {
            var tableRow = tableDataModel[row];
            var optVal = !this.fieldName ? tableRow : tableRow[this.fieldName];
            var optName = !this.formatFunc ? optVal : this.formatFunc(optVal);

            if (row == 0 && this.defaultOption == null) {
                this.defaultOption = optVal;
            }
            this.html += "<option value='";
            this.html += optVal;
            this.html += "'>";
            this.html += optName;
            this.html += "</option>\n";
        }
        this.html += "</select>\n";
    };

    this.refresh = function() {
        $(this.divId).html(this.html);
    };
}


// Wrapper for JQuery progress-bar
function ProgressBarWidget(selectId, labelId) {
    this.divId = selectId;
    this.doSort = false;
    this.value = 0;
    this.fieldName = "percent";

    var progressbar = $( this.divId ),
    progressLabel = $( labelId );
 
    progressbar.progressbar({
      value: false,
      change: function() {
        progressLabel.text( progressbar.progressbar( "value" ) + "%" );
      },
      complete: function() {
        progressLabel.text( "Complete" );
      }
    });

    this.getType = function() {
        return "ProgressBarWidget";
    };

    this.clear = function() {
        progressbar.progressbar( "value", 0);
    };

    this.applyParserData = function(dataModel) {
        if(dataModel == null) {
            progressbar.progressbar( "value", 0);
            return;
        }
        var refWidget = this;
        var obj = dataModel[0];
        refWidget.value = parseInt(obj[refWidget.fieldName]);
        progressbar.progressbar( "value", refWidget.value);
    };

    this.refresh = function() {
    };
}

// --------------------------------------------------------------------
// IFrameWidget
// --------------------------------------------------------------------
function IFrameWidget(parentDivId, iframeId, hostname, link) {
    this.init = false;
    this.iframeId = iframeId;
    this.hostName = hostname;
    this.srcLink = link;

    this.refresh = function() {
        $("#" + this.iframeId).attr("src", this.hostName + this.srcLink);
        console.log("iframe: " + this.hostName + this.srcLink);
    };

    this.setNewEnv = function(newval) {
        var toreplace = newval == "prod" ? "test" : "prod";
        this.hostName = this.hostName.replace(toreplace, newval);
        this.refresh();
    };

    this.initialize = function(widthVal, heightVal) {
        if (this.init) {
            return;
        }

        this.init = true;
        var iframeWidget = this;
        var width = !widthVal ? "100%" : widthVal;
        var height = !heightVal ? "100%" : heightVal;

        // create toolbar to select env, and reload btn
        var toolbarId = iframeWidget.iframeId + "-toolbar";
        $('<div/>', {
            id : toolbarId,
            class : "ui-button iframe-toolbar"
        }).appendTo(parentDivId);

        // add reload button to toolbar
        var reloadBtnId = iframeWidget.iframeId + "-reload-btn";
        $('<button/>', {
            id : reloadBtnId,
            class : "iframe-refresh-button"
        }).appendTo("#" + toolbarId);

        $("#" + reloadBtnId).button({
            label : "Refresh iframe",
            icons : {
                primary : "ui-icon-refresh"
            }
        }).click(function() {
            iframeWidget.refresh();
        });

        // create iframe
        $('<iframe/>', {
            src : link,
            // id : iframeWidget.iframeId,
            id : iframeWidget.iframeId,
            frameborder : 0,
            width : width,
            height : height,
            scrolling : "auto"
        }).appendTo("#" + toolbarId);

        // reload
        iframeWidget.refresh();
    };

}
// --------------------------------------------------------------------
// Currently uses flot, maybe replaced by jqPlot
// @param divId Id of the div in HTML/JSP
// --------------------------------------------------------------------
function TimeSeriesGraphWidget(divId, y2, y1label, y2label) {
    this.datasets = new Object();
    this.dataNames = new Array();
    this.y2 = y2;
    this.divId = divId;
    this.unity1 = "";
    this.unity2 = "";
    this.y1label = y1label;
    this.y2label = y2label;
    this.tickSize = null;
    this.capacity = 0;
    this.computeDelta = false;
    this.deltaFrom = null; // active = started - finished
    this.deltaMinus = null;
    this.deltaName = null;
    this.minTickSize = 1;
    this.numTicks = 5;
    this.fill = false;

    this.getType = function() {
        return "TimeSeriesGraphWidget";
    };

    this.setDataCapacity = function(size) {
        this.capacity = -1 * size;
        return this;
    };

    this.plotDelta = function(deltafrom, deltaminus, deltaname) {
        this.computeDelta = true;
        this.deltaFrom = deltafrom;
        this.deltaMinus = deltaminus;
        this.deltaName = deltaname;
        return this;
    };

    this.applyParserData = function(model) {
        // avoid duplicates
        if (this.dataNames.indexOf(model.name) == -1) {
            this.addDataSet(model.timeSeries, model.name);
        }
    };

    this.addDataSet = function(dataset, name) {
        this.datasets[name] = dataset;
        if (this.capacity != 0) {
            this.datasets[name] = dataset.slice(this.capacity, -1); // -ve from the end
        }
        this.dataNames.push(name);
    };

    this.refresh = function() {
        // console.log("divID = " + this.divId);
        // console.log("datasets size = " + this.datasets.length);
        // console.log("datasets = " + this.datasets);
        // console.log("names = " + this.dataNames);

        if (this.dataNames.length > 0) {
        } else {
            return;
        }

        var unity1 = this.unity1;
        var unity2 = this.unity2;
        this.dataNames.sort(); // don't want axis order changing

        if (this.computeDelta == true) {
            var deltaArray = new Array();
            var series1 = this.datasets[this.deltaFrom];
            var series2 = this.datasets[this.deltaMinus];
            if (series1 === undefined || series2 === undefined) {
                console.log("delta from,minus not found");
            } else {
                for (var i = 0; i < series1.length; i++) {
                    var series1Obj = series1[i];
                    var series2Obj = series2[i];
                    deltaArray.push([ series1Obj[0], series1Obj[1] - series2Obj[1] ]); // push([timestamp,
                    // value])
                }
                this.datasets[this.deltaName] = deltaArray;
                // this.dataNames = new Array();
                // this.dataNames.push(this.deltaName);
                this.dataNames.unshift(this.deltaName);
            }
        }

        var dataSeries = [];
        for (var i = 0; i < this.dataNames.length; i++) {

            var name = this.dataNames[i];

            if (i == 0 || this.y2 != 1) {
                dataSeries.push({
                    label : name,
                    data : this.datasets[name]
                });
            } else {
                if (this.y2 == 1) {
                    dataSeries.push({
                        label : name,
                        data : this.datasets[name],
                        yaxis : 2
                    });
                }
            }
        }

        $.plot($(this.divId), dataSeries, {
            lines : {
                fill: this.fill
            },
            legend: {
                position: "nw"
            },
            xaxis : {
                mode : "time",
                ticks : this.numTicks
            },
            yaxis : {
                min : 0,
                position : "left",
                tickFormatter : function(v, axis) {
                    return Math.floor(v) + unity1;
                }, // return Math.ceil(v) + unity1
                tickSize : this.tickSize,
                minTickSize : this.minTickSize
            },
            y2axis : {
                min : 0,
                position : y2 == 1 ? "right" : "left",
                alignTicksWithAxis : y2,
                tickFormatter : function(v, axis) {
                    return Math.floor(v) + unity2;
                },
                tickSize : this.tickSize,
                minTickSize : this.minTickSize
            }
        });
        xaxisLabel = $("<div class='axisLabel xaxisLabel'></div>").text("time").appendTo($(this.divId));

        yaxisLabel = $("<div class='axisLabel yaxisLabel'></div>").text(this.y1label).appendTo($(this.divId));

        y2axisLabel = $("<div class='axisLabel y2axisLabel'></div>").text(this.y2label).appendTo($(this.divId));

        // clear old arrays, and get them from TimeSeries next time
        this.datasets = new Object();
        this.dataNames = new Array();
    };
}


// helper fake widget
function HelperValidationWidget(callback, indexName, indexType, currentCycle, queries, fields, sorts) {
    var widget = this;
    function Model() {
        var data = [];

        this.addHitInfo = function(jsonData) {
            if (fields) {
                data.push(jsonData.fields);
            } else {
                data.push(jsonData._source.inputData);
            }

        };

        this.getDataModel = function() {
            return data;
        };
    }

    this.clear = function() {
        callback([]);
    };

    function getSearchQuery(queries) {
        var searchQuery = new SearchQuery();
        searchQuery.indexName = indexName;
        searchQuery.indexType = indexType;
        searchQuery.add("currentCycle:" + currentCycle);
        searchQuery.fields = fields;
        searchQuery.size = 1000;
        searchQuery.sort= sorts;
        if (queries != null && queries.length > 0) {
            $.each(queries, function(i, query) {
                searchQuery.add(query);
            });
        }
        return searchQuery;
    }

    this.applyParserData = function(model) {
        callback(model);
    };

    this.refresh = function() {
    };

    // Initialization
    var model = new Model();
    var searchQuery = getSearchQuery(queries);
    var searchDao = new SearchDAO(model, this, true);

    var queryURL = UrlMapper.prototype.getIndexHost() + searchQuery.toQueryString();
    var getSearchMethod = new GetJsonFromSearch(queryURL, searchDao);
    getSearchMethod.updateJsonFromSearch();
}

function InputValidationWidget(mapsId, resultsId, mapsFilterId, errorFilterId, indexName, currentCycle, clickMap, clickRule) {
    var widget = this;

    function udpateValidationErrors(data) {
        $(resultsId).html("");
        for (inputDataIndex in data) {
            var inputData = data[inputDataIndex];
            var keysDiv = $("<div/>").append(
                    $("<div/>", {
                        class : "ui-widget-header",
                        text : "Rule:" + inputData.ruleId + ", Namespace:" + inputData.namespace + ", Level:" + inputData.level + ", Error:"
                                + inputData.error + ", Count:" + inputData.count
                    })).append($("<div/>", {
                class : "ui-widget-content",
                style : "word-wrap: break-word;",
                text : JSON.stringify(inputData.keys)
            })).appendTo(resultsId);
        }
    }

    function invokeUdpateMapValidationErrors(map) {
        var mapsHelper = new HelperValidationWidget(udpateValidationErrors, indexName, "Validator", currentCycle, [ "map:" + map ]);
    }

    function invokeUdpateRuleValidationErrors(map, ruleId) {
        var rulesHelper = new HelperValidationWidget(udpateValidationErrors, indexName, "Validator", currentCycle, [ "map:" + map, "ruleId:" + ruleId ]);
    }

    function compare(a, b) {
        if (a < b)
            return -1;
        if (a > b)
            return 1;
        return 0;
    }

    function updateMaps(data) {
        $(mapsId).html("");
        $(resultsId).html("");
        if (data == null || data.length == 0) {
            return;
        }

        var maps = {};
        $.each(data, function(i, inputData) {
            var map = inputData["inputData.map"][0];
            var ruleId = inputData["inputData.ruleId"][0];
            var error = inputData["inputData.error"][0];
            var level = inputData["inputData.level"][0];
            var count = inputData["inputData.count"][0];
            var namespace = inputData["inputData.namespace"][0];
            if (!(map in maps)) {
                maps[map] = [];
            }
            maps[map].push({
                ruleId : ruleId,
                error : error,
                level : level,
                count : count,
                namespace : namespace
            });
        });

        var mapsFilter = $(mapsFilterId).val();
        mapsFilter = (!mapsFilter.trim()) ? null : mapsFilter.trim().toLowerCase();

        var errorFilter = $(errorFilterId).val();
        errorFilter = (errorFilter == "all") ? null : errorFilter;

        $.each(maps, function(map) {
            if (mapsFilter && map.toLowerCase().indexOf(mapsFilter) < 0) {
                return;
            }

            var mapDiv = $("<div/>").append($("<div/>", {
                class : "ui-widget-header",
                text : map,
                style : "cursor:pointer;",
                click : function() {
                    invokeUdpateMapValidationErrors(map);
                    clickMap(map);
                }
            })).appendTo(mapsId);

            var rules = maps[map];
            $.each(rules, function(i, rule) {
                if (errorFilter && errorFilter != rule.level) {
                    return;
                }
                mapDiv.append($("<div/>", {
                    html : "&nbsp;&nbsp;&nbsp;&nbsp;" + rule.ruleId + " (" + rule.namespace + ", " + rule.count + ", " + rule.level + ")",
                    class : "ui-widget-content",
                    style : "cursor:pointer;" + (rule.level == "Error" ? "background: #F5A9A9 url() no-repeat right top;" : ""),
                    click : function() {
                        invokeUdpateRuleValidationErrors(map, rule.ruleId);
                        clickRule(map, rule.ruleId);
                    },
                }));
            });
        });
    }

    $(mapsFilterId).val("");
    $(mapsFilterId).off("keypress");
    $(mapsFilterId).on(
            "keypress",
            function() {
                var mapsHelper = new HelperValidationWidget(updateMaps, indexName, "Validator", currentCycle, null,
                        "inputData.ruleId,inputData.map,inputData.error,inputData.level,inputData.count,inputData.namespace");
            });
    $(errorFilterId).val("");
    $(errorFilterId).off("change", "**");
    $(errorFilterId).on(
            "change",
            function() {
                var mapsHelper = new HelperValidationWidget(updateMaps, indexName, "Validator", currentCycle, null,
                        "inputData.ruleId,inputData.map,inputData.error,inputData.level,inputData.count,inputData.namespace");
            });
    var mapsHelper = new HelperValidationWidget(updateMaps, indexName, "Validator", currentCycle, null,
            "inputData.ruleId,inputData.map,inputData.error,inputData.level,inputData.count,inputData.namespace", "inputData.map");
}
//
