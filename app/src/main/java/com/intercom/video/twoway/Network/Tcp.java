package com.intercom.video.twoway.Network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Sean Luther
 * This class handles all network communciation for the Audio and VideoStreaming classes
 */
public class Tcp
{
    private int LISTENING_SERVICE_PORT = 1025;
    public String lastRemoteIpAddress; // the remote address of the last device we connected to
    private Socket tcpSocket; //Used when we are the client
    ServerSocket tcpServerSocket; //Used for accepting connections when we are the server
    // Lower level streams good for transferring raw bytes of video data
    InputStream tcpIn;
    OutputStream tcpOut;
    // higher level readers and writers good for transfering text
    BufferedReader bufferedTcpIn;
    BufferedWriter bufferedTcpOut;
    int connectionState;
    final int DISCONNECTED = 1;
    final int CONNECTED = 2;

    /**
     * Constructor
     * sets connectionState=DISCONNECTED
     */
    public Tcp()
    {
        connectionState=DISCONNECTED;
    }


    public int getLISTENING_SERVICE_PORT()
    {
        return LISTENING_SERVICE_PORT;
    }

    public void setLISTENING_SERVICE_PORT(int LISTENING_SERVICE_PORT)
    {
        this.LISTENING_SERVICE_PORT = LISTENING_SERVICE_PORT;
    }

    /**
     * Attempts to gracefully close all ports, sets connectionState=DISCONNECTED;
     */
    public void closeConnection()
    {
        try
        {
            tcpIn.close();
        }
        catch(Exception e)
        {

        }
        try
        {
            tcpOut.close();
        }
        catch(Exception e)
        {

        }
        try
        {
            bufferedTcpIn.close();
        }
        catch(Exception e)
        {

        }
        try
        {
            bufferedTcpOut.close();
        }
        catch(Exception e)
        {

        }
        try
        {
            tcpSocket.close();
        }
        catch(Exception e)
        {

        }
        try
        {
            tcpServerSocket.close();
        }
        catch(Exception e)
        {

        }

        connectionState=DISCONNECTED;
    }

    /**
     * Listen for a connection.  This should only be called from a separate thread so the main
     * thread isn't blocked
     *
     * @return int representing the current status of the connection
     * 0 = not connected
     * 1 = CONNECTED
     */
    public int listenForConnection()
    {
        int connectionStage=0;

        // close any previous connections
        closeConnection();
        try
        {
            tcpServerSocket = new ServerSocket(getLISTENING_SERVICE_PORT());
            tcpSocket = tcpServerSocket.accept();
            tcpIn = tcpSocket.getInputStream();
            tcpOut = tcpSocket.getOutputStream();
            bufferedTcpOut = new BufferedWriter(new OutputStreamWriter(tcpOut));
            bufferedTcpIn = new BufferedReader(new InputStreamReader(tcpIn));


            // if we got here with no exception we can assume we are connected
            connectionState = CONNECTED;
            lastRemoteIpAddress=getRemoteIpAddress();

            connectionStage=tcpIn.read();


        } catch (Exception e)
        {
            e.printStackTrace();
        }

        // just disconnect now, no use for keeping this connection open
        closeConnection();

        return connectionStage;
    }

    /**
     * Informs the remote device that we have started a streaming server and are ready to be
     * connected to
     * @param ipAddress
     * @param connectionStage
     */
    public void connectToDevice(final String ipAddress, final int connectionStage)
    {
        Thread openConnectionThread = new Thread()
        {
            public void run()
            {
                try
                {
                    closeConnection();

                    tcpSocket = new Socket(ipAddress, getLISTENING_SERVICE_PORT());
                    tcpIn= tcpSocket.getInputStream();
                    tcpOut= tcpSocket.getOutputStream();
                    bufferedTcpOut=new BufferedWriter(new OutputStreamWriter(tcpOut));
                    bufferedTcpIn=new BufferedReader(new InputStreamReader(tcpIn));

                    // if we got here with no exception we can assume we are connected
                    connectionState=CONNECTED;
                    lastRemoteIpAddress=getRemoteIpAddress();

                    // inform the remote device whether we initiated the connection
                    // or are starting our server in response to the connection being initiated
                    tcpOut.write(connectionStage);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }

                // just disconnect now, no use for keeping this connection open
                closeConnection();


            }
        };

        openConnectionThread.start();

    }

    /**
     * Returns the ip address of the remote device we are connected to
     * @return String with the remote IP address in it
     */
    public String getRemoteIpAddress()
    {
        return tcpSocket.getRemoteSocketAddress().toString();
    }

}
