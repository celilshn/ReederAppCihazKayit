package com.cengcelil.reederapp.Modal;

import java.util.ArrayList;

public class DeviceInformation {
    private int serviceId;
    private String detections, issues;
    private String uId;
    private String frontSide, backSide, bottomSide, topSide, leftSide, rightSide;

    public ArrayList<String> getExtraPhotos() {
        return extraPhotos;
    }

    public void setExtraPhotos(ArrayList<String> extraPhotos) {
        this.extraPhotos = extraPhotos;
    }

    private ArrayList<String> extraPhotos;
    public DeviceInformation() {
    }

    public int getServiceId() {
        return serviceId;
    }

    public String getFrontSide() {
        return frontSide;
    }

    public void setFrontSide(String frontSide) {
        this.frontSide = frontSide;
    }

    public String getBackSide() {
        return backSide;
    }

    public void setBackSide(String backSide) {
        this.backSide = backSide;
    }

    public String getBottomSide() {
        return bottomSide;
    }

    public void setBottomSide(String bottomSide) {
        this.bottomSide = bottomSide;
    }

    public String getTopSide() {
        return topSide;
    }

    public void setTopSide(String topSide) {
        this.topSide = topSide;
    }

    public String getLeftSide() {
        return leftSide;
    }

    public void setLeftSide(String leftSide) {
        this.leftSide = leftSide;
    }

    public String getRightSide() {
        return rightSide;
    }

    public void setRightSide(String rightSide) {
        this.rightSide = rightSide;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getDetections() {
        return detections;
    }

    public void setDetections(String detections) {
        this.detections = detections;
    }

    public String getIssues() {
        return issues;
    }

    public void setIssues(String issues) {
        this.issues = issues;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public DeviceInformation(int serviceId, String detections, String issues, String uId) {
        this.serviceId = serviceId;
        this.detections = detections;
        this.issues = issues;
        this.uId = uId;
        this.extraPhotos = new ArrayList<>();
    }
}
