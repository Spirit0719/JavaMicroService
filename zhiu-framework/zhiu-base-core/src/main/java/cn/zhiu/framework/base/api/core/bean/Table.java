package cn.zhiu.framework.base.api.core.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class Table implements BaseApiBean {

    private String tableName = "result";
    private List<Column> columns = new ArrayList<>();
    private List<Object[]> rows = new ArrayList<>();


    public Table() {
    }

    public Table(String tableName, List<Column> columns, List<Object[]> rows) {
        this.tableName = tableName;
        this.columns = columns;
        this.rows = rows;
    }

    public Table(String... clumnNames) {
        for (String clumnName : clumnNames) {
            columns.add(new Column(clumnName));
        }
    }


    public Table(Map map) {
        map.forEach((k, v) -> {
            this.rows.add(new Object[]{k, v});
        });
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<Column> getColumns() {
        return columns;
    }

    public void setColumns(List<Column> columns) {
        this.columns = columns;
    }

    public List<Object[]> getRows() {
        return rows;
    }

    public void setRows(List<Object[]> rows) {

        this.rows = rows;
    }

    public void addColumn(Column column) {
        this.columns.add(column);
    }

    public void addColumn(String column) {
        this.columns.add(new Column(column));
    }

    public Table addRows(List<Object> data) {
        if (Objects.nonNull(data) && data.size() > 0 && data.size() >= columns.size()) {
            rows.add(data.toArray(new Object[data.size()]));
        } else {
            throw new RuntimeException("数据项与标题项不一致！");
        }
        return this;
    }

    public Table addRows(Object... data) {
        if (Objects.nonNull(data) && data.length > 0 && data.length >= columns.size()) {
            rows.add(data);
        } else {
            throw new RuntimeException("数据项与标题项不一致！");
        }
        return this;
    }

}