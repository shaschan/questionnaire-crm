package com.example.vivekpradhan.basicquestionnaire;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class RankQuestionFragment  extends Fragment{

    public RankQuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        View w =  inflater.inflate(R.layout.fragment_rank_question, container, false);
        TextView questionText = (TextView) w.findViewById(R.id.questionText);
        questionText.setText("Lets Proceed.....");
        Button next = (Button) w.findViewById(R.id.next);
        /**Update button text when end reached**/
        if(((MainActivity) getActivity()).endOfQuestions()){
            next.setText("Submit");
        }
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    ((MainActivity) getActivity()).navigateQuestion();
            }
        });
        return w;
    }


}