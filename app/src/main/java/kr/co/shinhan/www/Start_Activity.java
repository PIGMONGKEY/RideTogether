package kr.co.shinhan.www;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import kr.co.shinhan.www.Login.Login_Activity;

public class Start_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        getSupportActionBar().hide();
    }

    //시작 버튼 onClick
    public void toLogin(View view) {
        Intent intent = new Intent(Start_Activity.this, Login_Activity.class);
        startActivity(intent);
    }
}