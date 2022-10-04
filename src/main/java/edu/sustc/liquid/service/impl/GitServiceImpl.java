package edu.sustc.liquid.service.impl;

import edu.sustc.liquid.service.GitService;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRefNameException;
import org.eclipse.jgit.api.errors.RefAlreadyExistsException;
import org.eclipse.jgit.api.errors.RefNotFoundException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.transport.FetchResult;

/**
 * Provide packed git function.
 *
 * @author Lizinan
 * @version 0.0.1
 */

public class GitServiceImpl implements GitService {

    /**
     * add changed file.
     *
     * @param dir  repository path
     * @param arg  argument of command(A)
     * @param file file or dir need to be added, maybe null
     * @return result message
     */
    @Override
    public String add(File dir, char arg, File file) {
        try {
            Git git = Git.open(dir);
            if (arg == 'A') {
                git.add().addFilepattern(".").call();
            } else {
                git.add().addFilepattern(file.getPath()).call();
            }
        } catch (IOException | GitAPIException e) {
            return e.getMessage();
        }
        return "Success";
    }

    /**
     * create, delete, rename branch.
     *
     * @param dir         repository path
     * @param arg         argument of command(a, b, d)
     * @param branchName1 branchName for create, delete
     * @param branchName2 branchName for rename
     * @return result message
     */
    @Override
    public String branch(File dir, char arg, String branchName1, String branchName2) {
        try {
            Git git = Git.open(dir);
            if (branchName1 == null && branchName2 == null && arg != 'a') {
                //FIXME: is it right?
                return git.branchList().call().toString();
            } else if (branchName1 == null && branchName2 == null) {
                return git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call()
                    .toString();
            } else if (arg == 'b') {
                git.checkout().setCreateBranch(true).setName(branchName1).call();
            } else if (arg == 'd') {
                git.branchDelete().setBranchNames(branchName1).setForce(false).call();
            } else {
                git.branchCreate().setName(branchName1).setForce(false).call();
            }
        } catch (IOException | GitAPIException e) {
            return e.getMessage();
        }
        return "Success";
    }

    /**
     * change branch or version.
     *
     * @param dir        repository path
     * @param arg        argument of command(b)
     * @param branchName branch, commitID, tag
     * @return result message
     */
    @Override
    public String checkout(File dir, char arg, String branchName) {
        try {
            Git git = Git.open(dir);
            if (arg == 'b') {
                git.checkout().setCreateBranch(true).setName(branchName).call();
            } else {
                git.checkout().setName(branchName).call();
            }
        } catch (IOException | GitAPIException e) {
            return e.getMessage();
        }
        return "Success";
    }

    /**
     * clone a repository to local path.
     *
     * @param dir local path
     * @param url remote repo url
     * @return result message
     */
    @Override
    public String clone(File dir, String url) {
        try {
            Git.cloneRepository().setDirectory(dir).setURI(url).call();
        } catch (GitAPIException e) {
            return e.getMessage();
        }
        return "Success";
    }

    /**
     * commit file.
     *
     * @param dir           repository path
     * @param arg           argument of command(a, A(amend))
     * @param commitMessage commitMessage
     * @return result message
     */
    @Override
    public String commit(File dir, char arg, String commitMessage) {
        return null;
    }

    /**
     * show diff between two commit.
     *
     * @param dir       repository path
     * @param commitId1 commitId1
     * @param commitId2 commitId1
     * @return result message
     */
    @Override
    public String diff(File dir, String commitId1, String commitId2) {
        return null;
    }

    /**
     * init a repository.
     *
     * @param dir repository path
     * @return result message
     */
    @Override
    public String init(File dir) {
        return null;
    }

    /**
     * merge this branch to assigned branch.
     *
     * @param dir        repository path
     * @param branchName assigned branch
     * @return result message
     */
    @Override
    public String merge(File dir, String branchName) {
        return null;
    }

    /**
     * pull change from remote server.
     *
     * @param dir repository path
     * @return result message
     */
    @Override
    public String pull(File dir) {
        return null;
    }

    /**
     * rebase this branch to assigned branch or commit.
     *
     * @param dir         repository path
     * @param rebasePoint assigned branch or commit
     * @return result message
     */
    @Override
    public String rebase(File dir, String rebasePoint) {
        return null;
    }

    /**
     * reset this branch to another position.
     *
     * @param dir     repository path
     * @param isSoft  arg soft
     * @param isMixed arg mixed
     * @param isHard  arg hard
     * @return result message
     */
    @Override
    public String reset(File dir, Boolean isSoft, Boolean isMixed, Boolean isHard) {
        return null;
    }

    /**
     * revert commit.
     *
     * @param dir      repository path
     * @param commitId commitId
     * @return result message
     */
    @Override
    public String revert(File dir, String commitId) {
        return null;
    }

    /**
     * delete file.
     *
     * @param dir      repository path
     * @param fileName fileName to delete
     * @return result message
     */
    @Override
    public String rm(File dir, String fileName) {
        return null;
    }

    /**
     * stash temporary work.
     *
     * @param dir   repository path
     * @param list  list arg
     * @param apply apply arg
     * @param drop  drop arg
     * @return result message
     */
    @Override
    public String stash(File dir, Boolean list, Boolean apply, Boolean drop) {
        return null;
    }

    /**
     * make tag on a commit.
     *
     * @param dir repository path
     * @param tag tag
     * @return result message
     */
    @Override
    public String tag(File dir, String tag) {
        return null;
    }
}
