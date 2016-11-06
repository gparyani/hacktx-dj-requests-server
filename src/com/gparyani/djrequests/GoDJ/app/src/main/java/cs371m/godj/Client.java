package cs371m.godj;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Jasmine on 10/23/2016.
 */




    public class Client extends AsyncTask<Void, Void, Void> {

        private String uri;
        private Context context;

        Client(Context context, String uri) {
            this.uri = uri;
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;

            try {
                socket = new Socket(InetAddress.getByName("10.146.229.69").getHostName(), 48736);
                PrintStream writer = new PrintStream(socket.getOutputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer.println("request " + uri);
                String response = reader.readLine();
                //Toast.makeText(context, response, Toast.LENGTH_SHORT).show();

            } catch (UnknownHostException e) {

                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }

