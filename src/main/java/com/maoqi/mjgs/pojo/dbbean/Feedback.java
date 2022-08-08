package com.maoqi.mjgs.pojo.dbbean;

import com.maoqi.mjgs.pojo.vo.FeedbackVO;
import com.maoqi.mjgs.util.StringUtils;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Feedback {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;
  private String type;
  private String title;
  private String content;
  private String ip;
  private String useragent;

  public Feedback() {
  }

  public Feedback(FeedbackVO feedbackVO) {
    this.type = feedbackVO.getType();
    this.title = feedbackVO.getTitle();
    this.content = feedbackVO.getContent();
    this.ip = feedbackVO.getIp();

    if (StringUtils.isNotBlank(feedbackVO.getUseragent())) {
      if (feedbackVO.getUseragent().length() > 900) {
        this.useragent = feedbackVO.getUseragent().substring(0, 900);
      } else {
        this.useragent = feedbackVO.getUseragent();
      }
    }
  }

}
