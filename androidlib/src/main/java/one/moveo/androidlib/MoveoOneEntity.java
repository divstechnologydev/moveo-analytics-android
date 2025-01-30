package one.moveo.androidlib;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
public class MoveoOneEntity {
    private String c; //context
    private String type;
    private String userId;
    private Long t;
    private Map<String, String> prop;
    private Map<String, String> meta;
    private String sId;
}
