package biz.poolparty.rya.tld;

/**
 *
 * @author turnguard
 */
public class Utils {
    public static boolean isInstanceof(Object o, Class c){
        System.out.println("o:"+o);
        System.out.println("c:"+c);
        return c.isInstance(o);        
    }
}
