package kh.edu.istad.platform.iam.application.dto;

public class RoleFilterQuery {
    private String search;
    private Boolean active;
    private int page = 0;
    private int size = 20;
    private String sort = "name,asc";

    public RoleFilterQuery() {}

    public static Builder builder() { return new Builder(); }

    public String getSearch() { return search; }
    public void setSearch(String search) { this.search = search; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public String getSort() { return sort; }
    public void setSort(String sort) { this.sort = sort; }

    public static class Builder {
        private final RoleFilterQuery target = new RoleFilterQuery();
        public Builder search(String s) { target.setSearch(s); return this; }
        public Builder active(Boolean a) { target.setActive(a); return this; }
        public Builder page(int p) { target.setPage(p); return this; }
        public Builder size(int sz) { target.setSize(sz); return this; }
        public Builder sort(String srt) { target.setSort(srt); return this; }
        public RoleFilterQuery build() { return target; }
    }
}
