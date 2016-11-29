package cs371m.godj;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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

        createEventBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*TODO: TAKE INFO, CREATE EVENT OBJECT, ADD TO DATABASE UNDER EVENTS*/
            }
        });
    }
}
