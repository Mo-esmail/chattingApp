import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class Server {
    private JFrame frmserver;
    public JTextField txtPort;
    private JLabel lblPortTitle,lblListTitle,lblError;
    private JButton btnStartListening,btnExit;
    private JScrollPane paneList,paneTable;
    private JTable table;
    private DefaultTableModel tablemodel ;
    private DefaultListModel serverlistmodel;
    private JList listActiveUsers;
    private	ServerSocket server;
    private Socket clientSocket;
    HashMap<String, OnlineUserProcessor> OlineUsersMap;
    BufferedReader in ;
    PrintWriter out;
    boolean isListening=false;
    public Server(){
        frmserver= new JFrame("Chating App");
        txtPort = new JTextField();
        lblPortTitle= new JLabel("Port Number :");
        btnStartListening= new JButton();
        btnStartListening.setText("Start listening");
        btnExit=new JButton("Exit");
        tablemodel= new DefaultTableModel(new String[][]{}, new String[]{"Event","Date"});
        table = new JTable(tablemodel);
        serverlistmodel = new DefaultListModel();
        listActiveUsers= new JList(serverlistmodel);
        lblListTitle= new JLabel("Online Users");
        paneList=new JScrollPane(listActiveUsers);
        paneTable= new  JScrollPane(table);
        lblError= new JLabel();
    }
    public void init(){
        frmserver.setLayout(null);
        lblPortTitle.setSize(100, 25);
        lblPortTitle.setLocation(40,10);
        frmserver.add(lblPortTitle);
        txtPort.setSize(100, 25);
        txtPort.setLocation(150, 10);
        frmserver.add(txtPort);
        btnStartListening.setSize(120, 25);
        btnStartListening.setLocation(500, 10);
        frmserver.add(btnStartListening);
        lblListTitle.setSize(100,25);
        lblListTitle.setLocation(60, 100);
        frmserver.add(lblListTitle);
        paneList.setSize(200, 200);
        paneList.setLocation(10, 130);
        frmserver.add(paneList);
        paneTable.setSize(400,200);
        paneTable.setLocation(220, 130);
        frmserver.add(paneTable);
        btnExit.setSize(120,25);
        btnExit.setLocation(500, 350);
        frmserver.add(btnExit);
        lblError.setSize(300, 25);
        lblError.setLocation(100, 40);
        frmserver.add(lblError);
        frmserver.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmserver.setSize(650,425);
        frmserver.setVisible(true);
        btnExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!listActiveUsers.isSelectionEmpty())
                    System.out.println("a77777777a");
                else
                    System.exit(0);
            }
        });
        btnStartListening.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!isListening){
                    lblError.setText(null);
                    if(txtPort.getText().isEmpty()){
                        try {
                            txtPort.setText("80");
                            server = new ServerSocket(80);
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }else{
                        try {
                            server = new ServerSocket(Integer.parseInt(txtPort.getText()));
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        } catch (NumberFormatException s) {
                            lblError.setText("sorry , port number must be a numeric input");
                        }
                    }
                    OlineUsersMap = new HashMap<String, OnlineUserProcessor>();
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                isListening=true;
                                btnStartListening.setText("Stop listening");
                                txtPort.setEditable(false);
                                while(true){
                                    clientSocket = server.accept();
                                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                                    setUserName();
                                }
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }).start();
                }else{
                    Iterator<String> it = OlineUsersMap.keySet().iterator();
                    ArrayList<String> list = new ArrayList<String>();
                    while(it.hasNext())
                        list.add(it.next());
                    for (int i = 0; i < list.size(); i++) {
                        OlineUsersMap.get(list.get(i)).out.println(Utils.prepareEndConnection("server"));
                    }
                    tablemodel.addRow(new String[]{"stoped listening",Utils.getCurrentDate()});
                    serverlistmodel.clear();
                    OlineUsersMap.clear();
                    btnStartListening.setText("Start listening");
                    txtPort.setEditable(true);
                    listActiveUsers.setModel(new DefaultListModel());
                    isListening=false;
                    try {
                        server.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }
    public static void main(String[] args) {
        Server s = new Server();
        s.init();
    }
    class OnlineUserProcessor extends Thread {
        PrintWriter out;
        BufferedReader in;
        String userName;
        Socket mySocket;
        boolean connected = false;

        @Override
        public void run() {
            listenforinputs();
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public void listenforinputs() {
            try {

                String message;
                String[] msg;

                while ((message = in.readLine()) != null && connected) {
                    int s = Utils.identifyMessage(message);
                    if (s == 1) {
                        msg = Utils.processMessage(message);
                        OlineUsersMap.get(msg[1]).out.println(message);
                    } else if (s == 2) {
                        for (int a = 0; a < serverlistmodel.size(); a++) {
                            OlineUsersMap.get(serverlistmodel.get(a)).out
                                    .println(message);
                        }
                    } else if (s == 4) {
                        String[] name = Utils.processEndConnection(message);
                        disconnect(userName);
                    }
                    if (!isListening)
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public OnlineUserProcessor(Socket clientSocket) throws IOException {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(
                    clientSocket.getInputStream()));
            mySocket = clientSocket;
            connected = true;
        }
        public void disconnect(String disconnector) {
            updateLists();
            out.println(Utils.prepareEndConnection(disconnector));
            OlineUsersMap.remove(userName);
            connected = false;
            tablemodel.addRow(new String[] { userName + " is disconnected",
                    Utils.getCurrentDate() });
            updateLists();
        }

    }

    private void updateLists() {
        Iterator<String> it = OlineUsersMap.keySet().iterator();
        ArrayList<String> list = new ArrayList<String>();
        while (it.hasNext())
            list.add(it.next());
        for (int i = 0; i < list.size(); i++) {
            OlineUsersMap.get(list.get(i)).out.println(Utils
                    .prepareUsersUpdate(list));
        }
        DefaultListModel model = new DefaultListModel();
        for (int j = 0; j < list.size(); j++) {
            model.addElement(list.get(j));
        }
        listActiveUsers.setModel(model);
    }
    public void setUserName() throws IOException{
        String handShakeMessage;
        while((handShakeMessage = in.readLine())!=null){
            boolean isUsed=false;
            handShakeMessage=Utils.processHandShake(handShakeMessage);
            Iterator<String> it = OlineUsersMap.keySet().iterator();
            ArrayList<String> list = new ArrayList<String>();
            while (it.hasNext())
                list.add(it.next());
            for (int i = 0; i < list.size(); i++)
                if(list.get(i).equals(handShakeMessage))
                    isUsed=true;
            if(!isUsed){
                OnlineUserProcessor user;
                user = new OnlineUserProcessor(clientSocket);
                user.setUserName(handShakeMessage);
                tablemodel.addRow(new String[]{user.getUserName()+" is connected",Utils.getCurrentDate()});
                serverlistmodel.addElement(user.getUserName());
                OlineUsersMap.put(user.getUserName(), user);
                updateLists();
                user.start();
                break;
            }else{
                out.println(Utils.prepareUserNameProblem());
            }
        }
    }
}