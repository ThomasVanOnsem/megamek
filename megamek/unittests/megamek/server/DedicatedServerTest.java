package megamek.server;

import junit.framework.TestCase;
import megamek.common.util.AbstractCommandLineParser;
import org.junit.Test;

public class DedicatedServerTest {

    @Test
    public void testCommandLineArgsFull() throws AbstractCommandLineParser.ParseException {
        String[] args = {"-password", "pass", "-port", "1234", "-competitive", "true"};
        DedicatedServer.CommandLineParser cp = new DedicatedServer.CommandLineParser(args);
        cp.parse();
        TestCase.assertEquals("pass", cp.getPassword());
        TestCase.assertEquals(1234, cp.getPort());
        TestCase.assertTrue(cp.getCompetitive());
    }

    @Test
    public void testCommandLineArgsPart() throws AbstractCommandLineParser.ParseException {
        String[] args = {"-password", "pass", "-port", "1234"};
        DedicatedServer.CommandLineParser cp = new DedicatedServer.CommandLineParser(args);
        cp.parse();
        TestCase.assertEquals("pass", cp.getPassword());
        TestCase.assertEquals(1234, cp.getPort());
        TestCase.assertFalse(cp.getCompetitive());
    }

}
