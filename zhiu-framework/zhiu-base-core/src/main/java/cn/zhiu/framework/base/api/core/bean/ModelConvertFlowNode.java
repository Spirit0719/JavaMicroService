package cn.zhiu.framework.base.api.core.bean;

import cn.zhiu.framework.bean.core.enums.FileOperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModelConvertFlowNode implements Serializable {

    private FileOperationType type;

    private String convertSetting;

    private String convertUniqueCode;

}
