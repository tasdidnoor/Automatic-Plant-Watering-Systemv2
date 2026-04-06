import org.firmata4j.IODevice;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.I2CDevice;
import org.firmata4j.ssd1306.SSD1306;
import java.io.IOException;

public class OLED {

    static final String myPort = "COM5";

    public static void main(String[] args) throws IOException, InterruptedException {

        // Setting up the board

        System.out.println("Connecting to the Grove Board...");
        IODevice myBoard = new FirmataDevice(myPort);
        myBoard.start();
        myBoard.ensureInitializationIsDone();

        System.out.println("Connected to the Grove Board...");

        // Setting the OLED Device

        System.out.println("Connecting to the OLED...");

        I2CDevice i2cObject = myBoard.getI2CDevice((byte) 0x3C);
        SSD1306 myOLED = new SSD1306(i2cObject, SSD1306.Size.SSD1306_128_64);

        myOLED.init();

        // Displaying Hello world
        System.out.println("Diplaying Text...");
        myOLED.getCanvas().clear();
        myOLED.getCanvas().drawString(0, 0, "Hello World!");
        myOLED.display();

        Thread.sleep(5000);

        myOLED.getCanvas().clear();
        myOLED.display();
        myBoard.stop();

        System.out.println("Complete!");

    }

}
