package cn.zhiu.framework.restful.api.core.bean.response;

import cn.zhiu.framework.restful.api.core.bean.CommonBaseRestfulApiBean;

/**
 * The type Data response.
 *
 * @param <T> the type parameter
 *
 * @author zhuzz
 * @time 2019 /04/02 18:40:44
 */
public class DataResponse<T> extends CommonBaseRestfulApiBean {
    
    private T data;

    public DataResponse() {
    }

    public DataResponse(T data) {
        this.data = data;
    }


    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

}
