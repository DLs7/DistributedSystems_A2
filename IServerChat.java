import java.util.*;
import java.rmi.*;

public interface IServerChat extends Remote {
    public ArrayList<String> getRooms() throws RemoteException;
    public void createRoom(String roomName) throws RemoteException;
}