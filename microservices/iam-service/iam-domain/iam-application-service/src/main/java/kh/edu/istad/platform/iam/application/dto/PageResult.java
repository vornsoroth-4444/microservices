package kh.edu.istad.platform.iam.application.dto;

import java.util.Collections;
import java.util.List;

public class PageResult<T> {
    private List<T> content;
    private int number;
    private int size;
    private long totalElements;
    private int totalPages;

    public PageResult() {
        this.content = Collections.emptyList();
    }

    public PageResult(List<T> content, int number, int size, long totalElements, int totalPages) {
        this.content = content != null ? content : Collections.emptyList();
        this.number = number;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
    }

    public List<T> getContent() { return content; }
    public List<T> getItems() { return content; }
    public void setContent(List<T> content) { this.content = content; }
    public int getNumber() { return number; }
    public int getPage() { return number; }
    public void setNumber(int number) { this.number = number; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
}
