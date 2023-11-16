# Application Title: LightHouse

## Configuration Settings

Must be used on a device with camera access and accelerometer and magnetic field sensors

## API Key

No API key used

## Reading the Code:

Read InfoActivity.java, then PresetList.java, then PresetActivity.java.
Before reading MainActivity.java make sure you know about the speed and preset keys in the bundles
getting sent over from the Preset and Info activities.


## Other information

The Preset activity is very similar to the requests and contacts activities from AS3, I took
a lot of inspiration from it in having the TableLayout be a separate file with helper methods
and the main file incorporating that class with SharedPreferences. 