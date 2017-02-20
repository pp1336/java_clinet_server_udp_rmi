package rmi;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import static common.Utils.*;
import common.MessageInfo;

public class RMIClient {

    public static void main(String[] args) {

        // check arguments
        if (args.length < 3) {
            handleError("Args: server name/IP, recv port, "
                            + "msg count");
        }

        // get total number of messages to send
        int total = getInt(args[2], "total number of messages"
                        + "must be integer");

        // get port number
        int portNum = getInt(args[1], "invalid port number");
        assert (portNum >= 1024 && portNum <= 65535)
                : "port number must be within 1024 to 65535";

        // set up Security Policy
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new SecurityManager());
        }

        try {
            // locate registry
            Registry registry = LocateRegistry.getRegistry(args[0],
                            portNum);
            // get server and bind to remote object
            RMIServerInterface server = (RMIServerInterface) registry
                            .lookup("RMIServerInterface");

            // main loop send messages
            for (int i = 0; i < total; i++) {
                MessageInfo message = new MessageInfo(total, i);
                server.receiveMessage(message);
            }

            // finish
            System.out.println(total + " Messages Sent");
            return;
        } catch (Exception e) {
            handleError("error starting client", e);
        }
    }
}
