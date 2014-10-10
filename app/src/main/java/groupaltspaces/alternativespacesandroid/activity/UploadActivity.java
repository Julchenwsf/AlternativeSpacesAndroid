package groupaltspaces.alternativespacesandroid.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;

import groupaltspaces.alternativespacesandroid.R;
import groupaltspaces.alternativespacesandroid.tasks.UploadTask;

/**
 * Created by BrageEkroll on 10.10.2014.
 */
public class UploadActivity extends Activity {

    private ImageView image;
    private Button button;
    private EditText title;
    private EditText interests;
    private EditText description;
    private File imageFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bitmap bm = (Bitmap) getIntent().getExtras().get("image");
        Uri uri = (Uri) getIntent().getExtras().get("URI");
        imageFile = new File(uri.getPath());
        setContentView(R.layout.upload_form);
        bindViews();
        addButtonListener();
        image.setImageBitmap(bm);
    }

    private void bindViews(){
        image = (ImageView) findViewById(R.id.image);
        button = (Button) findViewById(R.id.upload);
        title = (EditText) findViewById(R.id.title);
        interests = (EditText) findViewById(R.id.tags);
        description = (EditText) findViewById(R.id.description);
    }

    private void addButtonListener(){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UploadTask uploadTask = new UploadTask(title.getText().toString(), interests.getText().toString(), description.getText().toString(), imageFile);
                uploadTask.execute();
            }
        });
    }
}
