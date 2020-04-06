package cn.zhiu.framework.base.api.core.util;

import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipModel {
    private ZipFile zipFile;
    private List<ZipEntry> list;

    public ZipFile getZipFile() {
        return zipFile;
    }

    public void setZipFile(ZipFile zipFile) {
        this.zipFile = zipFile;
    }

    public List<ZipEntry> getList() {
        return list;
    }

    public void setList(List<ZipEntry> list) {
        this.list = list;
    }
}