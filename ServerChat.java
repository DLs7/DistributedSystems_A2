import java.util.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class ServerChat extends UnicastRemoteObject implements IServerChat {
    private JFrame frame;
    private JPanel panel;
    private JButton button;
    private JLabel label;
    private JList<String> list;
    private DefaultListModel<String> listModel;
    private JScrollPane listScroller;
    
    public ServerChat() throws RemoteException {
        super();

        roomList = new ArrayList<String>();

        frame = new JFrame();
        
        // frame.add(panel, BorderLayout.CENTER);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Servidor");
        addComponentsToPane(frame.getContentPane());

        frame.pack();
        frame.setVisible(true);
    }

    public void addComponentsToPane(Container pane) {
        // pane = new JPanel();
        // pane.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        listModel = new DefaultListModel<String>();

        list = new JList<String>(listModel);
        list.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 0;
        pane.add(list, c);

        listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(new Dimension(250, 100));

        pane.add(listScroller);

        button = new JButton("Fechar sala(s)");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                java.util.List<String> string = list.getSelectedValuesList();
                for(String s : string) {
                    try {
                        IRoomChat obj = (IRoomChat) Naming.lookup("//localhost:2020/Salas/" + s);
                        obj.closeRoom();
                        Naming.unbind("//localhost:2020/Salas/" + s);
                        
                        roomList.remove(s);
                        listModel.removeElement(s);
                    } catch (Exception ex) {
                        System.out.println("ServerChat error: " + ex.getMessage());
                        ex.printStackTrace();
                    }
                }
            }
        });

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;
        pane.add(button, c);

        label = new JLabel("Total de salas: 0");
        label.setVerticalTextPosition(JLabel.BOTTOM);
        label.setHorizontalTextPosition(JLabel.CENTER);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        pane.add(label, c);
    }

    // RFA1: O servidor central deve manter uma lista de salas (roomList).
    // A lista de salas deve declarada como private ArrayList<String> roomList.
    // RFA3: No servidor central, não deve haver limite de salas, tampouco de usuários por sala.
    private ArrayList<String> roomList;

    public ArrayList<String> getRooms() throws RemoteException {
        // System.out.println("roomList requisitada");
        return roomList;
    }

    public void createRoom(String roomName) throws RemoteException {
        // System.out.println("Sala " + roomName + " criada.");

        try {
            RoomChat obj = new RoomChat(roomName);
            roomList.add(roomName);

            listModel.addElement(roomName);
            label.setText("Total de salas: " + roomList.size());
            
            Naming.rebind("//localhost:2020/Salas/" + roomName, obj);
            System.out.println("Sala " + roomName + " criada.");
        } catch (Exception ex) {
            System.out.println("ServerChat error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    public static void main(String args[]) {
        try {
            // Creates an object of the HelloServer class.
            ServerChat obj = new ServerChat();
            // Bind this object instance to the name "HelloServer".
            LocateRegistry.createRegistry(2020).rebind("Servidor", obj);
        }
        catch (Exception ex) {
            System.out.println("ServerChat error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}