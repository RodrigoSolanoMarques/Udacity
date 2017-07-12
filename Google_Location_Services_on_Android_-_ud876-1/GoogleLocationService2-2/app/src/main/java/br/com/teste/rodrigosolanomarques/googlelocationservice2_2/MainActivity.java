package br.com.teste.rodrigosolanomarques.googlelocationservice2_2;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionApi;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, ResultCallback<Status> {

    private TextView mStatusText;
    private Button mRequest_activity_updates_button;
    private Button mRemove_activity_updates_button;

    protected GoogleApiClient mGoogleApiClient;
    protected ActivityDetectionBroadcasrReciever mBroadcasrReciever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStatusText = (TextView) findViewById(R.id.detectedActivities);
        mRequest_activity_updates_button = (Button) findViewById(R.id.request_activity_updates_button);
        mRemove_activity_updates_button = (Button) findViewById(R.id.remove_activity_updates_button);
        mBroadcasrReciever = new ActivityDetectionBroadcasrReciever();

        buildGoogleApiClient();
    }

    private void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mGoogleApiClient.disconnect();
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcasrReciever);
        super.onPause();
    }

    @Override
    protected void onResume() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcasrReciever, new IntentFilter(Constants.BROADCASR_ACTION));
        super.onResume();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void requestActivityUpdatesButtonHandler(View view) {

        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_LONG).show();
            return;
        }

        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, 2000, getActivityDetectionPendingIntent())
                .setResultCallback(this);
        mRequest_activity_updates_button.setEnabled(false);
        mRemove_activity_updates_button.setEnabled(true);

    }

    public void removeActivityUpdatesButtonHandler(View view) {

        if (!mGoogleApiClient.isConnected()) {
            Toast.makeText(this, getString(R.string.not_connected), Toast.LENGTH_LONG).show();
            return;
        }

        ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(mGoogleApiClient, getActivityDetectionPendingIntent())
                .setResultCallback(this);
        mRequest_activity_updates_button.setEnabled(true);
        mRemove_activity_updates_button.setEnabled(false);
    }

    public String getActivityString(int detectedAtivityType) {

        Resources resources = this.getResources();

        switch (detectedAtivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            default:
                return resources.getString(R.string.unidentifiable_activity);
        }
    }

    @Override
    public void onResult(@NonNull Status status) {
        if (status.isSuccess()) {
            Log.e("", "Successfully added activity detection.");
        } else {
            Log.e("", "Error addind or removing activity detection: " + status.getStatusMessage());
        }
    }

    private PendingIntent getActivityDetectionPendingIntent(){

        Intent intent = new Intent(this, DetectedActivitiesIntentService.class);
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public class ActivityDetectionBroadcasrReciever extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<DetectedActivity> updatedActivities = intent.getParcelableArrayListExtra(Constants.ACTIVITY_EXTRA);

            String strStatus = "";
            for (DetectedActivity thisActivity : updatedActivities) {

                strStatus = getActivityString(thisActivity.getType()) + thisActivity.getConfidence() + "%\n";
            }

            mStatusText.setText(strStatus);
        }


    }
}
