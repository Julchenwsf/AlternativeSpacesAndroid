package groupaltspaces.alternativespacesandroid.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import groupaltspaces.alternativespacesandroid.R;
import groupaltspaces.alternativespacesandroid.tasks.LoginTask;

public class LoginActivity extends Activity {
    private EditText username;
    private EditText password;
    private Button login;
    private LoginActivity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        activity = this;

        bindViews();
        addButtonListener();
    }

    private void bindViews(){
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        login = (Button) findViewById(R.id.login);
    }

    private void addButtonListener(){
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginTask loginTask = new LoginTask(username.getText().toString(), password.getText().toString(),activity);
                loginTask.execute();
                System.out.println(username.getText());
                System.out.println(password.getText());
            }
        });
    }

    public void onLoginSuccess(){
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);

    }

    public void onLoginFail(){
            Toast.makeText(getApplicationContext(), "Unable to log in", Toast.LENGTH_SHORT).show();
    }
}
