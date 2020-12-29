package app.mustansar.maxontracking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.victor.loading.rotate.RotateLoading;

public class LoginActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    String entermail, enterpassword;
    RotateLoading loading;

    EditText Firstname, Secondname, EnterPassword, ConfirmPassword, EnterAddress, EnterPhoneNumber, EnterMail;
    Button signUpBtn;
    private LinearLayout mRootView;
    RadioGroup Gendersignup;
    RadioButton SelectRB;
    TextView alreadylogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference("Registeration");
        firebaseAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_login);
        mRootView = findViewById(R.id.my_root);

        EnterMail = findViewById(R.id.Mail_ET);
        EnterPassword = findViewById(R.id.Password_ET);
        loading = findViewById(R.id.loader);
        signUpBtn = findViewById(R.id.signup_btn);
        alreadylogin = findViewById(R.id.AlreadyLogin);
        alreadylogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getStrings();
                registerUser();

            }
        });
    }

    private void registerUser() {
        loading.setVisibility(View.VISIBLE);
        loading.start();

        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


        if (entermail.matches(emailPattern)) {


            firebaseAuth.signInWithEmailAndPassword(entermail, enterpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        loading.stop();
                        loading.setVisibility(View.GONE);
                    }

                    if (task.isSuccessful()) {
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));

                        //startActivity(new Intent(SignupActivity.this, MainActivity.class));

                    }

                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    loading.stop();
                }
            });
        } else {
            EnterMail.setError("Enter Valid Email ");
            EnterMail.requestFocus();
            loading.stop();
        }

    }


    private void getStrings() {

        entermail = EnterMail.getText().toString().trim();
        enterpassword = EnterPassword.getText().toString();

    }
}
