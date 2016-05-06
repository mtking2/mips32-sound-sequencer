# README #

### Purpose ###

This repository encompasses the entire project structure and all of the source code of our MIPS Sound Sequencer.

Version: May 5th, 2016

Authors: Brad Westley & Michael King

### Project Setup ###

This repo in itself is an IntelliJ/Eclipse project. (IntelliJ is recommended)

Steps to execute program:

* Download and install the latest [Java Development Kit](http://www.oracle.com/technetwork/java/javase/downloads/index.html) if you don't already have it.
* Download and install the latest version of [IntelliJ](https://www.jetbrains.com/idea/download/) if you don't already have it.
* Clone this repository to your local machine.
* Open the clone as an IntelliJ project. (the directory should show as an IntelliJ project within the IntelliJ file viewer automatically)
+ Locate the `DisplayDriver.java` class within the project structure.
    * This class is in the following path: `mips-sound-sequencer/src/capstone/gui/DisplayDriver.java`
+ Right click on the DisplayDriver.java class, then click on run in the popup menu.
    * A secondary window may open here asking to setup up the Java SDK. Select the JDK version you downloaded (or already have) and then locate the installation directory of it from the browse viewer within this window.

* If the SDK is properly setup our sequencer program should execute and open up. 

## **Note on sounds in different OSs** ##

For mysterious reasons yet unknown, sounds do not play in Microsoft Windows. It just isn't cool enough for our program :P

We know it has something to do with Windows not instantiating a MIDI output device when [MARS](http://courses.missouristate.edu/KenVollmar/MARS/) is called from command line. However, it works beautifully on Linux. And we have yet to test it on Mac OSX. 

In short, run this on Linux. Linux is love. Linux is life.

### Documentation ###

JavaDoc for our GUI can be found at: http://agora.cs.wcu.edu/~mtking2/coursework/cs/capstone/doc/

### Contact ###

mtking2@catamount.wcu.edu

bawestley1@catamount.wcu.edu