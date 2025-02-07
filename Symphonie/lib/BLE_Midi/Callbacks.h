#ifndef BLE_CALLBACKS
#define BLE_CALLBACKS

#include <Arduino.h>
#include <BLEDevice.h>
#include <BLEUtils.h>
#include <BLEServer.h>

class MyServerCallbacks : public BLEServerCallbacks {
    private : 
        bool isConnected = false;

        void onConnect(BLEServer* pServer) override {
            isConnected = true;
            Serial.println("Un périphérique s'est connecté !");
        }

        void onDisconnect(BLEServer* pServer) override {
            isConnected = false;
            Serial.println("Un périphérique s'est déconnecté !");
            pServer->startAdvertising(); // Redémarre la publicité BLE pour accepter une nouvelle connexion
        }
    public:
        bool getIsConnected(){
            return isConnected;
        }
};

class MidiCharacteristicCallbacks : public BLECharacteristicCallbacks {
    private : 
        bool update_value = false;
        uint8_t channel = 0;
        uint8_t note = 0;
        uint8_t velocity = 0;

        void onWrite(BLECharacteristic* pCharacteristic) override { //Android 2 ESP
            std::string value = pCharacteristic->getValue();
            const uint8_t* data = reinterpret_cast<const uint8_t*>(value.data()); // Convertis le tableau value.data dans en un uint8_t*
            
            if(value.length() == 4){
                channel = data[1];   
                note = data[2];  
                velocity = data[3];

                update_value = true;
            }

            Serial.printf("Donnée Midi reçue : channel = %d \t, note = %d \t, velocity = %d \n", channel, note, velocity);

        }
        void onRead(BLECharacteristic *pCharacteristic) override {//ESP 2 Android
            Serial.println("Donnée lue !");
        }
    public: 
        uint8_t* getMidiOrder(){
            static uint8_t tab[3] = {channel, note, velocity} ;
            return tab;
        }

        bool getUpdate(){
            return update_value;
        }

        void setUpdate(bool value){
            update_value = value;
        }
};

class GenericCharacteristicCallbacks : public BLECharacteristicCallbacks {
    private: 
        bool update_value = false;
        uint8_t signal = 0;
        uint8_t content2 = 0;
        uint8_t content3 = 0;
        uint8_t content4 =0;

        void onWrite(BLECharacteristic* pCharacteristic) override {
            // Récupérer les données reçues
            std::string value = pCharacteristic->getValue();
            const uint8_t* data = reinterpret_cast<const uint8_t*>(value.data()); // Convertis le tableau value.data dans en un uint8_t*
            size_t length = value.length();

            if(data[0] == 0x00) {
            signal = data[1];   
            content2 = data[2];  
            content3 = data[3]; 
            content4 = data[4]; 

            Serial.printf("Donnée Generic reçue : Donnée 1 = %d \t, Donnée 2 = %d \t, Donnée 3 = %d \t, Donnée 3 = %d \n", signal, content2, content3, content4);
            update_value = true;
            } else {
                Serial.println("Erreur : Taille des données incorrecte");
            }
            
        }
        void onRead(BLECharacteristic *pCharacteristic) override {//ESP 2 Android
            Serial.println("Donnée lue !");
        }
    public : 
        uint8_t getSignal(){
            return signal;
        }

        bool getUpdate(){
            return update_value;
        }

        void setUpdate(bool value){
            update_value = value;
        }
};

class ColorCharacteristicCallbacks : public BLECharacteristicCallbacks {
    private: 
        bool update_value = false;
        uint8_t red = 0;
        uint8_t green = 0;
        uint8_t blue = 0;
        uint8_t animation = 0;

        void onWrite(BLECharacteristic* pCharacteristic) override {
            // Récupérer les données reçues
            std::string value = pCharacteristic->getValue();
            const uint8_t* data = reinterpret_cast<const uint8_t*>(value.data()); // Convertis le tableau value.data dans en un uint8_t*
            size_t length = value.length();

            if (length == 5 && (data[0] == 0xFF)) {
                
                red = data[1];    
                green = data[2]; 
                blue = data[3];  
                animation = data[4]; 

                Serial.printf("Donnée Couleur reçue : RED = %d \t, GREEN = %d \t, BLUE = %d \t, ANIM = %d \n", red, green, blue, animation);

                update_value = true;
            } else {
                Serial.println("Erreur : Taille des données incorrecte");
            }
        }
        void onRead(BLECharacteristic *pCharacteristic) override {//ESP 2 Android
            Serial.println("Donnée lue !");
        }

    public: 
        uint8_t* getColors(){
            static uint8_t tab[4]= { red, green,blue, animation}; 
            return tab;
        }// static car je retourne une variable local et si pas de static alors variable supprimer apres appel a la fonction

        bool getUpdate(){
            return update_value;
        }

        void setUpdate(bool value){
            update_value = value;
        }
};

#endif
