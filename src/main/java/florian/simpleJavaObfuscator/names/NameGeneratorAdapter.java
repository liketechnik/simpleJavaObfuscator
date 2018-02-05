package florian.simpleJavaObfuscator.names;

import florian.simpleJavaObfuscator.util.obfuscation.INameGenerator;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM6;

/**
 * This class is responsible for generating new names for classes and their methods + fields.
 * These names are generated and saved by the {@link #mappings} and applied by the {@link NameChangeAdapter}
 *
 * @author Florian Warzecha
 * @version 1.0
 * @date 11. Januar 2018
 * @see NameChangeAdapter
 * @see INameGenerator
 */
public class NameGeneratorAdapter extends ClassVisitor {
    
    private INameGenerator mappings;
    private String className;
    
    public NameGeneratorAdapter(INameGenerator mappings, ClassVisitor cv) {
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
        if (!name.equals("<init>") && !name.equals("main")) { // exclude methods whose name must not be changed
            mappings.createMethodName(name, className, desc);
        }
        return cv.visitMethod(access,name, desc, signature, exceptions);
    }
    
    public FieldVisitor visitField(int access, String name, String desc,
                                   String signature, Object value) {
        
        mappings.createFieldName(name, className);
        return cv.visitField(access, name, desc, signature, value);
    }
}
