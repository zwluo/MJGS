package com.maoqi.mjgs.dao.impl;

import com.maoqi.mjgs.dao.TaleDao;
import com.maoqi.mjgs.pojo.dbbean.Book;
import com.maoqi.mjgs.pojo.dbbean.Feedback;
import com.maoqi.mjgs.pojo.dbbean.Tale;
import com.maoqi.mjgs.pojo.dbbean.VisitLog;
import com.maoqi.mjgs.pojo.vo.BookColumnVO;
import com.maoqi.mjgs.pojo.vo.BookVO;
import com.maoqi.mjgs.pojo.vo.TaleVO;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class TaleDaoImpl implements TaleDao {

    private final EntityManager template;

    public TaleDaoImpl(EntityManager template) {
        this.template = template;
    }

    @Override
    public Tale getTaleById(Integer id) {
        return template.find(Tale.class, id);
    }

    @Override
    public List<TaleVO> getTalesByType(String type) {
        List<TaleVO> voList = new ArrayList<>();

        String sql = "select id,title from tale where type =?1 order by id ";
        Query query= template.createNativeQuery(sql);
        query.setParameter(1,type);

        List list = query.getResultList();
        if(list != null && list.size() > 0) {
            for(int i=0; i<list.size(); i++) {
                Object[] arr = (Object[])list.get(i);
                TaleVO taleVO = new TaleVO(arr[0], arr[1]);
                voList.add(taleVO);
            }
        }
        return voList;
    }

    @Override
    public void saveTales(List<Tale> list) {
        int j = 0;

        for (Tale item : list) {
            template.persist(item);

            j++;
            if (j % 50 == 0 || j == list.size()) {
                try {
                    template.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    template.clear();
                }
            }
        }
    }

    @Override
    public int getOnlineCounter() {
        String sql = "select count(*) " +
                "from ( " +
                "         select ip " +
                "         from visit_log " +
                "         where create_date > CURRENT_TIMESTAMP - INTERVAL 10 MINUTE " +
                "         group by ip) t";
        Query query= template.createNativeQuery(sql);

        int counter = Integer.parseInt(String.valueOf(query.getSingleResult()));
        return counter;
    }

    @Override
    public void saveVisitLog(VisitLog item) {
        template.persist(item);
        try {
            template.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            template.clear();
        }
    }

    @Override
    public void saveFeedback(Feedback item) {
        template.persist(item);
        try {
            template.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            template.clear();
        }
    }

    @Override
    public List<BookVO> getBookList() {
        List<BookVO> bookList = new ArrayList<>();
        String sql = "select id, title, url, cover from book order by id";
        Query query= template.createNativeQuery(sql);

        List list = query.getResultList();
        if(list != null && list.size() > 0) {
            for(int i=0; i<list.size(); i++) {
                Object[] arr = (Object[])list.get(i);
                BookVO bookVO = new BookVO(arr[0], arr[1], arr[2], arr[3]);
                bookList.add(bookVO);
            }
        }
        return bookList;
    }

    @Override
    public List<BookColumnVO> getColumnsByBookId(String bookId) {
        List<BookColumnVO> columnList = new ArrayList<>();
        String sql = "select id, title from book_column where book_id = ?1 order by id";
        Query query= template.createNativeQuery(sql);
        query.setParameter(1,bookId);

        List list = query.getResultList();
        if(list != null && list.size() > 0) {
            for(int i=0; i<list.size(); i++) {
                Object[] arr = (Object[])list.get(i);
                columnList.add(new BookColumnVO(arr[0], arr[1]));
            }
        }
        return columnList;
    }

    @Override
    public List<TaleVO> getCatalogueByColumnId(String columnId) {
        List<TaleVO> voList = new ArrayList<>();

        String sql = "select id,title from tale where column_id =?1 order by id ";
        Query query= template.createNativeQuery(sql);
        query.setParameter(1,columnId);

        List list = query.getResultList();
        if(list != null && list.size() > 0) {
            for(int i=0; i<list.size(); i++) {
                Object[] arr = (Object[])list.get(i);
                TaleVO taleVO = new TaleVO(arr[0], arr[1]);
                voList.add(taleVO);
            }
        }
        return voList;
    }

}
