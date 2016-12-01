/**
 * Created by Administrator on 2016/8/26.
 */
function getHTMLByTemplate(templateId, data) {
    return $(doT.template($("#" + templateId).text())(data));
}