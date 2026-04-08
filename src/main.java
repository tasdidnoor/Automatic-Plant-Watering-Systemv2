import org.firmata4j.IODevice;
import org.firmata4j.firmata.FirmataDevice;

public class Main {

    public static void main(String[] args) throws Exception {

        System.out.println("========================================");
        System.out.println("    AUTO PLANT WATERING SYSTEM");
        System.out.println("========================================\n");

        // =========================================================
        // CONNECT TO ARDUINO
        // =========================================================

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
        Thread.sleep(1500);

        // =========================================================
        // SET DRY THRESHOLD (Inverted: right = lower voltage = wetter)
        // =========================================================

        double dryThreshold = 3.0;
        int thresholdPercent = 50;

        oled.clear();
        oled.showText(0, 0, "Set Dry Threshold");

        while (true) {
            // INVERTED: turn right = lower threshold (wetter), turn left = higher threshold (drier)
            int potRaw = potentiometer.readRaw();
            int invertedRaw = 1023 - potRaw;
            dryThreshold = 2.0 + (invertedRaw / 1023.0) * 2.0;
            thresholdPercent = (invertedRaw * 100) / 1023;

            int numFilled = (thresholdPercent * 16) / 100;
            StringBuilder bar = new StringBuilder("[");
            for (int i = 0; i < numFilled; i++) bar.append('#');
            for (int i = numFilled; i < 16; i++) bar.append('-');
            bar.append("]");

            oled.showText(0, 16, bar.toString());
            oled.showText(0, 32, String.format("%.2fV", dryThreshold));
            oled.showText(0, 48, "Press Button");

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
        // COUNTDOWN
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
        // MAIN LOOP
        // =========================================================

        int cycleNumber = 0;

        while (true) {

            cycleNumber++;
            System.out.println("CYCLE #" + cycleNumber);

            // -------------------------------------------------
            // CHECK MOISTURE FOR 5 SECONDS (1 sample per second)
            // -------------------------------------------------

            oled.clear();
            oled.showText(0, 0, "Checking Moisture");

            double voltageSum = 0;
            boolean emergency = false;

            for (int second = 1; second <= 5; second++) {

                voltageSum += moisture.readVoltage();
                oled.showText(0, 20, "Time: " + second + "/5s");

                int percentComplete = (second * 100) / 5;
                int numFilled = (percentComplete * 16) / 100;
                StringBuilder bar = new StringBuilder("[");
                for (int x = 0; x < numFilled; x++) bar.append('=');
                for (int x = numFilled; x < 16; x++) bar.append('-');
                bar.append("]");
                oled.showText(0, 40, bar.toString());

                Thread.sleep(1000);

                // Emergency check at END of each second
                if (button.isPressed()) {
                    emergency = true;
                    break;
                }
            }

            if (emergency) {
                break;
            }

            double avgVoltage = voltageSum / 5;
            int moisturePercent = moisture.getPercentage(avgVoltage);

            // Get current threshold (inverted)
            int potRaw = potentiometer.readRaw();
            int invertedRaw = 1023 - potRaw;
            dryThreshold = 2.0 + (invertedRaw / 1023.0) * 2.0;

            boolean isDry = avgVoltage > dryThreshold;
            System.out.println("Moisture %: " + moisturePercent + "%, Moisture V: " + String.format("%.2f", avgVoltage) + ", Threshold: " + String.format("%.2f", dryThreshold) + "V");

            // -------------------------------------------------
            // WATER FOR 5 SECONDS IF DRY
            // -------------------------------------------------

            if (isDry) {
                oled.clear();
                oled.showText(0, 0, "DRY!");
                oled.showText(0, 20, "Watering...");

                pump.on();
                led.on();

                for (int second = 1; second <= 5; second++) {
                    oled.showText(0, 40, "Time: " + second + "/5s");
                    buzzer.play(200, 1000);

                    // Emergency check at END of each second
                    if (button.isPressed()) {
                        emergency = true;
                        break;
                    }
                }

                pump.off();
                led.off();
                buzzer.stop();

                if (emergency) {
                    break;
                }

            } else {
                oled.clear();
                oled.showText(0, 20, "WET!");
                oled.showText(0, 40, "No watering");
                Thread.sleep(2000);
            }

            // -------------------------------------------------
            // LOG DATA
            // -------------------------------------------------

            dataLogger.addReading(moisturePercent, dryThreshold, isDry ? "WATER" : "IDLE");
            graph.addDataPoint(moisturePercent);

            // -------------------------------------------------
            // 60 SECOND WAIT
            // -------------------------------------------------

            oled.clear();
            oled.showText(0, 0, "Wait 60s");
            oled.showText(0, 16, "Adjust Threshold");

            for (int second = 1; second <= 60; second++) {

                int timeLeft = 60 - second + 1;
                oled.showText(0, 32, "Time: " + timeLeft + "s");

                // Read and update threshold live (inverted)
                int currentRaw = potentiometer.readRaw();
                int currentInverted = 1023 - currentRaw;
                double newThreshold = 2.0 + (currentInverted / 1023.0) * 2.0;
                int newPercent = (currentInverted * 100) / 1023;

                int numFilled = (newPercent * 16) / 100;
                StringBuilder bar = new StringBuilder("[");
                for (int x = 0; x < numFilled; x++) bar.append('#');
                for (int x = numFilled; x < 16; x++) bar.append('-');
                bar.append("]");
                oled.showText(0, 48, bar.toString());
                oled.showText(80, 48, String.format("%.2fV", newThreshold));

                dryThreshold = newThreshold;

                Thread.sleep(1000);

                // Emergency check at END of each second
                if (button.isPressed()) {
                    emergency = true;
                    break;
                }
            }

            if (emergency) {
                break;
            }
        }

        // =========================================================
        // EMERGENCY STOP
        // =========================================================

        System.out.println("\nEMERGENCY STOP!");

        pump.off();

        oled.clear();
        oled.showText(0, 16, "EMERGENCY!");
        oled.showText(0, 32, "Stopping System");

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

        // Clear OLED right before exit
        oled.clear();

        // =========================================================
        // CLEANUP
        // =========================================================

        dataLogger.close();
        // graph.close();  // REMOVED - StdDraw doesn't need this
        myBoard.stop();

        System.out.println("System terminated.");
        System.exit(0);
    }
}