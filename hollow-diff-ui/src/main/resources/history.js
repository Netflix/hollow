/*
 *
 *  Copyright 2016 Netflix, Inc.
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */
var total = 0;

function handleAjax(state) {
  var added = 0;
  var modified = 0;
  var removed = 0;

  // Calculate the total number of changes
  for(var i = 0; i < state.objectTypes.length; i++) {
    var stats = state.objectTypes[i];
    added += stats.additions;
    modified += stats.modifications;
    removed += stats.removals;
  }

  total = added + modified + removed;

  // First calculate the % of additions, modifications and removals 
  var addedWidth = (added / total) * $('#total-stats').width();
  var modifiedWidth = (modified / total) * $('#total-stats').width();
  var removedWidth = (removed / total) * $('#total-stats').width();

  if(added >0)
    $('#total-additions').text(added);
  if(modified > 0)
    $('#total-modifications').text(modified);
  if(removed > 0)
    $('#total-removals').text(removed);

  $('#total-additions').animate({'width': Math.floor(addedWidth)}, 1000);
  $('#total-modifications').animate({'width': Math.floor(modifiedWidth)}, 1000);
  $('#total-removals').animate({'width': Math.floor(removedWidth)}, 1000);


}
