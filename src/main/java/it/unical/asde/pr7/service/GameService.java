package it.unical.asde.pr7.service;

import it.unical.asde.pr7.entity.Turn;
import it.unical.asde.pr7.entity.User;

public interface GameService {
    String initState();

    Turn doTurn(User user, Turn turn);
}
