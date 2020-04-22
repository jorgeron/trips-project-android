package us.master.entregable01;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    public static final String EMAIL_PARAM = "email_parameter";

    private AutoCompleteTextView login_email_et;
    private AutoCompleteTextView login_pass_et;
    private AutoCompleteTextView login_pass_confirmation_et;

    private TextInputLayout login_email;
    private TextInputLayout login_pass;
    private TextInputLayout login_pass_confirmation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        String emailParam = getIntent().getStringExtra(EMAIL_PARAM);

        login_email_et = findViewById(R.id.login_email_et);
        login_pass_et = findViewById(R.id.login_pass_et);
        login_pass_confirmation_et = findViewById(R.id.login_pass_confirmation_et);

        login_email = findViewById(R.id.login_email);
        login_pass = findViewById(R.id.login_pass);
        login_pass_confirmation = findViewById(R.id.login_pass_confirmation);

        login_email_et.setText(emailParam.toString()); //Char secuence o dejar a pelo
        findViewById(R.id.signup_button_mail).setOnClickListener(l -> {
            if (login_email_et.getText().length() == 0) {
                login_email.setErrorEnabled(true);
                login_email.setError(getString(R.string.signup_error_user));
            } else if (login_pass_et.getText().length() == 0) {
                login_pass.setErrorEnabled(true);
                login_pass.setError(getString(R.string.signup_error_pass));
            } else if (login_pass_confirmation_et.getText().length() == 0) {
                login_pass_confirmation.setErrorEnabled(true);
                login_pass_confirmation.setError(getString(R.string.signup_error_pass));
            } else if (!login_pass_confirmation_et.getText().toString().equals(login_email_et.getText().toString())) {
                login_pass.setErrorEnabled(true);
                login_pass.setError(getString(R.string.signup_error_pass_not_match));
            } else {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(login_email_et.getText().toString(), login_pass_et.getText().toString()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, R.string.signup_created, Toast.LENGTH_LONG).show();
                        RegisterActivity.this.finish();
                    } else {
                        Toast.makeText(this, R.string.signup_created_error, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });


    }
}
