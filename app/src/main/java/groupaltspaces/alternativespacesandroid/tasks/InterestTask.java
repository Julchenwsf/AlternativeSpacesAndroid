package groupaltspaces.alternativespacesandroid.tasks;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;


import groupaltspaces.alternativespacesandroid.util.JsonHelper;
import groupaltspaces.alternativespacesandroid.util.MultipartUtility;

/**
 * Created by BrageEkroll on 14.10.2014.
 */
public class InterestTask extends AsyncTask<String, Void, List<String>> {


    private static final String requestURL = "http://folk.ntnu.no/valerijf/div/AlternativeSpaces/source/backend/db/DBInterests.php?q=";
    private InterestCallback callback;

    public InterestTask(InterestCallback callback){
        this.callback = callback;
    }

    @Override
    protected List<String> doInBackground(String... strings) {
        List<String> response = null;
        try {
            MultipartUtility multipart = new MultipartUtility(requestURL + strings[0], "UTF-8");
            response = multipart.finish();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return response;
    }

    @Override
    protected void onPostExecute(List<String> response) {
        super.onPostExecute(response);
        try {
            JSONObject json = new JSONObject(response.get(0));
            JSONArray jsonArray = json.getJSONArray("response");
            List<Map<String,Object>> messages = JsonHelper.toList(jsonArray);
            callback.onInterestReceived(messages);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}
