package io.zeroparadigm.liquid.gateway.controller;


import com.alibaba.fastjson.JSONObject;
import io.swagger.util.Json;
import io.zeroparadigm.liquid.common.api.core.UserAuthService;
import io.zeroparadigm.liquid.common.bo.UserBO;
import io.zeroparadigm.liquid.common.enums.ServiceStatus;
import io.zeroparadigm.liquid.gateway.dto.LoginCredentials;
import io.zeroparadigm.liquid.gateway.exception.InvalidCredentialFieldException;
import io.zeroparadigm.liquid.gateway.jwt.JwtUtils;
import io.zeroparadigm.liquid.gateway.service.AuthService;
import io.zeroparadigm.liquid.gateway.dto.Result;
import javax.annotation.PostConstruct;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.PostMapping;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("/auth")
@Slf4j
@RestController
public class AuthTest {
    @Autowired
    private AuthService authService;

    @DubboReference
    private UserAuthService userAuthService;

    @Autowired
    private JwtUtils jwtUtils;

    @RequestMapping("/login")
    public Result<String> login(@RequestParam(value = "username") String userName, @RequestParam(value = "password") String password, @RequestParam(value = "remember") Boolean remember) throws Exception {
        LoginCredentials credentials = new LoginCredentials();
        credentials.password(userName, password);
        try {
            authService.login(credentials);
            UserBO user = userAuthService.findByNameOrMail(userName);
            Result<String> result = Result.success();
            result.setData(jwtUtils.createTokenFor(user.getId(), remember));
            return result;
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
        Result result = Result.success();
        result.setData(name);
        return result;
    }
}

