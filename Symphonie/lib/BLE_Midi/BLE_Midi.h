#ifndef BLE_MIDI
#define BLE_MIDI

#include <Arduino.h>
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>
#include <Animation_Neopix.h>

// Identifiants pour le service et la caractéristique
#define SERVICE_UUID "03B80E5A-EDE8-4B33-A751-6CE34EC4C700"      // UUID du service
#define CHARACTERISTIC_MIDI_UUID "7772E5DB-3868-4112-A1A9-F2669D106BF3" // UUID Midi
#define CHARACTERISTIC_COLOR_UUID "12345678-1234-5678-1234-56789ABCDEF0" 




class BLE_Midi
{
private:
    String midi_message;
    String color_order;
    uint8_t red;
    uint8_t green;
    uint8_t blue;

public:
    BLE_Midi(); // Déclaration du constructeur
    uint8_t currentNote; // Note MIDI par défaut (Do central)
    void initBLE();
    uint8_t* getColorOrder();

    
    
};

#endif