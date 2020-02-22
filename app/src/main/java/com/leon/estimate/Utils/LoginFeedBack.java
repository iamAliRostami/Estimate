package com.leon.estimate.Utils;

/**
 * Created by Leon on 12/17/2017.
 */

public class LoginFeedBack {
    String access_token;
    String refresh_token;

    public LoginFeedBack(String access_token, String refresh_token) {
        this.access_token = access_token;
        this.refresh_token = refresh_token;
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }
}
