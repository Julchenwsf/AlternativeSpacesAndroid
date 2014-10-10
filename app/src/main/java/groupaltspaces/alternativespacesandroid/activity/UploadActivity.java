package groupaltspaces.alternativespacesandroid.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import groupaltspaces.alternativespacesandroid.R;

/**
 * Created by BrageEkroll on 10.10.2014.
 */
public class UploadActivity extends Activity {

    private ImageView image;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bitmap bm = (Bitmap) getIntent().getExtras().get("image");
        setContentView(R.layout.upload_form);
        bindViews();
        image.setImageBitmap(bm);
    }

    private void bindViews(){
        image = (ImageView) findViewById(R.id.image);
    }
}
