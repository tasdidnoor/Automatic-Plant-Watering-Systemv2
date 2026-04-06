import org.firmata4j.IODevice;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;

public class BUTTON {

    public static void main(String[] args) throws Exception {

        // Connect to board
        String myPort = "COM5";
        IODevice myBoard = new FirmataDevice(myPort);
        myBoard.start();
        myBoard.ensureInitializationIsDone();

        Pin buttonPin = myBoard.getPin(6);

        buttonPin.setMode(Pin.Mode.INPUT);

        myLED led = new myLED(myBoard, 4);      // D4
        myBUZZER buzzer = new myBUZZER(myBoard, 5);

        System.out.println("Press the button");

        for (int i = 0; i < 20; i++) {
            long value = buttonPin.getValue();

            if (value == 1) {
                System.out.println("BUTTON PRESSED");
                led.on();
                buzzer.start(64);

                while (value == 1) {
                    Thread.sleep(10);
                }
            }

            else {
                System.out.println("Button not pressed");
                led.off();
                buzzer.stop();
            }

            Thread.sleep(500);
        }

        myBoard.stop();
        System.out.println("Done!");
    }
}