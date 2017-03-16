package ps.source.hazard.hazard.profile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ps.source.hazard.hazard.R;
import ps.source.hazard.hazard.model.User;

public class ProfileActivity extends AppCompatActivity {
    private TextView email, showEmail, showName, firstName, lastName, mobile;
    RadioGroup radioGroup;
    private String gender;
    private String uid;
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressDialog = new ProgressDialog(this);

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("profile");

        showEmail = (TextView) findViewById(R.id.show_email);
        showName = (TextView) findViewById(R.id.show_name);

        email = (TextView) findViewById(R.id.email);
        mobile = (TextView) findViewById(R.id.mobile);
        firstName = (TextView) findViewById(R.id.first_name);
        lastName = (TextView) findViewById(R.id.last_name);
        radioGroup = (RadioGroup) findViewById(R.id.gender);

        progressDialog.setMessage("Fetching Data ...");
        progressDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {

            String name = user.getDisplayName();
            Uri photoUrl = user.getPhotoUrl();
            uid = user.getUid();
            initAddValueEventListener(uid);

        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                User user = new User();
                user.setEmail(email.getText().toString());
                user.setFirstName(firstName.getText().toString());
                user.setLastName(lastName.getText().toString());
                user.setMobile(mobile.getText().toString());
                user.setGender(gender);
                myRef.child(uid).setValue(user);

                Snackbar.make(view, "Your Profile Has Been Updated", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:

                Intent intent = new Intent(this, ProfileActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_sign_out:
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    private void initAddValueEventListener(String uid) {

        // Read from the database
        myRef.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                updateUI(user);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
            }
        });
    }

    private void updateUI(User user) {

        email.setText(user.getEmail());
        mobile.setText(user.getMobile());
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        showEmail.setText(user.getEmail());
        showName.setText(user.getFirstName());
        gender = user.getGender();
        if(user.getGender().equals("male")){
            radioGroup.check(R.id.male);
        }else if(user.getGender().equals("female")){
            radioGroup.check(R.id.female);
        }

        progressDialog.dismiss();


    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.male:
                if (checked)
                    gender = "male";
                    break;
            case R.id.female:
                if (checked)
                    gender = "female";
                    break;
        }
    }
}
