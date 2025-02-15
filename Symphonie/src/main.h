#include <BLE_Midi.h>
#include <Keypad.h>
#include <Animation_Neopix.h>
#include <Mozzi.h>
#include <Oscil.h> // oscillator template
#include <tables/sin4096_int8.h> // sine table for oscillator
#include <tables/triangle_dist_cubed_2048_int8.h> // sine table for oscillator
#include <tables/SAW4096_int8.h> // sine table for oscillator
#include <tables/waveshape_chebyshev_6th_256_int8.h> // sine table for oscillator


#define MOZZI_CONTROL_RATE 128 // MAX1024 mais latence
#define  ROWS  5 // 5 lignes
#define  COLS  5 // 5 colonnes
#define NUM_FREQUENCIES 24
// #define CONNECTION_LED 10


byte rowPins[ROWS] = {16, 17, 18, 4, 19}; // 17 // Lignes
byte colPins[COLS] = {13, 25, 14, 32, 33};  // Colonnes

String state = "";

int key_pressed[3] = {9999,9999,9999};

char keys[ROWS][COLS] = {
  {'0', '1', '2', '3', '3'},  //Premiere octave [C;B]
  {'4','5', '6', '7', '8'},
  { '9','A', 'B', 'C', 'D'}, 
  { 'E', 'F', 'G', 'H', 'I'},
  { 'J', 'K', 'L', 'M', 'O'}
};

const float frequencies[NUM_FREQUENCIES] = {
  20, 43, 85, 129, 173, 261, 349, 440, 523, 698, 880, 1046,
  1397, 1760, 2093, 2794, 3520, 4186, 5588, 7040, 8372, 11175, 14080, 20000
};

void add2pressed_key(uint8_t key);
void remove_from_pressed_key(uint8_t key);
void scanKeyboard();
