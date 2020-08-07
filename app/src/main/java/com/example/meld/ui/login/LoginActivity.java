package com.example.emojo.ui.login;

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

import com.example.meld.R;
import com.example.meld.models.IUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {


    private ActionCodeSettings actionCodeSettings;
    private String userEmail;
    private String username;
    private IUser user;
    SharedPreferences.Editor editor;
    private FirebaseAuth firebaseAuth;
    private Boolean isLoggedIn = false;
    private String token;
    private Boolean doesUserExist = false;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();




//    public static class LoginFragment extends Fragment {
//
//    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_login);


        final EditText usernameEditText = findViewById(R.id.username);
//        final EditText passwordEditText = findViewById(R.id.password);


        final Button loginButton = findViewById(R.id.login);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);


//



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
                loginButton.setEnabled(usernameEditText.getText().toString().replaceAll("\\s+","").length() > 0);

            }
        };


        usernameEditText.addTextChangedListener(afterTextChangedListener);



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);

                userEmail = usernameEditText.getText().toString();

                if (!userEmail.contains("@")) {
                    username = userEmail;
                    userEmail = userEmail.concat("@emojo.com");
                }

                else {
                    username = userEmail.substring(0, userEmail.indexOf('@'));
                }

                String dummyPassword = "password1234";


                signInOrSignUp(userEmail, dummyPassword);




//                loginViewModel.login(usernameEditText.getText().toString(),
//                        passwordEditText.getText().toString());
            }
        });

        Log.v("!!!!logged", Boolean.toString(isLoggedIn));

        if (isLoggedIn) {
            startLoginFlow();
        }


    }

    private void startLoginFlow() {
//        Intent intent = new Intent(LoginActivity.this, LoggedInActivity.class);
//        intent.putExtra("username", firebaseAuth.getCurrentUser().getEmail());
//        startActivity(intent);
//        finish();
    }

    private void signUp(String userEmail, String dummyPassword) {

        firebaseAuth.createUserWithEmailAndPassword(userEmail, dummyPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // sign in success
                            isLoggedIn = true;
                            addUserToDatabase();
                            startLoginFlow();
                        } else {
                            task.getException().printStackTrace();
                        }

                    }
                });

    }

    private void signIn(final String userEmail, final String dummyPassword) {
//        final User user = new User(username, userEmail);
//
//        firebaseAuth.signInWithEmailAndPassword(userEmail, dummyPassword)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//
//                        if (task.isSuccessful()) {
//                            // Sign in success, update UI with the signed-in user's information
//                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
//                            isLoggedIn = true;
//                            startLoginFlow();
//                        }
//
//                        else {
//                            task.getException().printStackTrace();
//                        }
//                    }
////                            Toast.makeText(getApplicationContext(), "Error signing in! Please try again.", Toast.LENGTH_SHORT).show();
////                            task.getException().printStackTrace();
//                });

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putCharSequence("userEmail", this.userEmail);
        Log.v("SAVED", "SAVE CALLED!");

    }


//    @Override
//    public void onRestoreInstanceState(Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        Log.v("ORS", "ORS CALLED!");
////        this.userEmail = (String)savedInstanceState.getCharSequence("userEmail");
//        Log.v("restored email", this.userEmail);
//
//
//    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null) {
            isLoggedIn = true;
            Log.v("currentuser", currentUser.getEmail());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v("pause", "pause CALLED!");
        final EditText usernameEditText = findViewById(R.id.username);
        this.userEmail = usernameEditText.getText().toString();
    }

    @Override
    public void onStop() {
        super.onStop();
    }


    @Override
    public void onResume() {
        super.onResume();

        Log.v("name", Boolean.toString(userEmail != null));
//        Log.v("usrobj", Boolean.toString(userModelObject != null));
//
//        if(userModelObject != null) {
//            Log.v("email from User obj", userModelObject.getEmail());
//        }


        FirebaseAuth auth = FirebaseAuth.getInstance();
        Intent intent = getIntent();

        if (intent.getData() != null) {

            SharedPreferences prefs = getSharedPreferences("LoginAct", MODE_PRIVATE);
            userEmail = prefs.getString("userEmail", null);

            String emailLink = intent.getData().toString();
            Log.v("link: ", emailLink);
//            Log.v("userEmail: ", userEmail);



            if (auth.isSignInWithEmailLink(emailLink)) {
                Log.v("email268: ", this.userEmail);

//                if (savedInstanceState != null) {
//                    Log.v("!!!", "IHATETHIS!!!!!");
//                    this.userEmail = (String) savedInstanceState.getCharSequence("userEmail");



//                    auth.signInWithEmailLink(userEmail, emailLink)
//                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                                @Override
//                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                    if (task.isSuccessful()) {
//                                        Toast.makeText(getApplicationContext(), "Successfully signed in!", Toast.LENGTH_SHORT).show();
//
//                                        AuthResult result = task.getResult();
//
//                                        User user = new User(userEmail);
//
//                                        Intent intent = new Intent(LoginActivity.this, LoggedInActivity.class);
//                                        intent.putExtra("authResult", result);
//                                        intent.putExtra("userEmail", user.getEmail());
//
//                                        startActivity(intent);
//
//                                    } else {
//                                        task.getException().printStackTrace();
//                                        Toast.makeText(getApplicationContext(), "Error logging in :( !", Toast.LENGTH_SHORT).show();
//                                    }
//                                }
//                            });

            }
        }

        Log.v("resume", "resume CALLED!");
        Log.v("email: ", Boolean.toString(this.userEmail == null));

    }

    private void addUserToDatabase() {

        Map<String, Object> userMap = new HashMap<>();

        userMap.put("username", username);
        userMap.put("email", userEmail);

        db.collection("users").document().set(userMap).addOnSuccessListener(
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("DBOps", "user successfully written to DB!");
                        fetchNewUserToken();
                    }
                }
        ).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w("DBOps", "Error writing user to DB! :(", e);
            }
        });

    }

    private void signInOrSignUp(String username, String dummyPassword) {
        Query q = db.collection("users")
                .whereEqualTo(
                        "username",
                        username);

        q.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                          @Override
                                          public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                              if (task.isSuccessful()) {
                                                  for (QueryDocumentSnapshot document : task.getResult()) {
                                                      if(document.getData().containsKey("username")) {
                                                          doesUserExist = document
                                                                  .getData()
                                                                  .get("username")
                                                                  .equals(username);

                                                          if(doesUserExist) {
                                                              break;
                                                          }

                                                          Log.v("FindUserDB", "Found user in DB");
                                                      }
                                                  }

//                          if(doesUserExist) {
                                                  signIn(userEmail, dummyPassword);
//                          }

//                          else {
//                              signUp(userEmail, dummyPassword);
//                          }
                                              }

                                              else {
                                                  task.getException().printStackTrace();
                                              }
                                          }
                                      }


        );
    }

//
//    public void fetchNewUserToken() {
//        FirebaseInstanceId.getInstance().getInstanceId()
//                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
//                        if (task.isSuccessful()) {
//                            token = task.getResult().getToken();
//
//                            Map<String, Object> tokenMap = new HashMap<>();
//
//                            tokenMap.put("token", token);
//                            tokenMap.put("user", firebaseAuth.getCurrentUser().getEmail());
//
//
//                            db.collection("tokens").document().set(tokenMap)
//                                    .addOnSuccessListener(
//                                            new OnSuccessListener<Void>() {
//                                                @Override
//                                                public void onSuccess(Void aVoid) {
//                                                    Log.d("DBOps", "token successfully written to DB!");
//                                                }
//                                            }
//                                    ).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//                                    e.printStackTrace();
//                                }
//                            });
//
//
//
//                        }
//                    }
//                });

//    }



//    private void updateUiWithUser(LoggedInUserView model) {
//        String welcome = getString(R.string.welcome) + model.getDisplayName();
//        // TODO : initiate successful logged in experience
//        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
//    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }
}
