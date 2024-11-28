#include <Arduino.h>

#define PERIODE 1000
#define PERIODE_SCAN 300


uint8_t rows[3] = {12,11, 10};
uint8_t cols[3] = {7,8,9};
bool cols_scan = false;
int cpt = 0;
uint8_t btn = 00;
uint8_t last_btn = 00;
uint8_t btn_tab[25] = {00,01,02,10,11,12,20,21,22,99};

uint8_t col_select = 0;
uint8_t row_select = 0;

uint32_t maintenant_debug  = 0;
uint32_t maintenant_scan  = 0;



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
}

void loop()
{
  if ( (maintenant_debug + PERIODE) < millis())
  {
    digitalWrite(rows[0], HIGH);
    digitalWrite(rows[1], LOW);
    digitalWrite(rows[2], LOW);
    if (digitalRead(cols[0]) == LOW)
    {
      col_select = 0;
      row_select = 0;
    }  
    if (digitalRead(cols[1]) == LOW)
    {
      col_select = 1;
      row_select = 0;
    }  
    if (digitalRead(cols[2]) == LOW)
    {
      col_select = 2;
      row_select = 0;
    }

    digitalWrite(rows[0], LOW);
    digitalWrite(rows[1], HIGH);
    digitalWrite(rows[2], LOW);
    if (digitalRead(cols[0]) == LOW)
    {
      col_select = 0;
      row_select = 1;
    }  
    if (digitalRead(cols[1]) == LOW)
    {
      col_select = 1;
      row_select = 1;
    }  
    if (digitalRead(cols[2]) == LOW)
    {
      col_select = 2;
      row_select = 1;
    }

    digitalWrite(rows[0], LOW);
    digitalWrite(rows[1], LOW);
    digitalWrite(rows[2], HIGH);
    if (digitalRead(cols[0]) == LOW)
    {
      col_select = 0;
      row_select = 2;
    }  
    if (digitalRead(cols[1]) == LOW)
    {
      col_select = 1;
      row_select = 2;
    }  
    if (digitalRead(cols[2]) == LOW)
    {
      col_select = 2;
      row_select = 2;
    }

    toucherBouton(col_select, row_select);
  }
  

    




  // for (int num_ligne = 0; num_ligne < 3; num_ligne++)
  // {
  //   digitalWrite(rows[0], LOW);
  //   digitalWrite(rows[1], LOW);
  //   digitalWrite(rows[2], LOW);
  //   digitalWrite(rows[num_ligne], HIGH);

  //   for (int num_col = 0; num_col < 3; num_col++)
  //   {
  //     cols_scan = digitalRead(cols[num_col]);
  //     if (cols_scan == LOW)
  //     {

  //       col_select = num_col;
  //       row_select = num_ligne;

  //     }  
  //   }
  // }  

  

  if( (maintenant_debug + PERIODE) < millis())
  {
    maintenant_debug = millis();
    Serial.print(col_select);
    Serial.print("   ");
    Serial.println(row_select);
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
