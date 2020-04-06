package cn.zhiu.framework.bean.core.dao;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

/**
 * The interface Base dao.
 *
 * @param <T>  the type parameter
 * @param <ID> the type parameter
 *
 * @author zhuzz
 * @time 2019 /04/15 16:10:52
 */
@NoRepositoryBean
public interface BaseDao<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {
    /**
     * 锁行读写的方式通过ID获取对象
     *
     * @param id 对象ID
     *
     * @return 对应的对象 t
     *
     * @author zhuzz
     * @time 2019 /04/15 16:10:52
     */
    T findOneForUpdate(ID id);

    /**
     * Find one t.
     *
     * @param id the id
     *
     * @return the t
     *
     * @author zhuzz
     * @time 2019 /04/15 16:10:52
     */
    T findOne(ID id);

}
