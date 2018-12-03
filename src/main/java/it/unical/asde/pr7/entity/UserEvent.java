package it.unical.asde.pr7.entity;

public class UserEvent {
    private Long id;
    private String username;
    private String fullName;
    private int winCount;
    private int drawnCount;
    private int loseCount;
    private Long currentRoomId;

    public UserEvent() {
    }

    public UserEvent(String username, Long currentRoomId) {
        this.username = username;
        this.currentRoomId = currentRoomId;
    }

    public UserEvent(User user) {
        if (user != null) {
            this.id = user.getId();
            this.username = user.getUsername();
            this.fullName = user.getFullName();
            this.winCount = user.getWinCount();
            this.drawnCount = user.getDrawnCount();
            this.loseCount = user.getLoseCount();
            this.currentRoomId = user.getCurrentRoomId();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public int getWinCount() {
        return winCount;
    }

    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }

    public int getDrawnCount() {
        return drawnCount;
    }

    public void setDrawnCount(int drawnCount) {
        this.drawnCount = drawnCount;
    }

    public int getLoseCount() {
        return loseCount;
    }

    public void setLoseCount(int loseCount) {
        this.loseCount = loseCount;
    }

    public Long getCurrentRoomId() {
        return currentRoomId;
    }

    public void setCurrentRoomId(Long currentRoomId) {
        this.currentRoomId = currentRoomId;
    }
}
