package kr.co.shinhan.www.Carpool.NewCarpool;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import kr.co.shinhan.www.Carpool.CarpoolBoardList.CarpoolBoard_Activity;
import kr.co.shinhan.www.LogOnUserInfo;
import kr.co.shinhan.www.MyPage.MyPage_Activity;
import kr.co.shinhan.www.R;
import kr.co.shinhan.www.ServerConnect.ServerConnect;
import kr.co.shinhan.www.ServerConnect.ServerConnectInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewCarpool_Activity extends AppCompatActivity {

    private LogOnUserInfo userInfo;

    private EditText sp, sp_d, de, de_d, admit_ET;
    private TextView time, date;
    private LinearLayout timePickerLinear, calendarViewLinear;
    private TimePicker timePicker;
    private CalendarView calendarView;
    private CheckBox sameG, noSmoke;
    private Button changeDate, changeTime;

    private String writer_pn, write_time, start_point, start_Point_D, destination, destination_D, departure_date, departure_time, gender, smoke;
    private String errorString;
    private int admit;

    private static final String TAG = "Lee-NewCarpool";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_carpool);

        getSupportActionBar().setTitle("??? ?????? ?????????");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init() {
        sp = findViewById(R.id.newCP_startPointET);
        sp_d = findViewById(R.id.newCP_startPointDescET);
        de = findViewById(R.id.newCP_destinationET);
        de_d = findViewById(R.id.newCP_destinationDescET);
        admit_ET = findViewById(R.id.newCP_admit);
        timePicker = findViewById(R.id.newCP_timePicker);
        time = findViewById(R.id.newCP_timeTV);
        timePickerLinear = findViewById(R.id.newCP_timePickLinear);
        calendarView = findViewById(R.id.newCP_calendarView);
        calendarViewLinear = findViewById(R.id.newCP_calendarLinear);
        date = findViewById(R.id.newCP_date);
        sameG = findViewById(R.id.newCP_sameG);
        noSmoke = findViewById(R.id.newCP_noSmoke);
        changeDate = findViewById(R.id.newCP_changeDate);
        changeTime = findViewById(R.id.newCP_changeTime);

        time.setText(timePicker.getHour() + " : " + timePicker.getMinute());

        LocalDate today = LocalDate.now();
        date.setText(today.getYear()%100 + "/" + today.getMonthValue() + "/" + today.getDayOfMonth());
        calendarView.setMinDate(System.currentTimeMillis() - 1000);

        String month, day;
        if(today.getMonthValue() < 10)
            month = "0" + today.getMonthValue();
        else
            month = today.getMonthValue() + "";
        if(today.getDayOfMonth() < 10)
            day = "0" + today.getDayOfMonth();
        else
            day = today.getDayOfMonth() + "";
        departure_date = today.getYear()%100 + "/" + month+ "/" + day;

        getLogOnUserInfo();
        calenderListener();
    }

    private void getLogOnUserInfo() {
        Intent intent = getIntent();
        userInfo = new LogOnUserInfo((LogOnUserInfo) intent.getSerializableExtra("userInfo"));
    }

    //?????? ?????? onClick
    public void changeDate(View view) {
        calendarViewLinear.setVisibility(View.VISIBLE);
    }

    //?????? ?????? ?????? onClick
    public void confirmDate(View view) {
        calendarViewLinear.setVisibility(View.GONE);
    }

    //???????????? ?????????
    private void calenderListener() {
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                int year = i%100;
                String month, day;
                if(i1+1 < 10)
                    month = "0" + (i1+1);
                else
                    month = (i1+1) + "";
                if(i2 < 10)
                    day = "0" + i2;
                else
                    day = i2 + "";
                date.setText(year + "/" + (i1+1) + "/" + day);
                departure_date = year + "/" + month + "/" + day;
            }
        });
    }

    //?????? ?????? onClick
    public void changeTime(View view) {
        timePickerLinear.setVisibility(View.VISIBLE);
    }

    //?????? ?????? ?????? onClick
    public void confirmTime(View view) {
        timePickerLinear.setVisibility(View.GONE);
        time.setText(timePicker.getHour() + " : " + timePicker.getMinute());
    }

    //???????????? onClick
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void carpoolRegisterButton(View view) {

        setViewEnable(false);

        if(!emptyInfoCheck()) {
            errorString += "**";
            errorString = errorString.replace("\n**", "");
            AlertDialog.Builder builder = new AlertDialog.Builder(NewCarpool_Activity.this);
            AlertDialog dialog = builder.setMessage(errorString)
                    .setCancelable(false)
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            setViewEnable(true);
                        }
                    }).create();
            dialog.show();
        }
        else {
            getBoardInfo();
            finalCheck();
        }
    }

    //???????????? ????????????
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void getBoardInfo() {
        LocalDateTime now = LocalDateTime.now();
        String currentDateTime = now.format(DateTimeFormatter.ofPattern("yy/MM/dd/HH/mm"));
        String hour, min;

        writer_pn = userInfo.getPn();
        write_time = currentDateTime;
        start_point = sp.getText().toString();
        start_Point_D = sp_d.getText().toString();
        destination = de.getText().toString();
        destination_D = de_d.getText().toString();

        if(timePicker.getHour()<10)
            hour = "0" + timePicker.getHour();
        else
            hour = timePicker.getHour() + "";
        if(timePicker.getMinute()<10)
            min = "0" + timePicker.getMinute();
        else
            min = timePicker.getMinute() + "";

        departure_time = departure_date + "/" + hour + "/" + min;

        admit = Integer.parseInt(admit_ET.getText().toString());

        if(sameG.isChecked())
            gender = userInfo.getGender();
        else
            gender = "none";

        if(noSmoke.isChecked())
            smoke = "N";
        else
            smoke = "C";
    }

    //?????? ?????? ??????
    private void requestCarpoolBoardRegister() {
        ServerConnectInterface SCI = ServerConnect.getClient().create(ServerConnectInterface.class);
        Call<NewCarpoolDataForm> call = SCI.requestMakeNewCarpool(
                writer_pn,
                write_time,
                start_point,
                start_Point_D,
                destination,
                destination_D,
                departure_time,
                admit,
                gender,
                smoke);
        call.enqueue(new Callback<NewCarpoolDataForm>() {
            @Override
            public void onResponse(Call<NewCarpoolDataForm> call, Response<NewCarpoolDataForm> response) {
                Log.d(TAG, response.body().getResult());

                AlertDialog.Builder builder = new AlertDialog.Builder(NewCarpool_Activity.this);

                if(response.body().getResult().equals("success")) {

                    AlertDialog dialog = builder.setMessage("?????? ??????.")
                        .setCancelable(false)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(NewCarpool_Activity.this, CarpoolBoard_Activity.class);
                                intent.putExtra("userInfo", userInfo);
                                startActivity(intent);
                            }
                        }).create();
                    dialog.show();
                }

                else if(response.body().getResult().equals("fail_q") || response.body().getResult().equals("fail_c")) {
                    AlertDialog dialog = builder.setMessage("?????? ????????? ?????? ?????? ????????? ??? ????????????.")
                            .setCancelable(false)
                            .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    setViewEnable(true);
                                }
                            }).create();
                    dialog.show();
                }
            }

            @Override
            public void onFailure(Call<NewCarpoolDataForm> call, Throwable t) {
                Log.d(TAG, t.getMessage());

                AlertDialog.Builder builder = new AlertDialog.Builder(NewCarpool_Activity.this);
                AlertDialog dialog = builder.setMessage("?????? ????????? ?????? ?????? ????????? ??? ????????????.")
                        .setCancelable(false)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                setViewEnable(true);
                            }
                        }).create();
                dialog.show();
            }
        });
    }

    //????????? ?????? ????????? ??????
    private boolean emptyInfoCheck() {
        EditText[] eTemp = {sp, de};
        String[] sTemp = {"???????????? ", "????????????"};
        boolean emptyCheckKey = true;
        errorString = "";

        for(int i =0;i<eTemp.length;i++) {
            if(eTemp[i].getText().toString().equals("")) {
                emptyCheckKey = false;
                errorString += sTemp[i] + "??????????????????.\n";
            }
        }

        return emptyCheckKey;
    }

    //??????????????? ???????????? ?????? ??????
    private void finalCheck() {
        String gen, smo;

        if (!gender.equals("none")) {
            if (userInfo.getGender().equals("male"))
                gen = "??????";
            else
                gen = "??????";
        }
        else
            gen = "????????????";

        if(smoke.equals("N"))
            smo = "?????????";
        else
            smo = "??????";

        String boardInfo =
                "????????? : " + start_point + "\n" +
                "????????? : " + destination + "\n" +
                "???????????? : " + timePicker.getHour() + " ??? " + timePicker.getMinute() + "???" + "\n" +
                "???????????? : " + gen + "\n" +
                "????????? ?????? : " + smo + "\n" +
                "?????? ??????(?????? ??????) : " + admit + "???";

        AlertDialog.Builder builder = new AlertDialog.Builder(NewCarpool_Activity.this);
        AlertDialog dialog = builder.setMessage(boardInfo)
                .setCancelable(true)
                .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        requestCarpoolBoardRegister();
                    }
                })
                .setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setViewEnable(true);
                        return;
                    }
                })
                .create();
        dialog.show();
    }

    //?????? ????????? Enable ??????
    private void setViewEnable(boolean status) {
        sp.setEnabled(status);
        sp_d.setEnabled(status);
        de.setEnabled(status);
        de_d.setEnabled(status);
        changeDate.setEnabled(status);
        changeTime.setEnabled(status);
        admit_ET.setEnabled(status);
        sameG.setEnabled(status);
        noSmoke.setEnabled(status);
    }

    //Focus??? ????????? ?????? ??? ????????? ????????? ???????????? ??? ????????? ???????????? ?????? ??????
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
                Intent intent = new Intent(NewCarpool_Activity.this, CarpoolBoard_Activity.class);
                intent.putExtra("userInfo", userInfo);
                startActivity(intent);
                return true;
            }

            case R.id.profile: {
                Intent intent = new Intent(NewCarpool_Activity.this, MyPage_Activity.class);
                intent.putExtra("userInfo", userInfo);
                startActivity(intent);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}