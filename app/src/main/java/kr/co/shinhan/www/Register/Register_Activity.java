package kr.co.shinhan.www.Register;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import kr.co.shinhan.www.R;
import kr.co.shinhan.www.ServerConnect.ServerConnect;
import kr.co.shinhan.www.ServerConnect.ServerConnectInterface;
import kr.co.shinhan.www.Start_Activity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Register_Activity extends AppCompatActivity {
    private EditText Name, PN, PNCode, PW, PW_check;
    private LinearLayout PNCodeLinear;
    private TextView PWnotEqualAlert;
    private RadioGroup gender, campus, smoke;
    private String resultString, errorString;
    private Button sendPNCode, verifyPNCode, register;

    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private HashMap<String, String> info = new HashMap<>();

    private static final String TAG = "Lee-Register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();

        getSupportActionBar().setTitle("REGISTER");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        passwordEqualCheck();
    }

    private void init() {
        Name = findViewById(R.id.Name_register);
        PNCodeLinear = findViewById(R.id.PNCodeLinear_register);
        PN = findViewById(R.id.PN_register);
        PNCode = findViewById(R.id.PNCode_register);
        sendPNCode = findViewById(R.id.sendPNCode);
        verifyPNCode = findViewById(R.id.verifyPNCodeButton);
        PW = findViewById(R.id.PW_register);
        PW_check = findViewById(R.id.PWCheck_register);
        PWnotEqualAlert=findViewById(R.id.PWnotEqualAlert);
        gender = findViewById(R.id.gender_register);
        campus = findViewById(R.id.campus_register);
        smoke = findViewById(R.id.smokeRG_register);
        register = findViewById(R.id.register_register);
    }

    //???????????? ??????

    private void registerRequest() {
        ServerConnectInterface SCI = ServerConnect.getClient().create(ServerConnectInterface.class);

        Call<RegisterDataForm> call = SCI.registerRequest_php(info.get("pn"), info.get("pw"), info.get("name"), info.get("gender"), info.get("campus"), info.get("smoke"));

        call.enqueue(new Callback<RegisterDataForm>() {
            @Override
            public void onResponse(Call<RegisterDataForm> call, Response<RegisterDataForm> response) {
                resultString = response.body().getResult();
                afterRegisterRequest();
                Log.d(TAG, "onResponse : " + response.message() + " : " + response.code() + " : " + resultString);
            }

            @Override
            public void onFailure(Call<RegisterDataForm> call, Throwable t) {
                resultString = "fail";
                Log.d(TAG, "onFailure : " + t.getMessage());
            }
        });
    }

    //???????????? ?????? ??????
    private void passwordEqualCheck() {
        PW_check.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!PW.getText().toString().equals(PW_check.getText().toString())) {
                    PW_check.setBackgroundResource(R.drawable.edittext_error);
                    PWnotEqualAlert.setVisibility(View.VISIBLE);
                }
                else {
                    PW_check.setBackgroundResource(R.drawable.edittext_normal);
                    PW.setBackgroundResource(R.drawable.edittext_normal);
                    PWnotEqualAlert.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        PW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!PW.getText().toString().equals(PW_check.getText().toString())) {
                    PW_check.setBackgroundResource(R.drawable.edittext_error);
                    PWnotEqualAlert.setVisibility(View.VISIBLE);
                }
                else {
                    PW_check.setBackgroundResource(R.drawable.edittext_normal);
                    PW.setBackgroundResource(R.drawable.edittext_normal);
                    PWnotEqualAlert.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }

    //????????? ?????? ????????? ??????
    private boolean emptyInfoCheck() {
        EditText[] eTemp = {Name, PN, PW, PW_check};
        RadioGroup[] rTemp = {gender, campus, smoke};
        String[] errTemp = {"?????????", "???????????????", "???????????????", "???????????? ????????????", "?????????", "????????????", "?????? ?????????"};
        errorString = "";
        boolean emptyCheckKey = true;

        for(int i=0;i<eTemp.length; i++){
            if (eTemp[i].getText().toString().equals("")){
                eTemp[i].setBackgroundResource(R.drawable.edittext_error);
                errorString += errTemp[i] + " ???????????? ????????????.\n";
                emptyCheckKey = false;
            }
        }

        for (int i=0;i<rTemp.length;i++) {
            if(rTemp[i].getCheckedRadioButtonId() == -1) {
                errorString += errTemp[i+4] + " ??????????????????.\n";
                emptyCheckKey = false;
            }
        }

        return emptyCheckKey;
    }

    //???????????? ????????????
    private void getInputData() {
        info.put("pn", PN.getText().toString());
        info.put("pw", PW.getText().toString());
        info.put("name", Name.getText().toString());

        if (gender.getCheckedRadioButtonId() == R.id.male_register)
            info.put("gender", "male");
        else
            info.put("gender", "female");

        if (campus.getCheckedRadioButtonId() == R.id.ujeoungbu_register)
            info.put("campus", "Uijeongbu");
        else
            info.put("campus", "Dongduchun");

        if(smoke.getCheckedRadioButtonId() == R.id.smoke_register)
            info.put("smoke", "Y");
        else
            info.put("smoke", "N");
    }

    //???????????? ?????? ??? ??????
    private void afterRegisterRequest() {

        if ("success".equals(resultString) == true) {
            errorString = "??????????????? ??????????????????!";
            final AlertDialog.Builder builder = new AlertDialog.Builder(Register_Activity.this);
            AlertDialog dialog = builder.setMessage(errorString)
                    .setCancelable(false)
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Register_Activity.this, Start_Activity.class);
                            startActivity(intent);
                        }
                    }).create();
            dialog.show();
        }

        else if ("fail_e".equals(resultString) == true) {
            errorString = "?????? ????????? ???????????????.";
            final AlertDialog.Builder builder = new AlertDialog.Builder(Register_Activity.this);
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

        else if ("fail_q".equals(resultString) == true || "fail_c".equals(resultString) == true || "fail".equals(resultString) == true) {
            errorString = "?????? ????????? ?????? ??????????????? ????????? ??? ????????????.\n??????????????? ???????????????.";
            final AlertDialog.Builder builder = new AlertDialog.Builder(Register_Activity.this);
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
            Log.d(TAG, "str : " + resultString);
        }
    }

    //firebase ?????? ?????? ??? ?????? option ??????
    private void setFirebasePhoneAuthInit() {
        mAuth = FirebaseAuth.getInstance();
        mAuth.setLanguageCode("ko");
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.d(TAG, "FirebasePhoneAuth onCompleted : " + phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.d(TAG, "FirebasePhoneAuth onFailed : " + e.getMessage());
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                Log.d(TAG, "onCodeSent : " + verificationId);

                mVerificationId = verificationId;
                mResendingToken = token;

                PNCodeLinear.setVisibility(View.VISIBLE);
            }
            //mCallbacks end
        };
    }

    //???????????? ?????? ??? ???????????? ??????
    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+82 " + phoneNumber)
                        .setTimeout(120L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    //???????????? ?????????
    private void resendPhoneNumberVerification(PhoneAuthProvider.ForceResendingToken forceResendingToken) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+82 " + PN.getText().toString())
                        .setTimeout(120L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .setForceResendingToken(forceResendingToken)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    //???????????? ??????
    private void signInWithPhoneAuthCredential(String code) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "SignInWithCredential : success");
                            FirebaseUser user = task.getResult().getUser();

                            AlertDialog dialog = builder.setMessage("?????? ??????")
                                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            PN.setEnabled(false);
                                            PNCodeLinear.setVisibility(View.GONE);
                                            sendPNCode.setVisibility(View.GONE);
                                        }
                                    })
                                    .create();
                            dialog.show();
                        }
                        else {
                            Log.d(TAG, "SignInWithCredential : failure", task.getException());
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Log.d(TAG, "SingInWithCredential : failure - wrong code");

                                AlertDialog dialog = builder.setMessage("?????? ??????\n??????????????? ????????????.")
                                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                PN.setEnabled(true);
                                                PNCode.setEnabled(true);
                                                verifyPNCode.setEnabled(true);
                                                sendPNCode.setEnabled(true);
                                                sendPNCode.setText("???????????? ?????????");
                                                sendPNCode.setOnClickListener(new View.OnClickListener() {
                                                    @Override
                                                    public void onClick(View view) {
                                                        PN.setEnabled(false);
                                                        sendPNCode.setEnabled(false);
                                                        resendPhoneNumberVerification(mResendingToken);
                                                    }
                                                });

                                            }
                                        })
                                        .create();
                                dialog.show();
                            }
                        }
                    }
                });
    }

    //???????????? ?????? ?????? onClick
    public void requestVerifyPhoneNumberButtonFunc(View view) {
        PN.setEnabled(false);
        sendPNCode.setEnabled(false);
        setFirebasePhoneAuthInit();
        startPhoneNumberVerification(PN.getText().toString());
    }

    //???????????? ?????? ?????? onClick
    public void verifyCredentialCodeButtonFunc(View view) {
        verifyPNCode.setEnabled(false);
        PNCode.setEnabled(false);
        signInWithPhoneAuthCredential(PNCode.getText().toString());
    }

    //???????????? ?????? onClick
    public void registerRequestButtonFunc(View view) {
        if (emptyInfoCheck() == false) {
            errorString += "**";
            errorString = errorString.replace("\n**", "");
            final AlertDialog.Builder builder = new AlertDialog.Builder(Register_Activity.this);
            AlertDialog dialog = builder.setMessage(errorString)
                    .setCancelable(false)
                    .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).create();
            dialog.show();
        }

        else {
            getInputData();
            registerRequest();
        }
    }

    private void setViewEnable(boolean status) {
        Name.setEnabled(status);
        PN.setEnabled(status);
        PNCode.setEnabled(status);
        PW.setEnabled(status);
        PW_check.setEnabled(status);
        gender.setEnabled(status);
        campus.setEnabled(status);
        smoke.setEnabled(status);
        sendPNCode.setEnabled(status);
        verifyPNCode.setEnabled(status);
        register.setEnabled(status);
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