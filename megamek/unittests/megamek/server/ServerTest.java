package megamek.server;

import junit.framework.TestCase;
import megamek.common.*;
import megamek.common.event.GameListener;
import megamek.common.net.Packet;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class ServerTest {

    Server makeCompetitiveServer() throws IOException {
        return new Server("test", 1235, false, "", true);
    }

    @Test
    public void testServerConstructor() throws IOException {
        Server server = makeCompetitiveServer();
        TestCase.assertTrue(server.isCompetitive());
        server.die();
    }

    @Test
    public void testSetGame() throws IOException {
        Server server = makeCompetitiveServer();
        Game game = new Game();
        Player player = new Player(0, "Max");

        // Add mech
        Entity entity = new MechWarrior();
        entity.setOwner(player);
        game.addEntity(entity);

        // Add aero
        entity = new Aero();
        entity.setOwner(player);
        game.addEntity(entity);

        // Add aero without owner, will be orphan entity and not counted
        game.addEntity(new Aero());

        // Add tank
        entity = new Tank();
        entity.setOwner(player);
        game.addEntity(entity);

        // Set game
        server.setGame(game);

        // 1 mech, 1 aero and 1 tank
        TestCase.assertEquals(3, server.getGame().getNoOfEntities());

        // Ranking calculator was added because of competitiveness
        List<GameListener> listeners = server.getGame().getGameListeners();
        TestCase.assertEquals(1, listeners.size());
        boolean isRankingGameListener = listeners.get(0) instanceof RankingCalculator.RankingGameListener;
        TestCase.assertTrue(isRankingGameListener);

        server.die();
    }

    Packet makeClientNamePacket() {
        Object[] object = new Object[2];
        object[0] = "max";
        object[1] = "pass";
        return new Packet(Packet.COMMAND_CLIENT_NAME, object);
    }

    @Test
    public void testReceivePlayerName() throws IOException {
        Server server = makeCompetitiveServer();
        server.receivePlayerName(makeClientNamePacket(), 0);
        server.die();
    }

    @Test
    public void testDisconnected() throws IOException {
        Server server = makeCompetitiveServer();
        Player player = new Player(0, "Max");
        server.getGame().addPlayer(0, player);
        Player player2 = new Player(1, "Thomas");
        server.getGame().addPlayer(1, player2);

        server.disconnected(player);
        server.disconnected(player2);
        server.die();
    }

}
