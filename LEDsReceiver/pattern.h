#ifndef PATTERN_H
#define PATTERN_H

#include <FastLED.h>

class Pattern {
  protected:
    CRGB *leds;
    int numLeds;
  public:
    Pattern(CRGB *inLeds, uint16_t inNumLeds) {
      leds = inLeds;
      numLeds = inNumLeds;
    }

    virtual ~Pattern() {
      
    }

    virtual uint8_t getPatternID() = 0;     // This function must be kept sync with the resource values for pattern IDs in the controller
    virtual void initPattern() = 0;         // This function will be run once to start off the pattern
    virtual void updatePattern() = 0;       // This function will be continually looped through and re-draw/update the pattern
};


#endif
