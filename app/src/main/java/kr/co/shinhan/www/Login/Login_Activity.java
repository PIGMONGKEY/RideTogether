package kr.co.shinhan.www.Login;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import kr.co.shinhan.www.Home_Activity;
import kr.co.shinhan.www.LogOnUserInfo;
import kr.co.shinhan.www.R;
import kr.co.shinhan.www.Register.Register_Activity;
import kr.co.shinhan.www.ResetPassword.ResetPassword;
import kr.co.shinhan.www.ServerConnect.ServerConnect;
import kr.co.shinhan.www.ServerConnect.ServerConnectInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Login_Activity extends AppCompatActivity {

    private EditText PN, PW;
    private String resultString;
    private CheckBox autoLoginCheckBox;
    private Button loginButton, findPasswordButton, registerButton;

    private Map<String, String> info;

    private LogOnUserInfo userInfo;

    private final static String TAG = "Lee-Login";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setTitle("LOGIN");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init() {
        PN = findViewById(R.id.PN_login);
        PW = findViewById(R.id.PW_login);
        autoLoginCheckBox = findViewById(R.id.autoLoginCheckBox);
        loginButton = findViewById(R.id.loginButton);
        findPasswordButton = findViewById(R.id.findPasswordButton);
        registerButton = findViewById(R.id.registerButton);
        autoLogin();
    }

    //------------------------------------------------------------------------?????????
    //????????? ?????? onClick
    public void loginRequestButtonFunc(View view) {
        setViewEnable(false);

        info = new HashMap<>();
        info.put("pn", PN.getText().toString());
        info.put("pw", PW.getText().toString());

        loginRequest();
    }

    //????????? ?????? - retrofit2 ??????
    private void loginRequest() {
        ServerConnectInterface SCI = ServerConnect.getClient().create(ServerConnectInterface.class);
        Call<LoginDataForm> call = SCI.loginRequest_php(info.get("pn"), info.get("pw"));

        call.enqueue(new Callback<LoginDataForm>() {
            @Override
            public void onResponse(Call<LoginDataForm> call, Response<LoginDataForm> response) {
                resultString = response.body().getResult();
                userInfo = new LogOnUserInfo(response.body().getPn(),
                        response.body().getName(),
                        response.body().getGender(),
                        response.body().getCampus(),
                        response.body().getSmoke());
                afterLoginRequest();
                Log.d(TAG, "onResponse : " + response.message() + " : " + response.code() + " : " + resultString);
            }

            @Override
            public void onFailure(Call<LoginDataForm> call, Throwable t) {
                resultString = "failure";
                afterLoginRequest();
                Log.d(TAG, "onFailure : " + t.getMessage() + " : " + resultString);
            }
        });
    }

    //????????? ?????? ?????? ??????
    private void afterLoginRequest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Login_Activity.this);

        if(resultString.equals("success")) {
            if(autoLoginCheckBox.isChecked()) {
                setAutoLogin();
            }
            Intent intent = new Intent(Login_Activity.this, Home_Activity.class);
            intent.putExtra("userInfo", userInfo);
            startActivity(intent);
        }

        else if(resultString.equals("fail_wp")) {
            AlertDialog dialog = builder.setMessage("??????????????? ????????????")
                    .setCancelable(false)
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            setViewEnable(true);
                        }
                    }).create();
            dialog.show();
        }

        else if(resultString.equals("fail_ne")) {
            AlertDialog dialog = builder.setMessage("???????????? ?????? ?????????????????????.")
                    .setCancelable(false)
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            setViewEnable(true);
                        }
                    }).create();
            dialog.show();
        }

        else if(resultString.equals("fail_c") || resultString.equals("failure")) {
            AlertDialog dialog = builder.setMessage("?????? ????????? ?????? ???????????? ????????? ??? ????????????.\n??????????????? ???????????????.")
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

    //??????????????? ?????? ??????
    private void setAutoLogin() {
        AutoLoginSharedPreferenceManager.setAutoLoginInformation(this, info.get("pn"), info.get("pw"));
    }

    //??????????????? ??????
    private void autoLogin() {
        Map<String, String> autoLoginInfo = AutoLoginSharedPreferenceManager.getAutoLoginInformation(this);
        if(autoLoginInfo.get("pn").equals("") && autoLoginInfo.get("pw").equals("")) {
            Log.d(TAG, "no Auto");
            return;
        }
        else {
            info = AutoLoginSharedPreferenceManager.getAutoLoginInformation(this);
            loginRequest();
        }
    }

    //------------------------------------------------------------------------????????????
    //???????????? ?????? onClick
    public void toRegister(View view) {
        Intent intent = new Intent(Login_Activity.this, Register_Activity.class);
        startActivity(intent);
    }

    //------------------------------------------------------------------------???????????? ??????
    //???????????? ?????? onClick
    public void toFindPassword(View view) {
        Intent intent = new Intent(Login_Activity.this, ResetPassword.class);
        startActivity(intent);
    }
    
    private void setViewEnable(boolean status) {
        PN.setEnabled(status);
        PW.setEnabled(status);
        loginButton.setEnabled(status);
        findPasswordButton.setEnabled(status);
        registerButton.setEnabled(status);
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
}