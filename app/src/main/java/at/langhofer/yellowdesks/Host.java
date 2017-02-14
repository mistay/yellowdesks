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

    Float price_1day = null;
    Float price_10days = null;
    Float price_1month = null;
    Float price_6months =  null;


    // e.g. "Highspeed Internet, Küche, Besprechungsraum, ..."
    private String details = "";

    // e.g. "Served Coffee, Meetingroom, Printer, ..."
    String extras = "";

    // e.g. "Creativespace im Herzen Salzburgs"
    private String title = "";

    private String open_from = null;
    private String open_till = null;

    private Host() {
        // do not allow new Item()
    }

    public Host(Long id, String host, int totalDesks, int availableDesks, double lat, double lng, String imageURL, String details, String extras, String open_from, String open_till, Float price_1day, Float price_10days, Float price_1month, Float price_6months, String title, String videoURL) {
        this.totalDesks = totalDesks;
        this.availableDesks = availableDesks;
        this.id = id;
        this.host = host;
        this.lat = lat;
        this.lng = lng;
        this.imageURL = imageURL;
        this.details = details;
        this.extras = extras;
        this.open_from = open_from;
        this.open_till = open_till;
        this.price_1day = price_1day;
        this.price_10days = price_10days;
        this.price_1month = price_1month;
        this.price_6months = price_6months;

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

    public String getExtras() { return extras; }

    public String getTitle() { return title; }


    public String getOpenFrom() { return open_from; }
    public String getOpenTill() { return open_till; }


    public Bitmap getBitmap() { return bitmaps.size()>0 ? bitmaps.get(0) : null; }
    public LinkedList<Bitmap> getBitmaps() { return bitmaps; }

    public String getVideoURL() { return videoURL; }

    public void setBitmap(Bitmap bitmap) { this.bitmaps.add(bitmap); }

    public Float getPrice1Day(){ return price_1day; }
    public Float getPrice10Days(){ return price_10days; }
    public Float getPrice1Month(){ return price_1month; }
    public Float getPrice6Months(){ return price_6months; }



    public String getHostDetails() {
        return details;
    }
}
