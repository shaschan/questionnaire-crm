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
import android.widget.EditText;
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
public class RadiobuttonQuestionFragment extends Fragment {
    String finalSelection = "";
    int questionId;
    public RadiobuttonQuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View w =  inflater.inflate(R.layout.fragment_radiobutton_question, container, false);
        questionId = getArguments().getInt("questionId");
        Button next = (Button) w.findViewById(R.id.next);

        RadioGroup.LayoutParams checkParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        final JSONObject ques = ((MainActivity) getActivity()).getCurrentQuestion();
        //System.out.println(ques);
        TextView questionText = (TextView) w.findViewById(R.id.questionText);
        RadioGroup parentLayout = (RadioGroup) w.findViewById(R.id.radioGroup);
            //System.out.println(getResources().getAssets());

        try {
            if(!ques.getString("file").equals("") && ques.getString("blanks").equals("Single choice")){
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
                    if(finalSelection.equals("999999")){
                        EditText blankHolder = (EditText) w.findViewById(Integer.parseInt(finalSelection));
                        finalSelection       = blankHolder.getText().toString();
                    }
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


        final EditText blankHolder = new EditText(getActivity());
        blankHolder.setHint("Fill Details..");
        blankHolder.setId(Integer.parseInt("999999"));
        blankHolder.setVisibility(View.GONE);

        try {
            questionText.setText(ques.getString("QuesText"));
            JSONArray options = ques.getJSONArray("options");

            for(int i=0;i<ques.getInt("totalOpts");i++){
                JSONObject opt = options.getJSONObject(i);
                RadioButton radioButton = new RadioButton(getActivity());
                if((opt.getString("opsTextKey")).equals("#others")){
                    radioButton.setId(Integer.parseInt("999998"));
                    radioButton.setText("Others");
                }else{
                    radioButton.setId(opt.getInt("opsNoKey"));
                    radioButton.setText(opt.getString("opsTextKey"));
                }
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
                    if(checkedRadioButton.getId() == Integer.parseInt("999998")){
                        blankHolder.setVisibility(View.VISIBLE);
                        finalSelection = "999999";
                    }else{
                        blankHolder.setVisibility(View.GONE);
                        finalSelection = String.valueOf(checkedRadioButton.getId());
                    }
                }
            }
        });
        return w;
    }
}