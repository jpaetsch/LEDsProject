#ifndef PATTERNSOLID_H
#define PATTERNSOLID_H

#include <FastLED.h>
#include "pattern.h"


class PatternSolid: public Pattern {
  private:      
    uint8_t hue;
    uint8_t sat;
    uint8_t val;
  
  public:
    PatternSolid(CRGB* inLeds, uint16_t inNumLeds, uint8_t inHue, uint8_t inSat, uint8_t inVal): Pattern(inLeds, inNumLeds) {
      hue = inHue;
      sat = inSat;
      val = inVal;
    }

    ~PatternSolid() {
    
    }

    uint8_t getPatternID();
    void initPattern();
    void updatePattern();
};


uint8_t PatternSolid::getPatternID() {
  return 1;
}

void PatternSolid::initPattern() {
  CHSV color = CHSV(hue, sat, val);
  fill_solid(leds, numLeds, color);
  FastLED.show();
}

void PatternSolid::updatePattern() {
  
}


#endif
