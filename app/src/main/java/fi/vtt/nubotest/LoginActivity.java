package fi.vtt.nubotest;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.concurrent.Executor;

import fi.vtt.nubomedia.kurentoroomclientandroid.KurentoRoomAPI;
import fi.vtt.nubomedia.kurentoroomclientandroid.RoomError;
import fi.vtt.nubomedia.kurentoroomclientandroid.RoomListener;
import fi.vtt.nubomedia.kurentoroomclientandroid.RoomNotification;
import fi.vtt.nubomedia.kurentoroomclientandroid.RoomResponse;
import fi.vtt.nubomedia.utilitiesandroid.LooperExecutor;
import fi.vtt.nubotest.util.Constants;

/**
 * Login Activity for the first time the app is opened, or when a user clicks the sign out button.
 * Saves the username in SharedPreferences.
 */
public class LoginActivity extends AppCompatActivity {

    private String TAG = "LoginActivity";

    private EditText mUsername, mRoomname;
    private Context mContext;
    private SharedPreferences mSharedPreferences;
    //private LooperExecutor executor;
    //private KurentoRoomAPI kurentoRoomAPI;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        mContext = this;

        this.mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        mUsername = (EditText) findViewById(R.id.username);
        mRoomname = (EditText) findViewById(R.id.roomname);

        /*
        Bundle extras = getIntent().getExtras();
        if (extras != null){
            String lastUsername = extras.getString("oldUsername", "");
            mUsername.setText(lastUsername);
        }
        */

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent intent = new Intent(mContext, PreferencesActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();

        /*
        String wsUri = this.mSharedPreferences.getString(Constants.SERVER_NAME, Constants.DEFAULT_SERVER);


        if(executor==null) {
            executor = new LooperExecutor();
            executor.requestStart();
        }
        if(kurentoRoomAPI==null) {
            kurentoRoomAPI = new KurentoRoomAPI(executor, wsUri, this);
        }

        if(kurentoRoomAPI!=null) {

            if (!kurentoRoomAPI.isWebSocketConnected())
                kurentoRoomAPI.connectWebSocket();
        }
        */
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    };
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    };

    /**
     * Takes the username from the EditText, check its validity and saves it if valid.
     *   Then, redirects to the MainActivity.
     * @param view Button clicked to trigger call to joinChat
     */
    public void joinRoom(View view){
        String username = mUsername.getText().toString();
        String roomname = mRoomname.getText().toString();
        if (!validUsername(username) || !validRoomname(roomname))
            return;

        SharedPreferences.Editor edit = mSharedPreferences.edit();
        edit.putString(Constants.USER_NAME, username);
        edit.putString(Constants.ROOM_NAME, roomname);
        //edit.apply();
        edit.commit();

        Intent intent = new Intent(mContext, MainActivity.class);
        startActivity(intent);

        /*

        if(kurentoRoomAPI.isWebSocketConnected()) {
            Intent intent = new Intent(mContext, MainActivity.class);
            startActivity(intent);
        }
        else
            showToast(getApplicationContext().getString(R.string.not_connected));
        */

    }

    public void showToast(String string) {
        try {
            CharSequence text = string;
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(this, text, duration);
            toast.show();
        }
        catch (Exception e){e.printStackTrace();}
    }

    /**
     * Optional function to specify what a username in your chat app can look like.
     * @param username The name entered by a user.
     * @return is username valid
     */
    private boolean validUsername(String username) {
        if (username.length() == 0) {
            mUsername.setError("Username cannot be empty.");
            return false;
        }
        if (username.length() > 16) {
            mUsername.setError("Username too long.");
            return false;
        }
        return true;
    }

    /**
     * Optional function to specify what a username in your chat app can look like.
     * @param roomname The name entered by a user.
     * @return is username valid
     */
    private boolean validRoomname(String roomname) {
        if (roomname.length() == 0) {
            mRoomname.setError("Roomname cannot be empty.");
            return false;
        }
        if (roomname.length() > 16) {
            mRoomname.setError("Roomname too long.");
            return false;
        }
        return true;
    }

}
