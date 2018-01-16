package florian.simpleJavaObfuscator;

import florian.simpleJavaObfuscator.names.NameChangeAdapter;
import florian.simpleJavaObfuscator.names.NameGeneratorAdapter;
import florian.simpleJavaObfuscator.util.obfuscation.Mappings;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.*;

/**
 * @author Florian Warzecha
 * @version 1.0
 * @date 14 of Januar 2018
 */
public class Run {
    
    public static void main(String[] args) throws IOException {
    
        Mappings mappings = new Mappings();
        
        
        
        for (String file : args) {
            TraceClassVisitor tcv2 = new TraceClassVisitor(new PrintWriter(System.out));
            ClassVisitor generator = new NameGeneratorAdapter(mappings, tcv2);
    
            File f = new File(file);
            byte[] input = new byte[(int) f.length()];
            InputStream in = new FileInputStream(f);
            in.read(input);
            in.close();
            ClassReader cr = new ClassReader(input);
            cr.accept(generator, ClassReader.SKIP_DEBUG);
        }
        for (String file : args) {
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES); // setup transforming chain, doing from bottom to top
            CheckClassAdapter ccv = new CheckClassAdapter(cw); // seems broken
            TraceClassVisitor tcv = new TraceClassVisitor(ccv, new PrintWriter(System.out));
            ClassVisitor changer = new NameChangeAdapter(mappings, tcv);
    
            File f = new File(file);
            byte[] input = new byte[(int) f.length()];
            InputStream in = new FileInputStream(f);
            in.read(input);
            in.close();
            ClassReader cr = new ClassReader(input);
            cr.accept(changer, ClassReader.SKIP_DEBUG);
    
            byte[] output = cw.toByteArray();
            OutputStream out = new FileOutputStream(new File(file + "mod"));
            out.write(output);
            out.close();
        }
    }
}
