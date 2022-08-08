package com.maoqi.mjgs.service.impl;

import com.maoqi.mjgs.dao.TaleDao;
import com.maoqi.mjgs.dao.impl.TaleDaoImpl;
import com.maoqi.mjgs.pojo.dbbean.Feedback;
import com.maoqi.mjgs.pojo.dbbean.Tale;
import com.maoqi.mjgs.pojo.vo.BookColumnVO;
import com.maoqi.mjgs.pojo.vo.BookVO;
import com.maoqi.mjgs.pojo.vo.TaleVO;
import com.maoqi.mjgs.service.TaleService;
import com.maoqi.mjgs.util.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service
public class TaleServiceImpl implements TaleService {

    private final TaleDao taleDao;

    public TaleServiceImpl(TaleDaoImpl taleDao) {
        this.taleDao = taleDao;
    }

    @Cacheable(value = "mjgs:cache:getTaleById", key = "'id:' + #p0")
    @Override
    public TaleVO getTaleById(Integer id) {
        TaleVO taleVO = null;
        Tale tale = taleDao.getTaleById(id);
        if(tale != null) {
            taleVO = new TaleVO(tale);
        }

        return taleVO;
    }

    @Cacheable(value = "mjgs:cache:getTalesByType", key = "'type:' + #p0")
    @Override
    public List<TaleVO> getTalesByType(String type) {
        return taleDao.getTalesByType(type);
    }

    @Override
    @Cacheable(value = "mjgs:cache:getColumnsByBookId", key = "'bookId:' + #p0")
    public Map<String, List<TaleVO>> getColumnsByBookId(String bookId) {
        Map<String, List<TaleVO>> map = new LinkedHashMap<>();

        // 查询书籍目录分类
        List<BookColumnVO> columnList = taleDao.getColumnsByBookId(bookId);
        for (BookColumnVO item : columnList) {
            List<TaleVO> catalogueList = taleDao.getCatalogueByColumnId(item.getId());
            map.put(item.getTitle(), catalogueList);
        }

        return map;
    }

    @Override
    public void saveTales() {
        List<Tale> list;
        /*list = getTalesFromTxt();
        list.addAll(getBalladFromTxt());
        list.addAll(getProverbFromTxt());
        list.addAll(getDescribe());
        list.addAll(getNordicTales());*/

        list = getHBJZ();

        taleDao.saveTales(list);
    }

    @Cacheable(value = "mjgs:cache:getOnlineCounter")
    @Override
    public int getOnlineCounter() {
        return taleDao.getOnlineCounter();
    }

    @Override
    public void saveFeedback(Feedback feedback) {
        taleDao.saveFeedback(feedback);
    }

    @Override
    public List<BookVO> getBookList() {
        return taleDao.getBookList();
    }

    /**
     * 从TXT文本导入神话故事
     * @return 神话故事
     */
    public List<Tale> getTalesFromTxt() {
        List<Tale> list = new ArrayList<>();

        File file = new File("d:" + File.separator + "tale.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            // 故事类型
            String type = "";

            // 上一行是否是故事类型
            boolean lastIsType = false;

            // 上一行是否是空白行
            boolean lastIsBlank = false;

            Tale tale = new Tale();

            String line;
            while ((line = reader.readLine()) != null) {
                // 故事类别
                if ("神话".equals(line) || "传说".equals(line) || "故事".equals(line)) {
                    type = line;
                    lastIsType = true;
                } else if (StringUtils.isBlank(line)) {
                    lastIsBlank = true;
                } else {
                    // 故事标题
                    if (lastIsType || lastIsBlank) {
                        tale.setTitle(line.replaceAll("。", "·"));
                        lastIsType = false;
                        lastIsBlank = false;
                    } else if (line.contains("口述人") || line.contains("搜集人") || line.contains("整理人") || line.contains("采集人")) {   // 记录人
                        if (line.length() > 9 && line.indexOf("：") != line.lastIndexOf("：")) {
                            // 去除记录人空格
                            line = line.trim();
                            int lastIndex = line.lastIndexOf("：");
                            // 将多个空格替换为一个
                            tale.setRecorder(line.substring(lastIndex + 1).replaceAll("\\s+", " "));

                            line = line.substring(0, lastIndex - 3).trim();

                            // 将多个空格替换为一个
                            tale.setNarrator(line.substring(line.indexOf("：") + 1).replaceAll("\\s+", " "));

                            // 设置类型
                            tale.setType(type);
                            tale.setCreateDate(new Date());
                            tale.setCreateBy("system");

                            // 替换掉内容里面的空格
                            if (tale.getContent() != null) {
                                tale.setContent(tale.getContent().replaceAll(" ", ""));
                            }
                            list.add(tale);
                            tale = new Tale();
                        } else {
                            System.out.println("问题记录人：" + line);
                        }
                    } else {        // 故事内容
                        if (StringUtils.isBlank(tale.getContent())) {
                            tale.setContent("　　" + line);
                        } else {
                            tale.setContent(tale.getContent() + "\n" + "　　" + line);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 从TXT文本导入歌谣
     * @return 歌谣内容
     */
    public List<Tale> getBalladFromTxt() {
        File file = new File("d:" + File.separator + "ballad.txt");

        List<Tale> list = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;

            // 故事类型
            String type = "歌谣";

            // 上一行是否是故事类型
            boolean lastIsType = false;

            // 上一行是否是空白行
            boolean lastIsBlank = false;

            Tale tale = new Tale();

            while ((line = reader.readLine()) != null) {
                // 故事类别
                if ("神话".equals(line) || "传说".equals(line) || "故事".equals(line)) {
                    type = line;
                    lastIsType = true;
                } else if (StringUtils.isBlank(line)) {
                    lastIsBlank = true;
                } else {
                    // 故事标题
                    if ((lastIsType || lastIsBlank) && StringUtils.isBlank(tale.getTitle())) {
                        tale.setTitle(line.replaceAll("。", "·"));
                        tale.setContent("");
                        lastIsType = false;
                        lastIsBlank = false;
                    } else if (line.contains("口述人") || line.contains("搜集人") || line.contains("整理人") || line.contains("采集人") || line.contains("演唱者")) {   // 记录人
                        if (line.length() > 9 && line.indexOf("：") != line.lastIndexOf("：")) {
                            // 去除记录人空格
                            line = line.trim();
                            int lastIndex = line.lastIndexOf("：");
                            // 将多个空格替换为一个
                            tale.setRecorder(line.substring(lastIndex + 1).replaceAll("\\s+", " "));

                            line = line.substring(0, lastIndex - 3).trim();

                            // 将多个空格替换为一个
                            tale.setNarrator(line.substring(line.indexOf("：") + 1).replaceAll("\\s+", " "));

                            // 设置类型
                            tale.setType(type);
                            tale.setCreateDate(new Date());
                            tale.setCreateBy("system");

                            // 替换掉内容里面的空格
                            if (tale.getContent() != null) {
                                tale.setContent(tale.getContent().replaceAll(" ", ""));
                            }
                            list.add(tale);
                            tale = new Tale();
                        } else {
                            System.out.println("问题记录人：" + line);
                        }
                    } else {        // 故事内容
                        if (StringUtils.isBlank(tale.getContent())) {
                            tale.setContent(line);
                        } else {
                            tale.setContent(tale.getContent() + "\n" + line);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 从TXT文本导入谚语
     * @return 谚语
     */
    public List<Tale> getProverbFromTxt() {
        File file = new File("d:" + File.separator + "proverb.txt");

        List<Tale> list = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;

            // 故事类型
            String type = "谚语";

            Tale tale = null;
            Date date = new Date();

            while ((line = reader.readLine()) != null) {
                // 故事类别
                if (line.contains("（") && line.contains("）") && line.contains("类")) {
                    if (tale != null) {
                        list.add(tale);
                    }
                    tale = new Tale();
                    tale.setType(type);
                    tale.setCreateBy("system");
                    tale.setCreateDate(date);
                    tale.setTitle(line);
                } else {
                    if (tale == null) {
                        continue;
                    }

                    if (StringUtils.isBlank(tale.getContent())) {
                        tale.setContent(line);
                    } else {
                        tale.setContent(tale.getContent() + "\n" + line);
                    }
                }
            }
            if (tale != null) {
                // 替换掉内容里面的空格
                if (tale.getContent() != null) {
                    tale.setContent(tale.getContent().replaceAll(" ", ""));
                }
                list.add(tale);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 描述
     * @return 描述
     */
    public List<Tale> getDescribe() {
        List<Tale> list = new ArrayList<>();
        Date date = new Date();
        Tale tale = new Tale();
        tale.setType("关于本书");
        tale.setTitle("编后");
        tale.setContent("　　为了进一步开创民间文学的新局面，保存人民的口头文学财富，在省、扬州市有关部门和我市市委、市人大、市政府、" +
                "市政协领导同志的关心支持下，经过三年的搜集整理工作，《兴化市卷本》现在和读者见面了。省“三民”办公室唐雨奇、" +
                "陈为史和我市刁俊、毕明、仲鲁连、陈康、潘少卿等同志，为《兴化市卷本》的编辑做了大量的工作，在此谨致谢忱。\n" +
                "　　编者\n" + "　　一九九三年十二月六日");
        tale.setCreateDate(date);
        tale.setCreateBy("system");
        list.add(tale);

        tale = new Tale();
        tale.setType("关于本书");
        tale.setTitle("各乡镇入选作品");
        tale.setContent("单　位　民间故事　歌　谣　谚　语\n" +
                "戴南镇　　11　　　　16　　237\n" +
                "张郭乡　　2　　　　　6\n" +
                "唐刘乡　　11　　　　11\n" +
                "顾庄乡　　　　　　　1\n" +
                "周庄镇　　1　　　　　6\n" +
                "边城镇　　3　　　　　　　　11\n" +
                "茅山镇　　1　　　　　2　　　9\n" +
                "陈堡乡　　1\n" +
                "竹泓镇　　9　　　　　2\n" +
                "沈伦乡　　2　　　　　1\n" +
                "林湖乡　　3　　　　　2\n" +
                "垛田乡　　1　　　　　1\n" +
                "安丰镇　　9　　　　　4\n" +
                "新垛乡　　　　　　　　　　　4\n" +
                "中圩乡　　1　　　　　4　　　21\n" +
                "钓鱼乡　　3\n" +
                "下圩乡　　1\n" +
                "大邹镇　　3　　　　　　　　20\n" +
                "海河乡　　2\n" +
                "海南乡　　1\n" +
                "昌荣镇　　5\n" +
                "大垛镇　　　　　　　2\n" +
                "荻垛乡　　　　　　　3\n" +
                "陶庄乡　　2\n" +
                "沙沟镇　　3　　　　　2\n" +
                "舜生镇　　　　　　　2\n" +
                "周奋乡　　6　　　　　1\n" +
                "中堡镇　　1\n" +
                "戴窑镇　　2　　　　　　　　50\n" +
                "合塔乡　　1　　　　　1\n" +
                "舍陈镇\n" +
                "徐扬乡　　　　　　　1　　　48\n" +
                "西鲍乡　　　　　　　　　　　8\n" +
                "临城乡　　　　　　　1　　　10\n" +
                "东鲍乡\n" +
                "荡朱乡　　7　　　　　6\n" +
                "东潭乡　　1　　　　　2\n" +
                "昭阳镇　　2\n" +
                "市　直　　95　　　　　90　　322\n" +
                "其　他　　12　　　　　6　　　8\n" +
                "合　计　　202　　　　173　　748");
        tale.setCreateDate(date);
        tale.setCreateBy("system");
        list.add(tale);

        tale = new Tale();
        tale.setType("关于本书");
        tale.setTitle("网页版说明");
        tale.setContent("　　这本书出版日期是1993年10月，当时兴化还属于扬州，由兴化市民间文学集成办公室历时3年采编完成。" +
                "很感谢前辈们的先见之明和辛勤付出，才让我们有机会读到这些精彩的民间故事和人生哲理，里面有很多兴化方言，读来倍感亲切。" +
                "童话故事不是安徒生的专利，我们的先辈们讲故事也是很有一手的。\n" +
                "　　这里的故事并不是全部，希望能有人继续收集这些民间故事，因为口口相传的故事都会随着时间慢慢消逝，" +
                "先辈们留下的瑰宝不能在我们这代埋没，我能做的也就这些了。都说一个人一辈子只要干成一件事，那就没白活，我已经打完收工了，拜了您个拜！\n" +
                "　　二零一七年九月九日");
        tale.setCreateDate(date);
        tale.setCreateBy("system");
        //list.add(tale);

        return list;
    }

    /**
     * 导入北欧童话
     * @return 北欧童话
     */
    public List<Tale> getNordicTales() {
        File file = new File("d:" + File.separator + "nordicTales.txt");

        List<Tale> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line = null;

            // 故事类型
            String type = "";

            Tale tale = null;
            Date date = new Date();

            boolean isTitle = false;

            while ((line = reader.readLine()) != null) {
                line = line.replaceAll(" ", "");

                // 如果当前行是空行，就认为前一个故事已经结束了，并且下一行是新故事的标题
                if(StringUtils.isBlank(line)) {
                    if (tale != null) {
                        list.add(tale);
                        tale = null;
                    }
                    isTitle = true;
                    continue;
                }

                // 故事类别
                if ("挪威".equals(line) || "瑞典".equals(line) || "芬兰".equals(line)
                        || "丹麦".equals(line) || "冰岛".equals(line) || "拉普兰岛".equals(line) || "法罗岛".equals(line)) {
                    type = line;
                } else {
                    // 如果isTitle为true，表示当前行是标题
                    if(isTitle) {
                        tale = new Tale();
                        tale.setType(type);
                        tale.setCreateBy("system");
                        tale.setCreateDate(date);
                        tale.setTitle(line);

                        isTitle = false;
                        continue;
                    }

                    // 将当前行作为故事内容添加到实体类里面
                    if (tale != null) {
                        if (StringUtils.isBlank(tale.getContent())) {
                            // 替换掉内容里面的空格
                            tale.setContent("　　" + line);
                        } else {
                            tale.setContent(tale.getContent() + "\n" + "　　" + line);
                        }
                    }
                }
            }

            // 如果txt文档最后一行不是空行，会漏掉最后一个故事
            if (tale != null) {
                list.add(tale);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 导入湖北荆州民间故事
     * @return 民间故事
     */
    public List<Tale> getHBJZ() {
        File file = new File("d:" + File.separator + "中国民间故事集成湖北卷_20220807.txt");

        List<Tale> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line = null;

            // 故事类型
            String type = "";

            Tale tale = null;
            Date date = new Date();

            boolean isTitle = false;

            Set<String> taleTypes = new HashSet<>();
            taleTypes.add("献给祖国和人民");
            taleTypes.add("神话");

            while ((line = reader.readLine()) != null) {
                line = line.replaceAll(" ", "");

                // 如果当前行是空行，就认为前一个故事已经结束了，并且下一行是新故事的标题
                if(StringUtils.isBlank(line)) {
                    if (tale != null) {
                        list.add(tale);
                        tale = null;
                    }
                    isTitle = true;
                    continue;
                }

                // 故事类别
                if (taleTypes.contains(line)) {
                    type = line;
                } else {
                    // 如果isTitle为true，表示当前行是标题
                    if(isTitle) {
                        tale = new Tale();
                        tale.setType(type);
                        tale.setCreateBy("system");
                        tale.setCreateDate(date);
                        tale.setTitle(line);

                        isTitle = false;
                        continue;
                    }

                    // 将当前行作为故事内容添加到实体类里面
                    if (tale != null) {
                        if (StringUtils.isBlank(tale.getContent())) {
                            // 替换掉内容里面的空格
                            tale.setContent("　　" + line);
                        } else {
                            tale.setContent(tale.getContent() + "\n" + "　　" + line);
                        }
                    }
                }
            }

            // 如果txt文档最后一行不是空行，会漏掉最后一个故事
            if (tale != null) {
                list.add(tale);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }


}
