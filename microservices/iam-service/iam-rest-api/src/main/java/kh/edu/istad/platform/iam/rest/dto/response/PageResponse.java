package kh.edu.istad.platform.iam.rest.dto.response;

import java.util.List;

public class PageResponse<T> {
    private List<T> content;
    private PageMetadata page;

    public PageResponse() {}

    public PageResponse(List<T> content, int number, int size, long totalElements, int totalPages) {
        this.content = content;
        this.page = new PageMetadata(number, size, totalElements, totalPages);
    }

    public List<T> getContent() { return content; }
    public void setContent(List<T> content) { this.content = content; }
    public PageMetadata getPage() { return page; }
    public void setPage(PageMetadata page) { this.page = page; }

    public static class PageMetadata {
        private int number;
        private int size;
        private long totalElements;
        private int totalPages;

        public PageMetadata() {}

        public PageMetadata(int number, int size, long totalElements, int totalPages) {
            this.number = number;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
        }

        public int getNumber() { return number; }
        public void setNumber(int number) { this.number = number; }
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
        public long getTotalElements() { return totalElements; }
        public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    }
}
