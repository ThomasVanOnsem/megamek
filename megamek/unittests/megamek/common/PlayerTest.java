package megamek.common;

import junit.framework.TestCase;
import megamek.client.ui.swing.util.PlayerColour;
import megamek.common.icons.Camouflage;
import megamek.common.options.GameOptions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mockito;

import java.util.Vector;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(JUnit4.class)
public class PlayerTest {

    private Player player;
    private Player rival_player;
    private Player team_player;
    private Game game;


    @Before
    public void setup(){
        game = new Game();
        player = new Player(1, "player_name");
        rival_player = new Player(2, "rival_player");
        team_player = new Player(3, "team_player");
        player.setGame(game);
        rival_player.setGame(game);
        team_player.setGame(game);

        Entity tankMock = Mockito.mock(Tank.class);
        Mockito.when(tankMock.calculateBattleValue()).thenReturn(100);
    }

    @Test
    public void testMinefields(){
        TestCase.assertEquals(0, player.getMinefields().size());
        Minefield minefield = Minefield.createMinefield(new Coords(1,1), 1, 0, 1);
        player.addMinefield(minefield);
        TestCase.assertEquals(1, player.getMinefields().size());
        TestCase.assertEquals(minefield, player.getMinefields().get(0));

        Minefield minefield1 = Minefield.createMinefield(new Coords(2,2), 1, 0, 1);
        Minefield minefield2 = Minefield.createMinefield(new Coords(3,3), 1, 0, 1);
        Vector<Minefield> minefields = new Vector<>();
        minefields.add(minefield1);
        minefields.add(minefield2);
        player.addMinefields(minefields);
        TestCase.assertEquals(3, player.getMinefields().size());

        player.removeMinefield(minefield1);
        TestCase.assertEquals(2, player.getMinefields().size());
        player.removeMinefield(minefield2);
        TestCase.assertEquals(1, player.getMinefields().size());
        TestCase.assertEquals(minefield, player.getMinefields().get(0));
        player.addMinefield(minefield1);
        TestCase.assertTrue(player.containsMinefield(minefield1));
        TestCase.assertFalse(player.containsMinefield(minefield2));
        player.removeMinefields();
        TestCase.assertEquals(0, player.getMinefields().size());
        TestCase.assertFalse(player.hasMinefields());

        player.setNbrMFConventional(1);
        TestCase.assertEquals(1, player.getNbrMFConventional());
        player.setNbrMFActive(2);
        TestCase.assertEquals(2, player.getNbrMFActive());
        player.setNbrMFCommand(3);
        TestCase.assertEquals(3, player.getNbrMFCommand());
        player.setNbrMFInferno(4);
        TestCase.assertEquals(4, player.getNbrMFInferno());
        player.setNbrMFVibra(5);
        TestCase.assertEquals(5, player.getNbrMFVibra());

        //TODO try to break code? because no checks ever done
    }

    @Test
    public void testPlayerCamoAndColour(){
        TestCase.assertEquals(new Camouflage(Camouflage.COLOUR_CAMOUFLAGE, PlayerColour.BLUE.name()), player.getCamouflage());
        player.setCamoCategory(Camouflage.NO_CAMOUFLAGE);
        player.setCamoFileName(PlayerColour.BROWN.name());
        TestCase.assertEquals(new Camouflage(Camouflage.NO_CAMOUFLAGE, PlayerColour.BROWN.name()), player.getCamouflage());
        TestCase.assertEquals(PlayerColour.BLUE, player.getColour());
        player.setColour(PlayerColour.BROWN);
        TestCase.assertEquals(PlayerColour.BROWN, player.getColour());
        // TODO catch exception
    }

    @Test
    public void testPlayerBasicInfo(){
        TestCase.assertEquals("player_name", player.getName());
        player.setName("player_1");
        TestCase.assertEquals("player_1", player.getName());
        TestCase.assertEquals(1, player.getId());
        TestCase.assertEquals(player.getId(), player.hashCode());

        TestCase.assertEquals(0, player.getTeam());
        player.setTeam(3);
        TestCase.assertEquals(3, player.getTeam());

        TestCase.assertFalse(player.isAllowingTeamChange());
        player.setAllowTeamChange(true);
        TestCase.assertTrue(player.isAllowingTeamChange());
        player.setAllowTeamChange(false);
        TestCase.assertFalse(player.isAllowingTeamChange());

        TestCase.assertEquals("Player 1 (player_1)", player.toString());
    }

    @Test
    public void testObserver(){
        TestCase.assertFalse(player.isObserver());
        player.setObserver(true);
        TestCase.assertTrue(player.isObserver());
        game.setPhase(IGame.Phase.PHASE_VICTORY);
        TestCase.assertFalse(player.isObserver());
        game.setPhase(IGame.Phase.PHASE_UNKNOWN);
        player.setTeam(0);
        game.addPlayer(1, player);
        player.setObserver(false);
        TestCase.assertFalse(player.isObserver());
        TestCase.assertFalse(player.canSeeAll());
        player.setSeeAll(true);
        TestCase.assertTrue(player.getSeeAll());
        player.setObserver(true);
        TestCase.assertTrue(player.canSeeAll());
    }


    @Test
    public void testVaria(){
        TestCase.assertFalse(player.isDone());
        player.setDone(true);
        TestCase.assertTrue(player.isDone());

        TestCase.assertFalse(player.isGhost());
        player.setGhost(true);
        TestCase.assertTrue(player.isGhost());

        TestCase.assertFalse(player.hasTAG());
        //TODO rest of hasTAG
    }

    @Test
    public void testPlayerPosition(){
        TestCase.assertEquals(Board.START_ANY, player.getStartingPos());
        player.setStartingPos(1);
        TestCase.assertEquals(1, player.getStartingPos());
        player.setStartingPos(21);
        player.adjustStartingPosForReinforcements();
        TestCase.assertEquals(11, player.getStartingPos());
        player.setStartingPos(Board.START_CENTER);
        player.adjustStartingPosForReinforcements();
        TestCase.assertEquals(Board.START_ANY, player.getStartingPos());
    }

    @Test
    public void testPlayerEnemy(){
        TestCase.assertTrue(player.isEnemyOf(null));
        TestCase.assertFalse(player.isEnemyOf(player));
        player.setTeam(4);
        rival_player.setTeam(player.getTeam()+1);
        TestCase.assertTrue(player.isEnemyOf(rival_player));
        team_player.setTeam(player.getTeam());
        TestCase.assertFalse(player.isEnemyOf(team_player));
    }

    @Test
    public void testEquals(){
        TestCase.assertEquals(player, player);
        TestCase.assertFalse(player.equals(null));
        Player other_player = new Player(player.getId(), "other_player");
        TestCase.assertEquals(other_player, player);
        TestCase.assertFalse(player.equals(rival_player));
    }

    @Test
    public void testPlayerArtyAutoHitHexes(){
        TestCase.assertEquals(0, player.getArtyAutoHitHexes().size());
        Vector<Coords> coords = new Vector<>();
        coords.add(new Coords(1,1));
        coords.add(new Coords(2,2));
        player.setArtyAutoHitHexes(coords);
        TestCase.assertEquals(2, player.getArtyAutoHitHexes().size());
        player.addArtyAutoHitHex(new Coords(3,3));
        TestCase.assertEquals(3, player.getArtyAutoHitHexes().size());
        player.removeArtyAutoHitHexes();
        TestCase.assertEquals(0, player.getArtyAutoHitHexes().size());
    }

    @Test
    public void testPlayerBattle(){
        TestCase.assertFalse(player.admitsDefeat());
        player.setAdmitsDefeat(true);
        TestCase.assertTrue(player.admitsDefeat());
        player.setAdmitsDefeat(false);
        TestCase.assertFalse(player.admitsDefeat());

        TestCase.assertEquals(player.getBV(), 0);

        Entity tank = new Tank();
        tank.setOwner(player);
        game.addEntity(tank);
        TestCase.assertEquals(0, player.getBV());

        player.setInitialBV();
        TestCase.assertEquals(0, player.getBV());
        player.increaseInitialBV(5);
        TestCase.assertEquals(5, player.getInitialBV());

        TestCase.assertEquals(0, player.getFledBV());

    }

    @Test
    public void testPlayerBonus(){
        TestCase.assertEquals(0, player.getInitCompensationBonus());
        player.setInitCompensationBonus(10);
        TestCase.assertEquals(10, player.getInitCompensationBonus());
        TestCase.assertEquals(0, player.getConstantInitBonus());
        player.setConstantInitBonus(4);
        TestCase.assertEquals(4, player.getConstantInitBonus());

        player.setGame(null);
        TestCase.assertEquals(0, player.getTurnInitBonus());
        player.setGame(game);
        TestCase.assertEquals(0, player.getTurnInitBonus());
        Entity tank = new Tank();
        tank.setOwner(player);
        game.addEntity(tank);
        game.setOptions(new GameOptions());

        player.setGame(null);
        TestCase.assertEquals(0, player.getCommandBonus());
        player.setGame(game);
        TestCase.assertEquals(0, player.getCommandBonus());

    }
}
