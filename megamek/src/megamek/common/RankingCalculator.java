package megamek.common;

import megamek.common.event.GameEndEvent;
import megamek.common.event.GameListenerAdapter;

/**
 * Static methods for updating the rankings of players in a competitive context
 */
public class RankingCalculator {

    private RankingCalculator() {}

    /**
     * Update the ranking of the player during the end of the game.
     * @param e
     */
    public static void updateRankings(GameEndEvent e) {
        // To be implemented
    }

    /**
     * Notify the ranking system the client left the game without it ending.
     */
    public static void notifyClientDisconnect(){
        // To be implemented
    }

    public static class RankingGameListener extends GameListenerAdapter {
        @Override
        public void gameEnd(GameEndEvent e) {
            RankingCalculator.updateRankings(e);
        }
    }

}
