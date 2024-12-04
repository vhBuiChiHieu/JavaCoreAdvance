package bt3;

import jdk.jfr.Description;

import java.util.Random;
import java.util.concurrent.BlockingQueue;

public class Consumer extends Thread{
    private final BlockingQueue<String > messQueue;
    private final long consumeDelay;
    public  Consumer(Bt3 bt3){
        messQueue = bt3.getMessQueue();
        consumeDelay = bt3.getConsumeDelay();
    }
    @Description("Lay mess tu queue ra sau moi {consumeDelay} thoi gian")
    @Override
    public void run() {
        while (true){
            try {
                Thread.sleep(consumeDelay);
                String mess = messQueue.take(); //block neu queue trong
                System.out.println("Consumer take: " + mess + ". " + messQueue);
            } catch (InterruptedException e) {
                break;
            }
        }
    }
}
