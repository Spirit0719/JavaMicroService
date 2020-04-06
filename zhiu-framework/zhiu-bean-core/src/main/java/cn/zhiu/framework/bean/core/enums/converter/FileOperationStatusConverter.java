package cn.zhiu.framework.bean.core.enums.converter;

import cn.zhiu.framework.bean.core.enums.FileOperationStatus;
import org.springframework.core.convert.converter.Converter;

import javax.persistence.AttributeConverter;

/**
 * @Auther: yujuan
 * @Date: 19-4-11 14:42
 * @Description:
 */
public class FileOperationStatusConverter implements AttributeConverter<FileOperationStatus, Integer>, Converter<String, FileOperationStatus> {

    @Override
    public Integer convertToDatabaseColumn(FileOperationStatus value) {
        return value.getValue();
    }

    @Override
    public FileOperationStatus convertToEntityAttribute(Integer integer) {
        return FileOperationStatus.get(integer);
    }

    @Override
    public FileOperationStatus convert(String s) {
        return FileOperationStatus.get(Integer.parseInt(s));
    }
}
