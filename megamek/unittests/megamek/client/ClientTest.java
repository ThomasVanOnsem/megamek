package megamek.client;

import junit.framework.TestCase;
import org.junit.Test;

public class ClientTest {

    @Test
    public void testClient() {
        String name = "TestClient";
        String host = "localhost";
        int port = 3456;
        String password = "pass";
        Client c = new Client(name, host, port, password);
        TestCase.assertEquals(name, c.getName());
        TestCase.assertEquals(host, c.getHost());
        TestCase.assertEquals(port, c.getPort());
        TestCase.assertEquals(password, c.getPassword());
    }
}
