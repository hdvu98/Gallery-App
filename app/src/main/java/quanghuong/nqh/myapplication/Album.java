package quanghuong.nqh.myapplication;

public class Album {
    private String nameAlbum;
    private String pathAvatar;
    private int imgCount;
    private long createDate;
    private boolean checkbox;



    public Album(String nameAlbum, String path, int imgCount, long createDate, boolean checkbox) {
        this.nameAlbum = nameAlbum;
        this.pathAvatar = path;
        this.imgCount = imgCount;
        this.createDate = createDate;
        this.checkbox = checkbox;

    }

    public String getNameAlbum() {
        return nameAlbum;
    }

    public void setNameAlbum(String nameAlbum) {
        this.nameAlbum = nameAlbum;
    }

    public String getPath() {
        return pathAvatar;
    }

    public void setPath(String path) {
        this.pathAvatar = path;
    }

    public int getImgCount() {
        return imgCount;
    }

    public void setImgCount(int imgCount) {
        this.imgCount = imgCount;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public boolean isCheckbox() {
        return checkbox;
    }

    public void setCheckbox(boolean checkbox) {
        this.checkbox = checkbox;
    }
}
