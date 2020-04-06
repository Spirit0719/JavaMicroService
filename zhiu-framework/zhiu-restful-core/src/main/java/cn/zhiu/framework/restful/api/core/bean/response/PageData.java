package cn.zhiu.framework.restful.api.core.bean.response;

import java.util.Collection;

public class PageData<T> {

    /**
     * 分页数据md5加密值
     */
    private String md5;

    private String scope;
    /**
     * 页码
     */
    private int offset;
    /**
     * 每页大小
     */
    private int limit;
    /**
     * 总页数
     */
    private int totalPage;
    /**
     * 总条数
     */
    private long totalCount;
    /**
     * 分页数据
     */
    private Collection<T> rows;

    public PageData() {
    }


    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public Collection<T> getRows() {
        return rows;
    }

    public void setRows(Collection<T> rows) {
        this.rows = rows;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
}
