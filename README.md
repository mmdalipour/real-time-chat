# RTChat
Basic end-to-end encrypted messenger using 2048-bit RSA encryption; android, nodejs, express, socket.io

![alt text](https://raw.githubusercontent.com/localXx/RTChat/master/res/rt_chat.jpg)

# Overview
Basic end-to-end encrypted messenger using 2048-bit RSA encryption, RSA keypairs are stored in android keystore system.
This project is a great example to create private messenger you can improve this project by:
- Saving data in a database like redis or mongo for a better result.
- Group chats by storing multiple public keys, and encrypting the message for each user individually.
- Multimedia messages, by encrypting a byte-array containing the media file.

in this example you are allowed to add only 2 users in the same chat room

# How-To-Use

#### Start the node server
```shell
cd NodeProject
npm start
```

#### Start android AVDs
```shell
cd sdk/tools
emulator -avd <FIRST_DEVICE_NAME>
emulator -avd <SECOND_DEVICE_NAME>
```

fill the inputs use a nickname for each device and a same room_id for both clients; both clients need to be on the same room to be able 
to communicate with each other

**ENJOY!**
