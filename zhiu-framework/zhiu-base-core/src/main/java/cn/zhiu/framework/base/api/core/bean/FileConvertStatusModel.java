package cn.zhiu.framework.base.api.core.bean;

import cn.zhiu.framework.bean.core.enums.FileOperationStatus;
import cn.zhiu.framework.bean.core.enums.FileOperationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The type File convert status model.
 *
 * @author zhuzz
 * @time 2020 /01/03 15:47:36
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileConvertStatusModel implements BaseApiBean {

    /**
     * The File id.
     */
    private String fileId;
    /**
     * The Type.
     */
    private FileOperationType type;
    /**
     * The Convert unique code.
     */
    private String convertUniqueCode;
    /**
     * The Process.
     */
    private Integer process;
    /**
     * The Status.
     */
    private FileOperationStatus status;
}
