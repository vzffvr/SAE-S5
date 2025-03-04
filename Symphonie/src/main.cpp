#include <Arduino.h>
#include <main.h>

NEW_MSG new_data[3] {No_New_Msg};


uint8_t signal_form = 0;
uint8_t last_signal_form = signal_form;
uint8_t midiOrder[3] = {0};

Keypad keypad = Keypad(makeKeymap(keys), rowPins, colPins, ROWS, COLS);
Animation_Neopix anim;
BLE_Midi ble_midi;

Oscil<SIN4096_NUM_CELLS, MOZZI_AUDIO_RATE>* tableau[MAX_TILES_HELD];


void setup() {
  Serial.begin(115200);
  ble_midi.initBLE();

  anim.begin();

  startMozzi();
  for (int i = 0; i < MAX_TILES_HELD; ++i) {
    tableau[i] = new Oscil<SIN4096_NUM_CELLS, MOZZI_AUDIO_RATE>(SIN4096_DATA);
  }
  // oscil1.setFreq(1000.f); 
  // oscil2.setFreq(1000.f); 
  // oscil3.setFreq(1000.f); 
  pinMode(CONNECTION_LED, OUTPUT);
  digitalWrite(CONNECTION_LED, LOW);

  for (int i = 0; i < MAX_TILES_HELD; i++)
  {
    frequencies[i] = (MAX_FREQUENCIE / MAX_TILES_HELD) * (i+1);
    key_pressed[i] = 9999;
  }
  
}

void loop() {
  if(ble_midi.IsConnected())
    digitalWrite(CONNECTION_LED, HIGH);
  else
    digitalWrite(CONNECTION_LED, LOW);

  // Serial.printf("key1 = %d \t, key2 = %d \t, key3 = %d \n", key_pressed[0], key_pressed[1], key_pressed[2]);

  memcpy(new_data, ble_midi.loopBLE(), sizeof(new_data)); // Copie des valeurs de Whats_New qui est dans loopBLE dans new_data
  ble_midi.reset_tab();

  for(int i=0; i <sizeof(new_data) / sizeof(new_data[0]); i++){ 
    // Division car sizeof retourne des octect et non le nb de variables
    switch (new_data[i])
    {
      case Color:
        Serial.println("Maj Color");
        anim.setStripColor(ble_midi.getColorOrder());
        //Changement de couleur et animation bande de led
        break;
      case MIDI:
        Serial.println("Maj Midi");
        memcpy(midiOrder, ble_midi.getMidiOrder(), sizeof(midiOrder));

        if ((midiOrder[0] >> 4) ==  9){
          add2pressed_key(midiOrder[1]-12);
        }else if ((midiOrder[0] >> 4) ==  8){
          remove_from_pressed_key(midiOrder[1]-12);
        }
        break;
      case Generic:
        Serial.println("Maj generic"); 
        signal_form = ble_midi.getSignal();
        break;
      
      default:
        break;
    }
  }
  
  scanKeyboard();
  anim.setKeys(key_pressed);
  anim.updateNeo();
  audioHook();
}

int8_t myAudioOutput = 0;
uint8_t number_of_signals = 0;

void updateControl(){
  if(last_signal_form != signal_form){
    switch (signal_form)
    {
      case 0:
        for (int i = 0; i < MAX_TILES_HELD; i++){
          tableau[i]->setTable(SIN4096_DATA);
        }
        break;
      case 1:
        for (int i = 0; i < MAX_TILES_HELD; i++){
          tableau[i]->setTable(SAW4096_DATA);
        }
        break;
      case 2:
        for (int i = 0; i < MAX_TILES_HELD; i++){
          tableau[i]->setTable(TRIANGLE_DIST_CUBED_2048_DATA);
        }
        
        break;
      case 3:
        for (int i = 0; i < MAX_TILES_HELD; i++){
          tableau[i]->setTable(CHEBYSHEV_6TH_256_DATA);
        }
        break;
      
      default:
        break;
    }
  }

  myAudioOutput = 0;
  number_of_signals = 0;
  for(int i =0;i<3;i++){
    if(key_pressed[i]!=9999){
      tableau[i]->setFreq(frequencies[key_pressed[i]]); // Changement de freq
      myAudioOutput = myAudioOutput + tableau[i]->next(); // ajout dans myAudioOutput
      number_of_signals++;
    }
    else
      tableau[i]->setFreq(0);
  }
  // if(key_pressed[0]!=9999) // Changement de freq
  //   oscil1.setFreq(frequencies[key_pressed[0]]);
  // else
  //   oscil1.setFreq(0);

  // if(key_pressed[1]!=9999)
  //   oscil2.setFreq(frequencies[key_pressed[1]]);
  // else
  //   oscil2.setFreq(0);

  // if(key_pressed[2]!=9999)
  //   oscil3.setFreq(frequencies[key_pressed[2]]);
  // else
  //   oscil3.setFreq(0);

  

  // if(key_pressed[0]!=9999){ // ajout dans myAudioOutput
  //   myAudioOutput = myAudioOutput + oscil1.next();
  //   number_of_signals++;
  // }

  // if(key_pressed[1]!=9999){
  //   myAudioOutput = myAudioOutput + oscil2.next();
  //   number_of_signals++;
  // }

  // if(key_pressed[2]!=9999){
  //   myAudioOutput = myAudioOutput + oscil3.next();
  //   number_of_signals++;
  // }

  myAudioOutput = constrain(myAudioOutput, -128, 127);
}

AudioOutput updateAudio(){
    return MonoOutput::from8Bit(myAudioOutput/*+ 128*/);
}

void add2pressed_key(uint8_t key){
  for(int i = 0; i < MAX_TILES_HELD;i++){
    Serial.printf("key1 = %d \t, key2 = %d \t, key3 = %d \n", key_pressed[0], key_pressed[1], key_pressed[2]);
    if (key == key_pressed[i]){
      return;
   } else if (key_pressed[i] == 9999) {
      key_pressed[i] = key;
      Serial.print("add: ");
      Serial.println(key);
      return;
    }
  }
  Serial.printf("key1 = %d \t, key2 = %d \t, key3 = %d \n", key_pressed[0], key_pressed[1], key_pressed[2]);
  Serial.println("Tableau complet maximum de touche appuye en meme temps:3");
}

void remove_from_pressed_key(uint8_t key){
  for(int i = 0; i < MAX_TILES_HELD;i++){
    if (key_pressed[i] == key)
    {
      Serial.print("remove: ");
      Serial.println(key);
      key_pressed[i] = 9999;
      return;
    }
  }
  Serial.println("Erreur, Touche non appuye");
}

void scanKeyboard(){
  if (keypad.getKeys())
  {
      for (int i=0; i<LIST_MAX; i++)   // Scan the whole key list.
      {
          if ( keypad.key[i].stateChanged )   // Only find keys that have changed state.
          {
              switch (keypad.key[i].kstate) {  // Report active key state : IDLE, PRESSED, HOLD, or RELEASED
                  case PRESSED:
                    state = " PRESSED.";
                    break;
                  case HOLD:
                    state = " HOLD.";
                    add2pressed_key(keypad.key[i].kcode); 
                    Serial.printf("Tableau = %d ,\t %d ,\t %d \n", key_pressed[0], key_pressed[1], key_pressed[2]);
                    break;
                  case RELEASED:
                    state = " RELEASED.";
                    remove_from_pressed_key(keypad.key[i].kcode);
                    Serial.printf("Tableau = %d ,\t %d ,\t %d \n", key_pressed[0], key_pressed[1], key_pressed[2]);
                    break;
                  case IDLE:
                    state = " IDLE.";
                    break;
              }
              Serial.print(keypad.key[i].kchar);
              Serial.println(state);
          }
      }
  }
}

