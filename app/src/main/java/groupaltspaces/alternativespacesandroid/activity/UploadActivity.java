package groupaltspaces.alternativespacesandroid.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.List;

import groupaltspaces.alternativespacesandroid.R;
import groupaltspaces.alternativespacesandroid.dialogs.CustomDialog;
import groupaltspaces.alternativespacesandroid.tasks.Callback;
import groupaltspaces.alternativespacesandroid.tasks.UploadTask;

public class UploadActivity extends Activity implements Callback {
    private ImageView image;
    private Button button;
    private EditText title;
    private EditText interests;
    private EditText description;
    private File imageFile;
    private Dialog dialog;
    private Callback context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        Bitmap bm = null;
        Uri uri = (Uri) getIntent().getExtras().get("imageURI");
        imageFile = new File(uri.getPath());
        try {
            bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
                try {
                    ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
                    String lat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
                    String lng =   exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
                    System.out.println("Found location: " + lat + " " + lng);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                UploadTask uploadTask = new UploadTask(title.getText().toString(), interests.getText().toString(), description.getText().toString(), imageFile, context);
                uploadTask.execute();
            }
        });
    }

    @Override
    public void onSuccess() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog);
        dialog.setTitle("Status");
        description.setText("Image uploaded");
        dialog.show();
    }

    @Override
    public void onFail(List<String> messages) {
        dialog = new CustomDialog(this);
        LinearLayout descriptionLayout = (LinearLayout) dialog.findViewById(R.id.description_layout);
        dialog.setTitle("Status");
        for(String message : messages){
            TextView description = new TextView(this);
            description.setText(" - " + message);
            descriptionLayout.addView(description);
        }
        dialog.show();
    }
}
