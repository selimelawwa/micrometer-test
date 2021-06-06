import java.util.ArrayList;
import java.util.List;

public class App {

    public static void main(String[] args) {
        AppMetrics appMetrics = AppMetrics.getInstance();
        appMetrics.getOrCreateCounter(CountedObject.COUNTER_NAME, "description", "dev", "performance");
        
        List<CountedObject> objs = appMetrics.gaugeCollectionSize("gauge1", new ArrayList<CountedObject>());
        
//        System.out.println("Before creating any objects");
//        appMetrics.printCounterInfo(CountedObject.COUNTER_NAME);
        
        objs.add(new CountedObject(1));
//        System.out.println("After creating 1 CountedObject");
//        appMetrics.printCounterInfo(CountedObject.COUNTER_NAME);        
        
        objs.add(new CountedObject(2));
//        System.out.println("After creating 2 CountedObjects");
//        appMetrics.printCounterInfo(CountedObject.COUNTER_NAME);
        
        objs.add(new CountedObject(3));
        
        for(int i=0; i<objs.size(); i++){
            CountedObject obj = objs.get(i);
            new Thread("" + i){
              public void run(){
//                  obj.timeConsumingFunction();
//                  obj.fastFunction();
                  obj.measuredFunction();
//                  obj.timedFunction();
              }
            }.start();
         }        
        
        try { Thread.sleep(7000L);} catch (InterruptedException e) {}
        
        System.out.println("\n");
        appMetrics.printAllMeters();
    }
}
