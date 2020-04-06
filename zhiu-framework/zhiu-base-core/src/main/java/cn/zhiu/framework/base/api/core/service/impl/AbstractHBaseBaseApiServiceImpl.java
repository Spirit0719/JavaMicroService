package cn.zhiu.framework.base.api.core.service.impl;

import cn.zhiu.framework.base.api.core.bean.FieldDescModel;
import cn.zhiu.framework.base.api.core.request.ApiRequest;
import cn.zhiu.framework.base.api.core.request.ApiRequestFilter;
import cn.zhiu.framework.base.api.core.service.BaseApiService;
import com.google.common.collect.Lists;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


/**
 * The type Abstract h base base api service.
 *
 * @author zhuzz
 * @time 2019 /05/14 21:53:20
 */
public abstract class AbstractHBaseBaseApiServiceImpl implements BaseApiService {

    protected final transient Logger logger = LoggerFactory.getLogger(this.getClass());

    protected Scan convertScan(ApiRequest request) {
        Scan scan = new Scan();
        List<Filter> filters = Lists.newArrayList();
        if (CollectionUtils.notEmpty(request.getFilterList())) {
            for (ApiRequestFilter apiRequestFilter : request.getFilterList()) {
                switch (apiRequestFilter.getOperatorType()) {
                    case LIKE:
                        FieldDescModel fieldDesc = apiRequestFilter.getFieldDesc();
                        SingleColumnValueFilter filter = new SingleColumnValueFilter(Bytes.toBytes(fieldDesc.getColumnFamily())
                                , Bytes.toBytes(apiRequestFilter.getField())
                                , CompareFilter.CompareOp.EQUAL
                                , new RegexStringComparator(apiRequestFilter.getValue() + ""));
                        filters.add(filter);
                        break;
                    case EQ:
                        fieldDesc = apiRequestFilter.getFieldDesc();
                        filter = new SingleColumnValueFilter(Bytes.toBytes(fieldDesc.getColumnFamily()), Bytes.toBytes(apiRequestFilter.getField())
                                , CompareFilter.CompareOp.EQUAL
                                , getBytesByValue(apiRequestFilter.getValue()));
                        filters.add(filter);
                        break;
                }
            }
        }
        if (!CollectionUtils.isEmpty(filters)) {
            scan.setFilter(new FilterList(filters));
        }
        return scan;
    }

    private byte[] getBytesByValue(Object val) {
        String newVal = val + "";
        if (val instanceof Integer) {
            return Bytes.toBytes(Integer.valueOf(newVal));
        } else if (val instanceof String) {
            return Bytes.toBytes(newVal);
        } else if (val instanceof Long) {
            return Bytes.toBytes(Long.valueOf(newVal));
        } else if (val instanceof Boolean) {
            return Bytes.toBytes(Boolean.valueOf(newVal));
        }
        return null;
    }


}
