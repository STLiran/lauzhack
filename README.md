# lauzhack

This demo has a bluetooth name hardcoded for the sake of simplicity. If you want to try it out for yourself, please change the `bacon`'s argument String in `KeyboardPredictor.java` at line 12 to your device's bluetooth name.

The project works on Windows and requires the `Logitech Gaming Software`. It can be easily imported into eclipse.

After launching the application, a listener waits for nearby bluetooth devices until the chosen one shows up. Up until this point the keyboard LEDs should be red, after which a heart is displayed while data is loaded. Once this is completed, predicted keys will light up green, adapting to the user input.
