package bt4;

import common.ConfigUtil;
import jdk.jfr.Description;
import org.apache.log4j.Logger;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/**
* Mở rộng: viết chương trình chát trên 2 máy tính có kết nối với nhau.
* Gợi ý: viết 2 module trên 2 máy theo cơ chế client – server và kết nối với nhau qua tcp.
* Yêu cầu xử lý ngoại lệ trong quá trình gửi nhận (có thể test bằng việc rút dây mạng trong quá trình gửi nhận).
* Ghi ra log file nếu xảy ra lỗi, đưa vào file cấu hình các tham số
 */
public class Server {
    private final Logger logger = Logger.getLogger(Server.class);
    private int port, receiveTimeOut, connectTimeOut;
    private boolean isRunning = true;
    public Server() {
    }

    @Description("Lấy cấu hình từ file")
    private void getConfig() {
        try {
            port = Integer.parseInt(ConfigUtil.get("server.port"));
            receiveTimeOut = Integer.parseInt(ConfigUtil.get("receive.timeout"));
            connectTimeOut = Integer.parseInt(ConfigUtil.get("connect.timeout"));
        } catch (NumberFormatException e) {
            logger.warn("Du lieu khong dung dinh dang", e);
        }
    }

    @Description("Khởi tạo kết nối và đợi client")
    private void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setSoTimeout(connectTimeOut); // Thoi gian doi ket noi
            logger.info("Server bat dau lang nghe tai port " + port + "...");

            Socket socket = serverSocket.accept(); // Đợi client kết nối
            socket.setSoTimeout(receiveTimeOut);   // Giới hạn thời gian nhận dữ liệu
            logger.info("Client " + socket.getInetAddress() + ":" + socket.getPort() + " da ket noi");

            startChat(socket);
        } catch (SocketTimeoutException e) {
            logger.info("Het gio, khong co client ket noi", e);
        } catch (IOException ex) {
            logger.fatal("Mo server that bai", ex);
        }
    }

    @Description("Xử lý logic chat 2 chiều")
    private void startChat(Socket socket) {
        try (
                InputStream input = socket.getInputStream();
                BufferedReader messReader = new BufferedReader(new InputStreamReader(input));
                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);
                BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))
        ) {
            //2 thread de gui va nhan
            Thread receiveThread = new Thread(() -> {
                try {
                    String receiveMess;
                    while (isRunning && (receiveMess = messReader.readLine()) != null) {
                        System.out.println("Client: " + receiveMess);
                    }
                    isRunning = false;
                } catch (IOException e) {
                    logger.error("Loi khi nhan du lieu", e);
                }
            });
            Thread sendThread = new Thread(() -> {
                try {
                    String sentMess;
                    while (isRunning && (sentMess = consoleReader.readLine()) != null) {
                        if (sentMess.equalsIgnoreCase("stop")) {
                            isRunning = false;
                            socket.close(); // Dong luong nhan
                        } else {
                            writer.println(sentMess);
                        }
                    }
                } catch (IOException e) {
                    logger.error("Loi khi gui du lieu", e);
                }
            });
            receiveThread.start();
            sendThread.start();
            //
            receiveThread.join();
            sendThread.join();

            logger.info("Server da tat.");
        } catch (IOException | InterruptedException e) {
            logger.error("Loi trong qua trinh chat", e);
        }
    }
    public void run() {
        getConfig();
        startServer();
    }
    public static void main(String[] args) {
        new Server().run();
    }
}
