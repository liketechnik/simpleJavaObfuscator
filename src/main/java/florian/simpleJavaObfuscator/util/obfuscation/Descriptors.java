package florian.simpleJavaObfuscator.util.obfuscation;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Florian Warzecha
 * @version 1.0
 * @date 15. Januar 2018
 */
public class Descriptors {
    
    public static String getObfuscatedMethodTypeDescriptor(String orig, INameGenerator mappings) {
        String[] parts = orig.split("\\)");
        mappings.getLog().println("Transforming return type");
        parts[1] = getObfuscatedFieldTypeDescriptor(parts[1], mappings); // transform return type
        mappings.getLog().println("Transforming paramters");
    
        List<String> params = new LinkedList<>();
        parts[0] = parts[0].substring(1);
        int numberOfBrackets = 0;
        StringBuilder currentName = new StringBuilder();
        boolean parsing = false;
        for (char cha : parts[0].toCharArray()) {
            if (cha == '[') {
                numberOfBrackets++;
                continue;
            }
            if (parsing) {
                if (cha == ';') {
                    currentName.append(cha);
                    for (int i = 0; i < numberOfBrackets; i++) {
                        currentName.insert(0, '[');
                    }
                    
                    params.add(getObfuscatedFieldTypeDescriptor(currentName.toString(), mappings));
                    
                    numberOfBrackets = 0;
                    parsing = false;
                    currentName = new StringBuilder();
                } else {
                    currentName.append(cha);
                }
            } else {
                if (cha == 'L') {
                    parsing = true;
                    currentName = new StringBuilder().append('L');
                } else if (stringIsNativeTypeDescriptor(String.valueOf(cha))) {
                    currentName = new StringBuilder().append(cha);
                    for (int i = 0; i < numberOfBrackets; i++) {
                        currentName.insert(0, '[');
                    }
                    
                    params.add(currentName.toString());
                    
                    numberOfBrackets = 0;
                } else {
                    mappings.getLog().println("This shouldn't happen! Found char " + cha + " while not parsing a class name!");
                    System.err.println("This shouldn't happen! Found char " + cha + " while not parsing a class name!");
                }
            }
        }
        
        StringBuilder obfuscatedDescriptor = new StringBuilder().append("(");
        for (String entry : params) {
            obfuscatedDescriptor.append(entry);
        }
        obfuscatedDescriptor.append(")").append(parts[1]);
        
        return obfuscatedDescriptor.toString();
    }
    
    public static String getObfuscatedFieldTypeDescriptor(String orig, INameGenerator mappings) {
        if (stringIsNativeTypeDescriptor(orig)) {
            return orig;
        }
        
        String[] parts = orig.split("L", 2);
        int classIndex = 0;
        if (parts[0].contains("[") || parts[0].isEmpty()) {
            classIndex = 1;
        } else {
            parts[0] = parts[0].substring(1);
        }
        parts[classIndex] = "L" + mappings.getClassName(parts[classIndex].substring(0, parts[classIndex].length() - 1)) + ";"; // remove semicolon (last char) [.substring()]
        if (classIndex == 0) { // either class name at index 0
            //System.out.println("Replaced '" + orig + "' with '" + parts[classIndex] + "'.");
            return parts[classIndex];
        } else { // or class name at index 1, array declaration at index 0
           // System.out.println("Replaced '" + orig + "' with '" + parts[0] + parts[classIndex] + "'.");
            return parts[0] + parts[classIndex];
        }
    }
    
    private static boolean stringIsNativeTypeDescriptor(String test) {
        String[] descriptor = new String[]{"Z", "C", "B", "S", "I", "F", "J", "D", "V"};
        test = test.replaceAll("\\[", "");
        for (String prefix : descriptor) {
            if (test.startsWith(prefix) && test.length() == 1) {
                return true;
            }
        }
        return false;
    }
}
