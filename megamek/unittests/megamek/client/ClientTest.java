package megamek.client;

import junit.framework.TestCase;
import megamek.common.net.Packet;
import org.junit.Test;

public class ClientTest {

    String name = "TestClient";
    String host = "localhost";
    int port = 3456;
    String password = "pass";

    public Client makeClient() {
        return new Client(name, host, port, password);
    }

    @Test
    public void testClient() {
        Client c = makeClient();
        TestCase.assertEquals(name, c.getName());
        TestCase.assertEquals(host, c.getHost());
        TestCase.assertEquals(port, c.getPort());
        TestCase.assertEquals(password, c.getPassword());
    }

    @Test
    public void testServerGreetingPacket() {
        Client c = makeClient();
        c.setTesting(true);
        c.handlePacket(new Packet(Packet.COMMAND_SERVER_GREETING));
        TestCase.assertEquals(2, c.getTestingOutPackets().size());
        Packet result = c.getTestingOutPackets().get(0);
        TestCase.assertEquals(Packet.COMMAND_CLIENT_NAME, result.getCommand());
        TestCase.assertEquals(name, result.getObject(0));
        TestCase.assertEquals(password, result.getObject(1));
    }

    @Test
    public void testSendWithoutTestingMode() {
        Client c = makeClient();
        c.handlePacket(new Packet(Packet.COMMAND_SERVER_GREETING));
        TestCase.assertTrue( c.getTestingOutPackets().isEmpty());
    }

}
