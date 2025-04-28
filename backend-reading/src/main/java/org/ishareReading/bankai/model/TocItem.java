package org.ishareReading.bankai.model;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * 封装目录结构
 */
@Data
@ToString
public class TocItem {
    private String title;
    private int page;
    private List<TocItem> children;

    public TocItem(String title, int page) {
        this.title = title;
        this.page = page;
        this.children = new ArrayList<>();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public List<TocItem> getChildren() {
        return children;
    }

    public void setChildren(List<TocItem> children) {
        this.children = children;
    }

    public void addChild(TocItem child) {
        this.children.add(child);
    }
}