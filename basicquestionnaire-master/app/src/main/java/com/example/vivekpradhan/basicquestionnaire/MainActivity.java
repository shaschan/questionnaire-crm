package com.example.vivekpradhan.basicquestionnaire;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.R.id.input;

public class MainActivity extends AppCompatActivity {
    FragmentManager mFragmentManager;

    JSONArray MasterQuestionList;
    JSONArray ResponseList             = new JSONArray();

    JSONObject currentQuestion;

    String [][] saved_response_further_dep   = new String[1000][1000]; //quesno_ops(with sel pref)
    String [][] saved_response_further_undep = new String[1000][1000]; //quesno_ops(with sel pref)

    int currentIndex = -1; //current question no - 1
    int MDConjFlag             = 0; // max diff flag for conjoint going on
    int countTotalMDConjQues   = 0; // max diff iterator of total number of question in a conjoint questype
    int MDConjItr              = 0; // max diff current conjoint card no
    int flagRotMD              = 0; // max diff for flag of rotation allowed per conjoint
    int conjItr      = 0;  //current conjoint card no
    int conjFlag     = 0;  //flag for conjoint is going on
    int flagRot      = 0; //flag for rotation allowed? per conjoint
    int countTotalConjQues  = 0; //iterator of total number of question in a conjoint questype

    ArrayList<Integer> skippingQuestions = new ArrayList<Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MasterQuestionList          = getFromJSON();//PopulateQuestionJSON from assets file

        for(int i = 0; i <saved_response_further_dep.length; i++){
            for(int j = 0; j <saved_response_further_dep.length; j++){
                saved_response_further_dep[i][j] = "";
            }
        }

        for(int i = 0; i <saved_response_further_undep.length; i++){
            for(int j = 0; j <saved_response_further_undep.length; j++){
                saved_response_further_undep[i][j] = "";
            }
        }

        navigateQuestion();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**helper method that returns status of completition of questionnaire**/
    public boolean endOfQuestions(){
        try {
            if((currentIndex == MasterQuestionList.length() -1) && !(currentQuestion.getString("QuesType").split("_")[0].equals("Conjoint") && 0!=countTotalConjQues) && !(currentQuestion.getString("QuesType").split("_")[0].equals("Max Diff Conjoint") && 0!=countTotalMDConjQues))
                return true;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /***helper method to return Response object***/
    public JSONArray getResponses(){
        return ResponseList;
    }

    /*method to match and return transformed question text*/
    public String getCorrectedText(String currQuesAnsText, boolean removeLast){
        //1.checkbox with depen, particular pref
        String fromReplaceString_mcqSing_withPref = "#fromDep_[0-9]+_[0-9]+"; //from_quesno_optno_prefNo regex
        Pattern p1 = Pattern.compile(fromReplaceString_mcqSing_withPref);
        Matcher m1 = p1.matcher(currQuesAnsText);
        while(m1.find()){
            if(!saved_response_further_dep[Integer.parseInt(m1.group().split("_")[1])][Integer.parseInt(m1.group().split("_")[2])].equals("")){
                String toReplLoc = saved_response_further_dep[Integer.parseInt(m1.group().split("_")[1])][Integer.parseInt(m1.group().split("_")[2])];
                if(removeLast)
                    toReplLoc = toReplLoc.substring(0,toReplLoc.lastIndexOf("_"));

                currQuesAnsText = currQuesAnsText.replaceAll(m1.group(),toReplLoc);
            }
        }

        //2.checkbox with depen, all
        String fromReplaceString_mcqAll_withPref = "#fromDep_[0-9]+A"; //from_quesno_optno_prefNo regex
        Pattern p2 = Pattern.compile(fromReplaceString_mcqAll_withPref);
        Matcher m2 = p2.matcher(currQuesAnsText);
        while(m2.find()){
            String strToReplace = "";
            for(int optItr = 0; optItr < saved_response_further_dep[Integer.parseInt(m2.group().split("_")[1].substring(0,m2.group().split("_")[1].length()-1))].length; optItr++){
                if(!saved_response_further_dep[Integer.parseInt(m2.group().split("_")[1].substring(0,m2.group().split("_")[1].length()-1))][optItr].equals("")){
                    String toReplLoc = saved_response_further_dep[Integer.parseInt(m2.group().split("_")[1].substring(0,m2.group().split("_")[1].length()-1))][optItr];
                    if(removeLast)
                        toReplLoc = toReplLoc.substring(0,toReplLoc.lastIndexOf("_"));
                    strToReplace = strToReplace+", "+toReplLoc;
                }
            }
            currQuesAnsText = currQuesAnsText.replaceAll(m2.group(),(strToReplace.length()>2)?(strToReplace.substring(2,strToReplace.length())):m2.group());
        }

        //3.checkbox with Undepen, particular pref
        fromReplaceString_mcqSing_withPref = "#fromUnDep_[0-9]+_[0-9]+"; //from_quesno_optno_prefNo regex
        Pattern p3 = Pattern.compile(fromReplaceString_mcqSing_withPref);
        Matcher m3 = p3.matcher(currQuesAnsText);
        while(m3.find()){
            if(!saved_response_further_undep[Integer.parseInt(m3.group().split("_")[1])][Integer.parseInt(m3.group().split("_")[2])].equals("")){
                String toReplLoc = saved_response_further_undep[Integer.parseInt(m3.group().split("_")[1])][Integer.parseInt(m3.group().split("_")[2])];
                if(removeLast)
                    toReplLoc = toReplLoc.substring(0,toReplLoc.lastIndexOf("_"));
                currQuesAnsText = currQuesAnsText.replaceAll(m3.group(),toReplLoc);
            }
        }

        //4.checkbox with Undepen, all
        fromReplaceString_mcqAll_withPref = "#fromUnDep_[0-9]+A"; //from_quesno_optno_prefNo regex
        Pattern p4 = Pattern.compile(fromReplaceString_mcqAll_withPref);
        Matcher m4 = p4.matcher(currQuesAnsText);
        while(m4.find()){
            String strToReplace = "";
            for(int optItr = 0; optItr < saved_response_further_undep[Integer.parseInt(m4.group().split("_")[1].substring(0,m4.group().split("_")[1].length()-1))].length; optItr++){
                if(!saved_response_further_undep[Integer.parseInt(m4.group().split("_")[1].substring(0,m4.group().split("_")[1].length()-1))][optItr].equals("")){
                    String toReplLoc = saved_response_further_undep[Integer.parseInt(m4.group().split("_")[1].substring(0,m4.group().split("_")[1].length()-1))][optItr];
                    if(removeLast)
                        toReplLoc = toReplLoc.substring(0,toReplLoc.lastIndexOf("_"));
                    strToReplace = strToReplace+", "+toReplLoc;
                }
            }
            currQuesAnsText = currQuesAnsText.replaceAll(m4.group(),(strToReplace.length()>2)?(strToReplace.substring(2,strToReplace.length())):m4.group());
        }

        //5.radiobutton with depen, single
        String fromReplaceString_sin = "#fromDep_[0-9]+S"; //from_quesno_optno_prefNo regex
        Pattern p5 = Pattern.compile(fromReplaceString_sin);
        Matcher m5 = p5.matcher(currQuesAnsText);
        while(m5.find()){
            if(!saved_response_further_dep[Integer.parseInt(m5.group().split("_")[1].substring(0,m5.group().split("_")[1].length()-1))][0].equals("")){
                String toReplLoc = saved_response_further_dep[Integer.parseInt(m5.group().split("_")[1].substring(0,m5.group().split("_")[1].length()-1))][0];
                if(removeLast)
                    toReplLoc = toReplLoc.substring(0,toReplLoc.lastIndexOf("_"));
                currQuesAnsText = currQuesAnsText.replaceAll(m5.group(),toReplLoc);
            }
        }

        //6.radiobutton with Undepen, all
        String fromReplaceString_sinAll = "#fromUnDep_[0-9]+S"; //from_quesno_optno_prefNo regex
        Pattern p6 = Pattern.compile(fromReplaceString_sinAll);
        Matcher m6 = p6.matcher(currQuesAnsText);
        while(m6.find()){
            String strToReplace = "";
            for(int optItr = 0; optItr < saved_response_further_undep[Integer.parseInt(m6.group().split("_")[1].substring(0,m6.group().split("_")[1].length()-1))].length; optItr++){
                if(!saved_response_further_undep[Integer.parseInt(m6.group().split("_")[1].substring(0,m6.group().split("_")[1].length()-1))][optItr].equals("")){
                    String toReplLoc = saved_response_further_undep[Integer.parseInt(m6.group().split("_")[1].substring(0,m6.group().split("_")[1].length()-1))][optItr];
                    if(removeLast)
                        toReplLoc = toReplLoc.substring(0,toReplLoc.lastIndexOf("_"));
                    strToReplace = strToReplace+", "+toReplLoc;
                }
                //System.out.println("RESP["+Integer.parseInt(m6.group().split("_")[1].substring(0,m6.group().split("_")[1].length()-1))+"]"+"["+optItr+"]="+saved_response_further_undep[Integer.parseInt(m6.group().split("_")[1].substring(0,m6.group().split("_")[1].length()-1))][optItr]);
            }
            currQuesAnsText = currQuesAnsText.replaceAll(m6.group(),(strToReplace.length()>2)?(strToReplace.substring(2,strToReplace.length())):m6.group());
        }

        //7.blank with depen as default
        String fromReplaceString_plainText = "#fromDep_[0-9]+P"; //from_quesno_optno_prefNo regex
        Pattern p7 = Pattern.compile(fromReplaceString_plainText);
        Matcher m7 = p7.matcher(currQuesAnsText);
        while(m7.find()){
            if(!saved_response_further_dep[Integer.parseInt(m7.group().split("_")[1].substring(0,m7.group().split("_")[1].length()-1))][0].equals("")){
                String toReplLoc = saved_response_further_dep[Integer.parseInt(m7.group().split("_")[1].substring(0,m7.group().split("_")[1].length()-1))][0];
                if(removeLast)
                    toReplLoc = toReplLoc.substring(0,toReplLoc.lastIndexOf("_"));
                currQuesAnsText = currQuesAnsText.replaceAll(m7.group(),toReplLoc);
            }
        }

        return currQuesAnsText;
    }

    /***helper method to parse JSON from assets file**/
    public JSONArray getFromJSON(){
        JSONArray items     = null;
        String json         = null;
        InputStream is;
        try {
            is              = getAssets().open("questions.json");
            int size        = is.available();
            byte[] buffer   = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        try {
            JSONObject obj = new JSONObject(json);
            items = obj.getJSONArray("questions");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return items;
    }

    public JSONArray getFromJSONCards(String conjFileName, String rotation ,int rotAft){
        JSONArray items = null;
        String json = null;
        InputStream is;
        try {
            is = getAssets().open(conjFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        try {
            JSONArray obj = new JSONArray(json);
            items = obj;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        flagRot                    = ((rotation.equals("inclnoneinclrot") || rotation.equals("inclrot")) && conjFlag==0)?1:0;
        if(1 == flagRot){
            File file = new File(getExternalCacheDir(), "output.json");
            FileInputStream fis;
            int lengthToAddfrom;
            JSONArray obj = new JSONArray();
            try {
                fis =  new FileInputStream (file);
                byte[] input = new byte[fis.available()];
                while (fis.read(input) != -1) {}
                fis.close();
                json = new String(input, "UTF-8");
                try {
                    obj = new JSONArray(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            lengthToAddfrom = obj.length();

            int flagTask            = 1;
            int flagConcept         = 1;
            int flagVersion         = 1;
            int tempItr             = 0;
            int quesCnt             = 1;
            int optCnt              = 1;

            try{
                while(true){
                    if(flagVersion != items.getJSONObject(tempItr).getInt("Version")){
                        break;
                    }
                    if(flagTask != items.getJSONObject(tempItr).getInt("Task")){
                        quesCnt++;
                        flagTask = items.getJSONObject(tempItr).getInt("Task");
                    }
                    tempItr++;
                }

                tempItr     = 0;
                flagTask    = 1;

                while(true){
                    if(flagTask != items.getJSONObject(tempItr).getInt("Task")){
                        break;
                    }
                    if(flagConcept != items.getJSONObject(tempItr).getInt("Concept")){
                        optCnt++;
                        flagConcept = items.getJSONObject(tempItr).getInt("Concept");
                    }
                    tempItr++;
                }}
            catch (JSONException e) {
                e.printStackTrace();
            }
            conjItr = lengthToAddfrom*(optCnt*quesCnt)+rotAft;
            conjItr = (conjItr >= items.length())?0:conjItr;
        }
        return items;
    }

    public JSONArray getFromJSONMDCards(String conjFileName, String rotation ,int rotAft){
        JSONArray items = null;
        String json = null;
        InputStream is;
        try {
            is = getAssets().open(conjFileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        try {
            JSONArray obj = new JSONArray(json);
            items = obj;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        flagRotMD                  = ((rotation.equals("inclnoneinclrot") || rotation.equals("inclrot")) && MDConjFlag==0)?1:0;
        if(1 == flagRotMD){
            File file = new File(getExternalCacheDir(), "output.json");
            FileInputStream fis;
            int lengthToAddfrom;
            JSONArray obj = new JSONArray();
            try {
                fis =  new FileInputStream (file);
                byte[] input = new byte[fis.available()];
                while (fis.read(input) != -1) {}
                fis.close();
                json = new String(input, "UTF-8");
                try {
                    obj = new JSONArray(json);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            lengthToAddfrom = obj.length();

            int flagSet            = 0;
            int flagVersion         = 1;
            int tempItr             = 0;
            int quesCnt             = 0;

            try {
                while (true) {
                    if (flagVersion != items.getJSONObject(tempItr).getInt("Version")) {
                        break;
                    }
                    if (flagSet != items.getJSONObject(tempItr).getInt("Set")) {
                        quesCnt++;
                        flagSet = items.getJSONObject(tempItr).getInt("Set");
                    }
                    tempItr++;
                }

            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            MDConjItr = lengthToAddfrom*(quesCnt)+rotAft;
            MDConjItr = (MDConjItr >= items.length())?0:MDConjItr;
        }
        return items;
    }

    public void navigateQuestion(){
        currentIndex = currentIndex + 1;
        if(skippingQuestions.contains(currentIndex+1)){
            if(endOfQuestions()){
                try {
                    updateResponse(-1,"");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            navigateQuestion();
        }else {
            mFragmentManager = getSupportFragmentManager();
            if (currentIndex == MasterQuestionList.length()) {
                //Last question has been completed
                FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                mFragmentTransaction.replace(R.id.containerView, new QuesCompleteFragment()).commit();
            } else {
                try {
                    currentQuestion = MasterQuestionList.getJSONObject(currentIndex); //Get question
                    Log.d("questions", currentQuestion.getString("QuesText"));
                    Bundle args = new Bundle();
                    args.putInt("questionId", currentQuestion.getInt("QuesNum"));

                    String currQuesText = currentQuestion.getString("QuesText");
                    currQuesText = getCorrectedText(currQuesText,true);

                    JSONArray options = currentQuestion.getJSONArray("options");

                    int buff = currentQuestion.getInt("totalOpts");

                    for (int i = 0; i < currentQuestion.getInt("totalOpts"); i++) {
                        JSONObject opt = options.getJSONObject(i);

                        if(opt.getString("opsTextKey").matches("#fromDep_[0-9]+P") || opt.getString("opsTextKey").matches("#fromDep_[0-9]+S") || opt.getString("opsTextKey").matches("#fromUnDep_[0-9]+_[0-9]+") || opt.getString("opsTextKey").matches("#fromDep_[0-9]+_[0-9]+")){
                            MasterQuestionList.getJSONObject(currentIndex).put("options",RemoveJSONArray(MasterQuestionList.getJSONObject(currentIndex).getJSONArray("options"),opt.getString("opsTextKey")));
                            String[] optTextUpdated = getCorrectedText(opt.getString("opsTextKey"),false).split("_");
                            if(optTextUpdated.length>1 && !optTextUpdated[1].equals("NA")){
                                JSONObject oneOp = new JSONObject();

                                if(opt.getString("skipsinkey").equals("null"))
                                    oneOp.put("skipsinkey","null");
                                else
                                    oneOp.put("skipsinkey",opt.getJSONArray("skipsinkey"));
                                if(opt.getString("skipmulkey").equals("null"))
                                    oneOp.put("skipmulkey","null");
                                else
                                    oneOp.put("skipmulkey",opt.getJSONArray("skipmulkey"));

                                oneOp.put("del12depkey",opt.getString("del12depkey"));
                                oneOp.put("del12undepkey",opt.getString("del12undepkey"));
                                oneOp.put("opsNoKey",optTextUpdated[1]);
                                oneOp.put("opsTextKey",optTextUpdated[0]);
                                MasterQuestionList.getJSONObject(currentIndex).getJSONArray("options").put(oneOp);
                            }
                        }
                        else if(opt.getString("opsTextKey").matches("#fromUnDep_[0-9]+UNSINGALL")) { //populate with unselected opts in single choice
                            String toPut               = opt.getString("opsTextKey");
                                   toPut               = toPut.substring(0,(toPut.length()-9)) + "S";
                            String[] newOpts           = getCorrectedText(toPut,false).split(", ");

                            MasterQuestionList.getJSONObject(currentIndex).put("options",RemoveJSONArray(MasterQuestionList.getJSONObject(currentIndex).getJSONArray("options"),opt.getString("opsTextKey")));
                            for(int newOpItr = 0; newOpItr< newOpts.length; newOpItr++){
                                JSONObject oneOp = new JSONObject();

                                if(opt.getString("skipsinkey").equals("null"))
                                    oneOp.put("skipsinkey","null");
                                else
                                    oneOp.put("skipsinkey",opt.getJSONArray("skipsinkey"));
                                if(opt.getString("skipmulkey").equals("null"))
                                    oneOp.put("skipmulkey","null");
                                else
                                    oneOp.put("skipmulkey",opt.getJSONArray("skipmulkey"));

                                oneOp.put("del12depkey",opt.getString("del12depkey"));
                                oneOp.put("del12undepkey",opt.getString("del12undepkey"));

                                String[] updOptsLoc = newOpts[newOpItr].split("_");
                                if(updOptsLoc.length>1 && !updOptsLoc[1].equals("NA")){
                                    oneOp.put("opsNoKey",updOptsLoc[1]);
                                    oneOp.put("opsTextKey",updOptsLoc[0]);
                                    MasterQuestionList.getJSONObject(currentIndex).getJSONArray("options").put(oneOp);
                                    buff++;
                                }
                            }
                            buff--;
                        }
                        else if(opt.getString("opsTextKey").matches("#from_[0-9]+S_ALL")) { //populate with all opts in single choice
                            String toMatch             = opt.getString("opsTextKey").split("_")[1];
                            String[] newOpts           = getCorrectedText(("#fromUnDep_"+toMatch),false).split(", ");

                            MasterQuestionList.getJSONObject(currentIndex).put("options",RemoveJSONArray(MasterQuestionList.getJSONObject(currentIndex).getJSONArray("options"),opt.getString("opsTextKey")));
                            for(int newOpItr = 0; newOpItr< newOpts.length; newOpItr++){
                                JSONObject oneOp = new JSONObject();

                                if(opt.getString("skipsinkey").equals("null"))
                                    oneOp.put("skipsinkey","null");
                                else
                                    oneOp.put("skipsinkey",opt.getJSONArray("skipsinkey"));
                                if(opt.getString("skipmulkey").equals("null"))
                                    oneOp.put("skipmulkey","null");
                                else
                                    oneOp.put("skipmulkey",opt.getJSONArray("skipmulkey"));

                                oneOp.put("del12depkey",opt.getString("del12depkey"));
                                oneOp.put("del12undepkey",opt.getString("del12undepkey"));

                                String[] updOptsLoc = newOpts[newOpItr].split("_");
                                if(updOptsLoc.length>1 && !updOptsLoc[1].equals("NA")){
                                    oneOp.put("opsNoKey",updOptsLoc[1]);
                                    oneOp.put("opsTextKey",updOptsLoc[0]);
                                    MasterQuestionList.getJSONObject(currentIndex).getJSONArray("options").put(oneOp);
                                    buff++;
                                }
                            }
                            JSONObject oneOp = new JSONObject();
                            if(opt.getString("skipsinkey").equals("null"))
                                oneOp.put("skipsinkey","null");
                            else
                                oneOp.put("skipsinkey",opt.getJSONArray("skipsinkey"));
                            if(opt.getString("skipmulkey").equals("null"))
                                oneOp.put("skipmulkey","null");
                            else
                                oneOp.put("skipmulkey",opt.getJSONArray("skipmulkey"));

                            oneOp.put("del12depkey",opt.getString("del12depkey"));
                            oneOp.put("del12undepkey",opt.getString("del12undepkey"));

                            String[] updOptsLoc = getCorrectedText("#fromDep_"+toMatch,false).split("_");
                            if(updOptsLoc.length>1 && !updOptsLoc[1].equals("NA")){
                                oneOp.put("opsNoKey",updOptsLoc[1]);
                                oneOp.put("opsTextKey",updOptsLoc[0]);
                                MasterQuestionList.getJSONObject(currentIndex).getJSONArray("options").put(oneOp);
                                buff ++;
                            }
                            buff--;
                        }
                        else if(opt.getString("opsTextKey").matches("#fromUnDep_[0-9]+_ALL")) { //populate with unselected opts in Multiple choice
                            String toPut               = opt.getString("opsTextKey");
                            toPut                      = (toPut.substring(0,(toPut.length()-4)))+"A";
                            String[] newOpts           = getCorrectedText(toPut,false).split(", ");

                            MasterQuestionList.getJSONObject(currentIndex).put("options",RemoveJSONArray(MasterQuestionList.getJSONObject(currentIndex).getJSONArray("options"),opt.getString("opsTextKey")));
                            for(int newOpItr = 0; newOpItr< newOpts.length; newOpItr++){
                                JSONObject oneOp = new JSONObject();

                                if(opt.getString("skipsinkey").equals("null"))
                                    oneOp.put("skipsinkey","null");
                                else
                                    oneOp.put("skipsinkey",opt.getJSONArray("skipsinkey"));
                                if(opt.getString("skipmulkey").equals("null"))
                                    oneOp.put("skipmulkey","null");
                                else
                                    oneOp.put("skipmulkey",opt.getJSONArray("skipmulkey"));

                                oneOp.put("del12depkey",opt.getString("del12depkey"));
                                oneOp.put("del12undepkey",opt.getString("del12undepkey"));

                                String[] updOptsLoc = newOpts[newOpItr].split("_");
                                if(updOptsLoc.length>1 && !updOptsLoc[1].equals("NA")){
                                    oneOp.put("opsNoKey",updOptsLoc[1]);
                                    oneOp.put("opsTextKey",updOptsLoc[0]);
                                    MasterQuestionList.getJSONObject(currentIndex).getJSONArray("options").put(oneOp);
                                    buff++;
                                }
                            }
                            buff--;
                        }
                        else if(opt.getString("opsTextKey").matches("#fromDep_[0-9]+_ALL")) { //populate with selected opts in multiple choice
                            String toPass               = (opt.getString("opsTextKey"));
                            toPass                      = toPass.substring(0,toPass.length()-4) +"A" ;
                            String[] newOpts            = getCorrectedText(toPass, false).split(", ");

                            MasterQuestionList.getJSONObject(currentIndex).put("options",RemoveJSONArray(MasterQuestionList.getJSONObject(currentIndex).getJSONArray("options"),opt.getString("opsTextKey")));
                            for(int newOpItr = 0; newOpItr< newOpts.length; newOpItr++){
                                JSONObject oneOp = new JSONObject();
                                if(opt.getString("skipsinkey").equals("null"))
                                    oneOp.put("skipsinkey","null");
                                else
                                    oneOp.put("skipsinkey",opt.getJSONArray("skipsinkey"));
                                if(opt.getString("skipmulkey").equals("null"))
                                    oneOp.put("skipmulkey","null");
                                else
                                    oneOp.put("skipmulkey",opt.getJSONArray("skipmulkey"));

                                oneOp.put("del12depkey",opt.getString("del12depkey"));
                                oneOp.put("del12undepkey",opt.getString("del12undepkey"));

                                String[] updOptsLoc = newOpts[newOpItr].split("_");
                                if(updOptsLoc.length>1 && !updOptsLoc[1].equals("NA")){
                                    oneOp.put("opsNoKey",updOptsLoc[1]);
                                    oneOp.put("opsTextKey",updOptsLoc[0]);
                                    MasterQuestionList.getJSONObject(currentIndex).getJSONArray("options").put(oneOp);
                                    buff++;
                                }
                            }
                            buff--;
                        }
                        else if(opt.getString("opsTextKey").matches("#from_[0-9]+_ALL")) { //populate with all opts in multiple choice
                            String toMatch             = opt.getString("opsTextKey").split("_")[1];
                            String toPass              = "#fromUnDep_"+toMatch+"A";
                            String[] newOpts           = getCorrectedText(toPass, false).split(", ");

                            MasterQuestionList.getJSONObject(currentIndex).put("options",RemoveJSONArray(MasterQuestionList.getJSONObject(currentIndex).getJSONArray("options"),opt.getString("opsTextKey")));
                            for(int newOpItr = 0; newOpItr< newOpts.length; newOpItr++){
                                JSONObject oneOp = new JSONObject();

                                if(opt.getString("skipsinkey").equals("null"))
                                    oneOp.put("skipsinkey","null");
                                else
                                    oneOp.put("skipsinkey",opt.getJSONArray("skipsinkey"));
                                if(opt.getString("skipmulkey").equals("null"))
                                    oneOp.put("skipmulkey","null");
                                else
                                    oneOp.put("skipmulkey",opt.getJSONArray("skipmulkey"));

                                oneOp.put("del12depkey",opt.getString("del12depkey"));
                                oneOp.put("del12undepkey",opt.getString("del12undepkey"));

                                String[] updOptsLoc = newOpts[newOpItr].split("_");
                                if(updOptsLoc.length>1 && !updOptsLoc[1].equals("NA")){
                                    oneOp.put("opsNoKey",updOptsLoc[1]);
                                    oneOp.put("opsTextKey",updOptsLoc[0]);
                                    MasterQuestionList.getJSONObject(currentIndex).getJSONArray("options").put(oneOp);
                                    buff++;
                                }
                            }
                            String[] newOpts2           = getCorrectedText(("#fromDep_"+toMatch+"A"), false).split(", ");
                            for(int newOpItr = 0; newOpItr< newOpts2.length; newOpItr++){
                                JSONObject oneOp = new JSONObject();

                                if(opt.getString("skipsinkey").equals("null"))
                                    oneOp.put("skipsinkey","null");
                                else
                                    oneOp.put("skipsinkey",opt.getJSONArray("skipsinkey"));
                                if(opt.getString("skipmulkey").equals("null"))
                                    oneOp.put("skipmulkey","null");
                                else
                                    oneOp.put("skipmulkey",opt.getJSONArray("skipmulkey"));

                                oneOp.put("del12depkey",opt.getString("del12depkey"));
                                oneOp.put("del12undepkey",opt.getString("del12undepkey"));

                                String[] updOptsLoc = newOpts2[newOpItr].split("_");
                                if(updOptsLoc.length>1 && !updOptsLoc[1].equals("NA")){
                                    oneOp.put("opsNoKey",updOptsLoc[1]);
                                    oneOp.put("opsTextKey",updOptsLoc[0]);
                                    MasterQuestionList.getJSONObject(currentIndex).getJSONArray("options").put(oneOp);
                                    buff++;
                                }
                            }
                            buff--;
                        }
                        else{
                            String optTextUpdated = getCorrectedText(opt.getString("opsTextKey"),true);
                            MasterQuestionList.getJSONObject(currentIndex).put("options",RemoveJSONArray(MasterQuestionList.getJSONObject(currentIndex).getJSONArray("options"),opt.getString("opsTextKey")));
                            JSONObject oneOp = new JSONObject();

                            if(opt.getString("skipsinkey").equals("null"))
                                oneOp.put("skipsinkey","null");
                            else
                                oneOp.put("skipsinkey",opt.getJSONArray("skipsinkey"));
                            if(opt.getString("skipmulkey").equals("null"))
                                oneOp.put("skipmulkey","null");
                            else
                                oneOp.put("skipmulkey",opt.getJSONArray("skipmulkey"));

                            oneOp.put("del12depkey",opt.getString("del12depkey"));
                            oneOp.put("del12undepkey",opt.getString("del12undepkey"));
                            oneOp.put("opsNoKey",opt.getString("opsNoKey"));
                            oneOp.put("opsTextKey",optTextUpdated);

                            MasterQuestionList.getJSONObject(currentIndex).getJSONArray("options").put(oneOp);
                        }

                    }
                    MasterQuestionList.getJSONObject(currentIndex).put("totalOpts", buff);
                    MasterQuestionList.getJSONObject(currentIndex).put("QuesText", currQuesText);
                    currentQuestion = MasterQuestionList.getJSONObject(currentIndex); //Get question again after corrections
                    //System.out.println(currentQuestion.getJSONArray("options"));
                    switch (currentQuestion.getString("QuesType").split("_")[0]) {
                        case "Single Choice": {
                            conjFlag = 0;
                            MDConjFlag = 0;
                            RadiobuttonQuestionFragment radioQ = new RadiobuttonQuestionFragment();
                            radioQ.setArguments(args);
                            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                            mFragmentTransaction.replace(R.id.containerView, radioQ).commit();
                            break;
                        }
                        case "Multiple Choice": {
                            conjFlag = 0;
                            MDConjFlag = 0;
                            CheckboxQuestionFragment checkboxQ = new CheckboxQuestionFragment();
                            checkboxQ.setArguments(args);
                            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                            mFragmentTransaction.replace(R.id.containerView, checkboxQ).commit();
                            break;
                        }
                        case "Plain Text": {
                            conjFlag = 0;
                            MDConjFlag = 0;
                            TextBlankQuestionFragment blankQ = new TextBlankQuestionFragment();
                            blankQ.setArguments(args);
                            FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                            mFragmentTransaction.replace(R.id.containerView, blankQ).commit();
                            break;
                        }
                        case "Image based": {
                            conjFlag = 0;
                            MDConjFlag = 0;
                            switch (currentQuestion.getString("blanks")) {
                                case "Blank choice": {
                                    TextBlankQuestionFragment blankQ = new TextBlankQuestionFragment();
                                    blankQ.setArguments(args);
                                    FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                                    mFragmentTransaction.replace(R.id.containerView, blankQ).commit();
                                    break;
                                }
                                case "Single choice": {
                                    RadiobuttonQuestionFragment radioQ = new RadiobuttonQuestionFragment();
                                    radioQ.setArguments(args);
                                    FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                                    mFragmentTransaction.replace(R.id.containerView, radioQ).commit();
                                    break;
                                }
                                case "Multiple choice": {
                                    CheckboxQuestionFragment checkboxQ = new CheckboxQuestionFragment();
                                    checkboxQ.setArguments(args);
                                    FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                                    mFragmentTransaction.replace(R.id.containerView, checkboxQ).commit();
                                    break;
                                }
                                case "": {
                                    VideoQuestionFragment videoQ = new VideoQuestionFragment();
                                    videoQ.setArguments(args);
                                    FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                                    mFragmentTransaction.replace(R.id.containerView, videoQ).commit();
                                    break;
                                }
                                default: {
                                    break;
                                }
                            }
                            break;
                        }
                        case "Video based": {
                            conjFlag = 0;
                            MDConjFlag = 0;
                            switch (currentQuestion.getString("blanks")) {
                                case "Blank choice": {
                                    TextBlankQuestionFragment blankQ = new TextBlankQuestionFragment();
                                    blankQ.setArguments(args);
                                    FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                                    mFragmentTransaction.replace(R.id.containerView, blankQ).commit();
                                    break;
                                }
                                case "Single choice": {
                                    RadiobuttonQuestionFragment radioQ = new RadiobuttonQuestionFragment();
                                    radioQ.setArguments(args);
                                    FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                                    mFragmentTransaction.replace(R.id.containerView, radioQ).commit();
                                    break;
                                }
                                case "Multiple choice": {
                                    CheckboxQuestionFragment checkboxQ = new CheckboxQuestionFragment();
                                    checkboxQ.setArguments(args);
                                    FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                                    mFragmentTransaction.replace(R.id.containerView, checkboxQ).commit();
                                    break;
                                }
                                case "": {
                                    VideoQuestionFragment videoQ = new VideoQuestionFragment();
                                    videoQ.setArguments(args);
                                    FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                                    mFragmentTransaction.replace(R.id.containerView, videoQ).commit();
                                    break;
                                }
                                default: {
                                    break;
                                }
                            }
                            break;
                        }
                        case "Conjoint": {
                            JSONArray conjArr = getFromJSONCards(currentQuestion.getString("file"),currentQuestion.getString("QuesType").split("_")[1] ,Integer.parseInt(currentQuestion.getString("QuesType").split("_")[2]));

                            String VersionNo        = "";
                            String TaskNo           = "";
                            String ConceptNo        = "";

                            int tempItr             = 0;

                            int flagTask            = 1;
                            int flagConcept         = 1;
                            int flagVersion         = 1; //get from get json cards

                            int locCountTotalConjQues = 1; //max value of countTotalConjQues

                            while(true){
                                if(flagVersion != conjArr.getJSONObject(tempItr).getInt("Version")){
                                    break;
                                }
                                if(flagTask != conjArr.getJSONObject(tempItr).getInt("Task")){
                                    locCountTotalConjQues++;
                                    flagTask = conjArr.getJSONObject(tempItr).getInt("Task");
                                }
                                tempItr++;
                            }

                            tempItr                 = 0;
                            flagTask                = 1;
                            int countPerConjOpt     = 1; //total number of options per conjoint's specific question
                            while(true){
                                if(flagTask != conjArr.getJSONObject(tempItr).getInt("Task")){
                                    break;
                                }
                                if(flagConcept != conjArr.getJSONObject(tempItr).getInt("Concept")){
                                    countPerConjOpt++;
                                    flagConcept = conjArr.getJSONObject(tempItr).getInt("Concept");
                                }
                                tempItr++;
                            }

                            if(conjFlag == 0) {
                                final int[] verToStartFrom = {-1};

                                AlertDialog.Builder builderVersion = new AlertDialog.Builder(this);
                                builderVersion.setTitle("Please enter the version");
                                builderVersion.setCancelable(true);

                                final EditText inpVer = new EditText(this);
                                inpVer.setInputType(InputType.TYPE_CLASS_NUMBER);
                                builderVersion.setView(inpVer);
                                final int finalLocCountTotalConjQues = locCountTotalConjQues;
                                final int finalCountPerConjOpt = countPerConjOpt;
                                builderVersion.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(!inpVer.getText().toString().equals("")){
                                            verToStartFrom[0] = Integer.parseInt(inpVer.getText().toString());
                                            conjItr = (verToStartFrom[0]-1)* finalLocCountTotalConjQues* finalCountPerConjOpt;
                                        }
                                    }
                                });

                                builderVersion.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                AlertDialog alertVer = builderVersion.create();
                                alertVer.show();

                                conjFlag            = 1;
                                MDConjFlag          = 0;

                                RankQuestionFragment Conj = new RankQuestionFragment();
                                Conj.setArguments(args);
                                FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                                mFragmentTransaction.replace(R.id.containerView, Conj).commit();

                            }else{

                                countTotalConjQues++;

                                String inclWut = currentQuestion.getString("QuesType").split("_")[1];
                                int flag = (inclWut.equals("inclnone") || inclWut.equals("inclnoneinclrot")) ? 1 : 0;
//modify options and use as single options select

                                for (int i = 0; i < countPerConjOpt; i++) {
                                    JSONObject conjJsonOpt = conjArr.getJSONObject(conjItr);
                                    Iterator<String> KEYS = conjJsonOpt.keys();
                                    String conjStringopt = "";
                                    int flagTemp = 0;

                                    while (KEYS.hasNext()) {
                                        String key = KEYS.next();
                                        String value = conjJsonOpt.getString(key);

                                        if(key.equals("Task")){
                                            TaskNo = value;
                                        }else if(key.equals("Version")){
                                            VersionNo = value;
                                        }else if(key.equals("Concept")){
                                            ConceptNo = value;
                                        }else{
                                            if(flagTemp == 0){
                                                conjStringopt = conjStringopt + key + ":" + getCorrectedText(value, true);
                                                flagTemp = 1;
                                            }else{
                                                conjStringopt = conjStringopt + "\n" + key + ":" + getCorrectedText(value, true);
                                            }
                                        }
                                    }
                                    JSONObject oneOpConj = new JSONObject();
                                    oneOpConj.put("opsNoKey", ConceptNo);
                                    oneOpConj.put("opsTextKey", conjStringopt);
                                    oneOpConj.put("opsVerID", VersionNo);
                                    oneOpConj.put("opsTaskID", TaskNo);
                                    oneOpConj.put("skipsinkey", "null");
                                    oneOpConj.put("skipmulkey", "null");
                                    oneOpConj.put("del12depkey", "N");
                                    oneOpConj.put("del12undepkey", "N");

                                    MasterQuestionList.getJSONObject(currentIndex).getJSONArray("options").put(i, oneOpConj);
                                    conjItr++;
                                }

                                MasterQuestionList.getJSONObject(currentIndex).put("totalOpts", countPerConjOpt);
                                currentQuestion.put("QuesText", currQuesText);
                                if (1 == flag) {
                                    JSONObject oneOpConj = new JSONObject();
                                    oneOpConj.put("opsNoKey", ConceptNo + 1);
                                    oneOpConj.put("opsTextKey", "None of these");
                                    oneOpConj.put("opsVerID", VersionNo);
                                    oneOpConj.put("opsTaskID", TaskNo);
                                    oneOpConj.put("skipsinkey", "null");
                                    oneOpConj.put("skipmulkey", "null");
                                    oneOpConj.put("del12depkey", "N");
                                    oneOpConj.put("del12undepkey", "N");
                                    MasterQuestionList.getJSONObject(currentIndex).getJSONArray("options").put(countPerConjOpt, oneOpConj);
                                    MasterQuestionList.getJSONObject(currentIndex).put("totalOpts", countPerConjOpt + 1);
                                }

                                OrderQuestionFragment orderQ = new OrderQuestionFragment();
                                orderQ.setArguments(args);
                                FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                                mFragmentTransaction.replace(R.id.containerView, orderQ).commit();
                            }

                            //reset all conjoint variables after the end
                            if(locCountTotalConjQues == countTotalConjQues){
                                conjFlag             = 0;
                                countTotalConjQues   = 0;
                                conjItr              = 0;
                            }
                            break;
                        }
                        case "Max Diff Conjoint": {

                            JSONArray conjArr = getFromJSONMDCards(currentQuestion.getString("file"),currentQuestion.getString("QuesType").split("_")[1] ,Integer.parseInt(currentQuestion.getString("QuesType").split("_")[2]));

                            String VersionNo       = "";
                            String SetNo           = "";

                            int tempItr            = 0;

                            int flagSet            = 0;
                            int flagVersion        = 1; //get from get json cards

                            int locCountTotalConjQues = 0; //max value of countTotalConjQues

                            while(true){
                                if(flagVersion != conjArr.getJSONObject(tempItr).getInt("Version")){
                                    break;
                                }
                                if(flagSet != conjArr.getJSONObject(tempItr).getInt("Set")){
                                    locCountTotalConjQues++;
                                    flagSet = conjArr.getJSONObject(tempItr).getInt("Set");
                                }
                                tempItr++;
                            }

                            if(MDConjFlag == 0) {
                                final int[] verToStartFrom = {-1};

                                AlertDialog.Builder builderVersion = new AlertDialog.Builder(this);
                                builderVersion.setTitle("Please enter the version");
                                builderVersion.setCancelable(true);

                                final EditText inpVer = new EditText(this);
                                inpVer.setInputType(InputType.TYPE_CLASS_NUMBER);
                                builderVersion.setView(inpVer);
                                final int finalLocCountTotalConjQues = locCountTotalConjQues;
                                builderVersion.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        if(!inpVer.getText().toString().equals("")){
                                            verToStartFrom[0] = Integer.parseInt(inpVer.getText().toString());
                                            MDConjItr = (verToStartFrom[0]-1)* finalLocCountTotalConjQues;
                                        }
                                    }
                                });

                                builderVersion.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });

                                AlertDialog alertVer = builderVersion.create();
                                alertVer.show();

                                conjFlag            = 0;
                                MDConjFlag          = 1;

                                RankQuestionFragment maxDiffConj = new RankQuestionFragment();
                                maxDiffConj.setArguments(args);
                                FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                                mFragmentTransaction.replace(R.id.containerView, maxDiffConj).commit();

                            }else {

                                countTotalMDConjQues++;
                                //System.out.println(locCountTotalConjQues+"---"+countTotalMDConjQues);
                                int countPerConjOpt = 4; //total number of options per conjoint's specific question is 4 here
                                String inclWut = currentQuestion.getString("QuesType").split("_")[1];
                                int flag = (inclWut.equals("inclnone") || inclWut.equals("inclnoneinclrot")) ? 1 : 0;
//modify options and use as single options select

                                JSONObject conjJsonOpt = conjArr.getJSONObject(MDConjItr);
                                Iterator<String> KEYS = conjJsonOpt.keys();
                                String conjStringopt[] = new String[4];
                                //int locCount = 0;
                                int i = 0;

                                while (KEYS.hasNext()) {
                                    String key = KEYS.next();
                                    String value = conjJsonOpt.getString(key);

                                    if(key.equals("Set")){
                                        SetNo = value;
                                    }else if(key.equals("Version")){
                                        VersionNo = value;
                                    }else{
                                        conjStringopt[i] = key + ": " + getCorrectedText(value, true);
                                        i++;
                                    }
                                }

                                for(int k=0; k<4; k++){
                                    JSONObject oneOpConj = new JSONObject();
                                    oneOpConj.put("opsNoKey", k + 1);
                                    oneOpConj.put("opsSetID", SetNo);
                                    oneOpConj.put("opsTextKey", conjStringopt[k]);
                                    oneOpConj.put("opsVerID", VersionNo);
                                    oneOpConj.put("skipsinkey", "null");
                                    oneOpConj.put("skipmulkey", "null");
                                    oneOpConj.put("del12depkey", "N");
                                    oneOpConj.put("del12undepkey", "N");

                                    MasterQuestionList.getJSONObject(currentIndex).getJSONArray("options").put(k, oneOpConj);
                                }

                                MDConjItr++;


                                MasterQuestionList.getJSONObject(currentIndex).put("totalOpts", countPerConjOpt);
                                currentQuestion.put("QuesText", currQuesText);
                                if (1 == flag) {
                                    JSONObject oneOpConj = new JSONObject();
                                    oneOpConj.put("opsNoKey", i + 1);
                                    oneOpConj.put("opsTextKey", "None of these");
                                    oneOpConj.put("opsVerID", VersionNo);
                                    oneOpConj.put("opsSetID", SetNo);
                                    oneOpConj.put("skipsinkey", "null");
                                    oneOpConj.put("skipmulkey", "null");
                                    oneOpConj.put("del12depkey", "N");
                                    oneOpConj.put("del12undepkey", "N");
                                    MasterQuestionList.getJSONObject(currentIndex).getJSONArray("options").put(countPerConjOpt, oneOpConj);
                                    MasterQuestionList.getJSONObject(currentIndex).put("totalOpts", countPerConjOpt + 1);
                                }

                                MaxDiffConjoint maxDiffConj = new MaxDiffConjoint();
                                maxDiffConj.setArguments(args);
                                FragmentTransaction mFragmentTransaction = mFragmentManager.beginTransaction();
                                mFragmentTransaction.replace(R.id.containerView, maxDiffConj).commit();
                            }
                            //reset all conjoint variables after the end
                            if(locCountTotalConjQues == countTotalMDConjQues){
                                MDConjFlag             = 0;
                                countTotalMDConjQues   = 0;
                                MDConjItr              = 0;
                            }
                            break;
                        }
                        default:
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(conjFlag == 1 || MDConjFlag == 1){
                    currentIndex = currentIndex - 1; //retain conjoint
                }
            }
        }
    }
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public void updateResponse(int qId, String response) throws JSONException {
        //Find question for that id and add response property

        for (int i = 0; i < MasterQuestionList.length(); i++) {
            try {
                JSONObject jo_inside = MasterQuestionList.getJSONObject(i);
                if(jo_inside.getInt("QuesNum") == qId)
                {
                    JSONObject respInnrJson =   new JSONObject();
                    String qNoResp = String.valueOf(qId);
                    String ansResp = response;
                    if(jo_inside.getString("QuesType").split("_")[0].equals("Conjoint")){
                        qNoResp =  MasterQuestionList.getJSONObject(qId-1).getJSONArray("options").getJSONObject(0).getString("opsVerID")+"-"+MasterQuestionList.getJSONObject(qId-1).getJSONArray("options").getJSONObject(0).getString("opsTaskID");
                    }

                    if(jo_inside.getString("QuesType").split("_")[0].equals("Max Diff Conjoint")){
                        qNoResp =  MasterQuestionList.getJSONObject(qId-1).getJSONArray("options").getJSONObject(0).getString("opsVerID")+"-"+MasterQuestionList.getJSONObject(qId-1).getJSONArray("options").getJSONObject(0).getString("opsSetID");
                    }

                    if((MasterQuestionList.getJSONObject(qId-1).getString("QuesType").equals("Multiple Choice")) || (MasterQuestionList.getJSONObject(qId - 1).getString("blanks").equals("Multiple choice")))
                        ansResp = Arrays.asList(cleanLastTokenFromStringArr(response.split(","))).toString(); //Add response String to response key

                    if(qId==1){
                        respInnrJson.put("Serial Number",ansResp);
                    }else{
                        respInnrJson.put(qNoResp,ansResp);
                    }

                    ResponseList.put(respInnrJson);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        ArrayList<Integer> toSkipQues = new ArrayList<Integer>();
        if(qId>0) {
            if ((MasterQuestionList.getJSONObject(qId - 1).getString("QuesType").equals("Multiple Choice")) || (MasterQuestionList.getJSONObject(qId - 1).getString("blanks").equals("Multiple choice"))) {
                String splitedResponse[] = response.split(",");
                int innCount = 1;
                //option for a question is like optioncode_selPref => save all in dep if dep flag is on and selected in response
                for (int wholeopt = 0; wholeopt < MasterQuestionList.getJSONObject(qId - 1).getInt("totalOpts"); wholeopt++) {
                    for (int opSelItr = 0; opSelItr < splitedResponse.length; opSelItr++) {
                        if (MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("del12depkey").equals("Y")) {
                            String tempSelOpts[] = splitedResponse[opSelItr].split("_");
                            String last = tempSelOpts[1]; //selPref
                            String lastSec = tempSelOpts[0]; //optCode or text in case of others
                            if (MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("opsNoKey").equals(lastSec)) {
                                saved_response_further_dep[qId][Integer.parseInt(last)] = MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("opsTextKey")+"_"+lastSec;
                            }else if(!lastSec.matches("\\d+")){
                                saved_response_further_dep[qId][Integer.parseInt(last)] = lastSec+"_NA";
                            }
                        }
                    }

                    if (MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("del12undepkey").equals("Y")) {
                        if (!Arrays.asList(cleanLastTokenFromStringArr(splitedResponse)).contains(MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("opsNoKey"))) {
                            if(!MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("opsTextKey").equals("#others")){
                                saved_response_further_undep[qId][innCount] = MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("opsTextKey")+"_"+MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("opsNoKey");
                                innCount++;
                            }
                        }
                    }

                    if(!MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("opsNoKey").equals("")){
                        if (Arrays.asList(cleanLastTokenFromStringArr(splitedResponse)).contains(MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("opsNoKey"))) {
                            if (!MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("skipsinkey").equals("null")) {
                                for (int i = 0; i < MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getJSONArray("skipsinkey").length(); i++) {
                                    String tempStr[] = MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getJSONArray("skipsinkey").getString(i).split("_");
                                    toSkipQues.add(Integer.parseInt(tempStr[tempStr.length - 1]));
                                }
                            }
                        }
                    }else{
                        if ((MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("opsTextKey").equals("#others")) && (!MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("skipsinkey").equals("null"))){
                            for(int j=0; j<splitedResponse.length; j++){
                                if(!(cleanLastTokenFromStringArr(splitedResponse))[j].matches("\\d+")){
                                    for (int i = 0; i < MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getJSONArray("skipsinkey").length(); i++) {
                                        String tempStr[] = MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getJSONArray("skipsinkey").getString(i).split("_");
                                        toSkipQues.add(Integer.parseInt(tempStr[tempStr.length - 1]));
                                    }
                                }
                            }
                        }
                    }
                }

                if (skipMulActivated(cleanLastTokenFromStringArr(splitedResponse), MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options"))) {
                    for (int wholeopt = 0; wholeopt < MasterQuestionList.getJSONObject(qId - 1).getInt("totalOpts"); wholeopt++) {
                        if (!MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("skipmulkey").equals("null")) {
                            for (int i = 0; i < MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getJSONArray("skipmulkey").length(); i++) {
                                String tempStr[] = MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getJSONArray("skipmulkey").getString(i).split("_");
                                toSkipQues.add(Integer.parseInt(tempStr[tempStr.length - 1]));
                            }
                        }
                    }
                }
            } else if ((MasterQuestionList.getJSONObject(qId - 1).getString("QuesType").equals("Single Choice")) || (MasterQuestionList.getJSONObject(qId - 1).getString("blanks").equals("Single choice"))) {
                int innCount = 1;
                for (int wholeopt = 0; wholeopt < MasterQuestionList.getJSONObject(qId - 1).getInt("totalOpts"); wholeopt++) {
                    if ((MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("del12depkey").equals("Y"))) {
                        if((response.matches("\\d+")) && (MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("opsNoKey").equals(response))){
                            saved_response_further_dep[qId][0] = MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("opsTextKey")+"_"+MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("opsNoKey");
                        }
                        else if((!response.matches("\\d+")) && (MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("opsTextKey").equals("#others"))){
                            saved_response_further_dep[qId][0] = response+"_NA";
                        }
                    }
                    if ((MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("del12undepkey").equals("Y")) && !(MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("opsNoKey").equals(response))) {
                        if(!MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("opsTextKey").equals("#others")){
                            saved_response_further_undep[qId][innCount] = MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("opsTextKey")+"_"+MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("opsNoKey");
                            innCount++;
                        }
                    }

                    if (((response.matches("\\d+")) && (response.equals(MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("opsNoKey")))) || ((!response.matches("\\d+")) && (("#others").equals(MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("opsTextKey"))))) {
                        if (!MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getString("skipsinkey").equals("null")) {
                            for (int i = 0; i < MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getJSONArray("skipsinkey").length(); i++) {
                                String tempStr[] = MasterQuestionList.getJSONObject(qId - 1).getJSONArray("options").getJSONObject(wholeopt).getJSONArray("skipsinkey").getString(i).split("_");
                                toSkipQues.add(Integer.parseInt(tempStr[tempStr.length - 1]));
                            }
                        }
                    }
                }
            } else if ((MasterQuestionList.getJSONObject(qId - 1).getString("QuesType").equals("Plain Text")) || (MasterQuestionList.getJSONObject(qId - 1).getString("blanks").equals("Blank choice"))) {
                saved_response_further_dep[qId][0] = response+"_NA";
            }
        }
        Set<Integer> temphs = new HashSet<>();
        temphs.addAll(toSkipQues);
        toSkipQues.clear();
        toSkipQues.addAll(temphs);

        skippingQuestions.addAll(toSkipQues);

        Set<Integer> tempha = new HashSet<>();
        tempha.addAll(skippingQuestions);
        skippingQuestions.clear();
        skippingQuestions.addAll(tempha);

        if(endOfQuestions()){
            File file = new File(getExternalCacheDir(), "output.json");

            FileInputStream fis;
            String json = "";
            JSONArray obj = new JSONArray();
            try {
                fis =  new FileInputStream (file);
                byte[] input = new byte[fis.available()];
                while (fis.read(input) != -1) {}
                fis.close();
                json = new String(input, "UTF-8");
                obj = new JSONArray(json);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            int lengthToAddfrom = obj.length();

            FileOutputStream f = null;
            try {
                f = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            JSONObject toput = new JSONObject();
            String res = ResponseList.toString();

            toput.put(String.valueOf(obj.length()+1),res);

            obj.put(lengthToAddfrom,toput);

            try {
                f.write(obj.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                f.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.d("question",ResponseList.toString());
    }
    public boolean skipMulActivated(String[] resp, JSONArray optArr) throws JSONException {
        boolean ret         = false;
        String flagOthers   = "";

        for(int optItr=0; optItr < optArr.length(); optItr++){
            for(int respItr=0; respItr<resp.length; respItr++){
                if((!resp[respItr].matches("\\d+")) && (optArr.getJSONObject(optItr).getString("opsTextKey").equals("#others"))){
                    flagOthers = optArr.getJSONObject(optItr).getString("skipmulkey");
                }
            }
        }

        for(int optItr=0; optItr < optArr.length(); optItr++){
            if(!optArr.getJSONObject(optItr).getString("skipmulkey").equals("null")){
                if(Arrays.asList(resp).contains(optArr.getJSONObject(optItr).getString("opsNoKey"))){
                    ret = true;
                }
                else{
                    if(optArr.getJSONObject(optItr).getString("opsTextKey").equals("#others") && !flagOthers.equals("null")){
                        ret = true;
                    }else{
                        return false;
                    }
                }
            }
        }

        return ret;
    }
    public String[] cleanLastTokenFromStringArr(String[] strArr){
        String[] retArr = new String[strArr.length];
        for(int strItr=0; strItr < strArr.length; strItr++){
            String tempArr[] = strArr[strItr].split("_");
            String tempStr   = "";
            for(int innItr=0; innItr < tempArr.length-1; innItr++){
                if(innItr>0)
                    tempStr = tempStr.concat("_").concat(tempArr[innItr]);
                else
                    tempStr = tempStr.concat(tempArr[innItr]);
            }
            retArr[strItr] = tempStr;
        }
        return retArr;
    }
    public JSONObject getCurrentQuestion(){
        return currentQuestion;
    }
    /**Helper function to return question by id**/
    public JSONObject findQuestion(int qId){
        for (int i = 0; i < MasterQuestionList.length(); i++) {
            try {
                JSONObject jo_inside = MasterQuestionList.getJSONObject(i);
                if(jo_inside.getInt("id") == qId)
                    return jo_inside;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public static JSONArray RemoveJSONArray(JSONArray jarray,String textKey) {
        JSONArray Njarray=new JSONArray();
        try{
            for(int i=0;i<jarray.length();i++){
                if(!jarray.getJSONObject(i).getString("opsTextKey").equals(textKey))
                    Njarray.put(jarray.get(i));
            }
        }catch (Exception e){e.printStackTrace();}
        return Njarray;
    }
}