package com.example.vivekpradhan.basicquestionnaire;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
public class MaxDiffConjoint extends Fragment {
    int questionId;
    public MaxDiffConjoint() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final View w =  inflater.inflate(R.layout.fragment_maxdiff_conjoint, container, false);
        questionId = getArguments().getInt("questionId");
        Button next = (Button) w.findViewById(R.id.next);

        JSONObject ques = ((MainActivity) getActivity()).getCurrentQuestion();
        TextView questionText = (TextView) w.findViewById(R.id.questionText);
        final LinearLayout parentLayout = (LinearLayout) w.findViewById(R.id.maxDiff_add_layout);

        /**Update button text when end reached**/
        if(((MainActivity) getActivity()).endOfQuestions()){
            next.setText("Submit");
        }


        final String MIItem[] = {""};
        final String LIItem[] = {""};
        final int ASNItem[]   = {0};

        try {
            final JSONArray options = ques.getJSONArray("options");
            String VersionID  = options.getJSONObject(0).getString("opsVerID");
            String SetID     = options.getJSONObject(0).getString("opsSetID");
            questionText.setText(VersionID+"-"+SetID+": "+ques.getString("QuesText"));

            View lineSepOut = new View(getActivity());
            lineSepOut.setMinimumWidth(10);
            lineSepOut.setMinimumHeight(10);
            lineSepOut.setBackgroundColor(Color.GREEN);
            parentLayout.addView(lineSepOut);

            //total 4 options - no none
            JSONObject opt1 = options.getJSONObject(0);
            JSONObject opt2 = options.getJSONObject(1);
            JSONObject opt3 = options.getJSONObject(2);
            JSONObject opt4 = options.getJSONObject(3);

            TextView optText1 = new TextView(getActivity());
            TextView optText2 = new TextView(getActivity());
            TextView optText3 = new TextView(getActivity());
            TextView optText4 = new TextView(getActivity());

            optText1.setText(opt1.getString("opsTextKey"));
            optText2.setText(opt2.getString("opsTextKey"));
            optText3.setText(opt3.getString("opsTextKey"));
            optText4.setText(opt4.getString("opsTextKey"));

            final RadioButton radioButtonMI1 = new RadioButton(getActivity());
            radioButtonMI1.setText("Most Important");
            radioButtonMI1.setId(Integer.parseInt("1"));

            final RadioButton radioButtonLI1 = new RadioButton(getActivity());
            radioButtonLI1.setText("Least Important");
            radioButtonLI1.setId(Integer.parseInt("0"));

            final RadioButton radioButtonMI2 = new RadioButton(getActivity());
            radioButtonMI2.setText("Most Important");
            radioButtonMI2.setId(Integer.parseInt("1"));

            final RadioButton radioButtonLI2 = new RadioButton(getActivity());
            radioButtonLI2.setText("Least Important");
            radioButtonLI2.setId(Integer.parseInt("0"));

            final RadioButton radioButtonMI3 = new RadioButton(getActivity());
            radioButtonMI3.setText("Most Important");
            radioButtonMI3.setId(Integer.parseInt("1"));

            final RadioButton radioButtonLI3 = new RadioButton(getActivity());
            radioButtonLI3.setText("Least Important");
            radioButtonLI3.setId(Integer.parseInt("0"));

            final RadioButton radioButtonMI4 = new RadioButton(getActivity());
            radioButtonMI4.setText("Most Important");
            radioButtonMI4.setId(Integer.parseInt("1"));

            final RadioButton radioButtonLI4 = new RadioButton(getActivity());
            radioButtonLI4.setText("Least Important");
            radioButtonLI4.setId(Integer.parseInt("0"));

            radioButtonMI1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        MIItem[0] = "item1";

                    if("item1".equals(LIItem[0]))
                            LIItem[0]="";

                        radioButtonLI1.setChecked(false);
                        radioButtonMI2.setChecked(false);
                        radioButtonMI3.setChecked(false);
                        radioButtonMI4.setChecked(false);
                }}
            );

            radioButtonLI1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        LIItem[0] = "item1";

                    if("item1".equals(MIItem[0]))
                        MIItem[0]="";

                        radioButtonMI1.setChecked(false);
                        radioButtonLI2.setChecked(false);
                        radioButtonLI3.setChecked(false);
                        radioButtonLI4.setChecked(false);
                }}
            );

            radioButtonMI2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MIItem[0] = "item2";

                    if("item2".equals(LIItem[0]))
                        LIItem[0]="";

                    radioButtonLI2.setChecked(false);
                    radioButtonMI1.setChecked(false);
                    radioButtonMI3.setChecked(false);
                    radioButtonMI4.setChecked(false);
                }}
            );

            radioButtonLI2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LIItem[0] = "item2";

                    if("item2".equals(MIItem[0]))
                        MIItem[0]="";

                    radioButtonMI2.setChecked(false);
                    radioButtonLI1.setChecked(false);
                    radioButtonLI3.setChecked(false);
                    radioButtonLI4.setChecked(false);
                }}
            );

            radioButtonMI3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MIItem[0] = "item3";

                    if("item3".equals(LIItem[0]))
                        LIItem[0]="";

                    radioButtonLI3.setChecked(false);
                    radioButtonMI1.setChecked(false);
                    radioButtonMI2.setChecked(false);
                    radioButtonMI4.setChecked(false);
                }}
            );

            radioButtonLI3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LIItem[0] = "item3";

                    if("item3".equals(MIItem[0]))
                        MIItem[0]="";

                    radioButtonMI3.setChecked(false);
                    radioButtonLI1.setChecked(false);
                    radioButtonLI2.setChecked(false);
                    radioButtonLI4.setChecked(false);
                }}
            );

            radioButtonMI4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MIItem[0] = "item4";

                    if("item4".equals(LIItem[0]))
                        LIItem[0]="";

                    radioButtonLI4.setChecked(false);
                    radioButtonMI1.setChecked(false);
                    radioButtonMI2.setChecked(false);
                    radioButtonMI3.setChecked(false);
                }}
            );

            radioButtonLI4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LIItem[0] = "item4";

                    if("item4".equals(MIItem[0]))
                        MIItem[0]="";

                    radioButtonMI4.setChecked(false);
                    radioButtonLI1.setChecked(false);
                    radioButtonLI2.setChecked(false);
                    radioButtonLI3.setChecked(false);
                }}
            );

            View lineSep1 = new View(getActivity());
            lineSep1.setMinimumWidth(10);
            lineSep1.setMinimumHeight(10);
            lineSep1.setBackgroundColor(Color.GREEN);

            View lineSep2 = new View(getActivity());
            lineSep2.setMinimumWidth(10);
            lineSep2.setMinimumHeight(10);
            lineSep2.setBackgroundColor(Color.GREEN);

            View lineSep3 = new View(getActivity());
            lineSep3.setMinimumWidth(10);
            lineSep3.setMinimumHeight(10);
            lineSep3.setBackgroundColor(Color.GREEN);

            View lineSep4 = new View(getActivity());
            lineSep4.setMinimumWidth(10);
            lineSep4.setMinimumHeight(10);
            lineSep4.setBackgroundColor(Color.GREEN);

            LinearLayout linLayHoz1 = new LinearLayout(getActivity());
            linLayHoz1.setOrientation(LinearLayout.HORIZONTAL);
            parentLayout.addView(linLayHoz1);
            optText1.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f));
            radioButtonLI1.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f));
            radioButtonMI1.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f));
            linLayHoz1.addView(optText1);
            linLayHoz1.addView(radioButtonLI1);
            linLayHoz1.addView(radioButtonMI1);

            parentLayout.addView(lineSep1);

            LinearLayout linLayHoz2 = new LinearLayout(getActivity());
            linLayHoz2.setOrientation(LinearLayout.HORIZONTAL);
            parentLayout.addView(linLayHoz2);
            optText2.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f));
            radioButtonLI2.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f));
            radioButtonMI2.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f));

            linLayHoz2.addView(optText2);
            linLayHoz2.addView(radioButtonLI2);
            linLayHoz2.addView(radioButtonMI2);

            parentLayout.addView(lineSep2);

            LinearLayout linLayHoz3 = new LinearLayout(getActivity());
            linLayHoz3.setOrientation(LinearLayout.HORIZONTAL);
            parentLayout.addView(linLayHoz3);
            optText3.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f));
            radioButtonLI3.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f));
            radioButtonMI3.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f));

            linLayHoz3.addView(optText3);
            linLayHoz3.addView(radioButtonLI3);
            linLayHoz3.addView(radioButtonMI3);

            parentLayout.addView(lineSep3);

            LinearLayout linLayHoz4 = new LinearLayout(getActivity());
            linLayHoz4.setOrientation(LinearLayout.HORIZONTAL);
            parentLayout.addView(linLayHoz4);
            optText4.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f));
            radioButtonLI4.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f));
            radioButtonMI4.setLayoutParams(new TableLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT,1f));

            linLayHoz4.addView(optText4);
            linLayHoz4.addView(radioButtonLI4);
            linLayHoz4.addView(radioButtonMI4);

            parentLayout.addView(lineSep4);

            final RadioButton radioButtonAll = new RadioButton(getActivity());
            radioButtonAll.setId(Integer.parseInt("1"));
            radioButtonAll.setText("All of them are important");

            final RadioButton radioButtonSome = new RadioButton(getActivity());
            radioButtonSome.setId(Integer.parseInt("2"));
            radioButtonSome.setText("Some of them are important");

            final RadioButton radioButtonNone = new RadioButton(getActivity());
            radioButtonNone.setId(Integer.parseInt("3"));
            radioButtonNone.setText("None of them are important");

            radioButtonAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ASNItem[0] = 1;
                    radioButtonSome.setChecked(false);
                    radioButtonNone.setChecked(false);
                }}
            );

            radioButtonSome.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ASNItem[0] = 2;
                    radioButtonAll.setChecked(false);
                    radioButtonNone.setChecked(false);
                }}
            );

            radioButtonNone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ASNItem[0] = 3;
                    radioButtonSome.setChecked(false);
                    radioButtonAll.setChecked(false);
                }}
            );
            View lineSep5 = new View(getActivity());
            lineSep5.setMinimumWidth(20);
            lineSep5.setMinimumHeight(20);
            lineSep5.setBackgroundColor(Color.BLACK);

            parentLayout.addView(lineSep5);
            parentLayout.addView(radioButtonNone,checkParams);
            parentLayout.addView(radioButtonSome,checkParams);
            parentLayout.addView(radioButtonAll,checkParams);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println(finalSelection.length);
                if(ASNItem[0] == 0 || MIItem[0].equals("") || LIItem[0].equals("")){
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                    builder1.setMessage("Please Select atleast one option from both sides");
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
                    Log.d("question",ASNItem[0]+"--MI--"+MIItem[0]+"--LI--"+LIItem[0]);
                    //Do validation of response here.
                    //Add to response JSON

                    String response = MIItem[0]+"-"+LIItem[0]+"-"+ASNItem[0];

                    try {
                        ((MainActivity) getActivity()).updateResponse(questionId,response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ((MainActivity) getActivity()).navigateQuestion();
                }
            }
        });
        return w;
    }


}
