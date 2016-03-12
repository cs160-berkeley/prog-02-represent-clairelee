package claire.represent;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import android.widget.EditText;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.Wearable;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import com.twitter.sdk.android.core.*;
import com.twitter.sdk.android.core.identity.*;


public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private String mLatitudeText;
    private String mLongitudeText;
    private Location mLastLocation;
    private String county;
    private String finalCounty;
    private String apikey = "AIzaSyB_MQrobOAu37eRsfmNJ8J5hFaxCUUlnjo";
    private TwitterLoginButton loginButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(Wearable.API)  // used for data layer API
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                TwitterSession session = result.data;
                // TODO: Remove toast and use the TwitterSession's userID
                // with your app's user model
                String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });
//uncomment below for phone service
//        Intent in = getIntent();
//        Bundle extras = in.getExtras();
//
//        if (extras != null) {
//            Intent intentZip = new Intent(this, SecondScreen.class);
//
//            String mLat = in.getExtras().getString("mLat");
//            String mLon = in.getExtras().getString("mLon");
//            getCountybyCoord(mLat, mLon);
//
//            Intent watchIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
//            //watchIntent.putExtra("from", "currLoc");
//            watchIntent.putExtra("mLat", mLat);
//            watchIntent.putExtra("mLon", mLon);
//
//            startService(watchIntent);
//            startActivity(intentZip);
//        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }


    public void onSendUsersZip(View view) throws JSONException {

        EditText zipText = (EditText) findViewById(R.id.users_zip_edit_text);
        String zipCode = String.valueOf(zipText.getText());

        Intent getMainScreenIntent = new Intent(this,
                SecondScreen.class);

        getMainScreenIntent.putExtra("zipCode", zipCode);
        Log.i("zip", "" + zipCode);
        getCountybyZip(zipCode);
//uncomment below for phone service
//        Intent watchIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
//        //watchIntent.putExtra("from", "currLoc");
//        watchIntent.putExtra("zipCode", zipCode);
//        watchIntent.putExtra("mLat", "null");
//        watchIntent.putExtra("mLon", "null");
//
//        startService(watchIntent);
//        Log.i("onSendUsersZip", "hello");
        startActivity(getMainScreenIntent);
    }

    public void useCurrLoc(View view) {
        Intent getMainScreenIntent = new Intent(this,
                SecondScreen.class);
        Log.i("Google", "" + mLastLocation);
        getMainScreenIntent.putExtra("mLat", mLatitudeText);
        getMainScreenIntent.putExtra("mLon", mLongitudeText);
        getCountybyCoord(mLatitudeText, mLongitudeText);
//uncomment below
//        Intent watchIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
////        watchIntent.putExtra("from", "currLoc");
////        watchIntent.putExtra("zipCode", zipCode);
//        watchIntent.putExtra("mLat", mLatitudeText);
//        watchIntent.putExtra("mLon", mLongitudeText);
//        watchIntent.putExtra("zipCode", "null");
//        startService(watchIntent);
//        Log.i("tag", "hello");
        startActivity(getMainScreenIntent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        if (mLastLocation != null) {
            mLatitudeText=String.valueOf(mLastLocation.getLatitude());
            mLongitudeText=String.valueOf(mLastLocation.getLongitude());
            Log.i("place", ""+mLastLocation);
            Log.i("lat", ""+mLatitudeText);

        }

    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connResult) {}


    public void getCountybyZip(String zipCode) throws JSONException {
        Log.i("zip", "" + zipCode);
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address=" + zipCode + "&region=us&key=" + apikey;

        Log.i("url", "" + url);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(url);
        } else {
            county = "No network connection available.";
        }
        Log.d("county B", "" + finalCounty);

    }


    public void getCountybyCoord(String lat, String lon) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lon + "&key=" + apikey;
        Log.i("url", "" + url);
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask2().execute(url);
        } else {
            county = "No network connection available.";
        }
        Log.d("county A", "" + finalCounty);

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
            county = result;
            try {
                JSONObject myJObject = new JSONObject(county);
                String county = myJObject.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(2).getString("long_name");
                Log.i("county will deff", ""+county);
                finalCounty = county;
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

    /* END JSON READER1 */


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
            county = result;
            try {
                JSONObject myJObject = new JSONObject(county);
                String county = myJObject.getJSONArray("results").getJSONObject(0).getJSONArray("address_components").getJSONObject(3).getString("long_name");
                //Log.i("county", ""+county);
                finalCounty = county;
            } catch (JSONException e) {
                // Appropriate error handling code
            }
        }
    }

}

