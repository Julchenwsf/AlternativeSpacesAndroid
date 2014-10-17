package groupaltspaces.alternativespacesandroid.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import groupaltspaces.alternativespacesandroid.R;

public class StartupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkOfflineCredentials();
    }

    private void checkOfflineCredentials() {
        SharedPreferences sharedPreferences = getSharedPreferences(getResources().getString(R.string.user_credentials), 0);
        if (sharedPreferences.getString(getResources().getString(R.string.credentials_username), null) != null && sharedPreferences.getString(getResources().getString(R.string.credentials_password), null) != null) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish();
    }
}
