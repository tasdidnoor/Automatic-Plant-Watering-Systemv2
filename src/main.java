import org.firmata4j.IODevice;
import org.firmata4j.firmata.FirmataDevice;

public class main {

    public static void main(String[] args) throws Exception {

        System.out.println("========================================");
        System.out.println("    AUTO PLANT WATERING SYSTEM");
        System.out.println("========================================\n");

        // =========================================================
        // PHASE 1: CONNECT TO ARDUINO
        // =========================================================

        String myPort = "COM5";
        IODevice myBoard = new FirmataDevice(myPort);

        try {
            myBoard.start();
            myBoard.ensureInitializationIsDone();
            System.out.println("[SYSTEM] Connected to Arduino on " + myPort);
        } catch (Exception e) {
            System.out.println("[ERROR] Could not connect to board: " + e.getMessage());
            return;
        }

        // =========================================================
        // PHASE 2: CREATE ALL HELPER OBJECTS
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

        System.out.println("[SYSTEM] All sensors initialized");
        System.out.println("[SYSTEM] LED on D4, Buzzer on D5, Pump on D7");

        // =========================================================
        // PHASE 3: WELCOME MESSAGE
        // =========================================================

        oled.showMessage("Welcome to", "Auto Plant Watering");
        System.out.println("[SYSTEM] Welcome message displayed");
        Thread.sleep(2000);
        oled.clear();

        // =========================================================
        // PHASE 4: SET DRY THRESHOLD (User adjusts with potentiometer)
        // =========================================================

        System.out.println("\n[SETUP] Please set your dry threshold using the potentiometer");
        System.out.println("[SETUP] Turn the knob and press the button when ready");

        double dryThreshold = 2.8;  // Default value
        int thresholdPercent = 50;

        // Wait for button press
        while (!button.isPressed()) {
            // Read potentiometer and convert to threshold voltage (2.0V to 3.6V)
            int potRaw = potentiometer.readRaw();
            // Invert direction: turn left = lower threshold (wetter), turn right = higher threshold (drier)
            int invertedRaw = 1023 - potRaw;
            dryThreshold = 2.0 + (invertedRaw / 1023.0) * 1.6;
            thresholdPercent = (invertedRaw * 100) / 1023;

            // Update OLED
            oled.showThresholdScreen(thresholdPercent, dryThreshold);

            Thread.sleep(100);
        }

        // Wait for button release
        while (button.isPressed()) {
            Thread.sleep(50);
        }

        System.out.println("[SETUP] Threshold set to: " + String.format("%.2f", dryThreshold) + "V");

        // =========================================================
        // PHASE 5: COUNTDOWN (3, 2, 1, READY!)
        // =========================================================

        System.out.println("\n[SETUP] Starting in...");

        // Countdown 3
        oled.showLargeCentered("3");
        for (int i = 0; i < 3; i++) {
            led.on();
            buzzer.play(150, 100);
            Thread.sleep(100);
            led.off();
            Thread.sleep(100);
        }
        System.out.println("  3...");
        Thread.sleep(500);

        // Countdown 2
        oled.showLargeCentered("2");
        for (int i = 0; i < 2; i++) {
            led.on();
            buzzer.play(150, 100);
            Thread.sleep(100);
            led.off();
            Thread.sleep(100);
        }
        System.out.println("  2...");
        Thread.sleep(500);

        // Countdown 1
        oled.showLargeCentered("1");
        for (int i = 0; i < 1; i++) {
            led.on();
            buzzer.play(150, 100);
            Thread.sleep(100);
            led.off();
            Thread.sleep(100);
        }
        System.out.println("  1...");
        Thread.sleep(500);

        // READY!
        oled.showMessage("READY!", "Starting system...");
        led.on();
        buzzer.play(150, 1500);
        Thread.sleep(1500);
        led.off();
        oled.clear();

        System.out.println("[SETUP] System ready! Starting main loop...\n");

        // =========================================================
        // PHASE 6: MAIN LOOP (Runs forever while button is NOT pressed)
        // =========================================================

        int cycleNumber = 0;

        // Main loop - runs as long as emergency button is NOT pressed
        while (!button.isPressed()) {
            cycleNumber++;
            System.out.println("========================================");
            System.out.println("CYCLE #" + cycleNumber);
            System.out.println("========================================");

            // -------------------------------------------------
            // STEP 1: Check moisture for 5 seconds (take average)
            // -------------------------------------------------
            System.out.println("[MOISTURE] Checking soil moisture for 5 seconds...");

            double avgVoltage = moisture.readAverageOver5Seconds();
            int moisturePercent = moisture.getPercentage(avgVoltage);

            System.out.println("[MOISTURE] Average: " + String.format("%.2f", avgVoltage) + "V (" + moisturePercent + "%)");
            System.out.println("[MOISTURE] Threshold: " + String.format("%.2f", dryThreshold) + "V");

            // -------------------------------------------------
            // STEP 2: Decide to water or not
            // -------------------------------------------------

            boolean isDry = avgVoltage > dryThreshold;
            String action;

            if (isDry) {
                // SOIL IS DRY - WATER FOR 5 SECONDS
                System.out.println("[DECISION] SOIL IS DRY! Watering for 5 seconds...");
                action = "WATERING";

                oled.showMessage("DRY!", "Watering for 5s");
                Thread.sleep(1000);

                // Water for 5 seconds
                pump.on();
                led.on();
                buzzer.play(200, 5000);  // Buzzer on for 5 seconds

                for (int i = 5; i > 0; i--) {
                    oled.showWatering(i);
                    System.out.println("  Watering... " + i + " seconds left");
                    Thread.sleep(1000);
                }

                pump.off();
                led.off();
                buzzer.stop();

            } else {
                // SOIL IS WET - NO WATERING
                System.out.println("[DECISION] SOIL IS WET! No watering needed.");
                action = "IDLE";

                oled.showWet();
                Thread.sleep(2000);
            }

            // -------------------------------------------------
            // STEP 3: Log data and update graph
            // -------------------------------------------------

            // Get current threshold from potentiometer (for next cycle)
            int potRaw = potentiometer.readRaw();
            int invertedRaw = 1023 - potRaw;
            double currentThreshold = 2.0 + (invertedRaw / 1023.0) * 1.6;
            int thresholdPercentCurrent = (invertedRaw * 100) / 1023;

            // Add to data logger (ArrayList + CSV)
            dataLogger.addReading(moisturePercent, currentThreshold, action);

            // Update live graph
            graph.addDataPoint(moisturePercent);

            System.out.println("[DATA] Cycle " + cycleNumber + " logged");

            // -------------------------------------------------
            // STEP 4: 60 SECOND WAIT (with live threshold adjustment)
            // -------------------------------------------------

            System.out.println("[WAIT] Entering 60 second wait period...");
            System.out.println("[WAIT] You can adjust the dry threshold using the potentiometer");

            for (int second = 60; second > 0; second--) {
                // Flash LED once per second (250ms on)
                led.on();
                Thread.sleep(250);
                led.off();

                // Read potentiometer for live threshold update
                int currentPotRaw = potentiometer.readRaw();
                int currentInverted = 1023 - currentPotRaw;
                double newThreshold = 2.0 + (currentInverted / 1023.0) * 1.6;
                int newThresholdPercent = (currentInverted * 100) / 1023;

                // Calculate time remaining percentage
                int timePercent = (second * 100) / 60;

                // Update OLED with double bar
                // Top bar: Time Remaining using '='
                // Bottom bar: Threshold using '#'
                oled.showDoubleBar(0, "Time Remaining", timePercent, '=',
                        32, "DRY Threshold", newThresholdPercent, '#');

                // Also show voltage value
                oled.showText(0, 55, String.format("Threshold: %.2fV", newThreshold));

                // Update the dry threshold variable for next cycle (user can change during wait)
                dryThreshold = newThreshold;
                thresholdPercent = newThresholdPercent;

                // Show status every 10 seconds in console
                if (second % 10 == 0) {
                    System.out.println("[WAIT] " + second + " seconds remaining... Threshold: " +
                            String.format("%.2f", dryThreshold) + "V");
                }

                Thread.sleep(750);  // Remaining time after LED flash
            }

            oled.clear();
            System.out.println("[WAIT] 60 seconds complete. Starting next cycle...\n");
        }

        // =========================================================
        // PHASE 7: EMERGENCY STOP (Button was pressed)
        // =========================================================

        System.out.println("\n!!! EMERGENCY STOP ACTIVATED !!!");

        // Stop pump immediately
        pump.off();

        // Show emergency on OLED
        oled.showEmergency();

        // Emergency buzzer pattern (siren-like)
        for (int i = 0; i < 6; i++) {
            buzzer.play(200, 150);
            Thread.sleep(100);
            buzzer.play(100, 150);
            Thread.sleep(100);
        }

        // Flash LED rapidly
        for (int i = 0; i < 10; i++) {
            led.on();
            Thread.sleep(100);
            led.off();
            Thread.sleep(100);
        }

        buzzer.stop();
        System.out.println("[EMERGENCY] System shutting down...");
        Thread.sleep(2000);

        // =========================================================
        // PHASE 8: CLEANUP AND EXIT
        // =========================================================

        System.out.println("\n[SYSTEM] Closing data logger...");
        dataLogger.close();

        System.out.println("[SYSTEM] Closing graph window...");
        graph.close();

        System.out.println("[SYSTEM] Disconnecting from Arduino...");
        myBoard.stop();

        System.out.println("[SYSTEM] Plant Watering System terminated.");
        System.exit(0);
    }
}