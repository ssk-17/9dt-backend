package com._98point6.droptoken.model;

import com.google.common.base.Preconditions;

import java.util.List;

//TODO: This class can be removed if we can use customized Jackson Filter
public class GameStatusResponseWithoutWinner {
    private List<String> players;
    private GameState state;

    public GameStatusResponseWithoutWinner() {
    }

    private GameStatusResponseWithoutWinner(Builder builder) {
        this.players = Preconditions.checkNotNull(builder.players);
        this.state = Preconditions.checkNotNull(builder.state);
    }

    public List<String> getPlayers() {
        return players;
    }

    public GameState getState() {
        return state;
    }

    public static class Builder {
        private List<String> players;
        private GameState state;

        public Builder players(List<String> players) {
            this.players = players;
            return this;
        }

        public Builder state(GameState state) {
            this.state = state;
            return this;
        }

        public Builder fromPrototype(GameStatusResponseWithoutWinner prototype) {
            players = prototype.players;
            state = prototype.state;
            return this;
        }

        public GameStatusResponseWithoutWinner build() {
            return new GameStatusResponseWithoutWinner(this);
        }
    }
}
