package it.unical.asde.pr7.service;

import it.unical.asde.pr7.entity.User;
import it.unical.asde.pr7.entity.UserEvent;
import it.unical.asde.pr7.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private SimpUserRegistry simpUserRegistry;

    @Override
    public User findByUsername(String username) {
        return this.userRepository.findByUsername(username);
    }

    @Override
    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        this.userRepository.save(user);

        return user;
    }

    @Override
    public User setCurrentRoomId(String username, Long roomId) {
        User user = this.userRepository.findByUsername(username);
        user.setCurrentRoomId(roomId);

        this.userRepository.save(user);

        return user;
    }

    @Override
    public List<UserEvent> getOnlineUsers() {

        List<UserEvent> userEvents = new ArrayList<>();

        List<String> usernames = new ArrayList<>();

        for (SimpUser user : this.simpUserRegistry.getUsers()) {
            usernames.add(user.getName());
        }

        List<User> users = this.userRepository.findAllByUsernames(usernames);

        for (User user : users) {
            userEvents.add(new UserEvent(user.getUsername(), user.getCurrentRoomId()));
        }

        return userEvents;
    }

}
