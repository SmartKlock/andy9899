package com.example.vinayak.imagica;

/**
 * Created by vinayak on 31/8/16.
 */
import android.util.Log;
import java.io.*;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

public class TCPClient {

    private String serverMessage;
    public static String SERVERIP = "192.168.43.1"; //your computer IP address
    public static int SERVERPORT = 1234;
    private static OnMessageReceived mMessageListener = null;
    private static boolean mRun = false,mRunning=false;

    PrintWriter out;
    BufferedReader in;

    /**
     *  Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TCPClient(OnMessageReceived listener) {
        mMessageListener = listener;
    }

    /**
     * Sends the message entered by client to the server
     * @param message text entered by client
     */
    public void sendMessage(String message){
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
        }
    }

    public static final int SERVER_STATUS_CODE_RUNNING_SIM_GR =1;
    public static final int SERVER_STATUS_CODE_RUNNING_SIM_GR_RESET =2;
    public static final int SERVER_STATUS_CODE_RUNNING_RUN_GR =3;
    public static final int SERVER_STATUS_CODE_RUNNING_RUN_GR_RESET =4;

    public static final int SERVER_STATUS_CODE_RUNNING_SIM_SG =5;
    public static final int SERVER_STATUS_CODE_RUNNING_SIM_SG_RESET =6;
    public static final int SERVER_STATUS_CODE_RUNNING_RUN_SG =7;
    public static final int SERVER_STATUS_CODE_RUNNING_RUN_SG_RESET =8;

    public static final int SERVER_STATUS_CODE_RUNNING_SIM_MITM =9;
    public static final int SERVER_STATUS_CODE_RUNNING_SIM_MITM_RESET =10;
    public static final int SERVER_STATUS_CODE_RUNNING_RUN_MITM =11;
    public static final int SERVER_STATUS_CODE_RUNNING_RUN_MITM_RESET =12;

    public float distance=0;
    public int count=0;
    public int connecteclientcount=0;
    public int serverstatus=0;
    public int[] connectedclientlist;

    public OnMessageReceived getOnMessageReceived()
    {
        return mMessageListener;
    }
    public void stopClient(){
        mRun = false;
    }

    public static void setServerip(String ip)
    {
        SERVERIP=ip;
    }

    public static void setSERVERPORT(int port){
        SERVERPORT=port;
    }

    public static boolean isPortOpen(final String ip, final int port, final int timeout) {
        if((ip.equals(SERVERIP))&&(port==SERVERPORT)&&(mRunning))
            return true;
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), timeout);
            Thread.sleep(500);
            socket.close();
            return true;
        }

        catch(ConnectException ce){
            ce.printStackTrace();
            return false;
        }

        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    static int reply_count=0;

    public boolean isRunning()
    {
        return mRunning;
    }
    public void run() {

        mRun = true;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(SERVERIP);

            Log.e("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            Socket socket = new Socket(serverAddr, SERVERPORT);

            try {

                //send the message to the server
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                Log.e("TCP Client", "C: Sent.");

                Log.e("TCP Client", "C: Done.");

                //receive the message which the server sends back
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                Thread.sleep(50);
                //in this while the client listens for the messages sent by the server
                long time=System.currentTimeMillis();
                this.sendMessage("D");
                while (mRun) {
                    mRunning=true;
                    if(in.ready()) {
                        serverMessage = in.readLine();
     //                   Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage + "'");
                    }
                    if(serverMessage!=null) {
                            boolean send=true;
                            reply_count++;
                            if (serverMessage.contains("IP's")) {
                                connecteclientcount = Integer.parseInt(serverMessage.substring(0, serverMessage.indexOf("Clients")-1));
                                if(connecteclientcount>0)
                                {
                                    connectedclientlist=new int[connecteclientcount];
                                    serverMessage=serverMessage.substring(serverMessage.indexOf("IP's")+5);
                                }
                                for(int i=0;i<connecteclientcount;i++){
                                    connectedclientlist[i]=Integer.parseInt(serverMessage.substring(0,serverMessage.indexOf(" ")));
                                    serverMessage=serverMessage.substring(serverMessage.indexOf(" ")+1);
                                }
                                //Log.println(Log.DEBUG,"Concl",serverMessage+" "+connecteclientcount);
                            }else if(serverMessage.equals("SIM GR S")){
                                serverstatus=TCPClient.SERVER_STATUS_CODE_RUNNING_SIM_GR;
                            }else if(serverMessage.equals("SIM GR R")){
                                serverstatus=TCPClient.SERVER_STATUS_CODE_RUNNING_SIM_GR_RESET;
                            }else if(serverMessage.equals("SIM SG S")){
                                serverstatus=TCPClient.SERVER_STATUS_CODE_RUNNING_SIM_SG;
                            }else if(serverMessage.equals("SIM SG R")){
                                serverstatus=TCPClient.SERVER_STATUS_CODE_RUNNING_SIM_SG_RESET;
                            }else if(serverMessage.equals("SIM MM S")){
                                serverstatus=TCPClient.SERVER_STATUS_CODE_RUNNING_SIM_MITM;
                            }else if(serverMessage.equals("SIM MM R")){
                                serverstatus=TCPClient.SERVER_STATUS_CODE_RUNNING_SIM_MITM_RESET;
                            }else if(serverMessage.equals("RUN GR S")){
                                serverstatus=TCPClient.SERVER_STATUS_CODE_RUNNING_RUN_GR;
                            }else if(serverMessage.equals("RUN GR R")){
                                serverstatus=TCPClient.SERVER_STATUS_CODE_RUNNING_RUN_GR_RESET;
                            }else if(serverMessage.equals("RUN SG S")){
                                serverstatus=TCPClient.SERVER_STATUS_CODE_RUNNING_RUN_SG;
                            }else if(serverMessage.equals("RUN SG R")){
                                serverstatus=TCPClient.SERVER_STATUS_CODE_RUNNING_RUN_SG_RESET;
                            }else if(serverMessage.equals("RUN MM S")){
                                serverstatus=TCPClient.SERVER_STATUS_CODE_RUNNING_RUN_MITM;
                            }else if(serverMessage.equals("RUN MM R")){
                                serverstatus=TCPClient.SERVER_STATUS_CODE_RUNNING_RUN_MITM_RESET;
                            }else if((serverMessage.equals("SIM FL"))||(serverMessage.equals("RUN FL"))){
                                serverstatus=0;
                            }else if(serverMessage.contains("Count=")){
                                serverMessage=serverMessage.substring(serverMessage.indexOf("=")+1);
                                this.count=Integer.parseInt(serverMessage.substring(0,serverMessage.indexOf(",")));
                                this.distance=Float.parseFloat(serverMessage.substring(serverMessage.indexOf("=")+1));
                            }else {
                                send=false;
                            }
                        if(send) {
                            serverMessage = "" + distance;
                            if (mMessageListener != null) {
                                //call the method messageReceived from MyActivity class
                                mMessageListener.messageReceived(serverMessage);
                            }
                        }
                    }
                    if((System.currentTimeMillis()-time)>500){
                        this.sendMessage("PIG");
                        time=System.currentTimeMillis();
                    }
                    serverMessage = null;

                }
                mRunning=false;
                socket.close();
   //             Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + serverMessage + "'");

            } catch (Exception e) {

                Log.e("TCP", "S: Error", e);

            } finally {
                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket.close();
            }

        } catch (Exception e) {

            Log.e("TCP", "C: Error", e);

        }

    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}