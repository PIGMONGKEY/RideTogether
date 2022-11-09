package kr.co.shinhan.www.MyPage;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import kr.co.shinhan.www.Home_Activity;
import kr.co.shinhan.www.LogOnUserInfo;
import kr.co.shinhan.www.Login.AutoLoginSharedPreferenceManager;
import kr.co.shinhan.www.R;
import kr.co.shinhan.www.ServerConnect.ServerConnect;
import kr.co.shinhan.www.ServerConnect.ServerConnectInterface;
import kr.co.shinhan.www.Start_Activity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPage_Activity extends AppCompatActivity {

    private RadioGroup mp_map;
    private RelativeLayout userInfoRelative;
    private TextView name, pn, gender_smoke, campus, PWnotEqualAlert;
    private EditText curPsET, newPsET, newPsCET;
    private Button showPsChangeLinear, changePs, logout, cancelChangePs;
    private LinearLayout userInfoLinear, changePasswordLinear, bugReportLinear;
    private ListView carpoolRecord, taxiRecord;

    private LogOnUserInfo userInfo;

    private static final String TAG = "Lee-MyPage";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        init();Log.d(TAG, userInfo.getPn());
        passwordEqualCheck();
    }

    private void init() {
        mp_map = findViewById(R.id.mp_mapRG);
        name = findViewById(R.id.mp_name);
        pn = findViewById(R.id.mp_pn);
        gender_smoke = findViewById(R.id.mp_gender_smoke);
        campus = findViewById(R.id.mp_campus);
        curPsET = findViewById(R.id.mp_curPassword);
        newPsET = findViewById(R.id.mp_newPassword);
        newPsCET = findViewById(R.id.mp_newPasswordCheck);
        showPsChangeLinear = findViewById(R.id.mp_showPasswordChangeLinear);
        changePs = findViewById(R.id.mp_changePassword);
        logout = findViewById(R.id.mp_logout);
        cancelChangePs = findViewById(R.id.mp_cancelChangePassword);
        userInfoLinear = findViewById(R.id.mp_userInfoShowLinear);
        changePasswordLinear = findViewById(R.id.mp_changePasswordLinear);
        userInfoRelative = findViewById(R.id.mp_userInfoRelative);
        PWnotEqualAlert = findViewById(R.id.mp_PWnotEqualAlert);
        carpoolRecord = findViewById(R.id.mp_carpoolListView);
        bugReportLinear = findViewById(R.id.mp_bugReportLinear);
        taxiRecord = findViewById(R.id.mp_taxiListView);

        getSupportActionBar().setTitle("마이페이지");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.icon_home);

        getLogOnUserInfo();
        showUserData();
        mp_mapListenerFunc();
    }

    private void getLogOnUserInfo() {
        Intent intent = getIntent();
        userInfo = new LogOnUserInfo((LogOnUserInfo) intent.getSerializableExtra("userInfo"));
    }

    private void clearAutoLogin() {
        AutoLoginSharedPreferenceManager.clearAutoLoginInformation(this);
    }

    private void showUserData() {
        String gen, smo, cam;

        if(userInfo.getGender().equals("male"))
            gen = "남성";
        else
            gen = "여성";

        if(userInfo.getSmoke().equals("N"))
            smo = "비흡연";
        else
            smo = "흡연";

        if(userInfo.getCampus().equals("Dongduchun"))
            cam = "동두천 캠퍼스";
        else
            cam = "의정부 캠퍼스";

        name.setText(userInfo.getName() + name.getText());
        pn.setText(userInfo.getPn());
        gender_smoke.setText(gen + "/" + smo);
        campus.setText(cam);
    }

    //내 정보, 카풀 이용 내역, 택시 이용 내역 변경 라디오 리스너
    private void mp_mapListenerFunc() {
        mp_map.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.mp_myInfo:
                        carpoolRecord.setVisibility(View.GONE);
                        taxiRecord.setVisibility(View.GONE);
                        bugReportLinear.setVisibility(View.GONE);
                        userInfoRelative.setVisibility(View.VISIBLE);
                        myInfoButtonListener();
                        break;
                    case R.id.mp_carpool:
                        resetMyInfoVisibility();
                        userInfoRelative.setVisibility(View.GONE);
                        taxiRecord.setVisibility(View.GONE);
                        bugReportLinear.setVisibility(View.GONE);
                        getCarpoolUsedRecord();
                        carpoolRecord.setVisibility(View.VISIBLE);
                        break;
                    case R.id.mp_taxi:
                        resetMyInfoVisibility();
                        userInfoRelative.setVisibility(View.GONE);
                        carpoolRecord.setVisibility(View.GONE);
                        bugReportLinear.setVisibility(View.GONE);
                        getTaxiUsedRecord();
                        taxiRecord.setVisibility(View.VISIBLE);
                        break;
                    case R.id.mp_bug:
                        resetMyInfoVisibility();
                        userInfoRelative.setVisibility(View.GONE);
                        carpoolRecord.setVisibility(View.GONE);
                        taxiRecord.setVisibility(View.GONE);
                        bugReportLinear.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    //내 정보 버튼들 리스너
    private void myInfoButtonListener() {
        showPsChangeLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInfoLinear.setVisibility(View.GONE);
                showPsChangeLinear.setVisibility(View.GONE);
                logout.setVisibility(View.GONE);
                changePasswordLinear.setVisibility(View.VISIBLE);
                changePs.setVisibility(View.VISIBLE);
                cancelChangePs.setVisibility(View.VISIBLE);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearAutoLogin();
                Intent intent = new Intent(MyPage_Activity.this, Start_Activity.class);
                startActivity(intent);
            }
        });

        changePs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                curPsET.setEnabled(false);
                newPsET.setEnabled(false);
                newPsCET.setEnabled(false);
                requestPsChange();
            }
        });

        cancelChangePs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                curPsET.setText("");
                newPsET.setText("");
                newPsCET.setText("");
                changePasswordLinear.setVisibility(View.GONE);
                changePs.setVisibility(View.GONE);
                cancelChangePs.setVisibility(View.GONE);
                userInfoLinear.setVisibility(View.VISIBLE);
                showPsChangeLinear.setVisibility(View.VISIBLE);
                logout.setVisibility(View.VISIBLE);

            }
        });
    }

    //내 정보 Visibility 초기화
    private void resetMyInfoVisibility() {
        changePasswordLinear.setVisibility(View.GONE);
        changePs.setVisibility(View.GONE);
        cancelChangePs.setVisibility(View.GONE);
        userInfoLinear.setVisibility(View.VISIBLE);
        showPsChangeLinear.setVisibility(View.VISIBLE);
        logout.setVisibility(View.VISIBLE);

        curPsET.setText("");
        newPsET.setText("");
        newPsCET.setText("");
    }

    //내 정보 비밀번호 변경 요청
    private void requestPsChange() {
        if (!newPsET.getText().toString().equals(newPsCET.getText().toString())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MyPage_Activity.this);
            AlertDialog dialog = builder.setMessage("새로운 비밀번호가 서로 다릅니다.")
                    .setCancelable(false)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            curPsET.setEnabled(true);
                            newPsET.setEnabled(true);
                            newPsCET.setEnabled(true);
                        }
                    }).create();
            dialog.show();

            return;
        }

        ServerConnectInterface SCI = ServerConnect.getClient().create(ServerConnectInterface.class);
        Call<MyPageDataForm> call = SCI.requestChangePassword(userInfo.getPn(), curPsET.getText().toString(), newPsET.getText().toString());
        call.enqueue(new Callback<MyPageDataForm>() {
            @Override
            public void onResponse(Call<MyPageDataForm> call, Response<MyPageDataForm> response) {
                String result = response.body().getResult();
                Log.d(TAG, result);

                AlertDialog.Builder builder = new AlertDialog.Builder(MyPage_Activity.this);
                if(result.equals("success")) {
                    AlertDialog dialog = builder.setMessage("비밀번호 변경 성공")
                            .setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    resetMyInfoVisibility();
                                }
                            }).create();
                    dialog.show();
                }
                else if(result.equals("fail_nc")) {
                    AlertDialog dialog = builder.setMessage("기존 비밀번호가 틀립니다.")
                            .setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    curPsET.setEnabled(true);
                                    newPsET.setEnabled(true);
                                    newPsCET.setEnabled(true);
                                }
                            }).create();
                    dialog.show();
                }
                else if (result.equals("fail_c")) {
                    AlertDialog dialog = builder.setMessage("내부 사정으로 인해 비밀번호를 변경할 수 없습니다.\n관리자에게 문의하세요")
                            .setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    curPsET.setEnabled(true);
                                    newPsET.setEnabled(true);
                                    newPsCET.setEnabled(true);
                                }
                            }).create();
                    dialog.show();
                }
            }

            @Override
            public void onFailure(Call<MyPageDataForm> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                curPsET.setEnabled(true);
                newPsET.setEnabled(true);
                newPsCET.setEnabled(true);
            }
        });
    }

    //내 정보 새 비밀번호 일치 체크
    private void passwordEqualCheck() {
        newPsCET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!newPsET.getText().toString().equals(newPsCET.getText().toString())) {
                    PWnotEqualAlert.setVisibility(View.VISIBLE);
                }
                else {
                    PWnotEqualAlert.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        newPsET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!newPsET.getText().toString().equals(newPsCET.getText().toString())) {
                    PWnotEqualAlert.setVisibility(View.VISIBLE);
                }
                else {
                    PWnotEqualAlert.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }


    //카풀 이용내역 데이터 불러오기
    private void getCarpoolUsedRecord() {
        ServerConnectInterface SCI = ServerConnect.getClient().create(ServerConnectInterface.class);
        Call<ArrayList<MyPageCarpoolDataForm>> call = SCI.getCarpoolRecord(userInfo.getPn());
        call.enqueue(new Callback<ArrayList<MyPageCarpoolDataForm>>() {
            @Override
            public void onResponse(Call<ArrayList<MyPageCarpoolDataForm>> call, Response<ArrayList<MyPageCarpoolDataForm>> response) {
                ArrayList<MyPageCarpoolDataForm> item = new ArrayList<>();
                if(response.body().get(0).getResult().equals("success")) {
                    for(int i=1;i<response.body().size();i++)
                        item.add(response.body().get(i));

                    MyPageCarpoolListAdapter adapter = new MyPageCarpoolListAdapter(item, getApplicationContext());
                    carpoolRecord.setAdapter(adapter);
                }

                else if(response.body().get(0).getResult().equals("fail_c")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyPage_Activity.this);
                    AlertDialog dialog = builder.setMessage("내부 문제로 인해 카풀 이용내역을 가져올 수 없습니다.\n관리자에게 문의하세요.")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).create();
                    dialog.show();
                }

                Log.d(TAG + "-Carpool", response.body().get(0).getResult());
            }

            @Override
            public void onFailure(Call<ArrayList<MyPageCarpoolDataForm>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    //택시 이용내역 데이터 불러오기
    private void getTaxiUsedRecord() {
        ServerConnectInterface SCI = ServerConnect.getClient().create(ServerConnectInterface.class);
        Call<ArrayList<MyPageTaxiDataForm>> call = SCI.getTaxiRecord(userInfo.getPn());
        call.enqueue(new Callback<ArrayList<MyPageTaxiDataForm>>() {
            @Override
            public void onResponse(Call<ArrayList<MyPageTaxiDataForm>> call, Response<ArrayList<MyPageTaxiDataForm>> response) {
                ArrayList<MyPageTaxiDataForm> item = new ArrayList<>();
                if(response.body().get(0).getResult().equals("success")) {
                    for(int i=1;i<response.body().size();i++)
                        item.add(response.body().get(i));

                    MyPageTaxiListAdapter adapter = new MyPageTaxiListAdapter(item, getApplicationContext());
                    taxiRecord.setAdapter(adapter);
                }

                else if(response.body().get(0).getResult().equals("fail_c")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyPage_Activity.this);
                    AlertDialog dialog = builder.setMessage("내부 문제로 인해 택시 이용내역을 가져올 수 없습니다.\n관리자에게 문의하세요.")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            }).create();
                    dialog.show();
                }

                Log.d(TAG + "-Taxi", response.body().get(0).getResult());
            }

            @Override
            public void onFailure(Call<ArrayList<MyPageTaxiDataForm>> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    //Focus를 가지고 있는 뷰 이외의 부분을 터치했을 때 키보드 내려가게 하는 함수
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View view = getCurrentFocus();
        if (view != null && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) && view instanceof EditText && !view.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            view.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + view.getLeft() - scrcoords[0];
            float y = ev.getRawY() + view.getTop() - scrcoords[1];
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                ((InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow((this.getWindow().getDecorView().getApplicationWindowToken()), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                Intent intent = new Intent(MyPage_Activity.this, Home_Activity.class);
                intent.putExtra("userInfo", userInfo);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}