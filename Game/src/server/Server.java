package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket server;
    private user user1,user2;

    public Server(){
        System.out.println("CHAT SERVER");
    }

    public void runServer(){
        try{
            server=new ServerSocket(6789,2);

            while(true){
                try{
                    user1=new user(server.accept(),"user1");
                    System.out.println("client 1 ket noi thanh cong");
                    //user1.start();
                    user2=new user(server.accept(),"user2");
                    System.out.println("client 2 ket noi thanh cong");
                    while(true){
                        user1.start();
                        user2.start();
                    }
                }
                catch(Exception e){}
            }
        }
        catch(EOFException e){
            System.out.println("Client ket thuc ket noi");
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String args[]){
        Server app=new Server();
        app.runServer();
    }

    //tao thread cho tung user ket noi
    private class user extends Thread{
        private Socket connection;
        private ObjectInputStream input;
        private ObjectOutputStream output;
        String name;

        public user(Socket socket,String name){
            //  userId=number;
            connection=socket;
            this.name=name;
            try{
                input=new ObjectInputStream(connection.getInputStream());
                output=new ObjectOutputStream(connection.getOutputStream());
            }
            catch(IOException e){
                System.exit(1);
            }
        }
        public ObjectInputStream getObjectInputStream(){
            return this.input;
        }
        public ObjectOutputStream getObjectOutputStream(){
            return this.output;
        }
        /*
         * Day la phan cuc ki quan trong cua app
         * synchronized : lam cho thread khong chay lung tung
         * Chi khi nao thread nay thuc hien xong thi thread kia moi duoc chay
         */
        public synchronized void chuyen(user userA,user userB){
            try{
                //while(true){
                StringBuffer st = new StringBuffer();
                String dulieu=userA.getObjectInputStream().readObject().toString();
                st.append(dulieu);

                userB.getObjectOutputStream().writeObject(dulieu);
                //}
            }
            catch(Exception e){}
        }
        public void run(){
            //chuyen thong tin tu client nay sang client kia
            while(true){
                if(name.equals("user1")){
                    chuyen(this,user2);
                }else{
                    chuyen(this,user1);

                }
            }

        }
    }
}
