package meteor.android.todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import im.delight.android.ddp.MeteorCallback;
import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.ResultListener;

/**
 * Created by Sadmansamee on 2/3/16.
 */
public class LoginActivity extends AppCompatActivity implements MeteorCallback {


    EditText mEmail;
    EditText mPassword;
    App app;
    Button mLogin, registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        app = (App) this.getApplication();

        mEmail = (EditText) findViewById(R.id.mEmail);
        mPassword = (EditText) findViewById(R.id.mPassword);
        mLogin = (Button) findViewById(R.id.button);
        registerButton = (Button) findViewById(R.id.registerButton);

        //set Call back to this activity
        MeteorSingleton.getInstance().setCallback(this);

        mLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("mLogin", "onClick");
                login(mEmail.getText().toString(), mPassword.getText().toString());
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("mLogin", "onClick");
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }


    void login(final String email, final String password) {
        MeteorSingleton.getInstance().loginWithEmail(email, password, new ResultListener() {

            @Override
            public void onSuccess(String result) {

                Log.d("mLogin", "Successfully logged in");

                System.out.println("Successfully logged in: " + result);
                System.out.println("Is logged in: " + MeteorSingleton.getInstance().isLoggedIn());
                System.out.println("User ID: " + MeteorSingleton.getInstance().getUserId());

            }

            @Override
            public void onError(String error, String reason, String details) {
                Log.d("mLogin", "Could not log in: " + error + " / " + reason + " / " + details);

            }
        });
    }

    //UnSet callback
    @Override
    protected void onDestroy() {
        MeteorSingleton.getInstance().unsetCallback(this);
        super.onDestroy();
    }


    //MeteorCallback
    @Override
    public void onConnect(boolean b) {

    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onException(Exception e) {

    }

    @Override
    public void onDataAdded(String s, String s1, String s2) {

    }

    @Override
    public void onDataChanged(String s, String s1, String s2, String s3) {

    }

    @Override
    public void onDataRemoved(String s, String s1) {

    }


}
