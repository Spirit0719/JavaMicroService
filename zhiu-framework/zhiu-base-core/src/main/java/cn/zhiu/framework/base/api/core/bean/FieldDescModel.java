package cn.zhiu.framework.base.api.core.bean;

public class FieldDescModel implements BaseApiBean {

    public FieldDescModel(String columnFamily) {
        this.columnFamily = columnFamily;
    }

    private String columnFamily;

    public String getColumnFamily() {
        return columnFamily;
    }

    public void setColumnFamily(String columnFamily) {
        this.columnFamily = columnFamily;
    }
}
