import java.util.*;
import java.rmi.*;

public interface IUserChat extends Remote {
    public void deliverMsg(String senderName, String msg) throws RemoteException;
}