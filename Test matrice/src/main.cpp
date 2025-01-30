#include <Arduino.h>
#include <main.h>
#include <Tone32.h>

#define BUZZER_PIN 25 // Broche pour le buzzer
#define BUZZER_CHANNEL 0


void setup()
{
  Serial.begin(115200);

  //Boutons
  for (int i = 0; i < 3; i++)
  {
    pinMode(rows[i], OUTPUT);
    digitalWrite(rows[i], LOW); // Initialise les lignes à LOW
    pinMode(cols[i], INPUT_PULLUP); // Active les résistances internes PULLUP
  }
  maintenant_debug = millis();
  maintenant_scan = millis();
  
  //Neopixel
  strip.begin();
  strip.setBrightness(200);
  strip.show();
  tone32_begin(TONE_PIN);  
}

void loop()
{

  for(int i = 0; i<10; i++)
  {
    strip.setPixelColor(i,strip.Color(0,100,0));
    strip.show();
    Serial.println(strip.getBrightness());
  }
  scan();
  debug();
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
        delay(10); // Anti-rebond court
        if (digitalRead(cols[num_col]) == LOW) // Vérification stable
        {
          col_select = num_col;
          row_select = ligne_actuelle;
        }
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
