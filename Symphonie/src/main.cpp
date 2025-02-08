#include <Arduino.h>
#include <main.h>

NEW_MSG new_data[3] {No_New_Msg};

uint8_t signal_form = 0;

Keypad keypad = Keypad(makeKeymap(keys), rowPins, colPins, ROWS, COLS);
Animation_Neopix anim;
BLE_Midi ble_midi;
Oscil <SIN4096_NUM_CELLS, MOZZI_AUDIO_RATE> aSin1(SIN4096_DATA);
Oscil <SIN4096_NUM_CELLS, MOZZI_AUDIO_RATE> aSin2(SIN4096_DATA);
Oscil <SIN4096_NUM_CELLS, MOZZI_AUDIO_RATE> aSin3(SIN4096_DATA);

void setup() {
  Serial.begin(115200);
  ble_midi.initBLE();

  startMozzi();
  aSin1.setFreq(1000.f); 
  aSin2.setFreq(1000.f); 
  aSin3.setFreq(1000.f); 
}

void loop() {

  // for(int i=0; i<20; i++) {
  //   strip.setPixelColor(i, strip.Color(255, 255, 255));

  //   strip.show();

  //   delay(30);
  // }
  // strip.clear();
  // strip.show();

  new_data[0] = ble_midi.loopBLE()[0];
  new_data[1] = ble_midi.loopBLE()[1];
  new_data[2] = ble_midi.loopBLE()[2];
  
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
        //Traitement: Ajout ou retrait de la touche recu du tableau de touches appuye.
        // status = 0x9x -> ajout 
        // status = 0x8x -> retrait 
        break;
      case Generic:
        Serial.println("Maj generic"); 
        //Traitement: Changement de bande de son
        break;
      
      default:
        break;
    }
  }
  
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
  audioHook();
}

int8_t myAudioOutput = 0;
uint8_t number_of_signals = 0;

void updateControl(){
  if(key_pressed[0]!=9999)
    aSin1.setFreq(frequencies[key_pressed[0]]);
  else
    aSin1.setFreq(0);

  if(key_pressed[1]!=9999)
    aSin2.setFreq(frequencies[key_pressed[1]]);
  else
    aSin2.setFreq(0);

  if(key_pressed[2]!=9999)
    aSin3.setFreq(frequencies[key_pressed[2]]);
  else
    aSin3.setFreq(0);

  myAudioOutput = 0;
  number_of_signals = 0;
  if(key_pressed[0]!=9999){
    myAudioOutput = myAudioOutput + aSin1.next();
    number_of_signals++;
  }

  if(key_pressed[1]!=9999){
    myAudioOutput = myAudioOutput + aSin2.next();
    number_of_signals++;
  }

  if(key_pressed[2]!=9999){
    myAudioOutput = myAudioOutput + aSin3.next();
    number_of_signals++;
  }

  myAudioOutput = constrain(myAudioOutput, -128, 127);
}

AudioOutput updateAudio(){
    return MonoOutput::from8Bit(myAudioOutput + 128);
}

void add2pressed_key(uint8_t key){
  for(int i = 0; i<3;i++){
    if (key_pressed[i] == 9999)
    {
      key_pressed[i] = key;
      break;
    }
  }
}

void remove_from_pressed_key(uint8_t key){
  for(int i = 0; i<3;i++){
    if (key_pressed[i] == key)
    {
      key_pressed[i] = 9999;
      break;
    }
  }
}
