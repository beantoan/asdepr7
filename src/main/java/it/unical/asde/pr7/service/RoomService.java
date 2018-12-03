package it.unical.asde.pr7.service;

import it.unical.asde.pr7.entity.Room;
import it.unical.asde.pr7.entity.RoomEvent;
import it.unical.asde.pr7.entity.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface RoomService {
    List<RoomEvent> findAllAvailable();

    RoomEvent createRoom(User user, String title);

    RoomEvent joinRoom(User user, Long roomId);

    Page<Room> findAchievements(User user, Integer page);

    RoomEvent leaveRoom(User user, Long roomId);
}
