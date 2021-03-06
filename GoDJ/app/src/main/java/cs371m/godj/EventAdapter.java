package cs371m.godj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Jasmine on 10/16/2016.
 */


public class EventAdapter extends BaseAdapter {

    private List<EventObject> eventObjects;
    private LayoutInflater inflater;
    private Context context;

    public EventAdapter(Context context) {
        inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        if(eventObjects != null) {
            return eventObjects.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if(eventObjects != null) {
            return eventObjects.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if(eventObjects != null) {
            return eventObjects.get(position).hashCode();
        } else {
            return 0;
        }
    }

    public void bindView(EventObject data, View view, ViewGroup parent) {
        TextView nameTV = (TextView) view.findViewById(R.id.event_item_name);
        nameTV.setText(data.getEventName());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EventObject data = (EventObject) getItem(position);
        if (data == null) {
            throw new IllegalStateException("this should be called when list is not null");
        }

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.spotify_item, parent, false);
        }
        bindView(data, convertView, parent);
        return convertView;
    }


}