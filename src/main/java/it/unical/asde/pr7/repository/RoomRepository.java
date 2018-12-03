package it.unical.asde.pr7.repository;

import it.unical.asde.pr7.entity.Room;
import it.unical.asde.pr7.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends CrudRepository<Room, Long> {
    @Query("SELECT r FROM Room r WHERE r.finishedAt IS NULL")
    List<Room> findAllAvailable();

    @Query("SELECT r FROM Room r " +
            "WHERE (r.firstUser = ?1 OR r.secondUser = ?1) AND r.finishedAt IS NOT NULL " +
            "ORDER BY r.finishedAt DESC")
    Page<Room> findAchievements(User user, Pageable pageable);
}