import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client {
    private final JFrame frmClinet;
    public JTextField txtServerIP,txtPort,txtUserName,txtmsg;
    private final JLabel lblErrorChosedName;
    private final JLabel lblServerIPTitle;
    private final JLabel lblPortTitle;
    private final JLabel lblUserNameTitle;
    private final JLabel lblListTitle;
    private final JButton btnConnect;
    private final JButton btnRoadCast;
    private final JButton btnSend;
    private final JButton btnExit;
    private final JScrollPane paneList;
    private final JScrollPane paneMsgs;
    private final JPanel pnlInfo;
    public JTextArea msgs;
    private final JList listActiveUsers;
    public Socket s;
    PrintWriter out;
    BufferedReader in;
    boolean isConnected =false;
    public Client(){
        frmClinet= new JFrame("Chating App");
        txtPort = new JTextField("80");
        lblPortTitle= new JLabel("Port");
        txtServerIP=new JTextField();
        lblServerIPTitle= new JLabel("Server IP");
        txtUserName= new JTextField();
        lblUserNameTitle= new JLabel("User Name");
        lblErrorChosedName= new JLabel();
        btnConnect= new JButton("Connect");
        btnExit=new JButton("Exit");
        btnRoadCast=new JButton("Road Cast");
        btnSend=new JButton("Send");
        msgs= new JTextArea();
        msgs.setEditable(false);
        txtmsg= new JTextField();
        pnlInfo= new JPanel();
        listActiveUsers= new JList();
        lblListTitle= new JLabel("Online Users");
        paneList=new JScrollPane(listActiveUsers);
        paneMsgs= new JScrollPane(msgs);
    }
    public void init(){
        txtServerIP.setText("localhost");
        frmClinet.setLayout(null);
        pnlInfo.setLocation(10, 0);
        pnlInfo.setSize(650, 100);
        pnlInfo.setLayout(null);
        pnlInfo.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(Color.LIGHT_GRAY, Color.LIGHT_GRAY),"Info must be filled"));
        frmClinet.add(pnlInfo);
        lblServerIPTitle.setSize(60, 25);
        lblServerIPTitle.setLocation(90,10);
        pnlInfo.add(lblServerIPTitle);
        txtServerIP.setSize(100,25);
        txtServerIP.setLocation(70, 40);
        pnlInfo.add(txtServerIP);
        lblPortTitle.setSize(60, 25);
        lblPortTitle.setLocation(240,10);
        pnlInfo.add(lblPortTitle);
        txtPort.setSize(100, 25);
        txtPort.setLocation(200, 40);
        pnlInfo.add(txtPort);
        btnConnect.setSize(100,25);
        btnConnect.setLocation(530, 40);
        pnlInfo.add(btnConnect);
        lblUserNameTitle.setSize(70,25);
        lblUserNameTitle.setLocation(10, 70);
        pnlInfo.add(lblUserNameTitle);
        txtUserName.setSize(200,25);
        txtUserName.setLocation(100,70);
        pnlInfo.add(txtUserName);
        lblErrorChosedName.setSize(400,25);
        lblErrorChosedName.setLocation(350, 100);
        frmClinet.add(lblErrorChosedName);
        lblListTitle.setSize(100,25);
        lblListTitle.setLocation(60, 100);
        frmClinet.add(lblListTitle);
        paneList.setSize(200, 500);
        paneList.setLocation(10, 130);
        frmClinet.add(paneList);
        paneMsgs.setSize(300,450);
        paneMsgs.setLocation(220, 130);
        frmClinet.add(paneMsgs);
        txtmsg.setSize(200, 25);
        txtmsg.setLocation(220, 580);
        frmClinet.add(txtmsg);
        btnRoadCast.setSize(100,25);
        btnRoadCast.setLocation(530, 130);
        frmClinet.add(btnRoadCast);
        btnSend.setSize(100, 25);
        btnSend.setLocation(530,200);
        frmClinet.add(btnSend);
        btnExit.setSize(100,25);
        btnExit.setLocation(530, 305);
        frmClinet.add(btnExit);
        frmClinet.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frmClinet.setSize(700,700);
        frmClinet.setVisible(true);
        btnRoadCast.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                out.println(Utils.prepareBroadcast(txtUserName.getText(), txtmsg.getText()));
                txtmsg.setText(null);
            }
        });
        btnSend.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if(listActiveUsers.isSelectionEmpty())
                    JOptionPane.showMessageDialog(null, "u didnt sellect any user to sent the msg to", null, 0);
                else{
                    out.println(Utils.prepareMessage(txtUserName.getText(), listActiveUsers.getSelectedValue().toString(), txtmsg.getText()));
                    msgs.append(txtUserName.getText()+": "+txtmsg.getText()+"\n");
                    txtmsg.setText(null);
                }
            }
        });
        btnExit.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if(isConnected)
                    out.println(Utils.prepareEndConnection(txtUserName.getText()));
                System.exit(0);
            }
        });
        btnConnect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(!isConnected){
                    if(txtPort.getText().isEmpty()||txtServerIP.getText().isEmpty()||txtUserName.getText().isEmpty()){
                        JOptionPane.showMessageDialog(null, "u should fill all info in gray border", null, 0);
                    }else{
                        Thread t = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    int i =  Integer.parseInt(txtPort.getText());
                                    s=new Socket(txtServerIP.getText(),i);
                                    out = new PrintWriter(s.getOutputStream(), true);
                                    in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                                    btnConnect.setText("Disconnect");
                                    txtPort.setEditable(false);
                                    txtServerIP.setEditable(false);
                                    txtUserName.setEditable(false);
                                    isConnected =true;
                                    String message ;
                                    out.println(Utils.prepareHandShake(txtUserName.getText()));
                                    while ((message=in.readLine())!=null) {
                                        int q=Utils.identifyMessage(message);
                                        if(q==5){
                                            String name = JOptionPane.showInputDialog("Sorry but this name is already used,try an other one");
                                            txtUserName.setText(name);
                                            out.println(Utils.prepareHandShake(name));
                                        }else if(q==3){
                                            String[] element=Utils.processUsersUpdate(message);
                                            updatelist(element);
                                            break;
                                        }
                                    }//end of check hands
                                    while ((message = in.readLine()) != null) {
                                        int a =Utils.identifyMessage(message);
                                        String[] msg;
                                        if(a==1){
                                            msg=Utils.processMessage(message);
                                            msgs.append(msg[0]+": "+msg[2]+"\n");
                                        }else if(a==2){
                                            msg=Utils.processBroadcast(message);
                                            msgs.append(msg[0]+": "+msg[1]+"\n");
                                        }else if(a==3){
                                            msg=Utils.processUsersUpdate(message);
                                            updatelist(msg);
                                        }else if(a==4){
                                            btnConnect.setText("Connect");
                                            txtPort.setEditable(true);
                                            txtServerIP.setEditable(true);
                                            txtUserName.setEditable(true);
                                            isConnected =false;
                                            listActiveUsers.setModel(new DefaultListModel());
                                            s.close();
                                            break;
                                        }
                                        msgs.repaint();
                                    }
                                } catch (UnknownHostException e1) {
                                    e1.printStackTrace();
                                    JOptionPane.showMessageDialog(null, "u should fill all info in gray border", null, 0);
                                } catch (IOException e1) {
                                    e1.printStackTrace();
                                } catch (NumberFormatException d) {
                                    JOptionPane.showMessageDialog(null,"Sorry,port number must be a numeric input",null,0);
                                }
                            }
                        });t.start();
                    }
                }else{
                    out.println(Utils.prepareEndConnection(txtUserName.getText()));
                }
            }
        });
    }
    public void updatelist(String[] newMember){
        DefaultListModel model= new DefaultListModel();
        for (int j = 0; j < newMember.length; j++) {
            if(!newMember[j].equals(txtUserName.getText())){
                model.addElement(newMember[j]);
            }
        }
        listActiveUsers.setModel(model);
    }
    public static void main(String[] args) {
        Client c = new Client();
        c.init();
    }
}