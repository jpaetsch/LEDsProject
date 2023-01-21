# LED Project

## Table of Contents
* [Summary](#summary)
* [Setup & Usage](#setup-&-usage)
* [Technologies](#technologies)
* [Inspiration](#inspiration)

## Summary
A project involving the remote control of an LED lightstrip on a WiFi network. It involves an Android app developed in a modern Kotlin architectural style that interfaces a custom API hosted on an ESP32 Microcontroller which is configured as a web server. This microcontroller is hardwired to a fully-addressible app WS2812B LED lightstrip; both of these are powered by an external 5V power source.

## Setup & Usage
Flash the .ino Arduino and C/C++ source code onto the ESP32. Wire the ESP32 output pin to the data wire of the WS2812B LED strip and ensure both hardware components are powered. Download the Android app onto a compatible device to control the overall system.

## Potential Improvements
See the GitHub issues and project for a potential improvements roadmap.

## Technologies
* Android SDK / API
* Android Compose
* Kotlin
* Ktor Web Client
* ESP32 TinyPico MCU
* WS2812B LED lights
* C/C++ (compiled and flashed onto the board using Arduino IDE)
* Power supply and a bunch of wiring to combat voltage drop
* FastLED library

## Inspiration
Wanted to set up a lightstrip and have it controllable by an app I program from my phone. Also want to eventually set up a user pattern creation system and connect it to the Spotify API to automatically set the lights depending on songs, genres, etc and have 'smart, responsive' patterns.
