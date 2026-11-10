package kh.edu.istad.platform.iam.application.dto;

public class UserFilterQuery {
    private String search;
    private String role;
    private Boolean enabled;
    private int page = 0;
    private int size = 20;
    private String sort = "createdAt,desc";

    public UserFilterQuery() {}

    public static Builder builder() { return new Builder(); }

    public String getSearch() { return search; }
    public void setSearch(String search) { this.search = search; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getSize() { return size; }
    public void setSize(int size) { this.size = size; }
    public String getSort() { return sort; }
    public void setSort(String sort) { this.sort = sort; }

    public static class Builder {
        private final UserFilterQuery target = new UserFilterQuery();
        public Builder search(String s) { target.setSearch(s); return this; }
        public Builder role(String r) { target.setRole(r); return this; }
        public Builder enabled(Boolean e) { target.setEnabled(e); return this; }
        public Builder page(int p) { target.setPage(p); return this; }
        public Builder size(int sz) { target.setSize(sz); return this; }
        public Builder sort(String srt) { target.setSort(srt); return this; }
        public UserFilterQuery build() { return target; }
    }
}
