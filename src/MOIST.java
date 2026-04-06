import org.firmata4j.IODevice;
import org.firmata4j.firmata.FirmataDevice;

public class MOIST {
    public static void main(String[] args) throws Exception {

        // Connect to board
        String myPort = "COM5";
        IODevice myBoard = new FirmataDevice(myPort);
        myBoard.start();
        myBoard.ensureInitializationIsDone();

        // Create objects
        myMOIST moisture = new myMOIST(myBoard, 16);
        myOLED display = new myOLED(myBoard);

        System.out.println("Turn the potentiometer to set threshold");
        System.out.println("Watch the OLED for soil moisture reading");

        // Demo threshold
        double thresholdVoltage = 2.8;  // About 50% moisture

        for (int i = 0; i < 100; i++) {
            int percent = moisture.readPercentage();
            double voltage = moisture.readVoltage();
            String condition = moisture.getCondition(thresholdVoltage);

            // Show on OLED
            display.clear();
            display.showText(0, 0, "SOIL MOISTURE");
            display.showText(0, 16, percent + "%");
            display.showText(0, 32, voltage + "V");
            display.showText(0, 48, "Condition: " + condition);

            // Also show in console
            System.out.printf("Moisture: %3d%% | Voltage: %.2fV | %s\n",
                    percent, voltage, condition);

            Thread.sleep(500);
        }

        myBoard.stop();
    }
}