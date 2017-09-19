package com.example.vivekpradhan.basicquestionnaire;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.json.JSONArray;


/**
 * A simple {@link Fragment} subclass.
 * The submission of response to server or localstorage should happen from here.
 */
public class QuesCompleteFragment extends Fragment {


    public QuesCompleteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View w = inflater.inflate(R.layout.fragment_ques_complete, container, false);

        //Get response object and do whatever the hell you want
        JSONArray savedResponses = ((MainActivity)getActivity()).getResponses();
        Button end = (Button) w.findViewById(R.id.button);

        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
        return w;
    }


}
