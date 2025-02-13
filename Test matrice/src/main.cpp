#include <Arduino.h>
#include <main.h>
#include <Keypad.h>

#define BUZZER_PIN 25 // Broche pour le buzzer
#define BUZZER_CHANNEL 0

#define  ROWS  3 // 5 lignes
#define  COLS  3 // 5 colonnes

// Broches compatibles ESP32
byte rowPins[ROWS] = {D13, A0, A1}; // Lignes
byte colPins[COLS] = {D12, D11, D10};  // Colonnes

char keys[ROWS][COLS] = {
  {'1', '2', '3'},
  {'6', '7', '8'},
  {'B', 'C', 'D'}
};

Keypad keypad = Keypad(makeKeymap(keys), rowPins, colPins, ROWS, COLS);


void setup()
{
  Serial.begin(115200);

  // //Boutons
  // for (int i = 0; i < 3; i++)
  // {
  //   pinMode(rows[i], OUTPUT);
  //   digitalWrite(rows[i], LOW); // Initialise les lignes à LOW
  //   pinMode(cols[i], INPUT_PULLUP); // Active les résistances internes PULLUP
  // }

  for (int i = 0; i < COLS; i++) {
    pinMode(colPins[i], INPUT_PULLUP);
  }
  maintenant_debug = millis();
  maintenant_scan = millis();
  
  //Neopixel
  strip.begin();
  strip.setBrightness(200);
  strip.show();
  //tone32_begin(TONE_PIN);  
}

void loop()
{

  char key = keypad.getKey();

    if (key) { // Si une touche est détectée
        Serial.print("Touche pressée : ");
        Serial.println(key);
    }

  // for(int i = 0; i<10; i++)
  // {
  //   strip.setPixelColor(i,strip.Color(0,100,0));
  //   strip.show();
  //   Serial.println(strip.getBrightness());
  // }
  // scan();
  // debug();
}



void scan(){
  if (millis() - maintenant_scan >  PERIODE_SCAN)
  {
    maintenant_scan = millis();
    col_select = 9;
    row_select = 9;

    
    // Réinitialisation des lignes
    for (int i = 0; i < 3; i++)
    {
      digitalWrite(rows[i], LOW);// Désactive toutes les lignes
    }
    digitalWrite(rows[ligne_actuelle], HIGH);
    delay(5);

    for (int num_col = 0; num_col < 3; num_col++)
    {
      if (digitalRead(cols[num_col]) == LOW) // Détection état bas
      {
          col_select = num_col;
          row_select = ligne_actuelle;
        
      }
    }
    ligne_actuelle = (ligne_actuelle+1)%3;
  }
  toucherBouton(row_select, col_select); 
}

void debug(){
    // Affichage pour le débogage
  if ((maintenant_debug + PERIODE_DEBUG) < millis())
  {
    maintenant_debug = millis();
    Serial.printf("Bouton : %d,\t Colonne : %d,\t Ligne : %d\n", btn, col_select, row_select);
  }
}

void toucherBouton(uint8_t num_ligne, uint8_t num_col)
{
  if (num_col == 0 && num_ligne == 0) btn = btn_tab[0];
  else if (num_col == 0 && num_ligne == 1) btn = btn_tab[1];
  else if (num_col == 0 && num_ligne == 2) btn = btn_tab[2];
  else if (num_col == 1 && num_ligne == 0) btn = btn_tab[3];
  else if (num_col == 1 && num_ligne == 1) btn = btn_tab[4];
  else if (num_col == 1 && num_ligne == 2) btn = btn_tab[5];
  else if (num_col == 2 && num_ligne == 0) btn = btn_tab[6];
  else if (num_col == 2 && num_ligne == 1) btn = btn_tab[7];
  else if (num_col == 2 && num_ligne == 2) btn = btn_tab[8];
  else btn = btn_tab[9]; // Aucune touche détectée
}

