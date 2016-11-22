package at.langhofer.yellowdesks3;

import android.graphics.Bitmap;

public class Host {
    private Long id = null;
    private String host = null;
    private int totalDesks=0;
    private int availableDesks=0;
    private double lat = Integer.MIN_VALUE;
    private double lng = Integer.MIN_VALUE;
    private String imageURL = "";
    private Bitmap bitmap = null;
    private String details = "";

    private Host() {
        // do not allow new Item()
    }

    public Host( Long id, String host, int totalDesks, int availableDesks, double lat, double lng, String imageURL, String details) {
        this.totalDesks = totalDesks;
        this.availableDesks = availableDesks;
        this.id = id;
        this.host = host;
        this.lat = lat;
        this.lng = lng;
        this.imageURL = imageURL;
        this.details = details;
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

    public Bitmap getBitmap() { return bitmap; }
    public void setBitmap(Bitmap bitmap) { this.bitmap = bitmap; }
}
