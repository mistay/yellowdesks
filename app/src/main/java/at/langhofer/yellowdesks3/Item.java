package at.langhofer.yellowdesks3;

public class Item {
    int totalDesks=0;
    int availableDesks=0;
    private String id = null;
    private String title = null;
    private String desc = null;
    private String pubdate = null;
    private String link = null;

    private Item() {
        // do not allow new Item()
    }

    public Item(int totalDesks, int availableDesks, String id, String title, String desc, String pubdate) {
        this.totalDesks = totalDesks;
        this.availableDesks = availableDesks;
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.pubdate = pubdate;
    }
    public int gettotalDesks() {
        return totalDesks;
    }
    public int getAvailableDesks() {
        return availableDesks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
