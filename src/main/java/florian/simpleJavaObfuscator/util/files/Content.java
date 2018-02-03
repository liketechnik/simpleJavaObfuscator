package florian.simpleJavaObfuscator.util.files;

import java.io.*;

/**
 * @author Florian Warzecha
 * @version 1.0
 * @date 02 of Februar 2018
 */
public class Content {
    
    public static byte[] getFileContent(File file) throws IOException {
        byte[] input = new byte[(int) file.length()];
        InputStream in = new FileInputStream(file);
        in.read(input);
        in.close();
        return input;
    }
    
    public static void writeFileContent(File file, byte[] output) throws IOException {
        OutputStream out = new FileOutputStream(file);
        out.write(output);
        out.close();
    }
}
