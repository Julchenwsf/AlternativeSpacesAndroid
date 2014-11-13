package groupaltspaces.alternativespacesandroid.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.net.Uri;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

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
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
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
        System.out.println(requestCode + " " + resultCode + " " + data);
        if(resultCode != RESULT_OK) return;

        Intent intent = new Intent(context, UploadActivity.class);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location == null) location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if(location == null) {
                Toast.makeText(this, "Could not acquire location", Toast.LENGTH_LONG).show();
                return;
            }

            setImageLocation(fileUri, location);
            intent.putExtra("imageURI", fileUri);
            intent.putExtra("deleteImage", true);
        } else if (requestCode == ACTIVITY_CHOOSE_FILE) {
            intent.putExtra("imageURI", Uri.parse("file://" + FileUtils.getPath(this, data.getData())));
            intent.putExtra("deleteImage", false);
        }
        startActivity(intent);
    }

    private void setImageLocation(Uri fileUri, Location location){
        try {
            ExifInterface exif = new ExifInterface(fileUri.getPath());
            double lat = location.getLatitude();
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, convertDoubleCoordinateToString(lat));
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, lat>0?"N":"S");

            double lon = location.getLongitude();
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, convertDoubleCoordinateToString(lon));
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, lon>0?"E":"W");
            exif.saveAttributes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String convertDoubleCoordinateToString(double coordinate) {
        double alat = Math.abs(coordinate);
        String dms = Location.convert(alat, Location.FORMAT_SECONDS);
        String[] splits = dms.split(":");
        String[] secnds = (splits[2]).split("\\.");
        String seconds;
        if(secnds.length==0) seconds = splits[2];
        else seconds = secnds[0];

        return splits[0] + "/1," + splits[1] + "/1," + seconds + "/1";
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
        if(id == R.id.logout){
            getSharedPreferences(getResources().getString(R.string.user_credentials),0).edit().clear().commit();
            startActivity(new Intent(context, LoginActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "AltSpaces");

        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + System.currentTimeMillis() + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + System.currentTimeMillis() + ".mp4");
        } else {
            return null;
        }

        try {
            if(!mediaFile.exists()) {
                mediaFile.getParentFile().mkdirs();
                mediaFile.createNewFile();
            }

        } catch (IOException e) {
            System.err.println("Could not create file.");
        }

        return mediaFile;
    }
}
