/* jQuery Simple Multi-Select plugin 1.1.1
 * vim:expandtab:tabstop=4 
 *
 * Copyright (c) 2009 Ethan Miller
 * Modifications (c) 2010 Antti Kaihola
 *
 * http://ethanmiller.name/notes/jquery_simplemultiselect/
 *
 * Dual licensed under the MIT and GPL licenses:
 *   http://www.opensource.org/licenses/mit-license.php
 *   http://www.gnu.org/licenses/gpl.html
 *
 */

(function($){
    $.fn.simpleMultiSelect = function(options){
        var settings = $.extend({
            highlight : '#CCE',
            border : '#777',
            width : undefined,
            height : undefined,
            classesOnly : false,
            container : 'sms-container',
            pseudoSelect : 'sms-pseudo-select',
            selected : 'sms-selected',
            unselected : 'sms-unselected',
            disabled : 'sms-disabled',
            optgroup : 'sms-optgroup',
            optgroupLabel : 'sms-optgroup-label'
        }, options);
        return this.each(function(){
            // wrapping select in a div so that the select and 
            // pseudo select will be siblings
            $(this).wrap('<div class="' + settings.container + '"></div>');
            var divselect = $('<div class="' + settings.pseudoSelect + '"></div>');
            buildFauxOptions($('> option, > optgroup', this), settings, divselect);
            if(!settings.classesOnly){
                divselect.css({
                    fontSize : $(this).css('font-size'),
                    fontFamily : $(this).css('font-family'),
                    width : settings.width || $(this).width(),
                    height : settings.height || $(this).height(),
                    cursor : "default",
                    overflow : "auto",
                    border : "1px solid " + settings.border
                });
            }
            $(this).after(divselect).hide();
        });
    };
    $.fn.smsNone = function(){
        return this.each(function(){
            siblingDivSet(this).each(function(){
                var psop = $(this);
                if(psop.data('selected')){
                    psop.click();
                }
            });
        });
    };
    $.fn.smsAll = function(){
        return this.each(function(){
            siblingDivSet(this).each(function(){
                var psop = $(this);
                if(!psop.data('selected')){
                    psop.click();
                }
            });
        });
    };
    function toggleSelected(elem, config){
        var sel = elem.data('selected');
        var dis = elem.data('disabled')
        if(config.classesOnly){
            elem.toggleClass(config.selected, sel);
            elem.toggleClass(config.unselected, !sel);
            elem.toggleClass(config.disabled, dis); // only happens onload
        }else{
            if(sel){
                elem.css({'background-color' : config.highlight});
            }else{
                elem.css({'background-color' : 'transparent'});
            }
            if(dis){
                elem.css({'color' : '#888'}); // only happens onload
            }
        }
    }
    function buildFauxOptions(elements, settings, divselect){ 
        elements.each(function(){
            if(this.tagName == 'OPTGROUP'){
                var subsel = $('<div/>');
                var label = $('<div/>').text($(this).attr('label'));
                subsel.append(label);
                if(settings.classesOnly){
                    subsel.addClass(settings.optgroup);
                    label.addClass(settings.optgroupLabel);
                }else{
                    subsel.css({'padding-left' : '10px'});
                    label.css({'font-weight' : 'bold'});
                }
                // recursive call here, using the same selector which means
                // nested optgroup's are supported - however, it doesn't render them 
                // nested. I'm not sure why - but in any case it matches html 4
                buildFauxOptions($('> option, > optgroup', this), settings, subsel);
                divselect.append(subsel);
                return true;
            }
            var op = $(this);
            var disabled = op.attr('disabled');
            var dv = $('<div/>')
                .text(op.text())
                .data('selected', op.attr('selected'))
                .data('disabled', disabled);
            // highlight pseudo option on load
            toggleSelected(dv, settings);
            dv.click(function(){
                if(disabled) return;
                // we still have references to op and dv here ...
                if(op.attr('selected')){
                    //de-select
                    op.removeAttr('selected');
                    dv.data('selected', false);
                    toggleSelected(dv, settings);
                }else{
                    //select
                    op.attr('selected', true);
                    dv.data('selected', true);
                    toggleSelected(dv, settings);
                }
            });
            divselect.append(dv);
        });
    }
    function siblingDivSet(sel){
        // expects a select object, return jquery set
        return $(sel).siblings('div').find('div');
    }
})(jQuery);
