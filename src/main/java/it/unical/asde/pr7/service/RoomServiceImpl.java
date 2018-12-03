package it.unical.asde.pr7.service;

import it.unical.asde.pr7.entity.Room;
import it.unical.asde.pr7.entity.RoomEvent;
import it.unical.asde.pr7.entity.User;
import it.unical.asde.pr7.entity.UserEvent;
import it.unical.asde.pr7.repository.RoomRepository;
import it.unical.asde.pr7.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

@Service
public class RoomServiceImpl implements RoomService {
    @Autowired
    RoomRepository roomRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    GameService gameService;

    @Override
    public List<RoomEvent> findAllAvailable() {
        List<Room> rooms = this.roomRepository.findAllAvailable();

        List<RoomEvent> roomEvents = new ArrayList<>();

        for (Room room : rooms) {
            roomEvents.add(new RoomEvent(null, null, room, new UserEvent(room.getFirstUser()), new UserEvent(room.getSecondUser())));
        }

        return roomEvents;

    }

    @Override
    public RoomEvent createRoom(User user, String title) {
        Room room = new Room(title, user);

        room.setState(gameService.initState());

        this.roomRepository.save(room);

        user.setCurrentRoomId(room.getId());

        this.userRepository.save(user);

        return new RoomEvent(user.getUsername(), null, room, new UserEvent(user), new UserEvent());
    }

    @Override
    public RoomEvent joinRoom(User user, Long roomId) {
        Room room = this.roomRepository.findById(roomId).get();

        if (room.getFinishedAt() == null) {

            if (room.getSecondUser() == null && !room.getFirstUser().equals(user)) {
                room.setSecondUser(user);
            }

            if (room.getSecondUser() != null && room.getStartedAt() == null) {
                room.setStartedAt(Calendar.getInstance().getTime());
            }

            this.roomRepository.save(room);

            user.setCurrentRoomId(room.getId());

            this.userRepository.save(user);

            User firstUser = room.getFirstUser();
            User secondUser = room.getSecondUser();

            if (firstUser.equals(user)) {
                firstUser.setCurrentRoomId(room.getId());
            } else {
                if (secondUser != null) {
                    secondUser.setCurrentRoomId(room.getId());
                }
            }

            String firstPlayUser = null;

            if (firstUser.getCurrentRoomId() != null && secondUser != null && secondUser.getCurrentRoomId() != null) {
                if (room.getLastTurnUserId() == null) {
                    Random random = new Random();
                    firstPlayUser = random.nextBoolean() ? firstUser.getUsername() : secondUser.getUsername();
                } else {
                    firstPlayUser = room.getLastTurnUserId().equals(firstUser.getId()) ? secondUser.getUsername() : firstUser.getUsername();
                }
            }

            return new RoomEvent(user.getUsername(), firstPlayUser, room, new UserEvent(firstUser), new UserEvent(secondUser));
        }

        return null;
    }

    @Override
    public Page<Room> findAchievements(User user, Integer page) {
        PageRequest pageRequest = PageRequest.of(page == null ? 0 : page, 30);

        return this.roomRepository.findAchievements(user, pageRequest);
    }

    @Override
    public RoomEvent leaveRoom(User user, Long roomId) {
        user.setCurrentRoomId(null);

        this.userRepository.save(user);

        Room room = this.roomRepository.findById(roomId).get();

        return new RoomEvent(user.getUsername(), null, room, new UserEvent(room.getFirstUser()), new UserEvent(room.getSecondUser()));
    }
}
