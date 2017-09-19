package com.example.vivekpradhan.basicquestionnaire;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
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
public class CheckboxQuestionFragment extends Fragment {
    String [] finalSelection;
    int questionId;
    public CheckboxQuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final LinearLayout.LayoutParams checkParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        final View w =  inflater.inflate(R.layout.fragment_checkbox_question, container, false);
        questionId = getArguments().getInt("questionId");
        Button next = (Button) w.findViewById(R.id.next);

        JSONObject ques = ((MainActivity) getActivity()).getCurrentQuestion();
        TextView questionText = (TextView) w.findViewById(R.id.questionText);
        final LinearLayout parentLayout = (LinearLayout) w.findViewById(R.id.check_add_layout);

        try {
            if(!ques.getString("file").equals("") && ques.getString("blanks").equals("Multiple choice")){
                if(ques.getString("QuesType").equals("Video based")){
                    InputStream ins = getResources().openRawResource(getResources().getIdentifier(ques.getString("file").split("\\.")[0], "raw", this.getActivity().getPackageName()));
                    int size = ins.available();
// Read the entire resource into a local byte buffer.
                    byte[] buffer = new byte[size];
                    ins.read(buffer);
                    ins.close();
                    FileOutputStream fos = new FileOutputStream(new File(getContext().getExternalCacheDir(), ques.getString("file")));
                    fos.write(buffer);
                    fos.close();

                    File myvid = new File(getContext().getExternalCacheDir(), ques.getString("file"));
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(myvid), "video/*");

                    startActivity(intent);
                }else{
                    if(ques.getString("QuesType").equals("Image based")){
                        InputStream ins = getResources().openRawResource(getResources().getIdentifier(ques.getString("file").split("\\.")[0], "raw", this.getActivity().getPackageName()));
                        int size = ins.available();
// Read the entire resource into a local byte buffer.
                        byte[] buffer = new byte[size];
                        ins.read(buffer);
                        ins.close();
                        FileOutputStream fos = new FileOutputStream(new File(getContext().getExternalCacheDir(), ques.getString("file")));
                        fos.write(buffer);
                        fos.close();

                        File myimg = new File(getContext().getExternalCacheDir(), ques.getString("file"));
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(Uri.fromFile(myimg), "image/*");

                        startActivity(intent);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**Update button text when end reached**/
        if(((MainActivity) getActivity()).endOfQuestions()){
            next.setText("Submit");
        }

        int totopts = 0;
        try {
            totopts = ques.getInt("totalOpts");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String[][] optCodeCounter = new String[1][totopts];

        try {
            questionText.setText(ques.getString("QuesText"));
            final JSONArray options = ques.getJSONArray("options");
            final int totalOps = ques.getInt("totalOpts");
            /** Initialize array and set defaults **/
            final String [] selectedOptions = new String[totalOps];
            Arrays.fill(selectedOptions, "none");
            final int[] optPrefCounter  = new int[1];

            final EditText blankHolder = new EditText(getActivity());
            blankHolder.setHint("Fill Details..");
            blankHolder.setId(Integer.parseInt("999999"));
            blankHolder.setVisibility(View.GONE);

            optPrefCounter[0] = 1;

            for(int i=0;i<totalOps;i++){
                JSONObject opt = options.getJSONObject(i);
                final CheckBox checkBox = new CheckBox(getActivity());
                checkBox.setId(i);

                if(opt.getString("opsTextKey").equals("#others")){
                    checkBox.setText("Others");
                    optCodeCounter[0][i] = "999999";
                }else{
                    checkBox.setText(opt.getString("opsTextKey"));
                    optCodeCounter[0][i] = opt.getString("opsNoKey");
                }

                checkParams.setMargins(10, 10, 10, 10);
                checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if(isChecked){

                        if(buttonView.getText().equals("Others")){
                            blankHolder.setVisibility(View.VISIBLE);
                            parentLayout.addView(blankHolder, checkParams);
                        }
                        selectedOptions[buttonView.getId()] = String.valueOf(buttonView.getId())+"_"+Integer.toString(optPrefCounter[0]);
                        //System.out.println("CHECKED Button VALUE::"+selectedOptions[buttonView.getId()]+"::ID::"+buttonView.getId());
                        optPrefCounter[0]++;
                    }
                    else{
                        if(buttonView.getText().equals("Others")){
                            blankHolder.setVisibility(View.GONE);
                            parentLayout.removeView(blankHolder);
                        }
                        //System.out.println("UNCHECKED Button VALUE::"+selectedOptions[buttonView.getId()]+"::ID::"+buttonView.getId());
                        String[] toUpdateFromKey                              = selectedOptions[buttonView.getId()].split("_");
                        int toUpdateFromKeyIDPref                             = Integer.parseInt(toUpdateFromKey[0]);
                        selectedOptions[buttonView.getId()]                   = "none";

                        for(int aheadOptsToUpdate = 0; aheadOptsToUpdate < totalOps; aheadOptsToUpdate++){
                            if(!selectedOptions[aheadOptsToUpdate].equals("none")){
                                String tempSelOpts[]                = selectedOptions[aheadOptsToUpdate].split("_");
                                String last                         = tempSelOpts[1];
                                last                                = Integer.toString(Integer.parseInt(last)-1);

                                if(toUpdateFromKeyIDPref < Integer.parseInt(last)+1)
                                    selectedOptions[aheadOptsToUpdate]  = tempSelOpts[0]+"_"+last;
                            }
                        }

                        optPrefCounter[0]--;
                    }
                    //System.out.println("-----------------------"+Arrays.toString(finalSelection));
                    finalSelection = selectedOptions;
                    }
                });

                parentLayout.addView(checkBox, checkParams);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //System.out.println(finalSelection.length);
                int flag = 0;
                if(finalSelection != null){
                    for(int p=0;p<finalSelection.length;p++)
                    {
                        if(!(finalSelection[p].length() > 3 && finalSelection[p].substring(0,4).equals("none"))){
                            flag= 1;
                        }
                    }

                }
                if(finalSelection == null || (finalSelection != null && 0 == finalSelection.length) || flag == 0){
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
                    Log.d("question",Arrays.toString(finalSelection));
                    //Do validation of response here.
                    //Add to response JSON
                    ArrayList<String> finalSelectionList = new ArrayList<String>(Arrays.asList(finalSelection));

                    String none = "none";
                    while(finalSelectionList.contains(none))
                        finalSelectionList.remove(none);
                    finalSelection = finalSelectionList.toArray(new String[0]);

                    for(int finSelItr=0;finSelItr<finalSelection.length; finSelItr++){
                        String optCode = optCodeCounter[0][Integer.parseInt(finalSelection[finSelItr].split("_")[0])];
                        if(optCode.equals("999999")){
                            EditText blankHolder = (EditText) w.findViewById(Integer.parseInt("999999"));
                            optCode              = blankHolder.getText().toString();
                        }

                        finalSelection[finSelItr] = optCode+"_"+finalSelection[finSelItr].split("_")[1];
                    }

                    String[] respArr = new String[finalSelection.length];
                    for(int finItr=0; finItr<finalSelection.length; finItr++){
                        respArr[Integer.parseInt(finalSelection[finItr].split("_")[1])-1] = finalSelection[finItr];
                    }
                    String response = (respArr.length>1)?TextUtils.join(",", respArr):respArr[0];
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
