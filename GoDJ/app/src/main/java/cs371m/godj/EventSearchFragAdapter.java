package cs371m.godj;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


/**
 * Created by Jasmine on 11/29/2016.
 */

public class EventSearchFragAdapter extends BaseAdapter {

    private LayoutInflater inflater;
    private List<EventObject> events;

    public EventSearchFragAdapter(Context c) {
        inflater = LayoutInflater.from(c);

    }


    @Override
    public int getCount() {
        if(events != null) {
            return events.size();
        } else {
            return 0;
        }
    }

    @Override
    public Object getItem(int position) {
        if(events != null) {
            return events.get(position);
        } else {
            return null;
        }
    }

    @Override
    public long getItemId(int position) {
        if(events != null) {
            return events.get(position).hashCode();
        } else {
            return 0;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EventObject data = (EventObject) getItem(position);
        if (data == null) {
            throw new IllegalStateException("this should be called when list is not null");
        }

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.event_item_layout, parent, false);
        }
        bindView(data, convertView, parent);
        return convertView;
    }

    public void bindView(EventObject data, View view, ViewGroup parent) {
        TextView eventNm = (TextView) view.findViewById(R.id.event_item_name);
        TextView eventHost = (TextView) view.findViewById(R.id.event_item_hosted_by);
        TextView eventDate = (TextView) view.findViewById(R.id.event_item_date);
        TextView eventTime = (TextView) view.findViewById(R.id.event_item_time);
        TextView key = (TextView) view.findViewById(R.id.event_item_key);



        eventNm.setText(data.getEventName());
        eventHost.setText("Hosted by: " + data.getHostName());

        String dateString = "";
        long start = data.getStartTime();
        long end = data.getEndTime();

        Date d = new Date(start); /*TODO: CHANGE DATE FORMAT TO LOCAL FORMAT*/
        SimpleDateFormat sdf = new SimpleDateFormat("E MMMM d, yyyy");
        sdf.setTimeZone(TimeZone.getDefault());
        String formattedDate = sdf.format(d);

        sdf.applyPattern("h:mm a");
        String formattedStart = sdf.format(d);

        d = new Date(end);
        String formattedEnd = sdf.format(d);


        dateString += (formattedStart + " - " + formattedEnd);
        eventDate.setText(formattedDate);
        eventTime.setText(dateString);
        key.setText(data.getKey());



    }

    public void changeList(List<EventObject> newList) {
        events = newList;
    }
}
