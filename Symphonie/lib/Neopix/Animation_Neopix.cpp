#include <Arduino.h>
#include <Adafruit_NeoPixel.h>
#include <cstdlib>
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

    pressed_key[3] = {0};

    strip.begin();
    strip.setBrightness(255);
}

void Animation_Neopix::updateNeo(){
    if(animation != last_animation){
        last_animation = animation;
        pixel = 0;
    }
    switch (animation)
    {
    case 0:
        for(int i = 0; i< 3; i++){
            if ((pressed_key[i] != 9999) && pressed_key[i] <= NUMPIXELS)
                strip.setPixelColor(pressed_key[i]++, strip.Color(red, green, blue));
                strip.setPixelColor(pressed_key[i], strip.Color(red, green, blue));
                if (pressed_key[i] != 0)
                {
                    strip.setPixelColor(pressed_key[i]--, strip.Color(red, green, blue));
                }
        } 
        strip.show();
        break;

    case 1: //Cwipe
        colorWipe(pixel);
        break;

    case 2:
        Ambiance();
        break;

    case 3:
        doubleComete(pixel);
        break;

    case 4:
        colorWipeCenter(pixel); 
        break;  

    case 5:
        colorWipeEdge(pixel); 
        break;   

    case 6:
        Poumonage(); 
        break;     
    
    default:
        break;
    }  
    pixel++;   
}

void Animation_Neopix::Ambiance() { 
    if((millis() - maintenant == PERIODE_CWIPE) | (millis() < maintenant)){
        maintenant = millis();
        for(int i =0; i < NUMPIXELS; i++){
          strip.setPixelColor(pixel, strip.Color(red,green,blue));
        }
        strip.show();
    }
}

void Animation_Neopix::Poumonage() { 
    if((millis() - maintenant == PERIODE_POUMO) | (millis() < maintenant)){
        maintenant = millis();

        strip.setPixelColor(pixel, strip.Color(red *intensite_poumo, green * intensite_poumo, blue * intensite_poumo));
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
    if((millis() - maintenant == PERIODE_CWIPE) | (millis() < maintenant)){
        maintenant = millis();
        strip.setPixelColor(pixel, strip.Color(red,green,blue));
        strip.show();
    }
}

void Animation_Neopix::doubleComete(uint8_t pixel){
    if((millis() - maintenant == PERIODE_CWIPE) | (millis() < maintenant)){
        maintenant = millis();
        strip.setPixelColor(pixel, strip.Color(red, green, blue));
        strip.setPixelColor(pixel-3,strip.Color(0, 0, 0));
        strip.setPixelColor(NUMPIXELS - pixel, strip.Color(red, green, blue));
        strip.setPixelColor(NUMPIXELS - pixel + 3,strip.Color(0, 0, 0));
        strip.show();
    }
}

void Animation_Neopix::colorWipeCenter(uint8_t pixel){
    if((millis() - maintenant == PERIODE_CWIPE) | (millis() < maintenant)){
        maintenant = millis();
        if(reverse == false){
            strip.setPixelColor(milieu + 1  + pixel, strip.Color(red, green, blue));
            strip.setPixelColor(milieu - pixel, strip.Color(red, green, blue));
        }else if(reverse == true){
            strip.setPixelColor(milieu + 1 + pixel, strip.Color(0, 0, 0));
            strip.setPixelColor(milieu - 1 - pixel, strip.Color(0, 0, 0));
        }
        strip.show();
    }
}

void Animation_Neopix::colorWipeEdge(uint8_t pixel){
    if((millis() - maintenant == PERIODE_CWIPE) | (millis() < maintenant)){
        maintenant = millis();
        if(reverse == false){
            strip.setPixelColor(NUMPIXELS-pixel, strip.Color(red, green, blue));
            strip.setPixelColor( pixel, strip.Color(red, green, blue));
        }else if(reverse == true){
            strip.setPixelColor(NUMPIXELS - pixel, strip.Color(0, 0, 0));
            strip.setPixelColor( pixel, strip.Color(0, 0, 0));
        }
        strip.show();
    }
}

void Animation_Neopix::setStripColor(uint8_t tab[]){
    red = tab[0];
    green = tab[1];
    blue = tab[2];

    animation = tab[3];
}