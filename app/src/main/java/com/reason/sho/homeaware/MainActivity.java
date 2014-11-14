package com.reason.sho.homeaware;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationRequest;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class MainActivity extends Activity implements
        LocationListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener{

    private LocationClient mLocationClient;
    private Location mCurrentLocation;

    private TextView mAddress;
    private ProgressBar mActivityIndicator;


    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private final static String APP_TAG = "MainActivity";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAddress = (TextView)findViewById(R.id.location_address);
        mActivityIndicator = (ProgressBar)findViewById(R.id.address_progress);
        mLocationClient = new LocationClient(this, this, this);


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch(requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST :

                switch(resultCode) {
                    case Activity.RESULT_OK :

                        Log.d(APP_TAG, "Error resolved. Please re-try operation.");

                    break;

                    default:

                        Log.d(APP_TAG, getString(R.string.no_resolution));

                    break;
                }

            default:
                Log.d(APP_TAG, getString(R.string.unknown_activity_request_code, requestCode));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationClient.connect();
    }

    @Override
    protected void onStop() {
        mLocationClient.disconnect();
        super.onStop();
    }


    @Override
    public void onConnected(Bundle bundle) {

        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onDisconnected() {

        Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onLocationChanged(Location location) {
        Toast.makeText(this, "Location changed.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void getLocation(View v) {
        if(servicesConnected()) {
            mCurrentLocation = mLocationClient.getLastLocation();

            mAddress.setText(mCurrentLocation.getLatitude() + " " + mCurrentLocation.getLongitude());
        }

    }


   public void getAddress(View v) {

       if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && !Geocoder.isPresent()) {
           Toast.makeText(this, "Cannot get address. No geocoder available.", Toast.LENGTH_LONG).show();
           return;
       }

       if(servicesConnected()) {
           mCurrentLocation = mLocationClient.getLastLocation();

           mActivityIndicator.setVisibility(View.VISIBLE);

           (new GetAddressTask(this)).execute(mCurrentLocation);
       }
   }


    private boolean servicesConnected() {

        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {

            Log.d(APP_TAG, "Google Play services is available");


            return true;

        } else {



            Log.d(APP_TAG, "Google Play services is not available");

            return false;
        }
    }

    protected class GetAddressTask extends AsyncTask<Location, Void, String> {

        Context localContext;

        public GetAddressTask(Context context) {

            super();

            localContext = context;
        }

        @Override
        protected String doInBackground(Location... locations) {

            Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());

            Location location = locations[0];

            List<Address> addresses = null;

            try {

                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            } catch (IOException e) {

                Log.d(APP_TAG, "Exception in Geocoder.getFromLocation");
                e.printStackTrace();
                return("Exception in Geocoder.getFromLocation");
            } catch (IllegalArgumentException exception2) {

                String errorString = "Illegal arguements " +
                        Double.toString(location.getLatitude()) +
                        ", " +
                        Double.toString(location.getLongitude()) +
                        " passed to address service";

                Log.e(APP_TAG, errorString);
                exception2.printStackTrace();

                //
                return "Illegal Arguments";
            }

            if(addresses != null && addresses.size() > 0) {

                Address address = addresses.get(0);

                String addressText = String.format(
                        "%s, %s, %s",
                        address.getMaxAddressLineIndex() > 0 ?
                                address.getAddressLine(0) : "",
                        address.getLocality(),
                        address.getCountryName());

                return addressText;
            } else {

                return "No address found for location";
            }
        }

        @Override
        protected void onPostExecute(String address) {

            // Turn off the progress bar
            mActivityIndicator.setVisibility(View.GONE);

            // Set the address in the UI
            mAddress.setText(address);
        }
    }

    public void setAddress(String address) {
        mAddress.setText(address);
    }

    public void setProgressBarVisible() {
        mActivityIndicator.setVisibility(View.VISIBLE);
    }

    public void setProgressBarInvisible() {
        mActivityIndicator.setVisibility(View.GONE);
    }

}
