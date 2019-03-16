package mode1719.student.ju.jade;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
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
        setContentView(R.layout.activity_facebook);

        // Check if user is logged in already
        if (isLoggedIn()){
            goToMain();
        }

        // Login button
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("FbActivity / onSuccess");
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
    }

    @Override
    protected void onResume() {
        onLogout();
        System.out.println("FbActivity / onResume: " + isLoggedIn());
        Button forwardBtn = findViewById(R.id.forward_button);
        if (isLoggedIn()) {
            forwardBtn.setVisibility(View.VISIBLE);
        }
        forwardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLoggedIn()) { goToMain(); }
                else { makeToast("You need to sign in"); }
            }
        });
        super.onResume();
    }
    private void onLogout(){
        final Button forwardBtn = findViewById(R.id.forward_button);
        LoginButton loginBtn = findViewById(R.id.login_button);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLoggedIn()){
                    forwardBtn.setVisibility(View.GONE);
                }
            }
        });
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
        System.out.println("FbActivity / onActivityResult");
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }
}
