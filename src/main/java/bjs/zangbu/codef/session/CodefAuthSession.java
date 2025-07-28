package bjs.zangbu.codef.session;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.HashMap;

@Getter
@Setter
public class CodefAuthSession implements Serializable {
    private HashMap<String, Object> parameterMap;
    private Integer jobIndex;
    private Integer threadIndex;
    private String jti;
    private Long twoWayTimestamp;
    private String productUrl;
}

