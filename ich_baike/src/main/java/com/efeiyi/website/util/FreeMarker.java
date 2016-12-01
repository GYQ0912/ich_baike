package com.efeiyi.website.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.efeiyi.website.common.Constants;
import com.efeiyi.website.entity.Entity;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;

/**
 * FreeMarker工具类
 */
public class FreeMarker {

    private ServletContext servletContext;

    public FreeMarker(HttpServletRequest request) {
        servletContext = request.getServletContext();
    }

    /**
     * 生成静态页面主方法 默认编码为UTF-8
     *
     * @param entity
     * @throws Exception
     */

    public void createHTML(Entity entity, String dataName, String templateName)
            throws Exception {
        try {
            Method setIdMethod = entity.getClass().getMethod("getId");
            String id = (String) setIdMethod.invoke(entity);
            Map<String, Object> entityMap = new HashMap<String, Object>();
            entityMap.put(dataName, entity);
            String directory = Util.buildDirectory(id);// /9c/69/
            createHTML(entityMap, templateName + Constants.TEMPLATE_FORMAT,
                    Constants.CREATE_HTML_PATH + templateName + directory + id + Constants.CREATE_FORMAT);
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     *
     * @param entityList
     *            存放实体类的集合
     * @param dataName
     * @param templateName
     * @param id
     * @throws Exception
     */
    public void createHTML(List<Entity> entityList, String dataName,
                           String templateName, String id) throws Exception {
        Map<String, Entity> entityListMap = new HashMap<String, Entity>();
        for(Entity entity:entityList){
            String entityDataName = entity.getClass().getSimpleName();
            entityListMap.put(entityDataName, entity);
        }
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put(dataName, entityListMap);
        String directory = Util.buildDirectory(id);
        createHTML(dataMap, templateName + Constants.TEMPLATE_FORMAT, Constants.CREATE_HTML_PATH
                + templateName + directory + id + Constants.CREATE_FORMAT);
    }

    public void createHTML(HashMap<String, Entity> entityMap, String dataName,
                           String templateName, String id) throws Exception {
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put(dataName, entityMap);
        String directory = Util.buildDirectory(id);
        createHTML(dataMap, templateName + Constants.TEMPLATE_FORMAT, Constants.CREATE_HTML_PATH
                + templateName + directory + id + Constants.CREATE_FORMAT);
    }

    /**
     * 生成静态页面主方法
     *
     *            ServletContext
     * @param data
     *            一个Map的数据结果集
     * @param templateName
     *            ftl(html)模版
     *
     *            ftl(html)模版编码
     * @param htmlPath
     *            生成静态页面的路径
     * @throws IOException
     * @throws TemplateException
     */
    public void createHTML(Map<String, Object> data, String templateName, String htmlPath) throws Exception {
        Configuration freemarkerCfg = initCfg();
        freemarkerCfg.setDirectoryForTemplateLoading(new File(Constants.TEMPLATE_PATH));
        // 指定模版路径
        Template template = freemarkerCfg.getTemplate(templateName, Constants.ENCODE_UTF8);
        // 静态页面路径
        File htmlFile = new File(htmlPath);
        if (!htmlFile.getParentFile().exists()) {
            htmlFile.getParentFile().mkdirs();
        }
        Writer writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(htmlFile), Constants.ENCODE_UTF8));
        try {
            template.process(data, writer);
        } catch (Exception e) {
            throw e;
        } finally {
            writer.flush();
            writer.close();
        }
    }

    /**
     * 初始化freemarker Configuration配置
     *
     * @return
     * @throws TemplateModelException
     */
    public Configuration initCfg()
            throws TemplateModelException {
        Configuration freemarkerCfg = null;
        // 判断context中是否有freemarkerCfg属性
        if (servletContext.getAttribute("freemarkerCfg") != null) {
            freemarkerCfg = (Configuration) servletContext
                    .getAttribute("freemarkerCfg");
        } else {
            freemarkerCfg = new Configuration(Configuration.VERSION_2_3_25);
            // 加载模版
            freemarkerCfg.setServletContextForTemplateLoading(servletContext, "/");
            freemarkerCfg.setEncoding(Locale.getDefault(), Constants.ENCODE_UTF8);
            // 加载自定义标签
            //freemarkerCfg.setSharedVariable("dic", new DicDirective());
            //freemarkerCfg.setSharedVariable("city", new CityDirective());
            //freemarkerCfg.setSharedVariable("line", new BusinessLineDirective());
        }
        return freemarkerCfg;
    }

}