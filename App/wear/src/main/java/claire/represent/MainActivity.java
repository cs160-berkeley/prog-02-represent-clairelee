package claire.represent;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridPagerAdapter;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.GridViewPager;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends WearableActivity {

    String apikey = "3df0c023ddd540fd95e7ca7dea7b5570";
    private static final SimpleDateFormat AMBIENT_DATE_FORMAT =
            new SimpleDateFormat("HH:mm", Locale.US);

    private BoxInsetLayout mContainerView;
    private TextView mTextView;
    private TextView mClockView;
    private SensorManager mSensorManager;
    private ShakeEventListener mSensorListener;
    public String zipCode;
    public String mLat;
    public String mLon;
    public ArrayList<CongressDetails> congressPeopleFinal;

    List<String> congressPerson;

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mSensorListener);
        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setAmbientEnabled();
        Log.d("watch turned on", "hello i am the watch"); //this DOES NOT PRINT, but phone to watch is set up CORRECTLY

//        mContainerView = (BoxInsetLayout) findViewById(R.id.container);
//        mTextView = (TextView) findViewById(R.id.text);
//        mClockView = (TextView) findViewById(R.id.clock);

//        Intent activityThatCalled = getIntent();
//        if (activityThatCalled != null) {
//            String zipCode = activityThatCalled.getStringExtra("zipCode");
//            String mLat = activityThatCalled.getStringExtra("mLat");
//            String mLon = activityThatCalled.getStringExtra("mLon");
//        }

        final Resources res = getResources();
        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);

        String zipCode = getIntent().getStringExtra("zipCode");
        Log.d("zip in watch", ""+zipCode);
        String mLat = getIntent().getStringExtra("mLat");
        String mLon = getIntent().getStringExtra("mLon");
        Log.d("Lat  in watch", ""+mLat);

        if (zipCode != null) {
            Log.d("inside if", "hey");
            getCongressInfoByZip(zipCode);
        } else if (mLat != null) {
            getCongressInfoByCoord(mLat, mLon);
        } else {

        }

        //---Assigns an adapter to provide the content for this pager---

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorListener = new ShakeEventListener();

        mSensorListener.setOnShakeListener(new ShakeEventListener.OnShakeListener()
        {


            public void onShake() {
                Intent sendIntent = new Intent(getBaseContext(), WatchToPhoneService.class);
//                Random rdm = new Random();
//                Integer number = (int) rdm.nextFloat()*10000; //generate valid zip code
//
//                sendIntent.putExtra("zipCode", number.toString());
//                Log.i("new ZipCode generated", "NEW ZIP");

                //instead of generating random zip code, I generated latitudes inside USA
                Random rand = new Random();
                int latiInt = rand.nextInt(9) + 33;
                int latiFrac = rand.nextInt(1000000);
                int longiInt = rand.nextInt(34) + 83;
                int longiFrac = rand.nextInt(1000000);
                final String latitude = Integer.toString(latiInt) + "." + Integer.toString(latiFrac);
                final String longitude = "-" + Integer.toString(longiInt) + "." + Integer.toString(longiFrac);
                sendIntent.putExtra("mLat", latitude);
                sendIntent.putExtra("mLon", longitude);

                startService(sendIntent);
            }
        });

    }


    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            mContainerView.setBackgroundColor(getResources().getColor(android.R.color.black));
            mTextView.setTextColor(getResources().getColor(android.R.color.white));
            mClockView.setVisibility(View.VISIBLE);

            mClockView.setText(AMBIENT_DATE_FORMAT.format(new Date()));
        } else {
            mContainerView.setBackground(null);
            mTextView.setTextColor(getResources().getColor(android.R.color.black));
            mClockView.setVisibility(View.GONE);
        }
    }

    String textView = null;

    public void getCongressInfoByZip(String zipcode) {
        String url = "https://congress.api.sunlightfoundation.com/legislators/locate?zip=" + zipcode + "&apikey=" + apikey;
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(url);
        } else {
            textView = "No network connection available.";
        }
    }

    public void getCongressInfoByCoord(String mLat, String mLon) {
        String url = "https://congress.api.sunlightfoundation.com/legislators/locate?latitude="+mLat+"&longitude="+mLon+"&apikey="+apikey;
        Log.i("urlwtf", ""+url);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(url);
        } else {
            textView = "No network connection available.";
        }
        Log.d("congressCoord", "" + textView);
    }

    public void setCongressPeopleFinal(ArrayList<CongressDetails> congressPeople) {
        congressPeopleFinal = congressPeople;
    }

    public String a;
    public String b;
    public String c;
    public String d;
    public String e;
    public String f;
    public String g;
    public String h;


    private void updateDisplay(ArrayList<CongressDetails> congressPeople) {

        Log.i("updateDisplay", "holla");
        setCongressPeopleFinal(congressPeople);
        CongressDetails congressDude1 = congressPeople.get(0);

        b = congressDude1.getFirstName()+" "+congressDude1.getLastName() + " (" +congressDude1.getParty()+")";
        a = congressDude1.getTitle();

        CongressDetails congressDude2 = congressPeople.get(1);
        d = congressDude2.getFirstName()+" "+congressDude2.getLastName() + " (" +congressDude2.getParty()+")";
        c = congressDude2.getTitle();

        CongressDetails congressDude3 = congressPeople.get(2);
        f = congressDude3.getFirstName()+" "+congressDude3.getLastName() + " (" +congressDude3.getParty()+")";
        e = congressDude3.getTitle();

        if (congressPeople.size() > 3) {
            CongressDetails congressDude4 = congressPeople.get(3);
            g =congressDude4.getFirstName()+" "+congressDude4.getLastName() + " (" +congressDude4.getParty()+")";
            h = congressDude4.getTitle();
        }

        final Resources res = getResources();
        final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);

        pager.setAdapter(new ImageAdapter(this, zipCode));
        DotsPageIndicator dotsPageIndicator = (DotsPageIndicator) findViewById(R.id.page_indicator);
        dotsPageIndicator.setPager(pager);

    }


    public class ImageAdapter extends GridPagerAdapter {
        final Context mContext;
        String zip;
        public ImageAdapter(final Context context, String zipCode) {
            mContext = context;
            zip = zipCode;
        }

        @Override
        public int getRowCount() {
            return 1;
        }

        @Override
        public int getColumnCount(int i) {
            return 4;
        } // would be 5 if 2012 view worked

        //---Go to current column when scrolling up or down (instead of default column 0)---
        @Override
        public int getCurrentColumnForRow(int row, int currentColumn) {
            return currentColumn;
        }

        //---Return our car image based on the provided row and column---
        @Override
        public Object instantiateItem(ViewGroup viewGroup, int row, int col) {

            TextView textView;
            textView = new TextView(mContext);
            if (col == 0 ){
                textView.setText(b + "" + a);
                textView.setBackgroundResource(R.color.navy);
                textView.setTextColor(0xffffffff);
            }
            if (col == 1 ){
                textView.setText(c + "" + d);
                textView.setBackgroundResource(R.color.navy);
                textView.setTextColor(0xffffffff);
            }
            if (col == 2 ){
                textView.setText(e+""+f);
                textView.setBackgroundResource(R.color.navy);
                textView.setTextColor(0xffffffff);
            }
            if (col == 3) {
                textView.setText(g+""+h);
                textView.setBackgroundResource(R.color.navy);
                textView.setTextColor(0xffffffff);
            }

            // what's the point in doing county stuff if watch view won't show

//            if (col == 4 ){
//                textView.setText("2012 Presidential Vote\nCalifornia\nSan Mateo\n Obama 67% Romney 33%");
//                textView.setBackgroundColor(Color.rgb(30, 144, 255));
//                textView.setTextColor(0xffffffff);
//
//
//            }

            textView.setGravity(Gravity.CENTER);
            viewGroup.addView(textView);

            return textView;
        }

        @Override
        public void destroyItem(ViewGroup viewGroup, int i, int i2, Object o) {
            viewGroup.removeView((View) o);
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return view.equals(o);
        }
    }

    /* JSON READER BELOW  */

    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            textView = result;
            try {
                JSONObject details = new JSONObject(textView);
                Log.i("DOWNLOAD", "IN WATCH");

                ArrayList<CongressDetails> congressPeople = new ArrayList<CongressDetails>();

                JSONArray currentDetails = details.getJSONArray("results");
                for (int i = 0; i < currentDetails.length(); i++) {
                    JSONObject explrObject = currentDetails.getJSONObject(i);
                    String bioguide_id = explrObject.getString("bioguide_id");
                    String title = explrObject.getString("title");
                    String firstName = explrObject.getString("first_name");
                    String lastName = explrObject.getString("last_name");
                    String party = explrObject.getString("party");
                    String email = explrObject.getString("oc_email");
                    String website = explrObject.getString("website");
                    String termEnd = explrObject.getString("term_end");
                    String twitter = explrObject.getString("twitter_id");
                    Log.i("congressperson", "test " + firstName);

                    CongressDetails congressPerson = new CongressDetails(bioguide_id, title, firstName, lastName, party, email, website, termEnd, twitter);
                    congressPeople.add(congressPerson);
                }
                updateDisplay(congressPeople);
            } catch (JSONException e) {
                // Appropriate error handling code
            }

        }
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private String downloadUrl(String myurl) throws IOException {
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();

            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream) throws IOException, UnsupportedEncodingException {
        Scanner s = new Scanner(stream).useDelimiter("\\A");
        String result = s.hasNext() ? s.next() : "";
        return new String(result);
    }

    /* END JSON READER */
}
