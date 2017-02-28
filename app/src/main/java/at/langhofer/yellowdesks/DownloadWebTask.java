package at.langhofer.yellowdesks;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

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
        return downloadUrl(myurl, false);
    }

    private String downloadUrl(String myurl, boolean endlessly) throws IOException {
        InputStream is = null;

        try {
            URL url = new URL(myurl);


            HttpsURLConnection conn = null;
            try {
                InetAddress[] addresses = InetAddress.getAllByName( url.getHost() );
                do {
                    for (InetAddress i : addresses) {

                        System.out.println( String.format("ip address(es) for host %s: %s", i.getHostName(), i.getHostAddress() ) );
                        try {
                            // todo: not good. was, wenn per SNI ge-vhosted wird auf der serverseite? url möglichst nicht umschreiben ...
                            InetAddress address = InetAddress.getByName(i.getHostAddress());
                            Uri url2 ;

                            Uri.Builder builder = new Uri.Builder();
                            builder.scheme("https")
                                    .authority(i.getHostAddress())
                                    .appendPath(url.getPath());

                            url2 = builder.build();
                            System.out.println("Url2: " + url2.toString());
                            conn = (HttpsURLConnection) url.openConnection();

                            //System.out.println("login: " + Data.getInstance().loginDetails.username);

                            // --> "armin:inh"
                            System.out.println("login: " + url.getUserInfo());
                            //if (Data.getInstance().loginDetails.username != null)

                            // e.g. Basic YXJtfaW5jbaedvcmtwelcjpafaepbmhhcjFCKaag==
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

                            System.out.println("attached basic info");

                        } catch (Exception e) {
                            System.out.println( "Exception while openConnection() to " + i.getHostAddress() + " " + e.toString() );
                        }
                        conn.setReadTimeout( 10000 );
                        conn.setConnectTimeout( 15000 );
                        conn.setRequestMethod( "GET" );
                        conn.setDoInput( true );
                        // Starts the query

                        try {
                            conn.connect();
                        } catch (Exception e) {
                            System.out.println( "socket: could not connect to: " + i.getHostAddress() + " host: " + i.getHostName() + " exception: " + e.toString() );
                            continue;
                        }

                        int response = conn.getResponseCode();
                        is = conn.getInputStream();

                        // Convert the InputStream into a string
                        String contentAsString = readIt( is );
                        //System.out.println("contentasstring: " + contentAsString);
                        return contentAsString;
                    }
                } while (endlessly);

            } catch (Exception e) {
                System.out.println("Exception while resolving hostname: " + e.toString() );
            }
            return null;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
