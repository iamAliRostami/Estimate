package com.leon.estimate.Infrastructure;


import android.view.View;

public interface ViewConfigurator<T extends View> {
    void configureView(T v);
}
