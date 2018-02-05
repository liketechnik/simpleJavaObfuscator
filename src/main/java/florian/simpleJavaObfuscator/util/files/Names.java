package florian.simpleJavaObfuscator.util.files;

import florian.simpleJavaObfuscator.util.obfuscation.INameGenerator;

import java.io.File;

/**
 * @author Florian Warzecha
 * @version 1.0
 * @date 02 of Februar 2018
 */
public class Names {
    
    /**
     * Builds a new file object for the obfuscated clazz. (the file for the class must have the same name as the class)
     * @param clazz The file object representing the original clazz.
     * @param mappings The mappings between original and obfuscated clazz names.
     * @return A file object representing the obfuscated version of the clazz.
     */
    public static File generateObfFile(File clazz, INameGenerator mappings) {
        String name = clazz.getName();
        String[] parts = name.split("\\."); // first part contains path + file (class) name, second part contains ".class"
        parts[0] = mappings.getClassName(parts[0]);
        return new File(clazz.getPath().replace(name, parts[0] + "." + parts[1]));
    }
}
