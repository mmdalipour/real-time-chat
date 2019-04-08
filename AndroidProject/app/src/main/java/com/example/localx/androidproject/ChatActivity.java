package com.example.localx.androidproject;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.localx.androidproject.DataModel.Message;
import com.example.localx.androidproject.Encryption.EncryptionUtils;
import com.example.localx.androidproject.Encryption.SecurityKey;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    private static final String SERVER_URL = "http://192.168.1.3:3000"; // user your local ip here like: http://[LOCAL_IP]:3000
    private static final String TAG = "ChatActivity";

    public com.github.nkzawa.socketio.client.Socket socket;
    private String destinationPublicKey = null;
    private RecyclerView chatRecyclerView;
    private ArrayList<Message> messages;
    private EditText messageEditText;
    private String nickname;
    private int pendingRoom;
    private int room;


    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        ImageButton sendBtn = findViewById(R.id.send_btn);
        messageEditText = findViewById(R.id.message_edt);
        chatRecyclerView = findViewById(R.id.chat_recycler);
        TextView nicknameTxt = findViewById(R.id.nickname_txt_top);
        TextView roomTxt = findViewById(R.id.room_name_txt);

        messages = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setItemAnimator(new DefaultItemAnimator());

        nickname = getIntent().getStringExtra("nickname");
        if (nickname.isEmpty()) finish();

        room = getIntent().getIntExtra("room",0);

        try {
            socket = IO.socket(SERVER_URL);

            // create connection
            socket.connect();

            joinRoom(room);

            nicknameTxt.setText(getString(R.string.nickname_txt) + nickname);
            roomTxt.setText(getString(R.string.room_id_txt) + String.valueOf(room));

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // user joined the chat listener
        socket.on("join", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String data = (String) args[0];
                        Toast.makeText(ChatActivity.this, data, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // on this client joined a room
        socket.on("room_joined", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int data = (int) args[0];
                        Toast.makeText(ChatActivity.this, "You just joined room with id:  " + String.valueOf(data), Toast.LENGTH_SHORT).show();

                        // send public-key to the current room
                        sendPublicKey();
                    }
                });
            }
        });

        // on room already contains max user count
        socket.on("room_full", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        pendingRoom = (int)(Math.floor(Math.random() * 1000));
                        joinRoom(pendingRoom);
                    }
                });
            }
        });

        // on join room attempt in this case this room is full
        socket.on("join_attempt", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChatActivity.this, "A third user attempted to join the room.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // a user just connected to this room
        socket.on("new_connection", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String data = (String) args[0];
                        // get the extra data from the fired event and display a toast
                        Toast.makeText(ChatActivity.this, data, Toast.LENGTH_SHORT).show();

                        // send public-key to the current room
                        sendPublicKey();
                    }
                });
            }
        });


        // send message to socket gate
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (messageEditText.getText().toString().isEmpty()) {
                    Toast.makeText(ChatActivity.this, "You can not send empty message!", Toast.LENGTH_SHORT).show();
                    return;
                }

                sendMessage(messageEditText.getText().toString());
            }
        });

        // on a message received
        socket.on("message", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            Message message = new Message();

                            message.setUser(data.getString("user"));
                            message.setText(data.getString("text"));


                            /**
                             * Decrypt Message Here
                             */
                            String decryptedMessage = EncryptionUtils.decrypt(getApplicationContext(),message.getText());
                            message.setText(decryptedMessage);
                            message.setMessageColor(1);

                            messages.add(message);

                            ChatAdapter adapter = new ChatAdapter(getApplicationContext(),messages);
                            adapter.notifyDataSetChanged();

                            chatRecyclerView.setAdapter(adapter);

                        } catch (JSONException e) {
                            Toast.makeText(ChatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // on public-key exchange
        socket.on("public_key", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String data = (String) args[0];
                        Toast.makeText(ChatActivity.this, "public-key received", Toast.LENGTH_LONG).show();
                        destinationPublicKey = data;
                    }
                });
            }
        });

        // on a user disconnected from the room/chat
        socket.on("user_disconnected", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChatActivity.this, "User disconnected ", Toast.LENGTH_SHORT).show();
                        destinationPublicKey = null;
                    }
                });
            }
        });

    }

    /**
     * Join a room using it's id
     */
    private void joinRoom(int roomId) {
        // Reset room state variables
        messages.clear();

        ChatAdapter adapter = new ChatAdapter(getApplicationContext(),messages);
        adapter.notifyDataSetChanged();

        chatRecyclerView.setAdapter(adapter);
        this.destinationPublicKey = null;

        // Emit room join request.
        this.socket.emit("join", roomId, nickname);
    }

    /**
     * Send message to current room
     *
     * @param message
     */
    private void sendMessage(String message) {

        // we need to encrypt message using destination public-key
        if (destinationPublicKey == null) return;

        /**
         * Encrypt Message Here
         */
        PublicKey publicKey = getPublicKey(Base64.decode(destinationPublicKey, Base64.URL_SAFE));
        KeyPair keyPair = new KeyPair(publicKey, null);
        SecurityKey securityKey = new SecurityKey(keyPair);
        String encryptedMessage = securityKey.encrypt(message);

        socket.emit("message", encryptedMessage);

        // add raw text to recycler_view
        messages.add(new Message(nickname,messageEditText.getText().toString(),0));

        ChatAdapter adapter = new ChatAdapter(getApplicationContext(),messages);
        adapter.notifyDataSetChanged();

        chatRecyclerView.setAdapter(adapter);

        // clear text box
        messageEditText.setText("");
    }

    /**
     * Send public-key to every other person on the chat
     */
    private void sendPublicKey() {
        PublicKey publicKey =  EncryptionUtils.getKeyPair(getApplicationContext()).getPublic();
        String encodedPublicKey = Base64.encodeToString(publicKey.getEncoded(),Base64.URL_SAFE);
        socket.emit("public_key",encodedPublicKey);
    }

    /**
     * Convert base64-url encoded string to public key
     * @param publicKeyBytes
     * @return PublicKey
     */
    private PublicKey getPublicKey(byte[] publicKeyBytes) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            return keyFactory.generatePublic(keySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            Log.e(TAG, "getPublicKey: " + e.getMessage());
        }
        return null;
    }

    /**
     * States of user disconnection
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        socket.emit("user_disconnected","");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        socket.emit("user_disconnect","");
    }
}


