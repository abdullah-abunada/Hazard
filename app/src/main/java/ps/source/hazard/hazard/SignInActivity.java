package ps.source.hazard.hazard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private FirebaseAuth mAuth;

    TextView email_tv, password_tv;
    private ProgressDialog progressDialog;

    private static final String TAG = "GoogleActivity";
    private static final int RC_SIGN_IN = 9001;
    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        email_tv = (TextView) findViewById(R.id.email);
        password_tv = (TextView) findViewById(R.id.password);
        progressDialog = new ProgressDialog(this);

        // Button listeners
        findViewById(R.id.signIn).setOnClickListener(this);
        findViewById(R.id.signUp).setOnClickListener(this);
        findViewById(R.id.forgetPassword).setOnClickListener(this);
        findViewById(R.id.google_sign_in_button).setOnClickListener(this);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    private void signIn() {

        String email = email_tv.getText().toString();
        String password = password_tv.getText().toString();

        if (email.isEmpty()) {
            Toast.makeText(SignInActivity.this, "Please Enter Your Email Address",
                    Toast.LENGTH_LONG).show();
        } else if (password.isEmpty()) {
            Toast.makeText(SignInActivity.this, "Please Enter Password",
                    Toast.LENGTH_LONG).show();
        } else {

            progressDialog.setMessage("Signing In Please Wait...");
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {

                                Toast.makeText(SignInActivity.this, R.string.auth_failed,
                                        Toast.LENGTH_SHORT).show();

                                progressDialog.dismiss();

                            } else {

                                progressDialog.dismiss();

                                Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }
                    });

        }

    }

    private void signUp() {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    private void resetPassword() {

        String email = email_tv.getText().toString();
        if (email.isEmpty()) {
            Toast.makeText(SignInActivity.this, "Please Enter Your Email Address",
                    Toast.LENGTH_LONG).show();
        } else {

            progressDialog.setMessage("Sending Verification Email Please Wait...");
            progressDialog.show();

            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    Toast.makeText(SignInActivity.this, "Reset Password Link has been sent to your Email",
                            Toast.LENGTH_LONG).show();

                    progressDialog.dismiss();
                }
            });
        }
    }

    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (!task.isSuccessful()) {
                            Toast.makeText(SignInActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }else{

                            Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View v) {

        int i = v.getId();
        switch (i) {
            case R.id.signIn:
                signIn();
                break;
            case R.id.signUp:
                signUp();
                break;
            case R.id.forgetPassword:
                resetPassword();
                break;
            case R.id.google_sign_in_button:
                googleSignIn();
                break;
        }
    }
}
