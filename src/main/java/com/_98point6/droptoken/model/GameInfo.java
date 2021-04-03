package com._98point6.droptoken.model;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameInfo {
    private List<String> players;
    private String winner;
    private GameState state;
    private List<Move> movesPerformed;
    private int[][] grid;
    private int playerTurn = 0;
    private Map<String, Integer> playerToNumber;
    private Map<Integer, String> numberToPlayer;

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("players", players)
                .append("winner", winner)
                .append("state", state)
                .append("playerTurn", playerTurn)
                .append("grid", grid)
                .append("movesPerformed", movesPerformed)
                .append("playerNumber", playerToNumber)
                .toString();
    }

    public GameInfo(List<String> players) {
        this.players = players;
        this.grid = new int[4][4];
        movesPerformed = new ArrayList<>();
        playerToNumber = new HashMap<>();
        numberToPlayer = new HashMap<>();
        playerToNumber.put(players.get(0), 1);
        numberToPlayer.put(1, players.get(0));
        playerToNumber.put(players.get(1), 2);
        numberToPlayer.put(2, players.get(1));
    }

    public List<String> getPlayers() {
        return players;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public List<Move> getMovesPerformed() {
        return movesPerformed;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public void setMovesPerformed(List<Move> movesPerformed) {
        this.movesPerformed = movesPerformed;
    }

    public int[][] getGrid() {
        return grid;
    }

    public void setGrid(int[][] grid) {
        this.grid = grid;
    }

    public Map<String, Integer> getPlayerToNumber() {
        return playerToNumber;
    }

    public Map<Integer, String> getNumberToPlayer() {
        return numberToPlayer;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(int playerTurn) {
        this.playerTurn = playerTurn;
    }
}
