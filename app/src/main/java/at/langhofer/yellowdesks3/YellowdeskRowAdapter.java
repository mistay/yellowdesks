package at.langhofer.yellowdesks3;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class YellowdeskRowAdapter extends ArrayAdapter<Item> {

    private Activity activity;
    private List<Item> items;
    private Item objBean;
    private int row;

    public YellowdeskRowAdapter(Activity act, int resource, List<Item> arrayList) {
        super(act, resource, arrayList);
        this.activity = act;
        this.row = resource;
        this.items = arrayList;
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

        if ((items == null) || ((position + 1) > items.size()))
            return view;

        objBean = items.get(position);

        holder.tvTitle = (TextView) view.findViewById(R.id.tvtitle);
        holder.tvDesc = (TextView) view.findViewById(R.id.tvdesc);
        holder.tvDate = (TextView) view.findViewById(R.id.tvdate);

        holder.tvTitle.setText(objBean.getTitle());
        holder.tvDesc.setText(objBean.getDesc());


        return view;
    }

    public class ViewHolder {
        public TextView tvTitle, tvDesc, tvDate;
        private ImageView imgView;
    }
}
