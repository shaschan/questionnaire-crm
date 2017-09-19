package com.example.vivekpradhan.basicquestionnaire;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;


/**
 * A simple {@link Fragment} subclass.
 */
public class OrderQuestionFragment  extends Fragment{
    int questionId;
    String finalSelection = "";
    public OrderQuestionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View w =  inflater.inflate(R.layout.fragment_radiobutton_question, container, false);
        questionId = getArguments().getInt("questionId");
        Button next = (Button) w.findViewById(R.id.next);

        RadioGroup.LayoutParams checkParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        final JSONObject ques = ((MainActivity) getActivity()).getCurrentQuestion();
        TextView questionText = (TextView) w.findViewById(R.id.questionText);
        RadioGroup parentLayout = (RadioGroup) w.findViewById(R.id.radioGroup);

        /**Update button text when end reached**/
        if(((MainActivity) getActivity()).endOfQuestions()){
            next.setText("Submit");
        }
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(finalSelection.equals("")){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                    builder1.setMessage("Please Select atleast one option");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Ok",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                }else{
                    //Call the navigate method from main activity
                    Log.d("question", finalSelection);
                    //Do validation of response here.
                    //Add to response JSON
                    try {
                        ((MainActivity) getActivity()).updateResponse(questionId,finalSelection);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ((MainActivity) getActivity()).navigateQuestion();

                }
            }
        });

        try {
            JSONArray options = ques.getJSONArray("options");
            String VersionID  = options.getJSONObject(0).getString("opsVerID");
            String TaskID     = options.getJSONObject(0).getString("opsTaskID");
            questionText.setText(VersionID+"-"+TaskID+": "+ques.getString("QuesText"));
            //System.out.println(options);
            for(int i=0;i<ques.getInt("totalOpts");i++){
                JSONObject opt = options.getJSONObject(i);
                RadioButton radioButton = new RadioButton(getActivity());
                radioButton.setId(opt.getInt("opsNoKey"));
                radioButton.setText(opt.getString("opsTextKey"));
                parentLayout.addView(radioButton,checkParams);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        parentLayout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                if(checkedRadioButton.isChecked()){
                    finalSelection = String.valueOf(checkedRadioButton.getId());
                }
            }
        });
        return w;
    }
}