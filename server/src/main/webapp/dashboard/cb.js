var cbNames = {};
var cbProperties = {};
var cbCountries = [];

var PROPS = {}

var ruleNames = [];

var cbconfig = {};

function centerEnabledDisabledModal() {
  var height = $('#enabled-disabled-override-modal').height();
  var width = $('#enabled-disabled-override-modal').width();
  var windowHeight = $(window).height();
  var windowWidth = $(window).width();

  var left = (windowWidth - width)/2;
  $('#enabled-disabled-override-modal').css('left', left).css('top','20px');
}

function centerThresholdModal() {
  var height = $('#threshold-override-modal').height();
  var width = $('#threshold-override-modal').width();
  var windowHeight = $(window).height();
  var windowWidth = $(window).width();

  var left = (windowWidth - width)/2;
  $('#threshold-override-modal').css('left', left).css('top','20px');  
}

function centerEditThresholdModal() {
  var height = $('#edit-threshold-modal').height();
  var width = $('#edit-threshold-modal').width();
  var windowHeight = $(window).height();
  var windowWidth = $(window).width();
  var left = (windowWidth - width) / 2;
  $('#edit-threshold-modal').css('left', left).css('top', '20px');
}

function getEnabledDisabledOverrides(ruleName) {
  var config = cbconfig[ruleName];
  var ret = {};
  ret.enabled = [];
  ret.disabled = [];
  if(config) {
    ret.enabled = config.enabledOverrides;
    ret.disabled = config.disabledOverrides;
  }
  return ret;
}

function getThresholdOverrides(ruleName) {
  var config = cbconfig[ruleName];
  var ret = {};
  if(config) {
    ret = config.thresholdOverrides;
  }
  return ret;
}

function fillEnabledDisabledSelect(ruleName, enabledOverrides, disabledOverrides) {
  // Get the variations for the rule name
  var config = cbconfig[ruleName];
  var variarions = [];
  if(config) {
    variations = config.variations;
  }
  for(var i = 0; i < variations.length; i++) {
    var variation = variations[i];
    if(enabledOverrides.indexOf(variation) != -1) continue;
    if(disabledOverrides.indexOf(variation) != -1) continue;    
    var option = $('<option>').append(variation).attr('value',variation);
    $('#enabled-disabled-variations-select').append(option);
  }
}

function fillThresholdOverrideSelect(ruleName, overrides) {
  var config = cbconfig[ruleName];
  var variations = [];
  if(config) {
    variations = config.variations;
  }
  for(var i = 0; i < variations.length; i++) {
    var variation = variations[i];
    if(overrides.indexOf(variation) != -1) continue;
    var option = $('<option>').append(variation).attr('value',variation);
    $('#threshold-variations-select').append(option);
  }
}

function setMasterStatus() {
  var circuitBreakersEnabled = (cbProperties['vms.circuitBreakersEnabled'] === "true");

  if(circuitBreakersEnabled) {
    $('#global-cb-text').removeClass('cb-disabled').addClass('cb-enabled');
    $('#global-cb-select').val('on');    
    $('#global-cb-select').slider('refresh');
  } else {
    $('#global-cb-text').removeClass('cb-enabled').addClass('cb-disabled');
    $('#global-cb-select').val('off');    
    $('#global-cb-select').slider('refresh');
  }

}

function buildCBRuleConfig2() {
  for(var i = 0; i < ruleNames.length; i++) {
    var ruleName = ruleNames[i];
    // Determine if the rule is enabled or not
    var enabled = cbProperties['vms.circuitBreakerEnabled.' + ruleName]
    // Determine the threshold for the rule
    var threshold = cbProperties['vms.circuitBreakerThreshold.' + ruleName];
    var config = {};
    config.enabled = enabled;
    config.threshold = threshold;
    cbconfig[ruleName] = config;
  }

  // Collect variations
  for(var i = 0; i < ruleNames.length; i++) {
    var ruleName = ruleNames[i];
    var hasVariations = (cbNames[ruleName] === 'true');
    if(hasVariations) {
      cbconfig[ruleName].variations = cbCountries;
    } else {
      cbconfig[ruleName].variations = [];
    }
  }

  // Set the empty arrays for the following
  // 1. enabledOverrides
  // 2. disabledOverrides
  for(var i = 0; i < ruleNames.length; i++) {
    var ruleName = ruleNames[i];
    cbconfig[ruleName].enabledOverrides = [];
    cbconfig[ruleName].disabledOverrides = [];
    cbconfig[ruleName].thresholdOverrides = {};
  }
}


function paintUI() {
  for(var i = 0; i < ruleNames.length; i++) {
    var ruleName = ruleNames[i];
    var row = $('<tr>');
    var cbName = $('<td>').append(ruleNames[i]);
    var enableDisableSlider = createSwitch(isRuleEnabled(ruleName), ruleName)
    var enabled = $('<td>').append(enableDisableSlider);
    enableDisableSlider.slider();
    var threshold = $('<span>').addClass('threshold-value').attr('data-rule-name',ruleName).append(cbconfig[ruleName].threshold);
    var editThreshold = $('<img>').attr('src','images/edit.png').addClass('edit-cb-threshold').attr('data-rule-name', ruleName);
    var thresholdCell = $('<td>').append(threshold).append('&nbsp;').append(editThreshold);
    row.append(cbName);
    row.append(enabled);
    row.append(thresholdCell);
    $('#cb').append(row);


    if(hasVariations(ruleName)) {
      // Check if there are any overrides
      var enableDisableOverrides = getEnableDisableOverridesCount(ruleName);
      var thresholdOverrides = getThresholdOverridesCount(ruleName);

      var overrideRow = $('<tr>');
      var emptyCell = $('<td>').append('&nbsp;');
      var enableDisableOverridesCell = $('<td>').append(createEnableDisableOverrideLink(enableDisableOverrides, ruleName));
      var thresholdOverridesCell = $('<td>').append(createThresholdOverrideLink(thresholdOverrides, ruleName));
      overrideRow.append(emptyCell);
      overrideRow.append(enableDisableOverridesCell);
      overrideRow.append(thresholdOverridesCell);
      $('#cb').append(overrideRow);
    }
  }
}

function isRuleEnabled(ruleName) {
  var config = cbconfig[ruleName];
  if(config) {
    if(config.enabled == "true") return true;
    return false;
  }
  return false;
}

function createSwitch(enabled, ruleName) {
  var offOption = $('<option>').attr('value','off').append('Off');
  var onOption = $('<option>').attr('value', 'on').append('On');
  if(enabled) {
    onOption.attr('selected','true');
  } else {
    offOption.attr('selected', 'true');
  }
  var select = $('<select>')
                .attr('data-role','slider')
                .addClass('cb-rule-enable-disable-switch')
                .attr('data-rule-name', ruleName);
  select.append(offOption);
  select.append(onOption);
  return select;
}

function createEnableDisableOverrideLink(numOverrides, ruleName) {
  var overrideCount = $('<span>').addClass('override-count').append(numOverrides);
  var link = $('<a>')
              .attr('href','#')
              .addClass('ed-override-link')
              .attr('rule-name', ruleName)
              .append(overrideCount)
              .append(' override(s)');
  return link;
}

function createThresholdOverrideLink(numOverrides, ruleName) {
  var overrideCount = $('<span>').addClass('override-count').append(numOverrides);
  var link = $('<a>')
              .attr('href','#')
              .addClass('threshold-override-link')
              .attr('rule-name', ruleName)
              .append(overrideCount)
              .append(' override(s)');
  return link;
}

function getEnableDisableOverridesCount(ruleName) {
  // Get the rule config object
  var config = cbconfig[ruleName];
  if(config) {
    var numOverrides = config.enabledOverrides.length + config.disabledOverrides.length;
    return numOverrides;
  }
   return 0;
}

function getThresholdOverridesCount(ruleName) {
  var config = cbconfig[ruleName];
  if(config) {
    return Object.keys(config.thresholdOverrides).length;
  }
  return 0;
}

function hasVariations(ruleName) {
  var config = cbconfig[ruleName];
  if(config) {
    return (config.variations.length > 0);
  }
  return false;
}

$(document).ready(function(){

  // First get the data from the CircuitBreaker REST entry point
  $.getJSON('/REST/vms/cb/names', function(names){

    cbNames = names;
    ruleNames = Object.keys(cbNames);

    // Get the properties
    $.getJSON('/REST/vms/cb/properties', function(props){
      cbProperties = props;

      // get the countries
      $.getJSON('/REST/vms/cb/countries', function(countries){
        cbCountries = countries.sort();

        // Rest of the logic of painting the UI
        setMasterStatus();

        // Build rule configuration object
        buildCBRuleConfig2();

        // Paint the UI
        paintUI();


        $('#overlay').remove();

        addEventListeners();

      });
    });
  });
});


function addEventListeners() {
  // Set the draggable areas
  $('#enabled-overrides-cell').droppable({
    drop: function(event, ui) {
      // Make it enabled
      ui.draggable.removeClass('disabled-override').addClass('enabled-override');
      // If the table cell does not have any overrides .. a text will be displayed
      // Remove this text
      if($('#enabled-overrides .enabled-override').length == 0)
        $('#enabled-overrides').text('');
      // Remove the override and create a new one which gets appended to the div
      var variation = ui.draggable.text();
      ui.draggable.remove();
      var override = $('<div>').addClass('enabled-override').append(variation);
      override.draggable();
      $('#enabled-overrides').append(override);
      // Update the local state
      var ruleName = $('#enabled-disabled-override-modal-table').data('rule-name');
      cbconfig[ruleName].enabledOverrides.push(variation);
      var index = cbconfig[ruleName].disabledOverrides.indexOf(variation);
      if(index > -1)
        cbconfig[ruleName].disabledOverrides.splice(index, 1);

      // Create the fast property override by creating or updating the property
      overrideEnabledDisabledRule(ruleName, variation, true);

    }
  });
  $('#disabled-overrides-cell').droppable({
    drop: function(event, ui) {
      ui.draggable.removeClass('enabled-override').addClass('disabled-override');      
      // If the table cell does not have any overrides .. a text will be displayed
      // Remove this text
      if($('#disabled-overrides .disabled-override').length == 0)
        $('#disabled-overrides').text('');
      // Remove the override and create a new one which gets appended to the div
      var variation = ui.draggable.text();
      ui.draggable.remove();
      var override = $('<div>').addClass('disabled-override').append(variation);
      override.draggable();
      $('#disabled-overrides').append(override);
      // Update the local state
      var ruleName = $('#enabled-disabled-override-modal-table').data('rule-name');
      cbconfig[ruleName].disabledOverrides.push(variation);
      var index = cbconfig[ruleName].enabledOverrides.indexOf(variation);
      if(index > -1)
        cbconfig[ruleName].enabledOverrides.splice(index, 1);

      // Create the fast property override by creating or updating the property
      overrideEnabledDisabledRule(ruleName, variation, false);
    }
  });

  // Define the cell as delete target (deletes the override if it can)
  $('#delete-enabled-disabled-override-cell').droppable({
    drop: function(event, ui) {
      // Extract the following
      // 1. The rule name
      // 2. The variation name
      // 3. Whether this was disabled or enabled
      var ruleName = $('#enabled-disabled-override-modal-table').data('rule-name');
      var variation = ui.draggable.text();
      var enabled = false;
      if(ui.draggable.attr('class').indexOf('disabled-override') != -1) {
        enabled = false;
      }
      if(ui.draggable.attr('class').indexOf('enabled-override') != -1) {
        enabled = true;
      }
      // Create the fpkey
      var fpkey = 'vms.circuitBreakerEnabled.' + ruleName + '.' + variation;
      // Ask if the user wants to delete the override
      var answer = confirm('Are you sure about deleting the override: ' + ruleName + '(' + variation + ')');
      if(answer) {
        $.post('/REST/vms/fpadmin/delete',
          {key: fpkey},
          function(message){
            if(message.startsWith('Deleted')) {
              // Remove the UI override
              ui.draggable.remove();

              // Update the override count in the UI
              $('a.ed-override-link').each(function(){
                if($(this).attr('rule-name') == ruleName) {
                  var count = Number($(this).find('.override-count').text());
                  $(this).find('.override-count').text(count - 1);
                }
              });

              // Update the local state
              if(enabled) {
                var index = cbconfig[ruleName].enabledOverrides.indexOf(variation);
                if(index != -1)
                  cbconfig[ruleName].enabledOverrides.splice(index, 1);
              } else {
                var index = cbconfig[ruleName].disabledOverrides.indexOf(variation);
                if(index != -1)
                  cbconfig[ruleName].disabledOverrides.splice(index, 1);
              }
            } else if(message.startsWith('NotFound')) {
              alert('Fast property was not found. The override probably comes from code/config.');
              if(enabled) {
                ui.draggable.remove();
                var override = $('<div>').addClass('enabled-override').append(variation);
                override.draggable();
                $('#enabled-overrides').append(override);
              } else {
                ui.draggable.remove();
                var override = $('<div>').addClass('disabled-overide').append(variation);
                override.draggable();
                $('#disabled-overrides').append(override);
              }
            }
          },
          'text'
        );
      } else {
        if(enabled) {
          ui.draggable.remove();
          var override = $('<div>').addClass('enabled-override').append(variation);
          override.draggable();
          $('#enabled-overrides').append(override);
        } else {
          ui.draggable.remove();
          var override = $('<div>').addClass('disabled-override').append(variation);
          override.draggable();
          $('#disabled-overrides').append(override);
        }
      }
    }
  });


  // Enable or disable the global circuit breaker
  $('#global-cb-select').bind('change', function(event, ui){
    var circuitBreakersEnabled = ($(this).val() == "on");
    //TODO: Make the AJAX call to update / create the fast property
    if(circuitBreakersEnabled) {
      $('#global-cb-text').removeClass('cb-disabled').addClass('cb-enabled');
    } else {
      $('#global-cb-text').removeClass('cb-enabled').addClass('cb-disabled');
    }

    enableOrDisableMasterCircuitBreaker(circuitBreakersEnabled);
  });


  // Clicking on the enable/disable override link
  $('a.ed-override-link').click(handleEnableDisableOverrideLinkClick);
  $('a.threshold-override-link').click(handleThresholdOverrideLinkClick);
  $('#close-enabled-disabled-override-modal').click(closeEnableDisableOverrideModal);
  $('#add-enabled-disabled-override-button').click(addEnabledDisabledOverride);
  $('#close-threshold-override-modal').click(closeThresholdOverrideModal);
  $('#add-threshold-override-button').click(addThresholdOverride);
  $('#close-edit-threshold-modal').click(closeEditThresholdModal);

  // Clicking on the edit icon for the circuit breaker threshold
  $('img.edit-cb-threshold').click(handleEditThresholdClick);

  // Update the circuit breaker threshold
  $('#update-cb-threshold-button').click(updateRuleThreshold);

  // Enable or disable individual circuit breaker rule (at the root level)
  $(document).on('change', 'select.cb-rule-enable-disable-switch', function(){
    console.log($(this).val());
    var enabled = ($(this).val() == "on");
    var fpkey = 'vms.circuitBreakerEnabled.' + $(this).data('rule-name');
    $.post("/REST/vms/fpadmin/createorupdate",
      {key: fpkey, value: enabled}, 
      function(message){alert(message)},
      "text"
    );
  });

  // Deleting threshold override
  $(document).on('click', 'img.delete-threshold-override', deleteThresholdOverride);

  // Clicking on the edit icon to edit the override's threshold 
  $(document).on('click', 'img.edit-threshold-override', handleEditThresholdOverrideIconClick);

  $('#cancel-edit-threshold-override').click(function(event){
    event.preventDefault();
    // Hide the edit form
    $('#edit-threshold-override-form-container').hide();
    // Show the rest
    $('#add-threshold-override-form-container').show();
    $('#threshold-overrides').show();
  });

  // Edit the threshold override when clicked on the edit button
  $('#edit-threshold-override-button').click(editThresholdOverride);
}

// ALL THE HANDLERS

function editThresholdOverride() {
  // Get the following:
  // 1. Rule Name
  // 2. variation name
  // 3. threshold value
  var ruleName = $.trim($('#threshold-override-modal-header p').text());
  var variation = $.trim($('#edit-override-variation-name').text());
  var threshold = $.trim($('#edit-threshold-override-value-textbox').val());

  var fpkey = 'vms.circuitBreakerThreshold.' + ruleName + '.' + variation;

  // Create the fast property
  overrideThreshold(ruleName, variation, threshold);

  // Update the local view
  $('#threshold-overrides .threshold-override').each(function(){
    if($(this).attr('variation') == variation) {
      $(this).attr('threshold', threshold);
      $(this).find('.threshold-override-value').text(threshold);
    }
  });
  // Update the local data
  cbconfig[ruleName].thresholdOverrides[variation] = threshold;

  // Hide the edit form
  $('#edit-threshold-override-form-container').hide();
  // Show the rest
  $('#add-threshold-override-form-container').show();
  $('#threshold-overrides').show();
}

function handleEditThresholdOverrideIconClick() {
  // Get the variation name and the threshold value
  var variation = $(this).parent().attr('variation');
  var threshold = $(this).parent().attr('threshold');
  // Update the form with the above values
  $('#edit-override-variation-name').text(variation);
  $('#edit-threshold-override-value-textbox').val(threshold);
  // Hide the list of overrides and the 'Add override' form
  $('#threshold-overrides').hide();
  $('#add-threshold-override-form-container').hide();
  // Show the edit override form
  $('#edit-threshold-override-form-container').show();
}

function deleteThresholdOverride() {
  // We need to determine the following
  // 1. the name of the rule
  // 2. name of the variation
  var ruleName = $.trim($('#threshold-override-modal-header p').text());
  var variation = $(this).parent().attr('variation');
  var variationDiv = $(this).parent();
  var fpkey = 'vms.circuitBreakerThreshold.' + ruleName + '.' + variation;
  var answer = confirm('Are you sure you want to delete the threshold override for ' + ruleName + '(' + variation + ')');
  if(answer) {
    // Delete the fast property
    $.post('/REST/vms/fpadmin/delete',
      {key: fpkey},
      function(message){
        if(message.startsWith('Deleted')) {
          alert(message);
          variationDiv.remove();
          // Update the override count in the table
          $('a.threshold-override-link').each(function(){
            if($(this).attr('rule-name') == ruleName) {
              var count = Number($(this).find('.override-count').text());
              $(this).find('.override-count').text(count - 1);
            }
          });
          // Update cbconfig
          delete cbconfig[ruleName].thresholdOverrides[variation];
          // Add the variation back to select
          var variationOption = $('<option>').attr('value', variation).append(variation);
          $('#threshold-variations-select').append(variationOption);
        } else if(message.startsWith('NotFound')){
          alert('Fast property was not found. The override probably is done in code / properties file. You can edit this value to match the root value.');
        }
      },
      'text'
    );
  }

}

function updateRuleThreshold() {
  var ruleName = $.trim($('#edit-threshold-modal-header p').text());
  var threshold = $.trim($('#cb-threshold-text').val());
  // Update the local state
  cbconfig[ruleName].threshold = threshold;
  // Update the local view
  $('.threshold-value').each(function(){
    if($(this).data('rule-name') == ruleName) {
      $(this).text(threshold);
    }
  });
  // We should create the fast property key
  var fpkey = 'vms.circuitBreakerThreshold.' + ruleName;
  $.post('/REST/vms/fpadmin/createorupdate',
    {key: fpkey, value: threshold},
    function(message){alert(message)},
    'text'
  );
  // Close the modal
  $('#edit-threshold-modal').hide();
  $('#modal-background').hide();

}

function handleEditThresholdClick() {
  var ruleName = $(this).data('rule-name');
  // Get the current threshold for this rule
  // we will just query the current state
  var threshold = cbconfig[ruleName].threshold;
  // Update the text box with the above threshold value
  $('#cb-threshold-text').val(threshold);
  $('#edit-threshold-modal-header p').text(ruleName);
  $('#modal-background').show();
  centerEditThresholdModal();
  $('#edit-threshold-modal').show();
}

function closeEditThresholdModal() {
  $('#edit-threshold-modal').hide();
  $('#modal-background').hide();
}

function addEnabledDisabledOverride() {
  // Get the rule name
  var ruleName = $('#enabled-disabled-override-modal-table').data('rule-name');

  // Get the variation
  var variation = $('#enabled-disabled-variations-select').val();

  // Get whether it is enabled or disabled
  var enabledVal = $('#enabled-disabled-value-select').val();
  var enabled = false;
  if(enabledVal == "on") {
    enabled = true;
  }

  // Add it to the list
  if(enabled) {
      if($('.enabled-override').length == 0)
        $('#enabled-overrides').text('');
      var override = $('<div>').addClass('enabled-override').append(variation);
      override.draggable();
      $('#enabled-overrides').append(override);
  } else {
      if($('.disabled-override').length == 0)
        $('#disabled-overrides').text('');
      var override = $('<div>').addClass('disabled-override').append(variation);
      override.draggable();
      $('#disabled-overrides').append(override);
  }

  // Remove it from the select
  $('#enabled-disabled-variations-select option').each(function(){
    if($(this).attr('value') === variation)
      $(this).remove();
  });

  // Update local state
  if(enabled) {
    cbconfig[ruleName].enabledOverrides.push(variation);
  } else {
    cbconfig[ruleName].disabledOverrides.push(variation);
  }

  // Create the fast property
  overrideEnabledDisabledRule(ruleName, variation, enabled);

  // Update the override count in the UI
  $('a.ed-override-link').each(function(){
    if($(this).attr('rule-name') == ruleName) {
      var currentCount = Number($(this).find('.override-count').text());
      $(this).find('.override-count').text(currentCount + 1);
    }
  });
}

function addThresholdOverride() {

  if($('.threshold-override').length == 0) {
    $('#threshold-overrides').text('');
  }

  // Get the rule name
  var ruleName = $.trim($('#threshold-override-modal-header p').text());

  // Get the variation from
  var variation = $('#threshold-variations-select').val();

  // Get the threshold value
  var threshold = $('#threshold-value-textbox').val();

  // Make sure that there is something written in the textbox
  if(threshold.length == 0) {
    alert('Please enter the threshold value');
    return;
  }

  // Add it to the list
  var override = createThresholdOverrideDiv(variation, threshold);
  $('#threshold-overrides').append(override);

  // Remove the variation from the select
  $('#threshold-variations-select option').each(function(){
    if($(this).attr('value') == variation)
      $(this).remove();
  });

  // Create threshold override property
  overrideThreshold(ruleName, variation, threshold);

  // Update cbconfig
  cbconfig[ruleName].thresholdOverrides[variation] = threshold;

  // update the override count in the UI
  $('.threshold-override-link').each(function(){
    if($(this).attr('rule-name') == ruleName) {
      var count = Number($(this).find('.override-count').text());
      $(this).find('.override-count').text(count + 1);
    }
  });
}

function closeEnableDisableOverrideModal(event) {

  // Remove all the variations from the select control
  $('#enabled-disabled-variations-select option').remove();

  //Remove all the overrides if there are any
  $('.enabled-override').remove();
  $('.disabled-override').remove();

  // Reset the text if any
  $('#enabled-overrides').text('');
  $('#disabled-overrides').text('');


  $('#enabled-disabled-override-modal').hide();
  $('#modal-background').hide();
}

function closeThresholdOverrideModal() {
  // Remove all the variations from the select control
  $('#threshold-variations-select option').remove();

  // Remove all the overrides
  $('.threshold-override').remove();

  // Reset the text if there is any
  $('#threshold-overrides').text('');

  $('#threshold-override-modal').hide();
  $('#modal-background').hide();
}

function handleEnableDisableOverrideLinkClick(event) {
  event.preventDefault();
  $('#modal-background').show();
  centerEnabledDisabledModal();
  $('#enabled-disabled-override-modal').show();
  var ruleName = $(this).attr('rule-name');
  var overrides = getEnabledDisabledOverrides(ruleName);

  // Update the modal title
  $('#enabled-disabled-override-modal-header p').text(ruleName);

  // Update the table's data attribute
  $('#enabled-disabled-override-modal-table').attr('data-rule-name',ruleName);

  // Enter the enabled overrides
  var enabledOverrides = overrides.enabled;
  var disabledOverrides = overrides.disabled;

  // fill the select control
  fillEnabledDisabledSelect(ruleName, enabledOverrides, disabledOverrides);


  if(enabledOverrides.length == 0) {
    $('#enabled-overrides').text('There are no overrides');
  } else {
    for(var i = 0; i < enabledOverrides.length; i++) {
      var override = $('<div>').addClass('enabled-override').append(enabledOverrides[i]);
      override.draggable();
      $('#enabled-overrides').append(override);
    }
  }

  if(disabledOverrides.length == 0) {
    $('#disabled-overrides').text('There are no overrides');
  } else {
    for(var i = 0; i < disabledOverrides.length; i++) {
      var override = $('<div>').addClass('disabled-override').append(disabledOverrides[i]);
      override.draggable();
      $('#disabled-overrides').append(override);
    }
  }
}

function handleThresholdOverrideLinkClick(event) {
  event.preventDefault();
  $('#modal-background').show();
  centerThresholdModal();
  // Hide and show various parts
  $('#edit-threshold-override-form-container').hide();
  $('#add-threshold-override-form-container').show();
  $('#threshold-overrides').show();

  $('#threshold-override-modal').show();
  var ruleName = $(this).attr('rule-name');
  var overrides = getThresholdOverrides(ruleName);

    // Update the modal title
  $('#threshold-override-modal-header p').text(ruleName);

  if(Object.keys(overrides).length == 0) {
    $('#threshold-overrides').text('There are no threshold overrides');
  } else {
    // Show Threshold Overrides
    for(var variation in overrides) {
      var override = createThresholdOverrideDiv(variation, overrides[variation]);
      $('#threshold-overrides').append(override);
    }
  }

  fillThresholdOverrideSelect(ruleName, Object.keys(overrides))
;}

function createThresholdOverrideDiv(variation, threshold) {
  var variationSpan = $('<span>').addClass('threshold-override-variation').append(variation);
  var value = $('<span>').addClass('threshold-override-value').append(threshold);
  var editIcon = $('<img>').addClass('edit-threshold-override').attr('src', 'images/edit.png');
  var deleteIcon = $('<img>').addClass('delete-threshold-override').attr('src', 'images/delete.png');
  var thresholdOverride = $('<div>').attr('variation', variation).attr('threshold', threshold).addClass('threshold-override');
  thresholdOverride.append(variationSpan);
  thresholdOverride.append('&nbsp;:&nbsp;');
  thresholdOverride.append(value);
  thresholdOverride.append(editIcon);
  thresholdOverride.append(deleteIcon);
  return thresholdOverride;
}

function enableOrDisableMasterCircuitBreaker(enabled) {
  var circuitBreakerEnabledSDisabledKey = null;
  for(var key in PROPS) {
    if(key.indexOf("circuitBreakerAggregationEnabled") != -1) {
      circuitBreakerEnabledSDisabledKey = key;
      break;
    }
  }

  $.post("/REST/vms/fpadmin/createorupdate",
    {key: circuitBreakerEnabledSDisabledKey, value: enabled}, 
    function(message){alert(message)},
    "text"
  );
  
}

function overrideThreshold(ruleName, overrideVariation, threshold) {
  var fpkey = 'vms.circuitBreakerThreshold.' + ruleName + '.' + overrideVariation + '.float';
  $.post('/REST/vms/fpadmin/createorupdate',
    {key: fpkey, value: threshold},
    function(message){alert(message)},
    'text'
  );
}

function overrideEnabledDisabledRule(ruleName, overrideVariation, enabled) {
  var fpkey = 'vms.circuitBreakerEnabled.' + ruleName + '.' + overrideVariation;
  $.post('/REST/vms/fpadmin/createorupdate',
    {key: fpkey, value: enabled},
    function(message){alert(message)},
    'text'
  );
}