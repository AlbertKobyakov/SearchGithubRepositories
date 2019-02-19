package com.kobyakov.githubrepos.model;

import java.util.List;

public class UserAndRepositoryHolder {
    private List<Repository> repositories;
    private User user;

    public List<Repository> getRepositories() {
        return repositories;
    }

    public void setRepositories(List<Repository> repositories) {
        this.repositories = repositories;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserAndRepositoryHolder{" +
                "repositories=" + repositories +
                ", user=" + user +
                '}';
    }
}
