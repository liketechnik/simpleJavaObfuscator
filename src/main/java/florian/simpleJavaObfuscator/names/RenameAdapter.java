package florian.simpleJavaObfuscator.names;

import florian.simpleJavaObfuscator.util.obfuscation.INameGenerator;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;

import static florian.simpleJavaObfuscator.util.obfuscation.Descriptors.getObfuscatedFieldTypeDescriptor;
import static florian.simpleJavaObfuscator.util.obfuscation.Descriptors.getObfuscatedMethodTypeDescriptor;
import static org.objectweb.asm.Opcodes.ASM6;

/**
 * This class is responsible for applying the changes inside the bytecode of methods. The new names
 * are hold inside {@link #mappings}.
 *
 * @author Florian Warzecha
 * @version 1.0
 * @date 14. Januar 2018
 *
 * @see NameChangeAdapter Calls this class when needed
 */
public class RenameAdapter extends MethodVisitor {
    
    private INameGenerator mappings;
    
    public RenameAdapter(INameGenerator mappings, MethodVisitor mv) {
        super(ASM6, mv);
        mappings.getLog().println("created rename adapter");
        this.mappings = mappings;
    }
    
    @Override
    public void visitParameter(String name, int access) {
        mv.visitParameter(null, access); // parameter names can be simply deleted, not needed inside bytecode
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        String orig = desc;
        desc = getObfuscatedFieldTypeDescriptor(desc, mappings);
        mappings.getLog().println("Replaced Annotation '" + orig + "' with '" + desc + "'.");
        return mv.visitAnnotation(desc, visible);
    }
    
    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc,
                                                 boolean visible) {
        String orig = desc;
        desc = getObfuscatedFieldTypeDescriptor(desc, mappings);
        mappings.getLog().println("Replaced TypeAnnotation '" + orig + "' with '" + desc + "'.");
        return mv.visitTypeAnnotation(typeRef, typePath, desc, visible);
    }
    
    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
        String orig = desc;
        desc = getObfuscatedFieldTypeDescriptor(desc, mappings);
        mappings.getLog().println("Replaced ParamterAnnotation '" + orig + "' with '" + desc + "'.");
        return mv.visitParameterAnnotation(parameter, desc, visible);
    }
    
    @Override
    public void visitTypeInsn(int opcode, String type) {
        String orig = type;
        type = mappings.getClassName(type);
        mappings.getLog().println("Replaced typeInsn '" + orig + "' with '" + type + "'.");
        mv.visitTypeInsn(opcode, type);
    }
    
    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        
        String ownerOr = owner;
        String nameOr = name;
        String descOr = desc;
        
        owner = mappings.getClassName(owner);
        name = mappings.getFieldName(name, ownerOr);
        desc = getObfuscatedFieldTypeDescriptor(desc, mappings);
        
        
        mappings.getLog().println("Replaced fieldInsn '" + ownerOr + ", " + nameOr + ", " + descOr + " with " + owner + ", " + name + ", " + desc + ".");
        
        mv.visitFieldInsn(opcode, owner, name, desc);
    }
    
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        String ownerOr = owner;
        String nameOr = name;
        String descOr = desc;
    
        owner = mappings.getClassName(owner);
        name = mappings.getMethodName(name, ownerOr, desc);
        desc = getObfuscatedMethodTypeDescriptor(desc, mappings);
    
    
        mappings.getLog().println("Replaced methodInsn '" + ownerOr + ", " + nameOr + ", " + descOr + " with " + owner + ", " + name + ", " + desc + ".");
    
        mv.visitMethodInsn(opcode, owner, name, desc, itf);
    }
    
    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        mappings.getLog().println("Visiting (and removing) local variable: " + name + " " + desc + " " + signature + " " + start + " " + end + " " + index);
    }
    
    /**
     * Doing nothing because we want to remove as much unneeded information as possible.
     * @param line The line number
     * @param start The label
     */
    @Override
    public void visitLineNumber(int line, Label start) {}
}
