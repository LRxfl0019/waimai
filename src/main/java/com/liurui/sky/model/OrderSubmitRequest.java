package com.liurui.sky.model;

import jakarta.validation.constraints.NotNull;

public class OrderSubmitRequest {

    @NotNull
    private Long addressId;

    private String remark;

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
