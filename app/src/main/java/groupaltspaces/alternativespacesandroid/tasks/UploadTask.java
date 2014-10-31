package groupaltspaces.alternativespacesandroid.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import groupaltspaces.alternativespacesandroid.R;
import groupaltspaces.alternativespacesandroid.activity.UploadActivity;
import groupaltspaces.alternativespacesandroid.util.MultipartUtility;


public class UploadTask extends AsyncTask<Void, Void, List<String>> {
    private static final String requestURL = "http://mysplot.com/backend/forms/uploadphoto.php";
    private String title;
    private String interests;
    private String description;
    private File image;
    private UploadActivity callback;
    private ProgressDialog progressDialog;


    public UploadTask(String title, String interests, String description, File image, UploadActivity callback){
        this.title = title;
        this.interests = interests;
        this.description = description;
        this.image = image;
        this.callback = callback;
        this.progressDialog = new ProgressDialog(callback);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage("Uploading photo...");
        progressDialog.show();
    }

    @Override
    protected List<String> doInBackground(Void... voids) {
        List<String> response = null;
        try {
            MultipartUtility multipart = new MultipartUtility(requestURL, "UTF-8");

            multipart.addFormField("description", this.description);
            multipart.addFormField("title", this.title);
            multipart.addFormField("interests", this.interests);
            multipart.addFormField("username",callback.getSharedPreferences(callback.getResources().getString(R.string.user_credentials),0).getString(callback.getResources().getString(R.string.credentials_username), null));
            multipart.addFormField("password",callback.getSharedPreferences(callback.getResources().getString(R.string.user_credentials),0).getString(callback.getResources().getString(R.string.credentials_password), null));
            multipart.addFilePart("image", image);
            response = multipart.finish();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(List<String> response) {
        super.onPostExecute(response);
        System.out.println(response.get(0));
        progressDialog.dismiss();

        try {
            JSONObject json = new JSONObject(response.get(0));
            boolean status = json.getBoolean("success");

            JSONArray jsonArray = json.getJSONArray("response");
            List<String> messages = new ArrayList<String>();
            for (int i = 0;i<jsonArray.length();i++) messages.add(jsonArray.getString(i));

            if (status) callback.onSuccess();
            else callback.onFail(messages);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
