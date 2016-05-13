(function($) {
  // JSONFormatter json->HTML prototype straight from Firefox JSONView
  // For reference: http://code.google.com/p/jsonview
  function JSONFormatter() {
    // No magic required.
  }

  JSONFormatter.prototype = {
    htmlEncode: function (t) {
      return t != null ? t.toString().replace(/&/g,"&amp;").replace(/"/g,"&quot;").replace(/</g,"&lt;").replace(/>/g,"&gt;") : '';
    },

    decorateWithSpan: function (value, className) {
      return '<span class="' + className + '">' + this.htmlEncode(value) + '</span>';
    },

    // Convert a basic JSON datatype (number, string, boolean, null, object, array) into an HTML fragment.
    valueToHTML: function(value) {
      var valueType = typeof value;

      var output = "";
      if (value == null) {
        output += this.decorateWithSpan('null', 'null');
      }
      else if (value && value.constructor == Array) {
        output += this.arrayToHTML(value);
      }
      else if (valueType == 'object') {
        output += this.objectToHTML(value);
      }
      else if (valueType == 'number') {
        output += this.decorateWithSpan(value, 'num');
      }
      else if (valueType == 'string') {
        if (/^(http|https):\/\/[^\s]+$/.test(value)) {
          output += '<a href="' + value + '">' + this.htmlEncode(value) + '</a>';
        } else {
          output += this.decorateWithSpan('"' + value + '"', 'string');
        }
      }
      else if (valueType == 'boolean') {
        output += this.decorateWithSpan(value, 'bool');
      }

      return output;
    },

    // Convert an array into an HTML fragment
    arrayToHTML: function(json) {
      var output = '[<ul class="array collapsible">';
      var hasContents = false;
      for ( var prop in json ) {
        hasContents = true;
        output += '<li>';
        output += this.valueToHTML(json[prop]);
        output += '</li>';
      }
      output += '</ul>]';

      if ( ! hasContents ) {
        output = "[ ]";
      }

      return output;
    },

    // Convert a JSON object to an HTML fragment
    objectToHTML: function(json) {
      var output = '{<ul class="obj collapsible">';
      var hasContents = false;
      for ( var prop in json ) {
        hasContents = true;
        output += '<li>';
        output += '<span class="prop">' + this.htmlEncode(prop) + '</span>: ';
        output += this.valueToHTML(json[prop]);
        output += '</li>';
      }
      output += '</ul>}';

      if ( ! hasContents ) {
        output = "{ }";
      }

      return output;
    },

    // Convert a whole JSON object into a formatted HTML document.
    jsonToHTML: function(json) {
      var output = '';
      output += '<div id="jsonview">';
      output += this.valueToHTML(json);
      output += '</div>';
      return output;
    },

    // Produce an error document for when parsing fails.
    errorPage: function(error, data, uri) {
      // var output = '<div id="error">' + this.stringbundle.GetStringFromName('errorParsing') + '</div>';
      // output += '<h1>' + this.stringbundle.GetStringFromName('docContents') + ':</h1>';
      var output = '<div id="error">Error parsing JSON: '+error.message+'</div>';
      output += '<h1>'+error.stack+':</h1>';
      output += '<div id="jsonview">' + this.htmlEncode(data) + '</div>';
      return this.toHTML(output, uri + ' - Error');
    }
  };

  $.fn.JSONView = function(jsonObj) {

    function collapse(evt) {
      var collapser = evt.target;

      var target = collapser.parentNode.getElementsByClassName('collapsible');

      if ( ! target.length ) {
        return;
      }

      target = target[0];

      if ( target.style.display == 'none' ) {
        var ellipsis = target.parentNode.getElementsByClassName('ellipsis')[0];
        target.parentNode.removeChild(ellipsis);
        target.style.display = '';
        collapser.innerHTML = '-';
      } else {
        target.style.display = 'none';

        var ellipsis = document.createElement('span');
        ellipsis.className = 'ellipsis';
        ellipsis.innerHTML = ' &hellip; ';
        target.parentNode.insertBefore(ellipsis, target);
        collapser.innerHTML = '+';
      }
    }

    function addCollapser(item) {
      // This mainly filters out the root object (which shouldn't be collapsible)
      if ( item.nodeName != 'LI' ) {
        return;
      }

      var collapser = document.createElement('div');
      collapser.className = 'collapser';
      collapser.innerHTML = '-';
      collapser.addEventListener('click', collapse, false);
      item.insertBefore(collapser, item.firstChild);
    }

    var jsonFormatter = new JSONFormatter;
    // Covert, and catch exceptions on failure
    if( Object.prototype.toString.call(jsonObj) !== '[object Object]' ) {
      jsonObj = JSON.parse(jsonObj);
    }
    var outputDoc = jsonFormatter.jsonToHTML(jsonObj);

    $(this).html(outputDoc);

    var items = $(this)[0].getElementsByClassName('collapsible');
    for( var i = 0; i < items.length; i++) {
      addCollapser(items[i].parentNode);
    }
  };

})(jQuery);
