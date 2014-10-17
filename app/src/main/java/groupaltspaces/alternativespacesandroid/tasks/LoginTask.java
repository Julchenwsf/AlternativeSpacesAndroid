package groupaltspaces.alternativespacesandroid.tasks;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import groupaltspaces.alternativespacesandroid.activity.LoginActivity;
import groupaltspaces.alternativespacesandroid.util.MultipartUtility;

/**
 * Created by BrageEkroll on 17.10.2014.
 */
public class LoginTask extends AsyncTask<Void, Void, List<String>> {

    private static final String requestURL = "http://folk.ntnu.no/valerijf/div/AlternativeSpaces/source/backend/forms/logform.php";
    private String username;
    private String password;
    private LoginActivity callback;
    private ProgressDialog progressDialog;


    public LoginTask(String username, String password, LoginActivity callback){
        this.username = username;
        this.password = password;
        this.callback = callback;
        this.progressDialog = new ProgressDialog(callback);
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage("Authenticating user credentials...");
        progressDialog.show();
    }

    @Override
    protected List<String> doInBackground(Void... voids) {
        List<String> response = null;
        try {
            MultipartUtility multipart = new MultipartUtility(requestURL, "UTF-8");
            multipart.addFormField("username", this.username);
            multipart.addFormField("password", this.password);
            response = multipart.finish();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return response;
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

            if (status) callback.onLoginSuccess();
            else callback.onLoginFail();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
