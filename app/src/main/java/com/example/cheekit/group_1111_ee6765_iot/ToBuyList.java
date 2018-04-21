package com.example.cheekit.group_1111_ee6765_iot;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ToBuyList extends Fragment {

    private Button okBtn;
    private Button cancelBtn;
    int response_Code;
    String message_response = new String();
    String commandType = new String();
    String commandMsg = new String();
    Spinner categoryInput;
    Spinner itemInput;
    Spinner methodInput;
    Spinner quantityInput;
    ArrayList<String> categoryArray;
    ArrayList<String> itemArray;
    ArrayList<String> quantityArray;
    TableRow catRow;
    TableRow quantityRow;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.tobuylist, container, false);
        okBtn = (Button)view.findViewById(R.id.okButton); // Initializing the button
        cancelBtn = (Button)view.findViewById(R.id.cancelButton); // Initializing the button
        categoryInput = (Spinner) view.findViewById(R.id.category);
        methodInput = (Spinner) view.findViewById(R.id.method);
        itemInput = (Spinner) view.findViewById(R.id.item);
        catRow=(TableRow) view.findViewById(R.id.itemCatRow);
        quantityRow=(TableRow) view.findViewById(R.id.quantityRow);
        quantityInput = (Spinner) view.findViewById(R.id.quantity);

        quantityArray = new ArrayList<String>();
        for(int i=0;i<100;i++)
            quantityArray.add(Integer.toString(i+1));
        ArrayAdapter<String> adp = new ArrayAdapter<String> (getActivity(),android.R.layout.simple_spinner_dropdown_item,quantityArray);
        quantityInput.setAdapter(adp);

        methodInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
//                itemInput.setEnabled(Boolean.FALSE);
//                itemInput.setSelection(0);
                if(position==0){
                    catRow.setVisibility(View.GONE);
                    quantityRow.setVisibility(View.GONE);
                    commandType="getrecipe";
                    commandMsg="";
                    try {
                        Object result = new SendPostRequest().execute().get();
                    }
                    catch(Exception e){
                    }
                }
                else if(position==1){
                    catRow.setVisibility(View.GONE);
                    quantityRow.setVisibility(View.VISIBLE);
                    commandType="getitem";
                    commandMsg="";
                    try {
                        Object result = new SendPostRequest().execute().get();
                    }
                    catch(Exception e){
                    }

                }
                if(position==2) {
//                    itemInput.setEnabled(Boolean.TRUE);
                    catRow.setVisibility(View.VISIBLE);
                    quantityRow.setVisibility(View.VISIBLE);
                    commandType="retrievecat";
                    commandMsg="";
                    try {
                        Object result = new SendPostRequest().execute().get();
                    }
                    catch(Exception e){
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {

                // your code here
            }

        });
        methodInput.setSelection(0);
        //Sample String ArrayList
//        commandType="retrievecat";
//        commandMsg="";
//        try {
//            Object result = new SendPostRequest().execute().get();
//        }
//        catch(Exception e){
//        }

        categoryInput.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                commandType="retrieveitemfromcat";
                itemInput.setEnabled(Boolean.FALSE);
                commandMsg=categoryArray.get(position);
                try {
                    Object result = new SendPostRequest().execute().get();
                }
                catch(Exception e){
                }
                Toast.makeText(getActivity().getApplicationContext(), Integer.toString(position), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        // when pressed the button, set the output box message
        View.OnClickListener okBtnSend = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change text of TextView (output)
                commandType="addToBuyList";
                commandMsg="";
                try {
                    Object result = new SendPostRequest().execute().get();
                }
                catch(Exception e){
                }
//                itemTextInput.setText("");
                ((MainActivity)getActivity()).displaySelectedScreen(R.id.homePage);
            }
        };
        okBtn.setOnClickListener(okBtnSend);

        // when pressed the button, set the output box message
        View.OnClickListener cancelBtnSend = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).displaySelectedScreen(R.id.homePage);
            }
        };
        cancelBtn.setOnClickListener(cancelBtnSend);
        return view;
    }
    // send the request
    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try{
                URL url = new URL("http://ec2-34-204-93-26.compute-1.amazonaws.com/");  // set the url to the board's current url
                JSONObject postDataParams = new JSONObject();

                postDataParams.put("type",commandType);
                if(commandType=="addToBuyList")
                {
                    JSONObject msg = new JSONObject();
                    msg.put("method",Integer.toString(methodInput.getSelectedItemPosition()));
                    msg.put("itemname",itemInput.getSelectedItem());
                    if(methodInput.getSelectedItemPosition()==0)
                        msg.put("quantity","0");
                    else
                        msg.put("quantity",quantityInput.getSelectedItem());
                    postDataParams.put("msg",msg);
                }
                else {
                    postDataParams.put("msg", commandMsg);
                }

                postDataParams.put("userid",((MainActivity)getActivity()).getUserId());
                Log.e("params",postDataParams.toString());


                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(7000 /* milliseconds */);
                conn.setConnectTimeout(7000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                //conn.setRequestProperty("Content-Type", "application/json");
                //conn.setRequestProperty("Accept", "application/json");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                OutputStream os = conn.getOutputStream();

                OutputStreamWriter wr= new OutputStreamWriter(conn.getOutputStream());
                wr.write(postDataParams.toString());
                wr.flush();
                wr.close();
                os.close();

                //get the response message
                message_response = postDataParams.toString();
                StringBuffer sb3 = new StringBuffer("");
                //message_response2 = sb3.append(conn.getContent(string)).toString();
                int responseCode=conn.getResponseCode();
                response_Code=responseCode;

                // the input message from the board
                BufferedReader in=new BufferedReader(new
                        InputStreamReader(
                        conn.getInputStream()));
                StringBuffer sb = new StringBuffer("");
                String line="";
                while((line = in.readLine()) != null) {
                    sb.append(line);
                    break;
                }
                return sb.toString();
            }
            catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }

        }

        // set the response message and display it
        @Override
        protected void onPostExecute(String result) {
            if(commandType=="retrievecat")
            {
                String[] splited=result.split(",");
                categoryArray = new ArrayList<String>();
                for(int i=0;i<splited.length;i++)
                    categoryArray.add(splited[i]);
                ArrayAdapter<String> adp = new ArrayAdapter<String> (getActivity(),android.R.layout.simple_spinner_dropdown_item,categoryArray);
                categoryInput.setAdapter(adp);
            }
            if(commandType=="retrieveitemfromcat")
            {
                String[] splited=result.split(",");
                itemArray = new ArrayList<String>();
                for(int i=0;i<splited.length;i++)
                    itemArray.add(splited[i]);
                ArrayAdapter<String> adp = new ArrayAdapter<String> (getActivity(),android.R.layout.simple_spinner_dropdown_item,itemArray);
                itemInput.setAdapter(adp);
            }
            if(commandType=="getitem" || commandType=="getrecipe")
            {
                String[] splited=result.split(",");
                itemArray = new ArrayList<String>();
                if(splited.length>1){
                    for(int i=0;i<splited.length;) {
                        itemArray.add(splited[i]);
                        i=i+2;
                    }
                }
                ArrayAdapter<String> adp = new ArrayAdapter<String> (getActivity(),android.R.layout.simple_spinner_dropdown_item,itemArray);
                itemInput.setAdapter(adp);
            }
            itemInput.setEnabled(Boolean.TRUE);
        }


    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Add to To-Buy List");
    }

}