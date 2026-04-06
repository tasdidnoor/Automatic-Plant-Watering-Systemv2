import org.firmata4j.IODevice;
import org.firmata4j.Pin;

public class myBUZZER {

    private Pin buzzerPin;

    public myBUZZER(IODevice board, int pinNumber) throws Exception {
        buzzerPin = board.getPin(pinNumber);
        buzzerPin.setMode(Pin.Mode.PWM);
        buzzerPin.setValue(0);
    }

    // Play a tone with specific value (0-255)
    public void start(int value) throws Exception{
        buzzerPin.setValue(value);
    }

    // Stop buzzing immediately
    public void stop() throws Exception{
        buzzerPin.setValue(0);
    }

    // Play for fixed duration (old method)
    public void play(int value, int durationMs) throws Exception {
        start(value);
        Thread.sleep(durationMs);
        stop();
    }
}