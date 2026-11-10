package kh.edu.istad.platform.iam.application.dto;

public class PermissionFilterQuery {
    private String resource;
    private String action;
    private int page = 0;
    private int size = 20;
    private String sort = "name,asc";

    public PermissionFilterQuery() {}

    public static Builder builder() { return new Builder(); }

    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public String getSort() { return sort; }
    public void setSort(String sort) { this.sort = sort; }

    public static class Builder {
        private final PermissionFilterQuery target = new PermissionFilterQuery();
        public Builder resource(String r) { target.setResource(r); return this; }
        public Builder action(String a) { target.setAction(a); return this; }
        public Builder page(int p) { target.setPage(p); return this; }
        public Builder size(int sz) { target.setSize(sz); return this; }
        public Builder sort(String srt) { target.setSort(srt); return this; }
        public PermissionFilterQuery build() { return target; }
    }
}
