package com.kobyakov.githubrepos.repository;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.kobyakov.githubrepos.model.UserAndRepositoryHolder;
import com.kobyakov.githubrepos.utils.Util;
import com.kobyakov.githubrepos.model.APIError;
import com.kobyakov.githubrepos.model.Repository;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UserInfoRepository {
    private final String TAG = getClass().getSimpleName();
    private MutableLiveData<APIError> apiErrorLiveData;
    private UserAndRepositoryHolder userAndRepositoryHolder;
    private MutableLiveData<UserAndRepositoryHolder> userAndRepositoryHolderLiveData;

    public UserInfoRepository() {
        apiErrorLiveData = new MutableLiveData<>();
        userAndRepositoryHolderLiveData = new MutableLiveData<>();
    }

    public void searchUserAndRepositories(final String usernameGithub) {
        Disposable disposable = Util.initRetrofitRx().getUserData(usernameGithub)
                .flatMap(userDataResponse -> {
                    if (userDataResponse.isSuccessful() && userDataResponse.body() != null) {
                        userAndRepositoryHolder = new UserAndRepositoryHolder();
                        userAndRepositoryHolder.setUser(userDataResponse.body());

                        return Util.initRetrofitRx().getRepositories(usernameGithub);
                    } else if (userDataResponse.body() != null && userDataResponse.body().getPublicRepos() == 0) {
                        List<Repository> repositories = new ArrayList<>();
                        return Single.just(repositories);
                    } else {
                        Type typeError = new TypeToken<APIError>() {
                        }.getType();
                        APIError apiError = new Gson().fromJson(Objects.requireNonNull(userDataResponse.errorBody()).string(), typeError);

                        return Single.error(new Exception(apiError.getMessage()));
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        repositoriesResponse -> {
                            if (repositoriesResponse != null && repositoriesResponse.size() > 0) {
                                Log.d(TAG, repositoriesResponse + "");
                                userAndRepositoryHolder.setRepositories(repositoriesResponse);
                            }
                            userAndRepositoryHolderLiveData.setValue(userAndRepositoryHolder);
                        },
                        error -> {
                            APIError apiError = getAPIErrorWithCustomMessage(error.getLocalizedMessage(), usernameGithub);
                            apiErrorLiveData.setValue(apiError);
                        }
                );
    }

    public MutableLiveData<UserAndRepositoryHolder> getUserAndRepositoryHolderLiveData() {
        return userAndRepositoryHolderLiveData;
    }

    public MutableLiveData<APIError> getApiErrorLiveData() {
        return apiErrorLiveData;
    }

    private APIError getAPIErrorWithCustomMessage(String message, String userName) {
        APIError apiError = new APIError();
        apiError.setMessage(message);
        apiError.setRequest(userName);
        return apiError;
    }
}