#ifndef Animation_Neopix_h
    #define Animation_Neopix_h
    #include <Adafruit_NeoPixel.h>

 
#define NUMPIXELS 200

class Animation_Neopix{
    private:
    uint8_t red;
    uint8_t green;
    uint8_t blue;

    public:
    uint8_t rotation;
    uint8_t intensite;

    double poumon_i;
    int k;
    Animation_Neopix(); 

    uint8_t counter_chenille;
    uint16_t ri_last;
    uint16_t gi_last;
    uint16_t bi_last;
    
    uint8_t milieu1;
    uint8_t milieu2;




    //--------------- demonstrateur ---------------------
    void comete(uint16_t pixel,  Adafruit_NeoPixel *strip);
    void poumon(uint16_t pixel,  Adafruit_NeoPixel *strip);
    void cwipe(uint16_t pixel,  Adafruit_NeoPixel *strip);
    void doubleComete(uint8_t pixel,  Adafruit_NeoPixel *strip);
    void doubleCWcentre(uint8_t pixel, bool reverse,  Adafruit_NeoPixel *strip);
    void doubleCWext(uint8_t pixel, bool reverse,  Adafruit_NeoPixel *strip);
    // ----------------------------------
    void colorWipe(uint32_t color, uint16_t wait ,Adafruit_NeoPixel *strip);
    void Rotation_rubans(uint16_t tempo, Adafruit_NeoPixel *strip);
    void Trainee(uint32_t color, uint16_t tempo, Adafruit_NeoPixel *strip);
    void Scintillement(uint32_t color, uint16_t tempo, Adafruit_NeoPixel *strip);

    void setStripColor(uint8_t red, uint8_t green, uint8_t blue, uint8_t anim);
};

#endif