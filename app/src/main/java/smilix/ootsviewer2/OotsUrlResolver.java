package smilix.ootsviewer2;

import android.os.AsyncTask;
import android.util.Pair;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
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
        } else {
            new DownloadWebPageTask().execute(new DownloadOptions(comicNumber, false));
        }
        // prefetch the next comic
        int nextComicNumber = comicNumber + 1;
        if (!this.imageUrlCache.containsKey(nextComicNumber)) {
            new DownloadWebPageTask().execute(new DownloadOptions(nextComicNumber, true));
        }
    }


    public interface UrlResult {
        void onOotsUrlResolved(String imageUrl);

        void onOotsUrlError(String reason);
    }

    class DownloadOptions {
        int number;
        boolean prefetchOnly;

        DownloadOptions(int number, boolean prefetchOnly) {
            this.number = number;
            this.prefetchOnly = prefetchOnly;
        }
    }

    class DownloadWebPageTask extends AsyncTask<DownloadOptions, Void, AsyncTaskResult<Pair<DownloadOptions, String>>> {
        @Override
        protected AsyncTaskResult<Pair<DownloadOptions, String>> doInBackground(DownloadOptions... options) {

            final String htmlUrl = String.format("http://www.giantitp.com/comics/oots%04d.html", options[0].number);
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
                    return new AsyncTaskResult<>(Pair.create(options[0], content.toString()));
                } finally {
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                return new AsyncTaskResult<>(e);
            }
        }

        @Override
        protected void onPostExecute(AsyncTaskResult<Pair<DownloadOptions, String>> result) {
            if (result.hasError()) {
                String errMsg = result.getError().getMessage();
                if (result.getError() instanceof FileNotFoundException) {
                    errMsg = "Url not found ..." + errMsg.substring(errMsg.length() - 10);
                }
                callback.onOotsUrlError(String.format("Can't load page for comic strip (%s).", errMsg));
            } else {
                DownloadOptions options = result.getResult().first;
                final Matcher matcher = IMAGE_URL_PATTERN.matcher(result.getResult().second);
                if (matcher.find()) {
                    String imageName = matcher.group(1);
                    String imageUrl = "http://www.giantitp.com/comics/images/" + imageName;
                    imageUrlCache.put(options.number, imageUrl);
                    if (!options.prefetchOnly) {
                        callback.onOotsUrlResolved(imageUrl);
                    }
                } else {
                    callback.onOotsUrlError("Can't find image in html.");
                }

            }
        }
    }
}
