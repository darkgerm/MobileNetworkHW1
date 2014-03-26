package nctu.darkgerm.mnhw1;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;


/**
 * Usage:
 *  SensorTracker sensor = new SensorTracker(this);
 *  sensor.registerListener();      // start to listen
 *  sensor.unregisterListener();    // stop to listen
 *
 *  sensor.getOrientation();        // float[3]
 *  sensor.getAccelerometer();      // float[3]
 *
 *  if value is unavailable, return [0, 0, 0].
 */

public class SensorTracker implements SensorEventListener {

    private final SensorManager snrmgr;
    private final Sensor snr_acc, snr_mag;
    float[] accel;
    float[] geomagnetic;
    float[] orientation;

    public SensorTracker(Context context) {
        snrmgr = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        snr_acc = snrmgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        snr_mag = snrmgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
    }

    public void registerListener() {
        Log.d("SensorTracker", "registerListener");
        snrmgr.registerListener(this, snr_acc, SensorManager.SENSOR_DELAY_NORMAL);
        snrmgr.registerListener(this, snr_mag, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void unregisterListener() {
        Log.d("SensorTracker", "unregisterListener");
        snrmgr.unregisterListener(this);
    }

    public float[] getOrientation() {
        if(orientation == null) return new float[] {0, 0, 0};
        return orientation;
    }

    public float[] getAccelerometer() {
        if(accel == null) return new float[] {0, 0, 0};
        return accel;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accel = event.values;
        }
        if(event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values;
        }
        if(accel != null && geomagnetic != null) {
            float R[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, null, accel, geomagnetic);
            if (success) {
                orientation = new float[3];
                SensorManager.getOrientation(R, orientation);
                //Log.e("SensorTracker", "orient = " +
                //    "("+orientation[0]+", "+orientation[1]+", "+orientation[2]+")"
                //);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

} //class SensorTracker

