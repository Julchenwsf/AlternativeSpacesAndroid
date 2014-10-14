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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import groupaltspaces.alternativespacesandroid.R;
import groupaltspaces.alternativespacesandroid.dialogs.CustomDialog;
import groupaltspaces.alternativespacesandroid.tasks.Callback;
import groupaltspaces.alternativespacesandroid.tasks.UploadTask;
import groupaltspaces.alternativespacesandroid.util.Interest;
import groupaltspaces.alternativespacesandroid.util.InterestCompleteTextView;

public class UploadActivity extends Activity implements Callback {
    private ImageView image;
    private Button button;
    private EditText title;
    private InterestCompleteTextView interests;
    private EditText description;
    private File imageFile;
    private Dialog dialog;
    private Callback context;
    private static final Interest[] interestList = new Interest[]{new Interest(1000, "football"), new Interest(3000, "Basketball")};

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
        setUpAdapter();
        image.setImageBitmap(bm);
    }

    private void bindViews(){
        image = (ImageView) findViewById(R.id.image);
        button = (Button) findViewById(R.id.upload);
        title = (EditText) findViewById(R.id.title);
        interests = (InterestCompleteTextView) findViewById(R.id.tags);
        description = (EditText) findViewById(R.id.description);

    }

    private void setUpAdapter(){
        ArrayAdapter<Interest> arrayAdapter = new ArrayAdapter<Interest>(this, android.R.layout.simple_list_item_1,interestList);
        interests.setAdapter(arrayAdapter);
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
        Toast.makeText(getApplicationContext(), "Image uploaded", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onFail(List<String> messages) {
        dialog = new CustomDialog(this);
        final TextView dialogTitle = (TextView) dialog.findViewById(R.id.dialog_title);
        LinearLayout descriptionLayout = (LinearLayout) dialog.findViewById(R.id.description_layout);
        Button button = (Button) dialog.findViewById(R.id.dismissButton);
        dialogTitle.setText("Status");
        for(String message : messages){
            TextView description = new TextView(this);
            description.setText(" - " + message);
            descriptionLayout.addView(description);
        }
        dialog.show();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }
}
