package cn.zhiu.webapp.api.indoornav.controller;

import cn.zhiu.framework.base.api.core.request.ApiRequest;
import cn.zhiu.framework.restful.api.core.bean.response.DataResponse;
import cn.zhiu.webapp.api.indoornav.entity.UserEntity;
import cn.zhiu.webapp.api.indoornav.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * (User)表控制层
 *
 * @author Spirit0719
 * @since 2020-03-25 16:25:28
 */
@RestController
@RequestMapping("/user")
@Api(tags = "UserController", description = "UserController | user Module")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 查询多条数据
     *
     * @return 多条数据
     */
    @ApiOperation(value = "查询多条数据", notes = "")
    @RequestMapping(value = "/allList", method = RequestMethod.GET)
    public List<UserEntity> AllList() {
        return this.userService.AllList();
    }

    @ApiOperation(value = "登录", notes = "")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public DataResponse<UserEntity> Login(@RequestBody UserEntity userEntity) {
        System.out.println("传入的信息------>>>>>>>>>>>>>>>>>" + userEntity.getLoginName());
        ApiRequest apiRequest = ApiRequest.newInstance().filterEqual("loginName", userEntity.getLoginName());
        UserEntity one = userService.findAll(apiRequest);
        System.out.println("查询到的登录信息------>>>>>>>>>>>>>>>>>" + one);
        UserEntity user = new UserEntity();

        return new DataResponse<>(one);
    }

}