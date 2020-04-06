package cn.zhiu.framework.base.api.core.bean;


import cn.zhiu.framework.base.api.core.enums.DataTypeEnum;

public class Column implements BaseApiBean {

    public Column() {
    }


    public Column(String name) {
        this.name = name;
    }

    public Column(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public Column(String name, String code, DataTypeEnum type) {
        this.name = name;
        this.code = code;
        this.type = type;
    }

    private String name;
    private String code;
    private DataTypeEnum type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public DataTypeEnum getType() {
        return type;
    }

    public void setType(DataTypeEnum type) {
        this.type = type;
    }
}