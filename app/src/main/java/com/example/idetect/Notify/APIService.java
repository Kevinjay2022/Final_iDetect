package com.example.idetect.Notify;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAboH-i_Y:APA91bEbzNcCFGo9TCW0ILqPn3zaN4YsguFuotebckPyVGiurRZvwfTPLS943fmu3Feb-OWQyxEDTdGdhgaIsmT6cOSZ1yuZl8wTF38rPvfz4avoHjl4ytK9y5DNqLainP0ivdC1JUTu" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
