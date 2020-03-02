package com.elbaz.eliran.washmylaundry.emails;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Eliran Elbaz on 02-Mar-20.
 */
public interface MailGunApi {
    @FormUrlEncoded
    @POST("messages")
    Call<ResponseBody> sendEmail(
            @Field("from") String from,
            @Field("to") String to,
            @Field("subject") String subject,
            @Field("text") String text
    );
}
