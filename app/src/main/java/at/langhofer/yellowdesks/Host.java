package at.langhofer.yellowdesks;

import android.graphics.Bitmap;

import java.util.LinkedList;

public class Host {
    private Long id = null;
    private String host = null;
    private int totalDesks=0;
    private int availableDesks=0;
    private double lat = Integer.MIN_VALUE;
    private double lng = Integer.MIN_VALUE;
    private String imageURL = "";
    private LinkedList<Bitmap> bitmaps = new LinkedList<Bitmap>();
    private String videoURL = null;



    // e.g. "Highspeed Interhet, Küche, Besprechungsraum, ..."
    private String details = "";

    // e.g. "Creativespace im Herzen Salzburgs"
    private String title = "";

    private Host() {
        // do not allow new Item()
    }

    public Host( Long id, String host, int totalDesks, int availableDesks, double lat, double lng, String imageURL, String details, String title, String videoURL) {
        this.totalDesks = totalDesks;
        this.availableDesks = availableDesks;
        this.id = id;
        this.host = host;
        this.lat = lat;
        this.lng = lng;
        this.imageURL = imageURL;
        this.details = details;
        this.title = title;
        this.videoURL = videoURL;
    }

    public void debug() {
        System.out.println("id: " + id + " host: " + host + "totaldesks: " + totalDesks + " avail desks:" + availableDesks  + " lat: " + lat + " lng: " + lng);
    }

    public int gettotalDesks() {
        return totalDesks;
    }
    public int getAvailableDesks() {
        return availableDesks;
    }
    public Long getId() {
        return id;
    }
    public double getLat() {
        return lat;
    }
    public double getLng() { return lng; }
    public String getHost() { return host; }
    public String getImageURL() { return imageURL; }

    public String getDetails() { return details; }
    public String getTitle() { return title; }


    public Bitmap getBitmap() { return bitmaps.size()>0 ? bitmaps.get(0) : null; }
    public LinkedList<Bitmap> getBitmaps() { return bitmaps; }

    public String getVideoURL() { return videoURL; }

    public void setBitmap(Bitmap bitmap) { this.bitmaps.add(bitmap); }

    public String getHostDetails() {
        return details;
    }
}
