package kr.co.shinhan.www.Carpool.CarpoolBoardList;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import kr.co.shinhan.www.Carpool.CarpoolInfo.CarpoolInfo_Activity;
import kr.co.shinhan.www.Carpool.NewCarpool.NewCarpool_Activity;
import kr.co.shinhan.www.Home_Activity;
import kr.co.shinhan.www.LogOnUserInfo;
import kr.co.shinhan.www.MyPage.MyPage_Activity;
import kr.co.shinhan.www.R;
import kr.co.shinhan.www.ServerConnect.ServerConnect;
import kr.co.shinhan.www.ServerConnect.ServerConnectInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarpoolBoard_Activity extends AppCompatActivity {

    private LogOnUserInfo userInfo;
    private ListView carpoolList;
    private String resultString;

    private static final String TAG = "Lee-CarpoolList";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpool_board);

        getSupportActionBar().setTitle("카풀");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
        toCarpoolInfo();
    }

    private void init() {
        carpoolList = findViewById(R.id.carpool_ListView);
        getLogOnUserInfo();
        getCarpoolBoardList();
    }

    //intent로 넘겨준 로그인한 회원정보 받기
    private void getLogOnUserInfo() {
        Intent intent = getIntent();
        userInfo = new LogOnUserInfo((LogOnUserInfo) intent.getSerializableExtra("userInfo"));
    }

    //retrofit2를 이용하여 출발시간이 지나지 않은 카풀 리스트 받아와서 리스트뷰로 표시
    private void getCarpoolBoardList() {
        ServerConnectInterface SCI = ServerConnect.getClient().create(ServerConnectInterface.class);
        Call<ArrayList<CarpoolBoardListForm>> call = SCI.getCarpoolBoardList();
        call.enqueue(new Callback<ArrayList<CarpoolBoardListForm>>() {
            @Override
            public void onResponse(Call<ArrayList<CarpoolBoardListForm>> call, Response<ArrayList<CarpoolBoardListForm>> response) {
                ArrayList<CarpoolBoardListForm> item = new ArrayList<>();
                resultString = response.body().get(0).getResult();
                
                if(response.body().get(0).getResult().equals("success")) {
                    for(int i=1;i<response.body().size();i++)
                        item.add(response.body().get(i));

                    CarpoolListAdapter adapter = new CarpoolListAdapter(item, getApplicationContext());
                    carpoolList.setAdapter(adapter);
                }

                else if(response.body().get(0).getResult().equals("fail_c")) {
                    androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(CarpoolBoard_Activity.this);
                    androidx.appcompat.app.AlertDialog dialog = builder.setMessage("내부 문제로 인해 인해 카풀 정보를 가져올 수 없습니다.\n관리자에게 문의하세요.")
                            .setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).create();
                    dialog.show();
                }

                Log.d(TAG, response.message() + " : " + response.body().get(0).getResult());
            }

            @Override
            public void onFailure(Call<ArrayList<CarpoolBoardListForm>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    //새로운 카풀 버튼 onClick
    public void toNewCarpool(View view) {
        Intent intent = new Intent(CarpoolBoard_Activity.this, NewCarpool_Activity.class);
        intent.putExtra("userInfo", userInfo);
        startActivity(intent);
    }

    //리스트 onClick 리스너
    private void toCarpoolInfo() {
        carpoolList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(CarpoolBoard_Activity.this, CarpoolInfo_Activity.class);
                intent.putExtra("carpool_no", carpoolList.getAdapter().getItemId(i));
                intent.putExtra("userInfo", userInfo);
                startActivity(intent);
            }
        });
    }

    //액션바 설정
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile_refresh, menu);
        return true;
    }

    //액션바 아이템 클릭 이벤트
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                Intent intent = new Intent(CarpoolBoard_Activity.this, Home_Activity.class);
                intent.putExtra("userInfo", userInfo);
                startActivity(intent);
                return true;
            }
            case R.id.profile: {
                Intent intent = new Intent(CarpoolBoard_Activity.this, MyPage_Activity.class);
                intent.putExtra("userInfo", userInfo);
                startActivity(intent);
                return true;
            }

            case R.id.refresh: {
                getCarpoolBoardList();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}