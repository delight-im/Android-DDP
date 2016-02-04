package meteor.android.todo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import im.delight.android.ddp.MeteorSingleton;
import im.delight.android.ddp.ResultListener;

/**
 * Created by Sadmansamee on 2/3/16.
 */
public class RegisterActivity extends AppCompatActivity {

    EditText mEmailRegister, mPasswordRegister, mPasswordAgainRegister, mName;
    Button register;
    TextView errorTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mEmailRegister = (EditText) findViewById(R.id.mEmailRegister);
        mPasswordRegister = (EditText) findViewById(R.id.mPasswordRegister);
        mPasswordAgainRegister = (EditText) findViewById(R.id.mPasswordAgainRegister);
        mName = (EditText) findViewById(R.id.mName);

        register = (Button) findViewById(R.id.register);
        errorTextView = (TextView) findViewById(R.id.errorTextView);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(mName.getText().toString(), mPasswordRegister.getText().toString(), mPasswordRegister.getText().toString());
            }
        });

    }

    void register(String name, String email, String password) {
        MeteorSingleton.getInstance().registerAndLogin(name, email, password, new ResultListener() {
            @Override
            public void onSuccess(String s) {
                Log.d("RegisterActivity", "onSuccess");
                Intent intent = new Intent(RegisterActivity.this, TodoListActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onError(String s, String s1, String s2) {
                errorTextView.setText("Error occured");
                Log.d("RegisterActivity", s + " " + s1 + " " + s2);
            }
        });
    }

}
