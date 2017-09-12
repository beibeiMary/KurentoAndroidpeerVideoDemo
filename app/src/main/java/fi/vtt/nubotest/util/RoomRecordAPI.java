package fi.vtt.nubotest.util;

import android.util.Log;

import net.minidev.json.JSONObject;

import org.java_websocket.client.DefaultSSLWebSocketClientFactory;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Vector;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import fi.vtt.nubomedia.jsonrpcwsandroid.JsonRpcNotification;
import fi.vtt.nubomedia.jsonrpcwsandroid.JsonRpcResponse;
import fi.vtt.nubomedia.kurentoroomclientandroid.KurentoAPI;
import fi.vtt.nubomedia.kurentoroomclientandroid.KurentoRoomAPI;
import fi.vtt.nubomedia.kurentoroomclientandroid.RoomError;
import fi.vtt.nubomedia.kurentoroomclientandroid.RoomListener;
import fi.vtt.nubomedia.kurentoroomclientandroid.RoomNotification;
import fi.vtt.nubomedia.kurentoroomclientandroid.RoomResponse;
import fi.vtt.nubomedia.utilitiesandroid.LooperExecutor;

/**
 * Created by Mateusz on 2016-07-05.
 */

public class RoomRecordAPI extends KurentoAPI {
    private static final String LOG_TAG = "KurentoRoomAPI";
    private KeyStore keyStore;
    private boolean usingSelfSigned = false;
    private Vector<RoomListener> listeners;

    /**
     * Constructor that initializes required instances and parameters for the API calls.
     * WebSocket connections are not established in the constructor. User is responsible
     * for opening, closing and checking if the connection is open through the corresponding
     * API calls.
     *
     * @param executor is the asynchronous UI-safe executor for tasks.
     * @param uri is the web socket link to the room web services.
     * @param listener interface handles the callbacks for responses, notifications and errors.
     */
    public RoomRecordAPI(LooperExecutor executor, String uri, RoomListener listener){
        super(executor, uri);

        listeners = new Vector<RoomListener>();
        listeners.add(listener);

        // Create a KeyStore containing our trusted CAs
        try {
            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            keyStore.load(null, null);
        } catch (KeyStoreException | CertificateException | NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method for the user to join the room. If the room does not exist previously,
     * it will be created.
     *
     * The response will contain the list of users and their streams in the room.
     *
     * @param userId is the username as it appears to all other users.
     * @param roomId is the name of the room to be joined.
     * @param id is an index number to track the corresponding response message to this request.
     */
    public void sendJoinRoom(String userId, String roomId, int id){
        HashMap<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("user", userId);
        namedParameters.put("room", roomId);
        send("joinRoom", namedParameters, id);
    }

    public void sendRecordRequest(String roomId, boolean record, int id) {
        HashMap<String, Object> namedParameters = new HashMap<>();
        namedParameters.put("room", roomId);
        namedParameters.put("record", String.valueOf(record));

        send("recordRequest", namedParameters, id);
    }

    /**
     * Method will leave the current room.
     *
     * @param id is an index number to track the corresponding response message to this request.
     */
    public void sendLeaveRoom(int id){
        send("leaveRoom", null, id);
    }

    /**
     * Method to publish a video. The response will contain the sdpAnswer attribute.
     *
     * @param sdpOffer is a string sent by the client
     * @param doLoopback is a boolean value enabling media loopback
     * @param id is an index number to track the corresponding response message to this request.
     */
    public void sendPublishVideo(String sdpOffer, boolean doLoopback, int id){
        HashMap<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("sdpOffer", sdpOffer);
        namedParameters.put("doLoopback", new Boolean(doLoopback));
        send("publishVideo", namedParameters, id);
    }

    /**
     * Method unpublishes a previously published video.
     *
     * @param id is an index number to track the corresponding response message to this request.
     */
    public void sendUnpublishVideo(int id){
        send("unpublishVideo", null, id);
    }

    /**
     * Method represents the client's request to receive media from participants in
     * the room that published their media. The response will contain the sdpAnswer attribute.
     *
     * @param sender is a combination of publisher's name and his currently opened stream
     *               (usually webcam) separated by underscore. For example: userid_webcam
     * @param sdpOffer is the SDP offer sent by this client.
     * @param id is an index number to track the corresponding response message to this request.
     */
    public void sendReceiveVideoFrom(String sender, String sdpOffer, int id){
        HashMap<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("sdpOffer", sdpOffer);
        namedParameters.put("sender", sender);
        send("receiveVideoFrom", namedParameters, id);
    }

    /**
     * Method represents a client's request to stop receiving media from a given publisher.
     * Response will contain the sdpAnswer attribute.
     *
     * @param userId is the publisher's username.
     * @param streamId is the name of the stream (typically webcam)
     * @param id is an index number to track the corresponding response message to this request.
     */
    public void sendUnsubscribeFromVideo(String userId, String streamId, int id){
        String sender = userId+"_"+streamId;
        HashMap<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("sender", sender);
        send("unsubscribeFromVideo", namedParameters, id);
    }

    /**
     * Method carries the information about the ICE candidate gathered on the client side.
     * This information is required to implement the trickle ICE mechanism.
     *
     * @param endpointName is the username of the peer whose ICE candidate was found
     * @param candidate contains the candidate attribute information
     * @param sdpMid is the media stream identification, "audio" or "video", for the m-line,
     *               this candidate is associated with.
     * @param sdpMLineIndex is the index (starting at 0) of the m-line in the SDP,
     *                      this candidate is associated with.
     */
    public void sendOnIceCandidate(String endpointName, String candidate, String sdpMid, String sdpMLineIndex, int id){
        HashMap<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("endpointName", endpointName);
        namedParameters.put("candidate", candidate);
        namedParameters.put("sdpMid", sdpMid);
        namedParameters.put("sdpMLineIndex", sdpMLineIndex);
        send("onIceCandidate", namedParameters, id);
    }

    /**
     * Method sends a message from the user to all other participants in the room.
     *
     * @param roomId is the name of the room.
     * @param userId is the username of the user sending the message.
     * @param message is the text message sent to the room.
     * @param id is an index number to track the corresponding response message to this request.
     */
    public void sendMessage(String roomId, String userId, String message, int id){
        HashMap<String, Object> namedParameters = new HashMap<String, Object>();
        namedParameters.put("message", message);
        namedParameters.put("userMessage", userId);
        namedParameters.put("roomMessage", roomId);
        send("sendMessage", namedParameters, id);
    }

    /**
     * Method to send any custom requests that are not directly implemented by the Room server.
     *
     * @param names is an array of parameter names.
     * @param values is an array of parameter values where the index is corresponding with
     *               the applicable name value in the names array.
     * @param id is an index number to track the corresponding response message to this request.
     */
    public void sendCustomRequest(String[] names, String[] values, int id){
        if(names==null || values==null||names.length!=values.length){
            return;  // mismatching name-value pairs
        }
        HashMap<String, Object> namedParameters = new HashMap<String, Object>();
        for(int i=0;i<names.length;i++) {
            namedParameters.put(names[i], values[i]);
        }
        send("customRequest", namedParameters, id);

    }

    /**
     * This methods can be used to add a self-signed SSL certificate to be trusted when establishing
     * connection.
     * @param alias is a unique alias for the certificate
     * @param cert is the certificate object
     */
    public void addTrustedCertificate(String alias, Certificate cert){
        try {
            keyStore.setCertificateEntry(alias, cert);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }

    /**
     * Switches on/off the self-signed certificate support.
     *
     * @see KurentoRoomAPI#addTrustedCertificate(String, Certificate)
     * @param use
     */
    public void useSelfSignedCertificate(boolean use){
        this.usingSelfSigned = use;
    }

    /**
     * Opens a web socket connection to the predefined URI as provided in the constructor.
     * The method responds immediately, whether or not the connection is opened.
     * The method isWebSocketConnected() should be called to ensure that the connection is open.
     * Secure socket is created if protocol contained in Uri is either https or wss.
     */
    public void connectWebSocket() {
        if(isWebSocketConnected()){
            return;
        }

        // Switch to SSL web socket client factory if secure protocol detected
        String scheme = null;
        try {
            scheme = new URI(wsUri).getScheme();
            if (scheme.equals("https") || scheme.equals("wss")){

                // Create an SSLContext that uses our or default TrustManager
                SSLContext sslContext = SSLContext.getInstance("TLS");

                if (usingSelfSigned) {
                    // Create a TrustManager that trusts the CAs in our KeyStore
                    String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
                    TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
                    tmf.init(keyStore);
                    sslContext.init(null, tmf.getTrustManagers(), null);
                } else {
                    sslContext.init(null, null, null);
                }
                webSocketClientFactory = new DefaultSSLWebSocketClientFactory(sslContext);
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        super.connectWebSocket();
    }

    /* WEB SOCKET CONNECTION EVENTS */

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        super.onOpen(handshakedata);

        synchronized (listeners) {
            for (RoomListener rl : listeners) {
                rl.onRoomConnected();
            }
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        super.onClose(code, reason, remote);

        synchronized (listeners) {
            for (RoomListener rl : listeners) {
                rl.onRoomDisconnected();
            }
        }
    }

    /**
     * Callback method that relays the RoomResponse or RoomError to the RoomListener interface.
     */
    @Override
    public void onResponse(JsonRpcResponse response) {
        if(response.isSuccessful()){
            JSONObject jsonObject = (JSONObject)response.getResult();
            RoomResponse roomResponse = new RoomResponse(response.getId().toString(), jsonObject);

            synchronized (listeners) {
                for (RoomListener rl : listeners) {
                    rl.onRoomResponse(roomResponse);
                }
            }
        } else {
            RoomError roomError = new RoomError(response.getError());

            synchronized (listeners) {
                for (RoomListener rl : listeners) {
                    rl.onRoomError(roomError);
                }
            }
        }
    }

    /**
     * Callback method that relays the RoomNotification to the RoomListener interface.
     */
    @Override
    public void onNotification(JsonRpcNotification notification) {
        RoomNotification roomNotification = new RoomNotification(notification);

        synchronized (listeners) {
            for (RoomListener rl : listeners) {
                rl.onRoomNotification(roomNotification);
            }
        }
    }

    public void addObserver(RoomListener listener){
        synchronized (listeners) {
            listeners.add(listener);
        }
    }

    public void removeObserver(RoomListener listener){
        synchronized (listeners) {
            listeners.remove(listener);
        }
    }

    /**
     * Callback method that relays error messages to the RoomListener interface.
     * @param e The exception instance
     */
    @Override
    public void onError(Exception e) {
        Log.e(LOG_TAG, "onError: "+e.getMessage(), e);
    }

}
