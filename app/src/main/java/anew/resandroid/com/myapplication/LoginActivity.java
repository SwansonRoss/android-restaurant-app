package anew.resandroid.com.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {
    private String username;
    private String password;
    final int RC_SIGN_IN = 3;
    GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignInOptions signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, signInOptions);



        final Button login = findViewById(R.id.login_button);

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
        Intent toSearch = new Intent(LoginActivity.this, SearchActivity.class);
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

    @Override
            public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignIn(task);
        }
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
}
