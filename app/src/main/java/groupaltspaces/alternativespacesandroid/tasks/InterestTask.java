package groupaltspaces.alternativespacesandroid.tasks;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import groupaltspaces.alternativespacesandroid.util.MultipartUtility;

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
        List<Map<String,Object>> messages = new Gson().fromJson(response.get(0),new TypeToken<List<Map<String,Object>>>(){}.getType());
        callback.onInterestReceived(messages);
    }
}
