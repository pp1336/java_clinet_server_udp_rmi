
package rmi;

import static common.Utils.concatListInterval;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.LinkedList;

import static common.Utils.*;
import common.MessageInfo;

public class RMIServer extends UnicastRemoteObject
                implements RMIServerInterface {
    // RMI server implementation

    private int portNum;
    private int total = -1;
    private short[] receivedMsg;
    private int received = -1;

    public RMIServer(int portNum) throws RemoteException {
        super();
        this.portNum = portNum;
    }

    public static void main(String[] args) {

        // check arguments
        if (args.length < 1) {
            handleError("argument required: port number");
        }

        // get port number
        int portNum = getInt(args[0], "invalid port number");
        assert (portNum >= 1024 && portNum <= 65535)
                : "port number must be within 1024 to 65535";

        // set up Security Policy
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        // setup server
        try {
            // create server
            RMIServerInterface server = new RMIServer(portNum);
            try {
                // locate registry
                Registry registry = LocateRegistry
                                .createRegistry(server.getPortNum());
                // bind server object to remote interface
                registry.rebind("RMIServerInterface", server);
            } catch (RemoteException e) {
                handleError("error binding to server", e);
            }
            System.out.println("Server ready...");
        } catch (Exception e) {
            handleError("RMIServerInterface exception: ", e);
        }
    }

    // call back for receiving a message
    public void receiveMessage(MessageInfo msg)
                    throws RemoteException {

        // setup if first message
        if (received == -1) {
            total = msg.totalMessages;
            receivedMsg = new short[total];
            received = 0;
        }

        assert (total == msg.totalMessages)
                : "inconsistent total message count";

        // update message count
        if (receivedMsg[msg.messageNum] == 0) {
            received++;
        }
        receivedMsg[msg.messageNum] += 1;

        // print summary after last message
        if (msg.messageNum == msg.totalMessages - 1) {
            finish();
        }

    }

    // function to show port number used
    public int getPortNum() {
        return portNum;
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
