package cn.zhiu.framework.restful.api.core.bean;

/**
 * 参数验证出错详情条目
 */
public class ParameterNotValidDetailItem extends CommonBaseRestfulApiBean {

    private static final long serialVersionUID = 6514393759766916606L;

    /**
     * 验证出错的字段名
     */
    private String field;

    /**
     * 验证出错的消息
     */
    private String message;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
