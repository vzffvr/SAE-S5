#include <Arduino.h>
#include <Adafruit_NeoPixel.h>
#include <cstdlib>
#include "Animation_Neopix.h"

//classe contenant des animations pour des rubans de LED Neopix (sans matrices)

//Constructeur
Animation_Neopix::Animation_Neopix(void){
    this->rotation = 1;
    this->intensite = 200;
    poumon_i=1.0;
    k=-1;


    counter_chenille = 0;
    ri_last=0;
    gi_last=0;
    bi_last=0;

    milieu1 = (NUMPIXELS/2);
    milieu2 = (NUMPIXELS/2) -1;

    red = 0;
    green = 0;
    blue = 0;
    animation = 0;
}

//méthodes 
//mise de tout le strip a une couleur 
void Animation_Neopix::colorWipe(uint32_t color, uint16_t wait, Adafruit_NeoPixel *strip) { 
    strip->clear();
    for(int i=0; i<strip->numPixels(); i++) { // For each pixel in strip...
        strip->setPixelColor(i, color);         //  Set pixel's color (in RAM)
        strip->show();                          //  Update strip to match
        delay(wait);                            //  Pause for a moment
    }
}

//rotation de couleur RGB sur le strip 
void Animation_Neopix::Rotation_rubans(uint16_t tempo, Adafruit_NeoPixel *strip){
    if(rotation >3){
        rotation =1;
        }

    if(rotation == 1){
        colorWipe(strip->Color(intensite, 0, 0), 50, strip);
        }

    if(rotation == 2 ){
        colorWipe(strip->Color(0, intensite, 0), 50, strip);
    }

    if(rotation ==3){
        colorWipe(strip->Color(0, 0, intensite), 50, strip);
    }
    strip->show();
    rotation ++; 
    delay(tempo);
}

//un pixel coloré traverse le strip en continu 
void Animation_Neopix::Trainee(uint32_t color, uint16_t tempo, Adafruit_NeoPixel *strip){ //prblm = ne se répète pas 
    
    for(int i=0; i<30; i++) { //initalement NumPixel
         strip->setPixelColor(i, color);    
        strip->show();
  
        delay(tempo);

        strip->clear();
        strip->show();
        }
}

//effet de scintillement par allumage de LED pseudo aléatoire 
void Animation_Neopix::Scintillement(uint32_t color, uint16_t tempo, Adafruit_NeoPixel *strip){
    int x=0;
    int y=0;
    x=rand()%NUMPIXELS; //
    strip->setPixelColor(x, color);
    x=rand()%NUMPIXELS;
    strip->setPixelColor(x, color);

    strip->show();

    delay(tempo);

    strip->clear();
    strip->show();
}

void Animation_Neopix::comete(uint16_t pixel,  Adafruit_NeoPixel *strip){

    strip->clear();
    strip->show();

    if(pixel < NUMPIXELS){
         strip->setPixelColor(pixel, strip->Color(red,green,blue));
    }
    if(pixel-1 >=0){
         strip->setPixelColor(pixel-1, strip->Color(red,green,blue));
    }
   
    if(pixel-2>=0){
        strip->setPixelColor(pixel-2, strip->Color(red,green,blue));
    }
    if(pixel-3>=0){
        strip->setPixelColor(pixel-3, strip->Color(red*0.8,green*0.8,blue*0.8));
    }
    if(pixel-4>=0){
        strip->setPixelColor(pixel-4, strip->Color(red*0.8,green*0.8,blue*0.8));
    }
    if(pixel-5>=0){
        strip->setPixelColor(pixel-5, strip->Color(red*0.8,green*0.6,blue*0.8));
    }
    if(pixel-6>=0){
        strip->setPixelColor(pixel-6, strip->Color(red*0.6,green*0.6,blue*0.6));
    }
    if(pixel-7>=0){
        strip->setPixelColor(pixel-7, strip->Color(red*0.6,green*0.6,blue*0.6));
    }
    if(pixel-8>=0){
        strip->setPixelColor(pixel-8, strip->Color(red*0.5,green*0.5,blue*0.5));
    }
    if(pixel-9>=0){
        strip->setPixelColor(pixel-9, strip->Color(red*0.5,green*0.5,blue*0.5));
    }
    if(pixel-10>=0){
        strip->setPixelColor(pixel-10, strip->Color(red*0.4,green*0.4,blue*0.4));
    }
    if(pixel-11>=0){
        strip->setPixelColor(pixel-11, strip->Color(red*0.4,green*0.4,blue*0.4));
    }
    

    strip->show();
    

}

void Animation_Neopix::poumon(uint16_t pixel,  Adafruit_NeoPixel *strip){
    

    strip->setPixelColor(pixel, strip->Color(red*poumon_i,green*poumon_i,blue*poumon_i));
    
    if(pixel >= NUMPIXELS -1){
        poumon_i = poumon_i + 0.05*k;
        if(poumon_i < 0.3){
            poumon_i=0.3;
            k=k*(-1); //changement de sens 
        }
        else if(poumon_i > 0.9){
            poumon_i=0.9;
            k=k*(-1);
        }
        /* Serial.print(k);
        Serial.print("/");
        Serial.println(poumon_i); */
        strip->show();
    }
    



}

void Animation_Neopix::cwipe(uint16_t pixel,  Adafruit_NeoPixel *strip){
    

    strip->setPixelColor(pixel,strip->Color(red,green,blue));
    strip->show();

}


void Animation_Neopix::doubleComete(uint8_t pixel,  Adafruit_NeoPixel *strip){
    strip->setPixelColor(pixel, strip->Color(red, green, blue));
    strip->setPixelColor(pixel-3,strip->Color(0, 0, 0));
    strip->setPixelColor(NUMPIXELS - pixel, strip->Color(red, green, blue));
    strip->setPixelColor(NUMPIXELS - pixel + 3,strip->Color(0, 0, 0));
    strip->show();
}

void Animation_Neopix::doubleCWcentre(uint8_t pixel, bool reverse,  Adafruit_NeoPixel *strip){
    if(reverse == false){
          strip->setPixelColor((NUMPIXELS/2) + 1  + pixel, strip->Color(red, green, blue));
          strip->setPixelColor((NUMPIXELS/2) - pixel, strip->Color(red, green, blue));
        }
    else if(reverse == true){
          strip->setPixelColor((NUMPIXELS/2) + 1 + pixel, strip->Color(0, 0, 0));
          strip->setPixelColor((NUMPIXELS/2) - 1 - pixel, strip->Color(0, 0, 0));
        }
}

void Animation_Neopix::doubleCWext(uint8_t pixel, bool reverse,  Adafruit_NeoPixel *strip){
    if(reverse == false){
        strip->setPixelColor(NUMPIXELS-pixel, strip->Color(red, green, blue));
        strip->setPixelColor( pixel, strip->Color(red, green, blue));
    }

    else if(reverse == true){
        strip->setPixelColor(NUMPIXELS - pixel, strip->Color(0, 0, 0));
        strip->setPixelColor( pixel, strip->Color(0, 0, 0));
    }
}

void Animation_Neopix::setStripColor(uint8_t tab[]){
    red = tab[0];
    green = tab[1];
    blue = tab[2];
    animation = tab[3];
}