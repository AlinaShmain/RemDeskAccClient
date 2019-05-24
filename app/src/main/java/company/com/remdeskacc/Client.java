package company.com.remdeskacc;

import android.os.AsyncTask;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends AsyncTask<Void, Void, Void> {

    private InetAddress serverAddr;
    private int serverPort;

    private AppDelegate delegate;

    public Socket socket;
    public ClientListener listener;
    private DataOutputStream dos;


    public Client(InetAddress addr, int port, AppDelegate del) {
        serverAddr = addr;
        serverPort = port;
        delegate = del;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            System.out.println("!!!BACK");
            socket = new Socket(serverAddr, serverPort);
            System.out.println("!!!CONNECT");
            delegate.setMessage("connection_established");
        } catch (IOException e) {
            System.out.println("!!!CONNECTION FAIL");
            delegate.setMessage("connection_failed");
            this.cancel(true);
        }

        listener = new ClientListener(this, delegate);
        listener.execute();

        try {
            dos = new DataOutputStream(socket.getOutputStream());
        } catch(IOException e) {
            delegate.setMessage("connection_failed");
            this.cancel(true);
        }

        return null;
    }

    public void sendMessage(String message){
        try {
            if(dos != null){
                dos.writeUTF(message);
                dos.flush();
            }
        }
        catch (IOException e){
            delegate.setMessage("connection_failed");
            close();
            listener.cancel(true);
            this.cancel(true);
        }
    }

    public void close() {
        try {
            socket.close();
        } catch(IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
