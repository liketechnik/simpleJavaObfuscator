package florian.simpleJavaObfuscator.util.run;

import com.github.liketechnik.ArgumentTypes;
import com.github.liketechnik.Parameter;
import org.jetbrains.annotations.NotNull;

/**
 * @author Florian Warzecha
 * @version 1.0
 * @date 24. Januar 2018
 */
public class ObfuscationTypeParam extends Parameter {
    
    @NotNull
    @Override
    public String getId() {
        return "obfuscationType";
    }
    
    @NotNull
    @Override
    public String getName() {
        return "obfuscationType";
    }
    
    @NotNull
    @Override
    public String getShortName() {
        return "oT";
    }
    
    @NotNull
    @Override
    public Object getDefaultValue() {
        return "file";
    }
    
    @NotNull
    @Override
    public ArgumentTypes getType() {
        return ArgumentTypes.STRING;
    }
}
