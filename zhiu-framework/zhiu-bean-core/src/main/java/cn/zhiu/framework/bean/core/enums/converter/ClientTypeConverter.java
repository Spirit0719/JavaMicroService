package cn.zhiu.framework.bean.core.enums.converter;

import cn.zhiu.framework.bean.core.enums.ClientType;
import org.springframework.core.convert.converter.Converter;
import javax.persistence.AttributeConverter;

public class ClientTypeConverter implements AttributeConverter<ClientType, Integer>, Converter<String, ClientType> {
    
    @Override
    public Integer convertToDatabaseColumn(ClientType deleteStatus) {
        return deleteStatus.getValue();
    }

    @Override
    public ClientType convertToEntityAttribute(Integer integer) {
        return ClientType.get(integer);
    }

    @Override
    public ClientType convert(String s) {
        return ClientType.get(Integer.parseInt(s));
    }
}
