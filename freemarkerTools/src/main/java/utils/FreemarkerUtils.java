package utils;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import util.ProValueUtil;
import util.Tools;

import java.io.*;
import java.util.Map;

public class FreemarkerUtils {
    //模板文件的所在的home目录
    private static final String rootFile = ProValueUtil.loadProperties("freemarker.properties").getProperty("tempFilePath");
    //生成的文件目录
    private static final String fileDes = ProValueUtil.loadProperties("freemarker.properties").getProperty("fileDesPath");

    /***
     * 获取模板
     * @param templateName   home目录下的模板名字
     * @return
     * @throws IOException
     */
    public static Template geTemplate(String templateName) throws IOException {
        Configuration configuration = new Configuration();
        configuration.setDirectoryForTemplateLoading(new File(rootFile));
        configuration.setObjectWrapper(new DefaultObjectWrapper());
        configuration.setDefaultEncoding("UTF-8");
        Template template = configuration.getTemplate(templateName);
        return template;
    }


    /***
     * 给模板填充数据，生成文件
     * @param template 模板obj
     * @param data  需要填充的数据
     * @param desPath 文件保存路径
     * @throws IOException
     * @throws TemplateException
     */
    public static void filePorducer(Template template,Map<String, String> data, String desPath) throws IOException, TemplateException {

        Writer writer = Tools.notEmpty(desPath) ? new OutputStreamWriter(new FileOutputStream(desPath), "utf-8") : new OutputStreamWriter(new FileOutputStream(fileDes), "utf-8");
        template.process(data, writer);
    }

}
