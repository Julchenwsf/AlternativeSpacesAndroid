package groupaltspaces.alternativespacesandroid.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;

import groupaltspaces.alternativespacesandroid.R;
import groupaltspaces.alternativespacesandroid.util.FileUtils;


public class LoginActivity extends Activity {

    private Button login;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        context = this;
    }
}
