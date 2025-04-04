#include <Arduino.h>
#include <main.h>

NEW_MSG new_data[3] {No_New_Msg, No_New_Msg, No_New_Msg};
const uint8_t NO_KEY_PRESSED = 255;


uint8_t signal_form = 0;
uint8_t last_signal_form = signal_form;
uint8_t midiOrder[3] = {0};

Keypad keypad = Keypad(makeKeymap(keys), rowPins, colPins, ROWS, COLS);
Animation_Neopix anim;
BLE_Midi ble_midi;

uint8_t key_pressed[MAX_TILES_HELD] = {NO_KEY_PRESSED, NO_KEY_PRESSED, NO_KEY_PRESSED,NO_KEY_PRESSED,NO_KEY_PRESSED};

// Oscil<SIN4096_NUM_CELLS, MOZZI_AUDIO_RATE>* tableau[MAX_TILES_HELD];

Oscil<SIN4096_NUM_CELLS, MOZZI_AUDIO_RATE> oscil(SIN4096_DATA);

void setup() {
  Serial.begin(115200);
  ble_midi.initBLE();

  anim.begin();

  startMozzi();
  oscil.setFreq(0);
  // for (int i = 0; i < MAX_TILES_HELD; ++i) {
  //   tableau[i] = new Oscil<SIN4096_NUM_CELLS, MOZZI_AUDIO_RATE>(SIN4096_DATA);
  //   tableau[i]->setFreq(0.f);
  // }

  pinMode(CONNECTION_LED, OUTPUT);
  digitalWrite(CONNECTION_LED, LOW);
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
        if(signal_form!=99){
          SetSignalForm();
        }
        if(ble_midi.getResetMsg() == true){
          Serial.println("resetTab");
          resetTabOfPressedKeys();
        }
        break;
      // case ResetTab:
      //   Serial.println("Reset Tab"); 
      //   resetTabOfPressedKeys();
      //   break;
      
      default:
        break;
    }
  }
  
  scanKeyboard();
  anim.setKeys(key_pressed);
  anim.updateNeo();

    audioHook();
}

uint8_t number_of_signals = 0;

void SetSignalForm(){
    const int8_t* wave_table = SIN4096_DATA; // Par défaut
    
    switch (signal_form) {
      case 0: 
        Serial.println("Set Wable table SIN4096 ");
        wave_table = SIN4096_DATA; 
        break;
      case 1: 
        Serial.println("Set Wable table SAW4096 ");
        wave_table = SAW4096_DATA; 
        break;
      case 2: 
        Serial.println("Set Wable table phasor ");
        wave_table = PHASOR256_DATA; 
        break;
      case 3: 
        Serial.println("Set Wable table Square ");
        wave_table = SQUARE_NO_ALIAS_2048_DATA; 
        break;
    }
    
/*     for (int i = 0; i < MAX_TILES_HELD; i++) {
      tableau[i]->setTable(wave_table);
    } */
    oscil.setTable(wave_table);
  }

void updateControl(){
  number_of_signals = 0;

  bool any_key_pressed = false;
  
  int freq = 0;
  
  for(int i = 0; i<MAX_TILES_HELD; i++){
    if(key_pressed[i]!=NO_KEY_PRESSED){
      freq = freq + frequencies[key_pressed[i]];
      number_of_signals ++;
    }
  }

  if(number_of_signals!=0){
    freq = freq/number_of_signals;
    oscil.setFreq(freq);
  }else{
    oscil.setFreq(0);
  }

/*   if (key_pressed[0] != NO_KEY_PRESSED)
  {
    oscil.setFreq(frequencies[key_pressed[0]]);
  }else{
    oscil.setFreq(0);
  } */

}

AudioOutput updateAudio() {
  if (key_pressed[0] == NO_KEY_PRESSED) {
    return MonoOutput::from8Bit(0); // Silence si aucune touche n'est appuyée
  }
  return MonoOutput::from8Bit(oscil.next());
}

void resetTabOfPressedKeys(){
  for (int i = 0; i < MAX_TILES_HELD; i++)
  {
    key_pressed[i] = NO_KEY_PRESSED;
  }
}


void add2pressed_key(uint8_t key) {
  for(int i = 0; i < MAX_TILES_HELD; i++) {
    if(key_pressed[i] == key) return; // Déjà présent
    if(key_pressed[i] == NO_KEY_PRESSED) {
      key_pressed[i] = key;
      Serial.print("Ajout touche: ");
      Serial.println(key);
      Serial.printf("Tableau = %d ,\t %d ,\t %d \n\n", key_pressed[0], key_pressed[1], key_pressed[2]);
      return;
    }
  }
  Serial.printf("key1 = %d \t, key2 = %d \t, key3 = %d \n", key_pressed[0], key_pressed[1], key_pressed[2]);
  Serial.println("Erreur: Trop de touches pressées simultanément");
}

void remove_from_pressed_key(uint8_t key) {
  for(int i = 0; i < MAX_TILES_HELD; i++) {
    if(key_pressed[i] == key) {
      key_pressed[i] = NO_KEY_PRESSED;
      Serial.print("Suppression touche: ");
      Serial.println(key);
      Serial.printf("Tableau = %d ,\t %d ,\t %d \n\n", key_pressed[0], key_pressed[1], key_pressed[2]);
      
      // Réorganiser le tableau pour combler les trous
      for(int j = i; j < MAX_TILES_HELD-1; j++) {
        key_pressed[j] = key_pressed[j+1];
      }
      key_pressed[MAX_TILES_HELD-1] = NO_KEY_PRESSED;

      if (key_pressed[0] == NO_KEY_PRESSED) {
        oscil.setFreq(0); // Si plus de touche pressée, on met la sortie à 0
      }
      return;
    }
  }
  Serial.println("Avertissement: Touche non trouvée dans le tableau");
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
                    add2pressed_key(keypad.key[i].kcode); 
                    Serial.printf("Tableau = %d ,\t %d ,\t %d \n", key_pressed[0], key_pressed[1], key_pressed[2]);
                    break;
                  case HOLD:
                    state = " HOLD.";
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

