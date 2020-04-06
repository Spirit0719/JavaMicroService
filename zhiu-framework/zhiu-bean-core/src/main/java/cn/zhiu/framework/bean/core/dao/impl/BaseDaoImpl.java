package cn.zhiu.framework.bean.core.dao.impl;


import cn.zhiu.framework.bean.core.dao.BaseDao;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.io.Serializable;

public class BaseDaoImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseDao<T, ID> {

    private final EntityManager entityManager;

    public BaseDaoImpl(Class<T> domainClass, EntityManager em) {
        super(domainClass, em);
        this.entityManager = em;
    }


    @Override
    public T findOneForUpdate(ID id) {
        return this.entityManager.find(this.getDomainClass(), id, LockModeType.PESSIMISTIC_WRITE);
    }

    @Override
    public T findOne(ID id) {
        return this.entityManager.find(this.getDomainClass(), id);
    }

    @Override
    public T getOne(ID id) {
        return this.findOne(id);
    }
}
