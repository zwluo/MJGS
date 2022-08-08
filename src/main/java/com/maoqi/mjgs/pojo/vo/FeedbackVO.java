package com.maoqi.mjgs.pojo.vo;

import lombok.Data;

@Data
public class FeedbackVO {

    private long id;
    private String type;
    private String title;
    private String content;
    private String useragent;
    private String ip;

}
