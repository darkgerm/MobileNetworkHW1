package nctu.darkgerm.mnhw1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import android.util.Log;

/**
 * Usage:
 *  SocketConnect socket = new SocketConnect("ip.addr", port);
 *  socket.send("Hello world");
 *  String receive = socket.recv();
 *  socket.close();
 */

class SocketConnect {
    boolean thread_done;
    String receive;

    Socket socket;
    BufferedReader reader;
    PrintWriter writer;

    public SocketConnect(String addr, int port) {
        Log.i("SocketConnect", addr);
        Log.i("SocketConnect", Integer.toString(port));
        
        class CreateSocketThread implements Runnable {
            String addr;
            int port;

            public CreateSocketThread(String addr, int port) {
                this.addr = addr;
                this.port = port;
            }

            @Override
            public void run() {
                try {
                    socket = new Socket(addr, port);
                    reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
                }
                catch(IOException e) {
                    Log.e("SocketConnect", "socket open fail.", e);
                }
                finally {
                    thread_done = true;
                }
            }
        }

        thread_done = false;
        new Thread(new CreateSocketThread(addr, port)).start();

        // sleep for 100 sec to wait done.
        while(!thread_done) {
            try {
                Thread.sleep(100);
            }
            catch(InterruptedException e) {
                Log.e("SocketConnect", "sleep error", e);
            }
        }
    }

    public String recv() {
        receive = "";
        thread_done = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try                     { receive = reader.readLine(); }
                catch(IOException e)    { receive = "Error in receiveMsg."; }
                finally { thread_done = true; }
            }
        }).start();

        // sleep for 100 sec to wait done.
        while(!thread_done) {
            try {
                Thread.sleep(100);
            }
            catch(InterruptedException e) {
                Log.e("SocketConnect", "sleep error", e);
            }
        }
        return receive;
    }

    public void send(String msg) {
        writer.println(msg);
        writer.flush();
    }

    public void close() {
        try {
            this.send("end");
            writer.close();
            reader.close();
            socket.close();
        }
        catch(IOException e) {
            Log.e("SocketConnect", "socket close fail.", e);
        }
    }

} //class SocketConnect

