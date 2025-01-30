#include <Arduino.h>
#include <Adafruit_NeoPixel.h>
#include <math.h>

#define PERIODE_DEBUG 1000
#define PERIODE_SCAN 50
#define NeopixPin A1
#define NUMPIXELS 40

uint8_t pixel = 0;

Adafruit_NeoPixel strip(NUMPIXELS, NeopixPin, NEO_GRB + NEO_KHZ800);


uint8_t rows[3] = {12, 11, 10};
uint8_t cols[3] = {7, 8, 9};
bool cols_scan = false;

uint8_t btn = 99;       // Valeur par défaut (rien enfoncé)
uint8_t last_btn = 99;
uint8_t btn_tab[10] = {00, 01, 02, 10, 11, 12, 20, 21, 22, 99};

uint8_t col_select = 9; // Par défaut = 9 (pas de détection)
uint8_t row_select = 9;

uint32_t maintenant_debug = 0;
uint32_t maintenant_scan = 0;

uint8_t ligne_actuelle = 0;

void toucherBouton(uint8_t num_ligne, uint8_t num_col);
void scan();
void debug();
