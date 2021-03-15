package tech.turl.community.controller;

import com.google.code.kaptcha.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import tech.turl.community.entity.User;
import tech.turl.community.service.UserService;
import tech.turl.community.util.CommunityConstant;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

@Controller
public class LoginController implements CommunityConstant {
    @Autowired
    private UserService userService;
    @Autowired
    private Producer kaptchaProducer;

    private static final Logger LOGGER =  LoggerFactory.getLogger(LoginController.class);


    @GetMapping("/register")
    public String getRegisterPage(){
        return "/site/register";
    }
    @GetMapping("/login")
    public String getLoginPage(){
        return "/site/login";
    }

    @GetMapping("/kaptcha")
    public void getKaptcha(HttpServletResponse response, HttpSession session){
        // 生成验证码
        String text = kaptchaProducer.createText();
        BufferedImage image = kaptchaProducer.createImage(text);

        // 将验证码存入session
        session.setAttribute("kaptcha", text);

        // 将图片输出给浏览器
        try {
            OutputStream os = response.getOutputStream();
            ImageIO.write(image, "png", os);
        } catch (IOException e) {
            LOGGER.error("响应验证码失败："+e.getMessage());
        }

    }

    @PostMapping("/register")
    public String register(Model model, User user){
        Map<String, Object> map = userService.register(user);
        if(map == null || map.isEmpty()){
            model.addAttribute("msg", "注册成功，我们已经向您的邮箱发送了一封激活邮件，请尽快激活！");
            model.addAttribute("target", "/");
            return "/site/operate-result";
        }else {
            model.addAttribute("usernameMsg", map.get("usernameMsg"));
            model.addAttribute("passwordMsg", map.get("passwordMsg"));
            model.addAttribute("emailMsg", map.get("emailMsg"));
            return "/site/register";
        }
    }


    // http://localhost:8080/community/activation/101/code
    @GetMapping("/activation/{userId}/{code}")
    public String activation(Model model, @PathVariable("userId") int userId, @PathVariable("code") String code){
        int result = userService.activation(userId, code);
        switch (result){
            case ACTIVATION_SUCCESS:{
                model.addAttribute("msg", "激活成功，您的账号已经可以正常使用了！");
                model.addAttribute("target", "/login");
                break;
            }
            case ACTIVATION_REPEAT:{
                model.addAttribute("msg", "无效操作，该账号已经激活过了！");
                model.addAttribute("target", "/");
                break;
            }
            case ACTIVATION_FAILED:{
                model.addAttribute("msg", "激活失败，您提供的激活码不正确！");
                model.addAttribute("target", "/");
                break;
            }
            default:
        }
        return "/site/operate-result";
    }


}
