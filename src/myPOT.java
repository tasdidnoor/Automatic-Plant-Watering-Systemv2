import org.firmata4j.IODevice;
import org.firmata4j.Pin;

public class myPOT {

    private Pin potPin;
    private IODevice board;  // Store board reference
    private boolean isTestMode;  // Flag for testing

    // Voltage range for threshold
    private final double MIN_THRESHOLD_VOLTAGE = 2.0;
    private final double MAX_THRESHOLD_VOLTAGE = 3.6;

    // Constructor
    public myPOT(IODevice board, int pinNumber) throws Exception {
        this.board = board;
        this.isTestMode = (board == null);

        if (!isTestMode) {
            potPin = board.getPin(pinNumber);
            potPin.setMode(Pin.Mode.ANALOG);
        }
    }

    // Read raw value (0 to 1023)
    public int readRaw() {
        if (isTestMode) {
            return 512;  // Default value for testing
        }
        long value = potPin.getValue();
        return (int) value;
    }

    // Read as percentage (0 to 100)
    public int readPercentage() {
        int raw = readRaw();
        return rawToPercentage(raw);
    }

    // Convert raw to percentage (for testing)
    public int rawToPercentage(int rawValue) {
        return (rawValue * 100) / 1023;
    }

    // Read as voltage (0 to 5V)
    public double readVoltage() {
        int raw = readRaw();
        return rawToVoltage(raw);
    }

    // Convert raw to voltage (for testing)
    public double rawToVoltage(int rawValue) {
        return (rawValue / 1023.0) * 5.0;
    }

    // Map potentiometer to dryness threshold voltage
    public double getThresholdVoltage() {
        int raw = readRaw();
        return mapToVoltage(raw);
    }

    // Map raw value to threshold voltage (for testing)
    public double mapToVoltage(int rawValue) {
        return MIN_THRESHOLD_VOLTAGE + (rawValue / 1023.0) *
                (MAX_THRESHOLD_VOLTAGE - MIN_THRESHOLD_VOLTAGE);
    }

    // Map raw value to threshold percentage
    public int getThresholdPercentage() {
        int raw = readRaw();
        return rawToThresholdPercentage(raw);
    }

    // Convert raw to threshold percentage (for testing)
    public int rawToThresholdPercentage(int rawValue) {
        return (rawValue * 100) / 1023;
    }

    // Get text description
    public String getThresholdDescription() {
        int percent = getThresholdPercentage();
        if (percent < 30) {
            return "WET SETTING";
        } else if (percent > 70) {
            return "DRY SETTING";
        } else {
            return "NORMAL";
        }
    }
}