#include <Arduino.h>
#include <BLE_Midi.h>

#define PERIODE 1000
#define PERIODE_SCAN 30

BLE_Midi ble_midi;

uint8_t rows[3] = {9, 11, 10};
uint8_t cols[3] = {7, 8, 1};
bool cols_scan = false;
int cpt = 0;
uint8_t btn = 00;
uint8_t last_btn = 00;
uint8_t btn_tab[25] = {00, 01, 02, 10, 11, 12, 20, 21, 22, 99};

uint8_t col_select = 0;
uint8_t row_select = 0;

uint32_t maintenant_debug = 0;
uint32_t maintenant_scan = 0;

void toucherBouton(uint8_t num_ligne, uint8_t num_col);
void scan();

void setup()
{
  Serial.begin(115200);
  for (int i = 0; i < 3; i++)
  {
    pinMode(rows[i], OUTPUT);
    pinMode(cols[i], INPUT);
  }
  maintenant_debug = millis();
  maintenant_scan = millis();

  ble_midi.initBLE();
}

void loop()
{
  if ((maintenant_scan + PERIODE_SCAN) < millis())
  {
    maintenant_scan = millis();

    for (int num_ligne = 0; num_ligne < 3; num_ligne++)
    {
      // Réinitialisation des lignes
      for (int i = 0; i < 3; i++)
      {
        digitalWrite(rows[i], LOW);
      }
      digitalWrite(rows[num_ligne], HIGH);

      delay(5); // Stabilisation des lignes

      for (int num_col = 0; num_col < 2; num_col++)
      {
        cols_scan = digitalRead(cols[num_col]);
        if (cols_scan == LOW)
        {
          delay(10); // Anti-rebond
          if (digitalRead(cols[num_col]) == LOW)
          { // Vérification stable
            col_select = num_col;
            row_select = num_ligne;
          }
        }
      }
    }
  }

  // Traitement du bouton détecté
  toucherBouton(col_select, row_select);

  // Affichage pour le débogage
  if ((maintenant_debug + PERIODE) < millis())
  {
    maintenant_debug = millis();
    Serial.printf("bouton %d,\t colonne: %d,\t ligne : %d\n", btn, col_select, row_select);
  }

  last_btn = btn;

  // if((btn==last_btn) && (btn =! 99))
  // {
  //   cpt++;
  // }
  // else
  // {
  //   cpt=0;
  // }
  // if(cpt >= 10)
  // {
  //   Serial.println(btn);
  //   Serial.print("cpt = ");
  //   Serial.println(cpt);
  //   // Serial.print(num_col);
  //   // Serial.println(num_ligne);
  // }
  // last_btn = btn;
}

void toucherBouton(uint8_t num_col, uint8_t num_ligne)
{
  if (num_col == 0 && num_ligne == 0) // Bouton S1 enfoncé
    btn = btn_tab[0];
  if (num_col == 0 && num_ligne == 1) // Bouton S2 enfoncé
    btn = btn_tab[1];
  if (num_col == 0 && num_ligne == 2) // Bouton S2 enfoncé
    btn = btn_tab[2];

  if (num_col == 1 && num_ligne == 0) // Bouton S3 enfoncé
    btn = btn_tab[3];
  if (num_col == 1 && num_ligne == 1) // Bouton S4 enfoncé
    btn = btn_tab[4];
  if (num_col == 1 && num_ligne == 2) // Bouton S4 enfoncé
    btn = btn_tab[5];

  if (num_col == 2 && num_ligne == 0) // Bouton S4 enfoncé
    btn = btn_tab[6];
  if (num_col == 2 && num_ligne == 1) // Bouton S4 enfoncé
    btn = btn_tab[7];
  if (num_col == 2 && num_ligne == 2) // Bouton S4 enfoncé
    btn = btn_tab[8];
}
