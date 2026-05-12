package com.techfix.model;


public class DeviceModel {
    private int jobId;
    private int deviceId;
    private String fullName;
    private String city;
    private String phone;
    private String deviceName;
    private String status;
    private String problemDescription;
    private String devicePin;

    public DeviceModel(int jobId, int deviceId, String fullName, String city, String phone, String deviceName, String status, String problemDescription, String devicePin) {
        this.jobId = jobId;
        this.deviceId = deviceId;
        this.fullName = fullName;
        this.city = city;
        this.phone = phone;
        this.deviceName = deviceName;
        this.status = status;
        this.problemDescription = problemDescription;
        this.devicePin = devicePin;
    }

    public int getJobId() { return jobId; }
    public int getDeviceId() { return deviceId; }
    public String getFullName() { return fullName; }
    public String getCity() { return city; }
    public String getPhone() { return phone; }
    public String getDeviceName() { return deviceName; }
    public String getStatus() { return status; }
    public String getProblemDescription() { return problemDescription; }
    public String getDevicePin() { return devicePin; }

    @Override
    public String toString() {
        return deviceName + " [" + status + "]";
    }
}