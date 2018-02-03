package florian.simpleJavaObfuscator.run;

import florian.simpleJavaObfuscator.names.NameChangeAdapter;
import florian.simpleJavaObfuscator.names.NameGeneratorAdapter;
import florian.simpleJavaObfuscator.util.obfuscation.DefaultMappings;
import florian.simpleJavaObfuscator.util.obfuscation.INameGenerator;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.*;

import static florian.simpleJavaObfuscator.util.files.Content.getFileContent;
import static florian.simpleJavaObfuscator.util.files.Content.writeFileContent;
import static florian.simpleJavaObfuscator.util.files.Names.generateObfFile;

/**
 * @author Florian Warzecha
 * @version 1.0
 * @date 24 of Januar 2018
 */
public class SimpleFile {
    
    INameGenerator mappings;
    
    public static void run(String files[]) {
        INameGenerator mappings = new DefaultMappings();
        SimpleFile simpleFile = new SimpleFile(mappings);
        
        for (String file : files) {
            File clazz = new File(file);
            File trace = new File(file.replace(".", "_") + "_origTrace.txt");
            
            if (clazz.exists()) {
                try {
                    simpleFile.generateMappings(clazz, trace);
                } catch (IOException e) {
                    System.err.println("Could not obfuscate class " + file + " because IO errors occured. Printing trace:");
                    e.printStackTrace();
                }
            } else {
                System.err.println("Can't obfuscate class " + file + " because it does not exist or trace is cannot be written.");
            }
        }
        
        for (String file : files) {
            File clazz = new File(file);
            File obfClazz = generateObfFile(clazz, mappings);
            File trace = new File(file.replace(".", "_") + "_obfTrace.txt");
            File log = new File(file.replace(".", "_") + "_obfLog.txt");
            
            try {
                PrintWriter printWriter = new PrintWriter(log);
                mappings.setLog(printWriter);
                
                simpleFile.applyMappings(clazz, obfClazz, trace);
                
                printWriter.close();
            } catch (IOException e) {
                System.err.println("Could not obfuscate class " + file + " because IO errors occured. Printing trace:");
                e.printStackTrace();
            }
        }
    }
    
    public SimpleFile(INameGenerator mappings) {
        this.mappings = mappings;
    }
    
    public void generateMappings(File clazz, File trace) throws IOException {
        TraceClassVisitor tcv = new TraceClassVisitor(new PrintWriter(trace));
        ClassVisitor generator = new NameGeneratorAdapter(mappings, tcv);
        
        ClassReader cr = new ClassReader(getFileContent(clazz));
        cr.accept(generator, ClassReader.SKIP_DEBUG);
    }
    
    public void applyMappings(File origClazz, File obfClazz, File trace) throws IOException {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        CheckClassAdapter ccv = new CheckClassAdapter(cw);
        TraceClassVisitor tcv = new TraceClassVisitor(ccv, new PrintWriter(trace));
        ClassVisitor changer  = new NameChangeAdapter(mappings, tcv);
        
        ClassReader cr = new ClassReader(getFileContent(origClazz));
        cr.accept(changer, ClassReader.SKIP_FRAMES);
        
        writeFileContent(obfClazz, cw.toByteArray());
    }
}
