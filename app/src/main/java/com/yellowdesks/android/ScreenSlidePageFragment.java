package com.yellowdesks.android;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.VideoView;

import java.util.Map;

public class ScreenSlidePageFragment extends Fragment {

    private Bitmap bitmap;
    String videoURL= null;

    public void setVideoURL(String url) {
        this.videoURL = url;
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
    public Bitmap getBitmap() { return bitmap; }

    private Map.Entry<String, Bitmap> entry;
    public void setEntry (Map.Entry<String, Bitmap> entry) { this.entry = entry; }
    public Map.Entry<String, Bitmap> getEntry() {return this.entry;}


    public Boolean tryToSetBitmap() {

        if (bitmap == null && entry != null && entry.getValue() != null ) {
            bitmap = entry.getValue();
            entry = null; // free up double space
            return true;
        }

        return false;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        System.out.println("ScreenSlidePageFragment::onCreate");
        //Bundle args = getArguments();
        //Bitmap bitmap = (Bitmap) args.get("bitmap");

        tryToSetBitmap();


        if (bitmap != null) {
            ImageView ivImageView = new ImageView( getContext() ) ;
            ivImageView.setImageBitmap( bitmap );
            return ivImageView;
        }

        if (videoURL != null)
        {

            VideoView vvHost = new VideoView( getContext() );
            //vvHost.setVisibility(View.GONE);
            if (videoURL != null) {
                System.out.println( "trying to play video: " + videoURL );

                Uri uri = Uri.parse( videoURL );
                if (uri != null) {
                    //vvHost.setVisibility(View.VISIBLE);
                    System.out.println( "setting up video: " + videoURL );

                    MediaController mediaController = new MediaController( getContext());

                    //vvHost.setMediaController( mediaController );
                    vvHost.setVideoURI( uri );
                    vvHost.requestFocus();
                    vvHost.start();

                    System.out.println( "videoview element set to visible, video started w/ url: " + videoURL );

                }
            }
            return vvHost;

        }

        if (bitmap == null && videoURL == null) {
            ImageView ivImageView = new ImageView( getContext() ) ;
            ivImageView.setImageResource( R.drawable.loader );
            return ivImageView;
        }

        return null;
    }
}
