package bt3;

import jdk.jfr.Description;
import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class Producer extends Thread{
    private final BlockingQueue<String > messQueue;
    private final Random random;
    private final long produceDelay;
    public  Producer(Bt3 bt3){
        messQueue = bt3.getMessQueue();
        produceDelay = bt3.getProduceDelay();
        random = new Random();
    }
    public String getRandomString(){
        return random.nextInt(100) + "";
    }
    @Description("dua 1 mess vao queue moi {produceDelay} thoi gian")
    @Override
    public void run() {
        while (true){
            String mess = getRandomString();
            try {
                //gia dinh tao mess ton produceDelay thoi gian
                Thread.sleep(produceDelay);
                messQueue.put(mess);    //block neu queue day
                System.out.println("Producer put: " + mess + ". " + messQueue);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
