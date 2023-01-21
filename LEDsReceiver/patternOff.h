#ifndef PATTERNOFF_H
#define PATTERNOFF_H

#include <FastLED.h>
#include "pattern.h"


class PatternOff: public Pattern {
  private:      
  
  public:
    PatternOff(CRGB* inLeds, uint16_t inNumLeds): Pattern(inLeds, inNumLeds) {

    }

    ~PatternOff() {
    
    }

    uint8_t getPatternID();
    void initPattern();
    void updatePattern();
};


uint8_t PatternOff::getPatternID() {
  return 0;
}

void PatternOff::initPattern() {
  FastLED.clear();
  FastLED.show();
}

void PatternOff::updatePattern() {
  
}


#endif
