package cs371m.godj;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Jasmine on 11/29/2016.
 */

/*TODO: CHANGE TO ACTIVITY*/
public class EventSearchFragment extends Fragment {

    protected Button searchBut;
    protected EditText searchET;
    protected List<EventObject> events;
    protected Handler handler;

    static EventSearchFragment newInstance() {
        EventSearchFragment eventSearchFragment = new EventSearchFragment();
        return eventSearchFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.event_search_layout, container, false);
        searchBut = (Button) v.findViewById(R.id.event_search_but);
        searchET = (EditText) v.findViewById(R.id.event_search_term);
        events = new ArrayList<>();
        handler = new Handler();

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        searchBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle b = new Bundle();
                b.putString("searchTerm", searchET.getText().toString());
                EventSearchResultsFragment esrf = new EventSearchResultsFragment();
                esrf.setArguments(b);
                getFragmentManager().beginTransaction()
                        .replace(R.id.main_frame, esrf)
                        .addToBackStack("eventSearch")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }
}
