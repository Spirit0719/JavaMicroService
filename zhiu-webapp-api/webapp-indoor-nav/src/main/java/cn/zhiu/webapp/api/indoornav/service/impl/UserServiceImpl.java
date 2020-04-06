package cn.zhiu.webapp.api.indoornav.service.impl;

import cn.zhiu.framework.base.api.core.request.ApiRequest;
import cn.zhiu.framework.base.api.core.service.impl.AbstractBaseApiServiceImpl;
import cn.zhiu.framework.base.api.core.util.BeanMapping;
import cn.zhiu.webapp.api.indoornav.entity.UserEntity;
import cn.zhiu.webapp.api.indoornav.dao.UserDao;
import cn.zhiu.webapp.api.indoornav.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * (User)表服务实现类
 *
 * @author Spirit0719
 * @since 2020-03-25 16:25:28
 */
@Service("userService")
public class UserServiceImpl extends AbstractBaseApiServiceImpl implements UserService {
    @Resource
    private UserDao userDao;

    @Override
    public List<UserEntity> AllList() {
        List<UserEntity> users = new ArrayList<>();
        users = userDao.findAll();

        return users;
    }


    @Override
    public UserEntity findAll(ApiRequest apiRequest) {

        List<UserEntity> result = userDao.findAll(convertSpecification(apiRequest));

        if (!CollectionUtils.isEmpty(result)) {
            return BeanMapping.map(result.get(0), UserEntity.class);
        }
        return null;
    }
}