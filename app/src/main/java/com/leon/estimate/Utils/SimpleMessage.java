package com.leon.estimate.Utils;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Leon on 12/24/2017.
 */

public class SimpleMessage {
    @SerializedName("message")
    String message;

    public SimpleMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
