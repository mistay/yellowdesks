package at.langhofer.yellowdesks;

import android.os.AsyncTask;
import android.util.Base64;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSocketFactory;

import static android.R.attr.port;

/**
 * Created by arminlanghofer on 20.11.16.
 */

public class DownloadWebTask extends AsyncTask<String, Void, String> {

    public TaskDelegate delegate;

    @Override
    protected String doInBackground(String... urls) {

        // params comes from the execute() call: params[0] is the url
        System.out.println( "trying to download url: " + urls[0] );
        String raw = null;

        try {
            raw = downloadUrl( urls[0] );

        } catch (IOException e) {
            System.out.println( "could not download url: " + urls[0] + ". exception: " + e.toString() );
        }
        return raw;
    }

    // onPostExecute displays the results of the AsyncTask.
    @Override
    protected void onPostExecute(String result) {
        delegate.taskCompletionResult( result );
        System.out.println( "download finished: " + result );
    }

    public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        StringBuffer sb = new StringBuffer();

        reader = new InputStreamReader( stream, "UTF-8" );

        char[] buffer = new char[1024];

        for (int read; (read = reader.read( buffer )) != -1; ) {
            sb.append( buffer, 0, read );
        }

        return sb.toString();
    }

    private String downloadUrl(String myurl) throws IOException {
        return downloadUrl( myurl, false );
    }

    private String downloadUrl(String myurl, boolean endlessly) throws IOException {
        InputStream is = null;
        try {
            URL url = new URL( myurl );
            System.out.println( "url (URL): " + url.toString() );

            HttpsURLConnection conn = null;
            try {
                InetAddress[] addresses = InetAddress.getAllByName( url.getHost() );

                // randomly mix list of ip addresses
                List<InetAddress> aList = Arrays.asList(addresses);
                Collections.shuffle(aList);
                addresses = aList.toArray(new InetAddress[aList.size()]);

                do {
                    for (InetAddress inetAddress : addresses) {
                        String headervalue = null;
                        System.out.println( String.format( "current ip address for host %s: %s", inetAddress.getHostName(), inetAddress.getHostAddress() ) );
                        try {
                            conn = (HttpsURLConnection) url.openConnection();
                            System.out.println( "login: " + url.getUserInfo() );

                            // e.g. Basic YXJtfaW5jbaedvcmtwelcjpafaepbmhhcjFCKaag==
                            if (url.getUserInfo() != null) {
                                headervalue = String.format( "Basic %s", Base64.encodeToString( url.getUserInfo().getBytes(), Base64.NO_WRAP ) );
                            } else {
                                if (Data.getInstance().loginDetails != null) {
                                    headervalue = String.format( "Basic %s", Base64.encodeToString( String.format( "%s:%s", Data.getInstance().loginDetails.username, Data.getInstance().loginDetails.password ).getBytes(), Base64.NO_WRAP ) );
                                }
                            }
                        } catch (Exception e) {
                            System.out.println( "Exception while openConnection() to " + inetAddress.getHostAddress() + " " + e.toString() );
                        }

                        if (conn == null)
                            continue;

                        conn.setReadTimeout( 10000 );
                        conn.setConnectTimeout( 15000 );
                        conn.setRequestMethod( "GET" );
                        //conn.setRequestProperty( "Host", "yellowdesks.com" );
                        //System.out.println( "Host: " + "yellowdesks.com" );

                        try {
                            // force httpsconnection to connect to specific url
                            SSLSocketFactory sf = conn.getSSLSocketFactory();
                            int port = url.getPort() == -1 ? url.getDefaultPort() : url.getPort();
                            sf.createSocket( inetAddress, port );

                            //TLS 1.2 SNI: Extension server_name, server_name: [type=host_name (0), value=site.company.com]

                        } catch (Exception e) {
                            System.out.println( "SSLSocketFactory: could not connect to: " + inetAddress.getHostAddress() + " host: " + inetAddress.getHostName() + " port: " + port + " exception: " + e.toString() );
                            System.out.println( "-------- next host ----------" );
                            continue;
                        }

                        if (headervalue != null)
                            conn.setRequestProperty( "Authorization", headervalue );

                        System.out.println( "headervalue: " + headervalue );

                        conn.setDoInput( true );

                        for (String header : conn.getRequestProperties().keySet()) {
                            if (header != null) {
                                for (String value : conn.getRequestProperties().get(header)) {
                                    System.out.println("http request header: " + header + ":" + value);
                                }
                            }
                        }

                        System.out.println("conn: " + conn.toString());

                        try {
                            conn.connect();
                        } catch (Exception e) {
                            System.out.println( "socket: could not connect to: " + inetAddress.getHostAddress() + " host: " + inetAddress.getHostName() + " exception: " + e.toString() );
                            System.out.println( "-------- next host ----------" );
                            continue;
                        } finally {

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
                System.out.println( "Exception while DownloadWebTask::downloadUrl(): " + e.toString() );
            }
            return null;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
}
