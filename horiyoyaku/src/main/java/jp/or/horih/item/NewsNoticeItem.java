package jp.or.horih.item;

public class NewsNoticeItem {
    private String notice_id;
    private String send_date;
    private String title;
    private String content;
    private String um_flg;


    public String getNotice_id() {
        return notice_id;
    }

    public void setNotice_id(String _notice_id) {
        this.notice_id = _notice_id;
    }

    public String getSend_date() {
        return send_date;
    }

    public void setSend_date(String _send_date) {
        this.send_date = _send_date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String _title) {
        this.title = _title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String _content) {
        this.content = _content;
    }

    public String getUm_flg() {
        return um_flg;
    }

    public void setUm_flg(String _um_flg) {
        this.um_flg = _um_flg;
    }


}
