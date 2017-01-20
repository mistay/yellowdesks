package at.langhofer.yellowdesks3;

import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Data {
    List<Host> arrayOfList = new ArrayList<Host>();

    private Data() {
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
                            Host h = new Host(Long.parseLong(value.getString("id")), value.getString("host"), Integer.parseInt(value.getString("desks")), Integer.parseInt(value.getString("desks_avail")), Double.parseDouble( value.getString("lat")), Double.parseDouble( value.getString("lng")), value.getString("imageURL"), details, title);

                            System.out.println("debugging new Host()");
                            h.debug();
                            System.out.println("eof debugging new Host()");
                            arrayOfList.add(h);
                        }
                    } catch (Exception e) {
                        System.out.println("could not parse json: " + raw + ". exception: " + e.toString());
                    }

                    downloadFinished.taskCompletionResult("");
                }
            }
        };
        downloadWebTask.execute("https://yellowdesks.com/hosts");
        System.out.println("sent download request");
    }

    private static Data _instance = null;
    public static Data getInstance() {
        if (_instance == null)
            _instance = new Data();

        return _instance;
    }

    public List<Host> getData() {
        return arrayOfList;
    }

}
