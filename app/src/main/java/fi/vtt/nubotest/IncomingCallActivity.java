package fi.vtt.nubotest;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import fi.vtt.nubotest.util.Constants;


public class IncomingCallActivity extends Activity {
    private SharedPreferences mSharedPreferences;
    private String username;
    private String callUser;
    private String TAG = "IncomingCallActivity";

    //private Pubnub mPubNub;
    private TextView mCallerID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_call);

        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (!this.mSharedPreferences.contains(Constants.USER_NAME)){

            finish();
            return;
        }
        this.username = this.mSharedPreferences.getString(Constants.USER_NAME, "");

        Bundle extras = getIntent().getExtras();
        if (extras==null || !extras.containsKey(Constants.CALL_USER)){

            Toast.makeText(this, "Need to pass username to IncomingCallActivity in intent extras (Constants.CALL_USER).",
                    Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        this.callUser = extras.getString(Constants.CALL_USER, "");

        Log.wtf(TAG, "username: "+username);
        Log.wtf(TAG, "callUser: "+callUser);

        this.mCallerID = (TextView) findViewById(R.id.caller_id);
        this.mCallerID.setText(this.callUser);

        //this.mPubNub  = new Pubnub(Constants.PUB_KEY, Constants.SUB_KEY);
        //this.mPubNub.setUUID(this.username);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_incoming_call, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void acceptCall(View view){
        Intent intent = new Intent(IncomingCallActivity.this, PeerVideoActivity.class);
        intent.putExtra(Constants.USER_NAME, this.username); 
        intent.putExtra(Constants.CALL_USER, this.callUser);
        startActivity(intent);
        finish();
    }

    /**
     * Publish a hangup command if rejecting call.
     * @param view
     */
    public void rejectCall(View view){
        /*
        JSONObject hangupMsg = PnPeerConnectionClient.generateHangupPacket(this.username);
        this.mPubNub.publish(this.callUser,hangupMsg, new Callback() {
            @Override
            public void successCallback(String channel, Object message) {
                finish();
            }
        });
        */
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
                /*
        if(this.mPubNub!=null){

            this.mPubNub.unsubscribeAll();
        }
        */
    };
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    };
}
