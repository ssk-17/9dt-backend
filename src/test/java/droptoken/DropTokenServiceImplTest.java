package droptoken;

import com._98point6.droptoken.model.*;
import com._98point6.droptoken.service.DropTokenService;
import com._98point6.droptoken.service.DropTokenServiceImpl;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class DropTokenServiceImplTest {

    DropTokenService service;

    @Before
    public void init() {
        service = new DropTokenServiceImpl();
    }

    @Test
    public void testCreateGame() {
        CreateGameRequest gameRequest = new CreateGameRequest.Builder().players(Arrays.asList("Sai", "Teja")).columns(4).rows(4).build();
        CreateGameResponse response = service.createNewGame(gameRequest);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getGameId());
    }

    @Test
    public void testGetGameStatusWithNoGame() {
        GameStatusResponse response = service.getGameStatus("ABC");
        Assert.assertNull(response);
    }

    @Test
    public void testGetGameStatus() {
        CreateGameRequest gameRequest = new CreateGameRequest.Builder().players(Arrays.asList("Sai", "Teja")).columns(4).rows(4).build();
        CreateGameResponse createGameResponse = service.createNewGame(gameRequest);
        Assert.assertNotNull(createGameResponse);
        service.postMove(createGameResponse.getGameId(), "Sai", 0);
        GameStatusResponse response = service.getGameStatus(createGameResponse.getGameId());
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getPlayers().get(0).equalsIgnoreCase("Sai"));
        Assert.assertNotNull(response.getPlayers().get(1).equalsIgnoreCase("Teja"));
        Assert.assertNull(response.getWinner().isPresent() ? response.getWinner().get() : null);

    }

    @Test
    public void testGetGameStatusWithSuccessWinnerCase() {
        CreateGameRequest gameRequest = new CreateGameRequest.Builder().players(Arrays.asList("Sai", "Teja")).columns(4).rows(4).build();
        CreateGameResponse createGameResponse = service.createNewGame(gameRequest);
        Assert.assertNotNull(createGameResponse);
        service.postMove(createGameResponse.getGameId(), "Sai", 0);
        service.postMove(createGameResponse.getGameId(), "Teja", 1);
        service.postMove(createGameResponse.getGameId(), "Sai", 0);
        service.postMove(createGameResponse.getGameId(), "Teja", 1);
        service.postMove(createGameResponse.getGameId(), "Sai", 0);
        service.postMove(createGameResponse.getGameId(), "Teja", 1);
        service.postMove(createGameResponse.getGameId(), "Sai", 0);
        GameStatusResponse response = service.getGameStatus(createGameResponse.getGameId());
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getPlayers().get(0).equalsIgnoreCase("Sai"));
        Assert.assertNotNull(response.getPlayers().get(1).equalsIgnoreCase("Teja"));
        Assert.assertEquals("Sai", response.getWinner().isPresent() ? response.getWinner().get() : null);

    }

    @Test
    public void testPOSTMovesWithMoreEvenAfterWin() {
        CreateGameRequest gameRequest = new CreateGameRequest.Builder().players(Arrays.asList("Sai", "Teja")).columns(4).rows(4).build();
        CreateGameResponse createGameResponse = service.createNewGame(gameRequest);
        Assert.assertNotNull(createGameResponse);
        service.postMove(createGameResponse.getGameId(), "Sai", 0);
        service.postMove(createGameResponse.getGameId(), "Teja", 1);
        service.postMove(createGameResponse.getGameId(), "Sai", 0);
        service.postMove(createGameResponse.getGameId(), "Teja", 1);
        service.postMove(createGameResponse.getGameId(), "Sai", 0);
        service.postMove(createGameResponse.getGameId(), "Teja", 1);
        service.postMove(createGameResponse.getGameId(), "Sai", 0);
        Response response =
                service.postMove(createGameResponse.getGameId(), "Teja", 1);
        Assert.assertEquals(400, response.getStatus());

    }

    @Test
    public void testQuit() {
        CreateGameRequest gameRequest = new CreateGameRequest.Builder().players(Arrays.asList("Sai", "Teja")).columns(4).rows(4).build();
        CreateGameResponse createGameResponse = service.createNewGame(gameRequest);
        Assert.assertNotNull(createGameResponse);
        service.postMove(createGameResponse.getGameId(), "Sai", 0);
        service.postMove(createGameResponse.getGameId(), "Teja", 1);
        service.postMove(createGameResponse.getGameId(), "Sai", 0);
        Response response = service.playerQuit(createGameResponse.getGameId(), "Teja");
        Assert.assertEquals(202, response.getStatus());
        GameStatusResponse gameStatusResponse = service.getGameStatus(createGameResponse.getGameId());
        Assert.assertNotNull(gameStatusResponse);
        Assert.assertEquals("Sai", gameStatusResponse.getWinner().get());
    }

    @Test
    public void testGetMove() {
        CreateGameRequest gameRequest = new CreateGameRequest.Builder().players(Arrays.asList("Sai", "Teja")).columns(4).rows(4).build();
        CreateGameResponse createGameResponse = service.createNewGame(gameRequest);
        Assert.assertNotNull(createGameResponse);
        service.postMove(createGameResponse.getGameId(), "Sai", 0);
        service.postMove(createGameResponse.getGameId(), "Teja", 1);
        service.postMove(createGameResponse.getGameId(), "Sai", 0);

        Response response = service.getMove(createGameResponse.getGameId(), 0);
        Assert.assertNotNull(response);
        Assert.assertNotNull(response.getStatus());
        Assert.assertEquals("Sai", ((Move) response.getEntity()).getPlayer());
    }

    @Test
    public void testInvalidTurnInPostMove() {
        CreateGameRequest gameRequest = new CreateGameRequest.Builder().players(Arrays.asList("Sai", "Teja")).columns(4).rows(4).build();
        CreateGameResponse createGameResponse = service.createNewGame(gameRequest);
        Assert.assertNotNull(createGameResponse);
        service.postMove(createGameResponse.getGameId(), "Sai", 0);
        Response response = service.postMove(createGameResponse.getGameId(), "Sai", 1);

        Assert.assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());
        Assert.assertEquals("Its not Sai turn", response.getEntity());
    }

    @Test
    public void testPostMoveWithInvalidPlayerAndGame() {
        CreateGameRequest gameRequest = new CreateGameRequest.Builder().players(Arrays.asList("Sai", "Teja")).columns(4).rows(4).build();
        CreateGameResponse createGameResponse = service.createNewGame(gameRequest);
        Assert.assertNotNull(createGameResponse);
        service.postMove(createGameResponse.getGameId(), "Sai", 0);
        Response response = service.postMove(createGameResponse.getGameId(), "Tejas", 1);
        Response response1 = service.postMove(UUID.randomUUID().toString(), "Teja", 1);

        Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        Assert.assertEquals("game/player is not found", response.getEntity());
        Assert.assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response1.getStatus());
        Assert.assertEquals("game/player is not found", response1.getEntity());
    }

    @Test
    public void testPostMoveWithIllegalMove() {
        CreateGameRequest gameRequest = new CreateGameRequest.Builder().players(Arrays.asList("Sai", "Teja")).columns(4).rows(4).build();
        CreateGameResponse createGameResponse = service.createNewGame(gameRequest);
        Assert.assertNotNull(createGameResponse);
        service.postMove(createGameResponse.getGameId(), "Sai", 0);
        service.postMove(createGameResponse.getGameId(), "Teja", 0);
        service.postMove(createGameResponse.getGameId(), "Sai", 0);
        service.postMove(createGameResponse.getGameId(), "Teja", 0);
        Response response = service.postMove(createGameResponse.getGameId(), "Sai", 0);

        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        Assert.assertEquals("Illegal/Invalid move on column:0", response.getEntity());

    }

    @Test
    public void testGetMoves() {
        CreateGameRequest gameRequest = new CreateGameRequest.Builder().players(Arrays.asList("Sai", "Teja")).columns(4).rows(4).build();
        CreateGameResponse createGameResponse = service.createNewGame(gameRequest);
        Assert.assertNotNull(createGameResponse);
        service.postMove(createGameResponse.getGameId(), "Sai", 0);
        service.postMove(createGameResponse.getGameId(), "Teja", 1);
        service.postMove(createGameResponse.getGameId(), "Sai", 0);

        Response response = service.getMoves(createGameResponse.getGameId(), 0, 2);
        Assert.assertNotNull(response);
        Assert.assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        List<Move> list = (ArrayList<Move>) response.getEntity();
        Assert.assertEquals(3, list.size());
    }

    @Test
    public void testGetMovesWithInvalidStart() {
        CreateGameRequest gameRequest = new CreateGameRequest.Builder().players(Arrays.asList("Sai", "Teja")).columns(4).rows(4).build();
        CreateGameResponse createGameResponse = service.createNewGame(gameRequest);
        Assert.assertNotNull(createGameResponse);
        service.postMove(createGameResponse.getGameId(), "Sai", 0);
        service.postMove(createGameResponse.getGameId(), "Teja", 1);
        service.postMove(createGameResponse.getGameId(), "Sai", 0);

        Response response = service.getMoves(createGameResponse.getGameId(), 03, 2);
        Assert.assertNotNull(response);
        Assert.assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
        Assert.assertEquals("invalid start/until", response.getEntity());
    }

    @Test
    public void testQuitWithGameAlreadyDone() {
        CreateGameRequest gameRequest = new CreateGameRequest.Builder().players(Arrays.asList("Sai", "Teja")).columns(4).rows(4).build();
        CreateGameResponse createGameResponse = service.createNewGame(gameRequest);
        Assert.assertNotNull(createGameResponse);
        service.postMove(createGameResponse.getGameId(), "Sai", 0);
        service.postMove(createGameResponse.getGameId(), "Teja", 1);
        service.postMove(createGameResponse.getGameId(), "Sai", 0);
        service.playerQuit(createGameResponse.getGameId(), "Teja");
        Response response = service.playerQuit(createGameResponse.getGameId(), "Sai");

        Assert.assertEquals(Response.Status.GONE.getStatusCode(), response.getStatus());

    }
}