#include <BLE_Midi.h>

BLE_Midi::BLE_Midi() 
{
    maintenant_loop = 0;
    NEW_MSG WhatsNew[3] = {No_New_Msg,No_New_Msg,No_New_Msg};
}

void BLE_Midi::initBLE()
{
    // Initialiser le périphérique BLE
    BLEDevice::init("ESP32 BLE Instrument");

    BLEServer *pServer = BLEDevice::createServer();

    // Créer un service
    BLEService *pService = pServer->createService(SERVICE_UUID);

    // Initialisation de la caractéristique MIDI
    BLECharacteristic *midiCharacteristic;
    midiCharacteristic = pService->createCharacteristic(
        CHARACTERISTIC_MIDI_UUID,
        BLECharacteristic::PROPERTY_READ |
        BLECharacteristic::PROPERTY_WRITE 
        // BLECharacteristic::PROPERTY_NOTIFY
    );

    // Initialisation la caractéristique Couleur
    BLECharacteristic *colorCharacteristic;
    colorCharacteristic = pService->createCharacteristic(
        CHARACTERISTIC_COLOR_UUID,
        BLECharacteristic::PROPERTY_READ |
        BLECharacteristic::PROPERTY_WRITE 
        // BLECharacteristic::PROPERTY_NOTIFY
    );

    // Initialisation la caractéristique Generic
    BLECharacteristic *genericCharacteristic;
    genericCharacteristic = pService->createCharacteristic(
        CHARACTERISTIC_GENERIC_UUID,
        BLECharacteristic::PROPERTY_READ |
        BLECharacteristic::PROPERTY_WRITE 
        // BLECharacteristic::PROPERTY_NOTIFY
    );

    // Définir la valeur initiale de la caractéristique comme une chaîne vide
    // midiCharacteristic->setValue(midi_message.c_str());
    // colorCharacteristic->setValue(color_order.c_str());

    //Callbacks
    ServerCallback = MyServerCallbacks();
    ColorCallBack = ColorCharacteristicCallbacks();
    MidiCallBack = MidiCharacteristicCallbacks();
    GenericCallBack = GenericCharacteristicCallbacks();

    pServer->setCallbacks(&ServerCallback);
    midiCharacteristic->setCallbacks(&MidiCallBack);
    colorCharacteristic->setCallbacks(&ColorCallBack);
    genericCharacteristic->setCallbacks(&GenericCallBack);

    // Démarrer le service
    pService->start();    

    // Démarrer la publicité BLE
    BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
    pAdvertising->start();
    Serial.println("BLE Server started, waiting for connections...");
    maintenant_loop = millis();
}

NEW_MSG* BLE_Midi::loopBLE(){
    if((millis() - maintenant_loop >= PERIODE) | (millis() < maintenant_loop)){//Toutes les 100ms ou si millis depasse 47 jours
        maintenant_loop = millis();
        if(ServerCallback.getIsConnected()){
            if (MidiCallBack.getUpdate()){
                MidiCallBack.setUpdate(false);
                WhatsNew[0] = MIDI;
            }else 
                WhatsNew[0] = {No_New_Msg};

            if (ColorCallBack.getUpdate()){
                ColorCallBack.setUpdate(false);
                WhatsNew[1] = Color;
            }else 
                WhatsNew[1] = {No_New_Msg};
            
            if (GenericCallBack.getUpdate()){
                GenericCallBack.setUpdate(false);
                WhatsNew[2] = Generic;
            }else 
                WhatsNew[2] = {No_New_Msg};
        }else
            Serial.println("Not Connected");
    }
    
    return WhatsNew;
}

uint8_t* BLE_Midi::getColorOrder(){
    return ColorCallBack.getColors();
}

uint8_t* BLE_Midi::getMidiOrder(){
    return MidiCallBack.getMidiOrder();
}

uint8_t BLE_Midi::getSignal(){
    return GenericCallBack.getSignal();
}

bool BLE_Midi::IsConnected(){
    return ServerCallback.getIsConnected();
}


void BLE_Midi::reset_tab(){
    WhatsNew[0] = {No_New_Msg};
    WhatsNew[1] = {No_New_Msg};
    WhatsNew[2] = {No_New_Msg};
}

