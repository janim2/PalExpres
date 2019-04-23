package com.test.palexpress;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

public class HomeScreen extends FragmentActivity {

    private TextView mTextMessage;
    Accessories home_accessories;
    FragmentManager manager=getSupportFragmentManager();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    manager.beginTransaction().replace(R.id.container, new Home()).commit();
                    return true;
                case R.id.settings:
                    manager.beginTransaction().replace(R.id.container,new Settings()).commit();
                    return true;
                case R.id.orders:
                    manager.beginTransaction().replace(R.id.container, new Orders()).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        if(!home_accessories.getBoolean("login_checker")){
            startActivity(new Intent(HomeScreen.this,MainActivity.class));
        }
        manager.beginTransaction().replace(R.id.container, new Home()).commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        home_accessories = new Accessories(HomeScreen.this);
        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

}
