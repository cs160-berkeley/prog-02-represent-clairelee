package claire.represent;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.widget.EditText;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by clairelee on 3/1/16.
 */

public class ThirdScreen extends Activity {

    String apikey = "3df0c023ddd540fd95e7ca7dea7b5570";
    //private String mZipCode;
    //public static final String Tag = MainActivity.class.getSimpleName();
    private ArrayList<CongressDetails> congressPeople;
    private TextView mThatCongress;
    private String mSingleCongressPerson;
    static final int PICK_CONTACT_REQUEST = 1;
    public ArrayList<CongressDetails> congressPeopleFinal;
    String textView = null;
    List<String> congressPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detailed);

        Intent activityThatCalled = getIntent();
        String bioguide_id = activityThatCalled.getExtras().getString("bioguide_id");
        Log.i("3", "here i am"+bioguide_id);

        congressPerson = new ArrayList<String>();
        callthis(bioguide_id);
        callthis2(bioguide_id);
        callthis3(bioguide_id);

        ImageView pic = (ImageView) findViewById(R.id.pic);
        Picasso.with(this).load("https://theunitedstates.io/images/congress/225x275/" +bioguide_id+ ".jpg").into(pic);
    }

    public void callthis(String bioguide_id) {
        String url = "https://congress.api.sunlightfoundation.com/legislators?bioguide_id=" +bioguide_id+ "&apikey=" +apikey;
        Log.i("individ", "" + url);
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

    public void callthis2(String bioguide_id) {
        String url = "https://congress.api.sunlightfoundation.com/committees?member_ids=" +bioguide_id+ "&apikey=" +apikey;
        Log.i("individ", "" + url);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask2().execute(url);
        } else {
            textView = "No network connection available.";
        }
        Log.d("congressCoord", "" + textView);
    }

    public void callthis3(String bioguide_id) {
        String url = "https://congress.api.sunlightfoundation.com/bills?sponsor_id=" +bioguide_id+ "&apikey=" +apikey;
        Log.i("individ", "" + url);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask3().execute(url);
        } else {
            textView = "No network connection available.";
        }
        Log.d("congressCoord", "" + textView);
    }

    public void onBackButton(View view) {
        Intent getThirdScreenIntent = new Intent();
        setResult(RESULT_OK, getThirdScreenIntent);
        finish();
    }

    private void updateDisplay(CongressDetails congressPerson) {


        TextView party = (TextView) findViewById(R.id.party);
        TextView name = (TextView) findViewById(R.id.name);
        TextView termEnds = (TextView) findViewById(R.id.termEnds);

        name.setText(congressPerson.getFirstName()+" "+congressPerson.getLastName());
        party.setText("(" + congressPerson.getParty() + ") " + congressPerson.getTitle());
        termEnds.setText(congressPerson.getTermEnd());
    }

    private void updateDisplay3(String bills) {
        TextView thebills = (TextView) findViewById(R.id.bills);
        thebills.setText(bills);
    }

    private void updateDisplay2(String committees) {
        TextView thecommittees = (TextView) findViewById(R.id.committees);
        thecommittees.setText(committees);
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
                JSONArray currentDetails = details.getJSONArray("results");
                int i = 0;
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
                updateDisplay(congressPerson);
            } catch (JSONException e) {
                // Appropriate error handling code
            }

        }
    }

    private class DownloadWebpageTask2 extends AsyncTask<String, Void, String> {
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
                StringBuilder committees = new StringBuilder();
                JSONArray results = details.getJSONArray("results");
                for (int i = 0; i < results.length(); i++) {
                    JSONObject explrObject = results.getJSONObject(i);
                    String name = explrObject.getString("name");
                    if (name != null) {
                        committees.append(name);
                        committees.append("\n\n");
                    }
                }

                updateDisplay2(committees.toString());
            } catch (JSONException e) {
                // Appropriate error handling code
            }

        }
    }

    private class DownloadWebpageTask3 extends AsyncTask<String, Void, String> {
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
                StringBuilder bills = new StringBuilder();
                JSONArray results = details.getJSONArray("results");
                for (int i = 0; i < 6; i++) {
                    JSONObject explrObject = results.getJSONObject(i);
                    String name = explrObject.getString("short_title");
                    String date = explrObject.getString("introduced_on");
                    if (name!="null") {
                        bills.append(date+": ");
                        bills.append(name);
                        bills.append("\n\n");
                    }
                }

                updateDisplay3(bills.toString());
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
