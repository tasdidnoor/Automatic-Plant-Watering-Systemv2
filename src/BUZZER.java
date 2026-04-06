import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;

public class BUZZER {
    public static void main(String[] args) throws Exception {

        // Connect to board
        String myPort = "COM5";
        IODevice myBoard = new FirmataDevice(myPort);
        myBoard.start();
        myBoard.ensureInitializationIsDone();

        // Get the buzzer pin (D5)
        Pin buzzerPin = myBoard.getPin(5);

        // IMPORTANT: Use PWM mode, not OUTPUT
        buzzerPin.setMode(Pin.Mode.PWM);

        System.out.println("Testing different tones...");

        // Low tone (lower number = lower frequency)
        System.out.println("Low tone");
        buzzerPin.setValue(64);   // 64 out of 255
        Thread.sleep(1000);

        // Medium tone
        System.out.println("Medium tone");
        buzzerPin.setValue(128);  // 128 out of 255
        Thread.sleep(1000);

        // High tone (your original sound)
        System.out.println("High tone");
        buzzerPin.setValue(255);  // 255 out of 255
        Thread.sleep(1000);

        // Turn off
        buzzerPin.setValue(0);

        myBoard.stop();
        System.out.println("Done!");
    }
}