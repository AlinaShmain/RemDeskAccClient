package company.com.remdeskacc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.content.DialogInterface;
import android.widget.Button;
import android.widget.EditText;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public class MainActivity extends Activity {

    private EditText ipField;
    private EditText portField;

    public static final String PREFS_NAME = "TouchSettings";
    public static final String IP_PREF = "ip_pref";
    public static final String PORT_PREF = "port_pref";

    private static final Pattern PARTIAl_IP_ADDRESS =
            Pattern.compile("^((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])\\.){0,3}"+
                    "((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9])){0,1}$");

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            System.out.println("!!!onReceive");

            String msg = intent.getStringExtra("message");

            if(msg.equals("connection_failed")) {
                serverUnreachablealert();
            } else if(msg.equals("connection_established")){
                Toast.makeText(getApplicationContext(), "Connected success", Toast.LENGTH_LONG).show();
                Intent controller = new Intent(getApplicationContext(), Controller.class);
                startActivity( controller );
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ipField = (EditText) findViewById(R.id.EditText01);
        portField = (EditText) findViewById(R.id.EditText02);

        ipField.addTextChangedListener(new TextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void beforeTextChanged(CharSequence s,int start,int count,int after) {}

            private String mPreviousText = "";
            @Override
            public void afterTextChanged(Editable s) {
                if(PARTIAl_IP_ADDRESS.matcher(s).matches()) {
                    mPreviousText = s.toString();
                } else {
                    s.replace(0, s.length(), mPreviousText);
                }
            }
        });

        Button connectbutton = (Button) findViewById(R.id.Button01);
        connectbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                connectToServer();

                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = prefs.edit();

                editor.putString(PORT_PREF, portField.getText().toString());
                editor.putString(IP_PREF, ipField.getText().toString());

                editor.commit();
            }
        });
    }

    @Override
    protected void onResume() {
        registerReceiver(mReceiver, new IntentFilter(AppDelegate.BROADCAST_FILTER));
        super.onResume();

        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);

        String ip = prefs.getString(IP_PREF, "192.168.1.2");
        String port = prefs.getString(PORT_PREF, "5554");

        ipField.setText(ip);
        portField.setText(port);
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mReceiver);
        super.onPause();
    }

//    @Override
//    protected void onDestroy() {
//        unregisterReceiver(mReceiver);
//        super.onDestroy();
//    }

    private void networkUnavailableAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Network Unreachable")
                .setMessage("Your device is not connected to a network.")
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
//                                return;
                            }
                        });
        AlertDialog network_alert = builder.create();
        network_alert.show();
    }

    public void serverUnreachablealert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Server Connection Unavailable")
                .setMessage("Please make sure the server is running on your computer.")
                .setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
//                                return;
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void connectToServer() {
        AppDelegate appDel = ((AppDelegate) getApplicationContext());

        if (!appDel.isNetworkConnected()) {
            networkUnavailableAlert();
            return;
        }

        System.out.println("!!!BUtton");

        try {
            InetAddress host = InetAddress.getByName(ipField.getText().toString());
            int serverPort = Integer.parseInt(portField.getText().toString());
            appDel.createScreenCaptureThread(host, serverPort);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
}