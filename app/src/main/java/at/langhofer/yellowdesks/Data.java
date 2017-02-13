package at.langhofer.yellowdesks;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static at.langhofer.yellowdesks.LoginDetails.username;


public class Data {
    List<Host> arrayOfList = new ArrayList<Host>();
    LoginDetails loginDetails = null;

    private static Data _instance = null;
    public static Data getInstance() {
        if (_instance == null)
            _instance = new Data();

        return _instance;
    }

    private Data() {
    }

    public void sendBookingRequest(Host host, java.util.Date date, final TaskDelegate downloadFinished) {
        DownloadWebTask downloadWebTask = new DownloadWebTask();
        downloadWebTask.delegate = new TaskDelegate() {
            @Override
            public void taskCompletionResult(String raw) {
                if (raw != null && raw != "") {

                    System.out.println("sendBookingRequest json: " + raw );

                    try {
                        JSONObject value = new JSONObject(raw);

                        //username = value.getString("username");

                        System.out.println("eof debugging new taskCompletionResult()");
                    } catch (Exception e) {
                        System.out.println("could not parse sendBookingRequest json: " + raw + ". exception: " + e.toString());
                    }
                }

                // notify GUI
                downloadFinished.taskCompletionResult("OK");
            }
        };
        downloadWebTask.execute("https://yellowdesks.com/bookings/?username=" + username + "&password=" + "1234" + "&host_id=" + host.getId() + "&date=" + "20170101");
    }

    public void login(final String username, final String password, final TaskDelegate downloadFinished) {
        DownloadWebTask downloadWebTask = new DownloadWebTask();
        downloadWebTask.delegate = new TaskDelegate() {
            @Override
            public void taskCompletionResult(String raw) {
                if (raw != null && raw != "") {
                    try {
                        JSONObject value = new JSONObject(raw);
                        System.out.println("building new Login. error ?");
                        System.out.println( value.getString("error") );

                        loginDetails = new LoginDetails();
                        loginDetails.username = value.getString("username");
                        loginDetails.password = password;
                        loginDetails.firstname = value.getString("firstname");
                        loginDetails.lastname = value.getString("lastname");


                        System.out.println("debugging new LoginDetails");
                        loginDetails.debug();
                        System.out.println("eof debugging new LoginDetails()");
                    } catch (Exception e) {
                        System.out.println("could not parse login json: " + raw + ". exception: " + e.toString());
                    }
                }

                // notify GUI
                downloadFinished.taskCompletionResult("");
            }
        };
        downloadWebTask.execute("https://yellowdesks.com/users/login?username=" + username + "&password=" + password);
        System.out.println("sent login request");
    }

    public void downloadImage(final Host host, final DelegateImageDownloaded downloadFinished) {
        DownloadWebimageTask downloadWebimageTask = new DownloadWebimageTask();
        downloadWebimageTask.delegate = new DelegateImageDownloaded() {
            @Override
            public void imageDownloaded(Bitmap result) {
                host.setBitmap(result);
                System.out.println("taskCompletionResult: " + result);
                downloadFinished.imageDownloaded(result);
            }
        };
        downloadWebimageTask.execute(host.getImageURL());
        System.out.println("sent download request: " + host.getImageURL());
    }

    public void downloadHosts(final TaskDelegate downloadFinished) {
        DownloadWebTask downloadWebTask = new DownloadWebTask();
        downloadWebTask.delegate = new TaskDelegate() {
            @Override
            public void taskCompletionResult(String raw) {
                if (raw != null && raw != "") {
                    try {
                        JSONArray jsonArray = new JSONArray(raw);
                        System.out.println("created jsonObject: " + jsonArray.toString());

                        arrayOfList.clear();
                        for (int i=0; i<jsonArray.length(); i++) {
                            JSONObject value = jsonArray.getJSONObject(i);
                            System.out.println("building new Host()");
                            String details = value.getString("details");
                            String title = value.getString("title");

                            String imageURL = value.getString("imageURL").toLowerCase().startsWith("null") ? null : value.getString("imageURL");
                            Host h = new Host(Long.parseLong(value.getString("id")), value.getString("host"), Integer.parseInt(value.getString("desks")), Integer.parseInt(value.getString("desks_avail")), Double.parseDouble( value.getString("lat")), Double.parseDouble( value.getString("lng")), imageURL, details, title);

                            System.out.println("debugging new Host()");
                            h.debug();
                            System.out.println("eof debugging new Host()");
                            arrayOfList.add(h);
                        }
                    } catch (Exception e) {
                        System.out.println("could not parse json: " + raw + ". exception: " + e.toString());
                    }

                    // notify GUI
                    downloadFinished.taskCompletionResult("");
                }
            }
        };
        downloadWebTask.execute("https://yellowdesks.com/hosts");
        System.out.println("sent download request");
    }


    public List<Host> getData() {
        return arrayOfList;
    }

    public Host getHost(Long hostId) {
        for (Host host: arrayOfList) {
            if (host.getId() == hostId)
                return host;
        }
        return null;
    }
}
