package claire.represent;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.RemoteInput;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v7.app.ActionBar;
import android.telecom.Call;
import android.text.util.Linkify;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.content.Intent;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.appdatasearch.GetRecentContextCall;
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

//
import com.twitter.sdk.android.Twitter;
//import com.twitter.sdk.android.core.*;
//import com.twitter.sdk.android.core.models.Tweet;
//import com.twitter.sdk.android.tweetui.*;
//import io.fabric.sdk.android.Fabric;
/**
 * Created by clairelee on 3/1/16.
 */

public class SecondScreen extends Activity {

    String apikey = "3df0c023ddd540fd95e7ca7dea7b5570";
    //private String mZipCode;
    //public static final String Tag = MainActivity.class.getSimpleName();
    private ArrayList<CongressDetails> congressPeople;
    private TextView mThatCongress;
    private String mSingleCongressPerson;
    static final int PICK_CONTACT_REQUEST = 1;
    public ArrayList<CongressDetails> congressPeopleFinal;

    List<String> congressPerson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        congressPerson = new ArrayList<String>();

        Intent activityThatCalled = getIntent();
        String zipCode = activityThatCalled.getExtras().getString("zipCode");
        String mLat = activityThatCalled.getExtras().getString("mLat");
        String mLon = activityThatCalled.getExtras().getString("mLon");
        //setContentView(R.layout.congressional_view_more);

        if (zipCode != null) {
            getCongressInfoByZip(zipCode);
//            if (congressPeopleFinal.size() == 3) {
//                setContentView(R.layout.congressional_view);
//            } else {
//                setContentView(R.layout.congressional_view_more);
//            }
        } else {
            getCongressInfoByCoord(mLat, mLon);
//            if (congressPeopleFinal.size() == 3) {
//                setContentView(R.layout.congressional_view);
//            } else {
//                setContentView(R.layout.congressional_view_more);
//            }
        }

    }

    public void onSeeMore1(View view) {
        Intent getSecondScreenIntent = new Intent(this,
                ThirdScreen.class);

        String bioguide_id = congressPeopleFinal.get(0).getMbioguide_id();
        getSecondScreenIntent.putExtra("bioguide_id", bioguide_id);

        startActivity(getSecondScreenIntent);
    }

    public void onSeeMore2(View view) {
        Intent getSecondScreenIntent = new Intent(this,
                ThirdScreen.class);

        String bioguide_id = congressPeopleFinal.get(1).getMbioguide_id();
        getSecondScreenIntent.putExtra("bioguide_id", bioguide_id);

        startActivity(getSecondScreenIntent);
    }


    public void onSeeMore3(View view) {
        Intent getSecondScreenIntent = new Intent(this,
                ThirdScreen.class);

        String bioguide_id = congressPeopleFinal.get(2).getMbioguide_id();
        getSecondScreenIntent.putExtra("bioguide_id", bioguide_id);

        startActivity(getSecondScreenIntent);
    }

    public void onSeeMore4(View view) {
        Intent getSecondScreenIntent = new Intent(this,
                ThirdScreen.class);

        String bioguide_id = congressPeopleFinal.get(3).getMbioguide_id();
        getSecondScreenIntent.putExtra("bioguide_id", bioguide_id);

        startActivity(getSecondScreenIntent);
    }


    String textView = null;

    public void getCongressInfoByZip(String zipcode) {
        setContentView(R.layout.congressional_view_more);
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
        setContentView(R.layout.congressional_view);
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

    private void updateDisplay(ArrayList<CongressDetails> congressPeople) {

        setCongressPeopleFinal(congressPeople);
        CongressDetails congressDude1 = congressPeople.get(0);

        TextView type1 = (TextView) findViewById(R.id.type1);
        TextView name1 = (TextView) findViewById(R.id.name1);
        TextView email1 = (TextView) findViewById(R.id.email1);
        TextView website1 = (TextView) findViewById(R.id.website1);
        TextView tweet1 = (TextView) findViewById(R.id.tweet1);
        ImageView pic1 = (ImageView) findViewById(R.id.pic1);

        TextView type2 = (TextView) findViewById(R.id.type2);
        TextView name2 = (TextView) findViewById(R.id.name2);
        TextView email2 = (TextView) findViewById(R.id.email2);
        TextView website2 = (TextView) findViewById(R.id.website2);
        TextView tweet2 = (TextView) findViewById(R.id.tweet2);
        ImageView pic2 = (ImageView) findViewById(R.id.pic2);

        TextView type3 = (TextView) findViewById(R.id.type3);
        TextView name3 = (TextView) findViewById(R.id.name3);
        TextView email3 = (TextView) findViewById(R.id.email3);
        TextView website3 = (TextView) findViewById(R.id.website3);
        TextView tweet3 = (TextView) findViewById(R.id.tweet3);
        ImageView pic3 = (ImageView) findViewById(R.id.pic3);

        name1.setText(congressDude1.getFirstName()+" "+congressDude1.getLastName() + " (" +congressDude1.getParty()+")");
        type1.setText(congressDude1.getTitle());
        email1.setText(congressDude1.getEmail());
        Linkify.addLinks(email1, Linkify.ALL);
        website1.setText(congressDude1.getWebsite());

        Picasso.with(this).load("https://theunitedstates.io/images/congress/225x275/" +congressDude1.getMbioguide_id()+ ".jpg").into(pic1);

        CongressDetails congressDude2 = congressPeople.get(1);
        name2.setText(congressDude2.getFirstName()+" "+congressDude2.getLastName() + " (" +congressDude2.getParty()+")");
        type2.setText(congressDude2.getTitle());
        email2.setText(congressDude2.getEmail());
        Linkify.addLinks(email2, Linkify.ALL);
        website2.setText(congressDude2.getWebsite());

        Picasso.with(this).load("https://theunitedstates.io/images/congress/225x275/" +congressDude2.getMbioguide_id()+ ".jpg").into(pic2);

        CongressDetails congressDude3 = congressPeople.get(2);
        name3.setText(congressDude3.getFirstName()+" "+congressDude3.getLastName() + " (" +congressDude3.getParty()+")");
        type3.setText(congressDude3.getTitle());
        email3.setText(congressDude3.getEmail());
        Linkify.addLinks(email3, Linkify.ALL);
        website3.setText(congressDude3.getWebsite());

        Picasso.with(this).load("https://theunitedstates.io/images/congress/225x275/" +congressDude3.getMbioguide_id()+ ".jpg").into(pic3);

        if (congressPeople.size() > 3) {
            TextView type4 = (TextView) findViewById(R.id.type4);
            TextView name4 = (TextView) findViewById(R.id.name4);
            TextView email4 = (TextView) findViewById(R.id.email4);
            TextView website4 = (TextView) findViewById(R.id.website4);
            TextView tweet4 = (TextView) findViewById(R.id.tweet4);
            ImageView pic4 = (ImageView) findViewById(R.id.pic4);

            CongressDetails congressDude4 = congressPeople.get(3);
            name4.setText(congressDude4.getFirstName()+" "+congressDude4.getLastName() + " (" +congressDude4.getParty()+")");
            type4.setText(congressDude4.getTitle());
            email4.setText(congressDude4.getEmail());
            Linkify.addLinks(email4, Linkify.ALL);
            website4.setText(congressDude4.getWebsite());

            Picasso.with(this).load("https://theunitedstates.io/images/congress/225x275/" +congressDude4.getMbioguide_id()+ ".jpg").into(pic4);
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
