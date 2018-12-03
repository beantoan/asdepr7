package it.unical.asde.pr7.controller;

import it.unical.asde.pr7.entity.RoomEvent;
import it.unical.asde.pr7.entity.Turn;
import it.unical.asde.pr7.entity.UserEvent;
import it.unical.asde.pr7.service.GameService;
import it.unical.asde.pr7.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class GameController extends BaseController {

    @Autowired
    RoomService roomService;

    @Autowired
    GameService gameService;

    @MessageMapping("/me")
    @SendToUser("/queue/me")
    @SendTo("/event/userConnected")
    public UserEvent me() {
        return new UserEvent(this.loggedInUser());
    }

    @MessageMapping("/rooms")
    @SendToUser("/queue/rooms")
    public List<RoomEvent> rooms() {
        return this.roomService.findAllAvailable();
    }

    @MessageMapping("/roomChanged")
    @SendTo("/event/roomChanged")
    public List<RoomEvent> roomChanged() {
        return this.roomService.findAllAvailable();
    }

    @MessageMapping("/createRoom")
    @SendToUser("/queue/createRoom")
    public RoomEvent createRoom(String title) {
        return this.roomService.createRoom(this.loggedInUser(), title);
    }

    @MessageMapping("/joinRoom/{roomId}")
    @SendTo("/event/joinRoom/{roomId}")
    public RoomEvent joinRoom(@DestinationVariable Long roomId) {
        return this.roomService.joinRoom(this.loggedInUser(), roomId);
    }

    @MessageMapping("/leaveRoom/{roomId}")
    @SendTo("/event/leaveRoom/{roomId}")
    public RoomEvent leaveRoom(@DestinationVariable Long roomId) {
        return this.roomService.leaveRoom(this.loggedInUser(), roomId);
    }

    @MessageMapping("/turn/{roomId}")
    @SendTo("/event/turn/{roomId}")
    public Turn turn(@DestinationVariable Long roomId, Turn turn) {
        return this.gameService.doTurn(this.loggedInUser(), turn);
    }

    @MessageMapping("/users")
    @SendToUser("/queue/users")
    public List<UserEvent> users() {
        return this.userService.getOnlineUsers();
    }
}
