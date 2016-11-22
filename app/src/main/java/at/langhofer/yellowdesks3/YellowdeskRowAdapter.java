package at.langhofer.yellowdesks3;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.graphics.drawable.BitmapDrawable;

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

        holder.deskstatus.setText(format("YELLOW desks: %d/%d", host.gettotalDesks(), host.getAvailableDesks()));

        Drawable myDrawable = null;

        Bitmap bitmap = host.getBitmap();

        if (bitmap != null) {
            holder.imgView.setImageDrawable(new BitmapDrawable(bitmap));
        } else {
            DelegateImageDownloaded downloadFinished = new DelegateImageDownloaded() {
                @Override
                public void imageDownloaded(Bitmap result) {
                    System.out.println("imageDownloaded, result: " + result.toString());

                    Drawable myDrawable = new BitmapDrawable(result);
                    holder.imgView.setImageDrawable(myDrawable);
                }
            };
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
