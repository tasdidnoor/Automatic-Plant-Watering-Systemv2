import org.firmata4j.IODevice;
import org.firmata4j.Pin;

public class myMOIST {

    private Pin moisturePin;
    private IODevice board;
    private boolean isTestMode;

    // Threshold calibration values
    private final double DRY_VOLTAGE = 3.64;
    private final double WET_VOLTAGE = 2.68;

    public myMOIST(IODevice board, int pinNumber) throws Exception {
        this.board = board;
        this.isTestMode = (board == null);

        if (!isTestMode) {
            moisturePin = board.getPin(pinNumber);
            moisturePin.setMode(Pin.Mode.ANALOG);
        }
    }

    public int readRaw() {
        if (isTestMode) return 512;
        return (int) moisturePin.getValue();
    }

    // Read voltage (0 to 5V)
    public double readVoltage() {
        int raw = readRaw();
        return rawToVoltage(raw);
    }

    // Convert raw value to voltage (for testing)
    public double rawToVoltage(int raw) {
        return (raw / 1023.0) * 5.0;
    }

    // Calculate moisture percentage
    public int readPercentage() {
        double voltage = readVoltage();
        return getPercentage(voltage);
    }

    // Calculate percentage from voltage (for testing)
    public int getPercentage(double voltage) {
        double percent = (DRY_VOLTAGE - voltage) / (DRY_VOLTAGE - WET_VOLTAGE) * 100;

        if (percent < 0) percent = 0;
        if (percent > 100) percent = 100;

        return (int) percent;
    }

    // Check if soil is dry (using current reading)
    public boolean isDry(double thresholdVoltage) {
        double voltage = readVoltage();
        return voltage > thresholdVoltage;
    }

    // Check if soil is dry using given voltage and threshold (for testing)
    public boolean isDry(double voltage, double threshold) {
        return voltage > threshold;
    }

    // Check if soil is wet
    public boolean isWet(double thresholdVoltage) {
        double voltage = readVoltage();
        return voltage < thresholdVoltage;
    }

    // Get text description
    public String getCondition(double thresholdVoltage) {
        if (isDry(thresholdVoltage)) {
            return "DRY";
        } else {
            return "WET";
        }
    }

    public double readAverageOver5Seconds() throws Exception {
        double sum = 0;
        for (int i = 0; i < 10; i++) {  // 10 samples over 5 seconds
            sum += readVoltage();
            Thread.sleep(500);
        }
        return sum / 10;
    }

}