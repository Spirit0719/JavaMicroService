package cn.zhiu.webapp.api.indoornav.entity;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

/**
 * (User)实体类
 *
 * @author Spirit0719
 * @since 2020-03-25 16:25:28
 */
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "user")
public class UserEntity {
    /**
     * 用户id,主键
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 用户权限:
     * SUPER_ADMIN:超级管理员,
     * ADMIN:管理员,
     * ROOM_USER:房间用户
     * USER:普通用户
     */
    @Column(name = "power", nullable = false)
    private String power;
    /**
     * 用户名,不得重复
     */
    @Column(name = "name", nullable = false)
    private String name;
    /**
     * 登录名,不得重复,不可为空
     */
    @Column(name = "login_Name", nullable = false)
    private String loginName;
    /**
     * 登录密码
     */
    @Column(name = "login_Password", nullable = false)
    private String loginPassword;
    /**
     * 房间用户的房间ID,默认为null
     */
    @Column(name = "roomid", nullable = false)
    private String roomid;



    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPower() {
        return power;
    }

    public void setPower(String power) {
        this.power = power;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getLoginPassword() {
        return loginPassword;
    }

    public void setLoginPassword(String loginPassword) {
        this.loginPassword = loginPassword;
    }

    public String getRoomid() {
        return roomid;
    }

    public void setRoomid(String roomid) {
        this.roomid = roomid;
    }

}