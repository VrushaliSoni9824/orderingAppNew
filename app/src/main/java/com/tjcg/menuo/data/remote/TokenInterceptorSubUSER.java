package com.tjcg.menuo.data.remote;

import com.tjcg.menuo.utils.Constants;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptorSubUSER implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {

        //rewrite the request to add bearer token
        String token =Constants.getBearrToken_subUSER();
        Request newRequest=chain.request().newBuilder()
                .header("Authorization","Bearer "+ token)
                .build();

        return chain.proceed(newRequest);
    }
}