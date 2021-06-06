import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Meter.Id;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.distribution.CountAtBucket;
import io.micrometer.core.instrument.distribution.HistogramSnapshot;
import io.micrometer.core.instrument.distribution.ValueAtPercentile;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

public class AppMetrics {
    private static final AppMetrics instance = new AppMetrics();
    
    private MeterRegistry registry;
    private TimedAspect timedAspect;
    
    public static AppMetrics getInstance() {
        return instance;
    }
    
    private AppMetrics() {
        this.registry = new SimpleMeterRegistry();
        this.timedAspect = new TimedAspect(registry);
    }
    
    public Object handle(ProceedingJoinPoint pjp) throws Throwable {
        return timedAspect.timedMethod(pjp);
    }
    
    public void incrementCounter(String counterName) {
        registry.find(counterName).counter().increment();//can throw null pointer exception
    }
    
    public Counter getOrCreateCounter(String name, String description, String ... tags) {
        return Counter.builder(name)
                      .description(description)
                      .tags(tags)
                      .register(registry);
    }
    
    public void printCounterInfo(String name) {
        Counter counter = registry.find(name)
                                  .counter();
        
        if (counter != null) {
            Id counterId = counter.getId();
            
            StringBuilder str = new StringBuilder();
            
            String counterInfo =  str.append("Counter name: ")
                                     .append(counterId.getName())
                                     .append(" - Description: ")
                                     .append(counterId.getDescription())
                                     .append(" - tags: ")
                                     .append(counterId.getTags())
                                     .append(" - count: ")
                                     .append(counter.count())
                                     .append("\n")
                                     .toString();
            
            System.out.println(counterInfo);
        } else {
            System.out.println("Could not find counter with name: [" + name + "]");
        }
    }
        
    public LongTaskTimer getLongTaskTimer(String name) {
        return registry.more().longTaskTimer(name, Tags.empty());
    }
    
    public Timer getTimer(String name) {
        return Timer.builder(name)
                    .tags(Tags.empty())
                    .publishPercentiles(0.5)
                    .register(registry);
//        return registry.timer(name, Tags.empty());
    }
    
    public <T extends Collection<?>> T gaugeCollectionSize(String name, T collection) {
        return registry.gaugeCollectionSize(name, Tags.empty(), collection);
    }
    
    public void printAllMeters() {
        List<Meter> meters = registry.getMeters();
        
        for (Meter meter : meters) {
            System.out.println(meter.getId());
            for (Measurement measurement : meter.measure()) {
                System.out.println(measurement.getStatistic() + " : " + measurement.getValue());
            }
            System.out.println("\n");
        }
        
        HistogramSnapshot snapShot = getTimer("app.timer").takeSnapshot();
        
        System.out.println(snapShot.mean(TimeUnit.SECONDS));
        System.out.println(snapShot.count());
        
        for (ValueAtPercentile p : snapShot.percentileValues()) {
            System.out.println("(" + p.value(TimeUnit.SECONDS) + " at " + p.percentile() * 100 + "%)");
        }
        
    }
}
