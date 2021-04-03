package com._98point6.droptoken.service;

import com._98point6.droptoken.model.CreateGameRequest;
import com._98point6.droptoken.model.CreateGameResponse;
import com._98point6.droptoken.model.GameStatusResponse;
import com._98point6.droptoken.model.GetGamesResponse;

import javax.ws.rs.core.Response;

public interface DropTokenService {

    GetGamesResponse getActiveGames();

    CreateGameResponse createNewGame(CreateGameRequest request);

    GameStatusResponse getGameStatus(String gameId);

    Response postMove(String gameId, String playerId, int column);

    Response getMoves(String gameId, Integer start, Integer until);

    Response getMove(String gameId, Integer moveId);

    Response playerQuit(String gameId, String playerId);
}