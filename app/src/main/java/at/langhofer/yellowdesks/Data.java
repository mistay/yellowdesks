package at.langhofer.yellowdesks;

import android.content.pm.PackageInfo;
import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


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


    public static String version = "";
    public static String getByString() {
        PackageInfo pInfo = null;
        return "by COWORKINGSALZBURG " + ((version != "") ? "(v" + version + ")" : "");
    }
    public void sendBookingRequest(Host host, java.util.Date date, final TaskDelegate downloadFinished) {
        DownloadWebTask downloadWebTask = new DownloadWebTask();
        downloadWebTask.delegate = new TaskDelegate() {
            @Override
            public void taskCompletionResult(String raw) {
                boolean success = false;
                if (raw != null && raw != "") {

                    System.out.println("sendBookingRequest json: " + raw );

                    try {
                        JSONObject value = new JSONObject(raw);

                        success = value.getBoolean("success");

                        System.out.println("eof debugging new taskCompletionResult(): " + success);
                    } catch (Exception e) {
                        System.out.println("could not parse sendBookingRequest json: " + raw + ". exception: " + e.toString());
                    }
                }

                // notify GUI
                downloadFinished.taskCompletionResult(success ? "OK" : "NG");
            }
        };
        downloadWebTask.execute( String.format("https://yellowdesks.com/bookings/bookingrequest/?username=%s&password=%s&host_id=%d&date=%s", Data.getInstance().loginDetails.username, Data.getInstance().loginDetails.password, host.getId(), "20170101"));
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
                            String extras = value.getString("extras");
                            String title = value.getString("title");

                            String imageURL = value.getString("imageURL").toLowerCase().startsWith("null") ? null : value.getString("imageURL");

                            String videoURL = value.getString("videoURL");
                            videoURL = videoURL.toLowerCase().trim() == "null" ? null : videoURL;

                            System.out.println("1day" + value.getString("price_1day"));
                            System.out.println("10day" + value.getString("price_10days"));
                            System.out.println("1month" + value.getString("price_1month"));
                            System.out.println("6months" + value.getString("price_6months"));

                            Float price_1day = value.getString("price_1day").equals("null") ? null :  Float.parseFloat(value.getString("price_1day"));
                            Float price_10days = value.getString("price_10days").equals("null") ? null :  Float.parseFloat(value.getString("price_10days"));
                            Float price_1month = value.getString("price_1month").equals("null") ? null :  Float.parseFloat(value.getString("price_1month"));
                            Float price_6months =  value.getString("price_6months").equals("null") ? null :  Float.parseFloat(value.getString("price_6months"));

                            Host h = new Host(
                                    Long.parseLong(value.getString("id")),
                                    value.getString("host"),
                                    Integer.parseInt(value.getString("desks")),
                                    Integer.parseInt(value.getString("desks_avail")),
                                    Double.parseDouble( value.getString("lat")),
                                    Double.parseDouble( value.getString("lng")),
                                    imageURL,
                                    details,
                                    extras,
                                    value.getString("open_from") == "null" ? null : value.getString("open_from"),
                                    value.getString("open_till") == "null" ? null : value.getString("open_till"),
                                    price_1day,
                                    price_10days,
                                    price_1month,
                                    price_6months,
                                    title,
                                    videoURL);

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

                    System.out.println("done loading hosts");
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
