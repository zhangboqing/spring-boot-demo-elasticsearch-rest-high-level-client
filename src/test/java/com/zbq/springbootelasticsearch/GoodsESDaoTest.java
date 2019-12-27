package com.zbq.springbootelasticsearch;

import com.zbq.springbootelasticsearch.dao.GoodsESDao;
import com.zbq.springbootelasticsearch.model.GoodsESEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class GoodsESDaoTest {

    @Autowired
    private GoodsESDao goodsESDao;


    /**
     * 测试新增
     */
    @Test
    public void insertTest() {
        List<GoodsESEntity> list = new ArrayList<>();
        list.add(GoodsESEntity.builder().goodsId(1L).goodsName("1").goodBrand("1").goodsSpec("1").goodsAccessoriesCode("1").goodsOriginalFactoryCode("1").build());
        list.add(GoodsESEntity.builder().goodsId(1L).goodsName("2").goodBrand("1").goodsSpec("1").goodsAccessoriesCode("1").goodsOriginalFactoryCode("1").build());
        list.add(GoodsESEntity.builder().goodsId(1L).goodsName("3").goodBrand("1").goodsSpec("1").goodsAccessoriesCode("1").goodsOriginalFactoryCode("1").build());

        goodsESDao.insert(list);
    }

    /**
     * 测试更新
     */
    @Test
    public void updateTest() {
        GoodsESEntity person = GoodsESEntity.builder().goodsId(1L).goodsName("2").goodBrand("1").goodsSpec("1").goodsAccessoriesCode("1").goodsOriginalFactoryCode("1").build();
        List<GoodsESEntity> list = new ArrayList<>();
        list.add(person);
        goodsESDao.update( list);
    }

    /**
     * 测试删除
     */
    @Test
    public void deleteTest() {
        goodsESDao.delete( GoodsESEntity.builder().goodsId(1L).build());
    }

    /**
     * 测试查询
     */
    @Test
    public void searchListTest() {
        List<GoodsESEntity> personList = goodsESDao.searchList();
        System.out.println(personList);
    }
}