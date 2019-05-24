package company.com.remdeskacc;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;

import java.net.InetAddress;
import java.net.Socket;

public class AppDelegate extends Application {

//    private ClientThread client;
    private Client client_;
    private Controller controller;

    public static final String BROADCAST_FILTER = "ManageConection_broadcast_receiver_intent_filter";

    public void setController(Controller c){
        controller	= c;
    }

    public Controller getController()
    {
        return controller;
    }

    public void createScreenCaptureThread(InetAddress addr, int serverPort)
    {
        client_ = new Client(addr, serverPort, this);
        client_.execute();
    }

    public void sendMessage(String message){
        if(client_ != null)
            client_.sendMessage(message);
    }

    public void setMessage(String message){
        System.out.println("!!!setMessage");
        Intent i = new Intent(BROADCAST_FILTER);
        i.putExtra("message", message);
        sendBroadcast(i);
    }

    public boolean isNetworkConnected(){
        final ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
