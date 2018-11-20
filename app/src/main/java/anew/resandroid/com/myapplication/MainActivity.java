package anew.resandroid.com.myapplication;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginButton loginButton;
    private String name;

    private String username;
    private String password;
    final int RC_SIGN_IN = 3;
    GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Context context = this;

        loginButton = (LoginButton) findViewById(R.id.login_button);

        loginButton.setReadPermissions(Arrays.asList(
                "public_profile", "email", "user_birthday", "user_friends"));

//       Button button = this.loadLogInButton();
//
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                goToLogin();
//            }
//
//        });


        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        AccessToken accessToken = loginResult.getAccessToken();
                        Profile profile = Profile.getCurrentProfile();

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.v("LoginActivity", response.toString());

                                        try {
                                            String email = object.getString("email");
                                            String birthday = object.getString("birthday");

                                            JSONObject responseGraphObject = response.getJSONObject();
                                            name = (String) responseGraphObject.get("name");

                                            Log.d("ERRORS", name);

                                        } catch (Exception e){
                                            Log.d("ERRORS", e.getMessage());
                                        }
                                        Log.d("ERRORS", response.toString());
                                    }
                                });

                        TextView textView = findViewById(R.id.welcome);
                        textView.setText( name);
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,name,email,gender,birthday");
                        request.setParameters(parameters);
                        request.executeAsync();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, signInOptions);



        final Button login = findViewById(R.id.skip_login_button);

        View.OnClickListener onClickLogin = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = getTextFromInput(R.id.edit_email);
                password = getTextFromInput(R.id.edit_pw);

                negotiateCredentials(username, password);
            }
        };

        login.setOnClickListener(onClickLogin);

        SignInButton signInButton = findViewById(R.id.sign_in_button);
        View.OnClickListener signInListener;
        signInButton.setOnClickListener(signInListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googleSignIn();
            }
        });

    }

    protected Button loadLogInButton() {
        Button button = findViewById(R.id.login_button);
        return button;
    }

    @Override
    protected void onStart(){
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
    }

    private void negotiateCredentials(String un, String pw)
    {
        goToSearch();
    }

    private void goToSearch()
    {
        Intent toSearch = new Intent(MainActivity.this, SearchActivity.class);
        startActivity(toSearch);

    }

    private String getTextFromInput(int id){
        String inputString = "";
        final EditText userIn = findViewById(id);
        if (userIn != null){inputString = userIn.getText().toString();}

        return inputString;
    }

    private void googleSignIn(){
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    private void handleSignIn(Task task) {
        try {
            GoogleSignInAccount account = (GoogleSignInAccount) task.getResult(ApiException.class);
        } catch (ApiException e) {
            e.printStackTrace();


        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


    Intent intent = getIntent();



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RC_SIGN_IN:
                if (requestCode == RC_SIGN_IN) {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                    handleSignIn(task);
                }
                break;
            default:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
        }
    }

        private final void goToLogin () {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

    protected  Bitmap getFacebookProfilePicture(String userID){

        Bitmap bitmap = null;
        try {
            URL imageURL = new URL("https://graph.facebook.com/" + userID + "/picture?type=large");
            bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
        } catch (Exception e){
            Log.v("ERRORS", e.getMessage());
        }

        return bitmap;
    }

//        Bitmap bitmap = getFacebookProfilePicture(userID);
    }

