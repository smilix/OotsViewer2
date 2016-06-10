package smilix.ootsviewer2;

import android.os.AsyncTask;
import android.util.Pair;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by holger on 11.06.16.
 */
public class OotsUrlResolver {
    public interface UrlResult {
        void onOotsUrlResolved(String imageUrl);

        void onOotsUrlError(String reason);
    }


    private static final Pattern IMAGE_URL_PATTERN =
            Pattern.compile("<img src=\"\\/comics\\/images\\/([\\w\\.]+)\">", Pattern.CASE_INSENSITIVE);

    private UrlResult callback;
    private Map<Integer, String> imageUrlCache = new HashMap<>();

    public OotsUrlResolver(UrlResult callback) {
        this.callback = callback;
    }


    public void getImageUrl(int comicNumber) {
        if (this.imageUrlCache.containsKey(comicNumber)) {
            this.callback.onOotsUrlResolved(this.imageUrlCache.get(comicNumber));
            return;
        }
        new DownloadWebPageTask().execute(comicNumber);
    }


    class DownloadWebPageTask extends AsyncTask<Integer, Void, AsyncTaskResult<Pair<Integer, String>>> {
        @Override
        protected AsyncTaskResult<Pair<Integer, String>> doInBackground(Integer... number) {

            final String htmlUrl = String.format("http://www.giantitp.com/comics/oots%04d.html", number[0]);
            try {
                URL url = new URL(htmlUrl);
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
                    return new AsyncTaskResult<>(Pair.create(number[0], content.toString()));
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                return new AsyncTaskResult<>(e);
            }
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<Pair<Integer, String>> result) {
            Integer comicNumber = result.getResult().first;
            if (result.getError() != null) {
                callback.onOotsUrlError("Can't load page for comic strip: " + comicNumber);
            } else {
                final Matcher matcher = IMAGE_URL_PATTERN.matcher(result.getResult().second);
                if (matcher.find()) {
                    String imageName = matcher.group(1);
                    String imageUrl = "http://www.giantitp.com/comics/images/" + imageName;
                    imageUrlCache.put(comicNumber, imageUrl);
                    callback.onOotsUrlResolved(imageUrl);
                } else {
                    callback.onOotsUrlError("Can't find image in html.");
                }

            }
        }
    }
}
