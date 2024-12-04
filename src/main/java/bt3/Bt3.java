package bt3;

import common.ConfigUtil;
import jdk.jfr.Description;
import org.apache.log4j.Logger;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Viết chương trình giải quyết bài toán producer & consumer với các yêu cầu sau:
 * – Có một message queue chứa các message, cấu trúc message là tùy chọn. Message queue có size giới hạn.
 * – Một thread đóng vai trò producer: producer định kỳ sẽ tạo ra một message và đưa vào message queue.
 *   Nếu msgq đã full thì thread sẽ phải đợi cho tới khi msgq không ở trong trạng thái full và tiếp tục tạo message mới đưa vào queue.
 * – Một thread đóng vai trò consumer: mỗi lần sẽ lấy ra 1 message từ message queue và in message ra màn hình.
 *   Nếu message queue đang empty thì sẽ phải đợi cho tới khi có message trong queue để xử lý.
 */

public class Bt3 {
    private final BlockingQueue<String > messQueue;
    private static final Logger logger = Logger.getLogger(Bt3.class);
    private int maxQueue;
    private long produceDelay, consumeDelay;
    public Bt3(){
        getConfig();
        messQueue = new ArrayBlockingQueue<>(maxQueue);
    }
    @Description("Lay tham so tu config")
    private void getConfig(){
        try {
            maxQueue = Integer.parseInt(ConfigUtil.get("maxQueue"));
            produceDelay = Long.parseLong(ConfigUtil.get("produceDelay"));
            consumeDelay = Long.parseLong(ConfigUtil.get("consumeDelay"));
        } catch (NumberFormatException e){
            logger.error("Loi dinh dang cau hinh", e);
        }
    }

    public BlockingQueue<String> getMessQueue() {
        return messQueue;
    }

    public long getProduceDelay() {
        return produceDelay;
    }

    public long getConsumeDelay() {
        return consumeDelay;
    }

    public static void main(String[] args) {
        Bt3 bt3 = new Bt3();
        Producer producer = new Producer(bt3);
        Consumer consumer = new Consumer(bt3);
        logger.info("Chuong trinh bat dau...");
        producer.start();
        consumer.start();
    }
}
