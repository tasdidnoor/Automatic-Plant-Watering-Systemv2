import org.firmata4j.IODevice;
import org.firmata4j.firmata.FirmataDevice;

public class PUMP {
    public static void main(String[] args) throws Exception {

        // Connect to board
        String myPort = "COM5";
        IODevice myBoard = new FirmataDevice(myPort);
        myBoard.start();
        myBoard.ensureInitializationIsDone();

        // Create pump on D7
        myPUMP pump = new myPUMP(myBoard, 7);

        System.out.println("=== PUMP TEST ===");
        System.out.println("Make sure your pump is connected to D7");
        System.out.println("And your MOSFET/relay is connected correctly");
        System.out.println();

        // Test 1: Simple on/off
        System.out.println("Test 1: Turning pump ON for 2 seconds...");
        pump.on();
        Thread.sleep(2000);
        pump.off();
        System.out.println("Test 1 complete");
        System.out.println();

        Thread.sleep(1000);

        // Test 2: Using waterForSeconds method
        System.out.println("Test 2: Watering for 3 seconds...");
        pump.waterForSeconds(3);
        System.out.println("Test 2 complete");
        System.out.println();

        Thread.sleep(1000);

        // Test 3: Quick on/off (blink test)
        System.out.println("Test 3: Quick pulses (3 times)...");
        for (int i = 0; i < 3; i++) {
            System.out.println("  Pulse " + (i+1));
            pump.on();
            Thread.sleep(500);
            pump.off();
            Thread.sleep(500);
        }
        System.out.println("Test 3 complete");
        System.out.println();

        System.out.println("=== ALL TESTS COMPLETE ===");

        myBoard.stop();
    }
}