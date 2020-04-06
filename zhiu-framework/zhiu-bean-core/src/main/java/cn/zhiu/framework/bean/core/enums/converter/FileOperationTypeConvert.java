package cn.zhiu.framework.bean.core.enums.converter;


import cn.zhiu.framework.bean.core.enums.FileOperationType;
import org.springframework.core.convert.converter.Converter;

import javax.persistence.AttributeConverter;
import java.util.Objects;


/**
 * @Auther: yujuan
 * @Date: 19-4-11 11:46
 * @Description:
 */
public class FileOperationTypeConvert implements AttributeConverter<FileOperationType, Integer>, Converter<String, FileOperationType> {

    @Override
    public Integer convertToDatabaseColumn(FileOperationType fileOperationType) {
        if (!Objects.isNull(fileOperationType)) {
            return fileOperationType.getValue();
        }else{
            return null;
        }
    }

    @Override
    public FileOperationType convertToEntityAttribute(Integer integer) {
        return FileOperationType.get(integer);
    }

    @Override
    public FileOperationType convert(String s) {
        return FileOperationType.get(s);
    }
}
