package groupaltspaces.alternativespacesandroid.tasks;

import android.app.Application;
import android.os.AsyncTask;

import java.io.File;
import java.io.IOException;
import java.util.List;

import groupaltspaces.alternativespacesandroid.R;
import groupaltspaces.alternativespacesandroid.util.MultipartUtility;

/**
 * Created by BrageEkroll on 10.10.2014.
 */
public class UploadTask extends AsyncTask<Void, Void, List<String>> {




    private String requestURL = "http://folk.ntnu.no/valerijf/div/AlternativeSpaces/source/backend/forms/uploadphoto.php";
    private String title;
    private String interests;
    private String description;
    private File image;
    private String charset = "UTF-8";

    public UploadTask(String title, String interests, String description, File image){
        this.title = title;
        this.interests = interests;
        this.description = description;
        this.image = new File("src/main/res/drawable-mdpi/logo.png");
    }

    @Override
    protected List<String> doInBackground(Void... voids) {
        List<String> response = null;
        try {
            MultipartUtility multipart = new MultipartUtility(requestURL, charset);


            multipart.addFormField("description", this.description);
            multipart.addFormField("title", this.title);
            multipart.addFormField("interests", this.interests);

            multipart.addFilePart("image", image);

            response = multipart.finish();

            System.out.println("SERVER REPLIED:");

            for (String line : response) {
                System.out.println(line);
            }
        } catch (IOException ex) {
            System.err.println(ex);
        }
        return response;
    }

    @Override
    protected void onPostExecute(List<String> aVoid) {
        super.onPostExecute(aVoid);
    }
}
