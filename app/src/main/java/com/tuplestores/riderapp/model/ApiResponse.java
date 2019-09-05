package com.tuplestores.riderapp.model;

import com.google.gson.annotations.SerializedName;

/*Created By Ajish Dharman on 28-August-2019
 *
 *
 */public class ApiResponse {

    @SerializedName("status")
    private String status;
    @SerializedName("msg")
    private String msg;



    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
