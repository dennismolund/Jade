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
        System.out.println("FbActivity / onCreate");
        super.onCreate(savedInstanceState);

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        // Check if user is logged in already
        if (isLoggedIn()){
            goToMain();
        }

        // Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("FbActivity / onSuccess" + loginResult);
                makeToast("Signing in");
                goToMain();
            }
            @Override
            public void onCancel() {
                makeToast("Log in cancelled");
            }

            @Override
            public void onError(FacebookException exception) {
                System.out.println(exception.toString());
                makeToast("Ops something went wrong, error message:  " + exception.toString());
            }
        });

        setContentView(R.layout.activity_facebook);

        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
                System.out.println("loginBtn clicked");
            }
        });
    }

    public void performLogin(){
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
                System.out.println("onResume / logOubtn");
                LoginManager.getInstance().logOut();
                setUpView();
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                System.out.println("onResume / logINbtn");
            }
        });
        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("OnResum / frwrdBtn");
                goToMain();
            }
        });

        super.onResume();
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
        System.out.println("FbActivity / goToMain");
        Intent intent = new Intent(FacebookActivity.this, MainActivity.class);
        startActivity(intent);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        System.out.println(AccessToken.getCurrentAccessToken());
        super.onActivityResult(requestCode, resultCode, data);
    }
}
