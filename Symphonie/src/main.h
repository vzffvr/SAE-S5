#include <BLE_Midi.h>
#include <Keypad.h>
#include <Animation_Neopix.h>
#include <Mozzi.h>
#include <Oscil.h> // oscillator template
#include <tables/sin4096_int8.h> // sine table for oscillator
#include <tables/phasor256_int8.h> // sine table for oscillator
#include <tables/SAW4096_int8.h> // sine table for oscillator
#include <tables/square_no_alias_2048_int8.h> // sine table for oscillator


// #define MOZZI_CONTROL_RATE 512 // MAX1024 mais latence
#define  ROWS  5 // 5 lignes
#define  COLS  5 // 5 colonnes
#define NUM_FREQUENCIES 24
#define MAX_FREQUENCIE 10000
#define MAX_TILES_HELD 5

#define CONNECTION_LED 21

byte rowPins[ROWS] = {16, 17, 18, 4, 19}; // 17 // Lignes
//18, 17, 4, ,16 ,19

byte colPins[COLS] = {13, 27, 14, 12, 33};  // Colonnes

String state = "";

// int key_pressed[MAX_TILES_HELD];

char keys[ROWS][COLS] = {
  {'0', '1', '2', '3', 'p'},  //Premiere octave [C;B]
  {'4','5', '6', '7', '8'},
  { '9','A', 'B', 'C', 'D'},  //Deuxieme de C2 B2
  { 'E', 'F', 'G', 'H', 'I'},
  { 'J', 'K', 'L', 'M', 'O'}
};

float frequencies[NUM_FREQUENCIES] = {
  20, 43, 85, 129, 173, 261, 349, 440, 523, 698, 880, 1046,
  1397, 1760, 2093, 2794, 3520, 4186, 5588, 7040, 8372, 11175, 14080, 200
};

// float frequencies[NUM_FREQUENCIES] = {
//     20, 43, 85, 129, 173, 261, 349, 440, 523, 698, 880,
//     1046, 1397, 1760, 2093, 2794, 3520
// };

void add2pressed_key(uint8_t key);
void remove_from_pressed_key(uint8_t key);
void scanKeyboard();
void SetSignalForm();
void resetTabOfPressedKeys();
