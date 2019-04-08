package com.example.localx.androidproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

    private EditText nicknameEditText, roomEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        nicknameEditText = findViewById(R.id.nickname_edt);
        roomEditText = findViewById(R.id.room_edt);
        ImageButton submitBtn = findViewById(R.id.submit_btn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // on submit button clicked
                if(nicknameEditText.getText().toString().isEmpty() | roomEditText.getText().toString().isEmpty()) {
                    //Toast.makeText(MainActivity.this, "Please fill the inputs", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("nickname", nicknameEditText.getText().toString());
                intent.putExtra("room", Integer.valueOf(roomEditText.getText().toString()));

                startActivity(intent);
            }
        });
    }

}
