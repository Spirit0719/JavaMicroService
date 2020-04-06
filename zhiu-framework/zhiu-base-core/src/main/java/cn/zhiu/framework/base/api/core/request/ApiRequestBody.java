package cn.zhiu.framework.base.api.core.request;

public class ApiRequestBody {

    public static ApiRequestBody newInstance() {
        return new ApiRequestBody();
    }

    public static ApiRequestBody newInstance(ApiRequest request, ApiRequestPage requestPage) {
        return new ApiRequestBody(request, requestPage);
    }

    public ApiRequestBody() {

    }

    public ApiRequestBody(ApiRequest request, ApiRequestPage requestPage) {
        this.request = request;
        this.requestPage = requestPage;
    }

    private ApiRequest request;
    private ApiRequestPage requestPage;

    public ApiRequest getRequest() {
        return request;
    }

    public void setRequest(ApiRequest request) {
        this.request = request;
    }

    public ApiRequestPage getRequestPage() {
        return requestPage;
    }

    public void setRequestPage(ApiRequestPage requestPage) {
        this.requestPage = requestPage;
    }
}
