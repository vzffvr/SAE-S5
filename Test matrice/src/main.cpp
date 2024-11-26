#include <Arduino.h>

uint8_t rows[3] = {12,11, 10};
uint8_t cols[3] = {6,7,8};
int cols_scan = 0;
int cpt = 0;
uint8_t btn = 00;
uint8_t last_btn = 00;
uint8_t btn_tab[25] = {00,01,02,10,11,12,20,21,22};

void toucherBouton(uint8_t num_ligne, uint8_t num_col);
void scan();

void setup()
{
  Serial.begin(115200);
  for (int i = 0; i < 3; i++)
  {
    pinMode(rows[i], OUTPUT);
    pinMode(cols[i], INPUT_PULLUP);
  }

}

void loop()
{
  for (int num_ligne = 0; num_ligne < 3; num_ligne++)
  {
    digitalWrite(rows[0], LOW);
    digitalWrite(rows[1], LOW);
    digitalWrite(rows[2], LOW);
    digitalWrite(rows[num_ligne], HIGH);

    for (int num_col = 0; num_col < 3; num_col++)
    {
      cols_scan = digitalRead(cols[num_col]);
      if (cols_scan == LOW)
      {
      toucherBouton(num_col, num_ligne);    
      delay(300);
      }  
    }
  }  
  if(btn==last_btn)
  {
    cpt++;
  }
  else
  {
    cpt=0;
  }
  if(cpt >= 10)
  {
    Serial.println(btn);
    Serial.print("cpt = ");
    Serial.println(cpt);
    // Serial.print(num_col);
    // Serial.println(num_ligne);
  }
  last_btn = btn;
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
