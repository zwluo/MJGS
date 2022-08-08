package com.maoqi.mjgs.util;

import com.maoqi.mjgs.pojo.dbbean.Tale;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ImportNordicTales {

    public static void main(String[] args) {
        File file = new File("d:" + File.separator + "nordicTales.txt");

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line = null;

            // 故事类型
            String type = "";

            List<Tale> list = new ArrayList<>();
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
                if ("挪威".equals(line) || "丹麦".equals(line) || "瑞典".equals(line)) {
                    type = line;
                    continue;
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
                    if (StringUtils.isBlank(tale.getContent())) {
                        // 替换掉内容里面的空格
                        tale.setContent(line);
                    } else {
                        tale.setContent(tale.getContent() + "\n" + line);
                    }
                }
            }

            // 如果txt文档最后一行不是空行，会漏掉最后一个故事
            if (tale != null) {
                list.add(tale);
                tale = null;
            }

            System.out.println(list);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 导入北欧童话
     * @return 北欧童话
     */
    public static List<Tale> getCommonTales() {
        File file = new File("d:" + File.separator + "中国民间故事集成湖北卷.txt");

        // 类别
        List<String> typeList = new ArrayList<>();
        typeList.add("神话");

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
                if (typeList.contains(line)) {
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
