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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    private EditText username, fullname, email, password;
    private Button register;
    private TextView txt_login;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private ProgressDialog progressDialog;

    //========= onCreate START =========//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // initView
        username = findViewById(R.id.username);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        txt_login = findViewById(R.id.txt_login);

        // 取得 FirebaseAuth
        auth = FirebaseAuth.getInstance();

        // txt_login 的監聽事件
        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class)); // 跳轉到 LoginActivity
            }
        });

        // register 的監聽事件
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(RegisterActivity.this); // 創建 ProgressDialog
                progressDialog.setMessage("Please wait..."); // 設置 ProgressDialog 的文字
                progressDialog.show(); // 顯示 ProgressDialog

                String str_username = username.getText().toString(); // 取得用戶輸入的 username
                String str_fullname = fullname.getText().toString(); // 取得用戶輸入的 fullname
                String str_email = email.getText().toString(); // 取得用戶輸入的 email
                String str_password = password.getText().toString(); // 取得用戶輸入的 password

                if(TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname)
                    || TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)){ // 如果有欄位是空的
                    Toast.makeText(RegisterActivity.this, "All fields are required!", Toast.LENGTH_SHORT).show(); // 顯示需要填寫所有欄位的訊息
                } else if(str_password.length() < 6){ // 如果密碼少於六位數
                    Toast.makeText(RegisterActivity.this, "Password must have 6 characters", Toast.LENGTH_SHORT).show(); // 顯示密碼需要六位數的訊息
                } else { // 所有條件都符合
                    register(str_username, str_fullname, str_email, str_password); // 去註冊帳號
                }
            }
        });
    }
    //========= onCreate END =========//

    //========= 去註冊帳號 START =========//
    private void register(final String username, final String fullname, String email, String password){
        // 用 email 和密碼創建用戶
        auth.createUserWithEmailAndPassword(email, password)
                // 新增事件完成的監聽器
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){ // 任務成功
                            FirebaseUser firebaseUser = auth.getCurrentUser(); // 取得目前的使用者
                            String userid = firebaseUser.getUid(); // 取得目前使用者的 id

                            // 取得【 Users > 目前使用者的 id 】的資料庫參考
                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);

                            HashMap<String, Object> hashMap = new HashMap<>(); // 創建 HashMap
                            hashMap.put("id", userid); // 把 user id 放進 hashMap 裡
                            hashMap.put("username", username.toLowerCase()); // 把 user name 放進 hashMap 裡
                            hashMap.put("fullname", fullname); // 把 full name 放進 hashMap 裡
                            hashMap.put("bio", ""); // 把 bio 放進 hashMap 裡
                            hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/instagram-dd03b.appspot.com/o/user.png?alt=media&token=8b666625-95d7-4944-90db-e5ab391b5964"); // 把 image url 放進 hashMap 裡

                            // 設置 hashMap 的值到【 Users > 目前使用者的 id 】的資料庫參考
                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){ // 若任務成功
                                        progressDialog.dismiss(); // 讓 progressDialog 消失
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class); // 創建 Intent
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // 清掉舊的 Activity
                                        startActivity(intent); // 跳轉到 MainActivity

                                        /*  Intent.FLAG_ACTIVITY_CLEAR_TASK：
                                        這個 Flag 能造成在新的 Activity 啟動前，與舊的 Activity 相關聯的任務被清空。
                                        意即，新的 Activity 成為新任務的根，舊的 Activity 都被結束了。
                                        FLAG_ACTIVITY_CLEAR_TASK 只能與 FLAG_ACTIVITY_NEW_TASK 一起使用。   */
                                    }
                                }
                            });
                        } else { // 任務失敗
                            progressDialog.dismiss(); // 讓 progressDialog 消失
                            Toast.makeText(RegisterActivity.this, "You can't register with this email or password", Toast.LENGTH_SHORT).show(); // 顯示註冊失敗
                        }
                    }
                });
    }
    //========= 去註冊帳號 END =========//
}