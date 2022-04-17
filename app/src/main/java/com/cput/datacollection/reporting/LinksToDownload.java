package com.cput.datacollection.reporting;
import java.util.List;

public class LinksToDownload {
    Integer id;
    Integer count;
    List<Website> data;

    public void setCount(Integer count) {
        this.count = count;
    }

    public void setData(List<Website> data) {
        this.data = data;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCount() {
        return count;
    }

    public Integer getId() {
        return id;
    }

    public List<Website> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "LinksToDownload{" +
                "id='" + id + '\'' +
                ", count=" + count +
                ", data=" + data +
                '}';
    }
}
