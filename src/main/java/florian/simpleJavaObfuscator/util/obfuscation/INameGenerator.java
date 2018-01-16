package florian.simpleJavaObfuscator.util.obfuscation;

/**
 * @author Florian Warzecha
 * @version 1.0
 * @date 12 of Januar 2018
 */
public interface INameGenerator {
    
    public void createClassName(String orig);
    public void createMethodName(String orig, String className, String descriptor);
    public void createFieldName(String orig, String className);
    
}
