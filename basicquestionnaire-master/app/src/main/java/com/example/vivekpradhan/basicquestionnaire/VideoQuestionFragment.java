package com.example.vivekpradhan.basicquestionnaire;

//import io.vov.vitamio.MediaPlayer;
//import io.vov.vitamio.widget.MediaController;
//import io.vov.vitamio.widget.VideoView;

import android.content.Intent;
import android.media.MediaPlayer;
import android.widget.MediaController;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.VideoView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class VideoQuestionFragment extends Fragment {
    int baseIndex = 1729;
    int questionId;
    public VideoQuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View w =  inflater.inflate(R.layout.fragment_video_question, container, false);
        questionId = getArguments().getInt("questionId");
//        String video = getArguments().getString("video");
        Button next = (Button) w.findViewById(R.id.next);

        JSONObject ques = ((MainActivity) getActivity()).getCurrentQuestion();
        TextView questionText = (TextView) w.findViewById(R.id.questionText);

        try {
            if(!ques.getString("file").equals("") && ques.getString("blanks").equals("")){
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
                //Call the navigate method from main activity
                String response = "";
                //Do validation of response here.
                //Add to response JSON
                try {
                    ((MainActivity) getActivity()).updateResponse(questionId,response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ((MainActivity) getActivity()).navigateQuestion();
            }
        });

        try {
            questionText.setText(ques.getString("QuesText"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return w;
    }


}
