import org.firmata4j.IODevice;
import org.firmata4j.I2CDevice;
import org.firmata4j.ssd1306.SSD1306;

public class myOLED {

    private SSD1306 oled;
    private final int OLED_WIDTH = 128;
    private final int BAR_MAX_CHARS = 20;  // 20 characters for bar

    // Constructor
    public myOLED(IODevice board) throws Exception {
        I2CDevice i2cObject = board.getI2CDevice((byte) 0x3C);
        oled = new SSD1306(i2cObject, SSD1306.Size.SSD1306_128_64);
        oled.init();
        oled.getCanvas().clear();
        oled.display();
    }

    // Clear entire screen
    public void clear() {
        try {
            oled.getCanvas().clear();
            oled.display();
        } catch (Exception e) {
            System.out.println("Clear error: " + e.getMessage());
        }
    }

    // Show text at specific coordinates
    public void showText(int x, int y, String text) {
        try {
            oled.getCanvas().drawString(x, y, text);
            oled.display();
        } catch (Exception e) {
            System.out.println("Text error: " + e.getMessage());
        }
    }

    // Show text on a specific line (0 = top, 1, 2, 3 = bottom)
    public void showLine(int lineNumber, String text) {
        int y = lineNumber * 16;
        showText(0, y, text);
    }

    // Clear a specific line
    public void clearLine(int lineNumber) {
        showLine(lineNumber, "                    ");  // 20 spaces
    }

    // Show a progress bar with custom fill character
    // fillChar = '#' for threshold, '=' for time remaining
    public void showBar(int y, String title, int percent, char fillChar) {
        try {
            int numFilled = (percent * BAR_MAX_CHARS) / 100;

            // Build the bar: [filled...empty...]
            StringBuilder bar = new StringBuilder("[");
            for (int i = 0; i < numFilled; i++) {
                bar.append(fillChar);
            }
            for (int i = numFilled; i < BAR_MAX_CHARS; i++) {
                bar.append('-');
            }
            bar.append("]");

            // Show title
            oled.getCanvas().drawString(0, y, title);

            // Show bar
            oled.getCanvas().drawString(0, y + 16, bar.toString());

            // Show percentage on the right
            String percentText = percent + "%";
            oled.getCanvas().drawString(OLED_WIDTH - (percentText.length() * 6), y, percentText);

            oled.display();

        } catch (Exception e) {
            System.out.println("Bar error: " + e.getMessage());
        }
    }

    // Show two bars (for time remaining + threshold during wait)
    public void showDoubleBar(int y1, String title1, int percent1, char fillChar1,
                              int y2, String title2, int percent2, char fillChar2) {
        try {
            int numFilled1 = (percent1 * BAR_MAX_CHARS) / 100;
            int numFilled2 = (percent2 * BAR_MAX_CHARS) / 100;

            // Bar 1
            StringBuilder bar1 = new StringBuilder("[");
            for (int i = 0; i < numFilled1; i++) bar1.append(fillChar1);
            for (int i = numFilled1; i < BAR_MAX_CHARS; i++) bar1.append('-');
            bar1.append("]");

            // Bar 2
            StringBuilder bar2 = new StringBuilder("[");
            for (int i = 0; i < numFilled2; i++) bar2.append(fillChar2);
            for (int i = numFilled2; i < BAR_MAX_CHARS; i++) bar2.append('-');
            bar2.append("]");

            // Display
            oled.getCanvas().drawString(0, y1, title1);
            oled.getCanvas().drawString(0, y1 + 16, bar1.toString());
            oled.getCanvas().drawString(0, y2, title2);
            oled.getCanvas().drawString(0, y2 + 16, bar2.toString());

            // Percentages
            String percent1Text = percent1 + "%";
            String percent2Text = percent2 + "%";
            oled.getCanvas().drawString(OLED_WIDTH - (percent1Text.length() * 6), y1, percent1Text);
            oled.getCanvas().drawString(OLED_WIDTH - (percent2Text.length() * 6), y2, percent2Text);

            oled.display();

        } catch (Exception e) {
            System.out.println("Double bar error: " + e.getMessage());
        }
    }

    // Show large centered text (for countdown numbers 3,2,1)
    public void showLargeCentered(String text) {
        try {
            oled.getCanvas().clear();
            int x = (OLED_WIDTH - (text.length() * 12)) / 2;  // Approximate centering
            oled.getCanvas().drawString(x, 25, text);
            oled.display();
        } catch (Exception e) {
            System.out.println("Large text error: " + e.getMessage());
        }
    }

    // Show threshold selection screen
    public void showThresholdScreen(int percent, double voltage) {
        try {
            oled.getCanvas().clear();

            // Title
            oled.getCanvas().drawString(0, 0, "Set DRY Threshold");

            // Bar with # fill
            int numFilled = (percent * BAR_MAX_CHARS) / 100;
            StringBuilder bar = new StringBuilder("[");
            for (int i = 0; i < numFilled; i++) bar.append('#');
            for (int i = numFilled; i < BAR_MAX_CHARS; i++) bar.append('-');
            bar.append("]");
            oled.getCanvas().drawString(0, 20, bar.toString());

            // Current voltage
            String valueText = String.format("Current: %.2fV", voltage);
            oled.getCanvas().drawString(0, 40, valueText);

            // Instruction
            oled.getCanvas().drawString(0, 55, "Press button when ready");

            oled.display();

        } catch (Exception e) {
            System.out.println("Threshold screen error: " + e.getMessage());
        }
    }

    // Show emergency message
    public void showEmergency() {
        try {
            oled.getCanvas().clear();
            oled.getCanvas().drawString(15, 16, "!!! EMERGENCY !!!");
            oled.getCanvas().drawString(20, 32, "Turning off System");
            oled.display();
        } catch (Exception e) {
            System.out.println("Emergency error: " + e.getMessage());
        }
    }

    // Show simple two-line message
    public void showMessage(String line1, String line2) {
        try {
            oled.getCanvas().clear();
            oled.getCanvas().drawString(0, 16, line1);
            oled.getCanvas().drawString(0, 40, line2);
            oled.display();
        } catch (Exception e) {
            System.out.println("Message error: " + e.getMessage());
        }
    }

    // Show moisture check screen (during the 5-second sampling)
    public void showMoistureCheck(int secondsLeft, int percentComplete) {
        try {
            oled.getCanvas().clear();
            oled.getCanvas().drawString(0, 0, "Checking Moisture");
            oled.getCanvas().drawString(0, 20, "Time left: " + secondsLeft + "s");

            // Progress bar using '='
            int numFilled = (percentComplete * BAR_MAX_CHARS) / 100;
            StringBuilder bar = new StringBuilder("[");
            for (int i = 0; i < numFilled; i++) bar.append('=');
            for (int i = numFilled; i < BAR_MAX_CHARS; i++) bar.append('-');
            bar.append("]");
            oled.getCanvas().drawString(0, 40, bar.toString());

            oled.display();

        } catch (Exception e) {
            System.out.println("Moisture check error: " + e.getMessage());
        }
    }

    // Show watering screen
    public void showWatering(int secondsLeft) {
        try {
            oled.getCanvas().clear();
            oled.getCanvas().drawString(0, 0, "DRY!");
            oled.getCanvas().drawString(0, 20, "Watering for 5s");
            oled.getCanvas().drawString(0, 40, "Remaining: " + secondsLeft + "s");
            oled.display();
        } catch (Exception e) {
            System.out.println("Watering error: " + e.getMessage());
        }
    }

    // Show wet message
    public void showWet() {
        try {
            oled.getCanvas().clear();
            oled.getCanvas().drawString(0, 20, "WET!");
            oled.getCanvas().drawString(0, 40, "No watering needed");
            oled.display();
        } catch (Exception e) {
            System.out.println("Wet error: " + e.getMessage());
        }
    }
}