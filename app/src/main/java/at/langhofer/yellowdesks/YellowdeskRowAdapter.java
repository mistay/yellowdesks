package at.langhofer.yellowdesks;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static java.lang.String.format;

public class YellowdeskRowAdapter extends ArrayAdapter<Host> {

    private Activity activity;
    private List<Host> hosts;
    private Host host;
    private int row;

    public YellowdeskRowAdapter(Activity act, int resource, List<Host> arrayList) {
        super(act, resource, arrayList);
        this.activity = act;
        this.row = resource;
        this.hosts = arrayList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        System.out.println("getView()");

        View view = convertView;
        final ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(row, null);

            holder = new ViewHolder();
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        if ((hosts == null) || ((position + 1) > hosts.size()))
            return view;

        host = hosts.get(position);

        holder.deskstatus = (TextView) view.findViewById(R.id.deskstatus);
        holder.imgView = (ImageView) view.findViewById(R.id.resultimage);

        holder.deskstatus.setText(format("%s: Yellow Desks: %d/%d", host.getHost(), host.gettotalDesks(), host.getAvailableDesks()));

        Drawable myDrawable = null;

        Bitmap bitmap = host.getBitmap();




        if (bitmap != null) {
            holder.imgView.setImageDrawable(new BitmapDrawable(bitmap));
        } else {

            // muss man machen, sonst werden die images mit gecachten bildern geladen.
            holder.imgView.setImageDrawable(null);

            DelegateImageDownloaded downloadFinished = new DelegateImageDownloaded() {
                @Override
                public void imageDownloaded(Bitmap result) {
                    if (result != null) {
                        System.out.println("imageDownloaded, result: " + result.toString());

                        Drawable myDrawable = new BitmapDrawable(result);
                        holder.imgView.setImageDrawable(myDrawable);
                    } else {
                        System.out.println("DelegateImageDownloaded downloadFinished but result was null :(");
                    }
                }
            };

            System.out.println("downloadImage. id: " + host.getId() + " url: " +  host.getImageURL() + " isnull? " + ( host.getImageURL() == null));
            if (host.getImageURL() != null)
                Data.getInstance().downloadImage(host, downloadFinished);
        }
/*
        myDrawable = view.getContext().getResources().getDrawable(R.drawable.alex);
        if ((position % 2) == 0) {
            myDrawable = view.getContext().getResources().getDrawable(R.drawable.twocoworkers);
        }
        holder.imgView.setImageDrawable(myDrawable);
*/

        return view;
    }

    public class ViewHolder {
        public TextView deskstatus;
        private ImageView imgView;
    }
}
