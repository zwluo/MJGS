package com.maoqi.mjgs.controller;

import com.maoqi.mjgs.pojo.dbbean.Feedback;
import com.maoqi.mjgs.pojo.vo.BookVO;
import com.maoqi.mjgs.pojo.vo.FeedbackVO;
import com.maoqi.mjgs.pojo.vo.TaleVO;
import com.maoqi.mjgs.service.TaleService;
import com.maoqi.mjgs.service.impl.TaleServiceImpl;
import com.maoqi.mjgs.util.IpUtil;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
public class TaleController {

    private final TaleService taleService;

    public TaleController(TaleServiceImpl taleService) {
        this.taleService = taleService;
    }

    /**
     * 查询故事
     * @param id 故事 id
     * @return 故事信息
     */
    @GetMapping("/getTale/{id}")
    public TaleVO getTaleById(@PathVariable("id") Integer id) {
        return taleService.getTaleById(id);
    }

    /**
     * 查询不同类型的故事目录
     * @param type 故事类型
     * @return 故事目录
     */
    @GetMapping("/getTalesByType/{type}")
    public List<TaleVO> getTalesByType(@PathVariable("type") String type) {
        return taleService.getTalesByType(type);
    }

    /**
     * 将故事从 TXT 文档导入到数据库中
     * @return 导入结果
     */
    @GetMapping("/importTales")
    public String importTales() {
        taleService.saveTales();
        return "success";
    }

    /**
     * 获取当前在线人数
     * @return 在线人数
     */
    @GetMapping("/getCounter")
    public int getCounter() {
        return taleService.getOnlineCounter();
    }

    /**
     * 将故事从 TXT 文档导入到数据库中
     * @return 导入结果
     */
    @PostMapping("/addFeedback")
    public String addFeedback(@RequestBody FeedbackVO feedbackVO, HttpServletRequest request) {
        String ip = IpUtil.getIpAddr(request);
        feedbackVO.setIp(ip);

        Feedback feedback = new Feedback(feedbackVO);
        taleService.saveFeedback(feedback);
        return "success";
    }

    /**
     * 查询所有书籍
     * @return 书籍信息
     */
    @GetMapping("/getBookList")
    public List<BookVO> getBookList() {
        return taleService.getBookList();
    }

    /**
     * 查询书籍目录
     * @param bookId 书籍Id
     * @return 书籍目录
     */
    @GetMapping("/getColumnsByBookId")
    public Map<String, List<TaleVO>> getColumnsByBookId(String bookId) {
        return taleService.getColumnsByBookId(bookId);
    }
}
