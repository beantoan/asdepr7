package it.unical.asde.pr7.listener;

import it.unical.asde.pr7.entity.UserEvent;
import it.unical.asde.pr7.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.security.Principal;

@Component
public class SessionDisconnectedEventListener implements ApplicationListener<SessionDisconnectEvent> {

    private static final Logger logger = LoggerFactory.getLogger(SessionDisconnectedEventListener.class);

    @Autowired
    private SimpMessagingTemplate template;

    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent sessionDisconnectEvent) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(sessionDisconnectEvent.getMessage());

        Principal user = headerAccessor.getUser();

        if (user != null) {
            this.userService.setCurrentRoomId(user.getName(), null);

            UserEvent userEvent = new UserEvent(user.getName(), null);

            this.template.convertAndSend("/event/userConnected", userEvent);
        }
    }
}