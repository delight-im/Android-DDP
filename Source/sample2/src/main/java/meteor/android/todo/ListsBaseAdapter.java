package meteor.android.todo;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Sadmansamee on 2/3/16.
 */
public class ListsBaseAdapter extends BaseAdapter {

    ArrayList listsArrayList;
    Context context;
    private LayoutInflater layoutInflater;

    public ListsBaseAdapter(Context context, Map<String, Lists> listsMap) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.listsArrayList = new ArrayList();

        listsArrayList.addAll(listsMap.entrySet());
        Log.d("ListsBaseAdapter", "" + listsArrayList.size());
    }

    @Override
    public int getCount() {
        return listsArrayList.size();
    }

    @Override
    public HashMap.Entry<String, Lists> getItem(int position) {
        return (HashMap.Entry) listsArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        HashMap.Entry<String, Lists> listsEntry = getItem(position);
        Lists lists = listsEntry.getValue();

        ViewHolder holder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.row_lists, parent, false);
            holder = new ViewHolder();

            holder.listName = (TextView) convertView.findViewById(R.id.listName);

            convertView.setTag(holder);

        }//view
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        Log.d("ListsBaseAdapter", " " + lists.getName());
        holder.listName.setText(lists.getName());


        return convertView;
    }

    static class ViewHolder {
        TextView listName;
    }

}
