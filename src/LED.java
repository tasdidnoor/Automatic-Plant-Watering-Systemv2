import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;

public class LED {

    static final String myPort = "COM5";


    public static void main(String[] args) throws Exception {

        // Connect to board
        IODevice myBoard = new FirmataDevice(myPort);
        myBoard.start();
        myBoard.ensureInitializationIsDone();

        // Get the LED pin (D4)
        Pin ledPin = myBoard.getPin(4);

        // Set it as OUTPUT
        ledPin.setMode(Pin.Mode.OUTPUT);

        System.out.println("Blinking LED 5 times...");

        // Blink 5 times
        for (int i = 0; i < 5; i++) {

            // Turn ON
            ledPin.setValue(1);
            System.out.println("LED ON - blink " + (i + 1));
            Thread.sleep(500);  // Wait 0.5 seconds

            // Turn OFF
            ledPin.setValue(0);
            System.out.println("LED OFF");
            Thread.sleep(500);  // Wait 0.5 seconds
        }

        myBoard.stop();
        System.out.println("Done!");
    }
}