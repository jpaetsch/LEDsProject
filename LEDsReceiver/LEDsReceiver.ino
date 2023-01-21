#include <Arduino.h>
#include <ArduinoJson.h>
#include <ESPmDNS.h>
#include <FastLED.h>
#include <WebServer.h>
#include <WiFi.h>

#include "pattern.h"
#include "patternOff.h"
#include "patternSolid.h"


// Wifi Configuration Constants
const char* SSID = "DiagonAlley";             // Network name to connect to
const char* PWD = "selmaN9687";               // Network password
const char* HOSTNAME = "LEDsReceiver-Jacob";  // Hostname and service name - MUST contain LEDsReceiver and then a unique id

// FastLED Configuration Constants
#define LED_TYPE WS2812B                      // LED chipset
#define COLOR_ORDER GRB                       // Expected color ordering for setting proper values
#define NUM_LEDS 900                          // Number of LEDs in the overall system
#define DATA_PIN 12                           // GPIO pin connected to the strip

// General Configuration Constants and Global Variables
CRGBArray<NUM_LEDS> leds;                     // Addressable LED strip array
Pattern *pattern;                             // Current LED Pattern
WebServer server(80);                         // Web server running on port 80
StaticJsonDocument<250> jsonDocument;         // Json document that can be worked with
char jsonBuffer[250];                         // Json character array for serialized output


/*
  Connect to the WiFi network and set hostname
*/
void initWiFi() {
  WiFi.setHostname(HOSTNAME);

  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(SSID);
  Serial.println();

  WiFi.begin(SSID, PWD);

  while(WiFi.status() != WL_CONNECTED) {
      delay(300);
      Serial.print(".");
  }

  Serial.println();
  Serial.print("WiFi connected with IP address: ");
  Serial.println(WiFi.localIP());
  Serial.println();
}

/*
  Set HTTP server callback functions
*/
void initCallbacks() {
  server.on("/", handleOnConnect);
  server.on("/getStatus", handleGetStatus);
  server.on("/turnOff", HTTP_POST, handleTurnOff);
  server.on("/setPattern", HTTP_POST, handleSetPattern);
  server.onNotFound(handleNotFound);
}

/*
  Set up the mDNS responder to allow for network service discovery
*/
void initMDNS() {

  Serial.println();
  Serial.print("Setting up service discovery");
  Serial.println();
  if(!MDNS.begin(HOSTNAME)) {
    Serial.println("Error starting mDNS - necessary for device discovery and connection");
    return;
  }
  MDNS.addService("http", "tcp", 80);
}

/*
  Set up LED strip and initial off state
*/
void initLEDs() {
  FastLED.addLeds<LED_TYPE, DATA_PIN, COLOR_ORDER>(leds, NUM_LEDS);
  pattern = new PatternOff(leds, NUM_LEDS);
  pattern->initPattern();
}

/*
  Send an HTTP response with a simple json 'response' payload for server event handlers
*/
void sendJsonResponse(int code, String msg) {
  jsonDocument.clear();
  jsonDocument["response"] = msg;
  serializeJson(jsonDocument, jsonBuffer);
  server.send(code, "application/json", jsonBuffer);
}

/*
  Send an HTTP status message - currently a simple payload but will be extended in the future
*/
void sendJsonStatus(int code, String status) {
  jsonDocument.clear();
  jsonDocument["status"] = status;
  serializeJson(jsonDocument, jsonBuffer);
  server.send(code, "application/json", jsonBuffer);
}

/*
  Allocate and set LED's based on parsing the deserialized JSON document fields
  Returns true if the pattern was parsed and set and false if it could not be set due to a parse error
*/
bool parseAndSetPattern() {
  uint8_t id = jsonDocument["id"] || NULL;
  if(!id) {
    return false;
  }

  switch(id) {
    case 0:
      delete pattern;
      pattern = new PatternOff(leds, NUM_LEDS);

    case 1: {
      uint8_t h = jsonDocument["hue"] || NULL;
      uint8_t s = jsonDocument["saturation"] || NULL;
      uint8_t v = jsonDocument["value"] || NULL;  
      if(!h || !s || !v) {
        return false;
      }

      delete pattern;
      pattern = new PatternSolid(leds, NUM_LEDS, h, s, v);
    }

    default:
      return false;
    
    pattern->initPattern();
    return true;
  }
}

/*
  Handler to provide static root connection response describing where to go for api usage
*/
void handleOnConnect() {
  String rootResponse = "Online and accessible - see https://github.com/jpaetsch/LEDsReceiver/wiki/Public-API for API routes and usage";
  sendJsonStatus(200, rootResponse);
}

/*
  Handler to provide the current status - could be extended to also provide info on recent errors, etc.
*/
void handleGetStatus() {
  String currentStatus = "Current pattern id = " + String(pattern->getPatternID());
  sendJsonStatus(200, currentStatus);
}

/*
  Handler to turn the LEDs off; the server will continue running
*/
void handleTurnOff() {
  delete pattern;
  pattern = new PatternOff(leds, NUM_LEDS);
  pattern->initPattern();
  sendJsonResponse(200, "Turned LEDs off");
}

/*
  Handler to set an LED pattern
*/
void handleSetPattern() {
  if(server.hasArg("plain") == false) {
    sendJsonResponse(400, "Unable to set pattern - argument error");
  } else {
    String body  = server.arg("plain");
    DeserializationError error = deserializeJson(jsonDocument, body);
    if(error) {
      sendJsonResponse(400, "Unable to set pattern - deserialization error");
      // Serial.println(error.f_str());
    } else {
      if(!parseAndSetPattern()) {
        sendJsonResponse(400, "Unable to set pattern - parse error");
      } else {
        char successMsg[30];
        sprintf(successMsg, "Pattern set to id = %d", pattern->getPatternID());
        sendJsonResponse(200, successMsg);
      }
    }
  }
}

/*
  Handler for unknown or invalid routes
*/
void handleNotFound() {
  sendJsonResponse(404, "Could not find the requested resource");
}


/*
  Arduino specific initialization function
*/
void setup() {
  Serial.begin(115200);

  // Initialize WiFi, configure HTTP callback server, and set up mDNS responder
  initWiFi();
  initCallbacks();
  initMDNS();
  
  // Initialize LEDs and configure startup state
  initLEDs();

  // Start the server
  server.begin();
}

/*
  Arduino specific looping function
*/
void loop() {
  server.handleClient();
  pattern->updatePattern();
}
