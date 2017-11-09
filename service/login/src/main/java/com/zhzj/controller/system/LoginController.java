package com.zhzj.controller.system;

import com.google.gson.Gson;
import com.zhzj.BaseController;
import com.zhzj.service.UserService;
import com.zhzj.util.DateUtil;
import com.zhzj.util.JSONResultUtil;
import com.zhzj.util.PageData;
import com.zhzj.util.TokenProcessor;
import com.zhzj.utils.MD5;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;
import java.util.List;



/**
 * 登陆实现类
 *
 * @author 张京辉
 * 创建时间:2015/12/2
 */
@Controller
public class LoginController extends BaseController {
    private static Map<String, String> codeMap = new HashMap<String, String>();

    //存取用户登录信息
    public static Map<String, Object> userLoginInfo;
    //存储用户登录姓名，key:用户登录姓名 value：用户登录唯一token
    public static Map<String, String> loginUserName;


    public LoginController() {
        userLoginInfo = new HashMap<String, Object>();
        loginUserName = new HashMap<String, String>();
    }

    @Resource(name = "userService")
    private UserService userService;


    @RequestMapping(value = "/verLogin", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String verLogin(HttpServletRequest request) throws Exception {

        PageData pd = new PageData();
        pd = this.getPageData();
        String username = pd.getString("username");
        String password = pd.getString("password");
        //验证输入是否为空
        if (username == null || password == null || username.trim().equals("") || password.trim().equals("")) {
            return JSONResultUtil.getResultJSON(JSONResultUtil.ERR_NUM, "参数不能为空").toString();
        }
        //pd.put("password", MD5.md5(pd.getString("password")));
        // List<PageData> userByNameAndPwd = (List<PageData>) this.userService.getUserByNameAndPwd(pd);


//        if (userByNameAndPwd == null || userByNameAndPwd.get(0).getString("USERNAME") == null) {
//            return JSONResultUtil.getResultJSON(JSONResultUtil.ERR_NUM, "用户名或密码错误").toString();
//        }

        String result = JSONResultUtil.getResultJSON(JSONResultUtil.SUCCESS_NUM, JSONResultUtil.SUCCESS_MSG).toString();
        return result;
    }

    /**
     * 请求登录，验证用户
     *
     * @return
     * @throws Exception
     */

    @RequestMapping(value = "/toLogin")
    @ResponseBody
    public String login(HttpServletRequest req) throws Exception {
        String remoteAddr = req.getRemoteAddr();
        PageData pd = new PageData();
        pd = this.getPageData();
        String username = pd.getString("username");
        String password = pd.getString("password");
        String code = pd.getString("code");
        //验证输入是否为空
        if (username == null || password == null || code == null || username.trim().equals("") || password.trim().equals("") || code.trim().equals("")) {
            return JSONResultUtil.getResultJSON(JSONResultUtil.ERR_NUM, "参数不能为空").toString();
        }
        //验证码输入校验
        if (!(code.equalsIgnoreCase(codeMap.get(remoteAddr).toString()))) {
            return JSONResultUtil.getResultJSON(JSONResultUtil.ERR_NUM, "验证码错误").toString();
        }
        if (loginUserName.containsKey(username)) {
            String userToken = loginUserName.get(username);
            userLoginInfo.remove(userToken);
        }

        pd.put("password", MD5.md5(pd.getString("password")));
        List<PageData> userByNameAndPwd = (List<PageData>) this.userService.getUserByNameAndPwd(pd);
        //  组织数据，用户信息和用户权限
        PageData userInfo = new PageData();
        List<PageData> userRightsInfo = new ArrayList<>();
        for (int i = 0; i < userByNameAndPwd.size(); i++) {
            if (i == 0) {
                userInfo.put("id", userByNameAndPwd.get(i).get("id"));
                userInfo.put("username", userByNameAndPwd.get(i).get("username"));
                userInfo.put("password", userByNameAndPwd.get(i).get("password"));
                userInfo.put("start_avali_time", userByNameAndPwd.get(i).get("start_avali_time"));
                userInfo.put("end_avali_time", userByNameAndPwd.get(i).get("end_avali_time"));
                userInfo.put("is_lock", userByNameAndPwd.get(i).get("is_lock"));
                userInfo.put("is_approve", userByNameAndPwd.get(i).get("is_approve"));
                userInfo.put("map_username", "user_test");
                userInfo.put("map_pass", "123456");

            }

            PageData temp = new PageData();
            temp.put("role_id", userByNameAndPwd.get(i).get("role_id"));
            temp.put("right_code", userByNameAndPwd.get(i).get("right_code"));
            temp.put("right_name", userByNameAndPwd.get(i).get("right_name"));
            temp.put("right_type", userByNameAndPwd.get(i).get("right_type"));
            temp.put("right_time", userByNameAndPwd.get(i).get("right_time"));
            temp.put("is_mutil", userByNameAndPwd.get(i).get("is_mutil"));
            userRightsInfo.add(temp);
        }

        userInfo.put("rights", userRightsInfo);
        //用户密码错误
        if (userByNameAndPwd == null || userByNameAndPwd.size() == 0 || userByNameAndPwd.get(0).getString("username") == null) {
            return JSONResultUtil.getResultJSON(JSONResultUtil.ERR_NUM, "用户名或密码错误").toString();
        }
        //判断用户是否通过审核
        if ("0".equals(userInfo.get("is_approve").toString())) {
            return JSONResultUtil.getResultJSON(JSONResultUtil.ERR_NUM, "账户正在审核中").toString();
        }
        //判断用户账户是否锁定。
        if ("0".equals(userInfo.get("is_lock").toString())) {
            return JSONResultUtil.getResultJSON(JSONResultUtil.ERR_NUM, "账户已经锁定").toString();
        }
        boolean end_avali_time = DateUtil.compareDate(userInfo.get("end_avali_time").toString(), DateUtil.getTime());


        //假如用户账户不在有效期内，修改用户账户状态
        if (userInfo.get("end_avali_time") == null ||
                userInfo.get("start_avali_time") == null ||
                !(DateUtil.compareDate(userInfo.get("end_avali_time").toString(), DateUtil.getTime()) &&
                        DateUtil.compareDate(DateUtil.getTime(), userInfo.get("start_avali_time").toString()))) {
            PageData updateUserLockStatus = new PageData();
            updateUserLockStatus.put("is_lock", "0");
            updateUserLockStatus.put("id", userInfo.get("id"));
            return JSONResultUtil.getResultJSON(JSONResultUtil.ERR_NUM, "账户账期不在有效期内").toString();

        }
        JSONObject resultJSON = JSONResultUtil.getResultJSON(JSONResultUtil.SUCCESS_NUM, JSONResultUtil.SUCCESS_MSG);
        //将用户的权限添加Map中
        HttpSession session = req.getSession(false);

        String token = TokenProcessor.generateToken(username, true);
        session.setAttribute(token, userInfo);
        userLoginInfo.put(token, userInfo);
        loginUserName.put(username, token);
        userInfo.put("token", token);
        resultJSON.put("userInfo", userInfo);
        return resultJSON.toString();
    }
//    }


    /***
     * 注销登录
     * @param req
     * @return
     */
    @RequestMapping(value = "/loginOut")
    @ResponseBody
    public String loginOut(HttpServletRequest req, HttpSession session) {
        PageData pd = this.getPageData();
        String userToken = loginUserName.get(pd.getString("username"));
        userLoginInfo.remove(userToken);
        // logService.addLog(InterfaceUtil.getLogMap(Integer.parseInt(userInfo.get("id").toString()), InterfaceUtil.TOLOGIN, InterfaceUtil.SUCCESS, pd.toString(), resultJSON.toString(), 0));
        session.removeAttribute(userToken);
        PageData msg = new PageData();
        msg.put("stat", "0000");
        msg.put("msg", "登出成功");
        return new Gson().toJson(msg);
    }



    /**
     * 用户申请
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/toRegister")
    @ResponseBody
    public String register(HttpServletRequest req) throws Exception {
        String remoteAddr = req.getRemoteAddr();
        PageData pd = new PageData();
        pd = this.getPageData();
        String code = pd.getString("code");
        //验证输入是否为空
        if (code.trim().equals("")) {
            return JSONResultUtil.getResultJSON(JSONResultUtil.ERR_NUM, "验证码不能为空").toString();
        }

        if (!(code.equalsIgnoreCase(codeMap.get(remoteAddr).toString()))) {
            String error = JSONResultUtil.getResultJSON(JSONResultUtil.ERR_NUM, "验证码错误").toString();
            return error;
        } else if (this.userService.findByUsername(pd) != null) {
            String error = JSONResultUtil.getResultJSON(JSONResultUtil.ERR_NUM, "用户名已注册").toString();
            return error;
        } else {
            pd.put("password", MD5.md5(pd.getString("password")));
            pd.put("create_time", new Date());
            pd.put("update_time", new Date());
            this.userService.addUser(pd);
            String result = JSONResultUtil.getResultJSON(JSONResultUtil.SUCCESS_NUM, JSONResultUtil.SUCCESS_MSG).toString();
            return result;
        }
    }

    /**
     * 进入tab标签
     *
     * @return
     */
    @RequestMapping(value = "/tab")
    public String tab() {
        return "system/index/tab";
    }

    private int width = 90;//定义图片的width
    private int height = 20;//定义图片的height
    private int codeCount = 4;//定义图片上显示验证码的个数
    private int xx = 15;
    private int fontHeight = 18;
    private int codeY = 16;
    char[] codeSequence = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
            'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    @RequestMapping("/code")
    public void getCode(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        // 定义图像buffer
        BufferedImage buffImg = new BufferedImage(width, height,
                BufferedImage.TYPE_INT_RGB);
//		Graphics2D gd = buffImg.createGraphics();
        //Graphics2D gd = (Graphics2D) buffImg.getGraphics();
        Graphics gd = buffImg.getGraphics();
        // 创建一个随机数生成器类
        Random random = new Random();
        // 将图像填充为白色
        gd.setColor(Color.WHITE);
        gd.fillRect(0, 0, width, height);

        // 创建字体，字体的大小应该根据图片的高度来定。
        Font font = new Font("Fixedsys", Font.BOLD, fontHeight);
        // 设置字体。
        gd.setFont(font);

        // 画边框。
        gd.setColor(Color.BLACK);
        gd.drawRect(0, 0, width - 1, height - 1);

        // 随机产生40条干扰线，使图象中的认证码不易被其它程序探测到。
        gd.setColor(Color.BLACK);
//        for (int i = 0; i < 40; i++) {
//            int x = random.nextInt(width);
//            int y = random.nextInt(height);
//            int xl = random.nextInt(12);
//            int yl = random.nextInt(12);
//            gd.drawLine(x, y, x + xl, y + yl);
//        }

        // randomCode用于保存随机产生的验证码，以便用户登录后进行验证。
        StringBuffer randomCode = new StringBuffer();
        int red = 0, green = 0, blue = 0;

        // 随机产生codeCount数字的验证码。
        for (int i = 0; i < codeCount; i++) {
            // 得到随机产生的验证码数字。
            String code = String.valueOf(codeSequence[random.nextInt(36)]);
            // 产生随机的颜色分量来构造颜色值，这样输出的每位数字的颜色值都将不同。
            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);

            // 用随机产生的颜色将验证码绘制到图像中。
            gd.setColor(new Color(red, green, blue));
            gd.drawString(code, (i + 1) * xx, codeY);

            // 将产生的四个随机数组合在一起。
            randomCode.append(code);
        }
        // 将四位数字的验证码保存到Session中。
        HttpSession session = req.getSession();
        session.setAttribute("code", randomCode.toString());
        codeMap.put(req.getRemoteAddr(), randomCode.toString());

        // 禁止图像缓存。
        resp.setHeader("Pragma", "no-cache");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setDateHeader("Expires", 0);

        resp.setContentType("image/jpeg");

        // 将图像输出到Servlet输出流中。
        ServletOutputStream sos = resp.getOutputStream();
        ImageIO.write(buffImg, "jpeg", sos);
        sos.close();
    }
}
