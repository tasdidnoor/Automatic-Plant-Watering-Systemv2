import org.firmata4j.IODevice;
import org.firmata4j.Pin;

public class myBUTTON {

    private Pin buttonPin;

    // Constructor

    public myBUTTON(IODevice board, int pinNumber) throws Exception {
        buttonPin = board.getPin(pinNumber);
        buttonPin.setMode(Pin.Mode.INPUT);
    }

    // Button boolean yes
    public boolean isPressed() {
        long value = buttonPin.getValue();
        return value == 1;
    }

    // Button boolean no
    public boolean isReleased() {
        return !isPressed();
    }

    // Wait until button is pressed
    public void waitForPress() throws Exception {
        while (!isPressed()) {
            Thread.sleep(10);
        }
    }

    // Wait until button is released
    public void waitForRelease() throws Exception {
        while (isPressed()) {
            Thread.sleep(10);
        }
    }

    // Wait for a full press and release
    public void waitForClick() throws Exception {
        waitForPress();
        waitForRelease();
    }
}