#include <Arduino.h>

void read_state();

//Colonne
#define pin1 2
#define pin2 3
#define pin3 4

//Ligne
#define pin11 A7
#define pin12 A6
#define pin13 A5

//colonne
bool state_C1 = false;
bool state_C2 = false;
bool state_C3 = false;

//Ligne
bool state_L1 = false;
bool state_L2 = false;
bool state_L3 = false;

void setup() {
  pinMode(pin1, INPUT);
  pinMode(pin2, INPUT);
  pinMode(pin3, INPUT);

  pinMode(pin11, INPUT);
  pinMode(pin12, INPUT);
  pinMode(pin13, INPUT);
}

void loop() {
  read_state();
}

void read_state()
{
  state_C1 = digitalRead(pin1);
  state_C2 = digitalRead(pin2);
  state_C3 = digitalRead(pin3);  
  state_L1 = digitalRead(pin11);
  state_L2 = digitalRead(pin12);
  state_L3 = digitalRead(pin13);

  Serial.print("Colonne");
  Serial.print(state_C1    );
  Serial.print(state_C2    );
  Serial.println(state_C3);

  Serial.print("Ligne");
  Serial.print(state_L1    );
  Serial.print(state_L2    );
  Serial.println(state_L3);
}
