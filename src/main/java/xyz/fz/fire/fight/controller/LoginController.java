package xyz.fz.fire.fight.controller;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    @RequestMapping(value = "/doLogin")
    @ResponseBody
    public String login(@RequestParam("userName") String userName, @RequestParam("passWord") String passWord) {
        UsernamePasswordToken token = new UsernamePasswordToken(userName, passWord);
        try {
            SecurityUtils.getSubject().login(token);
            return "{\"success\": true}";
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"用户名密码不正确\"}";
        }
    }

    @RequestMapping("/doLogout")
    @ResponseBody
    public String login() {
        try {
            SecurityUtils.getSubject().logout();
            return "{\"success\": true}";
        } catch (Exception e) {
            return "{\"success\": false, \"message\": \"当前账户尚未登录\"}";
        }
    }
}
