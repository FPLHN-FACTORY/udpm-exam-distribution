package fplhn.udpm.examdistribution.infrastructure.config.global;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Setter
@Getter
@Component
public class GlobalVariables {

    private Map<String, Object> globalVariable = new HashMap<>();

    public void setGlobalVariable(String key, Object value) {
        globalVariable.put(key, value);
    }

    public Object getGlobalVariable(String key) {
        return globalVariable.get(key);
    }

}