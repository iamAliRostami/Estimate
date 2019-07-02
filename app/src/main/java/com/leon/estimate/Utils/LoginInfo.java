package com.leon.estimate.Utils;

/**
 * Created by Leon on 12/18/2017.
 */

public class LoginInfo {

    final String username;
    final String password;
    final String deviceid;

    public LoginInfo(String device_id, String username, String password) {
        this.deviceid = device_id;
        this.username = username;
        this.password = password;
    }

    public String getDevice_id() {
        return deviceid;
    }

    public String getPassword() {
        return password;
    }

    public String getUsername() {
        return username;
    }
}
