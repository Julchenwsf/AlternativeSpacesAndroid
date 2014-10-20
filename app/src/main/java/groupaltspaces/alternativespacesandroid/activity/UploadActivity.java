package groupaltspaces.alternativespacesandroid.activity;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
    private UploadActivity activity;
    private InterestCallback interestCallback;
    private LinearLayout uploadLayout;
    private static Interest[] interestList = new Interest[0];

    private boolean deleteImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        interestCallback = this;
        Bitmap bm = null;
        Uri uri = (Uri) getIntent().getExtras().get("imageURI");
        imageFile = new File(uri.getPath());

        deleteImg = getIntent().getExtras().getBoolean("deleteImage");

        try {
            bm = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.upload_form);
        bindViews();
        addButtonListener();
        addInterestListener();
        addLayoutListener();
        setUpAdapter();
        image.setImageBitmap(bm);
    }

    private void addLayoutListener(){
        uploadLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                return false;
            }
        });
    }

    private void addInterestListener(){
        interests.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                String searchString = charSequence.toString().replaceAll(",", "").replaceAll(" ", "");

                InterestTask interestTask = new InterestTask(interestCallback);
                interestTask.execute(searchString);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void bindViews(){
        image = (ImageView) findViewById(R.id.image);
        button = (Button) findViewById(R.id.upload);
        title = (EditText) findViewById(R.id.title);
        interests = (InterestCompleteTextView) findViewById(R.id.tags);
        description = (EditText) findViewById(R.id.description);
        uploadLayout = (LinearLayout) findViewById(R.id.upload_layout);
    }

    private void setUpAdapter(){
        ArrayAdapter<Interest> arrayAdapter = new ArrayAdapter<Interest>(this, android.R.layout.simple_list_item_1, interestList);
        interests.setAdapter(arrayAdapter);
        interests.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                interests.addObject(adapterView.getItemAtPosition(i));

                for(Object object : interests.getObjects()) {
                    System.out.println(((Interest) object).getName());
                }
            }
        });
    }

    private void addButtonListener(){
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String interestsString = "";
                for (Object interest : interests.getObjects()) interestsString += " " + ((Interest) interest).getId();
                if(interestsString.length() > 1) interestsString = interestsString.substring(1);

                UploadTask uploadTask = new UploadTask(title.getText().toString(), interestsString, description.getText().toString(), imageFile, activity);
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
        List<Interest> duplicateFiltered = new ArrayList<Interest>();
        for (Map<String, Object> map : maps) {
            Interest temp = new Interest((String) map.get("interest_id"), (String) map.get("interest_name"));
            duplicateFiltered.add(temp);
        }

        interestList = duplicateFiltered.toArray(new Interest[duplicateFiltered.size()]);
        setUpAdapter();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(deleteImg) {
            imageFile.delete();
        }
    }
}
