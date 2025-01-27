#ifndef BLE_MIDI
#define BLE_MIDI

#include <Arduino.h>
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>

// Identifiants pour le service et la caractéristique
#define SERVICE_UUID "12345678-1234-5678-1234-56789abcdef0"        // UUID du service
#define CHARACTERISTIC_UUID "abcdef01-1234-5678-1234-56789abcdef0" // UUID de la caractéristique

#define CHAR_UUID_NOTE "abcdef01-1234-5678-1234-56789abcdef0"
#define CHAR_UUID_VOLUME "abcdef02-1234-5678-1234-56789abcdef0"
#define CHAR_UUID_MIDI "abcdef03-1234-5678-1234-56789abcdef0"

BLECharacteristic *noteCharacteristic;
BLECharacteristic *midiCharacteristic;
BLECharacteristic *volumeCharacteristic;

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