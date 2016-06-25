/*
 * Assigns appropriate widgets to divs in index.html and drives the user interaction
 */

function Dashboard() {
    var dashboard = this;
    var vmsblobinfo;

    this.nflxEnvironment = "test";
    this.dataNameSpace = "boson";
    this.vipAddress = "";

    // --------------------------------------------------------------------
    // Create the Server, Client, Data and Diagnostics tabs
    // --------------------------------------------------------------------
    this.initialize = function() {
        $.ajax({
            async : false,
            type : 'GET',
            url : '/REST/vms/elasticsearchadmin?query=nflx-env',
            success : function(data) {
                var nflxEnv = new String(data);
                if (nflxEnv == "test" || nflxEnv == "int" || nflxEnv == "prod") {
                    dashboard.nflxEnvironment = nflxEnv;
                    $("#id-env-txt").text(nflxEnv.toUpperCase());
                    if (nflxEnv == "prod") {
                        $("#id-env-txt").addClass("spinnaker-banner-prod");
                    }
                }
            }
        });

        $.ajax({
            async : false,
            type : 'GET',
            url : '/REST/vms/elasticsearchadmin?query=data-namespace',
            success : function(data) {
                dashboard.dataNameSpace = new String(data);
            }
        });

        $.ajax({
            async : false,
            type : 'GET',
            url : '/REST/vms/elasticsearchadmin?query=vip-address',
            success : function(data) {
                dashboard.vipAddress = new String(data); 
                $("#id-vip-txt").html(dashboard.vipAddress);
            }
        });

        this.createServerInfoTab();
        this.createClientHistoryTab();
        this.createTitleStatsTab();
        this.createDiagnosticsTab();
        this.createCircuitBreakerTab();
        this.createDataIOTab();
    };

    // --------------------------------------------------------------------
    // Server Info Tab
    // --------------------------------------------------------------------
    this.createServerInfoTab = function() {
        var serverInfoTab = new VmsServerInfoTab(dashboard);
        serverInfoTab.initialize();
    };
    
    // --------------------------------------------------------------------
    // Client Stats Tab
    // --------------------------------------------------------------------
    this.createClientHistoryTab = function() {
        var hostName = "http://atlasui.prod.netflix.net";
        var path = "/dashboard/show/VMS%20Client%20Dashboard#url=http:%2F%2Fatlas-us.prod.netflix.net:7001%2Fapi%2Fv1%2Fgraph%3Fq&title=Refresh%20Duration%20%28max%29%20%28minutes%29&o=0&no_legend=1&e=now-5m&s=e-3h&w=517&h=310&refresh=23442969&tab=Client%20Refreshes%20grouped%20by%20app";
        var clientdash = new IFrameWidget("#id-vms-client-dashboard", "id-vms-client-dashboard-iframe", hostName, path);
        var clientJars = new IFrameWidget("#id-vms-client-jarversions-atlas", "id-vms-client-jarversions-atlas-iframe", "http://go", "/vmsclientjars");
        var vmssps = new IFrameWidget("#id-vms-sps-content", "id-vms-sps-content-iframe", "http://go", "/vmssps");
        var vmsusage = new IFrameWidget("#id-vms-usage-info", "id-vms-usage-content-iframe", "http://go", "/vmsusage");
        dashboard.vmsblobinfo = new IFrameWidget("#id-vms-blob-info", "id-vms-blob-info-iframe", "", "/dashboard/blobinfo");

        $("#okeanos-tabs").on("tabsbeforeactivate", function(e, ui) {
            var id = ui.newPanel.attr('id');
            if (id == "client-dashboard-tab") {
                vmssps.initialize();
            }
        });

        $("#client-dashboard-tab").on("tabsbeforeactivate", function(e, ui) {
            var id = ui.newPanel.attr('id');
            if (id == "stats-client-jarversions") {
                clientJars.initialize();
            }
            if (id == "stats-client-historic") {
                clientdash.initialize();
            }
            if (id == "stats-client-blobinfo") {
                dashboard.vmsblobinfo.initialize();
                dashboard.refreshBlobInfo();
            }
            if (id == "stats-client-usage") {
                vmsusage.initialize();
            }
        });
    };

    this.refreshBlobInfo = function() {
        dashboard.vmsblobinfo.srcLink = "/dashboard/blobinfo?vip=" + VipAddressHolder.prototype.getVipAddress();
        dashboard.vmsblobinfo.refresh();
    };

    this.changeDashboard = function() {
        var cluster = null;
        var region = "us-east-1";
        var clusterURL = "/dashboard/cluster.jsp?env=" + dashboard.nflxEnvironment + "&reg=" + region + "&vip=" + VipAddressHolder.prototype.getVipAddress();
        $.ajax({
            async : false,
            type : 'GET',
            url : clusterURL,
            statusCode: {
                200: function(data) {
                    cluster = new String(data);
                }
            },
            error: function() {
                //alert('cluster not found:' + cluster);
                //this.refreshBlobInfo();
            },
        });
        if (cluster==null) {
            alert("error: " + clusterURL);
            return;
        }

        var host = dashboard.nflxEnvironment == "prod" ? "discovery.cloud.netflix.net" : "discovery.cloudqa.netflix.net";
        var discoveryURL = "http://" + host +":7001/discovery/resolver/cluster/" + cluster + "/dashboard";
        var checkURL = "/dashboard/url.jsp?action=status&url="+discoveryURL;
        $.ajax({
            async : false,
            type : 'GET',
            url : checkURL,
            statusCode: {
                200: function(data) {
                    // cluster is fine so redirect to it
                    window.location = discoveryURL;
                }
            },
            error: function() {
                //alert('cluster not found:' + cluster);
                //this.refreshBlobInfo();
            },
        });
    };

    // --------------------------------------------------------------------
    // Published Data Tab
    // --------------------------------------------------------------------
    this.createTitleStatsTab = function() {
        var goHostName = "http://go";
        var wave2frame = new IFrameWidget("#id-title-stats-dashboard", "id-title-stats-iframe", goHostName, "/wave2launchcontent");
        var target = dashboard.nflxEnvironment == "prod" ? "vmseventsdashboard":"vmseventsdashboardtest";
        var mutationsFrame = new IFrameWidget("#id-mutation-events-dashboard", "id-mutation-events-dashboard-iframe", "http://go/", target);
        var dataValFrame = new IFrameWidget("#id-dataval-dashboard", "id-dataval-dashboard-iframe", "http://go/", "vmsdatavalidationdashboard");

        $("#okeanos-tabs").on("tabsbeforeactivate", function(e, ui) {
            var id = ui.newPanel.attr('id');
            if (id == "data-dashboard-tab") {
                wave2frame.initialize();
            }
        });

        $("#data-dashboard-tab").on("tabsbeforeactivate", function(e, ui) {
            var id = ui.newPanel.attr('id');
            if (id == "stats-events") {
                mutationsFrame.initialize();
            }
        });
        $("#data-dashboard-tab").on("tabsbeforeactivate", function(e, ui) {
            var id = ui.newPanel.attr('id');
            if (id == "stats-dataval") {
                dataValFrame.initialize();
            }
        });
    };

    // --------------------------------------------------------------------
    // Diagnostics Tab
    // --------------------------------------------------------------------
    this.createDiagnosticsTab = function() {
        var vmsdebug = new IFrameWidget("#id-vmsdebug-tab", "id-vmsdebug-iframe", "http://go/", "vmsdebug");
        var vmsiops = dashboard.nflxEnvironment == "prod" ? "vmsiops" : "vmsiopstest";
        var vmsiopsysframe = new IFrameWidget("#id-vmsiopsys-dashboard", "id-vmsiopsys-iframe", "http://go/", vmsiops);
        // $("#id-vmsiopsys-dashboard").load("miner.html");

        $("#okeanos-tabs").on("tabsbeforeactivate", function(e, ui) {
            var id = ui.newPanel.attr('id');
            if (id == "debug-dashboard-tab") {
                vmsiopsysframe.initialize();
                vmsdebug.initialize();
            }
        });
    };

    this.createCircuitBreakerTab = function() {
        var cbtab = new IFrameWidget("#cb-inset","id-cb-iframe","","/dashboard/cb.html");
        $("#okeanos-tabs").on("tabsbeforeactivate", function(e, ui) {
            var id = ui.newPanel.attr('id');
            if (id == "cb-dashboard-tab") {
                cbtab.initialize();
            }
        });
    };

    this.createDataIOTab = function() {
        var iotab = new IFrameWidget("#io-inset","id-io-iframe","","/dashboard/dataio/index.jsp");
        $("#okeanos-tabs").on("tabsbeforeactivate", function(e, ui) {
            var id = ui.newPanel.attr('id');
            if (id == "io-dashboard-tab") {
                iotab.initialize();
            }
        });
    };
}// Dashboard

