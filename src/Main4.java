import org.firmata4j.IODevice;
import org.firmata4j.firmata.FirmataDevice;

public class Main4 {

    public static void main(String[] args) throws Exception {

        System.out.println("    AUTO PLANT WATERING SYSTEM");

        // Connect to Board

        String myPort = "COM5";
        IODevice myBoard = new FirmataDevice(myPort);

        try {
            myBoard.start();
            myBoard.ensureInitializationIsDone();
            System.out.println("Connected to Arduino on " + myPort);
        } catch (Exception e) {
            System.out.println("Could not connect: " + e.getMessage());
            return;
        }

        // Create Objects

        myOLED oled = new myOLED(myBoard);
        myMOIST moisture = new myMOIST(myBoard, 16);      // A2
        myPOT potentiometer = new myPOT(myBoard, 14);     // A0
        myPUMP pump = new myPUMP(myBoard, 7);             // D7
        myLED led = new myLED(myBoard, 4);                // D4
        myBUZZER buzzer = new myBUZZER(myBoard, 5);       // D5
        myBUTTON button = new myBUTTON(myBoard, 6);       // D6
        myDATA dataLogger = new myDATA();
        myGRAPH graph = new myGRAPH("Soil Moisture Over Time", "Moisture (%)");

        //Welcome

        oled.showMessage("Welcome", "Auto Watering");
        Thread.sleep(1500);


        double dryThreshold = 3.0;
        int thresholdPercent = 50;

        oled.clear();
        oled.showText(0, 0, "Set Dry Threshold");

        while (true) {

            int potRaw = potentiometer.readRaw();
            dryThreshold = 2.0 + (potRaw / 1023.0) * 2.0;
            thresholdPercent = (potRaw * 100) / 1023;
            int invertedRaw = 1023 - potRaw;
            dryThreshold = 2.0 + (invertedRaw / 1023.0) * 1.6;
            thresholdPercent = (invertedRaw * 100) / 1023;

            // Draw bar (16 characters)
            int numFilled = (thresholdPercent * 16) / 100;
            StringBuilder bar = new StringBuilder("[");
            for (int i = 0; i < numFilled; i++) bar.append('#');
            for (int i = numFilled; i < 16; i++) bar.append('-');
            bar.append("]");

            oled.showText(0, 16, bar.toString());
            oled.showText(0, 32, String.format("%.2fV", dryThreshold));
            oled.showText(0, 48, "Press Button");

            // Check if button is pressed
            if (button.isPressed()) {
                Thread.sleep(50);
                while (button.isPressed()) {
                    Thread.sleep(50);
                }
                break;
            }

            Thread.sleep(100);
        }

        // =========================================================
        // COUNTDOWN (3,2,1,GO)
        // =========================================================

        oled.clear();
        oled.showLargeCentered("3");
        led.on();
        buzzer.play(150, 300);
        led.off();
        Thread.sleep(700);

        oled.showLargeCentered("2");
        led.on();
        buzzer.play(150, 300);
        led.off();
        Thread.sleep(700);

        oled.showLargeCentered("1");
        led.on();
        buzzer.play(150, 300);
        led.off();
        Thread.sleep(700);

        // =========================================================
        // MAIN LOOP
        // =========================================================

        int cycleNumber = 0;

        while (true) {

            // Check emergency button at start of each cycle
            if (button.isPressed()) {
                System.out.println("\nEMERGENCY STOP!");

                pump.off();

                // Clear OLED first, then show emergency message
                oled.clear();

                // Emergency buzzer and LED
                for (int i = 0; i < 5; i++) {
                    oled.showText(0, 16, "EMERGENCY!");
                    oled.showText(0, 32, "Stopping System");
                    led.on();
                    buzzer.play(200, 200);
                    Thread.sleep(100);
                    led.off();
                    buzzer.play(100, 200);
                    Thread.sleep(100);
                }

                buzzer.stop();
                led.off();

                Thread.sleep(2000);

                // Clear OLED at the very end
                oled.showText(0, 20, "System Off");
                oled.clear();

                // =========================================================
                // CLEANUP
                // =========================================================

                dataLogger.close();
                graph.close();
                myBoard.stop();

                System.out.println("System terminated.");
                System.exit(0);
                break;
            } else {

                oled.showMessage("GO!", "System Ready");
                led.on();
                buzzer.play(150, 1000);
                led.off();
                Thread.sleep(1000);
                oled.clear();

                cycleNumber++;
                System.out.println("CYCLE #" + cycleNumber);

                // -------------------------------------------------
                // CHECK MOISTURE FOR 5 SECONDS (10 samples)
                // -------------------------------------------------

                oled.clear();
                oled.showText(0, 0, "Checking Moisture");

                double voltageSum = 0;
                for (int i = 0; i < 10; i++) {
                    voltageSum += moisture.readVoltage();
                    int secondsLeft = 5 - (i / 2);
                    oled.showText(0, 20, "Time: " + secondsLeft + "s");

                    int percentComplete = ((i + 1) * 10);
                    int numFilled = (percentComplete * 16) / 100;
                    StringBuilder bar = new StringBuilder("[");
                    for (int x = 0; x < numFilled; x++) bar.append('=');
                    for (int x = numFilled; x < 16; x++) bar.append('-');
                    bar.append("]");
                    oled.showText(0, 40, bar.toString());

                    Thread.sleep(500);
                }

                double avgVoltage = voltageSum / 10;
                int moisturePercent = moisture.getPercentage(avgVoltage);
                boolean isDry = avgVoltage > dryThreshold;

                System.out.println("Moisture: " + moisturePercent + "%, Threshold: " + String.format("%.2f", dryThreshold) + "V");

                // -------------------------------------------------
                // WATER OR NOT
                // -------------------------------------------------

                if (isDry) {
                    oled.clear();
                    oled.showText(0, 0, "DRY!");
                    oled.showText(0, 20, "Watering...");

                    pump.on();
                    led.on();


                    for (int i = 5; i > 0; i--) {
                        oled.showText(0, 40, "Remaining: " + i + "s");
                        buzzer.play(64, 1000);
                    }

                    pump.off();
                    led.off();
                    buzzer.stop();

                } else {
                    oled.clear();
                    oled.showText(0, 20, "WET!");
                    oled.showText(0, 40, "No watering");
                    Thread.sleep(2000);
                }

                // -------------------------------------------------
                // LOG DATA
                // -------------------------------------------------

                int potRaw = potentiometer.readRaw();
                dryThreshold = 2.0 + (potRaw / 1023.0) * 2.0;

                dataLogger.addReading(moisturePercent, dryThreshold, isDry ? "WATER" : "IDLE");
                graph.addDataPoint(moisturePercent);

                // -------------------------------------------------
                // 60 SECOND WAIT
                // -------------------------------------------------

                oled.clear();
                oled.showText(0, 0, "Wait 60s");
                oled.showText(0, 16, "Adjust Threshold");

                for (int second = 60; second > 0; second--) {


                    oled.showText(0, 32, "Time: " + second + "s");

                    int currentRaw = potentiometer.readRaw();
                    double newThreshold = 2.0 + (currentRaw / 1023.0) * 2.0;
                    int newPercent = (currentRaw * 100) / 1023;

                    int numFilled = (newPercent * 16) / 100;
                    StringBuilder bar = new StringBuilder("[");
                    for (int x = 0; x < numFilled; x++) bar.append('#');
                    for (int x = numFilled; x < 16; x++) bar.append('-');
                    bar.append("]");
                    oled.showText(0, 48, bar.toString());
                    oled.showText(80, 48, String.format("%.2fV", newThreshold));

                    dryThreshold = newThreshold;

                    Thread.sleep(1000);
                }

            }

            // =========================================================
            // EMERGENCY STOP
            // =========================================================

        }
    }
}