package com.example.meld.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Printer;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.meld.MainActivity;
import com.example.meld.R;
import com.example.meld.models.IUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    private ActionCodeSettings actionCodeSettings;
    private IUser user;

    private String userEmail;
    private String username;

    private EditText usernameEditText;
    private EditText passwordEditText;

    SharedPreferences.Editor editor;
    private FirebaseAuth firebaseAuth;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();




//    public static class LoginFragment extends Fragment {
//
//    }

    public Boolean isEmailValid(String userEmail) {
        return userEmail.replaceAll("\\s+","").length() > 0;
    }

    public Boolean isPasswordPresent(String password) {
        return password.length() > 0;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);


        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            goToHomeScreen();
        }



        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);


        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);



        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
//                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
//                        passwordEditText.getText().toString());
                loginButton
                        .setEnabled(
                                isEmailValid(usernameEditText.getText().toString())
                                        && isPasswordPresent(passwordEditText.getText().toString())
                        );
            }
        };

        usernameEditText.addTextChangedListener(afterTextChangedListener);



        TextWatcher paswordTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
//                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
//                        passwordEditText.getText().toString());
                loginButton
                        .setEnabled(
                                isEmailValid(usernameEditText.getText().toString())
                                        && isPasswordPresent(passwordEditText.getText().toString())
                        );
            }
        };

        passwordEditText.addTextChangedListener(paswordTextChangedListener);



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);

                userEmail = usernameEditText.getText().toString();

                if (!userEmail.contains("@")) {
                    username = userEmail;
                    userEmail = userEmail.concat("@meld.io");
                }

                else {
                    username = userEmail.substring(0, userEmail.indexOf('@'));
                }

                String password = passwordEditText.getText().toString();

                signInOrSignUp(userEmail, password);

            }
        });

    }

    private void goToHomeScreen() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putCharSequence("userEmail", this.userEmail);
        Log.v("SAVED", "SAVE CALLED!");

    }



//    @Override
//    public void onResume() {
//        super.onResume();
//
//        Log.v("name", Boolean.toString(userEmail != null));
////        Log.v("usrobj", Boolean.toString(userModelObject != null));
////
////        if(userModelObject != null) {
////            Log.v("email from User obj", userModelObject.getEmail());
////        }
//
//
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        Intent intent = getIntent();
//
//        if (intent.getData() != null) {
//
//            SharedPreferences prefs = getSharedPreferences("LoginAct", MODE_PRIVATE);
//            userEmail = prefs.getString("userEmail", null);
//
//            String emailLink = intent.getData().toString();
//            Log.v("link: ", emailLink);
////            Log.v("userEmail: ", userEmail);
//
//
//
//            if (auth.isSignInWithEmailLink(emailLink)) {
//                Log.v("email268: ", this.userEmail);
//
//
//            }
//        }
//
//        Log.v("resume", "resume CALLED!");
//        Log.v("email: ", Boolean.toString(this.userEmail == null));
//
//    }

    private void addUserToDatabase() {

        Map<String, Object> userMap = new HashMap<>();

        userMap.put("username", username.toLowerCase());
        userMap.put("email", userEmail.toLowerCase());

        db.collection("users").document().set(userMap).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DBOps", "user successfully written to DB!");
//                        fetchNewUserToken();
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("DBOps", "Error writing user to DB! :(", e);
            }
        });

    }


    private void signUp(String userEmail, String password) {

        firebaseAuth.createUserWithEmailAndPassword(userEmail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // sign in success
                            addUserToDatabase();
                            goToHomeScreen();
                        }
                    }
                });

    }


    private void signInOrSignUp(String userEmail, String password) {

        firebaseAuth.signInWithEmailAndPassword(userEmail, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {

                                    goToHomeScreen();
                                }


                                else {

                                    try {
                                        throw task.getException();
                                    }

                                    catch (FirebaseAuthInvalidCredentialsException e) {
//
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "Incorrect email and/or password! Please try again.",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        clearLoginForm();


                                    }

                                    catch (FirebaseAuthInvalidUserException e) {
                                        signUp(userEmail, password);

                                    }


                                    catch (Exception e) {
                                        e.getStackTrace();
                                        Toast.makeText(getApplicationContext(),
                                                "Error signing in! Please try again.",
                                                Toast.LENGTH_SHORT).show();


                                    }
                                }
                            }
                        }
                );
    }

    private void clearLoginForm() {
        this.passwordEditText.setText("");
        this.usernameEditText.setText("");
    }



//    private void updateUiWithUser(LoggedInUserView model) {
//        String welcome = getString(R.string.welcome) + model.getDisplayName();
//        // TODO : initiate successful logged in experience
//        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
//    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
