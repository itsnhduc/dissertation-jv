package dissertation.pricing;

import java.util.Date;

public class TimeUtil {
    
    private static Date _start;
    
    public static void start() {
        _start = new Date();
    }
    
    public static void end() {
        System.out.println("Simulation time: " + ((new Date().getTime() - _start.getTime() * 1.0) / 1000) + "s");
    }
    
}
