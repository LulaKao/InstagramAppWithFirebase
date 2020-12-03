package com.quarterlife.instagramappwithfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText email, password;
    private Button login;
    private TextView txt_signup;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initView
        email = findViewById(R.id.email);
        login = findViewById(R.id.login);
        password = findViewById(R.id.password);
        txt_signup = findViewById(R.id.txt_signup);

        // 取得 Firebase 認證
        auth = FirebaseAuth.getInstance();

        // 設置 txt_signup 的監聽事件
        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class)); // 跳轉到 RegisterActivity
            }
        });

        // 設置 login 的監聽事件
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this); // 創建 ProgressDialog
                progressDialog.setMessage("Please wait..."); // 設置 progressDialog 的訊息文字
                progressDialog.show(); // 顯示 progressDialog

                String str_email = email.getText().toString(); // 取得用戶輸入的 email
                String str_password = password.getText().toString(); // 取得用戶輸入的 password

                if(TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){ // 如果有欄位的內容是空的
                    Toast.makeText(LoginActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show(); // 顯示需要填寫所有欄位的訊息
                } else { // 如果所有欄位都有輸入
                    // 用 email 和密碼創建用戶
                    auth.signInWithEmailAndPassword(str_email, str_password)
                            // 新增事件完成的監聽器
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){ // 如果任務成功
                                        // 取得【 Users > 目前使用者的 id 】的資料庫參考
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference()
                                                .child("Users").child(auth.getCurrentUser().getUid());

                                        // 新增 ValueEventListener 到【 Users > 目前使用者的 id 】的資料庫參考上
                                        reference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                progressDialog.dismiss(); // 讓 progressDialog 消失
                                                Intent intent = new Intent(LoginActivity.this, MainActivity.class); // 創建 Intent
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // 清掉舊的 Activity
                                                startActivity(intent); // 跳轉到 MainActivity
                                                finish(); // 結束此頁

                                                /*  Intent.FLAG_ACTIVITY_CLEAR_TASK：
                                                這個 Flag 能造成在新的 Activity 啟動前，與舊的 Activity 相關聯的任務被清空。
                                                意即，新的 Activity 成為新任務的根，舊的 Activity 都被結束了。
                                                FLAG_ACTIVITY_CLEAR_TASK 只能與 FLAG_ACTIVITY_NEW_TASK 一起使用。   */
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    } else { // 如果任務失敗
                                        progressDialog.dismiss(); // 讓 progressDialog 消失
                                        Toast.makeText(LoginActivity.this, "Authentication failed!", Toast.LENGTH_SHORT).show(); // 顯示登入失敗
                                    }
                                }
                            });
                }
            }
        });
    }
}