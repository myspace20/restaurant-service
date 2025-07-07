package com.bytebites.restaurant_service.models;

import java.util.List;

public class AppUser {
    private final Long id;
    private final List<String> roles;

    public AppUser(Long id, List<String> roles) {
        this.id = id;
        this.roles = roles;
    }

    public Long getId() { return id; }
    public List<String> getRoles() { return roles; }

    @Override
    public String toString() {
        return "AppUser{" +
                "id=" + id +
                ", roles=" + roles +
                '}';
    }

}