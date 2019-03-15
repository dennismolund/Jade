package mode1719.student.ju.jade;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    private User mUser = new User();

    public CallbackManager callbackManager = CallbackManager.Factory.create();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);

        // Check if user is logged in already
        checkLoginStatus();

        // Login button
        LoginButton loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email", "public_profile"));

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                System.out.println("FbActivity / onSuccess");
                fetchData();
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

    // Let user know if login is in progress
    private void makeToast(String message){
        Toast toast = Toast.makeText(FacebookActivity.this, message, Toast.LENGTH_LONG);
        toast.show();
    }

    protected void checkLoginStatus(){
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if(isLoggedIn){
            fetchData();
            goToMain();
        }
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

    // Fetch facebook data
    private void fetchData(){
        System.out.println("FbActivity / fetchData");
        Profile profile = Profile.getCurrentProfile();
        mUser.setuID(profile.getId());
        mUser.setuName(profile.getName());
    }



}
