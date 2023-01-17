package de.dfki.cos.basys.processcontrol.cctaskmanager.util;

import de.dfki.cos.basys.processcontrol.model.VariableType;

public class VariableTypeHelper {

    public static VariableType fromOpcUa(String type) {
        switch (type) {
            case "Int32":
            case "Int16":
            case "Integer":
                return VariableType.INTEGER;
            case "Int64":
                return VariableType.LONG;
            case "Double":
            case "Float":
                return VariableType.DOUBLE;
            case "String":
                return VariableType.STRING;
            case "Boolean":
                return VariableType.BOOLEAN;
            case "DateTime":
                return VariableType.DATE;

            default:
                return VariableType.NULL;
        }
    }

}
