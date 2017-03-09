(function (window, document) {

    'use strict';

    var _blobRows = [];
    var _history = {};
    var _includePoisonedRows = false;

    const DATE_FORMAT = 'DD MMM YYYY [—] HH:mm:ss';
    const UTC_DATE_FORMAT = 'DD MMM YYYY [—] HH:mm:ss [UTC]';
    const PINNED_VERSIONS = {};
    const app = document.querySelector('#app');

    app.toggleTimezone = function (e) {
        e.stopPropagation();
        e.preventDefault();

        var checkbox = app.$.toggleUtcCheckbox;
        var showTimeInUTC = checkbox.checked = !checkbox.checked;
        var blobRows = _getTableData();

        blobRows.forEach(function (blobRow) {
            blobRow.publishDateStr = showTimeInUTC ?
                moment(blobRow.publishDateInUTC).format(UTC_DATE_FORMAT) :
                moment(blobRow.publishDate).format(DATE_FORMAT);
        });

        _setTableData(blobRows);
    };

    app.togglePoisonedBlobs = function (e) {
        e.stopPropagation();
        e.preventDefault();

        var checkbox = app.$.togglePoisonedCheckbox;
        _includePoisonedRows = checkbox.checked = !checkbox.checked;
        _setTableData(_getTableData());
    };

    app.handleBlobIconClick = function (e) {
        var target = Polymer.dom(e).path[2];
        if (target && target.getAttribute) {
            var version  = target.getAttribute('data-version'),
                blobType = target.getAttribute('data-blob-type');
            if (version && blobType) {
                _toggleBlobDetails(version, blobType)
            }
        }
    };

    function _fetchEnvironment () {
        fetch('/REST/vms/elasticsearchadmin?query=nflx-env')
            .then(function (response) { return response.text(); })
            .then(function (env) {
                app.environment = env;
            });
    }

    function _fetchBlobHistory () {
        var vipName = _getVip();
        fetch('/REST/vms/diffhistory?vip=' + vipName)
            .then(function (response) { return response.json() })
            .then(function(histories) {
                if(histories.error == undefined) {
                    for(var i = 0; i < histories.length; i++) {
                        var history = histories[i];
                        var changeSummary = history.topLevelChanges;
                        var changes = changeSummary.modifiedRecords + changeSummary.addedRecords + changeSummary.removedRecords;
                        _history[Math.floor(history.version / 100) * 100] = changes;
                    }
                }
                _fetchBlobs();
            });
    }

    function _fetchBlobs () {
        app.$.mainContainer.classList.add('isLoading');

        var vipName = _getVip();
        fetch(`/REST/vms/blobinfo?vip=${vipName}&format=json`)
            .then(function (response) { return response.json() })
            .then(function (response) {
                app.vipName = response.meta.vip;

                _setRegions(response.publish);
                _blobRows = _buildBlobRows(response.blobs, response.publish);
                _setPinnedVersions(response.pins, _blobRows);
                _setTableData(_blobRows);
            });
    }

    function _setRegions (publishedVersions) {
        for (var region in publishedVersions) {
            PINNED_VERSIONS[region] = null;
        }
    }

    function _buildBlobRows (blobs, publishedVersions) {
        function isBlobPinnable (blob, pinnableJarVersion, publishedVersions) {
            // Return false if the blob is poisoned
            if (blob.poisoned) {
                return false;
            }
            /*
            Commented out at the moment as we do not want to force non-pinnability if the blob is produced by different jar version
            if (pinnableJarVersion && pinnableJarVersion !== blob.attribs.ProducedByJarVersion) {
                return false;
            }
            */
            // If the version is not announced in all regions, it is not pinnable
            for (var region in publishedVersions) {
                if (blob.version > publishedVersions[region]) {
                    return false;
                }
            }
            return blob.pinable;
        }

        var discoverybaseUrl = null;
        if(app.environment == 'test') {
            discoverybaseUrl = 'http://discovery.cloudqa.netflix.net:7001'
        } else {
            discoverybaseUrl = 'http://discovery.cloud.netflix.net:7001'
        }

        var diffUiClusterName = 'vmstransformerhistory-' + _getVip();

        var pinnableJarVersion = null;
        var blobRows = blobs.map(function (blob) {
            var blobRow = {};

            var publishedDate = new Date(blob.attribs.publishedDate);
            blobRow.publishDate = publishedDate.getTime();
            blobRow.publishDateInUTC = (new Date(publishedDate.toUTCString().replace(' GMT', ''))).getTime();
            blobRow.publishDateStr = moment(blobRow.publishDate).format(DATE_FORMAT)
            blobRow.version = blob.version;
            blobRow.types = blob.types;
            blobRow.changes = _history[Math.floor(blob.version / 100) * 100];
            blobRow.changeUrl = discoverybaseUrl + '/discovery/resolver/cluster/' + diffUiClusterName + '/REST/history/state?version=' + blob.version;
            blobRow.selectedType = null;
            blobRow.isPoisoned = blob.poisoned || false;
            blobRow.isPinnable = isBlobPinnable(blob, pinnableJarVersion, publishedVersions);
            //blobRow.isPinnable = blob.pinable
            blobRow.hasBrokenChain = !(typeof(blob.types.DELTA) === 'object') && !(typeof(blob.types.REVERSEDELTA) === 'object');
            blobRow.hasDelta = (typeof(blob.types.DELTA) === 'object');
            blobRow.hasReverseDelta = (typeof(blob.types.REVERSEDELTA) === 'object');
            blobRow.hasSnapshot = (typeof(blob.types.SNAPSHOT) === 'object');

            if (blobRow.isPinnable) {
                pinnableJarVersion = blob.attribs.ProducedByJarVersion;
            }

            return blobRow;
        });

        return blobRows;
    }

    function _setPinnedVersions (pinnedVersionMap, blobRows) {
        Object.keys(pinnedVersionMap).forEach(function (region) {
            PINNED_VERSIONS[region] = pinnedVersionMap[region];
        });

        blobRows.forEach(function (blobRow) {
            blobRow.isPinned = false;

            blobRow.regions = Object.keys(PINNED_VERSIONS).map(function (region) {
                return {
                    region: region,
                    pinned: (PINNED_VERSIONS[region] == blobRow.version)
                };
            });
            blobRow.regions.forEach(function (region) {
                if (region.pinned) {
                    blobRow.isPinned = true;
                }
            });
        });
    }

    function _setTableData (blobRows) {
        var filteredBlobRows = blobRows;
        if (!_includePoisonedRows) {
            filteredBlobRows = blobRows.filter(function (blobRow) { return !blobRow.isPoisoned });
        }
        app.$.blobTable.setData(filteredBlobRows);

        var pinnedBlobRows = filteredBlobRows.filter(function (blobRow) { return blobRow.isPinned === true });
        app.$.pinnedBlobTable.setData(pinnedBlobRows);

        if (pinnedBlobRows.length > 0) {
            app.$.pinnedBlobTable.parentNode.removeAttribute('hidden');
        } else {
            app.$.pinnedBlobTable.parentNode.setAttribute('hidden', true);
        }

        window.setTimeout(function () {
            app.$.mainContainer.classList.remove('isLoading');
            _setScrollableContainerHeight();
        }, 0);
    }

    function _getTableData () {
        return _blobRows;
    }

    function _toggleBlobDetails (version, blobType) {
        var blobRows = _getTableData();
        var selectedBlobRow = blobRows.find(function (item) { return item.version === version });
        var selectedBlobType = selectedBlobRow.types[blobType];

        if (selectedBlobRow.selectedType && selectedBlobRow.selectedType.type === blobType) {
            selectedBlobRow.selectedType = null;
            selectedBlobRow.showDetails = false;
        } else {
            selectedBlobRow.selectedType = { type: blobType };
            selectedBlobRow.selectedType.attributes = [
                { label: 'Published at',    value: moment(new Date(parseInt(selectedBlobType.publishedTimestamp))).format(DATE_FORMAT) },
                { label: 'From/To Version', value: (selectedBlobType.fromVersion || 'N/A') + ' ⟶ ' + selectedBlobType.toVersion },
                { label: 'Prior Version',   value: selectedBlobType.priorVersion },
                { label: 'Produced by',     value: selectedBlobType.ProducedByServer },
                { label: 'Bucket',          value: selectedBlobType.bucket },
                { label: 'Filename',        value: selectedBlobType.filename },
                { label: 'JAR Version',     value: selectedBlobType.ProducedByJarVersion }
            ];
            selectedBlobRow.showDetails = true;
        }

        _setTableData(blobRows);
    }

    function _updatePinnedState (operation, region, version) {
        var pinnedVersionMap = {};
        var regions = (region === 'all') ? Object.keys(PINNED_VERSIONS) : [ region ];
        var pinnedVersion = (operation === 'pin') ? version : null;
        var blobRows = _getTableData();

        regions.forEach(function (region) {
            pinnedVersionMap[region] = pinnedVersion;
        });

        _setPinnedVersions(pinnedVersionMap, blobRows);
        _setTableData(blobRows);
    }

    function _setScrollableContainerHeight () {
        var scrollableContainer = app.$.scrollableContainer,
            topOffset = scrollableContainer.getBoundingClientRect().top + 12;
        scrollableContainer.style.height = `calc(100% - ${topOffset}px)`;
    }

    function _assignEventListeners () {
        document.body.addEventListener('click', function () { app.fire('VMSCloseSplitButtons') });

        app.$.mainContainer.addEventListener('VMSPinUnpinSuccess', function (e) {
            _updatePinnedState(e.detail.op, e.detail.region, e.detail.version);
        });
    }

    function _getVip () {
        return nf.url.getParameterByName('vip') || '';
    }

    app.addEventListener('dom-change', function () {
        _assignEventListeners();
        _fetchEnvironment();
        _fetchBlobHistory();
    });

}(window, document));
