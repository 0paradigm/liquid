package io.zeroparadigm.liquid.auth.client;

import io.zeroparadigm.liquid.auth.client.bo.CheckPasswordBO;
import io.zeroparadigm.liquid.auth.client.dto.CheckPasswordDTO;
import io.zeroparadigm.liquid.auth.entity.Result;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author jiangqiao
 */
public interface ResourceServerClient {

    /**
     * 登录密码验证接口
     *
     * @param checkPassWordDTO
     * @return
     */
    @PostMapping("/auth/checkPassWord")
    public Result<CheckPasswordBO> checkPassWord(CheckPasswordDTO checkPassWordDTO);
}
