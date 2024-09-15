package per.cy.personalwiki.resp;

import java.util.List;

public class PageResp<T> {
    private int totalsPages;

    private List<T> list;

    public int getTotalsPages() {
        return totalsPages;
    }

    public void setTotalsPages(int totalsPages) {
        this.totalsPages = totalsPages;
    }


    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "PageResp{" +
                "totalsPages=" + totalsPages +
                ", list=" + list +
                '}';
    }
}
