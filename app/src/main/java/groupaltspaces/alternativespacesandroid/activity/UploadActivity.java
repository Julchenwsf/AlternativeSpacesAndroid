package groupaltspaces.alternativespacesandroid.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tokenautocomplete.TokenCompleteTextView;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import groupaltspaces.alternativespacesandroid.R;
import groupaltspaces.alternativespacesandroid.dialogs.CustomDialog;
import groupaltspaces.alternativespacesandroid.tasks.Callback;
import groupaltspaces.alternativespacesandroid.tasks.InterestCallback;
import groupaltspaces.alternativespacesandroid.tasks.InterestTask;
import groupaltspaces.alternativespacesandroid.tasks.UploadTask;
import groupaltspaces.alternativespacesandroid.util.Interest;
import groupaltspaces.alternativespacesandroid.util.InterestCompleteTextView;

public class UploadActivity extends Activity implements Callback, InterestCallback {
    private ImageView image;
    private Button button;
    private EditText title;
    private InterestCompleteTextView interests;
    private EditText description;
    private File imageFile;
    private Dialog dialog;
    private Callback context;
    private InterestCallback interestCallback;
    private static Interest[] interestList = new Interest[0];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        interestCallback = this;
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
        addInterestListener();
        setUpAdapter();
        image.setImageBitmap(bm);
    }

    private void addInterestListener(){
        interests.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                System.out.println(editable);
                if(editable.toString().length()<2){
                    return;
                }
                InterestTask interestTask = new InterestTask(interestCallback);
                String searchString = editable.toString().replaceAll(",","");
                searchString.replaceAll(" ", "");
                System.out.println(searchString);
                interestTask.execute(searchString);
            }
        });
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
        interests.setTokenClickStyle(TokenCompleteTextView.TokenClickStyle.Delete);
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

    @Override
    public void onInterestReceived(List<Map<String, Object>> maps) {
        interestList = new Interest[maps.size()];
        for(int i= 0; i<maps.size();i++){
            interestList[i] = new Interest((String)maps.get(i).get("interest_id"),(String)maps.get(i).get("interest_name"));
        }
        for(Interest interest : interestList){
            System.out.println(interest.getName());
        }
        setUpAdapter();

    }


}
