package kr.co.shinhan.www.ResetPassword;

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

import java.util.concurrent.TimeUnit;

import kr.co.shinhan.www.Login.Login_Activity;
import kr.co.shinhan.www.R;
import kr.co.shinhan.www.ServerConnect.ServerConnect;
import kr.co.shinhan.www.ServerConnect.ServerConnectInterface;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPassword extends AppCompatActivity {

    TextView inputPNTV, PWnotEqualAlertTV;
    EditText PN, PNCode, newPW, newPWCheck;
    LinearLayout PNCodeLinear;
    Button sendPNCode, checkPNCode, changePW;

    private FirebaseAuth mAuth;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private static final String TAG = "Lee-ResetPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        getSupportActionBar().setTitle("비밀번호 변경");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init() {
        inputPNTV = findViewById(R.id.RP_inputPNTV);
        PN = findViewById(R.id.RP_PhoneNumberET);
        PNCode = findViewById(R.id.RP_PNCode);
        PNCodeLinear = findViewById(R.id.RP_PNCodeLinear);
        sendPNCode = findViewById(R.id.RP_sendPNCode);
        checkPNCode = findViewById(R.id.RP_verifyPNCodeButton);
        newPW = findViewById(R.id.RP_NewPassword);
        newPWCheck = findViewById(R.id.RP_NewPasswordCheck);
        changePW = findViewById(R.id.RP_ChangePassword);
        PWnotEqualAlertTV = findViewById(R.id.RP_PWnotEqualAlert);

        passwordEqualCheck();
    }

    //firebase 서버 통신 후 응답 option 설정
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

    //로봇인지 확인 후 인증번호 전송
    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+82 " + phoneNumber)
                        .setTimeout(30L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    //인증번호 재전송
    private void resendPhoneNumberVerification(PhoneAuthProvider.ForceResendingToken forceResendingToken) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber("+82 " + PN.getText().toString())
                        .setTimeout(30L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallbacks)
                        .setForceResendingToken(forceResendingToken)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    //인증번호 확인
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

                            AlertDialog dialog = builder.setMessage("인증 성공")
                                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            inputPNTV.setText("비밀번호 변경");
                                            PN.setVisibility(View.GONE);
                                            sendPNCode.setVisibility(View.GONE);
                                            PNCodeLinear.setVisibility(View.GONE);
                                            newPW.setVisibility(View.VISIBLE);
                                            newPWCheck.setVisibility(View.VISIBLE);
                                            changePW.setVisibility(View.VISIBLE);
                                        }
                                    })
                                    .create();
                            dialog.show();
                        }
                        else {
                            Log.d(TAG, "SignInWithCredential : failure", task.getException());
                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Log.d(TAG, "SingInWithCredential : failure - wrong code");

                                AlertDialog dialog = builder.setMessage("인증 실패\n인증번호가 틀립니다.")
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                PN.setEnabled(true);
                                                PNCode.setEnabled(true);
                                                checkPNCode.setEnabled(true);
                                                sendPNCode.setEnabled(true);
                                                sendPNCode.setText("인증번호 재전송");
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

    private void changePassword() {
        ServerConnectInterface SCI = ServerConnect.getClient().create(ServerConnectInterface.class);
        Call<ResetPasswordDataForm> call = SCI.resetPassword(newPW.getText().toString(), PN.getText().toString());
        call.enqueue(new Callback<ResetPasswordDataForm>() {
            @Override
            public void onResponse(Call<ResetPasswordDataForm> call, Response<ResetPasswordDataForm> response) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPassword.this);
                AlertDialog dialog;
                if(response.body().getResult().equals("success")) {
                    Log.d(TAG, response.body().getResult());

                    dialog = builder.setMessage("변경 성공!")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(ResetPassword.this, Login_Activity.class);
                                    startActivity(intent);
                                }
                            }).create();
                    dialog.show();
                }
                else {
                    Log.d(TAG, response.body().getResult());

                    dialog = builder.setMessage("내부 문제로 인해 비밀번호 변경을 실패했습니다.\n관리자에게 문의하세요.")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    newPW.setEnabled(true);
                                    newPWCheck.setEnabled(true);
                                    changePW.setEnabled(true);
                                }
                            }).create();
                    dialog.show();
                }
            }

            @Override
            public void onFailure(Call<ResetPasswordDataForm> call, Throwable t) {
                Log.d(TAG, t.getMessage());

                AlertDialog.Builder builder = new AlertDialog.Builder(ResetPassword.this);
                AlertDialog dialog = builder.setMessage("내부 문제로 인해 비밀번호 변경을 실패했습니다.\n관리자에게 문의하세요.")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                newPW.setEnabled(true);
                                newPWCheck.setEnabled(true);
                                changePW.setEnabled(true);
                            }
                        }).create();
                dialog.show();
            }
        });
    }

    //비밀번호 동일 확인
    private void passwordEqualCheck() {
        newPWCheck.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!newPW.getText().toString().equals(newPWCheck.getText().toString())) {
                    newPWCheck.setBackgroundResource(R.drawable.edittext_error);
                    PWnotEqualAlertTV.setVisibility(View.VISIBLE);
                }
                else {
                    newPWCheck.setBackgroundResource(R.drawable.edittext_normal);
                    newPW.setBackgroundResource(R.drawable.edittext_normal);
                    PWnotEqualAlertTV.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        newPW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(!newPW.getText().toString().equals(newPWCheck.getText().toString())) {
                    newPWCheck.setBackgroundResource(R.drawable.edittext_error);
                    PWnotEqualAlertTV.setVisibility(View.VISIBLE);
                }
                else {
                    newPWCheck.setBackgroundResource(R.drawable.edittext_normal);
                    newPW.setBackgroundResource(R.drawable.edittext_normal);
                    PWnotEqualAlertTV.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }

    //인증번호 전송버튼 onClick
    public void sendVerifyCodeButtonFunc(View view) {
        PN.setEnabled(false);
        sendPNCode.setEnabled(false);

        setFirebasePhoneAuthInit();
        startPhoneNumberVerification(PN.getText().toString());
    }



    //인증번호 확인버튼 onClick
    public void verifyCredentialCodeButtonFunc(View view) {
        PNCode.setEnabled(false);
        checkPNCode.setEnabled(false);
        signInWithPhoneAuthCredential(PNCode.getText().toString());
    }

    //비밀번호 변경버튼 onClick
    public void changePasswordButtonFunc(View view) {
        newPW.setEnabled(false);
        newPWCheck.setEnabled(false);
        changePW.setEnabled(false);
        changePassword();
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
}