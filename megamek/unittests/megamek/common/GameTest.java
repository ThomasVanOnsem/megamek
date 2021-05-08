package megamek.common;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class GameTest {

    private Game game;


    @Before
    public void setup(){
        game = new Game();
    }


    @Test
    public void testCase() {
        TestCase.assertEquals(1, 1);
    }

}
