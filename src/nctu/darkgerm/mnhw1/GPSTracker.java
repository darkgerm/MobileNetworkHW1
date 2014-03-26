package nctu.darkgerm.mnhw1;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;


/**
 * Usage:
 *  GPSTracker gps = new GPSTracker(this);
 *  gps.getLocation();  //Location object
 *  gps.getLatitude();  //double
 *  gps.getLongitude(); //double
 *  gps.stopUsingGPS() {
 *
 * If gps is not available, gps.getLocation() will return null,
 * you can use GPSTracker.showSettingsAlert(this) to open the setting dialog.
 */

public class GPSTracker implements LocationListener{

    final Context mContext;
    int mode = 0;       // 0: unavailable  1: gps  2: network
    LocationManager locmgr;
    Location loc;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000; // 1 sec


    public GPSTracker(Context context) {
        this.mContext = context;

        // get and set locmgr
        locmgr = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        if(locmgr.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locmgr.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                this
            );
            Log.i("GPSTracker", "GPS Enabled");
            mode = 1;
        }
        else if(locmgr.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locmgr.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MIN_TIME_BW_UPDATES,
                MIN_DISTANCE_CHANGE_FOR_UPDATES,
                this
            );
            Log.i("GPSTracker", "Network Enabled");
            mode = 2;
        }
        else {
            Log.e("GPSTracker", "Cannot get LocationManager.");
        }
    }

    public Location getLocation() {
        if(mode == 1) {
            loc = locmgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        }else if(mode == 2) {
            loc = locmgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }else {
            loc = null;
        }
        return loc;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS() {
        if(locmgr != null){
            locmgr.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Shortcut method for getLocation().getLantitude().
     */
    public double getLatitude() {
        this.getLocation();
        if(loc !=  null){ return loc.getLatitude(); }
        return 0;
    }

    /**
     * Shortcut method for getLocation().getLongitude().
     */
    public double getLongitude() {
        this.getLocation();
        if(loc!= null){ return loc.getLongitude(); }
        return 0;
    }

    /**
     * Helper function to show settings alert dialog
     * On pressing Settings button will lauch Settings Options
     * */
    public static void showSettingsAlert(Context context) {

        class GPSDialogOnClickListener implements DialogInterface.OnClickListener {
            Context context;
            public GPSDialogOnClickListener(Context context) {
                this.context = context;
            }
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        }

        (new AlertDialog.Builder(context))
            .setTitle("GPS settings")
            .setMessage("GPS is not enabled. Do you want to go to settings menu?")
            .setPositiveButton("OK", new GPSDialogOnClickListener(context))
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            })
            .show();
    }

    @Override
    public void onLocationChanged(Location location) {}

    @Override
    public void onProviderDisabled(String provider) {}

    @Override
    public void onProviderEnabled(String provider) {}

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

} // class GPSTracker

