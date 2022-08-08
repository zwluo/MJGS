package com.maoqi.mjgs.pojo.dbbean;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Data
public class Tale {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;
  private String type;
  private String title;
  private String content;
  private String narrator;
  private String recorder;
  private Date createDate;
  private String createBy;

}
