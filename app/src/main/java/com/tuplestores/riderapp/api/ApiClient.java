package com.tuplestores.riderapp.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/*Created By Ajish Dharman on 28-August-2019
 *
 *
 */public class ApiClient {

    public static final String BASE_URL = "http://34.222.136.32:8080/";
    private static Retrofit retrofit = null;


    public static Retrofit getClient() {

        if (retrofit==null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
