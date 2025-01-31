#ifndef BLE_MIDI
#define BLE_MIDI

#include <Arduino.h>
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>

// Identifiants pour le service et la caractéristique
#define SERVICE_UUID "03B80E5A-EDE8-4B33-A751-6CE34EC4C700"      // UUID du service
#define CHARACTERISTIC_UUID "7772E5DB-3868-4112-A1A9-F2669D106BF3" // UUID de la caractéristique




class BLE_Midi
{

private:
    String midi_message;

public:
    BLE_Midi();
    uint8_t currentNote; // Note MIDI par défaut (Do central)
    uint8_t volume;      // Volume par défaut (50 %)
    void initBLE();
};

#endif