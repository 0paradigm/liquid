package io.zeroparadigm.liquid.auth.controller;


import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.zeroparadigm.liquid.common.api.core.UserAuthService;
import io.zeroparadigm.liquid.common.bo.UserBO;
import io.zeroparadigm.liquid.common.enums.ServiceStatus;
import io.zeroparadigm.liquid.auth.dto.LoginCredentials;
import io.zeroparadigm.liquid.auth.exception.InvalidCredentialFieldException;
import io.zeroparadigm.liquid.auth.jwt.JwtUtils;
import io.zeroparadigm.liquid.auth.service.AuthService;
import io.zeroparadigm.liquid.auth.dto.Result;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.PostMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "登录模块",tags = {"登录模块"})
@Slf4j
@RestController
public class AuthTest {
    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtils jwtUtils;

    @RequestMapping("/login")
    public Result<String> login(@RequestParam(value = "username") String userName, @RequestParam(value = "password") String password, @RequestParam(value = "remember") Boolean remember) throws Exception {
        LoginCredentials credentials = new LoginCredentials();
        credentials.password(userName, password);
        try {
            Subject subject = authService.login(credentials);
            return (Result<String>) Result.success();
        } catch (UnknownAccountException e) {
            return Result.error(ServiceStatus.ACCOUNT_NOT_FOUND,
                ServiceStatus.ACCOUNT_NOT_FOUND);
        } catch (IncorrectCredentialsException e) {
            return Result.error(ServiceStatus.INCORRECT_CREDENTIAL,
                ServiceStatus.INCORRECT_CREDENTIAL);
        }
    }


    @PostMapping("/whoami")
    public Result<JSONObject> whoami(){
        Subject subject = SecurityUtils.getSubject();
        Result<JSONObject> result = Result.success();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("result", subject.getPrincipal());
        jsonObject.put("Token", jwtUtils.createTokenFor((Integer) subject.getPrincipal(), true));
        result.setData(jsonObject);
        return result;
    }

    @GetMapping("/hello/{name}")
    public Result<String> hello(@PathVariable String name) {
        Result<String> result = Result.success();
        result.setData(name);
        return result;
    }
}

