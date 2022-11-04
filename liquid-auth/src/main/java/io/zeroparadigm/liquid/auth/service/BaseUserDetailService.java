package io.zeroparadigm.liquid.auth.service;


import io.zeroparadigm.liquid.auth.client.bo.CheckPasswordBO;
import io.zeroparadigm.liquid.auth.client.dto.CheckPasswordDTO;
import io.zeroparadigm.liquid.common.enums.ServiceStatus;
import io.zeroparadigm.liquid.auth.client.ResourceServerClient;
import io.zeroparadigm.liquid.auth.entity.Result;
import java.util.ArrayList;
import java.util.List;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author jiangqiao
 */
@Service
public class BaseUserDetailService implements UserDetailsService {

    ResourceServerClient resourceServerClient;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CheckPasswordDTO checkPassWordDTO = CheckPasswordDTO.builder().userName(username).build();
        Result<CheckPasswordBO> responseResult = resourceServerClient.checkPassWord(checkPassWordDTO);
        CheckPasswordBO checkPassWordBO = responseResult.getData();
        List<GrantedAuthority> authorities = new ArrayList<>();
        // Returns a User with user permission information
        return new User(checkPassWordBO.getUserName(),
            checkPassWordBO.getPassWord(), true, true, true, true, authorities);
    }
}

