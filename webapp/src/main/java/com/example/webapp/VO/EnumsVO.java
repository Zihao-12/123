package com.example.webapp.VO;

import lombok.Data;
import java.io.Serializable;
@Data
public class EnumsVO implements Serializable {
    private static final long serialVersionUID = -43873259441617904L;

    public String name;
    public Integer value;
}
