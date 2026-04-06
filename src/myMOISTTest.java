import org.junit.Test;
import static org.junit.Assert.*;

public class myMOISTTest {

    @Test
    public void testVoltageToPercentage() throws Exception {  // Add throws Exception
        myMOIST sensor = new myMOIST(null, 14);

        int dryPercent = sensor.getPercentage(3.64);
        assertEquals(0, dryPercent);

        int wetPercent = sensor.getPercentage(2.68);
        assertEquals(100, wetPercent);

        int midPercent = sensor.getPercentage(3.16);
        assertEquals(50, midPercent);
    }

    @Test
    public void testIsDry() throws Exception {  // Add throws Exception
        myMOIST sensor = new myMOIST(null, 14);

        assertTrue(sensor.isDry(3.4, 3.0));
        assertFalse(sensor.isDry(2.8, 3.0));
    }

    @Test
    public void testReadPercentageBounds() throws Exception {  // Add throws Exception
        myMOIST sensor = new myMOIST(null, 14);

        int belowMin = sensor.getPercentage(4.0);
        assertEquals(0, belowMin);

        int aboveMax = sensor.getPercentage(1.0);
        assertEquals(100, aboveMax);
    }

    @Test
    public void testRawToVoltage() throws Exception {  // Add throws Exception
        myMOIST sensor = new myMOIST(null, 14);

        double voltage0 = sensor.rawToVoltage(0);
        assertEquals(0.0, voltage0, 0.01);

        double voltage1023 = sensor.rawToVoltage(1023);
        assertEquals(5.0, voltage1023, 0.01);

        double voltage512 = sensor.rawToVoltage(512);
        assertEquals(2.5, voltage512, 0.01);
    }
}