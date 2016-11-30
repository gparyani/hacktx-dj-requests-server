package cs371m.godj;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

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
//                FragmentManager fragmentManager = getFragmentManager();
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
                /*TODO: TAKE INFO, CREATE EVENT OBJECT, ADD TO DATABASE UNDER EVENTS*/
                EventObject eventObject = new EventObject();



                EditText eventName = (EditText) getActivity().findViewById(R.id.create_event_nm_et);
                EditText hostName = (EditText) getActivity().findViewById(R.id.create_host_nm_et);
                EditText date = (EditText) getActivity().findViewById(R.id.event_date_et);
                EditText startTime = (EditText) getActivity().findViewById(R.id.event_start_et);
                EditText endTime = (EditText) getActivity().findViewById(R.id.event_end_et);

                String hostNameStr = hostName.getText().toString();
                String eventNameStr = eventName.getText().toString();
                String formattedDate = date.getText().toString();
                String formattedStart = formattedDate + " " + startTime.getText().toString();
                String formattedEnd = formattedDate + " " + endTime.getText().toString();

                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy h:mm a");
                sdf.setTimeZone(TimeZone.getDefault());
                try {
                    Date dStart = sdf.parse(formattedStart);
                    //long dateMill = dDate.getTime();
                    //System.out.println("TIME1 IN MILL: " + d.getTime());
                   // sdf.applyPattern("hh:mm a");
                    //Date dStart = sdf.parse(formattedStart);
                    long startMill = dStart.getTime();
                    Date dEnd = sdf.parse(formattedEnd);
                    long endMill = dEnd.getTime();

                    eventObject.setHostName(hostNameStr);
                    eventObject.setEventName(eventNameStr);
                    eventObject.setStartTime(startMill);

                    eventObject.setEndTime(endMill);

                    Date b = new Date(startMill);
                    Date c = new Date(System.currentTimeMillis());

                    System.out.println("SET TIME: " + sdf.format(b) + "\nCURR TIME: " +
                        sdf.format(c));

                    FirebaseDatabase.getInstance().getReference("events").push().setValue(eventObject);
                    getFragmentManager().popBackStack();


                    //System.out.println("TIME2 IN MILL: " + d2.getTime());
                    //System.out.println("TOTAL TIME IN MILL: " + (d.getTime() + d2.getTime()));
                    //System.out.println("CURRENT TIME IN MILL: " + System.currentTimeMillis());
                } catch (ParseException e) {
                    e.printStackTrace();
                }


            }
        });
    }
}
