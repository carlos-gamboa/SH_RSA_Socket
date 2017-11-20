# SH_RSA_Socket

SH_RSA_Socket is a Java Application in which you can establish a secure connection between smart devices and a smart hub using sockets.
 - You can connect smart devices such as light, thermo, motion or proximity sensors.
 - The current version only sends and receives messages between the device and the hub or the device and another random one.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites

 - Java SDK.
 - A Java Development IDE.
 - Project lombok library and annotations.

### Installing

To install the application you need to:
 - Clone [this repository][project_repo].
 - Open this project on any Java IDE (preferably [IntelliJ IDEA]).
 - Install the libraries needed.

You are ready to run the app!

### To run the program:

You have to change the variable for the path to access the devices' and hub's  keys. You have to do that on HubApplication, DeviceApplication and Hub_Handler classes.

 - First, run the HubApplication.
 - Next, run the DeviceApplication.
 - In console, the device will ask you his type, so you can insert "motion", "light", "thermo" or "prox" because the generated keys are related to them.
 - In case you want to run the intruder, insert "intruder" into the type of the device. That way you will intercept all the messages.
 - You can run as many devices as you want.
 - Type a message on the device's console.
 - Inmediately, on the hub's console will appear a message asking you if you want to add the device to the console, follow the instructions.
 - Based on what you type, the hub will do an action, if you type "Yes" the message sent by the device is received, but if you type "No", the hub will ask you if you want to add the device to the black list or no.
 - If the device is now on the cluster, you can send any message between the device and the hub.
 - If the device is on the black list, all the messages sent will be ignored.
 - Else, the hub will continue asking what you want to do.
 - Also, if you want to send a message to a random device that is in the same cluster type "Send!" followed by the desired message.

## Authors

This application was developed by:

  - [Ana Laura Vargas][analau05]
  - [Carlos Gamboa][carlos-gamboa]
  - [Carlos Portuguez][KarmanXV]
  
Based on [Luka Klacar][lklacar]'s [project][luka's_repo].

[//]: # (These are reference links used in the body of this note and get stripped out when the markdown processor does its job. There is no need to format nicely because it shouldn't be seen. Thanks SO - http://stackoverflow.com/questions/4823468/store-comments-in-markdown-syntax)

   [lklacar]: <https://github.com/lklacar>
   [analau05]: <https://github.com/analau05>
   [carlos-gamboa]: <https://github.com/carlos-gamboa>
   [KarmanXV]: <https://github.com/KarmanXV>

   [IntelliJ IDEA]: <https://www.jetbrains.com/idea/>
   [project_repo]: <https://github.com/carlos-gamboa/SH_RSA_Socket>
   [luka's_repo]: <https://github.com/lklacar/java-rsa-socket-chat>
