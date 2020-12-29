package app.mustansar.maxontracking;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import app.mustansar.maxontracking.Models.getLocationModel;


public class SignupActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    String  entermail,  enterpassword, namestr;
    RotateLoading loading;

    EditText   EnterPassword, EnterMail, name_ET;
    Button signUpBtn;

    TextView alreadylogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference("Registeration");
        firebaseAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_signup);

        EnterMail = findViewById(R.id.Mail_ET);
        EnterPassword = findViewById(R.id.Password_ET);
        name_ET = findViewById(R.id.name_ET);

        loading = findViewById(R.id.loader);
        signUpBtn = findViewById(R.id.signup_btn);

        alreadylogin=findViewById(R.id.AlreadyLogin);
        alreadylogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getStrings();
                registerUser();

//                if (firstname.isEmpty()) {
//                    Firstname.setError("Enter First name");
//                    Firstname.requestFocus();
//                } else if (secondname.isEmpty()) {
//                    Secondname.setError("Enter second name");
//                    Secondname.requestFocus();
//                } else if (entermail.isEmpty()) {
//                    EnterMail.setError("Enter Mail");
//                    EnterMail.requestFocus();
//                } else if (enterpassword.isEmpty()) {
//                    EnterPassword.setError("Enter Password");
//                    EnterPassword.requestFocus();
//                } else if (enterconfirmpassword.isEmpty()) {
//                    ConfirmPassword.setError("Enter Confirm Password");
//                    ConfirmPassword.requestFocus();
//                } else if (enterphonenumber.isEmpty() ) {
//                    EnterPhoneNumber.setError("Enter Phone Number");
//                    EnterPhoneNumber.requestFocus();
//                }
//             else if (enterphonenumber.length()<11) {
//                EnterPhoneNumber.setError("Enter Valid Phone Number");
//                EnterPhoneNumber.requestFocus();
//            }
//                else if (enteraddress.isEmpty()) {
//                    EnterAddress.setError("Enter Address");
//                    EnterAddress.requestFocus();
//                } else {
//                    loading.setVisibility(View.VISIBLE);
//                    loading.start();
//                    registerUser();
//                }
            }
        });
    }

    private void registerUser() {
        loading.setVisibility(View.VISIBLE);
        loading.start();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

            if (entermail.matches(emailPattern)) {


                firebaseAuth.createUserWithEmailAndPassword(entermail, enterpassword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            loading.stop();
                            loading.setVisibility(View.GONE);

                        }

                        if (task.isSuccessful()) {
                            getLocationModel model=new getLocationModel(32.654564564, 72.3564576457, namestr);
                            databaseReference.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(model);
                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            loading.setVisibility(View.GONE);

                            //startActivity(new Intent(SignupActivity.this, MainActivity.class));

                        }

                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        loading.stop();
                    }
                });
            }
            else {
                EnterMail.setError("Enter Valid Email ");
                EnterMail.requestFocus();
                loading.stop();
            }
        }





    private void getStrings() {

        entermail = EnterMail.getText().toString().trim();
        enterpassword = EnterPassword.getText().toString();
        namestr = name_ET.getText().toString();

    }
}
