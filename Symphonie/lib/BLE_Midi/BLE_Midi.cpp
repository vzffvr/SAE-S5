#include <BLE_Midi.h>

BLE_Midi::BLE_Midi() 
{
    currentNote = 0; // Note MIDI par défaut (Do central)
    midi_message = "";
    color_order = "";
}

class MyServerCallbacks : public BLEServerCallbacks {
    void onConnect(BLEServer* pServer) override {
        Serial.println("Un périphérique s'est connecté !");
    }

    void onDisconnect(BLEServer* pServer) override {
        Serial.println("Un périphérique s'est déconnecté !");
        pServer->startAdvertising(); // Redémarre la publicité BLE pour accepter une nouvelle connexion
    }
};

class MidiCharacteristicCallbacks : public BLECharacteristicCallbacks {
    void onWrite(BLECharacteristic* pCharacteristic) override { //Android 2 ESP
        std::string value = pCharacteristic->getValue();
        const uint8_t* data = reinterpret_cast<const uint8_t*>(value.data()); // Convertis le tableau value.data dans en un uint8_t*
        
        if(value.length() == 4){
            uint8_t channel = data[1];   
            uint8_t note = data[2];  
            uint8_t content3 = data[3]; 
            uint8_t content4 = data[4]; 
        }

        Serial.println("Donnée MIDI reçue : " );
        Serial.println(value.c_str());
    }
    void onRead(BLECharacteristic *pCharacteristic) override {//ESP 2 Android
        Serial.println("Donnée lue !");
    }
};

class GenericCharacteristicCallbacks : public BLECharacteristicCallbacks {
    void onWrite(BLECharacteristic* pCharacteristic) override {
        // Récupérer les données reçues
        std::string value = pCharacteristic->getValue();
        const uint8_t* data = reinterpret_cast<const uint8_t*>(value.data()); // Convertis le tableau value.data dans en un uint8_t*
        size_t length = value.length();

        if(data[0] == 0x00) {
        uint8_t forme_signal = data[1];   
        uint8_t content2 = data[2];  
        uint8_t content3 = data[3]; 
        uint8_t content4 = data[4]; 

        Serial.printf("Donnée Generic reçue : Donnée 1 = %d \t, Donnée 2 = %d \t, Donnée 3 = %d \t, Donnée 3 = %d \n", forme_signal, content2, content3, content4);

        } else {
            Serial.println("Erreur : Taille des données incorrecte");
        }
        
    }
    void onRead(BLECharacteristic *pCharacteristic) override {//ESP 2 Android
        Serial.println("Donnée lue !");
    }
};

class ColorCharacteristicCallbacks : public BLECharacteristicCallbacks {
    void onWrite(BLECharacteristic* pCharacteristic) override {
        // Récupérer les données reçues
        std::string value = pCharacteristic->getValue();
        const uint8_t* data = reinterpret_cast<const uint8_t*>(value.data()); // Convertis le tableau value.data dans en un uint8_t*
        size_t length = value.length();

        // Vérifier que la longueur des données est correcte
        if (length == 5) { // 1 byte pour 0xFF + 4 bytes pour les valeurs
            // Extraire les valeurs les valeurs de uint8_t* pour les mettre en un uint8_t
            uint8_t header = data[0]; // Premier byte (0xFF)
            uint8_t red = data[1];    // Rouge
            uint8_t green = data[2];  // Vert
            uint8_t blue = data[3];   // Bleu
            uint8_t animation = data[4]; // Animation

            Serial.printf("Donnée Couleur reçue : RED = %d \t, GREEN = %d \t, BLUE = %d \t, ANIM = %d \n", red, green, blue, animation);

        } else {
            Serial.println("Erreur : Taille des données incorrecte");
        }
    }
    void onRead(BLECharacteristic *pCharacteristic) override {//ESP 2 Android
        Serial.println("Donnée lue !");
    }
};


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
        CHARACTERISTIC_MIDI_UUID,
        BLECharacteristic::PROPERTY_READ |
        BLECharacteristic::PROPERTY_WRITE |
        BLECharacteristic::PROPERTY_NOTIFY
    );

    BLECharacteristic *colorCharacteristic;
    colorCharacteristic = pService->createCharacteristic(
        CHARACTERISTIC_COLOR_UUID,
        BLECharacteristic::PROPERTY_READ |
        BLECharacteristic::PROPERTY_WRITE |
        BLECharacteristic::PROPERTY_NOTIFY
    );

    BLECharacteristic *genericCharacteristic;
    genericCharacteristic = pService->createCharacteristic(
        CHARACTERISTIC_GENERIC_UUID,
        BLECharacteristic::PROPERTY_READ |
        BLECharacteristic::PROPERTY_WRITE |
        BLECharacteristic::PROPERTY_NOTIFY
    );

    // Définir la valeur initiale de la caractéristique comme une chaîne vide
    midiCharacteristic->setValue(midi_message.c_str());
    colorCharacteristic->setValue(color_order.c_str());

    //Callbacks
    pServer->setCallbacks(new MyServerCallbacks());
    midiCharacteristic->setCallbacks(new MidiCharacteristicCallbacks());
    colorCharacteristic->setCallbacks(new ColorCharacteristicCallbacks());
    genericCharacteristic->setCallbacks(new GenericCharacteristicCallbacks());

    // Démarrer le service
    pService->start();    

    // Démarrer la publicité BLE
    BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
    pAdvertising->start();
    Serial.println("BLE Server started, waiting for connections...");
}

// uint8_t* BLE_Midi::getColorOrder(){
//     uint8_t array_color[3] = {red, green, blue};
//     //static
//     return array_color;
// }

