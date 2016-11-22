package at.langhofer.yellowdesks3;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import static java.lang.String.*;

public class YellowdeskRowAdapter extends ArrayAdapter<Host> {

    private Activity activity;
    private List<Host> hosts;
    private Host objBean;
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
        ViewHolder holder;

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

        objBean = hosts.get(position);

        holder.deskstatus = (TextView) view.findViewById(R.id.deskstatus);
        holder.imgView = (ImageView) view.findViewById(R.id.resultimage);

        holder.deskstatus.setText(format("YELLOW desks: %d/%d", objBean.gettotalDesks(), objBean.getAvailableDesks()));

        Drawable myDrawable = null;
        myDrawable = view.getContext().getResources().getDrawable(R.drawable.alex);
        if ((position % 2) == 0) {
            myDrawable = view.getContext().getResources().getDrawable(R.drawable.twocoworkers);
        }
        holder.imgView.setImageDrawable(myDrawable);

        return view;
    }

    public class ViewHolder {
        public TextView deskstatus;
        private ImageView imgView;
    }
}
