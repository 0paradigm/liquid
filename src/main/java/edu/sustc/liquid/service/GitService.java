package edu.sustc.liquid.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

/**
 * Provide packed git function.
 *
 * @author Lizinan
 * @version 0.0.1
 */

public interface GitService {

    /**
     * add changed file.
     *
     * @param dir  repository path
     * @param arg  argument of command(u, A, f)
     * @param file file or dir need to be added, maybe null
     * @return result message
     */
    String add(File dir, char arg, File file);

    /**
     * create, delete, rename branch.
     *
     * @param dir         repository path
     * @param arg         argument of command(a, b, d, D, m)
     * @param branchName1 branchName for create, delete
     * @param branchName2 branchName for rename
     * @return result message
     */
    String branch(File dir, char arg, String branchName1, String branchName2);

    /**
     * change branch or version.
     *
     * @param dir repository path
     * @param arg argument of command(b)
     * @param ver branch, commitID, tag
     * @return result message
     */
    String checkout(File dir, char arg, String ver);

    /**
     * clone a repository to local path.
     *
     * @param dir local path
     * @param url remote repo url
     * @return result message
     */
    String clone(File dir, String url);

    /**
     * commit file.
     *
     * @param dir           repository path
     * @param arg           argument of command(a, A(amend))
     * @param commitMessage commitMessage
     * @return result message
     */
    String commit(File dir, char arg, String commitMessage);

    /**
     * show diff between two commit.
     *
     * @param dir       repository path
     * @param commitId1 commitId1
     * @param commitId2 commitId1
     * @return result message
     */
    String diff(File dir, String commitId1, String commitId2);

    /**
     * init a repository.
     *
     * @param dir repository path
     * @return result message
     */
    String init(File dir);

    //TODO: Do it later, can not figure out the usage of api yet
    //String log(File dir);

    /**
     * merge this branch to assigned branch.
     *
     * @param dir        repository path
     * @param branchName assigned branch
     * @return result message
     */
    String merge(File dir, String branchName);

    /**
     * pull change from remote server.
     *
     * @param dir repository path
     * @return result message
     */
    String pull(File dir);

    /**
     * rebase this branch to assigned branch or commit.
     *
     * @param dir         repository path
     * @param rebasePoint assigned branch or commit
     * @return result message
     */
    String rebase(File dir, String rebasePoint);

    /**
     * reset this branch to another position.
     *
     * @param dir     repository path
     * @param isSoft  arg soft
     * @param isMixed arg mixed
     * @param isHard  arg hard
     * @return result message
     */
    String reset(File dir, Boolean isSoft, Boolean isMixed, Boolean isHard);

    /**
     * revert commit.
     *
     * @param dir      repository path
     * @param commitId commitId
     * @return result message
     */
    String revert(File dir, String commitId);

    /**
     * delete file.
     *
     * @param dir      repository path
     * @param fileName fileName to delete
     * @return result message
     */
    String rm(File dir, String fileName);

    /**
     * stash temporary work.
     *
     * @param dir   repository path
     * @param list  list arg
     * @param apply apply arg
     * @param drop  drop arg
     * @return result message
     */
    String stash(File dir, Boolean list, Boolean apply, Boolean drop);

    /**
     * make tag on a commit.
     *
     * @param dir repository path
     * @param tag tag
     * @return result message
     */
    String tag(File dir, String tag);
}
