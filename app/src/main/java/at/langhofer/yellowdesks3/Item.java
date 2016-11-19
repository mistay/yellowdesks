package at.langhofer.yellowdesks3;

public class Item {
    private String id = null;
    private String title = null;
    private String desc = null;
    private String pubdate = null;
    private String link = null;

    private Item() {
        // do not allow new Item()
    }

    public Item(String id, String title, String desc, String pubdate, String link) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.pubdate = pubdate;
        this.link = link;
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
