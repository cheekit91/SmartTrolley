package com.example.cheekit.group_1111_ee6765_iot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.content.Intent;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private BluetoothAdapter bluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;

    LocationManager mLocationManager;
    String commandType = new String();
    String commandMsg = new String();
    private static UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final String TAG = "BluetoothDemo";

    int fragmentId;
    private OutputStream mmOutStream;
    private InputStream mmInStream;

    private BluetoothSocket mmSocket;
    private byte[] mmBuffer; // mmBuffer store for the stream

    private Handler mHandler; // handler that gets info from Bluetooth service

    public int pre_x = 0;
    public double dist = 0;
    public float pre_longt = 0;
    public float pre_lat = 0;
    public int pre_day = 0;
    public long countsec = 0;
    Calendar calendar = new GregorianCalendar();
    int day = calendar.get(Calendar.DAY_OF_WEEK);
    public Boolean bluetoothRdy=false;
    private String recipeName = new String();
    void setRecipeName(String input){
        recipeName=input;
    }
    String getRecipeName(){
        return recipeName;
    }

    private String userId =new String();
    String getUserId(){return userId;}
    void setUserId(String input){userId=input;}
    //globalList for recipeToAddList
    private List<String> recipeItemNameList = new ArrayList<String>();

    List<String> getRecipeItemNameList(){
        return recipeItemNameList;
    }
    void addItemToRecipeItemNameList(String item){
        recipeItemNameList.add(item);
    }
    void removeItemToRecipeItemNameList(int position){
        recipeItemNameList.remove(position);
    }
    void clearRecipeItemNameList(){
        recipeItemNameList.clear();
    }

    private List<String> recipeQuantityList = new ArrayList<String>();

    List<String> getRecipeQuantityList(){
        return recipeQuantityList;
    }
    void addItemToRecipeQuantityList(String item){
        recipeQuantityList.add(item);
    }
    void removeItemToRecipeQuantityList(int position){
        recipeQuantityList.remove(position);
    }
    void clearRecipeQuantityList(){
        recipeQuantityList.clear();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUserId(((MyApplication)getApplication()).getUserId());
//        bluetooth
        try {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            mmSocket = null;

            on();
            connector();
            th.start();
            bluetoothRdy = true;
        }
        catch(Exception e){}
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        displaySelectedScreen(R.id.homePage);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


//
//
//        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
//        try {
//            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10,
//                    10, mLocationListener);
//        } catch (SecurityException e) {
//        }
    }
    public void getlocal(Location location){
        String msg = locationStringFromLocation(location);
        if(pre_day != day){
            countsec = 0;
        }
        pre_day = day;

        float longt = Float.parseFloat(msg.split(" ")[0]);
        float lat = Float.parseFloat(msg.split(" ")[1]);
        dist = Math.sqrt((longt-pre_longt)*(longt-pre_longt) + (lat-pre_lat) *(lat-pre_lat));
        if(Math.sqrt((longt-pre_longt)*(longt-pre_longt) + (lat-pre_lat) *(lat-pre_lat)) >= 0.00005){
            ((MyApplication) this.getApplication()).setSomeVariable(countsec++);
        }
        ((MyApplication) this.getApplication()).lat=lat;
        ((MyApplication) this.getApplication()).longt=longt;
        pre_longt = longt;
        pre_lat = lat;
    }
    public static String locationStringFromLocation(final Location location) {
        return Location.convert(location.getLatitude(), Location.FORMAT_DEGREES) + " " + Location.convert(location.getLongitude(), Location.FORMAT_DEGREES);
    }
    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
//            Toast.makeText(getApplicationContext(),locationStringFromLocation(location),Toast.LENGTH_LONG).show();
            getlocal(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
//            Toast.makeText(getApplicationContext(),"gps changed",Toast.LENGTH_LONG).show();
        }

        @Override
        public void onProviderEnabled(String provider) {
//            Toast.makeText(getApplicationContext(),"gps enabled",Toast.LENGTH_LONG).show();

        }

        @Override
        public void onProviderDisabled(String provider) {
//            Toast.makeText(getApplicationContext(),"gps disabled",Toast.LENGTH_LONG).show();
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        if (fragment instanceof ItemInfo) {
//            ItemInfo my = (ItemInfo) fragment;
//            // Pass intent or its data to the fragment's method
//            my.handleIntent(intent);;
//        }
//        /**
//         * This method gets called, when a new Intent gets associated with the current activity instance.
//         * Instead of creating a new activity, onNewIntent will be called. For more information have a look
//         * at the documentation.
//         *
//         * In our case this method gets called, when the user attaches a Tag to the device.
//         */
//    }
    Fragment fragment;
    public void displaySelectedScreen(int id){
        fragment = null;
        fragmentId=id;
        switch(id) {
            case R.id.homePage:
                fragment = new HomePage();
                break;
            case R.id.tagItem:
                fragment = new TagItem();
                break;
            case R.id.itemList:
                fragment = new ItemList();
                break;
            case R.id.recipeList:
                fragment = new RecipeList();
                break;
            case R.id.linkRecipe:
                fragment = new LinkRecipe();
                break;
            case R.id.stats:
                fragment = new Statistics();
                break;
            case R.id.scheduler:
                fragment = new Scheduler();
                break;
            case R.id.linkRecipeAddItem:
                fragment = new LinkRecipeAddItem();
                break;
            case R.id.toBuyList:
                fragment = new ToBuyList();
                break;
            case R.id.purchase:
                fragment = new PurchaseHistory();
                break;
            case R.id.promotion:
                fragment = new Promotion();
                break;
//            case R.id.bluetooth:
//                fragment = new Bluetooth();
//                break;
        }

        if(fragment != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_main, fragment);
            ft.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if(id==R.id.getHelp)
        {
            commandType = "getHelp";
            commandMsg = "0";
            try {
                String result = new SendPostRequest().execute().get();
            } catch (Exception e) {
            }
        }
        else {
            displaySelectedScreen(id);
        }
        return true;
    }



    // This is to turn on the bluetooth adapter if it is not already on
    public void on(){
        if (!bluetoothAdapter.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
        }
    }


    // Call this to turn off the bluetooth adapter (not used)
    public void off(View v){
        bluetoothAdapter.disable();
        Toast.makeText(getApplicationContext(), "Turned off" ,Toast.LENGTH_LONG).show();
    }



    // If connection is not established on app startup (onCreate) try again with this method
    public void connect(View v){
        try{
            String name = "CONNECTED";
            byte[] bytes = name.getBytes();
            mmOutStream.write(bytes);
        }catch (IOException e){
            Toast.makeText(getApplicationContext(), "Connecting..." ,Toast.LENGTH_LONG).show();
            connector();
        }
    }


    public void connector(){

        OutputStream tmpOut = null;
        InputStream tmpIn = null;

        // Get list of paired devices
        BluetoothSocket tmp = null;

        String dname;

        pairedDevices = bluetoothAdapter.getBondedDevices();
        BluetoothDevice device = null;
        if(pairedDevices.size() >0) {
            for (BluetoothDevice bt : pairedDevices) {
                Log.d("TAG", bt.getName());
                dname = bt.getName();
                if (dname.equals("HC-05")) {
                    device = bt;
                    Log.d("TAG", "HC-05 PARED!!!");
                    Toast.makeText(getApplicationContext(), device.getName(), Toast.LENGTH_LONG).show();
                } else {
                    Log.d("TAG", "Not HC-05");
                }

            }

            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);

            } catch (IOException e) {
                Log.d("TAG", "Socket's listen() method failed", e);
                Toast.makeText(getApplicationContext(), "Error 1" ,Toast.LENGTH_LONG).show();
            }
            mmSocket = tmp;


            bluetoothAdapter.cancelDiscovery();



            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();

                Log.d("TAG", "Socket connected!!!!!");
                Toast.makeText(getApplicationContext(), "Connected" ,Toast.LENGTH_LONG).show();
            } catch (IOException connectException) {}



            try {
                tmpIn = mmSocket.getInputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating input stream", e);
            }


            try {
                tmpOut = mmSocket.getOutputStream();
            } catch (IOException e) {
                Log.e(TAG, "Error occurred when creating output stream", e);
                Toast.makeText(getApplicationContext(), "Error 2" ,Toast.LENGTH_LONG).show();
            }

            mmOutStream = tmpOut;
            mmInStream = tmpIn;



        }else{
            Log.d("TAG", "No devices");
            Toast.makeText(getApplicationContext(), "HC-05 is not pared", Toast.LENGTH_LONG).show();
        }




    }
    void sortmsg(String msg) {
        if (bluetoothRdy) {
//            if (fragmentId == R.id.tagItem) {
//                ((TagItem) fragment).updateTextInput(msg);
//            }
            if (msg.contains("rfid:")) {
                commandType = "moveToPurchaseHistory";


                String month = String.valueOf(Calendar.getInstance().get(Calendar.MONTH)+1);
                String day = String.valueOf(Calendar.getInstance().get(Calendar.DATE));
                int msgIndex = msg.indexOf("rfid:");
                String tagId = msg.substring(msgIndex + 5);
                if (tagId.length() == 10) {
                    commandMsg = month+','+day + "," + tagId;
                    try {
                        String result = new SendPostRequest().execute().get();
                    } catch (Exception e) {
                    }
                }
                if (fragmentId == R.id.homePage)
                    ((HomePage) fragment).updateToBuyList();
            }
            if (msg.contains("acc")) {
                int idx = msg.indexOf("acc");
                String subString = msg.substring(idx);
                String[] splitedmsg = subString.split(",");
//            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            try {
                if (!splitedmsg[1].equals("") && !splitedmsg[2].equals("") && !splitedmsg[3].equals("")) {
                    int x = Integer.valueOf(splitedmsg[1]);
                    int y = Integer.valueOf(splitedmsg[2]);
                    int z = Integer.valueOf(splitedmsg[3]);
                    if ((x > 100 || x < -100) && (pre_x > 100 || pre_x < -100) && (dist > 0.00005)) {
                        NotificationScheduler.showNotification(this, MainActivity.class,
                                "Do not run with trolley!", "Please slow down your speed!");
                    }
                    pre_x = x;
                }
            }
            catch(Exception e){}
            }
        }
    }

    // send the request to control the oled
    public class SendPostRequest extends AsyncTask<String, Void, String> {

        protected void onPreExecute(){}

        protected String doInBackground(String... arg0) {

            try{
                URL url = new URL("http://ec2-34-204-93-26.compute-1.amazonaws.com/");  // set the url to the board's current url
                JSONObject postDataParams = new JSONObject();

                postDataParams.put("type",commandType);
                postDataParams.put("msg",commandMsg);
                postDataParams.put("userid",getUserId());
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
            //output.setText(message_response);
            //output2.setText(result);
        }


    }
    // thread to listen to the input data from HC05 (not perfect)
    Thread th = new Thread(new Runnable() {
        public void run() {


            mmBuffer = new byte[4096];
            int numBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    if(mmInStream.available()>2) {
                        Log.d("TAG","mmInStream.available()>2");

                        // Read from the InputStream.
                        numBytes = mmInStream.read(mmBuffer);

                        final String readMessage = new String(mmBuffer, 0, numBytes);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                Toast.makeText(getApplicationContext(), readMessage, Toast.LENGTH_LONG).show();
                                sortmsg(readMessage);
                            }
                        });

                        Log.d("TAG", readMessage);
                    }else{
                        SystemClock.sleep(100);
                        Log.d("TAG", "No Data");
                    }
                } catch (IOException e) {
                    Log.d("TAG", "Input stream was disconnected", e);
                    break;
                }
            }


        }
    });

}
