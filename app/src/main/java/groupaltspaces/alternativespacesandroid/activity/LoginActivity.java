package groupaltspaces.alternativespacesandroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import groupaltspaces.alternativespacesandroid.R;
import groupaltspaces.alternativespacesandroid.tasks.Callback;
import groupaltspaces.alternativespacesandroid.tasks.LoginTask;

public class LoginActivity extends Activity implements Callback {
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


    private void saveUserCredentials(){
        String username = this.username.getText().toString();
        String password = this.password.getText().toString();
        try {
            password = sha1(sha1(password));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.user_credentials),0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();
    }

    private String sha1(String input) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("SHA1");
        byte[] result = mDigest.digest(input.getBytes());
        StringBuilder sb = new StringBuilder();
        for (byte aResult : result) {
            sb.append(Integer.toString((aResult & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    @Override
    public void onSuccess() {
        saveUserCredentials();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onFail(List<String> message) {
        Toast.makeText(getApplicationContext(), message.get(0), Toast.LENGTH_SHORT).show();
    }
}
