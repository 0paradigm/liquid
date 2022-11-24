package io.zeroparadigm.liquid.git.pojo;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

import java.io.File;
import java.nio.file.Files;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Info of the latest commit on a file/directory.
 *
 * @author hezean
 */
@Slf4j
@Data
@ToString
public class LatestCommitInfo {

    /**
     * Name of file / dir.
     */
    String name;

    /**
     * Whether the target is a directory.
     */
    Boolean isDir;

    /**
     * Latest commit sha.
     */
    String sha;

    /**
     * Latest commit timestamp.
     */
    Integer timestamp;

    /**
     * Latest commit short message.
     */
    String message;

    /**
     * Fetches the latest commit on a file/dir.
     *
     * @param checkOuted the jGit object, should be correctly check-outed.
     * @param rel        relative path from repo root.
     */
    public LatestCommitInfo(Git checkOuted, File rel) {
        File abs = new File(checkOuted.getRepository().getDirectory().getParent(), String.valueOf(rel));
        name = rel.getName();
        isDir = abs.isDirectory();

        AtomicReference<RevCommit> latestCommitAtom = new AtomicReference<>();
        try {
            checkOuted.log()
                    .addPath(rel.getPath())
                    .setMaxCount(1)
                    .call()
                    .forEach(latestCommitAtom::set);
        } catch (GitAPIException e) {
            log.error("Cannot fetch commit info for {}", rel.getAbsolutePath(), e);
            return;
        }
        RevCommit latestCommit = latestCommitAtom.get();
        if (Objects.isNull(latestCommit)) {
            return;
        }

        sha = latestCommit.getId().getName();
        timestamp = latestCommit.getCommitTime();
        message = latestCommit.getShortMessage();
    }
}
