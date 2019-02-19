package com.kobyakov.githubrepos.retrofit;

import com.kobyakov.githubrepos.model.User;
import com.kobyakov.githubrepos.model.Repository;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GithubReposClient {

    @GET("/users/{name}/repos")
    Single<List<Repository>> getRepositories(
            @Path("name") String githubUsername
    );

    @GET("/users/{name}")
    Single<Response<User>> getUserData(
            @Path("name") String githubUsername
    );
}