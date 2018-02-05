package florian.simpleJavaObfuscator.names;


import florian.simpleJavaObfuscator.util.obfuscation.INameGenerator;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;

import static florian.simpleJavaObfuscator.util.obfuscation.Descriptors.getObfuscatedFieldTypeDescriptor;
import static florian.simpleJavaObfuscator.util.obfuscation.Descriptors.getObfuscatedMethodTypeDescriptor;
import static org.objectweb.asm.Opcodes.ASM6;

/**
 * This class is responsible for applying the changes written into the {@link #mappings} by the
 * {@link NameGeneratorAdapter}.
 *
 * @author Florian Warzecha
 * @version 1.0
 * @date 14. Januar 2018
 * @see NameGeneratorAdapter
 * @see INameGenerator The interface that holds the new names
 * @see RenameAdapter Applies the changes to the methods internal code.
 */
public class NameChangeAdapter extends ClassVisitor {
    
    private INameGenerator mappings;
    private String className;
    
    public NameChangeAdapter(INameGenerator mappings, ClassVisitor cv) {
        super(ASM6, cv);
        mappings.getLog().println("Created name change adapter");
        this.mappings = mappings;
    }
    
    @Override
    public void visit(int version, int access, String name, String signature, String superName,
                      String[] interfaces) {
        this.className = name;
        mappings.getLog().println("Changed class " + name + " to " + mappings.getClassName(className));
        
        for (int i = 0; i < interfaces.length; i++) {
            interfaces[i] = mappings.getClassName(interfaces[i]);
        }
        
        if (superName != null) {
            cv.visit(version, access, mappings.getClassName(className), signature, mappings.getClassName(superName), interfaces);
        } else {
            cv.visit(version, access, mappings.getClassName(className), signature, superName, interfaces);
        }
    }
    
    /**
     * Left empty because we want to remove obvious information.
     * @param source The source file name of this class.
     * @param debug Debug information.
     */
    @Override
    public void visitSource(String source, String debug) {}
    
    @Override
    public void visitOuterClass(String owner, String name, String desc) {
        if (name != null && desc != null) {
            name = mappings.getMethodName(name, owner, desc);
            desc = getObfuscatedMethodTypeDescriptor(desc, mappings);
        }
    
        owner = mappings.getClassName(owner);
        
        cv.visitOuterClass(owner, name, desc);
    }
    
    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        name = mappings.getClassName(name);
        outerName = mappings.getClassName(outerName);
        innerName = mappings.getClassName(innerName);
        
        cv.visitInnerClass(name, outerName, innerName, access);
    }
    
    @Override
    public FieldVisitor visitField(int access, String name, String desc,
                                   String signature, Object value) {
        mappings.getLog().println("Changed field " + name + " to " + mappings.getFieldName(name, className));
        return cv.visitField(access, mappings.getFieldName(name, className), getObfuscatedFieldTypeDescriptor(desc, mappings), signature, value);
    }
    
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc,
                                     String signature, String[] exceptions) {
    
        mappings.getLog().println("Changed method " + name + " to " + mappings.getMethodName(name, className, desc));
        
        if (exceptions != null) {
            for (int i = 0; i < exceptions.length; i++) {
                exceptions[i] = mappings.getClassName(exceptions[i]);
            }
        }
        
        MethodVisitor mv = cv.visitMethod(access, mappings.getMethodName(name, className, desc), getObfuscatedMethodTypeDescriptor(desc, mappings), signature, exceptions);
        if (mv != null) {
            mv = new RenameAdapter(mappings, mv);
        }
        return mv;
    }
}
