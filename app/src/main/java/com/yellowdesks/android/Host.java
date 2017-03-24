package com.yellowdesks.android;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.Marker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

public class Host {
    private Long id = null;
    private String host = null;
    private int totalDesks=0;
    private int availableDesks=0;
    private double lat = Integer.MIN_VALUE;
    private double lng = Integer.MIN_VALUE;

    // featured image, displayed in list, ...
    private String imageURL = "";
    private Bitmap bitmap = null;

    // detail view images, excluding featured image (shown in detail view, not in search result list)
    private HashMap<String, Bitmap> images =  null;

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

    private String open_monday_from = null;
    private String open_monday_till = null;
    private String open_tuesday_from = null;
    private String open_tuesday_till = null;
    private String open_wednesday_from = null;
    private String open_wednesday_till = null;
    private String open_thursday_from = null;
    private String open_thursday_till = null;
    private String open_friday_from = null;
    private String open_friday_till = null;
    private String open_saturday_from = null;
    private String open_saturday_till = null;
    private String open_sunday_from = null;
    private String open_sunday_till = null;
    private Boolean open247fixworkers = null;
    private CANCELLATIONSCHEMES cancellationscheme = null;

    public enum CANCELLATIONSCHEMES {
        hard, soft,
    }

    public Marker marker;

    private Host() {
        // do not allow new Item()
    }

    public Host(Long id, String host, int totalDesks, int availableDesks,
                double lat, double lng, String imageURL,
                LinkedList<String> imageURLs, String details, String extras,
                String open_monday_from, String open_monday_till,
                String open_tuesday_from, String open_tuesday_till,
                String open_wednesday_from, String open_wednesday_till,
                String open_thursday_from, String open_thursday_till,
                String open_friday_from, String open_friday_till,
                String open_saturday_from, String open_saturday_till,
                String open_sunday_from, String open_sunday_till,
                Boolean open247fixworkers,
                CANCELLATIONSCHEMES cancellationscheme,
                Float price_1day, Float price_10days, Float price_1month,
                Float price_6months, String title, String videoURL) {
        this.totalDesks = totalDesks;
        this.availableDesks = availableDesks;
        this.id = id;
        this.host = host;
        this.lat = lat;
        this.lng = lng;
        this.imageURL = imageURL;

        images = new  HashMap<String, Bitmap>();
        for (String sURL : imageURLs) {
            images.put(sURL, null);
        }

        this.details = details;
        this.extras = extras;
        this.open_monday_from = open_monday_from;
        this.open_monday_till = open_monday_till;
        this.open_tuesday_from = open_tuesday_from;
        this.open_tuesday_till = open_tuesday_till;
        this.open_wednesday_from = open_wednesday_from;
        this.open_wednesday_till = open_wednesday_till;
        this.open_thursday_from = open_thursday_from;
        this.open_thursday_till = open_thursday_till;
        this.open_friday_from = open_friday_from;
        this.open_friday_till = open_friday_till;
        this.open_saturday_from = open_saturday_from;
        this.open_saturday_till = open_saturday_till;
        this.open_sunday_from = open_sunday_from;
        this.open_sunday_till = open_sunday_till;
        this.open247fixworkers = open247fixworkers;
        this.price_1day = price_1day;
        this.price_10days = price_10days;
        this.price_1month = price_1month;
        this.price_6months = price_6months;
        this.cancellationscheme = cancellationscheme;
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

    public String getOpenFrom() { return open_monday_from; }
    public String getOpenTill() { return open_monday_till; }
    public Boolean getOpen247fixworkers ( ) { return open247fixworkers; }

    public void setBitmap(Bitmap bitmap) { this.bitmap = bitmap; }
    public Bitmap getBitmap() { return this.bitmap; }


    public void setBitmapForImage(String imageURL, Bitmap bitmap) {
        images.put(imageURL, bitmap);
    }
    public HashMap<String, Bitmap> getImages() {
        return images;
    }

    public String getVideoURL() { return videoURL; }

    public Float getPrice1Day(){ return price_1day; }
    public Float getPrice10Days(){ return price_10days; }
    public Float getPrice1Month(){ return price_1month; }
    public Float getPrice6Months(){ return price_6months; }

    public CANCELLATIONSCHEMES getCancellationscheme() { return this.cancellationscheme; }

    public String getHostDetails() {
        return details;
    }

    private Boolean between(String from, String until) {
        if (from == null || until == null)
            return false;

        try {
            Date time1 = new SimpleDateFormat("HH:mm:ss").parse(from);
            Calendar calendar1 = Calendar.getInstance();
            calendar1.setTime(time1);

            Date time2 = new SimpleDateFormat("HH:mm:ss").parse(until);
            Calendar calendar2 = Calendar.getInstance();
            calendar2.setTime(time2);
            calendar2.add(Calendar.DATE, 1);

            Date d = new Date();
            Calendar calendar3 = Calendar.getInstance();
            calendar3.setTime(d);
            calendar3.add(Calendar.DATE, 1);

            Date x = calendar3.getTime();
            if (x.after(calendar1.getTime()) && x.before(calendar2.getTime())) {
                return true;
            }
        } catch (Exception e) {
            System.out.println("exception determing open:" + e.toString());
        }

        return false;
    }

    public Boolean isOpenNow() {
        if (open247fixworkers)
            return true;


        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK); // 0 = sun, 1=mon, ...

        switch (dayOfWeek) {
            case 0:
                //sunday
                return between(open_sunday_from, open_sunday_till);
            case 1:
                return between(open_monday_from, open_monday_till);
            case 2:
                return between(open_tuesday_from, open_tuesday_till);
            case 3:
                return between(open_wednesday_from, open_wednesday_till);
            case 4:
                return between(open_thursday_from, open_thursday_till);
            case 5:
                return between(open_friday_from, open_friday_till);
            case 6:
                return between(open_saturday_from, open_saturday_till);
        }

        return false;
    }
}