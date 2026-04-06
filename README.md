# 🪴 Auto Plant Watering System (Java & Arduino)

A modern, high-tech plant watering system that keeps your green friends happy automatically! This project uses **Java (Firmata4j)** to talk to an **Arduino** board, providing a real-time monitoring dashboard, data logging, and an interactive hardware setup.

---

## ✨ Features
- **💧 Smart Watering:** Automatically detects soil moisture and waters the plant for 5 seconds when it gets too dry.
- **🎛️ Interactive Calibration:** Use a potentiometer to set your custom "Dry Threshold" visually on an OLED screen.
- **📊 Real-time Monitoring:**
    - **OLED Display:** Shows live moisture levels, thresholds, and watering status.
    - **Live Graphing:** A pop-up window shows soil moisture trends over time.
    - **Data Logging:** Every cycle is logged into a CSV file (`water_log.csv`) for future analysis.
- **🚨 Emergency Stop:** A physical button allows you to stop the system immediately in case of overflow or issues.
- **🔊 Audio-Visual Feedback:** Integrated Buzzer and LED patterns for system status (startup countdown, watering, and emergency).

---

## 🛠️ Hardware Requirements
To build this, you'll need:
1. **Arduino Uno** (or compatible board)
2. **Capacitive Soil Moisture Sensor** (Connected to **A2**)
3. **Potentiometer** (Connected to **A0**)
4. **Water Pump** (Connected via Relay/Transistor to **D7**)
5. **SSD1306 OLED Display** (I2C - SDA/SCL)
6. **LED** (Connected to **D4**)
7. **Buzzer** (Connected to **D5**)
8. **Push Button** (Connected to **D6**)

---

## 🚀 Getting Started (Step-by-Step)

If you've never used GitHub or Java before, follow these steps:

### 1. Prepare your Arduino
1. Open the **Arduino IDE**.
2. Go to `File` -> `Examples` -> `Firmata` -> `StandardFirmata`.
3. Select your board (Arduino Uno) and Port.
4. Click **Upload**. Your Arduino is now ready to talk to Java!

### 2. Download the Project
1. Scroll to the top of this page.
2. Click the green **Code** button and select **Download ZIP**.
3. Extract the ZIP folder to a place on your computer (e.g., your Documents).

### 3. Open in IntelliJ IDEA
1. Download and install **IntelliJ IDEA Community Edition** (it's free!).
2. Open IntelliJ, click **Open**, and navigate to the extracted `WateringSystemJAVA` folder.
3. Wait for IntelliJ to load the project.

### 4. Setup Libraries
If IntelliJ says "Project SDK not defined" or "Library missing":
1. Go to `File` -> `Project Structure`.
2. Under **Project SDK**, select Java (JDK 17 or higher).
3. Under **Libraries**, ensure the `lib` folder is included. If not, click the `+` icon, select "Java", and point to the `lib` folder in this project.

### 5. Run the Program
1. Open `src/main.java`.
2. Look for the line: `String myPort = "COM5";`
3. Change `"COM5"` to the port your Arduino is plugged into (e.g., `"COM3"`, `"COM4"`, or `"/dev/ttyUSB0"` on Mac/Linux).
4. Click the **Green Play Button** next to `public class main`.

---

## 📖 How to Use
1. **Startup:** The system will connect to your Arduino.
2. **Threshold Setting:** Turn the potentiometer knob to set how "dry" you want the soil to be before it waters. Press the **Button** (D6) once to confirm.
3. **Countdown:** The system will count down (3, 2, 1) with beeps and lights.
4. **Loop:** The system checks moisture every 60 seconds.
    - If it's too dry, the pump runs for 5 seconds.
    - If it's wet enough, it stays idle.
5. **Adjustment:** During the 60-second wait, you can adjust the dry threshold live using the knob.
6. **Emergency:** Press the **Button** (D6) at any time to trigger an emergency shutdown.

---

## 📂 Project Structure
- `src/`: Contains all Java source code.
- `lib/`: All necessary `.jar` libraries for Firmata4j, Graphing, and Logging.
- `water_log.csv`: Automatically generated file containing your plant's data history.

---
*Created with ❤️ for smarter plants.*
