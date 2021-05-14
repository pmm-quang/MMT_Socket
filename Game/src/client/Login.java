package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Login extends JFrame implements ActionListener {
    Container container=getContentPane();
    JLabel userLabel=new JLabel("USERNAME");
    JLabel nameOfTheGame = new JLabel("Find Number");
    JTextField userTextField=new JTextField();
    JButton loginButton=new JButton("LOGIN");
    JButton exitButton =new JButton("EXIT");
    String username = "";
    Boolean checkLogin = false;
    JLabel background;

    public Login() {
        setTitle("Login");
        setBounds(10, 10, 370, 600);
        ImageIcon img = new ImageIcon("Game\\src\\client\\Background.jpg");

        background = new JLabel(img, JLabel.CENTER);
        nameOfTheGame.setFont(new Font("Georgia",Font.BOLD , 50));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayoutManager();
        setLocationAndSize();
        addComponentsToContainer();
        addActionEvent();
        setResizable(false);
        setVisible(true);
    }

    public void setLayoutManager() {
        container.setLayout(null);
    }

    public void setLocationAndSize() {
        nameOfTheGame.setBounds(5, 40, 350, 200);
        background.setBounds(0, 0, 370, 600);
        userLabel.setBounds(50,280,100,30);
        userTextField.setBounds(150,280,150,30);
        loginButton.setBounds(50,350,100,30);
        exitButton.setBounds(200,350,100,30);
    }

    public void addComponentsToContainer() {
        container.add(nameOfTheGame);
        container.add(userLabel);
        container.add(userTextField);
        container.add(loginButton);
        container.add(exitButton);
        container.add(background);
    }

    public static void main(String[] args) {
        new Login();
    }

    public void addActionEvent() {
        loginButton.addActionListener(this);
        exitButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            if (userTextField.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Hãy nhập username!","Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Login success!!!");
                username = userTextField.getText();
                checkLogin = true;
                CheckConnect.check = true;
                System.out.println(CheckConnect.check);
                this.setVisible(false);
            }
        }

        if (e.getSource() == exitButton) {
            int output = JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc muốn thoát game?", "Cảnh báo!", JOptionPane.WARNING_MESSAGE);
            if (output == JOptionPane.YES_OPTION) {
                System.exit(0);
            }


        }
    }

    public Boolean getCheckLogin() {
        return checkLogin;
    }

    public String createUsername() {
        return username;
    }
}
