package florian.simpleJavaObfuscator.names;


import florian.simpleJavaObfuscator.util.obfuscation.Mappings;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import static florian.simpleJavaObfuscator.util.obfuscation.Descriptors.getObfuscatedFieldTypeDescriptor;
import static florian.simpleJavaObfuscator.util.obfuscation.Descriptors.getObfuscatedMethodTypeDescriptor;
import static org.objectweb.asm.Opcodes.ASM6;

/**
 * @author Florian Warzecha
 * @version 1.0
 * @date 14 of Januar 2018
 */
public class NameChangeAdapter extends ClassVisitor {
    
    Mappings mappings;
    String className;
    
    public NameChangeAdapter(Mappings mappings, ClassVisitor cv) {
        super(ASM6, cv);
        System.out.println("Created name change adapter");
        this.mappings = mappings;
    }
    
    @Override
    public void visit(int version, int access, String name, String signature, String superName,
                      String[] interfaces) {
        this.className = name;
        System.out.println("Changed class " + name + " to " + mappings.getClassName(className));
        cv.visit(version, access, mappings.getClassName(className), signature, superName, interfaces); // superName is also a class name todo
    }
    
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
    
        System.out.println("Changed method " + name + " to " + mappings.getMethodName(name, className, desc));
        
        MethodVisitor mv = cv.visitMethod(access, mappings.getMethodName(name, className, desc), getObfuscatedMethodTypeDescriptor(desc, mappings), signature, exceptions); // exceptions contain class names todo
        if (mv != null) {
            mv = new RenameAdapter(mappings, mv, className);
        }
        return mv;
    }
    
    public FieldVisitor visitField(int access, String name, String desc,
                                   String signature, Object value) {
        System.out.println("Changed field " + name + " to " + mappings.getFieldName(name, className));
        return cv.visitField(access, mappings.getFieldName(name, className), getObfuscatedFieldTypeDescriptor(desc, mappings), signature, value);
    }
}
