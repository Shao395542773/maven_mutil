
import freemarker.template.Template;
import freemarker.template.TemplateException;
import util.LoggerTools;
import utils.FreemarkerUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FreemakerDemo {
    public static void main(String[] args) throws IOException, TemplateException {

        Template template = FreemarkerUtils.geTemplate("freemakerdemo");
        Map<String,String > map =new HashMap<>();
        map.put("name","liushaobo");
        FreemarkerUtils.filePorducer(template,map,null);
        LoggerTools.info("生成成功");
    }
}
