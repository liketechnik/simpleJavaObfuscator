package florian.simpleJavaObfuscator.run;

import com.github.liketechnik.Parameter;
import com.github.liketechnik.Parser;
import florian.simpleJavaObfuscator.util.run.ObfuscationTypeParam;

import java.io.*;
import java.util.Arrays;

/**
 * @author Florian Warzecha
 * @version 1.0
 * @date 14 of Januar 2018
 */
public class Run {
    
    public static void main(String[] args) throws IOException {
    
        Parameter[] params = new Parameter[]{new ObfuscationTypeParam()};
        
        Parser parser = new Parser(args, params);
        
        String obfuscationType = parser.getStringArgumentValue(new ObfuscationTypeParam().getId());
        
        if (!obfuscationType.equals("jar") && !obfuscationType.equals("file") && !obfuscationType.equals("dir")) {
            System.err.println("Invalid obfuscation type. You can obfuscate Jar Archives (jar), " +
                                       "any number of files in your working directory (file), or all " +
                                       "files inside a directory in your working directory!");
            System.exit(1);
        }
        if (obfuscationType.equals("file")) {
            SimpleFile.run(Arrays.copyOfRange(args, 1, args.length));
        } else {
            System.err.println("Currently not implemented! Please extract manually and apply to the single files.");
        }
    }
}
