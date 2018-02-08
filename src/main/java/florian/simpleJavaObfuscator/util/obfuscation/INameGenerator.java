package florian.simpleJavaObfuscator.util.obfuscation;

import java.io.PrintWriter;

/**
 * Interface for name generators used by the name obfuscation classes.
 *
 * @author Florian Warzecha
 * @version 1.0
 * @date 12. Januar 2018
 */
public interface INameGenerator {
    void createClassName(String orig);
    String getClassName(String orig);
    
    void createMethodName(String orig, String className, String descriptor);
    String getMethodName(String orig, String className, String descriptor);
    
    void createFieldName(String orig, String className);
    String getFieldName(String orig, String className);
    
    PrintWriter getLog();
    void setLog(PrintWriter printWriter);
}
