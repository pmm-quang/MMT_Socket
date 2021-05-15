package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Random;

import static client.Client.SQRT_OF_NUMBER_OF_NUMBERS;

public class Server {
    private ServerSocket server;
    private User user1,user2;
    private int players = 0;

    private int[][] number_matrix = new int[SQRT_OF_NUMBER_OF_NUMBERS][SQRT_OF_NUMBER_OF_NUMBERS];

    public Server(){
        System.out.println("SERVER START!");
        createNumberMatrix();
    }

    public void runServer(){
        try{
            server=new ServerSocket(6789,2);
                try{
                    ++players;
                    user1=new User(server.accept(),"user1");
                    System.out.println("client 1 ket noi thanh cong");
                    //user1.start();
                    user2=new User(server.accept(),"user2");
                    System.out.println("client 2 ket noi thanh cong");
                    user1.start();
                    user2.start();
                }
                catch(Exception e){}

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
    private class User extends Thread{
        private Socket connection;
        private ObjectInputStream input;
        private ObjectOutputStream output;
        String name;

        public User(Socket socket,String name){
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
        public synchronized void chuyen(User userA,User userB){
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
            try {
                for (int i = 0; i < SQRT_OF_NUMBER_OF_NUMBERS; ++i) {
                    for (int j = 0; j < SQRT_OF_NUMBER_OF_NUMBERS; ++j) {
                        output.writeInt(number_matrix[i][j]);
                    }
                }
                output.flush();
            } catch (IOException e) {
                System.out.println("Loi khi truyen du lieu ma tran den client");
            }

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
    private void createNumberMatrix() {
        for (int i = 0; i < SQRT_OF_NUMBER_OF_NUMBERS; ++i) {
            number_matrix[i] = new int[SQRT_OF_NUMBER_OF_NUMBERS];
        }
        boolean[] is_this_number_being_used = new boolean[SQRT_OF_NUMBER_OF_NUMBERS * SQRT_OF_NUMBER_OF_NUMBERS];
        Arrays.fill(is_this_number_being_used, false);
        Random random = new Random();
        int random_number;

        for (int i = 0; i < SQRT_OF_NUMBER_OF_NUMBERS; ++i) {
            for (int j = 0; j < SQRT_OF_NUMBER_OF_NUMBERS; ++j) {
                do {
                    random_number = random.nextInt(100) + 1;
                } while (is_this_number_being_used[random_number - 1]);
                number_matrix[i][j] = random_number;
                is_this_number_being_used[random_number - 1] = true;
            }
        }
    }
}
