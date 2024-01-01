## Das Weather
### _Application to get Weather and show it_

[![https://img.shields.io/badge/Java-8-white](https://img.shields.io/badge/Java-8-white)](https://www.java.com/) [![https://img.shields.io/badge/weather-api-blue](https://img.shields.io/badge/weather-api-blue)](https://www.weatherapi.com/) [![https://img.shields.io/badge/Spring-Boot-green](https://img.shields.io/badge/Spring-Boot-green)](https://spring.io/) [![https://img.shields.io/badge/Spring-Boot-green](https://img.shields.io/badge/Java-FX-cyan)](https://openjfx.io/)

### 📃 Features
- multiplatform
- quick and easy to use
- lightweight
- uses **multiple** languages to show weather info
- configured update interval

### 📌 Using
- Register on [Weather API](https://www.weatherapi.com/) and get API-KEY
- Download last version of [Application distribution](https://github.com/anrydas/DasWeather/releases) archive and unpack it into any folder on your PC
- Rename **application-default.properties** file to **application-PROD.properties** (if you want to launch application with **start-prod.sh** script).
- Into application-PROD.properties file:
  - Provide **app.api-key** parameter as API-KEY you have got before
  - Provide **app.weather.location** parameter - name of city weather you want to get for.
- Launch **start-prod.sh** (use Java 8)

### 📜 Main Window
![Screenshot](images/WeatherWindow_v2.png)
- Info Items:
  - 1st: **_Condition_** icon and it's name, **_Your Place Name_** and **_Last Updated_** - time when the weather have been updated  
  - 2nd: 🔥 **_Temperature_** ℃ **_Fills Like_** ℃ 🌫 **_Humidity_ %** 
  - 3rd: **Cloud** % **Precipitation** mm **Pressure** mmHg
  - 4th: 💨 **Wind Direction** (with arrow) **Wind Speed** km/h **Wind Gust** km/h
  - 5th: **Visibility** km **Ultra Violet Index** (with tooltip)
  - 6th: **Air Quality:**
  - 7th: **Sun Rise** **Sun Set** **Moon Rise** **Moon Phase** **Moon Set**
  - 8th: **Weather Forecast**
- Button:
  - **Update** - to update weather data
  - **Config** - to change Application's config

###### _Made by -=:dAs:=-_