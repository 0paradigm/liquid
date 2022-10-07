package edu.sustc.liquid.service.impl;

import edu.sustc.liquid.service.GitService;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ListBranchCommand;
import org.eclipse.jgit.api.MergeResult;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.jetbrains.annotations.NotNull;

/**
 * Provide packed git function.
 *
 * @author Lizinan
 * @version 0.0.1
 */

public class GitServiceImpl implements GitService {
    //FIXME: Authentic need to be added, or do it in user controller?

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
    public String commit(File dir, char arg, @NotNull String commitMessage) {
        try {
            Git git = Git.open(dir);
            if (arg == 'a') {
                git.add().addFilepattern(".").call();
                git.commit().setMessage(commitMessage).call();
            } else if (arg == 'A') {
                git.commit().setAmend(true).setMessage(commitMessage).call();
            } else {
                git.commit().setMessage(commitMessage).call();
            }
        } catch (IOException | GitAPIException e) {
            return e.getMessage();
        }
        return null;
    }

    /**
     * show diff between two commit.
     *
     * @param dir       repository path
     * @param arg       argument of command(b(branch), c(commit))
     * @param oldObject object1
     * @param newObject object2
     * @return result message
     */
    @Override
    public String diff(File dir, char arg, String oldObject, String newObject,
                       OutputStream outputStream) {
        try {
            Git git = Git.open(dir);
            if (arg == 'b') {
                AbstractTreeIterator oldTreeParser = prepareTreeParser(git.getRepository(),
                    String.format("/refs/heads/%s", oldObject));
                AbstractTreeIterator newTreeParser = prepareTreeParser(git.getRepository(),
                    String.format("/refs/heads/%s", newObject));
                git.diff().setOutputStream(outputStream).setOldTree(oldTreeParser)
                    .setNewTree(newTreeParser).call();
            } else if (arg == 'c') {
                //TODO: how to compare
                git.getRepository();
            } else {
                git.diff().setOutputStream(outputStream).call();
            }
        } catch (IOException | GitAPIException e) {
            return e.getMessage();
        }
        return null;
    }

    private static AbstractTreeIterator prepareTreeParser(Repository repository, String ref)
        throws IOException {
        // from the commit we can build the tree which allows us to construct the TreeParser
        Ref head = repository.exactRef(ref);
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(head.getObjectId());
            RevTree tree = walk.parseTree(commit.getTree().getId());
            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }
            walk.dispose();
            return treeParser;
        }
    }

    //FIXME: commitId is ObjectId
    private static CanonicalTreeParser prepareCanonicalTreeParser(Repository repository,
                                                                  ObjectId commitId)
        throws IOException {
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(commitId);
            ObjectId treeId = commit.getTree().getId();
            try (ObjectReader reader = repository.newObjectReader()) {
                return new CanonicalTreeParser(null, reader, treeId);
            }
        }

    }

    /**
     * init a repository.
     *
     * @param dir repository path
     * @return result message
     */
    @Override
    public String init(File dir) {
        try {
            Git.init().setDirectory(dir).call();
        } catch (GitAPIException e) {
            return e.getMessage();
        }
        return "Success";
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
        try {
            Git git = Git.open(dir);
            Repository repo = git.getRepository();
            ObjectId mergeBase = repo.resolve(branchName);
            MergeResult merge =
                git.merge().include(mergeBase).setCommit(true).setMessage("Merged changes").call();
            if (merge.getMergeStatus().equals(MergeResult.MergeStatus.CONFLICTING)) {
                return merge.getConflicts().toString();
            }
        } catch (IOException | GitAPIException e) {
            return e.getMessage();
        }
        return "Success";
    }

    /**
     * pull change from remote server.
     *
     * @param dir          repository path
     * @param remoteServer name of remoteServer, can be null
     * @param remoteBranch name of remoteBranch, can be null
     * @return result message
     */
    @Override
    public String pull(File dir, String remoteServer, String remoteBranch) {
        try {
            Git git = Git.open(dir);
            PullCommand pullCommand = git.pull();
            if (remoteServer != null) {
                pullCommand.setRemote(remoteServer);
            }
            if (remoteBranch != null) {
                pullCommand.setRemoteBranchName(remoteBranch);
            }
            pullCommand.call();
        } catch (IOException | GitAPIException e) {
            return e.getMessage();
        }
        return "Success";
    }

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
    @Override
    public String push(File dir, char arg, @NotNull String remote, @NotNull String localBranch,
                       String remoteBranch) {
        //FIXME: remoteBranch not considered
        try {
            Git git = Git.open(dir);
            PushCommand pushCommand = git.push();
            if (arg == 'f') {
                pushCommand.setForce(true);
                pushCommand.setRemote(remote).add(localBranch);
            } else if (arg == 'd') {
                RefSpec refSpec = new RefSpec().setSource(null)
                    .setDestination(String.format("refs/heads/%s", remoteBranch));
                pushCommand.setRefSpecs(refSpec).setRemote(remote);
            } else {
                pushCommand.setRemote(remote).add(localBranch);
            }
            pushCommand.call();
        } catch (IOException | GitAPIException e) {
            return e.getMessage();
        }
        return "Success";
    }

    /**
     * rebase this branch to assigned branch or commit.
     *
     * @param dir          repository path
     * @param rebaseBranch assigned branch
     * @return result message
     */
    @Override
    public String rebase(File dir, String rebaseBranch) {
        try {
            //TODO: allow rebase to commit
            Git git = Git.open(dir);
            git.rebase().setUpstream(String.format("refs/heads/%s", rebaseBranch)).call();
        } catch (IOException | GitAPIException e) {
            return e.getMessage();
        }
        return "Success";
    }

    /**
     * remote command.
     *
     * @param dir        repository path
     * @param arg        argument of command(a, r(rm))
     * @param remoteName name of remote
     * @param url        remote URL
     * @return result message
     */
    @Override
    public String remote(File dir, char arg, String remoteName, URL url) {
        try {
            Git git = Git.open(dir);
            if (arg == 'a') {
                git.remoteAdd().setName(remoteName).setUri(new URIish(url)).call();
            } else if (arg == 'r') {
                git.remoteRemove().setRemoteName(remoteName).call();
            } else {
                List<RemoteConfig> remoteConfigs = git.remoteList().call();
                StringBuilder tmp = new StringBuilder();
                for (RemoteConfig remoteConfig : remoteConfigs) {
                    tmp.append(remoteConfig.getName()).append(" ")
                        .append(remoteConfig.getURIs().toString()).append("\n");
                }
                return tmp.toString();
            }
        } catch (IOException | GitAPIException e) {
            return e.getMessage();
        }
        return "Success";
    }

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
    @Override
    public String reset(File dir, Boolean isSoft, Boolean isMixed, Boolean isHard,
                        String resetPoint) {
        try {
            Git git = Git.open(dir);
            ResetCommand resetCommand = git.reset();
            if (isHard) {
                resetCommand.setMode(ResetCommand.ResetType.HARD);
            } else if (isMixed) {
                resetCommand.setMode(ResetCommand.ResetType.MIXED);
            } else {
                resetCommand.setMode(ResetCommand.ResetType.SOFT);
            }
            resetCommand.setRef(resetPoint);
            resetCommand.call();
        } catch (IOException | GitAPIException e) {
            return e.getMessage();
        }
        return "Success";
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
        try {
            Git git = Git.open(dir);
            //FIXME: arg of resolve() ??
            ObjectId commit = git.getRepository().resolve(commitId);
            if (commit != null) {
                git.revert().include(commit).call();
            }
        } catch (IOException | GitAPIException e) {
            return e.getMessage();
        }
        return "Success";
    }

    /**
     * delete file.
     *
     * @param dir      repository path
     * @param cache    set cache
     * @param fileName fileName to delete
     * @return result message
     */
    @Override
    public String rm(File dir, boolean cache, String fileName) {
        try {
            Git git = Git.open(dir);
            git.rm().addFilepattern(fileName).setCached(cache).call();
        } catch (IOException | GitAPIException e) {
            return e.getMessage();
        }
        return "Success";
    }

    /**
     * stash temporary work.
     *
     * @param dir repository path
     * @param arg argument of command(l(list), a(apply), d(drop), c(clear))
     * @return result message
     */
    @Override
    public String stash(File dir, char arg, String stashName) {
        try {
            Git git = Git.open(dir);
            if (arg == 'l') {
                Collection<RevCommit> commits = git.stashList().call();
                StringBuilder stringBuilder = new StringBuilder();
                for (RevCommit rev : commits) {
                    stringBuilder.append(rev).append(": ").append(rev.getFullMessage())
                        .append("\n");
                }
                return stringBuilder.toString();
            } else if (arg == 'a') {
                git.stashApply().setStashRef(stashName).call();
            } else if (arg == 'd') {
                git.stashDrop().setStashRef(0).call();
            } else if (arg == 'c') {
                git.stashDrop().setAll(true).call();
            } else {
                RevCommit stash = git.stashCreate().call();
                return "Stash create " + stash;
            }
        } catch (IOException | GitAPIException e) {
            return e.getMessage();
        }
        return "Success";
    }

    /**
     * show status of repo.
     *
     * @param dir repository path
     * @return result message
     */
    public String status(File dir) {
        try {
            Git git = Git.open(dir);
            Status status = git.status().call();
            return status.toString();
        } catch (IOException | GitAPIException e) {
            return e.getMessage();
        }
    }

    /**
     * make tag on a commit.
     *
     * @param dir repository path
     * @param arg argument of command(a, d)
     * @param tag tag
     * @return result message
     */
    @Override
    public String tag(File dir, char arg, String tag, String message) {
        try {
            Git git = Git.open(dir);
            if (arg == 'a') {
                git.tag().setName(tag).setMessage(message).setForceUpdate(true).call();
            } else if (arg == 'd') {
                git.tagDelete().setTags(tag).call();
            } else if (tag == null) {
                //FIXME: need to be tested
                List<Ref> list = git.tagList().call();
                return list.toString();
            } else {
                git.tag().setName(tag).setForceUpdate(true).call();
            }
        } catch (IOException | GitAPIException e) {
            return e.getMessage();
        }
        return "Success";
    }
}
