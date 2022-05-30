import java.util.*;
import java.rmi.*;
import java.io.Serializable;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;

public class UserChat extends UnicastRemoteObject implements IUserChat, Serializable {
    private JFrame frame;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JList<String> chatList;
    private JList<String> roomList;
    private DefaultListModel<String> chatListModel;
    private DefaultListModel<String> roomListModel;
    private JScrollPane chatListScroller;
    private JScrollPane roomListScroller;
    private JTextField chatTextField;

    public UserChat() throws RemoteException {
        super();

        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Cliente");
        addComponentsToPane(frame.getContentPane(), this);

        frame.pack();
        frame.setVisible(true);
    }

    public void addComponentsToPane(Container pane, UserChat user) {
        // pane = new JPanel();
        // pane.setBorder(BorderFactory.createEmptyBorder(30, 30, 10, 30));
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        chatListModel = new DefaultListModel<String>();

        chatList = new JList<String>(chatListModel);
        chatList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        chatList.setLayoutOrientation(JList.VERTICAL);
        chatList.setVisibleRowCount(-1);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 2;
        c.gridheight = 2;
        c.gridx = 0;
        c.gridy = 0;
        pane.add(chatList);

        chatListScroller = new JScrollPane(chatList);
        chatListScroller.setPreferredSize(new Dimension(250, 250));

        pane.add(chatListScroller);

        chatTextField = new JTextField(20);
        chatTextField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    room.sendMsg(usrName, chatTextField.getText());
                    chatTextField.setText("");
                } catch (Exception ex) {
                    System.out.println("UserChat error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });
        
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 3;
        pane.add(chatTextField, c);

        roomListModel = new DefaultListModel<String>();

        roomList = new JList<String>(roomListModel);
        roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomList.setLayoutOrientation(JList.VERTICAL);
        roomList.setVisibleRowCount(-1);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridwidth = 1;
        c.gridheight = 2;
        c.gridx = 2;
        c.gridy = 0;
        pane.add(roomList, c);

        roomListScroller = new JScrollPane(roomList);
        roomListScroller.setPreferredSize(new Dimension(125, 250));

        pane.add(roomListScroller, c);

        button1 = new JButton("Enviar");
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    room.sendMsg(usrName, chatTextField.getText());
                    chatTextField.setText("");
                } catch (Exception ex) {
                    System.out.println("UserChat error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 2;
        c.gridy = 3;
        pane.add(button1, c);

        button2 = new JButton("Atualizar");
        button2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    // RFA4: No início, todo cliente, identificado pelo seu nome (usrName), deve contatar o servidor e
                    // solicitar a lista de salas roomList. 
                    IServerChat obj = (IServerChat) Naming.lookup("//localhost:2020/Servidor");
                        
                    // RFA5: A solicitação da lista de salas deve ser realizada através da invocação ao método remoto
                    // getRooms()da lista de salas roomList.
                    rooms = obj.getRooms();
                    roomListModel.removeAllElements();
                    for(String r : rooms) {
                        roomListModel.addElement(r);
                    }
                } catch (Exception ex) {
                    System.out.println("UserChat error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        c.gridy = 4;
        pane.add(button2, c);

        button3 = new JButton("Conectar");
        button3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    roomName = roomList.getSelectedValue();

                    if(roomName != null) {
                        user.room = (IRoomChat) Naming.lookup("//localhost:2020/Salas/" + user.roomName);
                        room.joinRoom(usrName, user);
                        roomName = null;
                    }
                } catch (Exception ex) {
                    System.out.println("UserChat error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        c.gridy = 0;
        pane.add(button3, c);

        button4 = new JButton("Sair");
        button4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    if(roomName != null && room != null) {
                        room.leaveRoom(usrName);
                        roomName = null;
                    }
                } catch (Exception ex) {
                    System.out.println("UserChat error: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        });

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 3;
        c.gridy = 1;
        pane.add(button4, c);
    }

    String usrName;
    String roomName;
    IRoomChat room;
    ArrayList<String> rooms;
    
    public void deliverMsg(String senderName, String msg) throws RemoteException {
        chatListModel.addElement("[" + senderName + "]: " + msg);
    }

    public static void main(String[] args) throws RemoteException {
        try {
            UserChat user = new UserChat();

            user.usrName = JOptionPane.showInputDialog(null, "Digite seu nome:", "Cliente", JOptionPane.INFORMATION_MESSAGE);
            
            // RFA4: No início, todo cliente, identificado pelo seu nome (usrName), deve contatar o servidor e
            // solicitar a lista de salas roomList. 
            IServerChat obj = (IServerChat) Naming.lookup("//localhost:2020/Servidor");
                
            // RFA5: A solicitação da lista de salas deve ser realizada através da invocação ao método remoto
            // getRooms()da lista de salas roomList.
            user.rooms = obj.getRooms();
            for(String r : user.rooms) {
                user.roomListModel.addElement(r);
            }
            
            user.roomName = JOptionPane.showInputDialog(null, "Digite o nome da sala que voce quer entrar:", 
                                                                "Cliente", JOptionPane.INFORMATION_MESSAGE);
            
            user.room = null;
            boolean equals = false;
            for(String r : user.rooms) {
                // RFA7: Sempre que um usuário desejar entrar numa sala já existente ele deve solicitar a referência
                // ao objeto remoto ao RMI Registry usando o nome da sala e, após conhecer o objeto, deve invocar o
                // método remoto joinRoom(String usrName) da respectiva sala.
                if(r.equals(user.roomName)) {
                    equals = true;
                }
            }

            // RFA8: Caso o usuário não encontre no servidor a sala desejada ele deve poder solicitar a criação de
            // uma nova sala. Isto deve ser feito através da invocação ao método remoto criateRoom(String
            // roomName) do servidor. A vinculação do usuário a esta sala não deve ser automática. Ele deve
            // solicitar a entrada invocando o método remoto joinRoom() da sala.
            if(!equals && user.roomName != null) {
                int reply = JOptionPane.showConfirmDialog(null, "A sala " + user.roomName + " nao existe. Deseja criar esta sala?", 
                                                                "Cliente", JOptionPane.YES_NO_OPTION);
                if(reply == JOptionPane.YES_OPTION)
                    obj.createRoom(user.roomName);     
            }

            if(user.roomName != null) {
                user.room = (IRoomChat) Naming.lookup("//localhost:2020/Salas/" + user.roomName);
                user.room.joinRoom(user.usrName, user);
            }

        } catch (Exception ex) {
            System.out.println("UserChat error: " + ex.getMessage());
            ex.printStackTrace();
        }
    }
}