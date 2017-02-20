package rmi;

import java.rmi.Remote;
import java.rmi.registry.Registry;
import java.rmi.RemoteException;

import common.*;

public interface RMIServerInterface extends Remote {
	// interface for remote object
	
	public void receiveMessage(MessageInfo msg)
	    throws RemoteException;

        public void setRegistry(Registry registry)
            throws RemoteException;
}
