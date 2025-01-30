#include <BLE_Midi.h>

BLE_Midi::BLE_Midi()
{
    currentNote = 0; // Note MIDI par défaut (Do central)
    volume = 50;     // Volume par défaut (50 %)
    midi_message = "";
}

void BLE_Midi::initBLE()
{
    // Initialiser le périphérique BLE
    BLEDevice::init("ESP32 BLE Instrument");

    BLEServer *pServer = BLEDevice::createServer();


    // Créer un service
    BLEService *pService = pServer->createService(SERVICE_UUID);

    // Créer la caractéristique MIDI
    BLECharacteristic *midiCharacteristic;
    midiCharacteristic = pService->createCharacteristic(
        CHARACTERISTIC_UUID,
        BLECharacteristic::PROPERTY_READ |
        BLECharacteristic::PROPERTY_WRITE |
        BLECharacteristic::PROPERTY_NOTIFY
    );

    // Définir la valeur initiale de la caractéristique comme une chaîne vide
    midiCharacteristic->setValue(midi_message.c_str());

    // Démarrer le service
    pService->start();

    // Démarrer la publicité BLE
    BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
    pAdvertising->start();
    Serial.println("BLE Server started, waiting for connections...");
}
