package at.langhofer.yellowdesks3;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by arminlanghofer on 20.11.16.
 */

public class DownloadWebTask extends AsyncTask<String, Void, String> {

    public TaskDelegate delegate;

        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url
            System.out.println("trying to download url: " + urls[0]);
            String raw=null;
            try {
                raw = downloadUrl(urls[0]);

            } catch (IOException e) {
                System.out.println("could not download url: " + urls[0] + ". exception: " + e.toString());
            }
            return raw;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            delegate.taskCompletionResult(result);

            System.out.println("download finished: " + result);
        }

    public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        StringBuffer sb = new StringBuffer(  );

        reader = new InputStreamReader(stream, "UTF-8");

        char[] buffer = new char[1024];

        for (int read; (read = reader.read(buffer)) != -1;) {
            sb.append(buffer, 0, read);
        }

        return sb.toString();
    }

    private String downloadUrl(String myurl) throws IOException {
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

            // Convert the InputStream into a string
            String contentAsString = readIt(is);
            System.out.println("contentasstring: " + contentAsString);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
