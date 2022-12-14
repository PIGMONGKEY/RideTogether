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
        getSupportActionBar().setTitle("????????????");
        requestTaxiInfo();
    }

    //retrofit2??? ???????????? ?????? ???????????? ?????????
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

    //onResponse ?????? ??????
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
            AlertDialog dialog = builder.setMessage("?????? ????????? ?????? ?????? ????????? ????????? ??? ????????????.\n??????????????? ???????????????.")
                    .setCancelable(false)
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
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

    //?????? ????????? ?????????
    private void showTaxiInfo() {
        sp.setText(sp.getText() + taxiData.getSp());
        sp_d.setText(taxiData.getSp_d());
        de.setText(de.getText() + taxiData.getDe());
        de_d.setText(taxiData.getDe_d());
        dd.setText(dd.getText() + "20" + taxiData.getDt().substring(0,2) + "??? " + taxiData.getDt().substring(3, 5) + "??? " + taxiData.getDt().substring(6, 8));
        dt.setText(dt.getText() + taxiData.getDt().substring(9, 11) + "??? " + taxiData.getDt().substring(12, 14) + "???");
        ls.setText(ls.getText() + ((taxiData.getAdmit() - taxiData.getCurAdmit()) + " ??????"));

        if(taxiData.getGender().equals("none"))
            gen.setText(gen.getText() + "??????, ??????");
        else if(taxiData.getGender().equals("male"))
            gen.setText(gen.getText() + "??????");
        else
            gen.setText(gen.getText() + "??????");

        if(taxiData.getSmoke().equals("C"))
            smo.setText(smo.getText() + "??????");
        else
            smo.setText(smo.getText() + "?????????");
    }

    //????????? ???????????? ?????????
    private void getLogOnUserInfo_BoardNo() {
        Intent intent = getIntent();
        userInfo = new LogOnUserInfo((LogOnUserInfo) intent.getSerializableExtra("userInfo"));
        boardNo = intent.getLongExtra("taxi_no", -1);
        Log.d(TAG, "no : " + boardNo);
    }

    //?????? ?????? ?????? onClick
    public void deleteTaxiButtonFunc(View view) {
        deleteTaxiButton.setEnabled(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(TaxiInfo_Activity.this);
        AlertDialog dialog = builder.setMessage("?????? ?????????????????????????")
                .setPositiveButton("???", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestDeleteTaxi();
                    }
                })
                .setNegativeButton("?????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteTaxiButton.setEnabled(true);
                    }
                }).create();
        dialog.show();
    }

    //?????? ?????? ?????? onClick
    public void joinTaxiButtonFunc(View view) {
        joinTaxiButton.setEnabled(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(TaxiInfo_Activity.this);

        if((taxiData.getGender().equals("male") && userInfo.getGender().equals("female")) ||
                (taxiData.getGender().equals("female") && userInfo.getGender().equals("male")) ||
                (taxiData.getSmoke().equals("N") && userInfo.getSmoke().equals("Y"))) {

            AlertDialog dialog = builder.setMessage("?????? ????????? ?????? ?????? ????????? ??? ????????????. ????????? ??????????????????.")
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
            AlertDialog dialog = builder.setMessage("???????????? ?????????, ????????? ?????? ????????? ???????????????????")
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestJoinTaxi();
                        }
                    })
                    .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            joinTaxiButton.setEnabled(true);
                        }
                    }).create();
            dialog.show();
        }

    }

    //?????? ?????? ?????? ?????? onClick
    public void cancelJoinTaxiButtonFunc(View view) {
        cancelJoinTaxiButton.setEnabled(false);

        AlertDialog.Builder builder = new AlertDialog.Builder(TaxiInfo_Activity.this);
        AlertDialog dialog = builder.setMessage("?????? ?????????????????????????")
                .setPositiveButton("???", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestCancelJoinTaxi();
                    }
                })
                .setNegativeButton("?????????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        cancelJoinTaxiButton.setEnabled(true);
                    }
                }).create();
        dialog.show();
    }

    //retrofit2??? ???????????? ?????? ?????? ??????
    private void requestJoinTaxi() {
        ServerConnectInterface SIF = ServerConnect.getClient().create(ServerConnectInterface.class);
        Call<TaxiInfoDataForm> call = SIF.joinTaxi((int)boardNo, userInfo.getPn());
        call.enqueue(new Callback<TaxiInfoDataForm>() {
            @Override
            public void onResponse(Call<TaxiInfoDataForm> call, Response<TaxiInfoDataForm> response) {
                Log.d(TAG, response.body().getResult().toString());
                AlertDialog.Builder builder = new AlertDialog.Builder(TaxiInfo_Activity.this);

                if(response.body().getResult().toString().equals("success")) {
                    AlertDialog dialog = builder.setMessage("?????? ??????\n??????????????? ??????????????? ???????????????.")
                            .setCancelable(false)
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
                    AlertDialog dialog = builder.setMessage("?????? ??????\n?????? ????????? ????????????.")
                            .setCancelable(false)
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
                    AlertDialog dialog = builder.setMessage("?????? ??????\n?????? ?????? ????????? ??????????????????.")
                            .setCancelable(false)
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
                    AlertDialog dialog = builder.setMessage("?????? ????????? ?????? ????????? ??? ????????????.\n??????????????? ???????????????.")
                            .setCancelable(false)
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
                AlertDialog dialog = builder.setMessage("?????? ????????? ?????? ????????? ??? ????????????.\n??????????????? ???????????????.")
                        .setCancelable(false)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
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

    //retrofit2??? ???????????? ?????? ?????? ?????? ??????
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
                    dialog = builder.setMessage("?????? ??????")
                            .setCancelable(false)
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
                    dialog = builder.setMessage("?????? ????????? ?????? ????????? ??? ????????????.\n??????????????? ???????????????.")
                            .setCancelable(false)
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
                dialog = builder.setMessage("?????? ????????? ?????? ????????? ??? ????????????.\n??????????????? ???????????????.")
                        .setCancelable(false)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                cancelJoinTaxiButton.setEnabled(true);
                            }
                        }).create();
                dialog.show();
            }
        });
    }

    //retrofit2??? ???????????? ?????? ?????? ??????
    private void requestDeleteTaxi() {
        ServerConnectInterface SCI = ServerConnect.getClient().create(ServerConnectInterface.class);
        Call<TaxiInfoDataForm> call = SCI.deleteTaxi(taxiData.getNo());
        call.enqueue(new Callback<TaxiInfoDataForm>() {
            @Override
            public void onResponse(Call<TaxiInfoDataForm> call, Response<TaxiInfoDataForm> response) {
                Log.d(TAG, response.body().getResult());

                AlertDialog.Builder builder = new AlertDialog.Builder(TaxiInfo_Activity.this);
                if(response.body().getResult().equals("success")) {
                    AlertDialog dialog = builder.setMessage("?????? ??????")
                            .setCancelable(false)
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
                    AlertDialog dialog = builder.setMessage("?????? ????????? ?????? ???????????? ???????????????.\n??????????????? ???????????????")
                            .setCancelable(false)
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
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
                AlertDialog dialog = builder.setMessage("?????? ????????? ?????? ???????????? ???????????????.\n??????????????? ???????????????")
                        .setCancelable(false)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                deleteTaxiButton.setEnabled(true);
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