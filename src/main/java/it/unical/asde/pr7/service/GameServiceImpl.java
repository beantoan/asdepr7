package it.unical.asde.pr7.service;

import com.google.gson.Gson;
import it.unical.asde.pr7.entity.Room;
import it.unical.asde.pr7.entity.Turn;
import it.unical.asde.pr7.entity.User;
import it.unical.asde.pr7.entity.UserEvent;
import it.unical.asde.pr7.repository.RoomRepository;
import it.unical.asde.pr7.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;

@Service
public class GameServiceImpl implements GameService {
    @Autowired
    RoomRepository roomRepository;

    @Autowired
    UserRepository userRepository;

    private int maxRow = 6;
    private int maxCol = 7;

    @Override
    public String initState() {
        Long[][] state = new Long[maxRow][maxCol];

        for (int i = 0; i < maxRow; i++) {
            for (int j = 0; j < maxCol; j++) {
                state[i][j] = 0L;
            }
        }

        Gson gson = new Gson();

        return gson.toJson(state);
    }

    @Override
    public Turn doTurn(User user, Turn turn) {
        turn.setCreator(user.getUsername());

        if (turn.getRow() > maxRow - 1 || turn.getRow() < 0 ||
                turn.getCol() > maxCol - 1 || turn.getCol() < 0) {
            turn.setError("Your turn is not valid. Please try again.");
            return turn;
        }

        Room room = this.roomRepository.findById(user.getCurrentRoomId()).get();

        if (room.getStartedAt() != null && room.getFinishedAt() == null) {
            Gson gson = new Gson();

            Long[][] state = gson.fromJson(room.getState(), Long[][].class);

            if (state[turn.getRow()][turn.getCol()] == 0L &&
                    (turn.getRow() == maxRow - 1 || state[turn.getRow() + 1][turn.getCol()] != 0L)) {
                state[turn.getRow()][turn.getCol()] = user.getId();

                room.setLastTurnUserId(user.getId());
                room.setState(gson.toJson(state));

                Long winUserId = this.checkState(state);

                if (winUserId != null) {
                    User firstUser = room.getFirstUser();
                    User secondUser = room.getSecondUser();

                    room.setFinishedAt(Calendar.getInstance().getTime());

                    if (winUserId == 0L) {
                        room.setDrawn(true);

                        firstUser.setDrawnCount(firstUser.getDrawnCount() + 1);
                        secondUser.setDrawnCount(secondUser.getDrawnCount() + 1);

                        turn.setDrawn(true);
                    } else {
                        if (winUserId.equals(firstUser.getId())) {
                            room.setWinUser(firstUser);
                            room.setLoseUser(secondUser);

                            turn.setWinUser(new UserEvent(firstUser));

                            firstUser.setWinCount(firstUser.getWinCount() + 1);
                            secondUser.setLoseCount(secondUser.getLoseCount() + 1);
                        } else {
                            room.setWinUser(secondUser);
                            room.setLoseUser(firstUser);

                            turn.setWinUser(new UserEvent(secondUser));

                            firstUser.setLoseCount(firstUser.getLoseCount() + 1);
                            secondUser.setWinCount(secondUser.getWinCount() + 1);
                        }
                    }

                    this.userRepository.save(firstUser);
                    this.userRepository.save(secondUser);

                    turn.setFirstUser(firstUser);
                    turn.setSecondUser(secondUser);
                }

                this.roomRepository.save(room);
            } else {
                turn.setError("Your turn is not valid. Please try again.");
            }
        } else {
            turn.setError("Your room is not available or finished. Try to create new room or join other room.");
        }

        return turn;
    }

    /**
     * Check the state to find the win user
     * - By horizontal line
     * - By vertical line
     * - By diagonal line. Includes left and right
     *
     * @param state
     * @return User id of the winner. O if it is drawn
     */
    private Long checkState(Long[][] state) {
        int countEmptyCell = 0;

        for (int i = maxRow - 1; i >= 0; i--) {
            for (int j = 0; j < maxCol; j++) {
                if (state[i][j] != 0L) {
                    /**
                     * Horizontal
                     */
                    if (j + 3 < maxCol &&
                            state[i][j].equals(state[i][j + 1]) &&
                            state[i][j].equals(state[i][j + 2]) &&
                            state[i][j].equals(state[i][j + 3])) {
                        return state[i][j];
                    }

                    /**
                     * Vertical
                     */
                    if (i - 3 >= 0 &&
                            state[i][j].equals(state[i - 1][j]) &&
                            state[i][j].equals(state[i - 2][j]) &&
                            state[i][j].equals(state[i - 3][j])) {
                        return state[i][j];
                    }

                    /**
                     * Diagonal
                     */
                    if (i + 3 < maxRow && j + 3 < maxCol &&
                            state[i][j].equals(state[i + 1][j + 1]) &&
                            state[i][j].equals(state[i + 2][j + 2]) &&
                            state[i][j].equals(state[i + 3][j + 3])) {
                        return state[i][j];
                    }

                    if (i - 3 >= 0 && j - 3 >= 0 &&
                            state[i][j].equals(state[i - 1][j - 1]) &&
                            state[i][j].equals(state[i - 2][j - 2]) &&
                            state[i][j].equals(state[i - 3][j - 3])) {
                        return state[i][j];
                    }

                    if (i + 3 < maxRow && j - 3 >= 0 &&
                            state[i][j].equals(state[i + 1][j - 1]) &&
                            state[i][j].equals(state[i + 2][j - 2]) &&
                            state[i][j].equals(state[i + 3][j - 3])) {
                        return state[i][j];
                    }

                    if (i - 3 >= 0 && j + 3 < maxCol &&
                            state[i][j].equals(state[i - 1][j + 1]) &&
                            state[i][j].equals(state[i - 2][j + 2]) &&
                            state[i][j].equals(state[i - 3][j + 3])) {
                        return state[i][j];
                    }
                } else {
                    countEmptyCell++;
                }
            }
        }

        return countEmptyCell == 0 ? 0L : null;
    }
}
