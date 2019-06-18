/**
 * Utilities to parse the "message" string in vmsiops to key-value map
 */

function UrlMapper() {
}

function VipAddressHolder() {
}

VipAddressHolder.prototype.getVipAddress = function() {
	var vip = $("#id-vms-vip-select").val();
	return vip.substring(4, vip.length - 4);
};

VipAddressHolder.prototype.getSummaryQuerySize = function() {
    var vip = $("#id-vms-vip-select").val();
    if(vip.indexOf("_override") == -1) {
        return 720;
    }
    return 3000;
}

UrlMapper.prototype.getIndexHost = function() {
    // "http://discovery.cloudqa.netflix.net:7001/discovery/resolver/cluster/elasticsearch-vmsiops:7104/"
    var hostname = $("#id-elasticsearchhost-box").val();
    if (!hostname) {
        alert("Hostname needs to be specified");
        return "http://localhost:7104/";
    }
    return "http://" + hostname + ":7104/";
};

function ResponseFieldMapper() {
}

ResponseFieldMapper.prototype.getIndexFields = function() {
    var fields = new Object();
    fields.push("_index");
    fields.push("_type");
    fields.push("_source");
    return fields;
};

function RegexParserMapper() {
}

RegexParserMapper.prototype.getTransformBuilderRegexInfo = function() {
    var fieldRegex = new Object();
    fieldRegex["builder"] = [ /^\(com.netflix.videometadata.builder.(.*)\)/, 1 ];
    fieldRegex["input-size"] = [ /^size_input\((.*)\)/, 1 ];
    fieldRegex["size-before"] = [ /^size_before\((.*)\)/, 1 ];
    fieldRegex["size-after"] = [ /^size_after\((.*)\)/, 1 ];
    fieldRegex["duration(ms)"] = [ /^duration\((.*)\)/, 1 ];
    fieldRegex["success"] = [ /^success\((.*)\)/, 1 ];
    return fieldRegex;
};

// Cycle=20130805054841921: Category=: ErrorCode=GeneralInfo: Info: : Starting load of all data...
RegexParserMapper.prototype.getNumCyclesRegexInfo = function() {
    var fieldRegex = new Object();
    fieldRegex["cycle"] = [ /^Cycle=(.*):/, 1 ];
    return fieldRegex;
};

// "Cycle=20130805054841921: Category=RequiredCountryProfileBitratesMayaInputBuilder:
// ErrorCode=GeneralInfo: Info: :
// Completed job: RequiredCountryProfileBitratesMayaInputBuilder in:520 ms"
RegexParserMapper.prototype.getJobBuilderRegexInfo = function() {
    var fieldRegex = new Object();
    fieldRegex["cycle"] = [ /^Cycle=(.*):/, 1 ];
    fieldRegex["category"] = [ /^Category=(.*):/, 1 ];
    fieldRegex["duration(ms)"] = [ /^in:(.*)/, 1 ];
    return fieldRegex;
};

// "Input=vmsconverter-muon Version=20190617232848627"
RegexParserMapper.prototype.getCinderInputDataVersionsRegexInfo = function() {
    var fieldRegex = new Object();
    fieldRegex["Input"] = [ /^Input=(.*)/, 1 ];
    fieldRegex["Version"] = [ /^Version=(.*)/, 1 ];
    return fieldRegex;
};


// "mutationGroup=PERSON_BIO latestEventId=1125975801 coldstartVersionId=1463012034071 coldstartKeybase=dummyValue coldstartS3Filename=anotherDummyValue"
RegexParserMapper.prototype.getInputDataRegexInfo = function() {
    var fieldRegex = new Object();
    fieldRegex["Input"] = [ /^Input=(.*)/, 1 ];
    fieldRegex["Keybase"] = [ /^Keybase=(.*)/, 1 ];
    fieldRegex["Type"] = [ /^Type=(.*)/, 1 ];
    fieldRegex["Version"] = [ /^Version=(.*)/, 1 ];
    fieldRegex["EventId"] = [ /^EventId=(.*)/, 1 ];
    fieldRegex["EventCheckpoint"] = [ /^EventCheckpoint=(.*)/, 1 ];
    fieldRegex["FileName"] = [ /^FileName=(.*)/, 1 ];
    fieldRegex["PublishTime"] = [ /^PublishTime=(.*)/, 1 ];
    return fieldRegex;
};


// Returning value(2) for property(com.netflix.videometadata.validation.threadpool.size)
RegexParserMapper.prototype.getStartPropertiesRegexInfo = function() {
    var fieldRegex = new Object();
    fieldRegex["property"] = [ /^property\((.*)\)/, 1 ];
    fieldRegex["value"] = [ /^value\((.*)\)/, 1 ];
    return fieldRegex;
};

RegexParserMapper.prototype.getBlobPublishRegexInfo = function() {
    var fieldRegex = new Object();
    fieldRegex["keybase"] = [ /^keybase=(.*)/, 1 ];
    fieldRegex["region"] = [ /^region=(.*)/, 1 ];
    fieldRegex["version"] = [ /^dataVersion=(.*)/, 1 ];
    fieldRegex["filesize(bytes)"] = [ /^size=(.*)/, 1 ];
    fieldRegex["duration(ms)"] = [ /^duration=(.*)ms$/, 1 ];
    return fieldRegex;
};

RegexParserMapper.prototype.getPropertiesRegexInfo = function() {
    var fieldRegex = new Object();
    fieldRegex["propertyName"] = [ /^key=(.*)/, 1 ];
    fieldRegex["propertyValue"] = [ /^value=(.*)/, 1 ];
    return fieldRegex;
};

RegexParserMapper.prototype.getBlobStatusRegexInfo = function() {
    var fieldRegex = new Object();
    fieldRegex["keybase"] = [ /^keybase=(.*),/, 1 ];
    fieldRegex["version"] = [ /^version=(.*),/, 1 ];
    fieldRegex["region"] = [ /^region=(.*)/, 1 ];
    fieldRegex["sucess"] = [ /^success=(.*)$/, 1 ];
    return fieldRegex;
};

RegexParserMapper.prototype.getDataVersionRegexInfo = function() {
    var fieldRegex = new Object();
    fieldRegex["dataVersion"] = [ /^dataVersion=(.*)$/, 1 ];
    return fieldRegex;
};

RegexParserMapper.prototype.getProgressRegexInfo = function() {
    var fieldRegex = new Object();
    fieldRegex["percent"] = [ /^percent=(.*)$/, 1 ];
    return fieldRegex;
};

RegexParserMapper.prototype.getTopNodesRegexInfo = function() {
    var fieldRegex = new Object();
    fieldRegex["topNodes"] = [ /^topNodes=(.*)/, 1 ];
    return fieldRegex;
};

RegexParserMapper.prototype.getJarVersionRegexInfo = function() {
    var fieldRegex = new Object();
    fieldRegex["JarVersion"] = [ /^jarVersion=(.*)/, 1 ];
    return fieldRegex;
};
