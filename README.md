# Auto Plant Watering System (Java & Arduino)

A modern, high-tech plant watering system that keeps your green friends happy automatically! This project uses **Java (Firmata4j)** to talk to an **Arduino** board, providing a real-time monitoring dashboard, data logging, and an interactive hardware setup.

Live Demo: [tasdidnoor.com/WateringSystemJAVA](https://tasdidnoor.com/WateringSystemJAVA/)

<p align="center">
  <img src="https://raw.githubusercontent.com/tasdidnoor/Assets/main/PlantWatering_JAVA/MainSetup.png" width="97%" alt="Main Setup" />
</p>
<p align="center">
  <img src="https://raw.githubusercontent.com/tasdidnoor/Assets/main/PlantWatering_JAVA/FlowChart.png" width="97%" alt="System Flowchart" />
</p>

---

## Features
- **Smart Watering:** Automatically detects soil moisture and waters the plant for 5 seconds when it gets too dry.
- **Interactive Calibration:** Use a potentiometer to set your custom "Dry Threshold" visually on an OLED screen.
- **Real-time Monitoring:**
    - **OLED Display:** Shows live moisture levels, thresholds, and watering status.
    - **Live Graphing:** A pop-up window shows soil moisture trends over time.
    - **Data Logging:** Every cycle is logged into a CSV file (`water_log.csv`) for future analysis.
- **Emergency Stop:** A physical button allows you to stop the system immediately in case of overflow or issues.
- **Audio-Visual Feedback:** Integrated Buzzer and LED patterns for system status (startup countdown, watering, and emergency).

---

## Hardware Requirements
To build this, you'll need:
1. **Arduino Uno**
2. **Capacitive Soil Moisture Sensor** (Connected to **A2**)
3. **Potentiometer** (Connected to **A0**)
4. **Water Pump** (Connected via Relay/Transistor to **D7**)
5. **SSD1306 OLED Display** (I2C)
6. **LED** (Connected to **D4**)
7. **Buzzer** (Connected to **D5**)
8. **Push Button** (Connected to **D6**)

---

## Calibration & Testing
The system uses voltage-based thresholding for precise moisture control. Calibration is performed using a multimeter to find the optimal dry and wet voltage points.

| Dry Threshold Calibration | Wet Threshold Calibration |
| :---: | :---: |
| <img src="https://raw.githubusercontent.com/tasdidnoor/Assets/main/PlantWatering_JAVA/Multimeter1.jpeg" width="400" /> | <img src="https://raw.githubusercontent.com/tasdidnoor/Assets/main/PlantWatering_JAVA/Multimeter2.jpeg" width="400" /> |
| Measuring voltage for dry soil | Measuring voltage for wet soil |

---

## Getting Started

### 1. Prepare your Arduino
1. Open the **Arduino IDE**.
2. Go to `File` -> `Examples` -> `Firmata` -> `StandardFirmata`.
3. Select your board (Arduino Uno) and Port.
4. Click **Upload**.

### 2. Setup IntelliJ IDEA
1. Open the project in **IntelliJ IDEA**.
2. Configure **Project SDK** (JDK 17+).
3. Ensure all `.jar` files in the `lib` folder are added as project libraries.

### 3. Run the Program
1. Open `src/Main.java`.
2. Update the `myPort` string with your Arduino's COM port (e.g., `"COM5"`).
3. Run the `main` method.

---

## How to Use
1. **Startup:** The system will connect to your Arduino.
2. **Threshold Setting:** Turn the potentiometer knob to set how "dry" you want the soil to be before it waters. Press the **Button** (D6) once to confirm.
3. **Loop:** The system checks moisture every 60 seconds.
    - If it's too dry, the pump runs for 5 seconds.
4. **Adjustment:** During the 60-second wait, you can adjust the dry threshold live using the knob.
5. **Emergency:** Press the **Button** (D6) at any time to trigger an emergency shutdown.

---

## License
This project is licensed under the [MIT License](LICENSE).

## Contact
If you have any questions or need assistance, feel free to reach out at: contact@tasdidnoor.com

---
*Smart Irrigation for Smart Plants.*
