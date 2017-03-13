package ps.source.hazard.hazard;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    TextView email_tv, password_tv;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        email_tv = (TextView) findViewById(R.id.email);
        password_tv = (TextView) findViewById(R.id.password);
        progressDialog = new ProgressDialog(this);

    }

    public void signIn(View view) {

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

    public void signUp(View view) {
        Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
        startActivity(intent);
    }

    public void resetPassword(View view) {

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

                    Toast.makeText(SignInActivity.this, "New Password has been sent to your Email",
                            Toast.LENGTH_LONG).show();

                    progressDialog.dismiss();
                }
            });
        }
    }
}
