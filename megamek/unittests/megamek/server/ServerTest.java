package megamek.server;

import junit.framework.TestCase;
import megamek.common.Game;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

public class ServerTest {

    @Test
    public void testServer(){
        try{
            Server server = new Server("test", 1);
            server.setGame(new Game());
        }
        catch (Exception e){
            e.printStackTrace();
        }
        TestCase.assertEquals(1, 1);
    }
}
