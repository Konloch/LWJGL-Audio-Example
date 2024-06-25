# LWJGL Audio Example
+ This is a loose example of how you can utilize LWJGL audio with a SoundBank. 
+ We're playing WAV files with 3d sound in this demo, and the audio gets farther away with each step.

## How To Start
+ Download the free **community edition** of Intellij - https://www.jetbrains.com/idea/download/?section=windows
+ Download the entire repo / unzip it
+ Import the repo
+ Open the class 'LWJGLAudioExample', right-click run

## How To Edit
+ Modify the SoundBank.txt file to modify and insert new audio triggers
+ Insert or modify the sounds by replacing the WAV audio files.

## Technical Notes
+ For 3D audio to work the sounds must be in MONO format.

### SoundBank Explanation
+ For grouped sounds, make them start with the same name, then append _numerical-index to the end, so *WALK_1, WALK_2*
+ The SoundBank is separated into **Name, Path, Volume, Roll-Off, Reference-Distance, Max-Distance**

### Quick Source Links
+ [LWJGL Audio Example](https://github.com/Konloch/LWJGL-Audio-Example/blob/master/src/main/java/com/konloch/LWJGLAudioExample.java)
+ [Sound Bank](https://github.com/Konloch/LWJGL-Audio-Example/blob/master/src/main/resources/SoundBank.txt)
+ [Sound Files](https://github.com/Konloch/LWJGL-Audio-Example/tree/master/src/main/resources/audio)

## Sample Audio Credits
+ https://freesound.org/people/Yoyodaman234/