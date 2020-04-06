package cn.zhiu.framework.base.api.core.bean;

import com.google.common.collect.Lists;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

public class DirStructModel implements BaseApiBean {
    private String name;
    private String path;
    private Boolean fileFlag;
    private DirStructModel parent;
    private List<DirStructModel> dirStructModels;


    /**
     * 获取扁平化结构子集
     *
     * @return the flattened struct
     *
     * @author zhuzz
     * @time 2019 /05/11 18:16:13
     */
    public List<DirStructModel> flattenedStructChilds() {
        return flattenedStructChilds(this);
    }

    private List<DirStructModel> flattenedStructChilds(DirStructModel dirStructModel) {
        List<DirStructModel> result = Lists.newArrayList();
        if (Objects.nonNull(dirStructModel.getDirStructModels())) {
            result.addAll(dirStructModel.getDirStructModels());
            dirStructModel.getDirStructModels().forEach(p ->
                    {
                        List<DirStructModel> dirStructModels = p.flattenedStructChilds(p);
                        if (!CollectionUtils.isEmpty(dirStructModels)) {
                            result.addAll(dirStructModels);
                        }
                    }
            );
        }
        return result;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Boolean getFileFlag() {
        return fileFlag;
    }

    public void setFileFlag(Boolean fileFlag) {
        this.fileFlag = fileFlag;
    }

    public DirStructModel getParent() {
        return parent;
    }

    public void setParent(DirStructModel parent) {
        this.parent = parent;
    }

    public List<DirStructModel> getDirStructModels() {
        return dirStructModels;
    }

    public void setDirStructModels(List<DirStructModel> dirStructModels) {
        this.dirStructModels = dirStructModels;
    }


}
