package com.maoqi.mjgs.service;

import com.maoqi.mjgs.pojo.dbbean.Feedback;
import com.maoqi.mjgs.pojo.vo.BookVO;
import com.maoqi.mjgs.pojo.vo.TaleVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface TaleService {

    /**
     * 查询故事
     * @param id 故事id
     * @return 故事信息
     */
    TaleVO getTaleById(Integer id);

    /**
     * 查询故事目录
     * @param type 故事类型
     * @return 故事目录
     */
    List<TaleVO> getTalesByType(String type);

    /**
     * 查询书籍目录
     * @param bookId 书籍Id
     * @return 书籍目录
     */
    Map<String, List<TaleVO>> getColumnsByBookId(String bookId);

    /**
     * 导入故事
     */
    void saveTales();

    /**
     * 获取同时在线人数
     * @return 在线人数
     */
    int getOnlineCounter();

    /**
     * 保存反馈的内容
     * @param feedback 反馈内容
     */
    void saveFeedback(Feedback feedback);

    /**
     * 查询书籍
     * @return 书籍信息
     */
    List<BookVO> getBookList();
}
