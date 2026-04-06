import org.firmata4j.IODevice;
import org.firmata4j.Pin;

public class myPUMP {

    private Pin pumpPin;
    private boolean isRunning;

    // Constructor
    public myPUMP(IODevice board, int pinNumber) throws Exception {
        pumpPin = board.getPin(pinNumber);
        pumpPin.setMode(Pin.Mode.OUTPUT);
        pumpPin.setValue(0);  // Start with pump OFF
        isRunning = false;
    }

    // Turn pump ON
    public void on() {
        try {
            pumpPin.setValue(1);
            isRunning = true;
            System.out.println("[PUMP] ON - Watering");
        } catch (Exception e) {
            System.out.println("Pump error: " + e.getMessage());
        }
    }

    // Turn pump OFF
    public void off() {
        try {
            pumpPin.setValue(0);
            isRunning = false;
            System.out.println("[PUMP] OFF");
        } catch (Exception e) {
            System.out.println("Pump error: " + e.getMessage());
        }
    }

    // Check if pump is currently running
    public boolean isRunning() {
        return isRunning;
    }

    // Water for a specific number of seconds (Although max = 5)
    public void waterForSeconds(int seconds) throws Exception {
        if (seconds > 5) {
            System.out.println("Warning: Max watering is 5 seconds. Limiting to 5.");
            seconds = 5;
        }

        on();

        for (int i = 0; i < seconds; i++) {
            Thread.sleep(1000);
            System.out.println("  Watering... " + (i + 1) + "/" + seconds + " sec");
        }

        off();
    }
}