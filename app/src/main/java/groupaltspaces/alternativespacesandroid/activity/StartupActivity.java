package groupaltspaces.alternativespacesandroid.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.Toast;

import groupaltspaces.alternativespacesandroid.R;

public class StartupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(isInternetAvailable()) checkOfflineCredentials();
        else {
            Toast.makeText(this, "Could not reach server", Toast.LENGTH_LONG).show();
            finish();
        }
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

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }
}
