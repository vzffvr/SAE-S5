#include <Arduino.h>

uint8_t rows[2] = {11, 12};
uint8_t cols[2] = {9, 10};
int col_scan;

void toucherBouton(uint8_t i, uint8_t j);

void setup()
{
  Serial.begin(115200);
  for (int i = 0; i <= 1; i++)
  {

    pinMode(rows[i], OUTPUT);
    pinMode(cols[i], INPUT);
    digitalWrite(cols[i], HIGH);
  }
}

void loop()
{
  // Regarde si un bouton est enfoncé
  for (int i = 0; i <= 1; i++)
  {
    digitalWrite(rows[0], HIGH);
    digitalWrite(rows[1], HIGH);
    //digitalWrite(rows[2], HIGH);

    digitalWrite(rows[i], LOW);
    for (int j = 0; j <= 1; j++)
    {
      col_scan = digitalRead(cols[j]);
      if (col_scan == LOW)
      {
        // Lorsqu'un bouton est enfoncé, appel de la fonction toucherBouton
        // pour savoir quel bouton est enfoncé
        toucherBouton(i, j);
        delay(300);
      }
    }
  }
}

void toucherBouton(uint8_t i, uint8_t j)
{
  if (i == 0 && j == 0) // Bouton S1 enfoncé
    Serial.println("Bas gauche");
  if (i == 0 && j == 1) // Bouton S2 enfoncé
    Serial.println("Haut gauche");
  if (i == 1 && j == 0) // Bouton S3 enfoncé
    Serial.println("Bas droit");
  if (i == 1 && j == 1) // Bouton S4 enfoncé
    Serial.println("Haut Droit");
}
