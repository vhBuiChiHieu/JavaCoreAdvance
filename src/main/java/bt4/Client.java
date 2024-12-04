package bt4;

import common.ConfigUtil;
import jdk.jfr.Description;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class Client {
    private final Logger logger = Logger.getLogger(Client.class);
    private String serverIp;
    private int port, connectTimeOut;
    private boolean isRunning = true;

    @Description("Lấy cấu hình từ file")
    private void getConfig() {
        try {
            serverIp = ConfigUtil.get("server.ip");
            port = Integer.parseInt(ConfigUtil.get("server.port"));
            connectTimeOut = Integer.parseInt(ConfigUtil.get("connect.timeout"));
        } catch (NumberFormatException e) {
            logger.warn("Du lieu khong dung dinh dang", e);
        }
    }

    @Description("Kết nối đến server và thực hiện logic chat")
    public void startClient() {
        try (Socket socket = new Socket(serverIp, port)) {
            socket.setSoTimeout(connectTimeOut); //Gioi han thoi gian nhan du lieu
            logger.info("Kết nối đến server " + serverIp + ":" + port + " thành công");
            //Khoi tao IO
            try (
                    InputStream input = socket.getInputStream();
                    BufferedReader serverReader = new BufferedReader(new InputStreamReader(input));
                    OutputStream output = socket.getOutputStream();
                    PrintWriter writer = new PrintWriter(output, true);
                    BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))
            ) {
                // Tao 2 Thread de gui va nhan
                Thread receiveThread = new Thread(() -> {
                    try {
                        String receiveMess;
                        while (isRunning && (receiveMess = serverReader.readLine()) != null) {
                            System.out.println("Server: " + receiveMess);
                        }
                        isRunning = false;
                    } catch (IOException e) {
                        logger.error("Loi khi nhan du lieu", e);
                    }
                });
                Thread sendThread = new Thread(() -> {
                    try {
                        String sentMess;
                        //Neu nhap stop se dong
                        while (isRunning && (sentMess = consoleReader.readLine()) != null) {
                            if (sentMess.equalsIgnoreCase("stop")) {
                                isRunning = false;
                            } else {
                                writer.println(sentMess);
                            }
                        }
                    } catch (IOException e) {
                        logger.error("Loi khi gui mess den server", e);
                    }
                });
                receiveThread.start();
                sendThread.start();
                //Doi cac luong ket thuc
                receiveThread.join();
                sendThread.join();

            } catch (InterruptedException e) {
                logger.error("Loi trong jhi chat", e);
            }
        } catch (IOException e) {
            logger.error("Ket noi that bai", e);
        }
    }
    private void run(){
        getConfig();
        startClient();
    }
    public static void main(String[] args) {
        new Client().run();
    }
}
