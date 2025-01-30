package one.moveo.androidlib;


import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MoveoOneData {
    private String semanticGroup;
    private String id;
    private Constants.MoveoOneType type;
    private Constants.MoveoOneAction action;
    private Object value;
    private Map<String, String> metadata;
}