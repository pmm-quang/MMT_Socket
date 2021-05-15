package client;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.awt.event.*;

import javax.swing.*;


public class Client extends JFrame implements Runnable,ActionListener{
    //private JTextField enterField;
    //private JTextArea displayArea;
    public static Boolean check = false;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private String message="";//message nay se gui vi tri i,j cua button ma client vua nhan
    private String chatServer;
    private Socket client;
    Login login;
    Boolean otherConnect = false;

    private Square[][] arrayButton;
    //qui dinh chieu dai va chieu rong cua table

    private GridBagConstraints contraints = new GridBagConstraints();
    private GridBagLayout layout = new GridBagLayout();

    private JTextField tf_nhap, tf_enterchat;
    private JTextArea ta_content;
    private JButton bt_send;
    private Timer timer;
    private Integer second, minute;
    private JLabel player_score_label;
    private JLabel opponent_score_label;
    JLabel l_demthoigian;
    TextField textField;
    JPanel p;
    String temp = "";
    String strNhan = "";
    JButton start;
    Boolean play= false;
    int xx, yy, x, y;



    Chat boxChat;
    String username = "";

    /*TODO: Nhét cho t cái trường gì mà nó hiện điểm của mình với của địch với, hai cái score t để ở dưới rồi
            với lại xem cái hét game thì làm kiểu gì nhé
            nếu không làm đc bảo t
    */

    public static int SQRT_OF_NUMBER_OF_NUMBERS = 10;   //Thêm cái này cho đỡ viết số 10
    static int current_number;                          //Số hiện tại cần phải bấm để có điểm
    private int player_score;                           //Điểm của người chơi
    private int opponent_score;                         //Điểm của đối thủ

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
    public Client(String host, String username){
        super("Client");
        player_score = 0;
        opponent_score = 0;
        current_number = 1;
        this.setSize(700, 600);
        x = 25;
        y = 25;
        this.getContentPane().setLayout(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        p = new JPanel();
        p.setBounds(10, 30, 400, 400);
        p.setLayout(new GridLayout(10, 10));
        add(p);
        player_score_label = new JLabel("MY SCORE: " + Integer.toString(player_score));
        player_score_label.setLayout(null);
        player_score_label.setBounds(520, 20, 200, 20);
        player_score_label.setFont(new Font("Consolas", Font.BOLD, 18));
        player_score_label.setForeground(Color.RED);
        player_score_label.setVisible(true);
        opponent_score_label = new JLabel("ENEMY'S SCORE: " + Integer.toString(opponent_score));
        opponent_score_label.setLayout(null);
        opponent_score_label.setBounds(520, 50, 200, 20);
        opponent_score_label.setFont(new Font("Consolas", Font.PLAIN, 14));
        opponent_score_label.setForeground(Color.BLACK);
        opponent_score_label.setVisible(true);
        add(player_score_label);
        add(opponent_score_label);
        this.username = username;
        chatServer=host;
        connectToServer();
        getStreams();

        arrayButton = new Square[SQRT_OF_NUMBER_OF_NUMBERS][SQRT_OF_NUMBER_OF_NUMBERS];
        for(int i=0;i<SQRT_OF_NUMBER_OF_NUMBERS;i++){
            for ( int j=0;j<SQRT_OF_NUMBER_OF_NUMBERS;j++) {
                arrayButton[i][j] = new Square(false);
                try {
                    arrayButton[i][j].setValue(input.readInt());
                    System.out.println(arrayButton[i][j].getValue());
                } catch (IOException e) {
                    System.out.println("Loi doc ma tran");
                }
                arrayButton[i][j].setBackground(Color.LIGHT_GRAY);
                arrayButton[i][j].setMargin(new Insets(0, 0, 0, 0));
                arrayButton[i][j].setFont(new Font("Consolas", Font.PLAIN, 14));
                arrayButton[i][j].setText(Integer.toString(arrayButton[i][j].getValue()));
                p.add(arrayButton[i][j]);
                int finalI = i;
                int finalJ = j;
                arrayButton[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        super.mousePressed(e);
                        Square current_button = arrayButton[finalI][finalJ];
                        if (current_button.getValue() == current_number) {
                            sendData(Key.GAME.toString() + "," + current_number);
                            current_button.setVisible(false);
                            ++current_number;
                            ++player_score;
                            player_score_label.setText("MY SCORE: " + Integer.toString(player_score));
                        }
                    }
                });
            }
        }
        sendData(Key.LOGIN.toString() + ",đã kết nối!");
        add(ButtonPanel());
        boxChat = new Chat();
        boxChat.setBounds(430, 170,260, 350);
        add(boxChat);
        boxChat.notice(username, Key.LOGIN.toString());



        boxChat.getBt_send().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boxChat.setFontMessage();
                String msg = boxChat.sendMessage();
                if (!msg.equals(""))
                    sendData(msg);
            }
        });
        boxChat.getTf_enterchat().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boxChat.setFontMessage();
                String msg = boxChat.sendMessage();
                if (!msg.equals(""))
                    sendData(msg);
            }
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
                int confirmedPane = JOptionPane.showConfirmDialog(null,
                        "Are you sure you want to exit the program?",
                        "Exit Program Message Box",
                        JOptionPane.YES_NO_OPTION);

                if (confirmedPane == JOptionPane.YES_OPTION) {
                    sendData(Key.QUIT.toString() + ",da out");
                    System.out.println("Yes is the option");
                    System.exit(0);
                }
            }
        });



        //   setSize(700,600);
        setVisible(true);
        setResizable(false);
    }

    private JPanel ButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setSize(70, 130);
        panel.setBounds(430, 30, 70, 130);
        ImageIcon imageIcon = new ImageIcon("src\\o.gif");

        JButton start = new JButton("", imageIcon);
        start.setBackground(Color.green);
        start.setBounds(0,0, 70, 60);
        start.setSize(imageIcon.getIconWidth(), imageIcon.getIconHeight());

        JButton quit = new JButton("Quit");
        quit.setBackground(Color.RED);
        quit.setBounds(0, 70, 70, 60);
        start.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendData(Key.INVITE.toString() + ",play game");
            }
        });

        panel.add(start);
        panel.add(quit);
        return panel;
    }

    public void run(){
        try{
            processConnection();
            closeConnection();
        }
        catch(EOFException e){
            System.out.println("Server ket thuc ket noi");
            System.exit(0);
        }
        catch(IOException e){
            System.out.println("Loi khi processConnection");
        }
    }

    private void connectToServer(){
        try{
            client=new Socket(InetAddress.getByName(chatServer),6789);

        }
        catch(Exception e){
            //displayArea.append("Rot mang ,hay co gang thu lai xem sao");
        }
        //displayArea.append("ket noi vao :"+client.getInetAddress().getHostName());
    }

    private void getStreams()  {
        try {
            output = new ObjectOutputStream(client.getOutputStream());
            output.flush();
            input = new ObjectInputStream(client.getInputStream());

            //getNumberMatrixFromServer(input);

        } catch (NullPointerException e) {
            System.exit(0);
        } catch (IOException e) {
            System.out.println("Loi getStream");
        }

        //displayArea.append("\n got IO stream \n");
    }

    private void processConnection() throws IOException{
        do{
            try{
                //xu ly phan ket qua nha duoc tu server

                message=(String) input.readObject();
                String[] tmp = message.split(",");

                System.out.println(username+" nhan: "+message);
                //phan tich message
                if (tmp[0].equals("CHAT")) {

                    boxChat.receivedMessage(tmp[1], tmp[2]);

                } else if (tmp[0].equals(Key.LOGIN.toString())) {

                    boxChat.notice(tmp[2], tmp[0]);

                } else if (tmp[0].equals(Key.INVITE.toString())) {

                    int output = JOptionPane.showConfirmDialog(this, "Thông báo",
                            tmp[2] + " mời bạn chơi game.", JOptionPane.YES_NO_OPTION);
                    if(output == JOptionPane.YES_OPTION) {
                        sendData(Key.ACCEPT.toString() + ",play");
                        play = true;
                    }

                } else if (tmp[0].equals(Key.ACCEPT.toString())) {

                    play = true;
                } else if (tmp[0].equals(Key.QUIT.toString())) {
                    boxChat.notice(tmp[2], tmp[0]);
                } else if (tmp[0].equals(Key.GAME.toString())) {
                    //Tao mượn cái key.game để đại diện cho thằng client gửi tín hiệu bấm nút số
                    if (tmp[1].equals(Integer.toString(current_number))) {
                        for (int i = 0; i < SQRT_OF_NUMBER_OF_NUMBERS; ++i) {
                            for (int j = 0; j < SQRT_OF_NUMBER_OF_NUMBERS; ++j) {
                                if (arrayButton[i][j].getValue() == current_number) {
                                    arrayButton[i][j].setVisible(false);
                                    break;
                                }
                            }
                        }
                        ++current_number;
                        ++opponent_score;
                        opponent_score_label.setText("ENEMY'S SCORE: " + Integer.toString(opponent_score));
                    }
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
        input.close();
        output.close();
        client.close();
    }

    /*private void getNumberMatrixFromServer(ObjectInputStream input) {
        for (int i = 0; i < SQRT_OF_NUMBER_OF_NUMBERS; ++i) {
            for (int j = 0; j < SQRT_OF_NUMBER_OF_NUMBERS; ++j) {
                try {

                } catch (IOException e) {
                    System.out.println("Error when getting number matrix from server");
                }
                System.out.print(arrayButton[i][j].getValue() + " ");
            }
            System.out.println();
        }
    }

     */

    private void sendData(String message){
        try{
            System.out.println("gui: "+message);
            output.writeObject(message + "," + username);
            output.flush();
        }
        catch(IOException e){
            System.err.println("Loi trong viec write Object");
        }
    }

    public static void main(String args[]){
        Client app;
        Login login = new Login();
        while (CheckConnect.check == false) {
            Boolean check = CheckConnect.check;
        }
        if (CheckConnect.check) {
            if (args.length == 0) {
                app = new Client("127.0.0.1", login.createUsername());
                System.out.println(0);
            } else {
                app = new Client(args[0], login.createUsername());
                System.out.println(1);
            }
            app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            Thread t = new Thread(app);
            t.start();

        }
        //app.runClient();
    }



    public void actionPerformed(ActionEvent event) {
        StringBuffer buffer = new StringBuffer(2048);
        if (play) {
            for(int i= 0;i<SQRT_OF_NUMBER_OF_NUMBERS ;i++) {
                for (int j = 0; j < SQRT_OF_NUMBER_OF_NUMBERS; j++) {
                    //tim xem nut nao duoc nhan
                    //nham lay vi tri cua button duoc kich hoat
                    if (event.getSource() == arrayButton[i][j]) {
                        //client 1
                        System.out.println("Bam nut " + i + j);
                        arrayButton[i][j].setStatus(true);
                        arrayButton[i][j].setValue(1);
                        //message gui di cho server
                        buffer.append("game,").append(i).append(",").append(j);
                        sendData(buffer.toString());
                    }
                    arrayButton[i][j].setAutoscrolls(false);
                    arrayButton[i][j].setRolloverEnabled(false);
                    if (isWinner()) {
                        //JOptionPane.showMessageDialog(CaroClient.this,"WIN");
                        dacowinner = true;
                        break;
                    }
                }
            }
            /*if(dacowinner){
                JOptionPane.showMessageDialog(CaroClient.this,"WIN");
            }*/
        }
    }



    public boolean isWinner() {
        return false;
    }


}