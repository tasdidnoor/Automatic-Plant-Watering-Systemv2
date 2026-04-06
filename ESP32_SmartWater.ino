#include <WiFi.h>
#include <WebServer.h>

// Replace with your network credentials
const char* ssid = "YOUR_WIFI_SSID";
const char* password = "YOUR_WIFI_PASSWORD";

WebServer server(80);

// Hardware Pins (Adjusted for ESP32 C3 Supermini)
// Note: ESP32 C3 Supermini pinout might differ from Standard Arduino
// Assuming D7 and D5 maps to GPIO 7 and GPIO 5
const int WATERING_PIN = 7;
const int EMERGENCY_PIN = 5;

String systemStatus = "Ready";
String operationMode = "STANDBY";

void handleRoot() {
  String html = "<!DOCTYPE html><html><head>";
  html += "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">";
  html += "<style>body{background:#000;color:#fff;font-family:sans-serif;text-align:center;padding-top:50px;}";
  html += ".status{font-size:48px;font-weight:bold;margin:20px;}";
  html += ".Ready{color:#888;} .Watering{color:#007aff;} .Emergency{color:#ff453a;}";
  html += "</style><meta http-equiv='refresh' content='2'></head><body>";
  html += "<h1>SmartWater Dashboard</h1>";
  html += "<div class='status " + systemStatus + "'>" + systemStatus + "</div>";
  html += "<div>Mode: " + operationMode + "</div>";
  html += "</body></html>";
  server.send(200, "text/html", html);
}

void handleStatus() {
  String json = "{\"status\":\"" + systemStatus + "\", \"mode\":\"" + operationMode + "\"}";
  server.send(200, "application/json", json);
}

void setup() {
  Serial.begin(115200);
  
  pinMode(WATERING_PIN, INPUT);
  pinMode(EMERGENCY_PIN, INPUT);

  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.print("Connected to WiFi. IP: ");
  Serial.println(WiFi.localIP());

  server.on("/", handleRoot);
  server.on("/status", handleStatus);
  server.begin();
}

void loop() {
  server.handleClient();

  // Read signals from Grove board
  bool isWatering = digitalRead(WATERING_PIN) == HIGH;
  bool isEmergency = digitalRead(EMERGENCY_PIN) == HIGH;

  if (isEmergency) {
    systemStatus = "Emergency";
    operationMode = "SHUTDOWN";
  } else if (isWatering) {
    systemStatus = "Watering";
    operationMode = "ACTIVE PUMP";
  } else {
    systemStatus = "Ready";
    operationMode = "STANDBY";
  }

  delay(100);
}
