package company.com.remdeskacc;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientListener extends AsyncTask<Void, Void, Void> {

    private Socket socket;
    private AppDelegate delegate;
    private Client client;

    private DataInputStream dis;


    public ClientListener(Client client, AppDelegate del){
        this.client = client;
        this.socket = client.socket;
        delegate = del;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            dis = new DataInputStream(socket.getInputStream());
            while(!socket.isClosed()) {
                int len = dis.readInt();
                byte[] buffer = new byte[len];

                int bytes, offset = 0;
                while (true) {
                    bytes = dis.read(buffer, offset, buffer.length - offset);
                    offset += bytes;
                    if (bytes == -1 || offset >= buffer.length) break;
                }
                Log.i("BUFFER", Integer.toString(buffer.length));

                Bitmap bmpScrn = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
                delegate.getController().setImage(bmpScrn);
            }
        } catch (IOException e) {
            delegate.setMessage("connection_failed");
            client.close();
            client.listener.cancel(true);
            this.cancel(true);
        }
//        while(!socket.isClosed()) {
//            byte[] buffer;

//            try {
//                InputStream in = socket.getInputStream();
//                DataInputStream dis = new DataInputStream(in);

//                if (in != null) {
//
//                    int len = dis.readInt();
//                    buffer = new byte[len];
//
//                    int bytes, offset = 0;
//                    while (true) {
//                        bytes = dis.read(buffer, offset, buffer.length - offset);
//                        offset += bytes;
//                        if (bytes == -1 || offset >= buffer.length) break;
//                    }
//                    Log.i("BUFFER", Integer.toString(buffer.length));
//
//                    Bitmap bmpScrn = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
//                    delegate.getController().setImage(bmpScrn);
//                }
//            } catch(IOException e){
//                e.printStackTrace();
//            }
//        }
        return null;
    }
}
