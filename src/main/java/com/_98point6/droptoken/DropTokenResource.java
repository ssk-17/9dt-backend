package com._98point6.droptoken;

import com._98point6.droptoken.model.*;
import com._98point6.droptoken.service.DropTokenService;
import com._98point6.droptoken.service.DropTokenServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 *
 */
@Path("/drop_token")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DropTokenResource {
    private static final Logger logger = LoggerFactory.getLogger(DropTokenResource.class);

    private DropTokenService service = new DropTokenServiceImpl();

    public DropTokenResource() {
    }

    @GET
    public Response getGames() {
        logger.info("getActiveGames");
        return Response.ok(service.getActiveGames()).build();
    }

    @POST
    public Response createNewGame(@Valid @NotNull CreateGameRequest request) {
        logger.info("createNewGame request={}", request);

        if (!checkValidNewGameRequest(request)) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }

        return Response.ok(service.createNewGame(request)).build();
    }

    @Path("/{id}")
    @GET
    public Response getGameStatus(@PathParam("id") String gameId) {
        logger.info("getGameStatus for gameId = {}", gameId);
        GameStatusResponse gameStatusResponse = service.getGameStatus(gameId);

        if (gameStatusResponse == null)
            return Response.status(Response.Status.NOT_FOUND).build();

        if (GameState.IN_PROGRESS.equals(gameStatusResponse.getState()))
            return Response.ok(new GameStatusResponseWithoutWinner.Builder().state(GameState.IN_PROGRESS)
                    .players(gameStatusResponse.getPlayers()).build()).build();

        return Response.ok(gameStatusResponse).build();
    }

    @Path("/{id}/{playerId}")
    @POST
    public Response postMove(@PathParam("id") String gameId, @PathParam("playerId") String playerId,
                             @Valid @NotNull PostMoveRequest request) {
        logger.info("postMove gameId={}, playerId={}, move={}", gameId, playerId, request);

        return service.postMove(gameId, playerId, request.getColumn());
    }

    @Path("/{id}/{playerId}")
    @DELETE
    public Response playerQuit(@PathParam("id") String gameId, @PathParam("playerId") String playerId) {
        logger.info("playerQuit gameId={}, playerId={}", gameId, playerId);
        return service.playerQuit(gameId, playerId);
    }

    @Path("/{id}/moves")
    @GET
    public Response getMoves(@PathParam("id") String gameId,
                             @QueryParam("start") @DefaultValue("0") Integer start,
                             @QueryParam("until") Integer until) {
        logger.info("getMoves gameId={}, start={}, until={}", gameId, start, until);
        return service.getMoves(gameId, start, until);
    }

    @Path("/{id}/moves/{moveId}")
    @GET
    public Response getMove(@PathParam("id") String gameId, @NotBlank @PathParam("moveId") String moveId) {
        logger.info("getMove gameId={}, moveId={}", gameId, moveId);
        if (!StringUtils.isNumeric(moveId))
            return Response.status(400).entity("moveId is not integer").build();

        return service.getMove(gameId, Integer.parseInt(moveId));
    }


    private boolean checkValidNewGameRequest(CreateGameRequest request) {
        return !request.getPlayers().get(0).isEmpty() && !request.getPlayers().get(1).isEmpty() &&
                !request.getPlayers().get(0).equals(request.getPlayers().get(1))
                && request.getRows() == 4 && request.getColumns() == 4;
    }
}
