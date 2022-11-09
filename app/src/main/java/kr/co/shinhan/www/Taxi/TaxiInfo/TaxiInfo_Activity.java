package kr.co.shinhan.www.Taxi.TaxiInfo;

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

import kr.co.shinhan.www.LogOnUserInfo;
import kr.co.shinhan.www.MyPage.MyPage_Activity;
import kr.co.shinhan.www.R;
import kr.co.shinhan.www.ServerConnect.ServerConnect;
import kr.co.shinhan.www.ServerConnect.ServerConnectInterface;
import kr.co.shinhan.www.Taxi.TaxiBoardList.TaxiBoard_Activity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TaxiInfo_Activity extends AppCompatActivity {

    private LogOnUserInfo userInfo;
    private long boardNo;
    private TextView sp, sp_d, de, de_d, dd, dt, ls, gen, smo;
    private Button deleteTaxiButton, joinTaxiButton, cancelJoinTaxiButton;
    
    private TaxiInfoDataForm taxiData;

    private static final String TAG = "Lee-TaxiInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taxi_info);

        init();
    }

    private void init() {
        sp = findViewById(R.id.ti_start_point);
        sp_d = findViewById(R.id.ti_start_point_d);
        de = findViewById(R.id.ti_destination);
        de_d = findViewById(R.id.ti_destination_d);
        dd = findViewById(R.id.ti_departure_date);
        dt = findViewById(R.id.ti_departure_time);
        ls = findViewById(R.id.ti_leftSeat);
        gen = findViewById(R.id.ti_gender);
        smo = findViewById(R.id.ti_smoke);

        deleteTaxiButton = findViewById(R.id.ti_deleteTaxi);
        joinTaxiButton = findViewById(R.id.ti_joinTaxi);
        cancelJoinTaxiButton = findViewById(R.id.ti_cancel_join);

        getLogOnUserInfo_BoardNo();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("택시정보");
        requestTaxiInfo();
    }

    //retrofit2를 이용하여 택시 새부정보 받아옴
    private void requestTaxiInfo() {
        ServerConnectInterface SCI = ServerConnect.getClient().create(ServerConnectInterface.class);
        Call<TaxiInfoDataForm> call = SCI.getTaxiInfo((int) boardNo, userInfo.getPn());
        call.enqueue(new Callback<TaxiInfoDataForm>() {
            @Override
            public void onResponse(Call<TaxiInfoDataForm> call, Response<TaxiInfoDataForm> response) {
                taxiData = new TaxiInfoDataForm(response.body());
                String result = response.body().getResult();
                showTaxiInfo();
                onResponseFunc_GetInfo(result);
                Log.d(TAG, taxiData.getResult());
            }

            @Override
            public void onFailure(Call<TaxiInfoDataForm> call, Throwable t) {
                Log.d(TAG, t.getMessage());
            }
        });
    }

    //onResponse 처리 함수
    private void onResponseFunc_GetInfo(String result) {
        if(result.equals("success_writer")) {
            deleteTaxiButton.setVisibility(View.VISIBLE);
            joinTaxiButton.setVisibility(View.GONE);
            cancelJoinTaxiButton.setVisibility(View.GONE);
        }
        else if(result.equals("success_passenger")) {
            deleteTaxiButton.setVisibility(View.GONE);
            joinTaxiButton.setVisibility(View.GONE);
            cancelJoinTaxiButton.setVisibility(View.VISIBLE);
        }
        else if(result.equals("success_none")){
            deleteTaxiButton.setVisibility(View.GONE);
            joinTaxiButton.setVisibility(View.VISIBLE);
            cancelJoinTaxiButton.setVisibility(View.GONE);
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(TaxiInfo_Activity.this);
            AlertDialog dialog = builder.setMessage("내부 문제로 인해 택시 정보를 불러올 수 없습니다.\n관리자에게 문의하세요.")
                    .setCancelable(false)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(TaxiInfo_Activity.this, TaxiBoard_Activity.class);
                            intent.putExtra("userInfo", userInfo);
                            startActivity(intent);
                        }
                    }).create();
            dialog.show();
        }
    }

    //택시 정보를 출력함
    private void showTaxiInfo() {
        sp.setText(sp.getText() + taxiData.getSp());
        sp_d.setText(taxiData.getSp_d());
        de.setText(de.getText() + taxiData.getDe());
        de_d.setText(taxiData.getDe_d());
        dd.setText(dd.getText() + "20" + taxiData.getDt().substring(0,2) + "년 " + taxiData.getDt().substring(3, 5) + "월 " + taxiData.getDt().substring(6, 8));
        dt.setText(dt.getText() + taxiData.getDt().substring(9, 11) + "시 " + taxiData.getDt().substring(12, 14) + "분");
        ls.setText(ls.getText() + ((taxiData.getAdmit() - taxiData.getCurAdmit()) + " 자리"));

        if(taxiData.getGender().equals("none"))
            gen.setText(gen.getText() + "여성, 남성");
        else if(taxiData.getGender().equals("male"))
            gen.setText(gen.getText() + "남성");
        else
            gen.setText(gen.getText() + "여성");

        if(taxiData.getSmoke().equals("C"))
            smo.setText(smo.getText() + "가능");
        else
            smo.setText(smo.getText() + "불가능");
    }

    //로그인 유저정보 받아옴
    private void getLogOnUserInfo_BoardNo() {
        Intent intent = getIntent();
        userInfo = new LogOnUserInfo((LogOnUserInfo) intent.getSerializableExtra("userInfo"));
        boardNo = intent.getLongExtra("taxi_no", -1);
        Log.d(TAG, "no : " + boardNo);
    }

    //택시 삭제 버튼 onClick
    public void deleteTaxiButtonFunc(View view) {
        deleteTaxiButton.setEnabled(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(TaxiInfo_Activity.this);
        AlertDialog dialog = builder.setMessage("정말 삭제하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestDeleteTaxi();
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteTaxiButton.setEnabled(true);
                    }
                }).create();
        dialog.show();
    }

    //택시 참여 버튼 onClick
    public void joinTaxiButtonFunc(View view) {
        joinTaxiButton.setEnabled(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(TaxiInfo_Activity.this);

        if((taxiData.getGender().equals("male") && userInfo.getGender().equals("female")) ||
                (taxiData.getGender().equals("female") && userInfo.getGender().equals("male")) ||
                (taxiData.getSmoke().equals("N") && userInfo.getSmoke().equals("Y"))) {

            AlertDialog dialog = builder.setMessage("탑승 조건이 맞지 않아 참여할 수 없습니다. 조건을 확인해주세요.")
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(TaxiInfo_Activity.this, TaxiBoard_Activity.class);
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
                            requestJoinTaxi();
                        }
                    })
                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            joinTaxiButton.setEnabled(true);
                        }
                    }).create();
            dialog.show();
        }

    }

    //택시 참여 취소 버튼 onClick
    public void cancelJoinTaxiButtonFunc(View view) {
        cancelJoinTaxiButton.setEnabled(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(TaxiInfo_Activity.this);
        AlertDialog dialog = builder.setMessage("정말 취소하시겠습니까?")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestCancelJoinTaxi();
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cancelJoinTaxiButton.setEnabled(true);
                    }
                }).create();
        dialog.show();
    }

    //retrofit2를 이용하여 택시 참여 요청
    private void requestJoinTaxi() {
        ServerConnectInterface SIF = ServerConnect.getClient().create(ServerConnectInterface.class);
        Call<TaxiInfoDataForm> call = SIF.joinTaxi((int)boardNo, userInfo.getPn());
        call.enqueue(new Callback<TaxiInfoDataForm>() {
            @Override
            public void onResponse(Call<TaxiInfoDataForm> call, Response<TaxiInfoDataForm> response) {
                Log.d(TAG, response.body().getResult().toString());
                AlertDialog.Builder builder = new AlertDialog.Builder(TaxiInfo_Activity.this);

                if(response.body().getResult().toString().equals("success")) {
                    AlertDialog dialog = builder.setMessage("참가 성공\n출발시간에 출발지에서 기다리세요.")
                            .setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(TaxiInfo_Activity.this, TaxiBoard_Activity.class);
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
                                    Intent intent = new Intent(TaxiInfo_Activity.this, TaxiBoard_Activity.class);
                                    intent.putExtra("userInfo", userInfo);
                                    startActivity(intent);
                                }
                            }).create();
                    dialog.show();
                }
                else if(response.body().getResult().toString().equals("fail_p")) {
                    AlertDialog dialog = builder.setMessage("참가 실패\n이미 다른 택시에 참여중입니다.")
                            .setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(TaxiInfo_Activity.this, TaxiBoard_Activity.class);
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
                                    Intent intent = new Intent(TaxiInfo_Activity.this, TaxiBoard_Activity.class);
                                    intent.putExtra("userInfo", userInfo);
                                    startActivity(intent);
                                }
                            }).create();
                    dialog.show();
                }
            }

            @Override
            public void onFailure(Call<TaxiInfoDataForm> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                AlertDialog.Builder builder = new AlertDialog.Builder(TaxiInfo_Activity.this);
                AlertDialog dialog = builder.setMessage("내부 문제로 인해 참여할 수 없습니다.\n관리자에게 문의하세요.")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(TaxiInfo_Activity.this, TaxiBoard_Activity.class);
                                intent.putExtra("userInfo", userInfo);
                                startActivity(intent);
                            }
                        }).create();
                dialog.show();
            }
        });
    }

    //retrofit2를 이용하여 택시 참여 취소 요청
    private void requestCancelJoinTaxi() {
        ServerConnectInterface SCI = ServerConnect.getClient().create(ServerConnectInterface.class);
        Call<TaxiInfoDataForm> call = SCI.cancelTaxiJoin((int)boardNo, userInfo.getPn());
        call.enqueue(new Callback<TaxiInfoDataForm>() {
            @Override
            public void onResponse(Call<TaxiInfoDataForm> call, Response<TaxiInfoDataForm> response) {
                String result = response.body().getResult();
                AlertDialog.Builder builder = new AlertDialog.Builder(TaxiInfo_Activity.this);
                AlertDialog dialog;

                if(result.equals("success")) {
                    dialog = builder.setMessage("취소 성공")
                            .setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(TaxiInfo_Activity.this, TaxiBoard_Activity.class);
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
                                    cancelJoinTaxiButton.setEnabled(true);
                                }
                            }).create();
                    dialog.show();
                }
            }

            @Override
            public void onFailure(Call<TaxiInfoDataForm> call, Throwable t) {
                AlertDialog.Builder builder = new AlertDialog.Builder(TaxiInfo_Activity.this);
                AlertDialog dialog;
                dialog = builder.setMessage("내부 문제로 인해 취소할 수 없습니다.\n관리자에게 문의하세요.")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cancelJoinTaxiButton.setEnabled(true);
                            }
                        }).create();
                dialog.show();
            }
        });
    }

    //retrofit2를 이용하여 택시 삭제 요청
    private void requestDeleteTaxi() {
        ServerConnectInterface SCI = ServerConnect.getClient().create(ServerConnectInterface.class);
        Call<TaxiInfoDataForm> call = SCI.deleteTaxi(taxiData.getNo());
        call.enqueue(new Callback<TaxiInfoDataForm>() {
            @Override
            public void onResponse(Call<TaxiInfoDataForm> call, Response<TaxiInfoDataForm> response) {
                Log.d(TAG, response.body().getResult());

                AlertDialog.Builder builder = new AlertDialog.Builder(TaxiInfo_Activity.this);
                if(response.body().getResult().equals("success")) {
                    AlertDialog dialog = builder.setMessage("삭제 성공")
                            .setCancelable(false)
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(TaxiInfo_Activity.this, TaxiBoard_Activity.class);
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
                                    deleteTaxiButton.setEnabled(true);
                                }
                            }).create();
                    dialog.show();
                }
            }

            @Override
            public void onFailure(Call<TaxiInfoDataForm> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                AlertDialog.Builder builder = new AlertDialog.Builder(TaxiInfo_Activity.this);
                AlertDialog dialog = builder.setMessage("내부 문제로 인해 삭제하지 못했습니다.\n관리자에게 문의하세요")
                        .setCancelable(false)
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteTaxiButton.setEnabled(true);
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
                Intent intent = new Intent(TaxiInfo_Activity.this, TaxiBoard_Activity.class);
                intent.putExtra("userInfo", userInfo);
                startActivity(intent);
                return true;
            }
            case R.id.profile: {
                Intent intent = new Intent(TaxiInfo_Activity.this, MyPage_Activity.class);
                intent.putExtra("userInfo", userInfo);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}