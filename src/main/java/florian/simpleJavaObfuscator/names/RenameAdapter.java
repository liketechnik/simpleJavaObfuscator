package florian.simpleJavaObfuscator.names;

import florian.simpleJavaObfuscator.util.obfuscation.Mappings;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.TypePath;

import static florian.simpleJavaObfuscator.util.obfuscation.Descriptors.getObfuscatedFieldTypeDescriptor;
import static florian.simpleJavaObfuscator.util.obfuscation.Descriptors.getObfuscatedMethodTypeDescriptor;
import static org.objectweb.asm.Opcodes.ASM6;

/**
 * @author Florian Warzecha
 * @version 1.0
 * @date 14 of Januar 2018
 */
public class RenameAdapter extends MethodVisitor {
    
    Mappings mappings;
    String className;
    
    public RenameAdapter(Mappings mappings, MethodVisitor mv, String className) {
        super(ASM6, mv);
        System.out.println("created rename adapter");
        this.mappings = mappings;
        this.className = className;
    }
    
    @Override
    public void visitParameter(String name, int access) {
        mv.visitParameter(null, access);
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        String orig = desc;
        desc = getObfuscatedFieldTypeDescriptor(desc, mappings);
        System.out.println("Replaced Annotation '" + orig + "' with '" + desc + "'.");
        return mv.visitAnnotation(desc, visible);
    }
    
    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc,
                                                 boolean visible) {
        String orig = desc;
        desc = getObfuscatedFieldTypeDescriptor(desc, mappings);
        System.out.println("Replaced TypeAnnotation '" + orig + "' with '" + desc + "'.");
        return mv.visitTypeAnnotation(typeRef, typePath, desc, visible);
    }
    
    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
        String orig = desc;
        desc = getObfuscatedFieldTypeDescriptor(desc, mappings);
        System.out.println("Replaced ParamterAnnotation '" + orig + "' with '" + desc + "'.");
        return mv.visitParameterAnnotation(parameter, desc, visible);
    }
    
    @Override
    public void visitTypeInsn(int opcode, String type) {
        String orig = type;
        type = mappings.getClassName(type);
        System.out.println("Replaced typeInsn '" + orig + "' with '" + type + "'.");
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
        
        
        System.out.println("Replaced fieldInsn '" + ownerOr + ", " + nameOr + ", " + descOr + " with " + owner + ", " + name + ", " + desc + ".");
        
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
    
    
        System.out.println("Replaced methodInsn '" + ownerOr + ", " + nameOr + ", " + descOr + " with " + owner + ", " + name + ", " + desc + ".");
    
        mv.visitMethodInsn(opcode, owner, name, desc, itf);
    }
    
    // many methods missing todo
}