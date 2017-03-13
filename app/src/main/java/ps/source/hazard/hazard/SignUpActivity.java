package ps.source.hazard.hazard;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    TextView email_tv, password_tv, password_confirmation_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();

        email_tv = (TextView) findViewById(R.id.email);
        password_tv = (TextView) findViewById(R.id.password);
        password_confirmation_tv = (TextView) findViewById(R.id.password_confirmation);
    }

    public void signUp(View view) {

        String email = email_tv.getText().toString();
        String password = password_tv.getText().toString();
        String password_confirmation = password_confirmation_tv.getText().toString();

        if (email.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Please Enter Your Email Address",
                    Toast.LENGTH_LONG).show();
        } else if (password.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Please Enter Password",
                    Toast.LENGTH_LONG).show();
        } else if (password.equals(password_confirmation)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, R.string.auth_failed,
                                        Toast.LENGTH_SHORT).show();
                            } else {

                                Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
        } else {
            Toast.makeText(SignUpActivity.this, "Please Enter matched password",
                    Toast.LENGTH_LONG).show();
        }
    }
}
