package com._98point6.droptoken.model;

import com.google.common.base.Preconditions;

import java.util.Optional;

public class Move {
    private MoveType type;
    private String player;
    private Integer column;

    public Move() {
    }

    private Move(Builder builder) {
        this.type = Preconditions.checkNotNull(builder.type);
        this.player = Preconditions.checkNotNull(builder.player);
        this.column = builder.column;
    }


    public MoveType getType() {
        return type;
    }

    public String getPlayer() {
        return player;
    }

    public Optional<Integer> getColumn() {
        return Optional.ofNullable(column);
    }

    public static class Builder {
        private MoveType type;
        private String player;
        private Integer column;

        public Builder type(MoveType type) {
            this.type = type;
            return this;
        }

        public Builder player(String player) {
            this.player = player;
            return this;
        }

        public Builder column(Integer column) {
            this.column = column;
            return this;
        }

        public Builder fromPrototype(Move prototype) {
            type = prototype.type;
            player = prototype.player;
            column = prototype.column;
            return this;
        }

        public Move build() {
            return new Move(this);
        }
    }
}
