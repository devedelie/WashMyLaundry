package com.elbaz.eliran.washmylaundry.emails;

import android.util.Base64;

import com.elbaz.eliran.washmylaundry.BuildConfig;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MailGunRetrofitClient {


    private static final String BASE_URL = "https://api.mailgun.net/v3/sandboxd0388cd87fd2436db14c7ed633da0967.mailgun.org/";

    private static final String API_USERNAME = "api";

    private static final String API_PASSWORD = BuildConfig.MAILGUN_API_KEY;

    private static final String AUTH = "Basic " + Base64.encodeToString((API_USERNAME+":"+API_PASSWORD).getBytes(), Base64.NO_WRAP);

    private static MailGunRetrofitClient mInstance;
    private Retrofit retrofit;

    private MailGunRetrofitClient() {
        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(
                        new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Request original = chain.request();

                                //Adding basic auth
                                Request.Builder requestBuilder = original.newBuilder()
                                        .header("Authorization", AUTH)
                                        .method(original.method(), original.body());

                                Request request = requestBuilder.build();
                                return chain.proceed(request);
                            }
                        })
                .build();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okClient)
                .build();
    }

    public static synchronized MailGunRetrofitClient getInstance() {
        if (mInstance == null) {
            mInstance = new MailGunRetrofitClient();
        }
        return mInstance;
    }

    public Retrofit getClient() {
        return retrofit;
    }

    public MailGunApi getApi() {
        return retrofit.create(MailGunApi.class);
    }
}