package io.zeroparadigm.liquid.core.service.impl;

import io.zeroparadigm.liquid.common.api.core.GitMetaService;
import io.zeroparadigm.liquid.core.dao.mapper.RepoMapper;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

@DubboService
public class GitMetaServiceImpl implements GitMetaService {

    @Autowired
    RepoMapper repoMapper;

    @Override
    public void recordContributor(String owner, String repo, String contLogin) {
        var repoE = repoMapper.findByOwnerAndName(owner, repo);
        if (repoE != null) {
            repoMapper.addContributor(repoE.getId(), contLogin);
        }
    }
}
