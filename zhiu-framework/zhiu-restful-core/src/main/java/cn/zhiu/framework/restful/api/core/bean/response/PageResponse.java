package cn.zhiu.framework.restful.api.core.bean.response;


import cn.zhiu.framework.restful.api.core.bean.CommonBaseRestfulApiBean;

import java.util.Collection;

public class PageResponse<T> extends CommonBaseRestfulApiBean {
    /**
     * 分页数据md5加密值
     */
    private String md5;

    private String scope;
    /**
     * 页码
     */
    private int pageIndex;
    /**
     * 每页大小
     */
    private int pageSize;
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
    private Collection<T> data;

    public PageResponse() {
    }

    public PageResponse(int pageIndex, int pageSize, long totalCount, Collection<T> listData) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.totalPage = (int) Math.ceil(totalCount / (double) pageSize);
        this.data = listData;
    }

    public PageResponse(int pageIndex, int pageSize, long totalCount, Collection<T> listData, String md5) {
        this.pageIndex = pageIndex;
        this.pageSize = pageSize;
        this.totalCount = totalCount;
        this.totalPage = (int) Math.ceil(totalCount / (double) pageSize);
        this.data = listData;
        this.md5 = md5;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
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

    public Collection<T> getData() {
        return data;
    }

    public void setData(Collection<T> data) {
        this.data = data;
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
