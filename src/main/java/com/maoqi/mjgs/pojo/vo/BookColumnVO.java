package com.maoqi.mjgs.pojo.vo;

import lombok.Data;

@Data
public class BookColumnVO {

    String id;
    String title;

    public BookColumnVO(Object id, Object title) {
        this.id = String.valueOf(id);
        this.title = String.valueOf(title);
    }

}
