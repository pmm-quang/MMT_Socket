package client;

import javax.swing.*;

public class Square extends JButton {
    private boolean status=false;//trang thai cua nut do
    /*
     * Neu nut da duoc nhan thi status = true
     * Nguoc la la false
     */
    private boolean X;
    private boolean O;
    /*
     * value nham xac dinh gia tri cua 1 Square
     * Neu Square do la X(client 1)-->value =1
     * Neu Square do la O(client 2)-->value =2
     * Muc dich : Dung de kiem tra viec ai la nguoi thang
     */
    private int value;

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }

    public Square(boolean status) {
        super();
        this.status = status;
    }
    public boolean isStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }
    public void setX(boolean X) {
        this.X = X;
    }
    public boolean isX() {
        return this.X;
    }
    public void setO(boolean O) {
        this.O = O;
    }
    public boolean isO() {
        return this.O;
    }

}
