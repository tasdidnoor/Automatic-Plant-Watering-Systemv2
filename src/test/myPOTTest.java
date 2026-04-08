import org.junit.Test;
import static org.junit.Assert.*;

public class myPOTTest {

    @Test
    public void testMapToVoltage() throws Exception {  // Add throws Exception
        myPOT pot = new myPOT(null, 15);

        double voltage0 = pot.mapToVoltage(0);
        assertEquals(2.0, voltage0, 0.01);

        double voltage1023 = pot.mapToVoltage(1023);
        assertEquals(3.6, voltage1023, 0.01);

        double voltage512 = pot.mapToVoltage(512);
        assertEquals(2.8, voltage512, 0.01);
    }

    @Test
    public void testRawToPercentage() throws Exception {  // Add throws Exception
        myPOT pot = new myPOT(null, 15);

        assertEquals(0, pot.rawToPercentage(0));
        assertEquals(100, pot.rawToPercentage(1023));
        assertEquals(50, pot.rawToPercentage(512));
        assertEquals(25, pot.rawToPercentage(256));
        assertEquals(75, pot.rawToPercentage(768));
    }

    @Test
    public void testThresholdMapping() throws Exception {  // Add throws Exception
        myPOT pot = new myPOT(null, 15);

        double voltageLow = pot.mapToVoltage(0);
        double voltageHigh = pot.mapToVoltage(1023);

        assertTrue(voltageHigh > voltageLow);
    }
}