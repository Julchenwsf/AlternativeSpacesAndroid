package groupaltspaces.alternativespacesandroid.activity;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.net.Uri;

import java.io.File;

import groupaltspaces.alternativespacesandroid.R;
import groupaltspaces.alternativespacesandroid.util.FileUtils;


public class MainActivity extends Activity {
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

    public static final int ACTIVITY_CHOOSE_FILE = 11;

    private Button takePhoto;
    private Button uploadPhoto;
    private Context context;
    private Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        bindViews();
        addListeners();
    }

    private void bindViews(){
        takePhoto = (Button) findViewById(R.id.takePhoto);
        uploadPhoto = (Button) findViewById(R.id.uploadPhoto);
    }

    private void addListeners(){
        takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });

        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                chooseFile.setType("image/*");
                Intent intent = Intent.createChooser(chooseFile, "Choose an image to upload");
                startActivityForResult(intent, ACTIVITY_CHOOSE_FILE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Intent intent = new Intent(context, UploadActivity.class);
            intent.putExtra("imageURI", fileUri);
            startActivity(intent);
        }
        if (requestCode == ACTIVITY_CHOOSE_FILE && resultCode == RESULT_OK) {
            Intent intent = new Intent(context, UploadActivity.class);
            intent.putExtra("imageURI", Uri.parse("file://" + FileUtils.getPath(this, data.getData())));
            startActivity(intent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstance) {
        savedInstance.putString("fileURI", fileUri == null ? "" : fileUri.getPath());

        super.onSaveInstanceState(savedInstance);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstance) {
        fileUri = Uri.parse("file://" + savedInstance.getString("fileURI"));
        super.onRestoreInstanceState(savedInstance);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        return id == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "AltSpaces");

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                System.out.println("MyCameraApp: failed to create directory");
                return null;
            }
        }


        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + System.currentTimeMillis() + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
}
