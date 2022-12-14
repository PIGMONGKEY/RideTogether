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
        getSupportActionBar().setTitle("????????????");
        requestCarpoolInfo();
    }

    //????????? ???????????? ?????????
    private void getLogOnUserInfo_BoardNo() {
        Intent intent = getIntent();
        userInfo = new LogOnUserInfo((LogOnUserInfo) intent.getSerializableExtra("userInfo"));
        boardNo = intent.getLongExtra("carpool_no", -1);
        Log.d(TAG, "no : " + boardNo);
    }

    //retrofit2??? ???????????? ?????? ???????????? ?????????
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

    //onResponse ?????? ??????
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
            AlertDialog dialog = builder.setMessage("?????? ????????? ?????? ?????? ????????? ????????? ??? ????????????.\n??????????????? ???????????????.")
                    .setCancelable(false)
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
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

    //?????? ????????? ?????????
    private void showCarpoolInfo() {
        sp.setText(sp.getText() + carpoolData.getSp());
        sp_d.setText(carpoolData.getSp_d());
        de.setText(de.getText() + carpoolData.getDe());
        de_d.setText(carpoolData.getDe_d());
        dd.setText(dt.getText() + "20" + carpoolData.getDt().substring(0,2) + "??? " + carpoolData.getDt().substring(3, 5) + "??? " + carpoolData.getDt().substring(6, 8));
        dt.setText(dt.getText() + carpoolData.getDt().substring(9, 11) + "??? " + carpoolData.getDt().substring(12, 14) + "???");
        ls.setText(ls.getText() + ((carpoolData.getAdmit() - carpoolData.getCurAdmit()) + " ??????"));

        if(carpoolData.getGender().equals("none"))
            gen.setText(gen.getText() + "??????, ??????");
        else if(carpoolData.getGender().equals("male"))
            gen.setText(gen.getText() + "??????");
        else
            gen.setText(gen.getText() + "??????");

        if(carpoolData.getSmoke().equals("C"))
            smo.setText(smo.getText() + "??????");
        else
            smo.setText(smo.getText() + "?????????");
    }

    //?????? ?????? ?????? onClick
    public void deleteCarpoolButtonFunc(View view) {
        deleteCarpoolButton.setEnabled(false);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(CarpoolInfo_Activity.this);
        AlertDialog dialog = builder.setMessage("?????? ?????????????????????????")
                .setPositiveButton("???", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestDeleteCarpool();
                    }
                })
                .setNegativeButton("?????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteCarpoolButton.setEnabled(true);
                    }
                }).create();
        dialog.show();
    }

    //?????? ?????? ?????? onClick
    public void joinCarpoolButtonFunc(View view) {
        joinCarpoolButton.setEnabled(false);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(CarpoolInfo_Activity.this);

        if((carpoolData.getGender().equals("male") && userInfo.getGender().equals("female")) ||
                (carpoolData.getGender().equals("female") && userInfo.getGender().equals("male")) ||
                (carpoolData.getSmoke().equals("N") && userInfo.getSmoke().equals("Y"))) {

            AlertDialog dialog = builder.setMessage("?????? ????????? ?????? ?????? ????????? ??? ????????????. ????????? ??????????????????.")
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
            AlertDialog dialog = builder.setMessage("???????????? ?????????, ????????? ?????? ????????? ???????????????????")
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestJoinCarpool();
                        }
                    })
                    .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            joinCarpoolButton.setEnabled(true);
                        }
                    }).create();
            dialog.show();
        }

    }

    //?????? ?????? ?????? ?????? onClick
    public void cancelJoinCarpoolButtonFunc(View view) {
        cancelJoinCarpoolButton.setEnabled(false);
        
        AlertDialog.Builder builder = new AlertDialog.Builder(CarpoolInfo_Activity.this);
        AlertDialog dialog = builder.setMessage("?????? ?????????????????????????")
                .setPositiveButton("???", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestCancelJoinCarpool();
                    }
                })
                .setNegativeButton("?????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cancelJoinCarpoolButton.setEnabled(true);
                    }
                }).create();
        dialog.show();
    }

    //retrofit2??? ???????????? ?????? ?????? ??????
    private void requestJoinCarpool() {
        ServerConnectInterface SIF = ServerConnect.getClient().create(ServerConnectInterface.class);
        Call<CarpoolInfoDataForm> call = SIF.joinCarpool((int)boardNo, userInfo.getPn());
        call.enqueue(new Callback<CarpoolInfoDataForm>() {
            @Override
            public void onResponse(Call<CarpoolInfoDataForm> call, Response<CarpoolInfoDataForm> response) {
                Log.d(TAG, response.body().getResult().toString());
                AlertDialog.Builder builder = new AlertDialog.Builder(CarpoolInfo_Activity.this);

                if(response.body().getResult().toString().equals("success")) {
                    AlertDialog dialog = builder.setMessage("?????? ??????\n??????????????? ??????????????? ???????????????.")
                            .setCancelable(false)
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
                    AlertDialog dialog = builder.setMessage("?????? ??????\n?????? ????????? ????????????.")
                            .setCancelable(false)
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
                    AlertDialog dialog = builder.setMessage("?????? ??????\n?????? ?????? ????????? ??????????????????.")
                            .setCancelable(false)
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
                    AlertDialog dialog = builder.setMessage("?????? ????????? ?????? ????????? ??? ????????????.\n??????????????? ???????????????.")
                            .setCancelable(false)
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
                AlertDialog dialog = builder.setMessage("?????? ????????? ?????? ????????? ??? ????????????.\n??????????????? ???????????????.")
                        .setCancelable(false)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
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

    //retrofit2??? ???????????? ?????? ?????? ?????? ??????
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
                    dialog = builder.setMessage("?????? ??????")
                            .setCancelable(false)
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
                    dialog = builder.setMessage("?????? ????????? ?????? ????????? ??? ????????????.\n??????????????? ???????????????.")
                            .setCancelable(false)
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
                dialog = builder.setMessage("?????? ????????? ?????? ????????? ??? ????????????.\n??????????????? ???????????????.")
                        .setCancelable(false)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cancelJoinCarpoolButton.setEnabled(true);
                            }
                        }).create();
                dialog.show();
            }
        });
    }

    //retrofit2??? ???????????? ?????? ?????? ??????
    private void requestDeleteCarpool() {
        ServerConnectInterface SCI = ServerConnect.getClient().create(ServerConnectInterface.class);
        Call<CarpoolInfoDataForm> call = SCI.deleteCarpool(carpoolData.getNo());
        call.enqueue(new Callback<CarpoolInfoDataForm>() {
            @Override
            public void onResponse(Call<CarpoolInfoDataForm> call, Response<CarpoolInfoDataForm> response) {
                Log.d(TAG, response.body().getResult());

                AlertDialog.Builder builder = new AlertDialog.Builder(CarpoolInfo_Activity.this);
                if(response.body().getResult().equals("success")) {
                    AlertDialog dialog = builder.setMessage("?????? ??????")
                            .setCancelable(false)
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
                    AlertDialog dialog = builder.setMessage("?????? ????????? ?????? ???????????? ???????????????.\n??????????????? ???????????????")
                            .setCancelable(false)
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
                AlertDialog dialog = builder.setMessage("?????? ????????? ?????? ???????????? ???????????????.\n??????????????? ???????????????")
                        .setCancelable(false)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteCarpoolButton.setEnabled(true);
                            }
                        }).create();
                dialog.show();
            }
        });
    }

    //????????? ??????
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_only_profile, menu);
        return true;
    }

    //????????? ????????? ?????? ?????????
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