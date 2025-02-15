#include <Arduino.h>
#include <Adafruit_NeoPixel.h>
#include "Animation_Neopix.h"

//classe contenant des animations pour des rubans de LED Neopix (sans matrices)

Adafruit_NeoPixel strip(NUMPIXELS, NEOPIX_PIN, NEO_GRB + NEO_KHZ800);
//Constructeur
Animation_Neopix::Animation_Neopix(void){

    milieu = (NUMPIXELS/2);
    red = 0;
    green = 0;
    blue = 0;
    animation = 0;
    last_animation = animation;
    maintenant = millis();
    reverse = false;
    reverse_poumo = false;
    intensite_poumo = 1.0;
    k=-1;

    mode = 255;

    for (int i = 0; i < 3; i++){
        pressed_key[i] = 9999;}

    
}

void Animation_Neopix::begin(){
    strip.begin();
    strip.setBrightness(100);
    strip.clear();
    strip.show();
}

void Animation_Neopix::updateNeo(){
    if(animation != last_animation){
        last_animation = animation;
        reverse = false;
        Mpixel = 0;
        strip.clear();
        strip.show();
    }
    switch (animation-1)
    {
    case 0:
        suiveur();
        break;

    case 1: //Cwipe
        colorWipe(Mpixel);
        break;

    case 2:
        Ambiance();
        break;

    case 3:
        doubleComete(Mpixel);
        break;

    case 4:
        colorWipeCenter(Mpixel); 
        break;  

    case 5:
        colorWipeEdge(Mpixel); 
        break;   

    case 6:
        Poumonage(); 
        break;     
    
    default:
        break;
    }   
    if (Mpixel >= NUMPIXELS)
    {
        Mpixel = 0;
        strip.clear();
        strip.show();
    }
         
}
void Animation_Neopix::suiveur() {
    strip.clear();
    // Serial.printf("key1 = %d \t, key2 = %d \t, key3 = %d \n", pressed_key[0], pressed_key[1], pressed_key[2]);
    for (int i = 0; i < 3; i++) {
        if ((pressed_key[i] != 9999) && (pressed_key[i] < NUMPIXELS)) {
            strip.setPixelColor(pressed_key[i], strip.Color(red, green, blue));

            if (pressed_key[i] + 1 < NUMPIXELS) {
                strip.setPixelColor(pressed_key[i] + 1, strip.Color(red, green, blue));
            }

            if (pressed_key[i] - 1 >= 0) {
                strip.setPixelColor(pressed_key[i] - 1, strip.Color(red, green, blue));
            }
        }
        
    }
    strip.show();
    
}
void Animation_Neopix::Ambiance() { 
    maintenant = millis();
    strip.clear();
    for(int i = 0; i < NUMPIXELS; i++){
        strip.setPixelColor(i, strip.Color(red,green,blue));
    }
    strip.show();
    
}

void Animation_Neopix::Poumonage() { 
    if((millis() - maintenant >= PERIODE_POUMO) || (millis() < maintenant)){
        Serial.println("Poumo");
        maintenant = millis();

        for(int i = 0; i < NUMPIXELS; i++){
            strip.setPixelColor(i, strip.Color(red *intensite_poumo, green * intensite_poumo, blue * intensite_poumo));
        }
        
        strip.show();
        
        intensite_poumo = intensite_poumo + 0.05*k;
        if(intensite_poumo < 0.3){
            intensite_poumo=0.3;
            k=k*(-1); //changement de sens 
        }
        else if(intensite_poumo > 0.9){
            intensite_poumo=0.9;
            k=k*(-1);
        }           
    }
}

void Animation_Neopix::colorWipe(uint8_t pixel) { 
    if((millis() - maintenant >= PERIODE_CWIPE) || (millis() < maintenant)){
        maintenant = millis();
        strip.setPixelColor(pixel, strip.Color(red,green,blue));
        strip.show();
        Mpixel++;
    }
}

void Animation_Neopix::doubleComete(uint8_t pixel){
    if((millis() - maintenant >= PERIODE_CWIPE) || (millis() < maintenant)){
        maintenant = millis();
        strip.setPixelColor(pixel, strip.Color(red, green, blue));
        strip.setPixelColor(pixel-5,strip.Color(0, 0, 0));
        strip.setPixelColor(NUMPIXELS - pixel, strip.Color(red, green, blue));
        strip.setPixelColor(NUMPIXELS - pixel + 5,strip.Color(0, 0, 0));
        strip.show();
        Mpixel++;
    }
}

void Animation_Neopix::colorWipeCenter(uint8_t pixel){
    if((millis() - maintenant >= PERIODE_CWIPE) || (millis() < maintenant)){
        //FAIRE REVERSE
        maintenant = millis();
        if(reverse == false){
            strip.setPixelColor(milieu + 1  + pixel, strip.Color(red, green, blue));
            strip.setPixelColor(milieu - pixel, strip.Color(red, green, blue));
        }/* else if(reverse == true){
            strip.setPixelColor(milieu + 1 + pixel, strip.Color(0, 0, 0));
            strip.setPixelColor(milieu - 1 - pixel, strip.Color(0, 0, 0));
        } */
        strip.show();
        Mpixel++;
    }
}

void Animation_Neopix::colorWipeEdge(uint8_t pixel){
    if((millis() - maintenant >= PERIODE_CWIPE) || (millis() < maintenant)){
        //FAIRE REVERSE
        maintenant = millis();
        if(reverse == false){
            strip.setPixelColor((NUMPIXELS)-pixel, strip.Color(red, green, blue));
            strip.setPixelColor( pixel, strip.Color(red, green, blue));
        }else if(reverse == true){
            strip.setPixelColor(NUMPIXELS - pixel, strip.Color(0, 0, 0));
            strip.setPixelColor( pixel, strip.Color(0, 0, 0));
        }
        strip.show();
        Mpixel++;
    }
}

void Animation_Neopix::setStripColor(uint8_t tab[]){
    red = tab[0];
    green = tab[1];
    blue = tab[2];

    animation = tab[3];
}

void Animation_Neopix::setKeys(int tab[]){
    pressed_key[0] = tab[0];
    pressed_key[1] = tab[1];
    pressed_key[2] = tab[2];
}