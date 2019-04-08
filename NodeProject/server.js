const express = require('express')

const app = express()
const http = require('http').Server(app)

// Connect Socket.IO to server
const io = require('socket.io')(http)

io.on('connection', (socket) => {
    console.log(`User Connected - Socket ID ${socket.id}`);

    let currentRoom = null;
    let currentUser = null;

    socket.on('join', (roomName,userName) => {

        // Get chatroom info
        let room = io.sockets.adapter.rooms[roomName];

        // Reject join request if room already has more than 1 connection
        if (room && room.length > 1) {

            // Notify user that their join request was rejected
            io.to(socket.id).emit('room_full', null);

            // Notify room that someone tried to join
            socket.broadcast.to(roomName).emit('join_attempt', null);

        } else {

            // Leave current room
            socket.leave(currentRoom);

            // Notify room that user has left
            socket.broadcast.to(currentRoom).emit('user_disconnected', null);

            // Join new room
            currentRoom = roomName;
            currentUser = userName; 
            socket.join(currentRoom);

            // Notify user of room join success
            io.to(socket.id).emit('room_joined', currentRoom);

            // Notify room that user has joined
            socket.broadcast.to(currentRoom).emit('new_connection', `${currentUser} has joined the room`);

        }
    });

    /** Broadcast a received message to the room */
    socket.on('message', (msg) => {
        console.log(`New Message - ${msg}`);
        message = {
            'user': currentUser,
            'text': msg
        };
        socket.broadcast.to(currentRoom).emit('message', message);
    });

    /** Broadcast a new publickey to the room */
    socket.on('public_key', (key) => {
        console.log(`${socket.id}::: public-key=> ${key}`)
        socket.broadcast.to(currentRoom).emit('public_key', key);
    });

    /** Broadcast a disconnection notification to the room */
    socket.on('user_disconnected', () => {
        console.log(`${currentUser} just disconnected`);
        socket.broadcast.to(currentRoom).emit('user_disconnected', `${currentUser} just disconnected`);
        socket.leave(currentRoom);
    });
});

// Start server
const port = process.env.PORT || 3000
http.listen(port, () => {
    console.log(`Chat server listening on port ${port}.`);
    
});
