package com.efeiyi.website.controller;

import com.efeiyi.website.entity.IchItem;
import com.efeiyi.website.service.IchItemService;
import com.efeiyi.website.util.FreeMarker;
import com.efeiyi.website.util.Util;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * Created by Administrator on 2016/12/1.
 */
@Controller
@RequestMapping("ichItem")
public class IchItemController extends BaseController {

    @Autowired
    IchItemService ichItemService;

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
            freeMarker.createHtml(ichItem, "ichItem", "template");
        } catch (Exception e) {
            return;
        }
        responseSuccess(request, response);
    }

    @RequestMapping(value = "uploadImage", method = RequestMethod.POST)
    @ResponseBody
    public void uploadImage(@RequestParam(value="file")MultipartFile[] files,
                           HttpServletRequest request, HttpServletResponse response) {
        List<String> urlList = null;
        try {
            urlList = Util.uploadImage(files);
        } catch (Exception e) {
            e.printStackTrace();
        }
        responseSuccess(request, response);
    }

}
