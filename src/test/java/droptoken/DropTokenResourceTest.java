//package droptoken;
//
//import com._98point6.droptoken.DropTokenResource;
//import com._98point6.droptoken.model.CreateGameRequest;
//import com._98point6.droptoken.model.PostMoveRequest;
//import org.glassfish.jersey.server.ResourceConfig;
//import org.glassfish.jersey.test.JerseyTest;
//import org.junit.Test;
//
//import javax.ws.rs.client.Entity;
//import javax.ws.rs.core.Application;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.UUID;
//
//import static org.junit.Assert.assertEquals;
//
//
//public class DropTokenResourceTest extends JerseyTest {
//
//    private static final String BASE_URL = "/drop_token";
//
//    @Override
//    protected Application configure() {
//        return new ResourceConfig(DropTokenResource.class);
//    }
//
//    @Test
//    public void getGames() {
//        Response response = target(BASE_URL).request().get();
//        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
//    }
//
//    @Test
//    public void createGame() {
//        List<String> players = new ArrayList<>();
//        players.add("Will");
//        players.add("Smith");
//        CreateGameRequest gameRequest = new CreateGameRequest.Builder().columns(4).rows(4).build();
//        Response response = target(BASE_URL).request()
//                .post(Entity.entity(gameRequest, MediaType.APPLICATION_JSON_TYPE));
//        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
//    }
//
//    @Test
//    public void createGameWithInvalidRowsAndColumns() {
//        List<String> players = new ArrayList<>();
//        players.add("Will");
//        players.add("Smith");
//        CreateGameRequest gameRequest = new CreateGameRequest.Builder().columns(5).rows(7).build();
//        Response response = target(BASE_URL).request()
//                .post(Entity.entity(gameRequest, MediaType.APPLICATION_JSON_TYPE));
//        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
//    }
//
//    @Test
//    public void createGameWithInvalidNumberOfPlayers() {
//        List<String> players = new ArrayList<>();
//        players.add("Will");
//        players.add("Smith");
//        players.add("John");
//        CreateGameRequest gameRequest = new CreateGameRequest.Builder().columns(5).rows(7).build();
//        Response response = target(BASE_URL).request()
//                .post(Entity.entity(gameRequest, MediaType.APPLICATION_JSON_TYPE));
//        assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());
//    }
//
//    @Test
//    public void getGameStatus() {
//        String gameId = UUID.randomUUID().toString();
//        CreateGameRequest gameRequest = new CreateGameRequest.Builder().columns(5).rows(7).build();
//        Response response = target(BASE_URL + "/" + gameId).request().get();
//        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
//    }
//
//    @Test
//    public void testPostMove() {
//        String gameId = UUID.randomUUID().toString();
//        String playerId = "John";
//        String postMoveUrl = BASE_URL + "/" + gameId + "/" + playerId;
//        PostMoveRequest moveRequest = new PostMoveRequest.Builder().column(2).build();
//        Response response = target(postMoveUrl).request()
//                .post(Entity.entity(moveRequest, MediaType.APPLICATION_JSON_TYPE));
//        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
//    }
//
//    @Test
//    public void testPostInvalidMove() {
//        String gameId = UUID.randomUUID().toString();
//        String playerId = "John";
//        String postMoveUrl = BASE_URL + "/" + gameId + "/" + playerId;
//        PostMoveRequest moveRequest = new PostMoveRequest.Builder().column(5).build();
//        Response response = target(postMoveUrl).request()
//                .post(Entity.entity(moveRequest, MediaType.APPLICATION_JSON_TYPE));
//        assertEquals(422, response.getStatus());
//    }
//
//    @Test
//    public void testPlayerQuit() {
//        String gameId = UUID.randomUUID().toString();
//        String playerId = "John";
//        String postMoveUrl = BASE_URL + "/" + gameId + "/" + playerId;
//        Response response = target(postMoveUrl).request().delete();
//        assertEquals(Response.Status.ACCEPTED.getStatusCode(), response.getStatus());
//    }
//
//    @Test
//    public void testGetMoves() {
//        String gameId = UUID.randomUUID().toString();
//        String getMovesUrl = BASE_URL + "/" + gameId + "/moves";
//        Response response = target(getMovesUrl).request().get();
//        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
//    }
//
//    @Test
//    public void testGetMovesWithStartAndUntil() {
//        String gameId = UUID.randomUUID().toString();
//        String getMovesUrl = BASE_URL + "/" + gameId + "/moves?start=0&end=4";
//        Response response = target(getMovesUrl).request().get();
//        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
//    }
//
//    @Test
//    public void testGetMovesWithMoveId() {
//        String gameId = UUID.randomUUID().toString();
//        String moveId = "4";
//        String getMoveUrl = BASE_URL + "/" + gameId + "/moves" + moveId;
//        Response response = target(getMoveUrl).request().get();
//        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
//    }
//}