package groupaltspaces.alternativespacesandroid.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import groupaltspaces.alternativespacesandroid.R;
import groupaltspaces.alternativespacesandroid.camera.CameraPreview;

/**
 * Created by BrageEkroll on 10.10.2014.
 */
public class CameraActivity extends Activity {

    private Camera camera;
    private Button captureButton;
    private CameraPreview cameraPreview;
    private FrameLayout preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);
        bindViews();
        addButtonListener();
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        setCamera();
        camera.setDisplayOrientation(90);
        cameraPreview = new CameraPreview(this,camera);
        preview.addView(cameraPreview);
    }

    private void bindViews(){
        preview = (FrameLayout) findViewById(R.id.camera_preview);
        captureButton = (Button) findViewById(R.id.button_capture);
    }

    private void addButtonListener(){
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public static Intent createLaunchIntent(Context context){
        return new Intent(context, CameraActivity.class);
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        System.out.println(numberOfCameras);
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                Log.d("Error", "Camera found");
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    private void setCamera() {
        int cameraId = findBackFacingCamera();
        System.out.print(cameraId);
        if(cameraId>=0) {
            camera = Camera.open(cameraId);
            System.out.println(camera);
        } else {
            Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
        }

    }
}
