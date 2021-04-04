package droptoken;

import com._98point6.droptoken.DropTokenResource;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

import static org.junit.Assert.assertEquals;


public class DropTokenResourceTest extends JerseyTest {

    private static final String BASE_URL = "/drop_token";

    @Override
    protected Application configure() {
        return new ResourceConfig(DropTokenResource.class);
    }

    @Test
    public void getGames() {
//        Response response = target(BASE_URL).request().get();
//        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }
}