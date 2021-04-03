package com._98point6.droptoken.service;

import com._98point6.droptoken.model.*;

import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class to handle game related requests/queries.
 */
public class DropTokenServiceImpl implements DropTokenService {

    Map<String, GameInfo> gameRecords = new HashMap<String, GameInfo>();
    private static final int GRID_SIZE = 4;

    public GetGamesResponse getActiveGames() {
        List<String> activeGameIds = gameRecords.entrySet().stream().filter(e ->
                GameState.IN_PROGRESS.equals(e.getValue().getState()))
                .map(Map.Entry::getKey).collect(Collectors.toList());

        return new GetGamesResponse.Builder().games(activeGameIds).build();
    }

    public CreateGameResponse createNewGame(CreateGameRequest request) {
        String gameId = UUID.randomUUID().toString();

        GameInfo gameInfo = new GameInfo(request.getPlayers());
        gameInfo.setState(GameState.IN_PROGRESS);
        gameRecords.put(gameId, gameInfo);

        return new CreateGameResponse.Builder()
                .gameId(gameId).build();
    }

    @Override
    public GameStatusResponse getGameStatus(String gameId) {
        GameInfo gameInfo = gameRecords.get(gameId);
        System.out.println(gameId + "info: " + gameInfo);
        if (gameInfo == null || gameInfo.getMovesPerformed() == null
                || gameInfo.getMovesPerformed().isEmpty()) return null;

        return new GameStatusResponse.Builder().state(gameInfo.getState()).
                winner(gameInfo.getWinner()).players(gameInfo.getPlayers()).build();
    }

    @Override
    public Response postMove(String gameId, String playerId, int column) {
        GameInfo gameInfo = gameRecords.get(gameId);
        if (gameInfo == null || !gameInfo.getPlayers().contains(playerId))
            return notFoundResponse("game/player is not found");

        int playerNumber = gameInfo.getPlayerToNumber().get(playerId);
        if (gameInfo.getPlayerTurn() == 0)
            gameInfo.setPlayerTurn(getOpponent(playerNumber));
        else if (playerNumber != gameInfo.getPlayerTurn()) return Response.status(Response.Status.CONFLICT)
                .entity("Its not " + playerId + " turn").build();

        int[][] grid = gameInfo.getGrid();
        if (!checkIfMoveValidAndPost(gameInfo, grid, column, playerId, playerNumber))
            return badRequestResponse("Illegal/Invalid move on column:" + column);

        gameInfo.setPlayerTurn(getOpponent(playerNumber)); //set opponent as next turn

        findAndState(gameInfo); // calculate state of game after posting move

        return Response.ok(new PostMoveResponse.Builder()
                .moveLink("{" + gameId + "}" + "/moves/" + (gameInfo.getMovesPerformed().size() - 1))
                .build()).build();
    }

    @Override
    public Response getMoves(String gameId, Integer start, Integer until) {
        GameInfo gameInfo = gameRecords.get(gameId);
        if (gameInfo == null || gameInfo.getMovesPerformed().isEmpty())
            return notFoundResponse("games/moves not found");
        List<Move> moves = gameInfo.getMovesPerformed();
        int movesSize = moves.size();
        if (until == null) until = movesSize - 1;
        if (start < 0 || until < 0 || start >= movesSize || until >= movesSize)
            return badRequestResponse("invalid start/until");

        List<Move> movesReturned = new ArrayList<>();
        for (int i = start; i <= until; i++)
            movesReturned.add(moves.get(i));

        return Response.ok(movesReturned).build();
    }

    @Override
    public Response getMove(String gameId, Integer moveId) {
        GameInfo gameInfo = gameRecords.get(gameId);
        if (gameInfo == null || gameInfo.getMovesPerformed().isEmpty())
            return notFoundResponse("games/moves not found");

        if (moveId < 0 || moveId >= gameInfo.getMovesPerformed().size())
            return badRequestResponse("invalid start/until");

        return Response.ok(gameInfo.getMovesPerformed().get(moveId)).build();
    }

    @Override
    public Response playerQuit(String gameId, String playerId) {
        GameInfo gameInfo = gameRecords.get(gameId);
        if (gameInfo == null || !gameInfo.getPlayers().contains(playerId))
            return notFoundResponse("game not found or player is not part of it");
        if (GameState.DONE.equals(gameInfo.getState()))
            return Response.status(Response.Status.GONE).build();

        gameInfo.setState(GameState.DONE);
        gameInfo.setWinner(gameInfo.getNumberToPlayer().get(
                getOpponent(gameInfo.getPlayerToNumber().get(playerId))));

        List<Move> moves = gameInfo.getMovesPerformed();
        moves.add(new Move.Builder().player(playerId).type(MoveType.QUIT).build());
        gameInfo.setMovesPerformed(moves);  // add quit move to the game

        return Response.accepted().build();
    }

    private void findAndState(GameInfo gameInfo) {
        int[][] grid = gameInfo.getGrid();

        if (checkIfPlayerWins(grid, 1)) {
            gameInfo.setWinner(gameInfo.getNumberToPlayer().get(1));
            gameInfo.setState(GameState.DONE);
        }
        if (checkIfPlayerWins(grid, 2)) {
            gameInfo.setWinner(gameInfo.getNumberToPlayer().get(2));
            gameInfo.setState(GameState.DONE);
        }
        //check for Draw
        int tokensOccupied = 0;
        for (int i = 0; i < GRID_SIZE; i++)
            for (int j = 0; j < GRID_SIZE; j++)
                if (grid[i][j] != 0) tokensOccupied++;
        if (tokensOccupied == 16)
            gameInfo.setState(GameState.DONE); // set Game state to done if its draw without setting winnner.
    }

    private boolean checkIfPlayerWins(int[][] grid, int playerNumber) {
        //check if any row is filled with player tokens
        for (int j = 0; j < GRID_SIZE; j++) {
            int playerTokensFilled = 0;
            for (int i = 0; i < GRID_SIZE; i++) {
                if (grid[i][j] == playerNumber) playerTokensFilled++;
            }
            if (playerTokensFilled == GRID_SIZE) return true;
        }
        //check if any column is filled with player tokens
        for (int i = 0; i < GRID_SIZE; i++) {
            int playerTokensFilled = 0;
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid[i][j] == playerNumber) playerTokensFilled++;
            }
            if (playerTokensFilled == GRID_SIZE) return true;
        }
        //check if any diagonal is filled
        int playerTokensFilled = 0;
        for (int i = 0; i < GRID_SIZE; i++) if (grid[i][i] == playerNumber) playerTokensFilled++;
        if (playerTokensFilled == GRID_SIZE) return true;

        playerTokensFilled = 0;
        for (int i = 0; i < GRID_SIZE; i++) if (grid[i][(GRID_SIZE - 1) - i] == playerNumber) playerTokensFilled++;

        return playerTokensFilled == GRID_SIZE;
    }

    private int getOpponent(int playerNumber) {
        return playerNumber == 1 ? 2 : 1;
    }

    private boolean checkIfMoveValidAndPost(GameInfo gameInfo, int[][] grid, int col,
                                            String playerId, int playerNumber) {

        if (gameInfo.getState() == GameState.DONE) return false; // game is already done

        int rowAvailable = 0;
        while (rowAvailable < GRID_SIZE && grid[rowAvailable][col] != 0) rowAvailable++;

        if (rowAvailable > (GRID_SIZE - 1)) return false; // column is full so move is not valid

        grid[rowAvailable][col] = playerNumber; // post move
        gameInfo.setGrid(grid);

        List<Move> moves = gameInfo.getMovesPerformed();// add move to list
        moves.add(new Move.Builder().type(MoveType.MOVE).column(col).player(playerId).build());
        gameInfo.setMovesPerformed(moves);

        return true;
    }

    private Response badRequestResponse(String message) {
        return Response.status(Response.Status.BAD_REQUEST).entity(message).build();
    }

    private Response notFoundResponse(String message) {
        return Response.status(Response.Status.NOT_FOUND).entity(message).build();
    }
}