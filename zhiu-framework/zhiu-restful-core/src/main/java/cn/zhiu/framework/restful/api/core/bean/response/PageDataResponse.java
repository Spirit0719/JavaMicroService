package cn.zhiu.framework.restful.api.core.bean.response;


import cn.zhiu.framework.restful.api.core.bean.CommonBaseRestfulApiBean;

import java.util.Collection;

public class PageDataResponse<T> extends CommonBaseRestfulApiBean {

    private PageData<T> data;

    public PageDataResponse() {
        data = new PageData<>();
    }

    public PageDataResponse(int pageIndex, int pageSize, long totalCount, Collection<T> listData) {
        data = new PageData<>();
        this.data.setOffset(pageIndex);
        this.data.setLimit(pageSize);
        this.data.setTotalCount(totalCount);
        this.data.setTotalPage((int) Math.ceil(totalCount / (double) pageSize));
        this.data.setRows(listData);
    }

    public PageDataResponse(int pageIndex, int pageSize, long totalCount, Collection<T> listData, String md5) {
        data = new PageData<>();
        this.data.setOffset(pageIndex);
        this.data.setLimit(pageSize);
        this.data.setTotalCount(totalCount);
        this.data.setTotalPage((int) Math.ceil(totalCount / (double) pageSize));
        this.data.setRows(listData);
        this.data.setMd5(md5);
    }


    public PageData<T> getData() {
        return data;
    }

    public void setData(PageData<T> data) {
        this.data = data;
    }

    public void setOffset(int offset) {
        this.data.setOffset(offset);
    }



    public void setLimit(int limit) {
        this.data.setLimit(limit);
    }


    public void setTotalPage(int totalPage) {
        this.data.setTotalPage(totalPage);
    }

    public void setTotalCount(long totalCount) {
        this.data.setTotalCount(totalCount);
    }


    public void setRows(Collection<T> rows) {
        this.data.setRows(rows);
    }

    public void setMd5(String md5) {
        this.data.setMd5(md5);
    }

    public void setScope(String scope) {
        this.data.setScope(scope);
    }

}
