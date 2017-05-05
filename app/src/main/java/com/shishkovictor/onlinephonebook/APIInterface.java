package com.shishkovictor.onlinephonebook;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface APIInterface {

    @POST("/todo/api/v1.0/tasks")
    Call<User> addUser(@Body User user);

    @GET("/todo/api/v1.0/tasks")
    Call<Object> getUsers();

}


































