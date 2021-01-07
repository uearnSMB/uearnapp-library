package smarter.uearn.money.utils;

/**
 * Created by Srinath on 15-02-2018.
 */

public class KnowlarityCallLogs {
    private String apiRequestTime = "";
    private String apiResponseTime = "";
    private String callLandTime = "";

    public KnowlarityCallLogs(String apiRequestTime, String apiResponseTime, String callLandTime) {
        this.apiRequestTime = apiRequestTime;
        this.apiResponseTime = apiResponseTime;
        this.callLandTime = callLandTime;
    }

    public String getApiRequestTime() {
        return apiRequestTime;
    }

    public String getApiResponseTime() {
        return apiResponseTime;
    }

    public String getCallLandTime() {
        return callLandTime;
    }
}
