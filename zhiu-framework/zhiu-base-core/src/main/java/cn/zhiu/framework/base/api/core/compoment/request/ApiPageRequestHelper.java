package cn.zhiu.framework.base.api.core.compoment.request;

import cn.zhiu.framework.base.api.core.request.ApiRequest;
import cn.zhiu.framework.base.api.core.request.ApiRequestPage;
import cn.zhiu.framework.base.api.core.response.ApiResponse;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.BiFunction;

public class ApiPageRequestHelper {

    public static <T> List<T> request(ApiRequest apiRequest, ApiRequestPage apiRequestPage, BiFunction<ApiRequest, ApiRequestPage, ApiResponse<T>> biFunction) throws RuntimeException {
        List<T> result = Lists.newArrayList();

        while (true) {
            ApiResponse<T> apiResponse = biFunction.apply(apiRequest, apiRequestPage);

            if (apiResponse == null || apiResponse.getCount() == 0) {
                break;
            }

            result.addAll(apiResponse.getPagedData());

            if (apiResponse.getCount() < apiRequestPage.getPageSize()) {
                break;
            }

            apiRequestPage.pagingNext();
        }

        return result;
    }
}
