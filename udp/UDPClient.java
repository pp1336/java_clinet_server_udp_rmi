package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import static common.Utils.*;
import common.MessageInfo;

public class UDPClient {
    // UDP client implementation

    private DatagramSocket sendSoc;

    public UDPClient() {
        try {
            // initialise socket
            sendSoc = new DatagramSocket();
        } catch (SocketException e) {
            handleError("error initialising socket", e);
        }
    }

    public static void main(String[] args) {
        InetAddress serverAddr = null;
        int portNum;
        int total;

        // check arguments
        if (args.length < 3) {
            handleError("arguments: hostname, port number, "
                            + "msg count");
        }

        // get server address
        try {
            serverAddr = InetAddress.getByName(args[0]);
        } catch (UnknownHostException e) {
            handleError("unknown host exception at server, "
                            + args[0], e);
        }

        // get port number
        portNum = getInt(args[1], "invalid port number");
        assert (portNum >= 1024 && portNum <= 65535)
                : "port number must be within 1024 to 65535";

        // get total number messages to send
        total = getInt(args[2], "total msg must be integer");

        // create client
        UDPClient client = new UDPClient();

        // run client
        client.run(serverAddr, portNum, total);
        return;
    }

    // client main loop
    public void run(InetAddress serverAddr, int portNum, int total) {
        System.out.println("client ready...");

        for (int i = 0; i < total; i++) {
            // create message
            MessageInfo msg = new MessageInfo(total, i);

            // send message
            this.send(msg.toString(), serverAddr, portNum);
        }
        // finish sending
        System.out.println(total + " Messages sent");
    }

    // function to send message
    private void send(String payload, InetAddress destAddr,
                    int destPort) {

        // create packet
        byte[] pktData = payload.getBytes();
        int payloadSize = pktData.length;
        DatagramPacket pkt;
        pkt = new DatagramPacket(pktData, payloadSize, destAddr,
                        destPort);

        try {
            // send the packet over the socket
            sendSoc.send(pkt);
        } catch (IOException e) {
            handleError("error sending packet");
        }
    }
}
