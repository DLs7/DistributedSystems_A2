// import java.util.*;
import java.rmi.*;
import java.io.*;
import java.util.*;
import java.rmi.server.UnicastRemoteObject;

public class RoomChat extends UnicastRemoteObject implements IRoomChat, Serializable {
    public RoomChat(String roomName) throws RemoteException {
        super();

        this.roomName = roomName;
        userList = new HashMap<String, IUserChat>();
        isOpen = true;
    }

    // RFA2: Cada sala (RoomChat) da roomList deve manter uma lista de usuários (userList). A
    // lista de usuários deve declarada como private Map<String, IUserChat> userList.
    private Map<String, IUserChat> userList;
    private String roomName;
    private boolean isOpen;

    // RFA11: O controlador da sala (sala) é quem deve controlar o envio das mensagens aos membros da
    // sala
    public void sendMsg(String usrName, String msg) throws RemoteException {
        if(msg.equals("/quit")) {
            leaveRoom(usrName);
        } else {
            if(isOpen) {
                for(Map.Entry<String, IUserChat> entry : userList.entrySet()) {
                    try {
                        entry.getValue().deliverMsg(usrName, msg);
                    } catch (Exception ex) {
                        System.out.println("RoomChat error: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    public void joinRoom(String usrName, IUserChat user) throws RemoteException {
        userList.put(usrName, user);

        for(Map.Entry<String, IUserChat> entry : userList.entrySet()) {
            try {
                entry.getValue().deliverMsg(usrName, " entrou na sala " + roomName + ".");
            } catch (Exception ex) {
                System.out.println("RoomChat error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }

    // RFA12: Os usuários devem sair da sala invocando o método remoto leaveRoom(String
    // usrName) da sala.
    public void leaveRoom(String usrName) throws RemoteException {
        for(Map.Entry<String, IUserChat> entry : userList.entrySet()) {
            try {
                entry.getValue().deliverMsg(usrName, " saiu da sala " + roomName + ".");
            } catch (Exception ex) {
                System.out.println("RoomChat error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        userList.remove(usrName);
    }
    
    public void closeRoom() throws RemoteException {
        for(Map.Entry<String, IUserChat> entry : userList.entrySet()) {
            try {
                entry.getValue().deliverMsg("Servidor", "Sala fechada pelo servidor.");
            } catch (Exception ex) {
                System.out.println("RoomChat error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

        isOpen = false;
    }

    public String getRoomName() throws RemoteException {
        return roomName;
    }
}