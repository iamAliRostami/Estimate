package com.leon.estimate.Infrastructure;

/**
 * Created by Leon on 1/10/2018.
 */

public interface ISharedPreferenceManager {
    void putData(String key, String value);

    void putData(String key, int value);

    void putData(String key, boolean value);

    String getStringData(String key);

    int getIntData(String key);

    boolean getBoolData(String key);
}
