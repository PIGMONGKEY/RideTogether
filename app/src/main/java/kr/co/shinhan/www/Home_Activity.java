package kr.co.shinhan.www;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import kr.co.shinhan.www.Carpool.CarpoolBoardList.CarpoolBoard_Activity;
import kr.co.shinhan.www.MyPage.MyPage_Activity;
import kr.co.shinhan.www.Taxi.TaxiBoardList.TaxiBoard_Activity;

public class Home_Activity extends AppCompatActivity {

    private LogOnUserInfo userInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().setTitle("HOME");
        getLogOnUserInfo();
    }

    //intent로 넘겨준 로그인한 회원 정보 받기
    private void getLogOnUserInfo() {
        Intent intent = getIntent();
        userInfo = new LogOnUserInfo((LogOnUserInfo) intent.getSerializableExtra("userInfo"));
    }

    //카풀 onClick
    public void toCarpoolBoard(View view) {
        Intent intent = new Intent(Home_Activity.this, CarpoolBoard_Activity.class);
        intent.putExtra("userInfo", userInfo);
        startActivity(intent);
    }

    //택시 onClick
    public void toTaxiBoard(View view) {
        Intent intent = new Intent(Home_Activity.this, TaxiBoard_Activity.class);
        intent.putExtra("userInfo", userInfo);
        startActivity(intent);
    }

    //액션바 버튼 세팅
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_only_profile, menu);
        return true;
    }

    //액션바 버튼 클릭 이밴트
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile: {
                Intent intent = new Intent(Home_Activity.this, MyPage_Activity.class);
                intent.putExtra("userInfo", userInfo);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    //하드웨어 뒤로가기 버튼 무력화
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}