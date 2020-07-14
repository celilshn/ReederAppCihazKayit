package com.cengcelil.reederapp.Modal;

public class MyPreviewsRecyclerItem {
    private int ServiceId;

    public int getServiceId() {
        return ServiceId;
    }

    public void setServiceId(int serviceId) {
        ServiceId = serviceId;
    }

    public DeviceInformation getDeviceInformationFirstPreview() {
        return deviceInformationFirstPreview;
    }

    public void setDeviceInformationFirstPreview(DeviceInformation deviceInformationFirstPreview) {
        this.deviceInformationFirstPreview = deviceInformationFirstPreview;
    }

    public DeviceInformation getDeviceInformationLastPreview() {
        return deviceInformationLastPreview;
    }

    public void setDeviceInformationLastPreview(DeviceInformation deviceInformationLastPreview) {
        this.deviceInformationLastPreview = deviceInformationLastPreview;
    }

    public MyPreviewsRecyclerItem(int serviceId, DeviceInformation deviceInformationFirstPreview, DeviceInformation deviceInformationLastPreview) {
        ServiceId = serviceId;
        this.deviceInformationFirstPreview = deviceInformationFirstPreview;
        this.deviceInformationLastPreview = deviceInformationLastPreview;
    }

    private DeviceInformation deviceInformationFirstPreview,deviceInformationLastPreview;
}
