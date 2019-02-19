package com.kobyakov.githubrepos.utils;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.kobyakov.githubrepos.App;
import com.kobyakov.githubrepos.retrofit.GithubReposClient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Util {

    private static final int CACHE_SIZE = 10 * 1024 * 1024; //10mb

    public static GithubReposClient initRetrofitRx() {
        Cache cache = new Cache(App.INSTANCE.getCacheDir(), CACHE_SIZE);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cache(cache)
                .build();

        return new Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build().create(GithubReposClient.class);
    }

    public static String getStrDateByCustomPattern(String strDate) {
        Date date = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            date = simpleDateFormat.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new SimpleDateFormat("dd/MM/yyyy hh:mm", Locale.ENGLISH).format(date);
    }
}