package at.langhofer.yellowdesks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadWebimageTask extends AsyncTask<String, Void, String> {

    public DelegateImageDownloaded delegate;

    Bitmap bitmap = null;

    private Object tag;
    public void setTag (Object tag) {
        this.tag = tag;
    }
    public Object getTag () {
        return tag;
    }

    @Override
    protected String doInBackground(String... urls) {
        // params comes from the execute() call: params[0] is the url
        System.out.println("trying to download url: " + urls[0]);

        try {
            System.out.println("trying to download image..");
            bitmap = downloadUrl(urls[0]);
            System.out.println("done downloading image.");
            if (bitmap != null) {
                System.out.println("image width: " + bitmap.getWidth());
            }
            else {
                System.out.println("image was null :(");
            }
        } catch (IOException e) {
            System.out.println("could not download url: " + urls[0] + ". exception: " + e.toString());
        }
        return "";
    }
    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        System.out.println("onPostExecute, result: " + result);
        System.out.println("onPostExecute, bitmap =?= null: " + ( bitmap == null));

        delegate.imageDownloaded(bitmap, tag);

        System.out.println("download finished: " + result);
    }


    private Bitmap downloadUrl(String myurl) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            String headervalue = null;
            if (url.getUserInfo() != null) {
                headervalue = String.format("Basic %s", Base64.encodeToString(url.getUserInfo().getBytes(), Base64.NO_WRAP));
            } else {
                if (Data.getInstance().loginDetails!=null) {
                    headervalue = String.format("Basic %s", Base64.encodeToString( String.format("%s:%s", Data.getInstance().loginDetails.username, Data.getInstance().loginDetails.password).getBytes(), Base64.NO_WRAP));
                }
            }
            System.out.println("headervalue: " + headervalue);
            if (headervalue != null)
                conn.setRequestProperty("Authorization", headervalue);


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
