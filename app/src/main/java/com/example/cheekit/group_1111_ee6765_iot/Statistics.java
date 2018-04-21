package com.example.cheekit.group_1111_ee6765_iot;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.*;

public class Statistics extends Fragment {
    PieChart pieChart ;
    ArrayList<Entry> entries ;
    ArrayList<String> PieEntryLabels ;
    PieDataSet pieDataSet ;
    PieData pieData ;

    BarChart barChart ;
    ArrayList<BarEntry> BARENTRY ;
    ArrayList<String> BarEntryLabels ;
    BarDataSet Bardataset ;
    BarData BARDATA ;

    int response_Code;
    Dictionary Category = new Hashtable();
    String message_response = new String();
    String cat_price = new String();
    List<String> catName = new ArrayList<String>();
    List<Float> catPrice = new ArrayList<Float>();

    String month_price = new String();
    List<String> monthName = new ArrayList<String>();
    List<Float> monthPrice = new ArrayList<Float>();

    String commandType = new String();

    Calendar calendar = new GregorianCalendar();
    int day = calendar.get(Calendar.DAY_OF_WEEK);
    public long count=0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.statistics, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try{
            commandType="getPurchaseHistoryPieChart";
            cat_price = new SendPostRequest().execute().get();
            String [] catPriceList = cat_price.split(";");
            for (int i = 0; i < catPriceList.length; i++){
                String [] splited =catPriceList[i].split(",");
                catName.add(splited[0]);
                catPrice.add(Float.parseFloat(splited[1]));
            }
        }
        catch (Exception e){
        }
        try{
            commandType="getPurchaseHistoryBarChart";
            month_price = new SendPostRequest().execute().get();
            String [] monthPriceList = month_price.split(";");
            for (int i = 0; i < monthPriceList.length; i++){
                String [] splited = monthPriceList[i].split(",");
                monthName.add(splited[0]);
                monthPrice.add(Float.parseFloat(splited[1]));
            }
        }
        catch (Exception e){
        }
        initializeBarChart();
        initializePieChart();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Statistics");
    }
    public void initializePieChart() {
        pieChart = (PieChart) getActivity().findViewById(R.id.pieChart);
        entries = new ArrayList<>();
        PieEntryLabels = new ArrayList<String>();
        AddValuesToPIEENTRY();
        AddValuesToPieEntryLabels();
        pieDataSet = new PieDataSet(entries, "");
        pieData = new PieData(PieEntryLabels, pieDataSet);
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.setData(pieData);
        pieChart.animateY(3000);
    }

    public void initializeBarChart() {
        barChart = (BarChart) getActivity().findViewById(R.id.barChart);
        BARENTRY = new ArrayList<>();
        BarEntryLabels = new ArrayList<String>();
        AddValuesToBARENTRY();
        AddValuesToBarEntryLabels();
        Bardataset = new BarDataSet(BARENTRY, "Carry Time");
        BARDATA = new BarData(BarEntryLabels, Bardataset);
        Bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart.setData(BARDATA);
        barChart.animateY(3000);
    }

    // This function will be changed to fill the array with values from the DB
    public void AddValuesToPIEENTRY(){
//        entries.add(new BarEntry(2, 0));//values: weight 2f
//        entries.add(new BarEntry(4, 1));
//        entries.add(new BarEntry(6, 2));
        for(int i = 0; i < catPrice.size(); i++){
            entries.add(new BarEntry(catPrice.get(i), i));
        }
    }

    // This function will be changed to fill the array with values from the DB
    public void AddValuesToPieEntryLabels(){
//        PieEntryLabels.add("Books");    //value: catgory
//        PieEntryLabels.add("Clothes");
//        PieEntryLabels.add("Accesories");
        for(int i = 0; i < catName.size(); i++){
            PieEntryLabels.add(catName.get(i));
        }
    }

    public void AddValuesToBARENTRY(){
        count = ((MyApplication) getActivity().getApplication()).getSomeVariable();
        int min = Math.round(count / 12);
        for(int i = 0; i < monthName.size(); i++){
//            Toast.makeText(getActivity().getApplicationContext(), String.valueOf(monthPrice.get(i)), Toast.LENGTH_LONG).show();
            BARENTRY.add(new BarEntry(monthPrice.get(i), i));
        }
//        BarEntry January = new BarEntry(200f, 0);
//        BarEntry February = new BarEntry(400f, 1);
//        BarEntry March = new BarEntry(200f, 2);
//        BarEntry April = new BarEntry(200f, 3);
//        BarEntry May = new BarEntry(500f, 4);
//        BarEntry June = new BarEntry(200f, 5);
//        BarEntry July = new BarEntry(200f, 6);
//        BarEntry August = new BarEntry(200f, 7);
//        BarEntry September = new BarEntry(200f, 8);
//        BarEntry October = new BarEntry(500f, 9);
//        BarEntry November = new BarEntry(200f, 10);
//        BarEntry December = new BarEntry(200f, 11);
//
//        BARENTRY.add(January);// values to be get from server
//        BARENTRY.add(February);
//        BARENTRY.add(March);
//        BARENTRY.add(April);
//        BARENTRY.add(May);
//        BARENTRY.add(June);
//        BARENTRY.add(July);
//        BARENTRY.add(August);
//        BARENTRY.add(September);
//        BARENTRY.add(October);
//        BARENTRY.add(November);
//        BARENTRY.add(December);
    }

    public void AddValuesToBarEntryLabels(){
        BarEntryLabels.add("January");
        BarEntryLabels.add("February");
        BarEntryLabels.add("March");
        BarEntryLabels.add("April");
        BarEntryLabels.add("May");
        BarEntryLabels.add("June");
        BarEntryLabels.add("July");
        BarEntryLabels.add("August");
        BarEntryLabels.add("September");
        BarEntryLabels.add("October");
        BarEntryLabels.add("November");
        BarEntryLabels.add("December");
    }




    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try{
                URL url = new URL("http://ec2-34-204-93-26.compute-1.amazonaws.com/");  // set the url to the board's current url
                JSONObject postDataParams = new JSONObject();

                postDataParams.put("type",commandType);
                postDataParams.put("msg","");
                postDataParams.put("userid",((MainActivity)getActivity()).getUserId());
//                postDataParams.put("message", "test");
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
                message_response = conn.getResponseMessage();
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
//            output.setText(message_response);
//            output2.setText(result);
        }


    }







}
