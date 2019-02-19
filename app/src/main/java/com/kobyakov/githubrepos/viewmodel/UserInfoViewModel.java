package com.kobyakov.githubrepos.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.kobyakov.githubrepos.model.APIError;
import com.kobyakov.githubrepos.model.UserAndRepositoryHolder;
import com.kobyakov.githubrepos.repository.UserInfoRepository;

public class UserInfoViewModel extends ViewModel {
    private final String TAG = getClass().getSimpleName();
    private UserInfoRepository repository;

    private LiveData<APIError> apiErrorLiveData;
    private LiveData<UserAndRepositoryHolder> userAndRepositoryHolderLive;

    public UserInfoViewModel() {
        repository = new UserInfoRepository();
        apiErrorLiveData = repository.getApiErrorLiveData();
        userAndRepositoryHolderLive = repository.getUserAndRepositoryHolderLiveData();
    }

    public void searchRepositories(String usernameGithub) {
        repository.searchUserAndRepositories(usernameGithub);
    }

    public LiveData<APIError> getApiErrorLiveData() {
        return apiErrorLiveData;
    }

    public LiveData<UserAndRepositoryHolder> getUserAndRepositoryHolderLive() {
        return userAndRepositoryHolderLive;
    }
}