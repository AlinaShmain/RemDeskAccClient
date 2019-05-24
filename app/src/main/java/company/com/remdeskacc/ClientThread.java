//package company.com.remdeskacc;
//
//import android.os.AsyncTask;
//import android.util.Log;
//
//import java.io.BufferedReader;
//import java.io.DataInputStream;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.net.InetAddress;
//import java.net.Socket;
//import java.net.UnknownHostException;
//
//public class ClientThread extends AsyncTask<Void, Void, Void> {
//
//    private InetAddress serverAddr;
//    private int serverPort;
//
//    private Socket socket;
//    private DataInputStream dis;
//    private DataOutputStream dos;
//
//    private AppDelegate delegate;
//    ClientListener listener;
//
//    public boolean connected = false;
//
//    String message;
//
//    public ClientThread(InetAddress addr, int port, AppDelegate del){
//        delegate = del;
//        serverAddr = addr;
//        serverPort = port;
//    }
//
//    @Override
//    protected Void doInBackground(Void... voids) {
//        Log.i("BACKGR", "here");
//        try {
//            socket = new Socket(serverAddr, serverPort);
//            Log.i("HOST", serverAddr.toString());
//            Log.i("PORT", Integer.toString(serverPort));
//
//            connected = testConnection();
//            if(connected){
//                listener = new ClientListener(socket, delegate);
//                listener.execute();
//            }
//
//        } catch (UnknownHostException e) {
//            System.out.println("Unknown host");
//        } catch (IOException e) {
//            Log.e("ClientActivity", "Client Connection Error", e);
//        }
//        return null;
//    }
//
//    public void sendMessage(String message){
//        try {
//            if(dos != null){
//                dos.writeUTF(message);
//                dos.flush();
//            }
//        }
//        catch (IOException e){
//        }
//    }
//
//    /*
//     * Used to test connection with the server.
//     */
//    private boolean testConnection(){
//        try {
//            message = "Connected";
//            Log.i("conn", message);
//
//            dos = new DataOutputStream(socket.getOutputStream());
//            dos.writeUTF(message);
//            dos.flush();
//        } catch(IOException e) {
//            System.err.println(e.getMessage());
//            return false;
//        }
//
//        try {
//            dis = new DataInputStream(socket.getInputStream());
//
//            int len = dis.readInt();
//            byte[] buffer = new byte[len];
//
//            int bytes, offset = 0;
//            while (true) {
//                bytes = dis.read(buffer, offset, buffer.length - offset);
//                offset += bytes;
//                if (bytes == -1 || offset >= buffer.length) break;
//            }
//
//            return true;
//        } catch (IOException e) {
//            System.err.println(e.getMessage());
//            return false;
//        }
//    }
//
//    private void close() {
//        try {
//            socket.close();
//        } catch(IOException e) {
//            System.err.println(e.getMessage());
//        }
//    }
//}
