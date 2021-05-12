package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends JFrame implements Runnable, ActionListener {
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message="";//message nay se gui vi tri i,j cua button ma client vua nhan
    private String chatServer;
    private Socket client;

    private Square[][] arrayButton;
    //qui dinh chieu dai va chieu rong cua table
    private int length=15;
    private int width=15;
    private GridBagConstraints contraints = new GridBagConstraints();
    private GridBagLayout layout = new GridBagLayout();

    private JTextField tf_nhap, tf_enterchat;
    private JTextArea ta_content;
    private JButton bt_send;
    private Timer timer;
    private Integer second, minute;
    JLabel l_demthoigian;
    TextField textField;
    JPanel p;
    String temp = "";
    String strNhan = "";
    int xx, yy, x, y;


    private static int i,j;

    private int sttu;
    /*
     *nham xac dinh so thu tu cho clien quy dinh viec gan O,X cho client tuong ung
     *if sothutu==1 -->X
     *if sothutu==2 -->O
     *Viec qui dinh nay duoc thuc hien o ham khoi tao
     */

    private boolean dadi = false;
    /*
     * kiem tra xem client da di chua
     * Neu da di thi khong  duoc di tiep
     * dadi = false --> duoc phep di
     */

    private boolean dacowinner = false;
    public Client(String host, int sttu){
        super("Client");
        this.sttu = sttu;

        chatServer=host;
        this.setSize(800, 500);
        x = 25;
        y = 25;
        this.getContentPane().setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);

        p = new JPanel();
        p.setBounds(10, 30, 400, 400);
        p.setLayout(new GridLayout(x, y));
        this.add(p);

        //khung chat
        Font fo = new Font("Arial",Font.BOLD,15);
        ta_content = new JTextArea();
        ta_content.setFont(fo);
        ta_content.setBackground(Color.white);

        ta_content.setEditable(false);
        JScrollPane sp = new JScrollPane(ta_content);
        sp.setBounds(430,170,300,180);
        bt_send = new JButton("Gui");
        bt_send.setBounds(640, 390, 70, 40);
        tf_nhap = new JTextField(30);
        tf_nhap.setFont(fo);
        tf_enterchat = new JTextField("");
        tf_enterchat.setFont(fo);
        tf_enterchat.setBounds(430, 400, 200, 30);
        tf_enterchat.setBackground(Color.white);
        this.add(tf_enterchat);
        this.add(bt_send);
        this.add(sp);
        this.setVisible(true);
        bt_send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = tf_enterchat.getText();
                ta_content.append("ME: " + msg + "\n");
                sendData("chatsend," + msg +"," + sttu);
                tf_enterchat.setText("");
                tf_enterchat.requestFocus();
                ta_content.setVisible(false);
                ta_content.setVisible(true);
            }
        });
        tf_enterchat.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String msg = tf_enterchat.getText();
                ta_content.append("ME: " + msg + "\n");
                sendData("chatsend," + msg +"," + sttu);
                tf_enterchat.setText("");
                tf_enterchat.requestFocus();
                ta_content.setVisible(false);
                ta_content.setVisible(true);
            }
        });

        //   Container container = getContentPane();
        //   container.setLayout(layout);
        //  contraints.fill = GridBagConstraints.BOTH;
        arrayButton = new Square[length][width];
        for(int i=0;i<length;i++){
            for ( int j=0;j<width;j++) {
                //   contraints.gridx = i;
                //   contraints.gridy = j;
                //   contraints.weightx = 2;
                //   contraints.weighty = 2;
                arrayButton[i][j] = new Square(false);
                arrayButton[i][j].setBackground(Color.LIGHT_GRAY);
                //   layout.setConstraints(arrayButton[i][j],contraints);
                //set icon truoc de khong bi dan button sau nay
                //    arrayButton[i][j].setIcon(new ImageIcon("blank.gif"));
                p.add(arrayButton[i][j]);

                arrayButton[i][j].addActionListener(this);
            }
        }
        setSize(700,600);
        setVisible(true);
        setResizable(false);
    }

    public void run(){
        try{
            connectToServer();
            getStreams();
            processConnection();
            closeConnection();
        }
        catch(EOFException e){
            //System.out.println("Server ket thuc ket noi");
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    private void connectToServer(){
        //displayArea.setText("Co gang ket noi\n");
        try{
            client=new Socket(InetAddress.getByName(chatServer),6789);
        }
        catch(Exception e){
            //displayArea.append("Rot mang ,hay co gang thu lai xem sao");
        }
        //displayArea.append("ket noi vao :"+client.getInetAddress().getHostName());
    }

    private void getStreams() throws IOException{
        output=new ObjectOutputStream(client.getOutputStream());
        output.flush();
        input=new ObjectInputStream(client.getInputStream());
        //displayArea.append("\n got IO stream \n");
    }
    private void processConnection() throws IOException{
        do{
            try{
                //xu ly phan ket qua nha duoc tu server
                //Client se nhan duoc nuoc di cua doi thu luu trong message

                message=(String) input.readObject();
                String[] tmp = message.split(",");
                System.out.println(sttu+"nh"+message);
                //phan tich message
                int sothutu = getSothutu();
                if (!tmp[0].equals("chatsend")) {
                    int i = getI();
                    int j = getJ();
                    arrayButton[i][j].setStatus(true);
                    if (sothutu == 1) {
                        arrayButton[i][j].setIcon(new ImageIcon("x.gif"));
                        arrayButton[i][j].setValue(1);
                    } else if (sothutu == 2) {
                        arrayButton[i][j].setIcon(new ImageIcon("o.gif"));
                        arrayButton[i][j].setValue(2);
                    }
                    dadi = false;
                } else {
                    ta_content.append("KHACH: " + tmp[1] + "\n");
                }
                /*
                 * Truong hop nay client nhan duoc tin hieu tu client kia
                 * Tuc la Client nay se duoc phep choi trong luot tiep theo
                 */
                if(isWinner()){
                    dacowinner = true;
                    break;
                }
            }
            catch(ClassNotFoundException e){
                //displayArea.append("\n Unknown object type received");
            }
        }while(true);
    }

    private void closeConnection() throws IOException{
        output.close();
        input.close();
        client.close();
    }

    private void sendData(String message){
        try{
            System.out.println("gui"+message);
            output.writeObject(message);
            output.flush();
        }
        catch(IOException e){
            System.err.println("Loi trong viec write Object");
        }
    }

    public static void main(String args[]){
        Client app;
        if(args.length==0){
            //chu y ham khoi tao se qui dinh So thu tu client
            app=new Client("127.0.0.1",1);
        }
        else{
            app=new Client(args[0],1);
        }
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Thread t=new Thread(app);
        t.start();
        //app.runClient();
    }


    public void actionPerformed(ActionEvent event) {
        StringBuffer buffer = new StringBuffer(2048);

        for(int i= 0;i<length ;i++){
            for(int j=0;j<width;j++){
                //tim xem nut nao duoc nhan
                //nham lay vi tri cua button duoc kich hoat
                if(arrayButton[i][j]==event.getSource()&& (arrayButton[i][j].isStatus()==true || dadi == true)){
                    //truong hop button nay da duoc chon
                    //hoac client nay di lien tiep 2 buoc
                    //-->INVALID
                    JOptionPane.showMessageDialog(Client.this,"Ban dang vi pham luat");
                }else if(arrayButton[i][j]==event.getSource()&&sttu==1&&arrayButton[i][j].isStatus()==false){
                    //client 1
                    arrayButton[i][j].setIcon(new ImageIcon("x.gif"));
                    arrayButton[i][j].setStatus(true);
                    arrayButton[i][j].setValue(1);
                    //message gui di cho server
                    buffer.append(i).append(",").append(j).append(",").append(sttu);
                    sendData(buffer.toString());
                    dadi = true;


                }else if(arrayButton[i][j]==event.getSource()&&sttu==2&&arrayButton[i][j].isStatus()==false) {
                    //client 2
                    arrayButton[i][j].setIcon(new ImageIcon("o.gif"));
                    arrayButton[i][j].setStatus(true);
                    arrayButton[i][j].setValue(2);
                    //message
                    buffer.append(i).append(",").append(j).append(",").append(sttu);
                    sendData(buffer.toString());
                    dadi = true;

                }
                arrayButton[i][j].setAutoscrolls(false);
                arrayButton[i][j].setRolloverEnabled(false);
                if(isWinner()){
                    //JOptionPane.showMessageDialog(CaroClient.this,"WIN");
                    dacowinner = true;
                    break;
                }
            }
            /*if(dacowinner){
                JOptionPane.showMessageDialog(CaroClient.this,"WIN");
            }*/
        }
    }


    /**
     *  cau truc cua 1 message:i,j,sothutu
     *i:vi tri dong cua button ma client vua nhan
     *j:vi tri cot cua button ma client vua nhan
     * sothutu: nham xac dinh kieu x hay o(kieu cua client)
     */

    public int getI(){
        String i = message.substring(0,message.indexOf(","));
        //System.out.println(i);
        try {
            int vitridong = Integer.parseInt(i);
            return vitridong;
        } catch (Exception e) {
            return -1;
        }
    }

    public int getJ(){
        String j = message.substring(message.indexOf(",")+1,message.lastIndexOf(","));
        //System.out.println(j);
        try {
            int vitricot = Integer.parseInt(j);
            return vitricot;
        } catch (Exception e) {
            return -1;
        }
    }
    public int getSothutu() {
        String stt = message.substring(message.lastIndexOf(",")+1);
        //System.out.println(sothutu);
        try {
            int sothutu = Integer.parseInt(stt);
            return sothutu;
        } catch (Exception e) {
            return -1;//default
        }
    }

    /*
    public boolean isWin(int vitricot, int vitridong) {
        int count_dong1 = 0,count_dong2=0;
        int count_cot1 = 0,count_cot2 = 0;
        int count_duongcheo_trai_phai1 = 0,count_duongcheo_trai_phai2 = 0;
        int count_duongcheo_phai_trai1 = 0,count_duongcheo_phai_trai2 = 0;
        for(int i=vitricot;i<length;i++){
            if(arrayButton[i][vitridong].getValue()==1){
                count_dong1++;
            }
            else {
                break;
            }
        }

        for(int j=vitridong;j<width;j++){
            if(arrayButton[vitricot][j].getValue()==1) {
                count_cot1++;
            } else {
                break;
            }

        }
        for(int i=vitricot;i<length;i++) {
            if(arrayButton[i][vitridong].getValue()==1){
                count_duongcheo_trai_phai1++;
                vitridong++;
            } else {
                break;
            }
        }
        for(int i=vitridong;i>0;i--) {
            if(arrayButton[vitricot][i].getValue()==1){
                count_duongcheo_phai_trai1++;
                vitricot++;
            } else {
                break;
            }
        }
        if(count_cot1==5||count_dong1==5||count_duongcheo_phai_trai1==5||count_duongcheo_trai_phai1==5){
            return true;
        } else {
            return false;
        }
    }*/

    public boolean isWinner(){
        int count_hang = 0;
        for(int i=0;i<length;i++){
            for(int j=0;j<width;j++){
                if(arrayButton[i][j].getValue()==1){
                    count_hang++;
                } else {
                    count_hang = 0;
                    continue;
                }
                if(count_hang==5){
                    //lan nguoc xac dinh duoc nuoc thang
                    //chi co j doi i khong doi
                    for(int l=j;l>=j-4;l--){
                        arrayButton[i][l].setBackground(Color.RED);
                    }
                    return true;
                }
            }
        }

        int count_cot = 0;
        for(int i=0;i<length;i++){
            for(int j=0;j<width;j++){
                if(arrayButton[j][i].getValue()==1){
                    count_cot++;
                } else {
                    count_cot = 0;
                    continue;
                }
                if(count_cot == 5){
                    for(int l=j;l>=j-4;l--){
                        arrayButton[l][i].setBackground(Color.RED);
                    }
                    return true;
                }
            }
        }
        /*
        int count_cheo1 = 0;
        for(int i=0;i<length;i++){
            for(int j=0;j<width;j++){
                while(j<width&&i<length){
                    if(arrayButton[i][j].getValue()==1){
                        count_cheo1++;
                        j++;
                        i++;
                    } else {
                        count_cheo1 = 0;
                        continue;
                    }
                    if(count_cheo1 == 5){
                        for(int l=i;l<=i-4;l--){
                            for(int k=j;k<=j-4;k--){
                                arrayButton[l][k].setBackground(Color.blue);
                            }
                            return true;
                        }
                    }
                }
            }
        }*/

        int count_hango = 0;
        for(int i=0;i<length;i++){
            for(int j=0;j<width;j++){
                if(arrayButton[i][j].getValue()==2){
                    count_hango++;
                } else {
                    count_hango = 0;
                    continue;
                }
                if(count_hango==5){
                    //lan nguoc xac dinh duoc nuoc thang
                    //chi co j doi i khong doi
                    for(int l=j;l>=j-4;l--){
                        arrayButton[i][l].setBackground(Color.blue);
                    }
                    return true;
                }
            }
        }

        int count_coto = 0;
        for(int i=0;i<length;i++){
            for(int j=0;j<width;j++){
                if(arrayButton[j][i].getValue()==2){
                    count_coto++;
                } else {
                    count_coto = 0;
                    continue;
                }
                if(count_coto == 5){
                    for(int l=j;l>=j-4;l--){
                        arrayButton[l][i].setBackground(Color.blue);
                    }
                    return true;
                }
            }
        }

        return false;
    }


}
