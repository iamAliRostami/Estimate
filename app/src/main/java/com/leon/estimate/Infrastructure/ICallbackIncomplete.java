package com.leon.estimate.Infrastructure;

import retrofit2.Response;

public interface ICallbackIncomplete<T> {
    void executeIncomplete(Response<T> response);
}
