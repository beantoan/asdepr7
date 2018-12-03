package it.unical.asde.pr7.service;

import it.unical.asde.pr7.entity.User;
import it.unical.asde.pr7.entity.UserEvent;

import java.util.List;

public interface UserService {
    User findByUsername(String username);

    User createUser(User user);

    User setCurrentRoomId(String username, Long roomId);

    List<UserEvent> getOnlineUsers();
}
