package cn.zhiu.framework.restful.api.core.exception;

import java.io.Serializable;
import java.util.Date;

/**
 * The type Exception info.
 *
 * @author zhuzz
 * @time 2019 /04/14 18:23:09
 */
public class ExceptionInfo implements Serializable {
    
    private Date timestamp;
    private Integer status;
    private String error;
    private String message;
    private String path;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
