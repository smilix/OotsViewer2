package smilix.ootsviewer2;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by holger on 08.06.16.
 */
public class DownloadWebPageTask extends AsyncTask<String, Void, AsyncTaskResult<String>> {

    @Override
    protected AsyncTaskResult<String> doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            try {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader buffer = new BufferedReader(
                        new InputStreamReader(in));
                StringBuilder content = new StringBuilder();
                String s = "";
                while ((s = buffer.readLine()) != null) {
                    content.append(s);
                }
                return new AsyncTaskResult<>(content.toString());
            } finally {
                urlConnection.disconnect();
            }
        } catch (IOException e) {
            return new AsyncTaskResult<>(e);
        }
    }

    @Override
    protected void onPostExecute(AsyncTaskResult<String> result) {
//            textView.setText(Html.fromHtml(result));
    }
}
