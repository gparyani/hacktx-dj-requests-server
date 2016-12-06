package cs371m.godj;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Jasmine on 11/28/2016.
 */

public class CreateEventFragment extends Fragment {

    protected Button createEventBut;

    private boolean returnToSearch;

    static CreateEventFragment newInstance() {
        CreateEventFragment createEventFragment = new CreateEventFragment();
        return createEventFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.create_event, container, false);
        createEventBut = (Button) v.findViewById(R.id.create_event_but);

        if(getArguments() != null) {
            returnToSearch = getArguments().getBoolean("returnToSearch", false);
        } else {
            returnToSearch = false;
        }


        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        EditText setDate = (EditText) getActivity().findViewById(R.id.event_date_et);
        setDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "eventDatePicker");
            }
        });

        final EditText setStartTime = (EditText) getActivity().findViewById(R.id.event_start_et);
        setStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerFragment timePickerFragment = new TimePickerFragment();
                Bundle b = new Bundle();
                b.putInt("textField", R.id.event_start_et);
                timePickerFragment.setArguments(b);
//                FragmentManager fragmentManager = getFragmentManager();
                timePickerFragment.show(getFragmentManager(), "startTimePicker");
            }
        });

        EditText setEndTime = (EditText) getActivity().findViewById(R.id.event_end_et);
        setEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerFragment timePickerFragment = new TimePickerFragment();
                Bundle b = new Bundle();
                b.putInt("textField", R.id.event_end_et);
                timePickerFragment.setArguments(b);
//                FragmentManager fragmentManager = getFragmentManager();
                timePickerFragment.show(getFragmentManager(), "endTimePicker");
            }
        });

        createEventBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*TODO: ERROR CHECK INPUT (I.E. MAKE SURE ALL FIELDS ARE FILLED IN)*/
                EventObject eventObject = new EventObject();



                EditText eventName = (EditText) getActivity().findViewById(R.id.create_event_nm_et);
                EditText hostName = (EditText) getActivity().findViewById(R.id.create_host_nm_et);
                EditText date = (EditText) getActivity().findViewById(R.id.event_date_et);
                EditText startTime = (EditText) getActivity().findViewById(R.id.event_start_et);
                EditText endTime = (EditText) getActivity().findViewById(R.id.event_end_et);


                String hostNameStr = hostName.getText().toString();
                String eventNameStr = eventName.getText().toString();
                String formattedDate = date.getText().toString();
                String start = startTime.getText().toString();
                String end = endTime.getText().toString();




                if(hostNameStr.equals("") || eventNameStr.equals("") || formattedDate.equals("")
                        || start.equals("") || end.equals("")) {
//                    System.out.println("start: " + start);
                    Snackbar snack = Snackbar.make(getView(), "Please make sure all fields are filled in", Snackbar.LENGTH_LONG);
                    View view = snack.getView();
                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    snack.show();
                } else {
                    String formattedStart = formattedDate + " " + start;
                    String formattedEnd = formattedDate + " " + end;
                    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy h:mm a");
                    sdf.setTimeZone(TimeZone.getDefault());
                    try {
                        Date dStart = sdf.parse(formattedStart);

                        long startMill = dStart.getTime();
                        Date dEnd = sdf.parse(formattedEnd);
                        long endMill = dEnd.getTime();

                        eventObject.setHostName(hostNameStr);
                        eventObject.setEventName(eventNameStr);
                        eventObject.setEventQueryName(eventNameStr.toLowerCase());
                        eventObject.setStartTime(startMill);

                        eventObject.setEndTime(endMill);
                        eventObject.setKey("null");

                        Date b = new Date(startMill);
                        Date c = new Date(System.currentTimeMillis());

                        System.out.println("SET TIME: " + sdf.format(b) + "\nCURR TIME: " +
                                sdf.format(c));

                    /*TODO: NEED TO FIGURE OUT HOW TO PUT GENERATED KEY IN EVENT OBJECT*/
                        String key = FirebaseDatabase.getInstance().getReference("events").push().getKey();
                        eventObject.setKey(key);
                        FirebaseDatabase.getInstance().getReference("events").child(key).setValue(eventObject);
                        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
                        userName = userName.replaceAll("\\.", "@");
                        FirebaseDatabase.getInstance().getReference("users").child(userName)
                                .child("hostedEvents").child(key).setValue(eventObject);
                        Snackbar snack = Snackbar.make(getView(), "Event Created!", Snackbar.LENGTH_SHORT);
                        snack.setCallback(new Snackbar.Callback() {
                            @Override
                            public void onDismissed(Snackbar snackbar, int event) {
                                super.onDismissed(snackbar, event);
                                getFragmentManager().popBackStack();
                            }
                        });
                        View view = snack.getView();
                        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                        snack.show();


                    } catch (ParseException e) {
                        e.printStackTrace();
                    }


                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(returnToSearch) {
            getActivity().getSupportFragmentManager().popBackStack();
            Intent startUserMain = new Intent(getContext(), UserMainActivity.class);
            startActivity(startUserMain);
        }
    }
}
