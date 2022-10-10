/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.zeroparadigm.liquid.service.impl;

import io.zeroparadigm.liquid.service.GitService;
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

public class GitServiceImpl implements GitService {
    // FIXME: Authentic need to be added, or do it in user controller?

    @Override
    public String add(File dir, char arg, File file) throws IOException, GitAPIException {
        Git git = Git.open(dir);
        if (arg == 'A') {
            git.add().addFilepattern(".").call();
        } else {
            git.add().addFilepattern(file.getPath()).call();
        }
        return "Success";
    }

    @Override
    public String branch(File dir, char arg, String branchName1,
                         String branchName2) throws IOException, GitAPIException {
        Git git = Git.open(dir);
        if (branchName1 == null && branchName2 == null && arg != 'a') {
            // FIXME: is it right?
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
        return "Success";
    }

    @Override
    public String checkout(File dir, char arg, String branchName) throws IOException, GitAPIException {

        Git git = Git.open(dir);
        if (arg == 'b') {
            git.checkout().setCreateBranch(true).setName(branchName).call();
        } else {
            git.checkout().setName(branchName).call();
        }
        return "Success";
    }

    @Override
    public String clone(File dir, String url) throws GitAPIException {
        Git.cloneRepository().setDirectory(dir).setURI(url).call();
        return "Success";
    }

    @Override
    public String commit(File dir, char arg, @NotNull String commitMessage) throws IOException, GitAPIException {
        Git git = Git.open(dir);
        if (arg == 'a') {
            git.add().addFilepattern(".").call();
            git.commit().setMessage(commitMessage).call();
        } else if (arg == 'A') {
            git.commit().setAmend(true).setMessage(commitMessage).call();
        } else {
            git.commit().setMessage(commitMessage).call();
        }
        return null;
    }

    @Override
    public String diff(File dir, char arg, String oldObject, String newObject,
                       OutputStream outputStream) throws IOException, GitAPIException {
        Git git = Git.open(dir);
        if (arg == 'b') {
            AbstractTreeIterator oldTreeParser = prepareTreeParser(git.getRepository(),
                    String.format("/refs/heads/%s", oldObject));
            AbstractTreeIterator newTreeParser = prepareTreeParser(git.getRepository(),
                    String.format("/refs/heads/%s", newObject));
            git.diff().setOutputStream(outputStream).setOldTree(oldTreeParser)
                    .setNewTree(newTreeParser).call();
        } else if (arg == 'c') {
            // TODO: how to compare
            git.getRepository();
        } else {
            git.diff().setOutputStream(outputStream).call();
        }
        return null;
    }

    private static AbstractTreeIterator prepareTreeParser(Repository repository, String ref) throws IOException {
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

    // FIXME: commitId is ObjectId
    private static CanonicalTreeParser prepareCanonicalTreeParser(Repository repository,
                                                                  ObjectId commitId) throws IOException {
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(commitId);
            ObjectId treeId = commit.getTree().getId();
            try (ObjectReader reader = repository.newObjectReader()) {
                return new CanonicalTreeParser(null, reader, treeId);
            }
        }

    }

    @Override
    public String init(File dir) throws GitAPIException {
        Git.init().setDirectory(dir).call();
        return "Success";
    }

    @Override
    public String merge(File dir, String branchName) throws IOException, GitAPIException {
        Git git = Git.open(dir);
        Repository repo = git.getRepository();
        ObjectId mergeBase = repo.resolve(branchName);
        MergeResult merge =
                git.merge().include(mergeBase).setCommit(true).setMessage("Merged changes").call();
        if (merge.getMergeStatus().equals(MergeResult.MergeStatus.CONFLICTING)) {
            return merge.getConflicts().toString();
        }
        return "Success";
    }

    @Override
    public String pull(File dir, String remoteServer, String remoteBranch) throws IOException, GitAPIException {
        Git git = Git.open(dir);
        PullCommand pullCommand = git.pull();
        if (remoteServer != null) {
            pullCommand.setRemote(remoteServer);
        }
        if (remoteBranch != null) {
            pullCommand.setRemoteBranchName(remoteBranch);
        }
        pullCommand.call();
        return "Success";
    }

    @Override
    public String push(File dir, char arg, @NotNull String remote, @NotNull String localBranch,
                       String remoteBranch) throws IOException, GitAPIException {
        // FIXME: remoteBranch not considered
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
        return "Success";
    }

    @Override
    public String rebase(File dir, String rebaseBranch) throws IOException, GitAPIException {
        // TODO: allow rebase to commit
        Git git = Git.open(dir);
        git.rebase().setUpstream(String.format("refs/heads/%s", rebaseBranch)).call();
        return "Success";
    }

    @Override
    public String remote(File dir, char arg, String remoteName, URL url) throws IOException, GitAPIException {
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
        return "Success";
    }

    @Override
    public String reset(File dir, Boolean isSoft, Boolean isMixed, Boolean isHard,
                        String resetPoint) throws IOException, GitAPIException {
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
        return "Success";
    }

    @Override
    public String revert(File dir, String commitId) throws IOException, GitAPIException {
        Git git = Git.open(dir);
        // FIXME: arg of resolve() ??
        ObjectId commit = git.getRepository().resolve(commitId);
        if (commit != null) {
            git.revert().include(commit).call();
        }
        return "Success";
    }

    @Override
    public String rm(File dir, boolean cache, String fileName) throws IOException, GitAPIException {
        Git git = Git.open(dir);
        git.rm().addFilepattern(fileName).setCached(cache).call();
        return "Success";
    }

    @Override
    public String stash(File dir, char arg, String stashName) throws IOException, GitAPIException {
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
        return "Success";
    }

    public String status(File dir) throws IOException, GitAPIException {
        Git git = Git.open(dir);
        Status status = git.status().call();
        return status.toString();
    }

    @Override
    public String tag(File dir, char arg, String tag, String message) throws IOException, GitAPIException {
        Git git = Git.open(dir);
        if (arg == 'a') {
            git.tag().setName(tag).setMessage(message).setForceUpdate(true).call();
        } else if (arg == 'd') {
            git.tagDelete().setTags(tag).call();
        } else if (tag == null) {
            // FIXME: need to be tested
            List<Ref> list = git.tagList().call();
            return list.toString();
        } else {
            git.tag().setName(tag).setForceUpdate(true).call();
        }
        return "Success";
    }
}
