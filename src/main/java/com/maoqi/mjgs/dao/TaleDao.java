package com.maoqi.mjgs.dao;

import com.maoqi.mjgs.pojo.dbbean.Book;
import com.maoqi.mjgs.pojo.dbbean.Feedback;
import com.maoqi.mjgs.pojo.dbbean.Tale;
import com.maoqi.mjgs.pojo.dbbean.VisitLog;
import com.maoqi.mjgs.pojo.vo.BookColumnVO;
import com.maoqi.mjgs.pojo.vo.BookVO;
import com.maoqi.mjgs.pojo.vo.TaleVO;

import java.util.List;

public interface TaleDao {

    /**
     * 查询故事
     * @param id 故事id
     * @return 故事信息
     */
    Tale getTaleById(Integer id);

    /**
     * 查询故事目录
     * @param type 故事类型
     * @return 故事目录
     */
    List<TaleVO> getTalesByType(String type);

    /**
     * 导入故事
     */
    void saveTales(List<Tale> list);

    /**
     * 获取同时在线人数
     * @return 在线人数
     */
    int getOnlineCounter();

    /**
     * 导入访问日志
     */
    void saveVisitLog(VisitLog item);

    /**
     * 保存反馈内容
     * @param feedback 反馈内容
     */
    void saveFeedback(Feedback feedback);

    /**
     * 查询书籍
     * @return 书籍信息
     */
    List<BookVO> getBookList();

    /**
     * 查询书籍目录分类
     * @param bookId 书籍Id
     * @return 目录分类
     */
    List<BookColumnVO> getColumnsByBookId(String bookId);

    /**
     * 查询分类下的目录
     * @param columnId 分类Id
     * @return 分类目录
     */
    List<TaleVO> getCatalogueByColumnId(String columnId);
}
