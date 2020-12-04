package com.quarterlife.instagramappwithfirebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.quarterlife.instagramappwithfirebase.Fragment.HomeFragment;
import com.quarterlife.instagramappwithfirebase.Fragment.NotificationFragment;
import com.quarterlife.instagramappwithfirebase.Fragment.ProfileFragment;
import com.quarterlife.instagramappwithfirebase.Fragment.SearchFragment;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Fragment selectedFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initView
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        // setOnNavigationItemSelectedListener
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        // 預設開啟是 HomeFragment
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment(); // 創建 HomeFragment，並把其指定為 selectedFragment
                            break;
                        case R.id.nav_search:
                            selectedFragment = new SearchFragment(); // 創建 SearchFragment，並把其指定為 selectedFragment
                            break;
                        case R.id.nav_add:
                            selectedFragment = null; // 指定 null 為 selectedFragment
                            startActivity(new Intent(MainActivity.this, PostActivity.class)); // 跳轉到 PostActivity
                            break;
                        case R.id.nav_heart:
                            selectedFragment = new NotificationFragment(); // 創建 NotificationFragment，並把其指定為 selectedFragment
                            break;
                        case R.id.nav_profile:
                            // 把目前使用者的 profile id 記在 PREFS 的 SharedPreferences 裡
                            getSharedPreferences("PREFS", MODE_PRIVATE).edit()
                                    .putString("profileid", FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .commit(); // 儲存資料

                            selectedFragment = new ProfileFragment(); // 創建 ProfileFragment，並把其指定為 selectedFragment
                            break;
                    }

                    if(selectedFragment != null){ // 若不是點選 R.id.nav_add
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit(); // 替換成被選擇的 Fragment
                    }

                    return true;
                }
            };
}