package groupaltspaces.alternativespacesandroid.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import groupaltspaces.alternativespacesandroid.R;

public class LoginActivity extends Activity {
    private EditText username;
    private EditText password;
    private Button login;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;

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
                System.out.println(username.getText());
                System.out.println(password.getText());
            }
        });
    }
}
