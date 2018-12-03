package it.unical.asde.pr7.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.validator.constraints.Length;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(
        name = "users"
)
public final class User extends BaseEntity {

    @Column(
            name = "username",
            unique = true,
            nullable = false
    )
    @NotNull
    private String username;

    @Column(
            name = "`password`",
            nullable = false
    )
    @NotNull
    @Length(min = 6)
    @JsonIgnore
    private String password;

    @Column(
            name = "full_name",
            nullable = false
    )
    @NotNull
    private String fullName;

    @Column(
            name = "win_count",
            columnDefinition = "INT UNSIGNED",
            nullable = false
    )
    @ColumnDefault("0")
    private int winCount;

    @Column(
            name = "lose_count",
            columnDefinition = "INT UNSIGNED",
            nullable = false
    )
    @ColumnDefault("0")
    private int loseCount;

    @Column(
            name = "drawn_count",
            columnDefinition = "INT UNSIGNED",
            nullable = false
    )
    @ColumnDefault("0")
    private int drawnCount;

    @Column(
            name = "current_room_id",
            columnDefinition = "BIGINT UNSIGNED"
    )
    private Long currentRoomId;

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public int getLoseCount() {
        return loseCount;
    }

    public void setLoseCount(int loseCount) {
        this.loseCount = loseCount;
    }

    public int getDrawnCount() {
        return drawnCount;
    }

    public void setDrawnCount(int drawnCount) {
        this.drawnCount = drawnCount;
    }

    public Long getCurrentRoomId() {
        return currentRoomId;
    }

    public void setCurrentRoomId(Long currentRoomId) {
        this.currentRoomId = currentRoomId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return getId().equals(user.getId());
    }
}
