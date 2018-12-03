package it.unical.asde.pr7.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;

@Entity
@Table(
        name = "rooms"
)
@EntityListeners(AuditingEntityListener.class)
public final class Room extends BaseEntity {
    @Column(
            name = "title",
            nullable = false
    )
    @NotNull
    private String title;

    @Column(
            name = "is_drawn",
            columnDefinition = "TINYINT UNSIGNED"
    )
    @ColumnDefault("0")
    private boolean isDrawn = false;

    @Column(
            name = "state",
            columnDefinition = "TEXT"
    )
    private String state;

    @Column(
            name = "last_turn_user_id",
            columnDefinition = "BIGINT UNSIGNED"
    )
    @Nullable
    private Long lastTurnUserId;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "started_at")
    @Temporal(TemporalType.TIMESTAMP)
    @Nullable
    private Date startedAt;

    @Column(name = "finished_at")
    @Temporal(TemporalType.TIMESTAMP)
    @Nullable
    private Date finishedAt;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "first_user_id", nullable = false)
    @JsonIgnore
    private User firstUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "second_user_id", nullable = true)
    @JsonIgnore
    private User secondUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "lose_user_id", nullable = true)
    @JsonIgnore
    private User loseUser;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "win_user_id", nullable = true)
    @JsonIgnore
    private User winUser;

    public Room() {
    }

    public Room(@NotNull String title, @NotNull User firstUser) {
        this.title = title;
        this.firstUser = firstUser;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDrawn() {
        return isDrawn;
    }

    public void setDrawn(boolean drawn) {
        isDrawn = drawn;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Nullable
    public Long getLastTurnUserId() {
        return lastTurnUserId;
    }

    public void setLastTurnUserId(@Nullable Long lastTurnUserId) {
        this.lastTurnUserId = lastTurnUserId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
    }

    public User getFirstUser() {
        return firstUser;
    }

    public void setFirstUser(User firstUser) {
        this.firstUser = firstUser;
    }

    public User getSecondUser() {
        return secondUser;
    }

    public void setSecondUser(User secondUser) {
        this.secondUser = secondUser;
    }

    public User getLoseUser() {
        return loseUser;
    }

    public void setLoseUser(User loseUser) {
        this.loseUser = loseUser;
    }

    public User getWinUser() {
        return winUser;
    }

    public void setWinUser(User winUser) {
        this.winUser = winUser;
    }
}
