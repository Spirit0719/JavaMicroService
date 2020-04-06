package cn.zhiu.framework.restful.api.core.bean;

/**
 * 抽象的基础bean
 */
public class CommonBaseRestfulApiBean implements BaseRestfulApiBean {

    private static final long serialVersionUID = 7062911059028505557L;

    public static final String SUCCESS_CODE = "000000";


    private String code = SUCCESS_CODE;

    private String errorMsg;

    private Boolean isSuccess;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public Boolean getSuccess() {
        return this.code.equals(SUCCESS_CODE);
    }
}
