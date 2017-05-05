package com.shishkovictor.onlinephonebook;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIInterface {

    @POST("/todo/api/v1.0/tasks")
    Call<User> addUser(@Body User user);

    @GET("/todo/api/v1.0/tasks")
    Call<Object> getUsers();

    @PUT("/todo/api/v1.0/tasks/{task_id}")
    Call<User> updateUserByID(@Path("task_id") int id, @Body User user);

    @DELETE("/todo/api/v1.0/tasks/{task_id}")
    Call<ResponseBody> deleteUserByID(@Path("task_id") int id);

}


































