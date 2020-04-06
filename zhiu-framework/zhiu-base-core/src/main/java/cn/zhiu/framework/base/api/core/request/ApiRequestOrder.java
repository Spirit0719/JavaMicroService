package cn.zhiu.framework.base.api.core.request;


import cn.zhiu.framework.base.api.core.enums.PageOrderType;

import java.io.Serializable;

/**
 * The type Api request order.
 *
 * @author zhuzz
 * @time 2019 /04/02 16:49:35
 */
public class ApiRequestOrder implements Serializable {
    private static final long serialVersionUID = 2434398026798733708L;

    private String field;
    private PageOrderType orderType;

    private ApiRequestOrder() {

    }

    public ApiRequestOrder(String field, PageOrderType orderType) {
        this.field = field;
        this.orderType = orderType;
    }

    public String getField() {
        return field;
    }

    public PageOrderType getOrderType() {
        return orderType;
    }
}
