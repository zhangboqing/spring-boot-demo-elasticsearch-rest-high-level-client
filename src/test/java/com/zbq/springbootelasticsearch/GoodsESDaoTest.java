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
     * 测试新增或更新
     */
    @Test
    public void saveOrUpdateTest() {
        List<GoodsESEntity> list = new ArrayList<>();
            list.add(GoodsESEntity.builder().goodsId(1L).goodsName("保时捷跑车V10").goodBrand("国际").goodsSpec("哈哈").goodsAccessoriesCode("所噶").goodsOriginalFactoryCode("212").groupData("保时捷跑车V10 国际 哈哈 所噶 212").build());
//        list.add(GoodsESEntity.builder().goodsId(1L).goodsName("2").goodBrand("1").goodsSpec("1").goodsAccessoriesCode("1").goodsOriginalFactoryCode("1").build());
//        list.add(GoodsESEntity.builder().goodsId(1L).goodsName("3").goodBrand("1").goodsSpec("1").goodsAccessoriesCode("1").goodsOriginalFactoryCode("1").build());

        goodsESDao.saveOrUpdate(list);
    }

    /**
     * 测试删除
     */
    @Test
    public void deleteTest() {
        goodsESDao.delete( GoodsESEntity.builder().goodsId(2L).build());
    }

    /**
     * 测试查询
     */
    @Test
    public void searchListTest() {
        List<GoodsESEntity> personList = goodsESDao.searchList();
        System.out.println(personList);
    }

    /**
     * 测试查询
     */
    @Test
    public void findList() {
        List<GoodsESEntity> personList = goodsESDao.findListByGroupData("所");
        System.out.println(personList);
    }
}