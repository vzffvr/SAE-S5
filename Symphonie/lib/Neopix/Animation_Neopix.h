#ifndef Animation_Neopix_h
#define Animation_Neopix_h
#include <Adafruit_NeoPixel.h>

 
#define NUMPIXELS 144
#define NEOPIX_PIN 23
#define PERIODE_CWIPE 20
#define PERIODE_POUMO 25 //EDGE

class Animation_Neopix{
    private:
    uint8_t red;
    uint8_t green;
    uint8_t blue;
    uint8_t animation;
    uint8_t last_animation;
    uint8_t Mpixel;

    enum MODE{
        MANUEL,
        AUTO,
        SEMI_AUTO
    };
    uint8_t mode;
    int pressed_key[3];
    uint32_t maintenant;
    bool reverse_poumo;
    uint8_t intensite_poumo;

    void suiveur();
    void colorWipe(uint8_t pixel);
    void Ambiance();
    void Poumonage();
    void colorWipeCenter(uint8_t pixel);
    void colorWipeEdge(uint8_t pixel);
    void doubleComete(uint8_t pixel);
    uint8_t milieu;
    int k;
    bool reverse;

    //--------------------------------------------------//
    public:
    Animation_Neopix(); 
    void begin();
    void setStripColor(uint8_t tab[]);
    void setKeys(int tab[]);
    void updateNeo();
    
};

#endif