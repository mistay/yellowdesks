package at.langhofer.yellowdesks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadWebimageTask extends AsyncTask<String, Void, String> {

    public DelegateImageDownloaded delegate;

    Bitmap bitmap = null;

    @Override
    protected String doInBackground(String... urls) {
        // params comes from the execute() call: params[0] is the url
        System.out.println("trying to download url: " + urls[0]);

        try {
            System.out.println("trying to dlownload image..");
            bitmap = downloadUrl(urls[0]);
            System.out.println("done downloading image. width: " + bitmap.getWidth());
        } catch (IOException e) {
            System.out.println("could not download url: " + urls[0] + ". exception: " + e.toString());
        }
        return "";
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        System.out.println("onPostExecute, result: " + result);
        System.out.println("onPostExecute, bitmap: " + ( bitmap == null));

        delegate.imageDownloaded(bitmap);

        System.out.println("download finished: " + result);
    }


    private Bitmap downloadUrl(String myurl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            is = conn.getInputStream();

            return BitmapFactory.decodeStream(is);
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
