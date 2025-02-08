#ifndef BLE_MIDI
#define BLE_MIDI

#include <Arduino.h>
#include <BLEUtils.h>
#include <BLEServer.h>
#include <Animation_Neopix.h>
#include <Callbacks.h>

// Identifiants pour le service et la caractéristique
#define SERVICE_UUID "03B80E5A-EDE8-4B33-A751-6CE34EC4C700"      // UUID du service
#define CHARACTERISTIC_MIDI_UUID "7772E5DB-3868-4112-A1A9-F2669D106BF3" // UUID Midi
#define CHARACTERISTIC_COLOR_UUID "12345678-1234-5678-1234-56789ABCDEF0" 
#define CHARACTERISTIC_GENERIC_UUID "12345678-5678-9012-3456-56789ABCDEF0" 

#define PERIODE 100


enum NEW_MSG{
    No_New_Msg,
    MIDI,
    Color,
    Generic
};

class BLE_Midi
{
private:

    MyServerCallbacks ServerCallback;
    ColorCharacteristicCallbacks ColorCallBack;
    MidiCharacteristicCallbacks MidiCallBack;
    GenericCharacteristicCallbacks GenericCallBack;

    uint32_t maintenant_loop;
    NEW_MSG WhatsNew[3] = {No_New_Msg};

public:
    BLE_Midi(); // Déclaration du constructeur
    void initBLE();
    NEW_MSG* loopBLE();
    
    uint8_t* getColorOrder();
    uint8_t* getMidiOrder();
    uint8_t getSignal();
    void reset_tab();
};

#endif