package com.leon.estimate.Tables;

public class Uri {
    String uri;
    String billIdOrTrackNumber;

    public Uri(String uri) {
        this.uri = uri;
    }

    public Uri(String uri, String billIdOrTrackNumber) {
        this.uri = uri;
        this.billIdOrTrackNumber = billIdOrTrackNumber;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
