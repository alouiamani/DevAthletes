package org.gymify.controllers;


import org.gymify.entities.User;

public interface UserAwareController {
    void setUser(User user);
}
