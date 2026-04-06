import org.firmata4j.IODevice;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.I2CDevice;
import org.firmata4j.ssd1306.*;

public class POT {
    public static void main(String[] args) throws Exception {

        // Connect to board
        String myPort = "COM5";
        IODevice myBoard = new FirmataDevice(myPort);
        myBoard.start();
        myBoard.ensureInitializationIsDone();

        // Create potentiometer on A0 (pin 14)
        myPOT pot = new myPOT(myBoard, 14);

        // Setup OLED
        I2CDevice i2cObject = myBoard.getI2CDevice((byte) 0x3C);
        SSD1306 oled = new SSD1306(i2cObject, SSD1306.Size.SSD1306_128_64);
        oled.init();
        oled.getCanvas().clear();
        oled.display();

        System.out.println("Turn the potentiometer knob!");
        System.out.println("Watch the OLED screen for visual feedback");

        // Keep reading and updating OLED
        while (true) {
            int raw = pot.readRaw();
            int percent = pot.readPercentage();
            double voltage = pot.readVoltage();

            // Clear OLED
            oled.getCanvas().clear();

            // Show percentage as text
            oled.getCanvas().drawString(0, 0, "Potentiometer");
            oled.getCanvas().drawString(0, 16, "Value: " + raw);
            oled.getCanvas().drawString(0, 32, "Percent: " + percent + "%");
            oled.getCanvas().drawString(0, 48, String.format("%.2fV", voltage));

            // Draw a progress bar (line that grows with percentage)
            int barLength = (percent * 128) / 100;  // 128 is OLED width
            for (int x = 0; x < barLength; x++) {
                oled.getCanvas().setPixel(x, 55, MonochromeCanvas.Color.BRIGHT);
            }

            oled.display();

            Thread.sleep(50);  // Update 20 times per second
        }
    }
}