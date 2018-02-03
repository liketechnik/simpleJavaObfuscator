package florian.simpleJavaObfuscator.util.files;

import florian.simpleJavaObfuscator.util.obfuscation.INameGenerator;

import java.io.File;

/**
 * @author Florian Warzecha
 * @version 1.0
 * @date 02 of Februar 2018
 */
public class Names {
    
    public static File generateObfFile(File clazz, INameGenerator mappings) {
        String name = clazz.getName();
        String[] parts = name.split("\\.");
        parts[0] = mappings.getClassName(parts[0]);
        return new File(clazz.getPath().replace(name, parts[0] + "." + parts[1]));
    }
}
