package com.example.models;

public class PendingRequest {

    private int requestId;
    private String employeeName;
    private String softwareName;
    private String accessType;
    private String reason;

    public int getRequestId() {
        return requestId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getSoftwareName() {
        return softwareName;
    }

    public String getAccessType() {
        return accessType;
    }

    public String getReason() {
        return reason;
    }


    public PendingRequest(int requestId, String employeeName, String softwareName, String accessType, String reason) {
        this.requestId = requestId;
        this.employeeName = employeeName;
        this.softwareName = softwareName;
        this.accessType = accessType;
        this.reason = reason;
    }
}
