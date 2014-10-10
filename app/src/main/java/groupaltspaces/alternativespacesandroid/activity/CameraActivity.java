package groupaltspaces.alternativespacesandroid.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.Toast;

import groupaltspaces.alternativespacesandroid.R;
import groupaltspaces.alternativespacesandroid.camera.CameraPreview;

/**
 * Created by BrageEkroll on 10.10.2014.
 */
public class CameraActivity extends Activity {

    private Camera camera;
    private CameraPreview cameraPreview;
    private FrameLayout preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        setCamera();
        cameraPreview = new CameraPreview(this,camera);
        preview.addView(cameraPreview);
    }

    public static Intent createLaunchIntent(Context context){
        return new Intent(context, CameraActivity.class);
    }

    private int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Log.d("Error", "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    private void setCamera() {
        int cameraId = findFrontFacingCamera();
        if(cameraId>=0) {
            camera = Camera.open(cameraId);
            System.out.println(camera);
        }
        else{
            Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
        }

    }
}
