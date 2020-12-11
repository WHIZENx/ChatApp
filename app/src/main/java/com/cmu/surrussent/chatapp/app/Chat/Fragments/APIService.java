package com.cmu.surrussent.chatapp.app.Chat.Fragments;

import com.cmu.surrussent.chatapp.app.Chat.Notifications.MyResponse;
import com.cmu.surrussent.chatapp.app.Chat.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAABvPbGZ4:APA91bGJ4QbwrBbp2Y_C2hmR5dnDEELtQ9TdLNa8X3OzrK13B0ywtw4KXW_Zp9ZPPQiFYe3l2w-4cnlILladez0a9KgKNN-l2pTrDz27WGhwJ-r9xXvknnx0hn7Sw4z1qbQOa0aJTIWA"
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
