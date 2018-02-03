package florian.simpleJavaObfuscator.util.obfuscation;

import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Hashtable;

/**
 * @author Florian Warzecha
 * @version 1.0
 * @date 11. Januar 2018
 */
public class DefaultMappings implements INameGenerator {
    
    private PrintWriter printWriter;
    
    private Hashtable<String, String> obfuscatedClassNames = new Hashtable<>();
    private Hashtable<String, Hashtable<String, String>> obfuscatedMethodNames = new Hashtable<>();
    private Hashtable<String, String> obfuscatedFieldNames = new Hashtable<>();
    
    private HashSet<String> methodDescs = new HashSet<>();
    
    private char[] classLetters;
    private Hashtable<String, char[]> methodLetters;
    private char[] fieldLetters;
    
    private int maxNumberOfAutoOverloading = 3;
    
    public DefaultMappings() {
        classLetters = new char[]{'a'};
        methodLetters = new Hashtable<>();
        fieldLetters = new char[]{'a'};
        
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
        // grouping methods by descriptor, only methods with new descriptor need new name
        if (!methodDescs.contains(descriptor)) {
            obfuscatedMethodNames.put(descriptor, new Hashtable<>());
            
            int additionalOffset = methodDescs.size() / maxNumberOfAutoOverloading; // start new names for methods with a new descriptor with another letter after certain number of overloads
            char[] methodLetters = new char[]{'a'};
            for (int i = 0; i < additionalOffset; i++) {
                methodLetters = generateNextName(methodLetters);
            }
            this.methodLetters.put(descriptor, methodLetters);
            
            methodDescs.add(descriptor);
        }
        
        String result = this.toString(methodLetters.get(descriptor));
        obfuscatedMethodNames.get(descriptor).put(className + "#" + orig, result);
        methodLetters.put(descriptor, this.generateNextName(methodLetters.get(descriptor)));
    }
    
    public String getMethodName(String orig, String className, String descriptor) {
        if (obfuscatedMethodNames.containsKey(descriptor) && obfuscatedMethodNames.get(descriptor).containsKey(className + "#" + orig)) {
            return obfuscatedMethodNames.get(descriptor).get(className + "#" + orig);
        }
        return orig;
    }
    
    @Override
    public void createFieldName(String orig, String className) {
        String result = this.toString(fieldLetters);
        obfuscatedFieldNames.put(className + "#" + orig, result);
        fieldLetters = this.generateNextName(fieldLetters);
    }
    
    public String getFieldName(String orig, String className) {
        if (obfuscatedFieldNames.containsKey(className + "#" + orig)) {
            return obfuscatedFieldNames.get(className + "#" + orig);
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
