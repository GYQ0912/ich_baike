package com.efeiyi.website.controller;

import com.efeiyi.website.entity.IchItem;
import com.efeiyi.website.util.FreeMarker;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Administrator on 2016/12/1.
 */
@Controller
@RequestMapping("ichItem")
public class IchItemController extends BaseController {

    @RequestMapping("testFreeMarker")
    public void testFreeMarker(HttpServletRequest request, HttpServletResponse response) {
        IchItem ichItem = new IchItem();
        ichItem.setId("fsetewsfe");
        ichItem.setCnName("张三");
        ichItem.setDeclareBatch("2016第一批");
        ichItem.setEditRank(1);
        ichItem.setEnName("zhangsan");

        FreeMarker freeMarker = new FreeMarker(request);
        try {
            freeMarker.createHTML(ichItem, "ichItem", "template");
        } catch (Exception e) {
            e.printStackTrace();
        }
        responseSuccess(request, response);
    }
}
