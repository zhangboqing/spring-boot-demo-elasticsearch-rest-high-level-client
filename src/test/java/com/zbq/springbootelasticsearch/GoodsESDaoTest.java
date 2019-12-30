package com.zbq.springbootelasticsearch;

import com.zbq.springbootelasticsearch.common.elasticsearch.base.ESPageResult;
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
        list.add(GoodsESEntity.builder().goodsId(1L).goodsName("保时捷跑车V10").goodBrand("国际1").goodsSpec("哈哈1").goodsAccessoriesCode("所噶1").goodsOriginalFactoryCode("2121").groupData("保时捷跑车V10 国际1 哈哈1 所噶1 2121").build());
        list.add(GoodsESEntity.builder().goodsId(2L).goodsName("保时捷跑车V10").goodBrand("国际2").goodsSpec("哈哈2").goodsAccessoriesCode("所噶2").goodsOriginalFactoryCode("2122").groupData("保时捷跑车V20 国际2 哈哈2 所噶2 2122").build());
        list.add(GoodsESEntity.builder().goodsId(3L).goodsName("保时捷跑车V20").goodBrand("国际1").goodsSpec("哈哈3").goodsAccessoriesCode("所噶3").goodsOriginalFactoryCode("2123").groupData("保时捷跑车V30 国际3 哈哈3 所噶3 2123").build());
        list.add(GoodsESEntity.builder().goodsId(4L).goodsName("保时捷跑车V20").goodBrand("国际2").goodsSpec("哈哈4").goodsAccessoriesCode("所噶4").goodsOriginalFactoryCode("2124").groupData("保时捷跑车V40 国际4 哈哈4 所噶4 2124").build());
        list.add(GoodsESEntity.builder().goodsId(5L).goodsName("保时捷跑车V30").goodBrand("国际1").goodsSpec("哈哈5").goodsAccessoriesCode("所噶5").goodsOriginalFactoryCode("2125").groupData("保时捷跑车V50 国际5 哈哈5 所噶5 2125").build());
        list.add(GoodsESEntity.builder().goodsId(6L).goodsName("保时捷跑车V40").goodBrand("国际2").goodsSpec("哈哈6").goodsAccessoriesCode("所噶6").goodsOriginalFactoryCode("2126").groupData("保时捷跑车V60 国际6 哈哈6 所噶6 2126").build());

        goodsESDao.saveOrUpdate(list);
    }

    /**
     * 测试删除
     */
    @Test
    public void deleteTest() {
        goodsESDao.delete(GoodsESEntity.builder().goodsId(2L).build());
    }

    /**
     * 测试分词查询
     */
    @Test
    public void searchListByAnalysisTest() {
        List<GoodsESEntity> goodsESEntityList = goodsESDao.findListByAnalysisForGroupData("所");
        System.out.println(goodsESEntityList);
    }

    /**
     * 测试等值查询
     */
    @Test
    public void searchListByEqTest() {
        List<GoodsESEntity> goodsESEntityList = goodsESDao.findListByEq();
        System.out.println(goodsESEntityList);
    }

    /**
     * 测试like查询
     */
    @Test
    public void searchListByLikeTest() {
        List<GoodsESEntity> goodsESEntityList = goodsESDao.findListByLike();
        System.out.println(goodsESEntityList);
    }

    /**
     * 测试分页和排序
     */
    @Test
    public void findList() {

        ESPageResult esPageResult = goodsESDao.findList("所");
        System.out.println(esPageResult);

    }
}