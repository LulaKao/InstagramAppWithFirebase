package com.quarterlife.instagramappwithfirebase;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {
    private Button login, register;
    private FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();

        // 取得目前的使用者
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        // 若目前的使用者存在，直接跳到 MainActivity
        if(firebaseUser != null){ // 確認目前的使用者是否為 null
            startActivity(new Intent(StartActivity.this, MainActivity.class)); // 跳轉到 MainActivity
            finish(); // 結束此頁
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        // initView
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);

        // login 的監聽事件
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class)); // 跳轉到 LoginActivity
            }
        });

        // register 的監聽事件
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StartActivity.this, RegisterActivity.class)); // 跳轉到 RegisterActivity
            }
        });
    }
}