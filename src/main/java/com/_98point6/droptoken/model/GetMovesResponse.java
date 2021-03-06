package com._98point6.droptoken.model;

import com.google.common.base.Preconditions;

import java.util.List;

/**
 *
 */
public class GetMovesResponse {
    private List<Move> moves;

    public GetMovesResponse() {}

    private GetMovesResponse(Builder builder) {
        this.moves = Preconditions.checkNotNull(builder.moves);
    }

    public List<Move> getMoves() {
        return moves;
    }

    public static class Builder {
        private List<Move> moves;

        public Builder moves(List<Move> moves) {
            this.moves = moves;
            return this;
        }

        public Builder fromPrototype(GetMovesResponse prototype) {
            moves = prototype.moves;
            return this;
        }

        public GetMovesResponse build() {
            return new GetMovesResponse(this);
        }
    }
}
