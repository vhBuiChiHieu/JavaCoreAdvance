package bt2;

import jdk.jfr.Description;
import common.ConfigUtil;
import org.apache.log4j.Logger;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Viết 1 chương trình chạy real-time, cứ n giây in 1 số nguyên random ra mà hình.
 * Dừng chương trình sau n phút.
 */
public class Bt2 {
    private static final Logger logger = Logger.getLogger(Bt2.class);
    private int delay;
    private int maxTime;

    @Description("lấy giá trị khoảng cách giữa 2 lần random và thời gian tối đa")
    private void getConfig(){
        delay = Integer.parseInt(ConfigUtil.get("delay"));
        maxTime= Integer.parseInt(ConfigUtil.get("maxTime"));
    }

    @Description("Dùng timer tạo số random mỗi {delay} giây và dừng sau {maxTime} phút")
    private void randomNumberPrint(){
        Random random = new Random();
        //task tao so random va in
        int[] i = {1};
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                int randomNumber = random.nextInt(100);
                System.out.println("Random Number " + i[0] + ": " + randomNumber);
                i[0]++;
            }
        };
        Timer mainTimer = new Timer();
        //chay mainTimer
        logger.info("Bat dau in ra man hinh...");
        mainTimer.scheduleAtFixedRate(timerTask,0,delay * 1000L);
        Timer stopTimer = new Timer();
        stopTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                logger.info("Chuong trinh ket thuc.");
                mainTimer.cancel();
                stopTimer.cancel();
            }
        }, maxTime * 60000L);

    }
    public void run(){
        getConfig();
        randomNumberPrint();
    }

    public static void main(String[] args) {
        new Bt2().run();
    }
}
