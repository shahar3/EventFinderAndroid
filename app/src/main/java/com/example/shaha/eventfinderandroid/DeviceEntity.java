package com.example.shaha.eventfinderandroid;

import com.microsoft.azure.storage.table.TableServiceEntity;

/**
 * Created by shaha on 22/02/2018.
 */

public class DeviceEntity extends TableServiceEntity {
    private String deviceId;

    public DeviceEntity() {

    }

    public DeviceEntity(String deviceId) {
        this.partitionKey = deviceId;
        this.rowKey = deviceId;
        this.deviceId = deviceId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
