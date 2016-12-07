package cs371m.godj;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.support.design.widget.Snackbar.make;

/**
 * Created by Jasmine on 11/29/2016.
 */

public class EventSearchFragment extends Fragment {

    protected Button searchBut;
    protected EditText searchET;
    protected EditText dateET;
    protected Button dateBut;
    protected List<EventObject> events;
    protected Handler handler;

    private boolean returnToSearch;
    private boolean startingEventFind;

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
        dateET = (EditText) v.findViewById(R.id.date_event_search_term);
        dateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new SearchDatePickerFragment();
                newFragment.show(getFragmentManager(), "searchDatePicker");
            }
        });
        dateBut = (Button) v.findViewById(R.id.date_event_search_but);
        events = new ArrayList<>();
        handler = new Handler();

        if(getArguments() != null) {
            returnToSearch = getArguments().getBoolean("returnToSearch", false);
        } else {
            returnToSearch = false;
        }
        startingEventFind = false;

        return v;
    }

    public void goSearchName() {
        startingEventFind = true;
        Bundle b = new Bundle();
        if(!(searchET.getText().toString().equals(""))) {
            b.putString("searchTerm", searchET.getText().toString());
            EventSearchResultsFragment esrf = new EventSearchResultsFragment();
            esrf.setArguments(b);
            getFragmentManager().beginTransaction()
                    .replace(R.id.main_frame, esrf, "eventSearch")
                    .addToBackStack("eventSearch")
                    .commit();
        } else {
            ViewGroup viewGroup = (ViewGroup) ((ViewGroup) getActivity().findViewById(android.R.id.content)).getChildAt(0);
            Snackbar snack = make(viewGroup, "Please enter a search term first", Snackbar.LENGTH_LONG);
            View view = snack.getView();
            TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            snack.show();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        searchET.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            goSearchName();
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });

        searchBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSearchName();
            }
        });

        dateBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startingEventFind = true;
                Bundle b = new Bundle();
                if (!(dateET.getText().toString().equals(""))) {
                    b.putString("dateTerm", dateET.getText().toString());
                    EventSearchResultsFragment esrf = new EventSearchResultsFragment();
                    esrf.setArguments(b);
                    getFragmentManager().beginTransaction()
                            .replace(R.id.main_frame, esrf, "eventSearch")
                            .addToBackStack("eventSearch")
                            .commit();
                } else {
                    ViewGroup viewGroup = (ViewGroup) ((ViewGroup) getActivity().findViewById(android.R.id.content)).getChildAt(0);
                    Snackbar snack = make(viewGroup, "Please enter a search term first", Snackbar.LENGTH_LONG);
                    View view = snack.getView();
                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    snack.show();
                }
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(!startingEventFind && returnToSearch) {
            getActivity().getSupportFragmentManager().popBackStack();
            Intent startUserMain = new Intent(getContext(), UserMainActivity.class);
            startActivity(startUserMain);
        }
    }
}
