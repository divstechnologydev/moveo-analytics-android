package one.moveo.androidlib;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MoveoOneEntity {
    private String c; //context
    private String type;
    private String userId;
    private Long t;
    private Map<String, String> prop;
    private Map<String, String> meta;
    private String sId;
    private Map<String, String> additionalMeta; // New field for additional metadata

    // Constructor with all fields including additional metadata
    public MoveoOneEntity(String c, String type, String userId, Long t, Map<String, String> prop, Map<String, String> meta, String sId, Map<String, String> additionalMeta) {
        this.c = c;
        this.type = type;
        this.userId = userId;
        this.t = t;
        this.prop = prop;
        this.meta = meta;
        this.sId = sId;
        this.additionalMeta = additionalMeta;
    }

    // Constructor without additional metadata (sets it to empty map)
    public MoveoOneEntity(String c, String type, String userId, Long t, Map<String, String> prop, Map<String, String> meta, String sId) {
        this(c, type, userId, t, prop, meta, sId, new HashMap<>());
    }

    public String getC() {
        return c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getT() {
        return t;
    }

    public void setT(Long t) {
        this.t = t;
    }

    public Map<String, String> getProp() {
        return prop;
    }

    public void setProp(Map<String, String> prop) {
        this.prop = prop;
    }

    public Map<String, String> getMeta() {
        return meta;
    }

    public void setMeta(Map<String, String> meta) {
        this.meta = meta;
    }

    public String getSId() {
        return sId;
    }

    public void setSId(String sId) {
        this.sId = sId;
    }

    public Map<String, String> getAdditionalMeta() {
        return additionalMeta;
    }

    public void setAdditionalMeta(Map<String, String> additionalMeta) {
        this.additionalMeta = additionalMeta;
    }
}