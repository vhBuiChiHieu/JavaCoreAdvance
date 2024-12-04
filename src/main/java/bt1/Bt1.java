package bt1;

import jdk.jfr.Description;
import org.apache.log4j.Logger;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

/**
 * Viết 1 chương trình chạy real-time, ghi các số nguyên random ra file output.txt.
 * Dừng chương trình khi gõ lệnh stop trên cửa sổ chương trình.
 */
public class Bt1 {
    private static final Logger logger = Logger.getLogger(Bt1.class);
    private static volatile boolean isRunning = true;

    @Description("Tạo số ngẫu nhiên và khi vào output.txt mỗi 1s")
    private void writeNumbersToFile(){
        try (FileWriter writer = new FileWriter("./src/main/java/bt1/output.txt")) {
            Random random = new Random();
            int number;
            //ghi số vào file mỗi 1s nếu isRunning = true
            while (isRunning){
                number = random.nextInt(100);
                writer.write(number + "\n");
                writer.flush(); //ghi ngay từ bộ đệm
                Thread.sleep(1000);
            }
            logger.info("Ghi file hoan tat.");
        } catch (IOException | InterruptedException e){
            logger.error("Loi ", e);
        }
    }

    @Description("Kiểm tra lệnh stop từ console")
    private void readConsole(){
        Scanner scanner = new Scanner(System.in);
        while (isRunning){
            if (scanner.nextLine().equalsIgnoreCase("stop")){
                isRunning = false;
            }
        }
    }

    @Description("Sử dụng 2 luồng để thực hiện đọc và ghi, thông báo khi kết thúc")
    public void run() {
        Thread writeThread = new Thread(this::writeNumbersToFile);
        Thread readThread = new Thread(this::readConsole);
        long startTime = System.currentTimeMillis();
        writeThread.start();
        readThread.start();
        logger.info("Bat dau ghi file....");
        //đợi 2 luồng đọc ghi hoàn tất
        try {
            writeThread.join();
            readThread.join();
        } catch (InterruptedException e){
            logger.error("Luong bi gian doan", e);
        }
        logger.info("Chuong trinh da ket thuc sau " + (System.currentTimeMillis() - startTime) + " ms.");
    }
    public static void main(String[] args) {
        new Bt1().run();
    }
}
