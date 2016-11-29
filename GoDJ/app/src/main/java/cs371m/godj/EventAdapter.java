package cs371m.godj;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.EventObject;

/**
 * Created by Jasmine on 10/16/2016.
 */

/*
TODO: HOOK UP ADAPTER TO A RECYCLER VIEW IN MAIN ACTIVITY TO SHOW USER EVENTS
!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private ArrayList<EventObject> eventObjects;
    private EventAdapter.EventViewHolder myHolder;
    private Context context;

    public class EventViewHolder extends RecyclerView.ViewHolder {
        public ImageView postThumbnail;
        public TextView postTitle;
        public View container;

        public EventViewHolder(View view) {
            super(view);
            container = view;
           // postThumbnail = (ImageView) view.findViewById(R.id.picTextRowPic);
           // postTitle = (TextView) view.findViewById(R.id.picTextRowText);
        }
    }

    public EventAdapter(Context c) {
        context = c;
    }

    @Override
    public EventAdapter.EventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_item_layout, parent, false);
        return new EventViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final EventAdapter.EventViewHolder holder, final int position) {

    }

    @Override
    public int getItemCount() {
        return eventObjects.size();
    }


}
