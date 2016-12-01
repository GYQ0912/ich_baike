package com.efeiyi.website.util;

import java.io.IOException;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class DicDirective extends BaseDirective implements
		TemplateDirectiveModel {
	@SuppressWarnings("rawtypes")
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars,
			TemplateDirectiveBody body) throws TemplateException, IOException {
		String id = getParam(params, "id");
		if (!"".equals(id)) {
			//id = Util.getDicText(id);
			if(null == id || id.equals("-1")) {
				id = "";
			}
		}
		env.getOut().write(id);
	}
}
