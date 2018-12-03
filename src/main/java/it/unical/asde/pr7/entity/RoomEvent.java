package it.unical.asde.pr7.entity;

public class RoomEvent {
    private String eventCreator;
    private String firstPlayUser;
    private Long roomId;
    private String roomTitle;
    private String roomState;
    private UserEvent firstUser;
    private UserEvent secondUser;

    public RoomEvent() {
    }

    public RoomEvent(String eventCreator, String firstPlayUser, Room room, UserEvent firstUser, UserEvent secondUser) {
        this.eventCreator = eventCreator;
        this.firstPlayUser = firstPlayUser;
        this.roomId = room.getId();
        this.roomTitle = room.getTitle();
        this.roomState = room.getState();
        this.firstUser = firstUser;
        this.secondUser = secondUser;
    }

    public String getEventCreator() {
        return eventCreator;
    }

    public void setEventCreator(String eventCreator) {
        this.eventCreator = eventCreator;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomTitle() {
        return roomTitle;
    }

    public void setRoomTitle(String roomTitle) {
        this.roomTitle = roomTitle;
    }

    public String getRoomState() {
        return roomState;
    }

    public void setRoomState(String roomState) {
        this.roomState = roomState;
    }

    public String getFirstPlayUser() {
        return firstPlayUser;
    }

    public void setFirstPlayUser(String firstPlayUser) {
        this.firstPlayUser = firstPlayUser;
    }

    public UserEvent getFirstUser() {
        return firstUser;
    }

    public void setFirstUser(UserEvent firstUser) {
        this.firstUser = firstUser;
    }

    public UserEvent getSecondUser() {
        return secondUser;
    }

    public void setSecondUser(UserEvent secondUser) {
        this.secondUser = secondUser;
    }
}
