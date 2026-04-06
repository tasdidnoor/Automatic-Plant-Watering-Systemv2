import org.firmata4j.IODevice;
import org.firmata4j.firmata.FirmataDevice;

public class Main2 {

    public static void main(String[] args) throws Exception {

        System.out.println("========================================");
        System.out.println("    AUTO PLANT WATERING SYSTEM");
        System.out.println("========================================\n");

        // =========================================================
        // CONNECT TO ARDUINO
        // =========================================================

        String myPort = "COM5";  // Change this to your port
        IODevice myBoard = new FirmataDevice(myPort);

        try {
            myBoard.start();
            myBoard.ensureInitializationIsDone();
            System.out.println("Connected to Arduino on " + myPort);
        } catch (Exception e) {
            System.out.println("Could not connect: " + e.getMessage());
            return;
        }

        // =========================================================
        // CREATE ALL HELPER OBJECTS
        // =========================================================

        myOLED oled = new myOLED(myBoard);
        myMOIST moisture = new myMOIST(myBoard, 16);      // A2
        myPOT potentiometer = new myPOT(myBoard, 14);     // A0
        myPUMP pump = new myPUMP(myBoard, 7);             // D7
        myLED led = new myLED(myBoard, 4);                // D4
        myBUZZER buzzer = new myBUZZER(myBoard, 5);       // D5
        myBUTTON button = new myBUTTON(myBoard, 6);       // D6
        myDATA dataLogger = new myDATA();
        myGRAPH graph = new myGRAPH("Soil Moisture Over Time", "Moisture (%)");

        // =========================================================
        // WELCOME
        // =========================================================

        oled.showMessage("Welcome", "Auto Watering");
        Thread.sleep(2000);

        // =========================================================
        // SET DRY THRESHOLD (All instructions on OLED)
        // =========================================================

        double dryThreshold = 2.8;
        int thresholdPercent = 50;

        // Show threshold screen (make text fit on 0.96" screen)
        oled.clear();
        oled.showText(0, 0, "Set Dry Threshold");

        while (!button.isPressed()) {
            int potRaw = potentiometer.readRaw();
            int invertedRaw = 1023 - potRaw;
            dryThreshold = 2.0 + (invertedRaw / 1023.0) * 1.6;
            thresholdPercent = (invertedRaw * 100) / 1023;

            // Draw bar (using fewer chars to fit screen)
            int numFilled = (thresholdPercent * 16) / 100;
            StringBuilder bar = new StringBuilder("[");
            for (int i = 0; i < numFilled; i++) bar.append('#');
            for (int i = numFilled; i < 16; i++) bar.append('-');
            bar.append("]");

            oled.showText(0, 16, bar.toString());
            oled.showText(0, 32, String.format("%.2fV", dryThreshold));
            oled.showText(0, 48, "Press Button");

            Thread.sleep(100);
        }

        while (button.isPressed()) {
            Thread.sleep(50);
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

        oled.showMessage("GO!", "System Ready");
        led.on();
        buzzer.play(150, 1000);
        led.off();
        Thread.sleep(1000);
        oled.clear();

        // =========================================================
        // MAIN LOOP (Everything inside while button is NOT pressed)
        // =========================================================

        int cycleNumber = 0;

        while (true) {

            // If button is pressed, break out and do emergency stop
            if (button.isPressed()) {
                break;
            }

            cycleNumber++;
            System.out.println("CYCLE #" + cycleNumber);

            // -------------------------------------------------
            // CHECK MOISTURE FOR 5 SECONDS
            // -------------------------------------------------

            oled.clear();
            oled.showText(0, 0, "Checking Moisture");

            double voltageSum = 0;
            for (int i = 0; i < 10; i++) {
                voltageSum += moisture.readVoltage();
                int secondsLeft = 5 - (i / 2);
                oled.showText(0, 20, "Time: " + secondsLeft + "s");

                // Progress bar
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
                // SOIL IS DRY - WATER FOR EXACTLY 5 SECONDS
                oled.clear();
                oled.showText(0, 0, "DRY!");
                oled.showText(0, 20, "Watering 5s");

                pump.on();
                led.on();
                buzzer.play(200, 5000);

                for (int i = 5; i > 0; i--) {
                    oled.showText(0, 40, "Left: " + i + "s");
                    Thread.sleep(1000);
                }

                pump.off();
                led.off();
                buzzer.stop();

            } else {
                // SOIL IS WET - NO WATERING
                oled.clear();
                oled.showText(0, 20, "WET!");
                oled.showText(0, 40, "No watering");
                Thread.sleep(2000);
            }

            // -------------------------------------------------
            // LOG DATA
            // -------------------------------------------------

            int potRaw = potentiometer.readRaw();
            int invertedRaw = 1023 - potRaw;
            double currentThreshold = 2.0 + (invertedRaw / 1023.0) * 1.6;

            dataLogger.addReading(moisturePercent, currentThreshold, isDry ? "WATER" : "IDLE");
            graph.addDataPoint(moisturePercent);

            // Update dryThreshold for next cycle (user can change during wait)
            dryThreshold = currentThreshold;

            // -------------------------------------------------
            // 60 SECOND WAIT
            // -------------------------------------------------

            oled.clear();
            oled.showText(0, 0, "Wait 60s");
            oled.showText(0, 16, "Adjust Threshold");
            oled.showText(0, 48, "Potentiometer");

            for (int second = 60; second > 0; second--) {

                // Check emergency during wait
                if (button.isPressed()) {
                    break;
                }

                // Show time remaining
                oled.showText(0, 32, "Time: " + second + "s");

                // Read and update threshold live
                int currentRaw = potentiometer.readRaw();
                int currentInverted = 1023 - currentRaw;
                dryThreshold = 2.0 + (currentInverted / 1023.0) * 1.6;

                // Update threshold bar on OLED
                int newPercent = (currentInverted * 100) / 1023;
                int numFilled = (newPercent * 16) / 100;
                StringBuilder bar = new StringBuilder("[");
                for (int x = 0; x < numFilled; x++) bar.append('#');
                for (int x = numFilled; x < 16; x++) bar.append('-');
                bar.append("]");
                oled.showText(0, 48, bar.toString());

                Thread.sleep(1000);
            }

            // If button was pressed during wait, break out
            if (button.isPressed()) {
                break;
            }
        }

        // =========================================================
        // EMERGENCY STOP (Button was pressed)
        // =========================================================

        System.out.println("\nEMERGENCY STOP!");

        pump.off();

        oled.clear();
        oled.showText(0, 16, "EMERGENCY!");
        oled.showText(0, 32, "Stopping System");

        // Emergency buzzer and LED
        for (int i = 0; i < 5; i++) {
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

        // =========================================================
        // CLEANUP
        // =========================================================

        dataLogger.close();
        graph.close();
        myBoard.stop();

        System.out.println("System terminated.");
        System.exit(0);
    }
}