package client;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;

public class Chat extends JPanel {
    JTextArea ta_content;
    JButton bt_send;
    JTextField tf_enterchat;
    JTextPane textPane;
    Document doc;
    SimpleAttributeSet attributeSet;
    String message = "";

    public Chat() {
        this.setLayout(null);
        this.setSize(260, 350);
        this.setBackground(Color.BLACK);
        setPanelChat();

    }
    public void setFontMessage() {
        message = tf_enterchat.getText();
        if (!message.equals("")) {
            tf_enterchat.setText("");
            //    tf_enterchat.requestFocus();
            attributeSet = new SimpleAttributeSet();
            try {
                StyleConstants.setForeground(attributeSet, Color.BLUE);
                StyleConstants.setBold(attributeSet, true);
                doc.insertString(doc.getLength(), "Tôi: ", attributeSet);
                StyleConstants.setBold(attributeSet, false);
                StyleConstants.setItalic(attributeSet, true);
                doc.insertString(doc.getLength(), message + "\n", attributeSet);
            } catch (BadLocationException badLocationException) {
                badLocationException.printStackTrace();
            }
        }
    }
    //hiển thị trên khung chat
    public void notice(String username, String key)  {
        attributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(attributeSet, Color.RED);
        try {
            if (key.equals(Key.LOGIN.toString())) {
                doc.insertString(doc.getLength(), "'" + username + "' đã đăng nhập thành công!\n", attributeSet);
            } else if( key.equals(Key.QUIT.toString())) {
                doc.insertString(doc.getLength(), "'" + username + "' đã thoát!\n", attributeSet);
            }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public JTextField getTf_enterchat() {
        return tf_enterchat;
    }
    public JButton getBt_send() {
        return bt_send;
    }

    public String sendMessage() {
        if (!message.equals("")) {
            String msg ="CHAT" + "," + message;
            message = "";
            return msg;
        }
        return "";
    }

    public void receivedMessage(String message, String username) {
        attributeSet = new SimpleAttributeSet();
        try {
            StyleConstants.setBold(attributeSet, true);
            this.doc.insertString(doc.getLength(), username + ": ", attributeSet);
            StyleConstants.setBold(attributeSet, false);
            this.doc.insertString(doc.getLength(), message + "\n", attributeSet);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void setPanelChat() {
        Font fo = new Font("Arial",Font.BOLD,15);
        ta_content = new JTextArea();
        ta_content.setFont(fo);
        ta_content.setBackground(Color.white);
        ta_content.setEditable(false);

        textPane = new JTextPane();
        textPane.setEditable(false);
        textPane.setBackground(Color.WHITE);
        doc = textPane.getStyledDocument();

        JScrollPane sp = new JScrollPane(textPane);
        sp.setBounds(0,0,260,280);
        bt_send = new JButton("Gui");
        bt_send.setBounds(200, 300, 60, 30);
        tf_enterchat = new JTextField(30);
        tf_enterchat.setFont(fo);
        tf_enterchat.setBounds(0, 300, 195, 30);
        tf_enterchat.setBackground(Color.white);
        this.add(tf_enterchat);
        this.add(bt_send);
        this.add(sp);
    }



}
