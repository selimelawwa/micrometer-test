import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import io.micrometer.core.instrument.LongTaskTimer;
import io.micrometer.core.instrument.LongTaskTimer.Sample;

@Aspect
public class MeasuredAspect {
    @Around("execution(* *(..)) && @annotation(Measured)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        return AppMetrics.getInstance().handle(pjp);
//        LongTaskTimer longTaskTimer = AppMetrics.getInstance().getLongTaskTimer(CountedObject.LONG_TASK_TIMER_NAME);
//        Sample task = longTaskTimer.start();
//        
//        Object proceed = pjp.proceed();
//        
//        double duration = task.duration(TimeUnit.MILLISECONDS);
//        
//        task.stop();
//        
//        Field f = pjp.getThis().getClass().getDeclaredField("id");
//        f.setAccessible(true);
//        
//        System.out.println(pjp.getThis().getClass().getName()+ " with " + f.getName() + " "+ f.get(pjp.getThis()) + " " + pjp.getSignature().getName() + " took: " + duration + " ms");
//        return proceed;
    }
}