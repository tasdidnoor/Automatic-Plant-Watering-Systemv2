import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import java.io.IOException;
public class myLED {

    private Pin ledPin;

    // Constructor
    public myLED(IODevice board, int pinNumber) throws Exception {
        ledPin = board.getPin(pinNumber);
        ledPin.setMode(Pin.Mode.OUTPUT);
        ledPin.setValue(0);  // Start with OFF
    }

    // Turn LED on
    public void on() throws IOException {
        ledPin.setValue(1);
    }

    // Turn LED off
    public void off() throws IOException {
        ledPin.setValue(0);
    }

    // Blink a certain number of times
    public void blink(int times, int delayMs) throws Exception {
        for (int i = 0; i < times; i++) {
            on();
            Thread.sleep(delayMs);
            off();
            Thread.sleep(delayMs);
        }
    }
}