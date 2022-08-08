package com.maoqi.mjgs.pojo.vo;

import com.maoqi.mjgs.pojo.dbbean.Tale;
import com.maoqi.mjgs.util.StringUtils;
import lombok.Data;

import java.io.Serializable;

@Data
public class TaleVO implements Serializable {

    private String id;
    private String type;
    private String title;
    private String[] paragraphs;
    private String narrator;
    private String recorder;

    public TaleVO() {}

    public TaleVO(Tale tale) {
        this.type = tale.getType();
        this.title = tale.getTitle();
        this.narrator = tale.getNarrator();
        this.recorder = tale.getRecorder();

        // 对内容按章节切分
        if (StringUtils.isNotBlank(tale.getContent())) {
            this.paragraphs = tale.getContent().split("\\n");
        }
    }

    public TaleVO(Object id, Object title) {
        this.id = String.valueOf(id);
        this.title = String.valueOf(title);
    }

}
