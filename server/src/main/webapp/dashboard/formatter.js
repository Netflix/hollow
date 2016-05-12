
// Format JSON to display properly in HTML
// TODO specify formatters for a column

// Adds a space after prefix match, for text wrapping
function PrefixSpacerFormatter(spacestr) {
    this.spacestr = !spacestr? null : spacestr;
    var formatter = this;

    this.format = function(cellValue, row, col) {
        if(formatter.spacestr != null) { 
            var sstr = new String(formatter.spacestr);
            if (cellValue.indexOf(sstr) == 0) {
                cellValue = sstr + " " + cellValue.substring(sstr.length, cellValue.length);
                return cellValue;
            }
        }
        return cellValue;
    }
}


// finds starts of code block and envelopes within <pre> tag
function CodePreFormatter(spacestr, codeStartIndicator) {
    this.spacestr = !spacestr? null : spacestr;
    this.codeStartIndicator = !codeStartIndicator ? null : new String(codeStartIndicator);
    var formatter = this;

    this.format = function(cellValue, row, col) {
        if(formatter.spacestr != null) { 
            var sstr = new String(formatter.spacestr);
            if (cellValue.indexOf(sstr) == 0) {
                cellValue = sstr + " " + cellValue.substring(sstr.length, cellValue.length);
                return cellValue;
            }
        }

        if(formatter.codeStartIndicator != null) {
            var codeStart = cellValue.indexOf(formatter.codeStartIndicator);
            if(codeStart > 0) {
                return cellValue.substring(0, codeStart) + "<br>" + "<pre>" + cellValue.substring(codeStart,cellValue.length) + "</pre>"; 
            }
        }
        return cellValue;
    };
}


// changes exception layout to list
function JavaExceptionFormatter(spacestr) {
    this.spacestr = !spacestr? null : spacestr;
    var formatter = this;

    this.format = function(cellValue, row, col) {
        if(formatter.spacestr != null) { 
            var sstr = new String(formatter.spacestr);
            if (cellValue.indexOf(sstr) == 0) {
                cellValue = sstr + " " + cellValue.substring(sstr.length, cellValue.length);
                return cellValue;
            }
        }

        // var jsTreeId = this.tableId + '-' + row + '-' + col;
        var cellHtml = "";
        if (cellValue.indexOf("Exception") == -1 || cellValue.indexOf(".java:") == -1) {
            return cellValue;
        }

        var lastIndex = cellValue.indexOf("at ", prevIndex + 1);
        if (lastIndex != -1) {
            cellHtml += cellValue.substring(0, lastIndex);
        }

        var ul = "<ul style='list-style-type: circle;'>";
        cellHtml += ul;
        while (lastIndex != -1) {
            var prevIndex = lastIndex;
            lastIndex = cellValue.indexOf("at ", prevIndex + 1);
            var endIndex = lastIndex == -1 ? cellValue.length : lastIndex;
            cellHtml += "<li>";
            var exceptionLine = cellValue.substring(prevIndex, endIndex);
            var causedByIndex = exceptionLine.indexOf("Caused by");
            if (causedByIndex == -1) {
                cellHtml += exceptionLine;
                cellHtml += "</li>\n";
            } else {
                cellHtml += exceptionLine.substring(0, causedByIndex);
                cellHtml += "</li>\n";
                cellHtml += "</li></ul>" + exceptionLine.substring(causedByIndex, exceptionLine.length) + ul;
            }
        }

        cellHtml += "</ul>"; // </div>
        return cellHtml;
    }
}

