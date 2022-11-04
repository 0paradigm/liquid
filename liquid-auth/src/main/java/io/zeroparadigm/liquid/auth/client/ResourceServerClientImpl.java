package io.zeroparadigm.liquid.auth.client;

import io.zeroparadigm.liquid.auth.client.bo.CheckPasswordBO;
import io.zeroparadigm.liquid.auth.client.dto.CheckPasswordDTO;
import io.zeroparadigm.liquid.auth.entity.Result;
import org.apache.dubbo.config.annotation.DubboService;

@DubboService
public class ResourceServerClientImpl implements ResourceServerClient{

    /**
     * Login password verification
     *
     * @param checkPassWordDTO
     * @return
     */
    @Override
    public Result<CheckPasswordBO> checkPassWord(CheckPasswordDTO checkPassWordDTO) {
        return null;
    }
}
