import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.LongTaskTimer.Sample;
import io.micrometer.core.instrument.Timer;

public class CountedObject {
    public final static String COUNTER_NAME = "objects.instance";
    public final static String LONG_TASK_TIMER_NAME = "app.long.tasks.timer";//TODO check naming conventions
    public final static String TIMER_NAME = "app.timer";
    int id;
    
    public CountedObject(int id) {
        this.id = id;
    }
    
    @Measured
    @Timed(value = "timer1")
    public void measuredFunction() {
        try {
            int sleepTime = new Random().nextInt(3) + 1;
            Thread.sleep(sleepTime * 1000L);
        } catch (InterruptedException e) {}
    }
    
    @Timed(value = "timer2")
    public void timedFunction() {
        try {
            int sleepTime = new Random().nextInt(3) + 1;
            Thread.sleep(sleepTime * 1000L);
        } catch (InterruptedException e) {}
        System.out.println("timedFunction");
    }
    
    public int fastFunction() {
        Timer timer = AppMetrics.getInstance().getTimer(TIMER_NAME);
        
        Supplier<Integer> f = () -> {
            try {
                Thread.sleep(500L);
            } catch (InterruptedException e) {}
            return generateRandomNumber();
        };
        
        return timer.wrap(f).get();
    }
    
    public void timeConsumingFunction() {
        LongTaskTimer longTaskTimer = AppMetrics.getInstance().getLongTaskTimer(LONG_TASK_TIMER_NAME);
        Sample task = longTaskTimer.start();
        
        try {
            int sleepTime = new Random().nextInt(2) + 1;
            Thread.sleep(sleepTime * 1000L);
        } catch (InterruptedException e) {}
        
        System.out.println("timeConsumingFunction() for countedObject-" + id + " took: " + task.duration(TimeUnit.SECONDS));
        task.stop();
        
//        longTaskTimer.duration(TimeUnit.SECONDS);//The cumulative duration of all current tasks.           
    }
    
    private int generateRandomNumber() {
        return new Random().nextInt(5); 
    }
}
