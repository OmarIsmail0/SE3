package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AdminInterface extends Remote {
    public void editAccount() throws RemoteException;
}
