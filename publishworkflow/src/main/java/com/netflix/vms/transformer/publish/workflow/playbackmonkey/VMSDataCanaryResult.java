package com.netflix.vms.transformer.publish.workflow.playbackmonkey;

import com.netflix.config.NetflixConfiguration;
import com.netflix.config.NetflixConfiguration.RegionEnum;

import java.util.ArrayList;
import java.util.List;

public class VMSDataCanaryResult {

    private final String appId;
    private final RegionEnum region;
    private final List<Result> results;

    public VMSDataCanaryResult() {
        this(NetflixConfiguration.getAppId(), NetflixConfiguration.getRegionEnum());
    }

    public VMSDataCanaryResult(String appId, RegionEnum region) {
        this.appId = appId;
        this.region = region;
        this.results = new ArrayList<Result>();
    }

    public String getAppId() {
        return appId;
    }

    public RegionEnum getRegion() {
        return region;
    }

    public boolean allWereSuccessful() {
        for(Result result : results) {
            if(result.getResultType() != ResultType.SUCCESS)
                return false;
        }
        return true;
    }

    public List<Result> getResults() {
        return results;
    }

    public void addResult(String validatorClassname, ResultType result, String message) {
        results.add(new Result(validatorClassname, result, message));
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(appId);

        builder.append("|").append(region.toString());

        for(Result result : results) {
            builder.append("|");
            builder.append(result.getValidator()).append("`");
            builder.append(result.getResultType().toString()).append("`");
            builder.append(replaceControlChars(result.getMessage()));
        }

        return builder.toString();
    }

    private String replaceControlChars(String message) {
        return message.replace('|', '\\').replace('`', '\'');
    }

    public static VMSDataCanaryResult fromString(String str) {
        String results[] = str.split("\\|");

        VMSDataCanaryResult result = new VMSDataCanaryResult(results[0], RegionEnum.toEnum(results[1]));

        for(int i=2;i<results.length;i++) {
            String resultFields[] = results[i].split("`");
            result.addResult(resultFields[0], ResultType.valueOf(resultFields[1]), resultFields[2]);
        }

        return result;
    }

    public class Result {
        private final String validatorClassname;
        private final ResultType resultType;
        private final String message;

        public Result(String validatorClassname, ResultType resultType, String message) {
            this.validatorClassname = validatorClassname;
            this.resultType = resultType;
            this.message = message;
        }

        public String getValidator() {
            return validatorClassname;
        }

        public ResultType getResultType() {
            return resultType;
        }

        public String getMessage() {
            return message;
        }
    }

    public enum ResultType {
        SUCCESS,
        FAIL,
        UNEXPECTEDFAIL
    }

}
