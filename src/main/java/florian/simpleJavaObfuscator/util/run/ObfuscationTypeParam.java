package florian.simpleJavaObfuscator.util.run;

import com.github.liketechnik.ArgumentTypes;
import com.github.liketechnik.Parameter;

/**
 * @author Florian Warzecha
 * @version 1.0
 * @date 24. Januar 2018
 */
public class ObfuscationTypeParam extends Parameter {
    
    @Override
    public String getId() {
        return "obfuscationType";
    }
    
    @Override
    public String getName() {
        return "obfuscationType";
    }
    
    @Override
    public String getShortName() {
        return "oT";
    }
    
    @Override
    public Object getDefaultValue() {
        return "file";
    }
    
    @Override
    public ArgumentTypes getType() {
        return ArgumentTypes.STRING;
    }
}
