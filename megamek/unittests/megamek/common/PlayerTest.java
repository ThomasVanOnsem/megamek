package megamek.common;

import junit.framework.TestCase;
import megamek.client.ui.swing.util.PlayerColour;
import megamek.common.icons.Camouflage;
import megamek.common.options.GameOptions;
import megamek.common.options.OptionsConstants;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(JUnit4.class)
public class PlayerTest {

    private Player player;
    private Player rival_player;
    private Player team_player;
    private Game game;
    private Entity mockedTank;
    private Game mockedGame;
    private GameOptions mockedOptions;
    private Crew mockedCrew;
    private VTOL mockedVTOL;


    @Before
    public void setup(){
        game = new Game();
        player = new Player(1, "player_name");
        rival_player = new Player(2, "rival_player");
        team_player = new Player(3, "team_player");
        player.setGame(game);
        rival_player.setGame(game);
        team_player.setGame(game);
        Entity tank = new Tank();
        tank.setOwner(player);
        game.addEntity(tank);

        mockedTank = mock(Tank.class);
        mockedGame = mock(Game.class);
        mockedOptions = mock(GameOptions.class);
        mockedCrew = mock(Crew.class);
        mockedVTOL = mock(VTOL.class);
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
        Game game2 = new Game();
        Player player2 = new Player(2, "player_name_2");
        game2.addPlayer(2, player2);
        game2.addEntity(new Tank());
        TestCase.assertFalse(player2.hasTAG());

        Entity tank2 = new Tank();
        tank2.setOwner(player);
        game2.addEntity(tank2);
        TestCase.assertFalse(player2.hasTAG());

        Game game3 = new Game();
        Player player3 = new Player(3, "player_name_3");
        player3.setGame(game3);
        when(mockedTank.hasTAG()).thenReturn(true);
        when(mockedTank.getOwner()).thenReturn(player3);
        game3.addEntity(mockedTank);
        TestCase.assertTrue(player3.hasTAG());

        TestCase.assertEquals(0, player.getAirborneVTOL().size());
        Player player4 = new Player(4, "player_4");
        player4.setGame(mockedGame);
        List<Entity> lst = new ArrayList<>();
        lst.add(mockedVTOL);
        when(mockedVTOL.getMovementMode()).thenReturn(EntityMovementMode.WIGE);
        when(mockedVTOL.getElevation()).thenReturn(10);
        when(mockedVTOL.getOwner()).thenReturn(player4);
        when(mockedGame.getEntitiesVector()).thenReturn(lst);
        TestCase.assertEquals(1, player4.getAirborneVTOL().size());

        player.setRanking(500);
        TestCase.assertEquals(500, player.getRanking());
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

        TestCase.assertEquals(0, player.getBV());

        Entity tank = new Tank();
        tank.setOwner(player);
        game.addEntity(tank);
        TestCase.assertEquals(0, player.getBV());

        player.setInitialBV();
        TestCase.assertEquals(0, player.getBV());
        player.increaseInitialBV(5);
        TestCase.assertEquals(5, player.getInitialBV());

        TestCase.assertEquals(0, player.getFledBV());
        Player player1 = new Player(2, "player_2");
        Vector<Entity> fled = new Vector<>();
        fled.add(mockedTank);
        when(mockedGame.getRetreatedEntities()).thenReturn(fled.elements());
        when(mockedTank.getOwner()).thenReturn(player1);
        when(mockedTank.calculateBattleValue()).thenReturn(100);
        player1.setGame(mockedGame);
        TestCase.assertEquals(100, player1.getFledBV());
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

        Player player_2 = new Player(2, "player_2");
        when(mockedGame.getEntitiesVector()).thenReturn(null);
        player_2.setGame(mockedGame);
        TestCase.assertEquals(0, player_2.getTurnInitBonus());
        when(mockedTank.getOwner()).thenReturn(player_2);
        when(mockedGame.getOptions()).thenReturn(mockedOptions);
        when(mockedOptions.booleanOption(OptionsConstants.ADVANCED_TACOPS_MOBILE_HQS)).thenReturn(true);
        when(mockedTank.getHQIniBonus()).thenReturn(10);
        when(mockedTank.getQuirkIniBonus()).thenReturn(20);
        List<Entity> lst = new ArrayList<>();
        lst.add(mockedTank);
        when(mockedGame.getEntitiesVector()).thenReturn(lst);
        TestCase.assertEquals(30, player_2.getTurnInitBonus());

        player.setGame(null);
        TestCase.assertEquals(0, player.getCommandBonus());
        player.setGame(game);
        TestCase.assertEquals(0, player.getCommandBonus());

        Player player_3 = new Player(3, "player_3");
        when(mockedTank.getOwner()).thenReturn(player_3);
        when(mockedTank.isDeployed()).thenReturn(true);
        when(mockedTank.isOffBoard()).thenReturn(false);
        when(mockedTank.getCrew()).thenReturn(mockedCrew);
        when(mockedCrew.isActive()).thenReturn(true);
        when(mockedCrew.getCommandBonus()).thenReturn(10);
        when(mockedCrew.hasActiveTechOfficer()).thenReturn(true);
        when(mockedTank.isCaptured()).thenReturn(false);
        when(mockedOptions.booleanOption(OptionsConstants.RPG_COMMAND_INIT)).thenReturn(true);
        player_3.setGame(mockedGame);

        TestCase.assertEquals(12, player_3.getCommandBonus());

    }
}
