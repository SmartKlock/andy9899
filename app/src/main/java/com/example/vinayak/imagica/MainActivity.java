package com.example.vinayak.imagica;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    String IpAddress=TCPClient.SERVERIP;
    public static Handler handle;
    public static boolean RunThis=false,ShutDown=false;
    static TCPClient client;
    static Button ResetServer,StartExperience, switchserver;
    static TextView DisplayMessage,ConnectedClients,ServerStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String previousm="";
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        boolean PRS=true,PSE=false;
        if(ResetServer!=null)
            PRS=ResetServer.isEnabled();
        if(StartExperience!=null)
            PSE=StartExperience.isEnabled();
        ResetServer=(Button)findViewById(R.id.resetserver);
        ResetServer.setEnabled(PRS);
        ResetServer.setClickable(PRS);
        StartExperience=(Button)findViewById(R.id.startexperience);
        StartExperience.setEnabled(PSE);
        StartExperience.setClickable(PSE);
        switchserver =(Button)findViewById(R.id.switchserver);
        if(DisplayMessage!=null){
            previousm=DisplayMessage.getText().toString();
        }
        DisplayMessage=(TextView)findViewById(R.id.dispmessage);
        DisplayMessage.setText(previousm);

        if(ServerStatus!=null){
            previousm=ServerStatus.getText().toString();
        }
        ServerStatus=(TextView)findViewById(R.id.serverstatus);
        ServerStatus.setText(previousm);

        ConnectedClients=(TextView)findViewById(R.id.clientsconnected);

        ResetServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        RunThis = false;
                        handle.post(new Runnable() {
                            @Override
                            public void run() {
                                DisplayMessage.setText("Resetting Server");
                                RunThis = true;
                            }
                        });
                        while (RunThis == false) ;
                        client.sendMessage("R");
                    }
                })).start();
            }
        });

        StartExperience.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        client.sendMessage("L");
                        handle.post(new Runnable() {
                            @Override
                            public void run() {

                                DisplayMessage = (TextView) findViewById(R.id.dispmessage);
                                DisplayMessage.setText("Starting Experience");
                            }
                        });
                    }
                })).start();

            }
        });

        switchserver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RunThis = false;
                AlertDialog.Builder dlgAlert;
                dlgAlert = new AlertDialog.Builder(MainActivity.this);
                dlgAlert.setMessage("Are you sure?");
                dlgAlert.setTitle("switch server?");
                dlgAlert.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ShutDown = true;
                                RunThis = true;
                            }
                        });
                dlgAlert.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                ShutDown = true;
                                RunThis = false;
                            }
                        });
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (RunThis == false) ;
                        if (ShutDown) {
                            client.sendMessage("V");
                            handle.post(new Runnable() {
                                @Override
                                public void run() {

                                    DisplayMessage = (TextView) findViewById(R.id.dispmessage);
                                    DisplayMessage.setText("switching server");
                                }
                            });
                        }
                    }
                })).start();
            }
        });

        handle=new Handler();
        final Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {

                if(TCPClient.isPortOpen(IpAddress,1234,1000))
                {
                    if((client==null)||(!client.isRunning())) {
                        handle.post(new Runnable() {
                            @Override
                            public void run() {

                                AlertDialog.Builder dlgAlert;
                                dlgAlert = new AlertDialog.Builder(MainActivity.this);
                                dlgAlert.setMessage("The server is available");
                                dlgAlert.setTitle(getString(R.string.app_name));
                                dlgAlert.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                RunThis = true;
                                            }
                                        });
                                dlgAlert.setCancelable(true);
                                dlgAlert.create().show();
                            }
                        });
                        while (RunThis == false) ;

                        TCPClient.OnMessageReceived onMessageReceived = new TCPClient.OnMessageReceived() {
                            String previousmessage = "",displaystatus="";
                            boolean RS, SE;

                            @Override
                            public void messageReceived(String message) {
                                String displaymessage="";

                                switch(client.serverstatus){
                                    case TCPClient.SERVER_STATUS_CODE_RUNNING_SIM_SG:
                                        displaystatus="Salimghar Simulation\n";
                                        if(client.distance==-2){
                                            displaymessage="Server is ready, start the experience when everything is setup\n";
                                            RS=false;
                                            SE=true;
                                        }else if(client.distance==-1){
                                            displaymessage="Waiting for old man to finish his speech\n";
                                            RS=false;
                                            SE=false;
                                        }else if(client.distance==0)
                                        {
                                            displaymessage="Old Man is done, start the cart\n";
                                            RS=false;
                                            SE=false;
                                        }else if(client.distance>235000)
                                        {
                                            displaymessage="The cart has gone past the unloading bay. Please press the reset button\n Distance travelled = "+client.distance;
                                            RS=true;
                                            SE=false;
                                        }else
                                        {
                                            displaymessage="Distance travelled = "+client.distance;
                                            RS=true;
                                            SE=false;
                                        }
                                        break;
                                    case TCPClient.SERVER_STATUS_CODE_RUNNING_SIM_SG_RESET:
                                        displaystatus="Salimghar Simulation Reset Mode\n";
                                        if(client.distance==-2){
                                            displaymessage="Server is ready, start the experience when everything is setup\n";
                                            RS=false;
                                            SE=true;
                                        }else if(client.distance==-1){
                                            displaymessage="Waiting for old man to finish his speech\n";
                                            RS=false;
                                            SE=false;
                                        }else if(client.distance==0)
                                        {
                                            displaymessage="Old Man is done, start the cart\n";
                                            RS=false;
                                            SE=false;
                                        }else if(client.distance>235000)
                                        {
                                            displaymessage="The cart has gone past the unloading bay. Please press the reset button\n Distance travelled = "+client.distance;
                                            RS=true;
                                            SE=false;
                                        }else
                                        {
                                            displaymessage="Distance travelled = "+client.distance;
                                            RS=true;
                                            SE=false;
                                        }
                                        break;
                                    case TCPClient.SERVER_STATUS_CODE_RUNNING_SIM_GR:
                                        displaystatus="GoldRush Simulation\n";
                                        if(client.distance>800000)
                                        {
                                            displaymessage="The cart has almost reached the loading bay\n Distance travelled = "+client.distance;
                                            RS=true;
                                            SE=false;
                                        }else
                                        {
                                            displaymessage="Distance travelled = "+client.distance;
                                            RS=true;
                                            SE=false;
                                        }
                                        break;
                                    case TCPClient.SERVER_STATUS_CODE_RUNNING_SIM_GR_RESET:
                                        displaystatus="GoldRush Simulation Reset Mode\n";
                                        displaymessage="Server is ready, Start the experience when everything is ready\n";
                                        RS=false;
                                        SE=true;
                                        break;
                                    case TCPClient.SERVER_STATUS_CODE_RUNNING_SIM_MITM:
                                        displaystatus="MITM Simulation\n";
                                        if(client.distance>90000)
                                        {
                                            displaymessage="The cart has almost reached the loading bay\n Distance travelled = "+client.distance;
                                            RS=true;
                                            SE=false;
                                        }else
                                        {
                                            displaymessage="Distance travelled = "+client.distance;
                                            RS=true;
                                            SE=false;
                                        }
                                        break;
                                    case TCPClient.SERVER_STATUS_CODE_RUNNING_SIM_MITM_RESET:
                                        displaystatus="MITM Simulation Reset Mode\n";
                                        displaymessage="Server is ready, Start the experience when everything is ready\n";
                                        RS=false;
                                        SE=true;
                                        break;
                                    case TCPClient.SERVER_STATUS_CODE_RUNNING_RUN_SG:
                                        displaystatus="Salimghar Sensor Run\n";
                                        if(client.distance==-2){
                                            displaymessage="Server is ready, start the experience when everything is setup\n";
                                            RS=false;
                                            SE=true;
                                        }else if(client.distance==-1){
                                            displaymessage="Waiting for old man to finish his speech\n";
                                            RS=false;
                                            SE=false;
                                        }else if(client.distance==0)
                                        {
                                            displaymessage="Old Man is done, start the cart\n";
                                            RS=false;
                                            SE=false;
                                        }else if(client.distance>235000)
                                        {
                                            displaymessage="The cart has gone past the unloading bay. Please press the reset button\n Distance travelled = "+client.distance;
                                            RS=true;
                                            SE=false;
                                        }else
                                        {
                                            displaymessage="Distance travelled = "+client.distance;
                                            RS=true;
                                            SE=false;
                                        }
                                        break;
                                    case TCPClient.SERVER_STATUS_CODE_RUNNING_RUN_SG_RESET:
                                        displaystatus="Salimghar Sensor Run Reset Mode\n";
                                        if(client.distance==-2){
                                            displaymessage="Server is ready, start the experience when everything is setup\n";
                                            RS=false;
                                            SE=true;
                                        }else if(client.distance==-1){
                                            displaymessage="Waiting for old man to finish his speech\n";
                                            RS=false;
                                            SE=false;
                                        }else if(client.distance==0)
                                        {
                                            displaymessage="Old Man is done, start the cart\n";
                                            RS=false;
                                            SE=false;
                                        }else if(client.distance>235000)
                                        {
                                            displaymessage="The cart has gone past the unloading bay. Please press the reset button\n Distance travelled = "+client.distance;
                                            RS=true;
                                            SE=false;
                                        }else
                                        {
                                            displaymessage="Distance travelled = "+client.distance;
                                            RS=true;
                                            SE=false;
                                        }
                                        break;
                                    case TCPClient.SERVER_STATUS_CODE_RUNNING_RUN_GR:
                                        displaystatus="GoldRush Sensor run\n";
                                        if(client.distance>800000)
                                        {
                                            displaymessage="The cart has almost reached the loading bay\n Distance travelled = "+client.distance;
                                            RS=true;
                                            SE=false;
                                        }else
                                        {
                                            displaymessage="Distance travelled = "+client.distance;
                                            RS=true;
                                            SE=false;
                                        }
                                        break;
                                    case TCPClient.SERVER_STATUS_CODE_RUNNING_RUN_GR_RESET:
                                        displaystatus="GoldRush Sensor Run Reset Mode\n";
                                        displaymessage="Server is ready, Start the experience when everything is ready\n";
                                        RS=false;
                                        SE=true;
                                        break;
                                    case TCPClient.SERVER_STATUS_CODE_RUNNING_RUN_MITM:
                                        if(client.distance>90000)
                                        {
                                            displaymessage="The cart has almost reached the loading bay\n Distance travelled = "+client.distance;
                                            RS=true;
                                            SE=false;
                                        }else
                                        {
                                            displaymessage="Distance travelled = "+client.distance;
                                            RS=true;
                                            SE=false;
                                        }
                                        break;
                                    case TCPClient.SERVER_STATUS_CODE_RUNNING_RUN_MITM_RESET:
                                        displaystatus="MITM Sensor Reset Mode\n";
                                        displaymessage="Server is ready, Start the experience when everything is ready\n";
                                        RS=false;
                                        SE=true;
                                        break;
                                    default:
                                        break;
                                }
                                if((!(displaystatus.startsWith("0")))&&(!(displaystatus.startsWith("1"))))
                                {
                                    int dispnum=TCPClient.reply_count;
                                    dispnum=dispnum/3;
                                    dispnum=dispnum%2;
                                    displaystatus=dispnum+" "+displaystatus;
                                }
                                String connectedclients="";
                                if(client.connecteclientcount>0){
                                    connectedclients=client.connecteclientcount+" clients connected with ip ";
                                    for(int i=0;i<client.connecteclientcount;i++)
                                    {
                                        connectedclients=connectedclients+" "+client.connectedclientlist[i];
                                    }
                                }

                                final String finalDisplaymessage = displaymessage,finalDisplayStatus=displaystatus,finalconnectedclients=connectedclients;
                                final boolean FRS = RS, FSE = SE;
                                handle.post(new Runnable() {

                                    @Override
                                    public void run() {
                                        ServerStatus.setText(finalDisplayStatus);
                                        DisplayMessage.setText(finalDisplaymessage);
                                        ConnectedClients.setText(finalconnectedclients);
                                        ResetServer.setEnabled(FRS);
                                        ResetServer.setClickable(FRS);
                                        StartExperience.setEnabled(FSE);
                                        StartExperience.setClickable(FSE);
                                    }
                                });
                                previousmessage = message;
                                //Do nothing for now
                            }
                        };

                        client = new TCPClient(onMessageReceived);/*
                        TCPClient.setServerip(IpAddress);
                        TCPClient.setSERVERPORT(1234);
                        Thread thread1 = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(2000, 0);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                while (client.isRunning()) {
                                    client.sendMessage("G");
                                    try {
                                        Thread.sleep(500, 0);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        });
                        thread1.start();*/
                        client.run();
                    }
                }else
                {
                    handle.post(new Runnable() {
                        @Override
                        public void run() {

                            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(MainActivity.this);
                            dlgAlert.setMessage("The server is unavailable");
                            dlgAlert.setTitle(getString(R.string.app_name));
                            dlgAlert.setPositiveButton("Ok",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            System.exit(1);
                                            //dismiss the dialog
                                        }
                                    });
                            dlgAlert.setCancelable(true);
                            dlgAlert.create().show();
                        }
                    });
                }
            }
        });
        thread.start();
    }

}
