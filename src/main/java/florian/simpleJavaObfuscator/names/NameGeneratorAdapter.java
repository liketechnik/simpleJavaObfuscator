package florian.simpleJavaObfuscator.names;

import com.github.liketechnik.ArgumentTypes;
import florian.simpleJavaObfuscator.util.obfuscation.Mappings;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM6;

/**
 * @author Florian Warzecha
 * @version 1.0
 * @date 11. Januar 2018
 */
public class NameGeneratorAdapter extends ClassVisitor {
    
    Mappings mappings;
    String className;
    
    public NameGeneratorAdapter(Mappings mappings, ClassVisitor cv) {
        super(ASM6, cv);
        this.mappings = mappings;
    }
    
    @Override
    public void visit(int version, int access, String name, String signature, String superName,
                      String[] interfaces) {
        this.className = name;
        mappings.createClassName(name);
        cv.visit(version, access, name, signature, superName, interfaces);
    }
    
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
        if (!name.equals("<init>") && !name.equals("main")) {
            mappings.createMethodName(name, className, desc);
        }
        return cv.visitMethod(access,name, desc, signature, exceptions);
    }
    
    public FieldVisitor visitField(int access, String name, String desc,
                                   String signature, Object value) {
        
        mappings.createFieldName(name, className);
        return cv.visitField(access, name, desc, signature, value);
    }
    
    public static void main(String[] args) {
        char test = 65;
        System.out.println(test);
        System.out.println(1 / 3);
        System.out.println(2 / 3);
        System.out.println(3  / 3);
    }
}
