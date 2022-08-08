package com.maoqi.mjgs.pojo.vo;

import lombok.Data;

@Data
public class BookVO {

    private String id;
    private String title;
    private String url;
    private String cover;

    public BookVO(Object id, Object title, Object url, Object cover) {
        this.id = String.valueOf(id);;
        this.title = String.valueOf(title);;
        this.url = String.valueOf(url);;
        this.cover = String.valueOf(cover);;
    }

}
