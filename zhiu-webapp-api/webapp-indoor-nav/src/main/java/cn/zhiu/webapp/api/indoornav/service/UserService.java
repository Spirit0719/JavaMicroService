package cn.zhiu.webapp.api.indoornav.service;

import cn.zhiu.framework.base.api.core.request.ApiRequest;
import cn.zhiu.framework.base.api.core.service.BaseApiService;
import cn.zhiu.webapp.api.indoornav.entity.UserEntity;
import java.util.List;

/**
 * (User)表服务接口
 *
 * @author Spirit0719
 * @since 2020-03-25 16:25:28
 */
public interface UserService extends BaseApiService {

    List<UserEntity> AllList();

    UserEntity findAll(ApiRequest apiRequest);
}