package cn.zhiu.webapp.api.indoornav.config;

import cn.zhiu.framework.base.api.core.constant.RequestHeaderConstants;
import cn.zhiu.framework.restful.api.core.controller.AbstractBaseController;
import cn.zhiu.framework.restful.api.core.exception.user.UserNotFoundException;
import com.google.common.collect.Lists;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class ExtendAbstractBaseController extends AbstractBaseController {

    @Autowired
    protected JPAQueryFactory queryFactory;


    protected String getUserId() {
        HttpServletRequest request = getRequest();
        String userId = getHeader(RequestHeaderConstants.ACCESS_USERID);
        if (Objects.isNull(userId)) {
            userId = request.getParameter("userId");
        }
//        userId = "1fcf54fefa6941b8a2cd2bbf4cb4ef0f";
        if (Objects.isNull(userId)) {
            throw new UserNotFoundException();
        }
        return userId;
    }


    protected List<Map> tupleToMapList(List<Tuple> tuples, Expression<?>[] exprs) {
        List<Map> list = Lists.newArrayList();
        if (!CollectionUtils.isEmpty(tuples)) {
            Function<Object, String> defaultConvertKeyMap = p -> {
                if (p instanceof Path) {
                    return ((Path) p).getMetadata().getName();
                } else if (p instanceof Operation) {
                    List args = ((Operation) p).getArgs();
                    return args.get(args.size() - 1).toString();
                } else if (p instanceof SubQueryExpression) {
                    List args = ((Operation) ((SubQueryExpression) p).getMetadata().getProjection()).getArgs();
                    return args.get(args.size() - 1).toString();
                }
                return "";
            };
            list = tuples.stream().map(p -> Lists.newArrayList(exprs).stream().collect(Collectors.toMap(q -> defaultConvertKeyMap.apply(q), q -> p.get(q) == null ? "" : p.get(q)))).collect(Collectors.toList());
        }
        return list;
    }


}


