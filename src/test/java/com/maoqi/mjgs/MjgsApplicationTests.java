package com.maoqi.mjgs;

import com.maoqi.mjgs.pojo.dbbean.Tale;
import com.maoqi.mjgs.util.ImportNordicTales;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

//@SpringBootTest
class MjgsApplicationTests {

    @Test
    void contextLoads() {

        List<Tale> list = ImportNordicTales.getCommonTales();
        System.out.println("dd");

    }

}
