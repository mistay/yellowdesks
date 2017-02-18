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
        // wird immer (wieder mal) aufgerufen, wenn das view gui-control weitere
        // hosts (vorallem: images) nachlaedt ...

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

        holder.deskstatus.setText(format("%s\n%d desks available", host.getTitle(), host.getAvailableDesks()));

        Bitmap bitmap = host.getBitmap();

        if (bitmap != null) {
            holder.imgView.setImageDrawable(new BitmapDrawable(bitmap));
        } else {
            // muss man explizit auf null, sonst werden die images mit ge-cache-ten bildern angezeigt (view-bug?).
            holder.imgView.setImageDrawable(null);

            DelegateImageDownloaded downloadFinished = new DelegateImageDownloaded() {
                @Override
                public void imageDownloaded(Bitmap result, Object tag) {
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

        return view;
    }

    public class ViewHolder {
        public TextView deskstatus;
        private ImageView imgView;
    }
}
