package mode1719.student.ju.jade;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.FaceDetector;
import android.media.MediaSync;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;


public class FacebookActivity extends AppCompatActivity {


    public CallbackManager callbackManager = CallbackManager.Factory.create();
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        // Check if user is logged in already
        if (isLoggedIn()){
            goToMain();
        }


        handleLoginResult();
        setContentView(R.layout.activity_facebook);

        // User was logged out when launching app
        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
    }

    public void handleLoginResult(){
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                goToMain();
            }
            @Override
            public void onCancel() {
                makeToast(getString(R.string.login_cancelled));
            }
            @Override
            public void onError(FacebookException exception) {
                makeToast(R.string.something_wrong + exception.toString());
            }
        });
    }

    public void performLogin(){
        makeToast(getString(R.string.signing_in));
        LoginManager.getInstance().logInWithReadPermissions(FacebookActivity.this, Arrays.asList("public_profile"));
    }

    @Override
    protected void onResume() {
        Button loginButton = findViewById(R.id.login_button);
        Button logoutButton = findViewById(R.id.logout_button);
        Button forwardButton = findViewById(R.id.forward_button);
        setUpView();

        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLogoutClicked();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoginClicked();
            }
        });
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMain();
            }
        });

        super.onResume();
    }
    public void onLogoutClicked(){
        new AlertDialog.Builder(FacebookActivity.this)
                .setTitle("Log out?")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton(
                        android.R.string.yes,
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int whichButton){
                                LoginManager.getInstance().logOut();
                                setUpView();
                            }
                        }
                ).setNegativeButton(
                android.R.string.no,
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){
                        makeToast("Logout cancelled");
                    }
                }
        ).show();

    }

    public void onLoginClicked(){
        new AlertDialog.Builder(FacebookActivity.this)
                .setTitle("Log in with Facebook?")
                .setMessage("This will grant Jade Application read permission to your' public facebook profile.")
                .setPositiveButton(
                        android.R.string.yes,
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int whichButton){
                                performLogin();
                            }
                        }
                ).setNegativeButton(
                android.R.string.no,
                new DialogInterface.OnClickListener(){
                    public void onClick(DialogInterface dialog, int whichButton){
                        makeToast("Login cancelled by user.");
                    }
                }
        ).show();
    }

    private void setUpView(){
        Button loginButton = findViewById(R.id.login_button);
        Button logoutButton = findViewById(R.id.logout_button);
        Button forwardButton = findViewById(R.id.forward_button);
        if(isLoggedIn()) {
            loginButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
            forwardButton.setVisibility(View.VISIBLE);
        }
        else {
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
            forwardButton.setVisibility(View.GONE);
        }
    }

    // Let user know if login is in progress
    private void makeToast(String message){
        Toast toast = Toast.makeText(FacebookActivity.this, message, Toast.LENGTH_LONG);
        toast.show();
    }

    protected boolean isLoggedIn(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        return isLoggedIn;
    }

    // Go to main activity
    private void goToMain(){
        Intent intent = new Intent(FacebookActivity.this, MainActivity.class);
        startActivity(intent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
