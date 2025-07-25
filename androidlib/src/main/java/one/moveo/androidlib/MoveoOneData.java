package one.moveo.androidlib;


import java.util.Map;



public class MoveoOneData {
    private String semanticGroup;
    private String id;
    private Constants.MoveoOneType type;
    private Constants.MoveoOneAction action;
    private Object value;
    private Map<String, String> metadata;

    public MoveoOneData(String semanticGroup, String id, Constants.MoveoOneType type, Constants.MoveoOneAction action, Object value, Map<String, String> metadata) {
        this.semanticGroup = semanticGroup;
        this.id = id;
        this.type = type;
        this.action = action;
        this.value = value;
        this.metadata = metadata;
    }

    public String getSemanticGroup() {
        return semanticGroup;
    }

    public void setSemanticGroup(String semanticGroup) {
        this.semanticGroup = semanticGroup;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Constants.MoveoOneType getType() {
        return type;
    }

    public void setType(Constants.MoveoOneType type) {
        this.type = type;
    }

    public Constants.MoveoOneAction getAction() {
        return action;
    }

    public void setAction(Constants.MoveoOneAction action) {
        this.action = action;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }
}