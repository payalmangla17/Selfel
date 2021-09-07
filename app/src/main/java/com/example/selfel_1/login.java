package com.example.selfel_1;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class login extends AppCompatActivity {

    Button loggin_button;
    private static final String TAG = "loginn";
    TextView oppen_signup;
    FirebaseAuth firebaseAuth;
    String verify_Id;
    PhoneAuthProvider.ForceResendingToken force_token;

    EditText numberToLogin;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        final Intent i = new Intent(this, sign_up.class);
        firebaseAuth = firebaseAuth.getInstance();
        oppen_signup = findViewById(R.id.open_signup);
        loggin_button = findViewById(R.id.login_button);

        loggin_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerMobile();
            }
        });
        numberToLogin = findViewById(R.id.num_text);
        oppen_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(i);
            }
        });



        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {

                Log.d(TAG, "onVerificationCompleted:" + credential);

            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {

                    Toast.makeText(login.this,"cannot login"+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();

                }
                else if (e instanceof FirebaseTooManyRequestsException) {

                    Toast.makeText(login.this,"cannot login"+e.getLocalizedMessage(),Toast.LENGTH_SHORT).show();

                }

            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {

                verify_Id=verificationId;
                force_token=token;
                Log.e("onCodeSent","onCodeSent");
                otpDialog();

            }
        };

    }
    EditText otp_text;
    private void otpDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(login.this);

        View view = getLayoutInflater().inflate(R.layout.fragment_otp_checker,null);
        otp_text = view.findViewById(R.id.et_otp_dig_1);
        builder.setCancelable(false);

        builder.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(otp_text.getText().toString().equals(""))
                {
                    Toast.makeText(login.this, "Cannot leave empty", Toast.LENGTH_SHORT).show();
                    return ;
                }

                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verify_Id, otp_text.getText().toString().trim());
                signInWithPhoneAuthCredential(credential);

            }
        });
        builder.setView(view);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();


    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            user.getUid();

                            Intent inten = new Intent(login.this,MainActivity.class);
                            inten.putExtra("mmobile",numberToLogin.getText().toString());
                            startActivity(inten);


                        } else {
                            Toast.makeText(login.this,"failed",Toast.LENGTH_SHORT).show();

                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            }
                        }
                    }
                });
    }

    private void registerMobile() {
        String mob_no = "+91" + numberToLogin.getText().toString().trim();

        if (mob_no.length() != 13) {
            Toast.makeText(this, "Enter a valid 10 digit number", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Please wait !!", Toast.LENGTH_SHORT).show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                mob_no,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

}