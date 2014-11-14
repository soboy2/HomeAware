package com.reason.sho.homeaware;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by soboy on 10/29/14.
 */
public class GetAddressAsync extends AsyncTask<Location, Void, String> {
    private static final String TAG = "GetAddressAsync";


    MainActivity mainActivity = new MainActivity();

    public GetAddressAsync(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    protected String doInBackground(Location... locations) {

        Geocoder geocoder = new Geocoder(this.mainActivity, Locale.getDefault());

        Location location = locations[0];

        List<Address> addresses = null;

        try {

            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

        } catch (IOException e) {

            Log.d(TAG, "Exception in Geocoder.getFromLocation");
            e.printStackTrace();
            return("Exception in Geocoder.getFromLocation");
        } catch (IllegalArgumentException exception2) {


            Log.e(TAG, "Illegal Arguements");
            exception2.printStackTrace();

            //
            return "Illegal Arguments";
        }

        if(addresses != null && addresses.size() > 0) {

            Address address = addresses.get(0);

            String addressText = address.getAddressLine(0);

            return addressText;
        } else {

            return "No address found for location";
        }
    }

    @Override
    protected void onPostExecute(String address) {


        mainActivity.setProgressBarInvisible();


       mainActivity.setAddress(address);
    }
}
