package edu.sustc.liquid.service;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.stream.Stream;
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
    String add(File dir, char arg, File file) throws IOException, GitAPIException;

    /**
     * create, delete, rename branch.
     *
     * @param dir         repository path
     * @param arg         argument of command(a, b, d, D, m)
     * @param branchName1 branchName for create, delete
     * @param branchName2 branchName for rename
     * @return result message
     */
    String branch(File dir, char arg, String branchName1, String branchName2)throws IOException, GitAPIException;

    /**
     * change branch or version.
     *
     * @param dir repository path
     * @param arg argument of command(b)
     * @param ver branch, commitID, tag
     * @return result message
     */
    String checkout(File dir, char arg, String ver)throws IOException, GitAPIException;

    /**
     * clone a repository to local path.
     *
     * @param dir local path
     * @param url remote repo url
     * @return result message
     */
    String clone(File dir, String url)throws IOException, GitAPIException;

    /**
     * commit file.
     *
     * @param dir           repository path
     * @param arg           argument of command(a, A(amend))
     * @param commitMessage commitMessage
     * @return result message
     */
    String commit(File dir, char arg, String commitMessage)throws IOException, GitAPIException;

    /**
     * show diff between two commit.
     *
     * @param dir          repository path
     * @param arg          argument of command(b(branch), c(commit))
     * @param oldObject    object1
     * @param newObject    object2
     * @param outputStream outputStream, it's hard to return a String
     * @return result message
     */
    String diff(File dir, char arg, String oldObject, String newObject, OutputStream outputStream)throws IOException, GitAPIException;

    /**
     * init a repository.
     *
     * @param dir repository path
     * @return result message
     */
    String init(File dir)throws IOException, GitAPIException;

    //TODO: Do it later
    //String log(File dir);

    /**
     * merge this branch to assigned branch.
     *
     * @param dir        repository path
     * @param branchName assigned branch
     * @return result message
     */
    String merge(File dir, String branchName)throws IOException, GitAPIException;

    /**
     * pull change from remote server.
     *
     * @param dir          repository path
     * @param remoteServer name of remoteServer
     * @param remoteBranch name of remoteBranch
     * @return result message
     */
    String pull(File dir, String remoteServer, String remoteBranch)throws IOException, GitAPIException;

    /**
     * push change to remote server.
     *
     * @param dir          repository path
     * @param arg          argument of command(f, d)
     * @param remote       name of remote server
     * @param localBranch  name of localBranch
     * @param remoteBranch name of remoteBranch
     * @return result message
     */
    String push(File dir, char arg, String remote, String localBranch, String remoteBranch)throws IOException, GitAPIException;

    /**
     * rebase this branch to assigned branch or commit.
     *
     * @param dir          repository path
     * @param rebaseBranch assigned branch
     * @return result message
     */
    String rebase(File dir, String rebaseBranch)throws IOException, GitAPIException;

    /**
     * remote command.
     *
     * @param dir        repository path
     * @param arg        argument of command(a, r(rm))
     * @param remoteName name of remote
     * @param url        remote URL
     * @return result message
     */
    String remote(File dir, char arg, String remoteName, URL url)throws IOException, GitAPIException;

    /**
     * reset this branch to another position.
     *
     * @param dir        repository path
     * @param isSoft     arg soft
     * @param isMixed    arg mixed
     * @param isHard     arg hard
     * @param resetPoint resetPoint
     * @return result message
     */
    String reset(File dir, Boolean isSoft, Boolean isMixed, Boolean isHard, String resetPoint)throws IOException, GitAPIException;

    /**
     * revert commit.
     *
     * @param dir      repository path
     * @param commitId commitId
     * @return result message
     */
    String revert(File dir, String commitId)throws IOException, GitAPIException;

    /**
     * delete file.
     *
     * @param dir      repository path
     * @param cache    cache
     * @param fileName fileName to delete
     * @return result message
     */
    String rm(File dir, boolean cache, String fileName)throws IOException, GitAPIException;

    /**
     * stash temporary work.
     *
     * @param dir repository path
     * @param arg argument of command(l(list), a(apply), p(pop), d(drop), c(clear))
     * @return result message
     */
    String stash(File dir, char arg, String stashName)throws IOException, GitAPIException;

    /**
     * show status of repo.
     *
     * @param dir repository path
     * @return result message
     */
    String status(File dir)throws IOException, GitAPIException;

    /**
     * make tag on a commit.
     *
     * @param dir     repository path
     * @param arg     argument of command(a, s(show), d)
     * @param tag     tag
     * @param message message
     * @return result message
     */
    String tag(File dir, char arg, String tag, String message)throws IOException, GitAPIException;
}
