package nctu.darkgerm.mnhw1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
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
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}



/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment extends Fragment {

    View view;
    Activity activity;
    TextView tv_GPS, tv_Orient, tv_Accel;

    CountDownTimer refresh_timer;
    GPSTracker gps = null;
    SensorTracker sensor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        view = inflater.inflate(
            R.layout.fragment_main,
            container,
            false
        );
        activity = this.getActivity();

        tv_GPS = (TextView) view.findViewById(R.id.GPS);
        tv_Orient = (TextView) view.findViewById(R.id.Orient);
        tv_Accel = (TextView) view.findViewById(R.id.Accel);
        sensor = new SensorTracker(activity);

        this.bindSendButtonHandler();

        // Start onclick handler
        ((Button) view.findViewById(R.id.Start))
            .setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gps == null) {
                    gps = new GPSTracker(activity);
                    if(gps.getLocation() == null) {
                        GPSTracker.showSettingsAlert(activity);
                    }
                }
                sensor.registerListener();
                refresh_timer.start();
                Log.d("main", "start capture");
            }
        });

        // Stop onclick handler
        ((Button) view.findViewById(R.id.Stop))
            .setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                refresh_timer.cancel();
                gps.stopUsingGPS();
                gps = null;
                sensor.unregisterListener();
                Log.d("main", "stop capture");
            }
        });

        // refresh_timer
        refresh_timer = new CountDownTimer(864000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                double lantitude = gps.getLatitude();
                double longitude = gps.getLongitude();
                tv_GPS.setText("GPS = (" + lantitude + ", " + longitude + ")");

                float[] orient = sensor.getOrientation();
                tv_Orient.setText(
                    "Orientation = " +
                    "(" + orient[0] + ", " + orient[1] + ", " + orient[2] + ")"
                );

                float[] accel = sensor.getAccelerometer();
                tv_Accel.setText(
                    "Accelerometer = " +
                    "(" + accel[0] + ", " + accel[1] + ", " + accel[2] + ")"
                );
            }
            
            @Override
            public void onFinish() {}
        };

        return view;
    } // PlaceholderFragment.onCreateView


    private void bindSendButtonHandler() {
        final EditText et_IP = (EditText) view.findViewById(R.id.IP);
        final EditText et_Port = (EditText) view.findViewById(R.id.Port);

        class SendOnClickListener implements OnClickListener {
            TextView tv;
            public SendOnClickListener(TextView tv) {
                this.tv = tv;
            }
            @Override
            public void onClick(View v) {
                // send and recv things
                SocketConnect socket = new SocketConnect(
                    et_IP.getText().toString(),
                    Integer.parseInt(et_Port.getText().toString())
                    //"140.113.131.68", 10000
                );
                socket.send(tv.getText().toString());
                String receive = socket.recv();
                socket.close();

                //show it
                (new AlertDialog.Builder(activity))
                    .setTitle("Message")
                    .setMessage(receive)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {}
                    })
                    .show();
            }
        }

        ((Button) view.findViewById(R.id.SendGPS))
            .setOnClickListener(new SendOnClickListener(tv_GPS));
        ((Button) view.findViewById(R.id.SendOrient))
            .setOnClickListener(new SendOnClickListener(tv_Orient));
        ((Button) view.findViewById(R.id.SendAccel))
            .setOnClickListener(new SendOnClickListener(tv_Accel));
    } // PlaceholderFragment.bindSendButtonHandler

} // class PlaceholderFragment

