package cn.zhiu.framework.bean.core.enums.converter;

import cn.zhiu.framework.bean.core.enums.YesNoStatus;
import org.springframework.core.convert.converter.Converter;

import javax.persistence.AttributeConverter;

/**
 * The type Yes no status convert.
 *
 * @author zhuzz
 * @time 2019 /04/02 11:33:49
 */
public class YesNoStatusConvert  implements AttributeConverter<YesNoStatus, Integer>, Converter<String, YesNoStatus> {

    @Override
    public Integer convertToDatabaseColumn(YesNoStatus yesNoStatus) {
        return yesNoStatus.getValue();
    }

    @Override
    public YesNoStatus convertToEntityAttribute(Integer integer) {
        return YesNoStatus.get(integer);
    }

    @Override
    public YesNoStatus convert(String s) {
        return YesNoStatus.get(Integer.parseInt(s));
    }
}
