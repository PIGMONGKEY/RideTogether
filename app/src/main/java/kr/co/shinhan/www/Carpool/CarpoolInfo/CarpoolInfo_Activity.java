package kr.co.shinhan.www.Carpool.CarpoolInfo;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import kr.co.shinhan.www.Carpool.CarpoolBoardList.CarpoolBoard_Activity;
import kr.co.shinhan.www.LogOnUserInfo;
import kr.co.shinhan.www.MyPage.MyPage_Activity;
import kr.co.shinhan.www.R;
import kr.co.shinhan.www.ServerConnect.ServerConnect;
import kr.co.shinhan.www.ServerConnect.ServerConnectInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CarpoolInfo_Activity extends AppCompatActivity {

    private LogOnUserInfo userInfo;
    private long boardNo;
    private TextView sp, sp_d, de, de_d, dd, dt, ls, gen, smo;
    private Button deleteCarpoolButton, joinCarpoolButton, cancelJoinCarpoolButton;
    
    private CarpoolInfoDataForm carpoolData;

    private static final String TAG = "Lee-CarpoolInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carpool_info);

        init();
    }

    private void init() {
        sp = findViewById(R.id.ci_start_point);
        sp_d = findViewById(R.id.ci_start_point_d);
        de = findViewById(R.id.ci_destination);
        de_d = findViewById(R.id.ci_destination_d);
        dd = findViewById(R.id.ci_departure_date);
        dt = findViewById(R.id.ci_departure_time);
        ls = findViewById(R.id.ci_leftSeat);
        gen = findViewById(R.id.ci_gender);
        smo = findViewById(R.id.ci_smoke);

        deleteCarpoolButton = findViewById(R.id.ci_deleteCarpool);
        joinCarpoolButton = findViewById(R.id.ci_joinCarpool);
        cancelJoinCarpoolButton = findViewById(R.id.ci_cancel_join);

        getLogOnUserInfo_BoardNo();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("카풀정보");
        requestCarpoolInfo();
    }

    //로그인 유저정보 받아옴
    private void getLogOnUserInfo_BoardNo() {
        Intent intent = getIntent();
        userInfo = new LogOnUserInfo((LogOnUserInfo) intent.getSerializableExtra("userInfo"));
        boardNo = intent.getLongExtra("carpool_no", -1);
        Log.d(TAG, "no : " + boardNo);
    }

    //retrofit2를 이용하여 카풀 새부정보 받아옴
    private void requestCarpoolInfo() {
        ServerConnectInterface SCI = ServerConnect.getClient().create(ServerConnectInterface.class);
    Call<CarpoolInfoDataForm> call = SCI.getCarpoolInfo((int) boardNo, userInfo.getPn());
        call.enqueue(new Callback<CarpoolInfoDataForm>() {
            @Override
            public void onResponse(Call<CarpoolInfoDataForm> call, Response<CarpoolInfoDataForm> response) {
                carpoolData = new CarpoolInfoDataForm(response.body());
                String result = response.body().getResult();
                showCarpoolInfo();
                onResponseFunc_GetInfo(result);
                Log.d(TAG, carpoolData.getResult());
            }

            @Override
            public void onFailure(Call<CarpoolInfoDataForm> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    //onResponse 처리 함수
    private void onResponseFunc_GetInfo(String result) {
        if(result.equals("success_writer")) {
            deleteCarpoolButton.setVisibility(View.VISIBLE);
            joinCarpoolButton.setVisibility(View.GONE);
            cancelJoinCarpoolButton.setVisibility(View.GONE);
        }
        else if(result.equals("success_passenger")) {
            deleteCarpoolButton.setVisibility(View.GONE);
            joinCarpoolButton.setVisibility(View.GONE);
            cancelJoinCarpoolButton.setVisibility(View.VISIBLE);
        }
        else if(result.equals("success_none")){
            deleteCarpoolButton.setVisibility(View.GONE);
            joinCarpoolButton.setVisibility(View.VISIBLE);
            cancelJoinCarpoolButton.setVisibility(View.GONE);
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(CarpoolInfo_Activity.this);
            AlertDialog dialog = builder.setMessage("내부 문제로 인해 카풀 정보를 불러올 수 없습니다.\n관리자에게 문의하세요.")
                    .setCancelable(false)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(CarpoolInfo_Activity.this, CarpoolBoard_Activity.class);
                            intent.putExtra("userInfo", userInfo);
                            startActivity(intent);
                        }
                    }).create();
            dialog.show();
        }
    }

    //카풀 정보를 출력함
    private void showCarpoolInfo() {
        sp.setText(sp.getText() + carpoolData.getSp());
        sp_d.setText(carpoolData.getSp_d());
        de.setText(de.getText() + carpoolData.getDe());
        de_d.setText(carpoolData.getDe_d());
        dd.setText(dt.getText() + "20" + carpoolData.getDt().substring(0,2) + "년 " + carpoolData.getDt().substring(3, 5) + "월 " + carpoolData.getDt().substring(6, 8));
        dt.setText(dt.getText() + carpoolData.getDt().substring(9, 11) + "시 " + carpoolData.getDt().substring(12, 14) + "분");
        ls.setText(ls.getText() + ((carpoolData.getAdmit() - carpoolData.getCurAdmit()) + " 자리"));

        if(carpoolData.getGender().equals("none"))
            gen.setText(gen.getText() + "여성, 남성");
        else if(carpoolData.getGender().equals("male"))
            gen.setText(gen.getText() + "남성");
        else
            gen.setText(gen.getText() + "여성");

        if(carpoolData.getSmoke().equals("C"))
            smo.setText(smo.getText() + "가능");
        else
            smo.setText(smo.getText() + "불가능");
    }

    //카풀 삭제 버튼 onClick
    public void deleteCarpoolButtonFunc(View view) {
        deleteCarpoolButton.setEnabled(false);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(CarpoolInfo_Activity.this);
        AlertDialog dialog = builder.setMessage("정말 삭제하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestDeleteCarpool();
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteCarpoolButton.setEnabled(true);
                    }
                }).create();
        dialog.show();
    }

    //카풀 참여 버튼 onClick
    public void joinCarpoolButtonFunc(View view) {
        joinCarpoolButton.setEnabled(false);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(CarpoolInfo_Activity.this);

        if((carpoolData.getGender().equals("male") && userInfo.getGender().equals("female")) ||
                (carpoolData.getGender().equals("female") && userInfo.getGender().equals("male")) ||
                (carpoolData.getSmoke().equals("N") && userInfo.getSmoke().equals("Y"))) {

            AlertDialog dialog = builder.setMessage("탑승 조건이 맞지 않아 참여할 수 없습니다. 조건을 확인해주세요.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(CarpoolInfo_Activity.this, CarpoolBoard_Activity.class);
                            intent.putExtra("userInfo", userInfo);
                            startActivity(intent);
                        }
                    }).create();
            dialog.show();
        }
        else {
            AlertDialog dialog = builder.setMessage("출발지와 목적지, 그리고 출발 시간을 확인하셨나요?")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestJoinCarpool();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            joinCarpoolButton.setEnabled(true);
                        }
                    }).create();
            dialog.show();
        }

    }

    //카풀 참여 취소 버튼 onClick
    public void cancelJoinCarpoolButtonFunc(View view) {
        cancelJoinCarpoolButton.setEnabled(false);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(CarpoolInfo_Activity.this);
        AlertDialog dialog = builder.setMessage("정말 취소하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestCancelJoinCarpool();
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cancelJoinCarpoolButton.setEnabled(true);
                    }
                }).create();
        dialog.show();
    }

    //retrofit2를 이용하여 카풀 참여 요청
    private void requestJoinCarpool() {
        ServerConnectInterface SIF = ServerConnect.getClient().create(ServerConnectInterface.class);
        Call<CarpoolInfoDataForm> call = SIF.joinCarpool((int)boardNo, userInfo.getPn());
        call.enqueue(new Callback<CarpoolInfoDataForm>() {
            @Override
            public void onResponse(Call<CarpoolInfoDataForm> call, Response<CarpoolInfoDataForm> response) {
                Log.d(TAG, response.body().getResult().toString());
                AlertDialog.Builder builder = new AlertDialog.Builder(CarpoolInfo_Activity.this);

                if(response.body().getResult().toString().equals("success")) {
                    AlertDialog dialog = builder.setMessage("참가 성공\n출발시간에 출발지에서 기다리세요.")
                            .setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(CarpoolInfo_Activity.this, CarpoolBoard_Activity.class);
                                    intent.putExtra("userInfo", userInfo);
                                    startActivity(intent);
                                }
                            }).create();
                    dialog.show();
                }
                else if(response.body().getResult().toString().equals("fail_f")) {
                    AlertDialog dialog = builder.setMessage("참가 실패\n남은 자리가 없습니다.")
                            .setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(CarpoolInfo_Activity.this, CarpoolBoard_Activity.class);
                                    intent.putExtra("userInfo", userInfo);
                                    startActivity(intent);
                                }
                            }).create();
                    dialog.show();
                }
                else if(response.body().getResult().toString().equals("fail_p")) {
                    AlertDialog dialog = builder.setMessage("참가 실패\n이미 다른 카풀에 참여중입니다.")
                            .setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(CarpoolInfo_Activity.this, CarpoolBoard_Activity.class);
                                    intent.putExtra("userInfo", userInfo);
                                    startActivity(intent);
                                }
                            }).create();
                    dialog.show();
                }
                else {
                    AlertDialog dialog = builder.setMessage("내부 문제로 인해 참여할 수 없습니다.\n관리자에게 문의하세요.")
                            .setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(CarpoolInfo_Activity.this, CarpoolBoard_Activity.class);
                                    intent.putExtra("userInfo", userInfo);
                                    startActivity(intent);
                                }
                            }).create();
                    dialog.show();
                }
            }

            @Override
            public void onFailure(Call<CarpoolInfoDataForm> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                AlertDialog.Builder builder = new AlertDialog.Builder(CarpoolInfo_Activity.this);
                AlertDialog dialog = builder.setMessage("내부 문제로 인해 참여할 수 없습니다.\n관리자에게 문의하세요.")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(CarpoolInfo_Activity.this, CarpoolBoard_Activity.class);
                                intent.putExtra("userInfo", userInfo);
                                startActivity(intent);
                            }
                        }).create();
                dialog.show();
            }
        });
    }

    //retrofit2를 이용하여 카풀 참여 취소 요청
    private void requestCancelJoinCarpool() {
        ServerConnectInterface SCI = ServerConnect.getClient().create(ServerConnectInterface.class);
        Call<CarpoolInfoDataForm> call = SCI.cancelCarpoolJoin((int)boardNo, userInfo.getPn());
        call.enqueue(new Callback<CarpoolInfoDataForm>() {
            @Override
            public void onResponse(Call<CarpoolInfoDataForm> call, Response<CarpoolInfoDataForm> response) {
                String result = response.body().getResult();
                AlertDialog.Builder builder = new AlertDialog.Builder(CarpoolInfo_Activity.this);
                AlertDialog dialog;

                if(result.equals("success")) {
                    dialog = builder.setMessage("취소 성공")
                            .setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(CarpoolInfo_Activity.this, CarpoolBoard_Activity.class);
                                    intent.putExtra("userInfo", userInfo);
                                    startActivity(intent);
                                }
                            }).create();
                    dialog.show();
                }
                else {
                    dialog = builder.setMessage("내부 문제로 인해 취소할 수 없습니다.\n관리자에게 문의하세요.")
                            .setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    cancelJoinCarpoolButton.setEnabled(true);
                                }
                            }).create();
                    dialog.show();
                }
            }

            @Override
            public void onFailure(Call<CarpoolInfoDataForm> call, Throwable t) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CarpoolInfo_Activity.this);
                AlertDialog dialog;
                dialog = builder.setMessage("내부 문제로 인해 취소할 수 없습니다.\n관리자에게 문의하세요.")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cancelJoinCarpoolButton.setEnabled(true);
                            }
                        }).create();
                dialog.show();
            }
        });
    }

    //retrofit2를 이용하여 카풀 삭제 요청
    private void requestDeleteCarpool() {
        ServerConnectInterface SCI = ServerConnect.getClient().create(ServerConnectInterface.class);
        Call<CarpoolInfoDataForm> call = SCI.deleteCarpool(carpoolData.getNo());
        call.enqueue(new Callback<CarpoolInfoDataForm>() {
            @Override
            public void onResponse(Call<CarpoolInfoDataForm> call, Response<CarpoolInfoDataForm> response) {
                Log.d(TAG, response.body().getResult());

                AlertDialog.Builder builder = new AlertDialog.Builder(CarpoolInfo_Activity.this);
                if(response.body().getResult().equals("success")) {
                    AlertDialog dialog = builder.setMessage("삭제 성공")
                            .setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(CarpoolInfo_Activity.this, CarpoolBoard_Activity.class);
                                    intent.putExtra("userInfo", userInfo);
                                    startActivity(intent);
                                }
                            }).create();
                    dialog.show();
                }
                else {
                    AlertDialog dialog = builder.setMessage("내부 문제로 인해 삭제하지 못했습니다.\n관리자에게 문의하세요")
                            .setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    deleteCarpoolButton.setEnabled(true);
                                }
                            }).create();
                    dialog.show();
                }
            }

            @Override
            public void onFailure(Call<CarpoolInfoDataForm> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                AlertDialog.Builder builder = new AlertDialog.Builder(CarpoolInfo_Activity.this);
                AlertDialog dialog = builder.setMessage("내부 문제로 인해 삭제하지 못했습니다.\n관리자에게 문의하세요")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteCarpoolButton.setEnabled(true);
                            }
                        }).create();
                dialog.show();
            }
        });
    }

    //액션바 설정
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_only_profile, menu);
        return true;
    }

    //액션바 아이탬 선택 이벤트
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                Intent intent = new Intent(CarpoolInfo_Activity.this, CarpoolBoard_Activity.class);
                intent.putExtra("userInfo", userInfo);
                startActivity(intent);
                return true;
            }
            case R.id.profile: {
                Intent intent = new Intent(CarpoolInfo_Activity.this, MyPage_Activity.class);
                intent.putExtra("userInfo", userInfo);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}