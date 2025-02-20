package Gimify.Pi.Controllers;

import Gimify.Pi.entities.User;

public interface UserAwareController {
    void setUser(User user);
}
