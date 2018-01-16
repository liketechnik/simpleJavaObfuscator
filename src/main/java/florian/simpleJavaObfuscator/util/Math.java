package florian.simpleJavaObfuscator.util;

/**
 * @author Florian Warzecha
 * @version 1.0
 * @date 12 of Januar 2018
 */
public class Math {
    
    public static int sumPows(int base, int maxExp) {
        int result = 0;
        for (int i = 1; i <= maxExp; i++) {
            result += java.lang.Math.pow(base, i);
        }
        return result;
    }
}
