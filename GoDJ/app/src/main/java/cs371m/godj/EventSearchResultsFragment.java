package cs371m.godj;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jasmine on 11/29/2016.
 */

public class EventSearchResultsFragment extends Fragment {

    protected ListView listView;
    protected EventItemAdapter adapter;
    protected String searchTerm;
    protected List<EventObject> events;
    protected Handler handler;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.favorite_layout, container, false);
        handler = new Handler();
        listView = (ListView) v.findViewById(R.id.fav_list_view);
        events = new ArrayList<>();
        adapter = new EventItemAdapter(getActivity());
        listView.setAdapter(adapter);
        searchTerm = getArguments().getString("searchTerm");
        TextView header = new TextView(getActivity());
        header.setText("Events");
        header.setTextColor(0xffffffff);
        header.setTypeface(header.getTypeface(), 1);
        header.setTextSize(20);
        header.setGravity(0x01);
        listView.addHeaderView(header, null, false);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int pos = position - 1;
                String eventNm = events.get(pos).getEventName();
                String eventHost = events.get(pos).getHostName();
                long startTime = events.get(pos).getStartTime();
                long endTime = events.get(pos).getEndTime();
                String key = events.get(pos).getKey();
                EventItemOptionsFragment eiof = new EventItemOptionsFragment();
                Bundle b = new Bundle();
                b.putString("eventNm", eventNm);
                b.putString("eventHost", eventHost);
                b.putLong("startTime", startTime);
                b.putLong("endTime", endTime);
                b.putString("key", key);
                eiof.setArguments(b);
                eiof.show(getFragmentManager(), "options");
            }
        });

        Log.d("CustomAdapter", "MusicFragment onCreateView successful");

        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Query query = FirebaseDatabase.getInstance().getReference()
                .child("events")
                .orderByChild("eventQueryName")
                .equalTo(searchTerm.toLowerCase());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot eventSnapshot: dataSnapshot.getChildren()) {
                    String key = eventSnapshot.getKey();
                    EventObject eventObject = eventSnapshot.getValue(EventObject.class);
                    eventObject.setKey(key);
                    Log.d("eventByName ", eventObject.getEventName());
                    events.add(eventObject);
                    handler.post(new showResults());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    class showResults implements Runnable {
        @Override
        public void run() {
            adapter.changeList(events);
            adapter.notifyDataSetChanged();
        }
    }

    public static class EventItemOptionsFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            String eventNm = getArguments().getString("eventNm");
            String eventHost = getArguments().getString("eventHost");
            long startTime = getArguments().getLong("startTime");
            long endTime = getArguments().getLong("endTime");
            String key = getArguments().getString("key");
            final EventObject eventObject = new EventObject(eventNm, eventNm.toLowerCase(), eventHost, startTime, endTime, key);
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            String[] options = {"Save Event", "Attend Event"};
            builder.setTitle("Options")
                    .setItems(options, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // The 'which' argument contains the index position
                            // of the selected item
                            if(which == 0) {
                                String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                                userName = userName.replaceAll("\\.", "@");
                                FirebaseDatabase.getInstance().getReference("users").child(userName).child("savedEvents").push().setValue(eventObject);
                                /*TODO: ADD TOAST OR SNACKBAR*/
                            } else if(which == 1) {
                                String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                                userName = userName.replaceAll("\\.", "@");
                                FirebaseDatabase.getInstance().getReference("users").child(userName).child("eventAttending").setValue(eventObject.getKey());
                                /*TODO: ADD TOAST OR SNACKBAR*/
                            }
                        }
                    });
            return builder.create();
        }
    }
}
