(function ($) {
    /**
     * Cell action logic exported here
     * @param  {object} subject   [your table]
     * @param  {string} method    [which function should be applied to object]
     * @param  {integer} number    [number from zero]
     * @param  {any} parameter [you can pass any parameter compatible with target method]
     */
    var cellApply = function (subject, method, number, parameter) {
        var rows = subject.find('tr');
        for (var i = 0; i < rows.length; i++) {
            $(rows[i]).find('th:eq(' + number + '), td:eq(' + number  + ')')[method](parameter);
        }
    }

    /**
     * Get column function
     * @param  {integer} number [from zero]
     * @return {object} jQuery object
     */
    $.fn.getColumn = function(number) {
        var result = this.find('tr').find('th:eq(' + number + '), td:eq(' + number  + ')');
        return result;
    };

    /**
     * Function to hide column
     * @param  {integer} number from zero]
     * @param  {any} parameter [you can pass any parameter compatible with target method]
     * @return {object}           [this]
     */
    $.fn.hideColumn = function(number, parameter) {
        cellApply(this, 'hide', number, parameter);
        return this;
    };

    /**
     * Function to toggle column
     * @param  {integer} number from zero]
     * @param  {any} parameter [you can pass any parameter compatible with target method]
     * @return {object}           [this]
     */
    $.fn.toggleColumn = function(number, parameter) {
        cellApply(this, 'toggle', number, parameter);
        return this;
    };

    /**
     * Function to show column
     * @param  {integer} number from zero]
     * @param  {any} parameter [you can pass any parameter compatible with target method]
     * @return {object}           [this]
     */
    $.fn.showColumn = function(number, parameter) {
        cellApply(this, 'show', number, parameter);
        return this;
    };

    /**
     * Function to show all columns
     * @param  {any} parameter [you can pass any parameter compatible with target method]
     * @return {object}           [this]
     */
    $.fn.showAllColumns = function(parameter) {
        this.find('th, td').show(parameter);
        return this;
    };
}(jQuery));