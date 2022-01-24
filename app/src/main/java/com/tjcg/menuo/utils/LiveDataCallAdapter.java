package com.tjcg.menuo.utils;


import android.util.Log;

import androidx.lifecycle.LiveData;

import com.tjcg.menuo.data.repository.ApiResponse;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;

public class LiveDataCallAdapter<R> implements CallAdapter<R, LiveData<ApiResponse<R>>> {

    private final Type responseType;

    public LiveDataCallAdapter(Type responseType) {
        this.responseType = responseType;
    }

    @NotNull
    @Override
    public Type responseType() {
        return responseType;
    }

    @Override
    public LiveData<ApiResponse<R>> adapt(@NotNull final Call<R> call) {
        return new LiveData<ApiResponse<R>>(){
            @Override
            protected void onActive() {
                super.onActive();
                final ApiResponse apiResponse = new ApiResponse();
                if(!call.isExecuted()){
                    call.enqueue(new Callback<R>() {
                        @Override
                        public void onResponse(@NotNull Call<R> call, @NotNull Response<R> response) {
                            postValue(apiResponse.create(response));
                        }

                        @Override
                        public void onFailure(@NotNull Call<R> call, @NotNull Throwable t) {
                            Log.e("tag"," =  = = 1==   " + t.getMessage());
                            Log.e("tag"," =  = = =2=   " + t.getLocalizedMessage());
                            postValue(apiResponse.create(t));
                        }
                    });
                }

            }
        };
    }

}







