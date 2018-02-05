package florian.simpleJavaObfuscator.util.obfuscation;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Hashtable;

/**
 * An implementation of the {@link INameGenerator} interface, providing
 * non random names for classes, methods and fields.
 *
 * The first name for classes, methods and fields is for each a, then b, c and so on.
 * When 26 names have been used, it adds one character, eg. aa, ab, ac...
 *
 * Performs auto-overloading of methods. How many methods get the same name
 * can be controlled via {@link #maxNumberOfAutoOverloading}. By default its value is 3.
 *
 * @author Florian Warzecha
 * @version 1.0
 * @date 11. Januar 2018
 */
public class DefaultMappings implements INameGenerator {
    
    private PrintWriter printWriter;
    
    private Hashtable<String, String> obfuscatedClassNames = new Hashtable<>();
    private Hashtable<String, Hashtable<String, Hashtable<String, String>>> obfuscatedMethodNames = new Hashtable<>(); // class, desc, name
    private Hashtable<String, Hashtable<String, String>> obfuscatedFieldNames = new Hashtable<>(); // class, name
    
    private Hashtable<String, HashSet<String>> methodDescs = new Hashtable<>(); // class
    
    private char[] classLetters;
    private Hashtable<String, Hashtable<String, char[]>> methodLetters; // class, desc
    private Hashtable<String, char[]> fieldLetters; // class
    
    private int maxNumberOfAutoOverloading = 3;
    
    public DefaultMappings() {
        classLetters = new char[]{'a'};
        methodLetters = new Hashtable<>();
        fieldLetters = new Hashtable<>();
        
        printWriter = new PrintWriter(System.out);
    }
    
    public DefaultMappings(int maxNumberOfAutoOverloading) {
        this();
        this.maxNumberOfAutoOverloading = maxNumberOfAutoOverloading;
    }
    
    @Override
    public PrintWriter getLog() {
        return printWriter;
    }
    
    @Override
    public void setLog(PrintWriter printWriter) {
        if (printWriter != null) {
            this.printWriter = printWriter;
        }
    }
    
    /**
     *
     * @param orig The fully qualified class name.
     * @return
     */
    @Override
    public void createClassName(String orig) {
        String result;
        if (orig.contains("/")) {
            String[] origArr = new StringBuilder(orig).reverse().toString().split("/", 2); // split package and name
            for (int i = 0; i < 2; i++) {
                origArr[i] = new StringBuilder(origArr[i]).reverse().toString();
            }
    
            result = origArr[1] + "/" + this.toString(this.classLetters);
        } else {
            result = this.toString(this.classLetters);
        }
        
        obfuscatedClassNames.put(orig, result);
        classLetters = this.generateNextName(classLetters);
    }
    
    @Override
    public String getClassName(String orig) {
        if (obfuscatedClassNames.containsKey(orig)) {
            return obfuscatedClassNames.get(orig);
        }
        return orig;
    }
    
    @Override
    public void createMethodName(String orig, String className, String descriptor) {
        descriptor = descriptor.split("\\)")[0]; // only when parameters are different
        
        // if this is the first field from the class, initialize
        if (!methodDescs.containsKey(className)) {
            methodDescs.put(className, new HashSet<>());
            obfuscatedMethodNames.put(className, new Hashtable<>());
            methodLetters.put(className, new Hashtable<>());
        }
        // replace global ones with local ones that dont contain all classes
        HashSet<String> methodDescs = this.methodDescs.get(className);
        Hashtable<String, Hashtable<String, String>> obfuscatedMethodNames = this.obfuscatedMethodNames.get(className); // desc, name
        Hashtable<String, char[]> methodLetters = this.methodLetters.get(className); // desc
        
        // grouping methods by descriptor, only methods with new descriptor from need new name
        if (!methodDescs.contains(descriptor)) {
            // initializing letter array and new hashables
            obfuscatedMethodNames.put(descriptor, new Hashtable<>());
            
            int additionalOffset = methodDescs.size() / maxNumberOfAutoOverloading; // start new names for methods with a new descriptor with another letter after certain number of overloads
            char[] methodLettersChar = new char[]{'a'};
            for (int i = 0; i < additionalOffset; i++) {
                methodLettersChar = generateNextName(methodLettersChar);
            }
            methodLetters.put(descriptor, methodLettersChar);
            
            methodDescs.add(descriptor);
        }
        
        String result = this.toString(methodLetters.get(descriptor));
        obfuscatedMethodNames.get(descriptor).put(orig, result);
        methodLetters.put(descriptor, this.generateNextName(methodLetters.get(descriptor)));
    }
    
    public String getMethodName(String orig, String className, String descriptor) {
        descriptor = descriptor.split("\\)")[0];
        if (obfuscatedMethodNames.containsKey(className) && // check if methods from class are obfuscated
                    obfuscatedMethodNames.get(className).containsKey(descriptor) && // check if desc is obfuscated
                        obfuscatedMethodNames.get(className).get(descriptor).containsKey(orig)) { // check if method is obfuscated
            return obfuscatedMethodNames.get(className).get(descriptor).get(orig);
        }
        return orig;
    }
    
    @Override
    public void createFieldName(String orig, String className) {
        // if this is the first field from the class, initialize
        if (!fieldLetters.containsKey(className)) {
            fieldLetters.put(className, new char[]{'a'});
            obfuscatedFieldNames.put(className, new Hashtable<>());
        }
        // retrieve letters and hashtable for current class
        char[] fieldLetters = this.fieldLetters.get(className);
        Hashtable<String, String> obfuscatedFieldNames = this.obfuscatedFieldNames.get(className);
        
        // store obfuscated name and generate next one
        String result = this.toString(fieldLetters);
        obfuscatedFieldNames.put(orig, result);
        this.fieldLetters.put(className, generateNextName(fieldLetters));
    }
    
    public String getFieldName(String orig, String className) {
        if (obfuscatedFieldNames.containsKey(className) && // check if fields for this class are obfuscated
                    obfuscatedFieldNames.get(className).containsKey(orig)) { // check if this field is obfuscated
            return obfuscatedFieldNames.get(className).get(orig); // return obfuscated name
        }
        return orig;
    }
    
    private char[] generateNextName(char[] letters) {
        if (letters[letters.length - 1] < 'z') {
            letters[letters.length - 1] += 1;
        } else {
            letters[letters.length - 1] = 'a';
            for (int i = letters.length - 2; i > -1; i--) {
                if (letters[i] < 'z') {
                    letters[i]++;
                    break;
                } else {
                    letters[i] = 'a';
                }
                if (i == 0) {
                    letters = new char[letters.length + 1];
                    for (int x = 0; x < letters.length; x++) {
                        letters[x] = 'a';
                    }
                }
            }
        }
        
        return letters;
    }
    
    private String toString(char[] letters) {
        StringBuilder name = new StringBuilder();
        for (char letter : letters) {
            name.append(letter);
        }
        return name.toString();
    }
}
