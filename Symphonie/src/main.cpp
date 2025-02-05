#include <Arduino.h>
#include <BLE_Midi.h>
#include <Keypad.h>
#include <Animation_Neopix.h>

#define  ROWS  5 // 5 lignes
#define  COLS  5 // 5 colonnes
#define LED_PIN 10


uint32_t maintenant_debug = 0;

// byte rowPins[ROWS] = {13, 12, 14, 27, 26}; // Lignes
// byte colPins[COLS] = {25, 33, 32, 34, 34};  // Colonnes


// String keys[ROWS][COLS] = {
//   {"C#", "C#1", "D1", "D#1", "E1"},  //Premiere octave [C;B]
//   {"F1","F#1", "G1", "G#1", "A1"},
//   { "A#1","B1", "C", "C#", "D"}, 
//   { "D#", "E", "F", "F#", "G"},
//   { "G#", "A", "A#", "B", ""}
// };

// Keypad keypad = Keypad(makeKeymap(keys), rowPins, colPins, ROWS, COLS);
Adafruit_NeoPixel strip(NUMPIXELS, LED_PIN, NEO_GRB + NEO_KHZ800);
Animation_Neopix anim;
BLE_Midi ble_midi(anim);

void keypadEvent(KeypadEvent key);

void setup() {
  Serial.begin(115200);
  ble_midi.initBLE();
  // keypad.addEventListener(keypadEvent);
  
  // for (int i = 0; i < COLS; i++) { // Essaie sans
  //   pinMode(colPins[i], INPUT_PULLUP);
  // }
  maintenant_debug = millis();
}

void loop() {
  // put your main code here, to run repeatedly:
    // char key = keypad.getKey();
    

}

// void keypadEvent(KeypadEvent key){
//     switch (keypad.getState()){
//     case PRESSED:
//         Serial.println("Key pressed" + key);
//         break;

//     case RELEASED:
//         Serial.println("Key released" + key);
//         break;

//     case HOLD:
//         break;
//     }
// }
