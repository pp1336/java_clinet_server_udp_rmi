package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.LinkedList;

import static common.Utils.*;
import common.MessageInfo;

public class UDPServer {
    // UDP server implementation

    private final static int BUFFERSIZE = 50;
    private final static int TIMEOUT = 40000;
    private DatagramSocket socket;
    private int total = -1;
    private short[] receivedMsg;
    private int received = -1;

    public UDPServer(int portNum) {

        try {
            // open socket and listen for messages
            socket = new DatagramSocket(portNum);
        } catch (SocketException e) {
            handleError("error initialising socket", e);
        }
        System.out.println("Server ready...");
    }

    public static void main(String args[]) {

        // check arguments
        if (args.length < 1) {
            handleError("argument required: port number");
        }

        // get port number
        int portNum = getInt(args[0], "port number must be integer");
        assert (portNum >= 1024 && portNum <= 65535)
                : "port number must be within 1024 to 65535";

        // setup server
        UDPServer server = new UDPServer(portNum);

        // run server
        server.run();

        // output results
        server.finish();
        return;
    }

    // server main loop
    private void run() {

        byte[] pacData = new byte[BUFFERSIZE];
        DatagramPacket pac = null;
        boolean open = true;

        // receive message until all received == total or timeout
        do {
            try {

                // create buffer
                pac = new DatagramPacket(pacData, BUFFERSIZE);

                // set timeout on Socket
                socket.setSoTimeout(TIMEOUT);

                // receive message
                socket.receive(pac);

                // process message
                process(new String(pac.getData()).trim());
            } catch (IOException e) {
                // timeout finishes
                open = false;
            }
        } while (open && total != received);
    }

    // process message
    private void process(String data) {

        // parse string to MessageInfo
        MessageInfo msg = null;
        try {
            msg = new MessageInfo(data);
        } catch (Exception e) {
            handleError("invalid data format", e);
        }

        // setup if first message
        if (received == -1) {
            total = msg.totalMessages;
            receivedMsg = new short[total];
            received = 0;
        }

        assert (total == msg.totalMessages)
                : "inconsistent total number of messages";

        // update message count
        if (receivedMsg[msg.messageNum] == 0) {
            received++;
        }
        receivedMsg[msg.messageNum] += 1;
    }

    // print summary
    public void finish() {

        System.out.println("finishing");

        // no message received
        if (received == -1) {
            System.out.println("no messages recieved!");
            return;
        }

        LinkedList<Integer> lostMsg = new LinkedList<Integer>();
        LinkedList<Integer> duplicateMsg = new LinkedList<Integer>();

        // collate lost and duplicate messages
        for (int i = 0; i < total; i++) {
            if (receivedMsg[i] == 0) {
                lostMsg.add(i);
            } else if (receivedMsg[i] >= 2) {
                duplicateMsg.add(i);
            }
        }

        // print summary
        System.out.println("Msg sent: " + total);
        System.out.print("Msg received: " + received);
        System.out.println("    of which " + duplicateMsg.size()
                        + " are duplicates");
        System.out.println("Percentage duplicates: "
                        + ((float) duplicateMsg.size()) / received
                                        * 100);
        assert (total - received == lostMsg
                        .size()) : "incosistent message count";
        System.out.println("Msg lost: " + (total - received));
        System.out.println("Percentage lost: "
                        + ((float) (total - received))
                        / total * 100);

        // print details about lost and duplicate message numbers
        if (lostMsg.size() > 0) {
            System.out.println("lost messages:");
            System.out.println(concatListInterval(lostMsg));
        }
        if (duplicateMsg.size() > 0) {
            System.out.println("duplicated messages:");
            System.out.println(concatListInterval(duplicateMsg));
        }
    }

}
