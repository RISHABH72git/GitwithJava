package com.jgit.gitwithjava.service;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameGenerator;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawText;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.revwalk.FooterLine;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CrudBranchService {

    final Map<String, List<String>> allFileWithBranch = new HashMap<>();

    public String gitInitialize(String dirName) throws IOException, GitAPIException {
        Git git = Git.init().setDirectory(new File(dirName)).call();
        return git.getRepository().getBranch();
    }

    public Object gitCreateBranch(String dirName, String newBranchName) throws IOException {
        Repository existingRepo = new FileRepositoryBuilder().setGitDir(new File("/home/" + dirName + "/.git")).build();
        Ref master = existingRepo.findRef("master");
        ObjectId masterTip = master.getObjectId();
        RefUpdate createBranch1 = existingRepo.updateRef("refs/heads/" + newBranchName);
        createBranch1.setNewObjectId(masterTip);
        createBranch1.update();
        return createBranch1.getName() + " created new branch";
    }

    public String gitDeleteBranch(String dirName, String branchName) throws IOException {
        Git git = Git.open(new File("/home/" + dirName));
        RefUpdate deleteBranch1 = git.getRepository().updateRef("refs/heads/" + branchName);
        deleteBranch1.setForceUpdate(true);
        deleteBranch1.delete();
        return deleteBranch1.getName() + " is Deleted ";
    }

    public Map<String, String> getGitFileNameWithPath() {
        Map<String, String> map = new HashMap<>();
        getAllGitFileAndBranch().keySet().forEach(s -> {
            map.put(s, s.substring(s.lastIndexOf("/") + 1));
        });
        return map;
    }


    public Map<String, List<String>> getAllGitFileAndBranch() {
        if (allFileWithBranch.size() > 0) {
            return allFileWithBranch;
        }
        Set<Path> filePath = new HashSet<>();
        File directoryPath = new File(getRootFile());
        //List of all files and directories
        File[] filesList = directoryPath.listFiles();
        for (File file : filesList) {
            if (!file.isFile() && !file.isHidden()) {
                try (Stream<Path> files = Files.walk(Paths.get(file.getPath()))) {
                    //find .git file in directory
                    filePath.addAll(files.filter(f -> f.getFileName().toString().equals(".git")).collect(Collectors.toList()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        filePath.forEach(path1 -> {
            try {
                Git git = Git.open(path1.toFile());
                List<String> branchList = new ArrayList<>();
                git.branchList().call().forEach(ref -> {
                    //add branch name one by one
                    branchList.add(ref.getName());
                });
                allFileWithBranch.put(path1.toFile().getParent(), branchList);
            } catch (IOException | GitAPIException e) {
                e.printStackTrace();
            }
        });
        return allFileWithBranch;
    }

    public String getRootFile() {
        File directoryPath = new File("/home");
        File[] filesList = directoryPath.listFiles();
        Assert.notNull(filesList, "List of File Empty");
        for (File file : filesList) {
            if (file.isDirectory()) {
                return file.getPath();
            }
        }
        return null;
    }

    public Map<String, String> getAllAuthorName(String path) throws IOException, GitAPIException {
        File directoryPath = new File(path);
        Map<String, String> authorName = new HashMap<>();
        if (directoryPath.isFile() || directoryPath.isDirectory()) {
            Git git = Git.open(directoryPath.getAbsoluteFile());
            git.log().call().forEach(ref -> {
                authorName.put(ref.getCommitterIdent().getName(), ref.getCommitterIdent().getEmailAddress());
            });
        }
        return authorName;
    }

    public Map<String, Object> getAllCommit(String path) throws IOException, GitAPIException {
        File directoryPath = new File(path);
        Map<String, Object> commitList = new HashMap<>();
        if (directoryPath.isFile() || directoryPath.isDirectory()) {
            Git git = Git.open(directoryPath.getAbsoluteFile());
            git.log().all().call().forEach(ref -> {
                Map<String, Object> newMap = new HashMap<>();
                newMap.put(ref.getCommitterIdent().getName(), ref.getCommitterIdent().getEmailAddress());
                Date date = new Date(ref.getCommitTime());
                newMap.put("TimeCommit", date.toString());
                newMap.put("commitMessage", ref.getFullMessage());
                newMap.put("FooterLine", ref.getFooterLines().stream().map(FooterLine::toString).collect(Collectors.toList()));
                commitList.put(ref.getName(), newMap);
            });
        } else {
            commitList.put(path, "This path not match any File or Directory");
        }
        return commitList;
    }

    public Map<String, Object> getCommitDetailById(String path, String commitId) throws IOException, GitAPIException {
        File directoryPath = new File(path);
        Map<String, Object> commitList = new HashMap<>();
        if (directoryPath.isFile() || directoryPath.isDirectory()) {
            Git git = Git.open(directoryPath.getAbsoluteFile());
            git.log().all().call().forEach(ref -> {
                if (ref.getName().equals(commitId)) {
                    Map<String, Object> newMap = new HashMap<>();
                    newMap.put(ref.getCommitterIdent().getName(), ref.getCommitterIdent().getEmailAddress());
                    newMap.put("TimeCommit", ref.getCommitTime());
                    newMap.put("CommitMessage", ref.getFullMessage());
                    newMap.put("FooterLine", ref.getFooterLines().stream().map(FooterLine::toString).collect(Collectors.toList()));
                    commitList.put(ref.getName(), newMap);
                }
            });
            if (commitList.isEmpty()) {
                commitList.put(commitId, "you enter wrong Commit Id");
            }
        }
        return commitList;
    }

    public Map<String, Object> getRemotePublicRepositoryDetail(String remoteUrl) throws GitAPIException, MalformedURLException {
        URL url = new URL(remoteUrl);
        Map<String, Object> urlMap = new HashMap<>();
        urlMap.put("Authority", url.getAuthority());
        urlMap.put("Host", url.getHost());
        urlMap.put("File", url.getFile());
        urlMap.put("UserInfo", url.getUserInfo());
        urlMap.put("Port", url.getDefaultPort());
        urlMap.put("Protocol", url.getProtocol());
        Map<String, Object> remoteGitDetail = new HashMap<>();
        try {
            Collection<Ref> refCollection = Git.lsRemoteRepository()
                    .setRemote(remoteUrl)
                    .setHeads(true).setTags(true).call();
            refCollection.forEach(ref -> {
                Map<String, Object> map = new HashMap<>();
                map.put("objectId", ref.getObjectId());
                map.put("peeledObjectId", ref.getPeeledObjectId());
                map.put("leaf", ref.getLeaf().getName());
                map.put("storage", ref.getStorage().name());
                map.put("target", ref.getTarget().getName());
                remoteGitDetail.put(ref.getName(), map);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (remoteGitDetail.isEmpty()) {
            remoteGitDetail.put(remoteUrl, "your url not valid");
        }
        remoteGitDetail.put("RemoteUrl", urlMap);
        return remoteGitDetail;
    }

    public Map<String, Object> cloneRepositoryAndGetDetail(String remoteUrl) throws IOException {
        Map<String, Object> remoteGitDetail = new HashMap<>();
        File localPath = new File("/tmp/TestGitRepository");
        localPath.mkdir();
        if (localPath.isDirectory()) {
            try {
                Git result = Git.cloneRepository().setURI(remoteUrl).setDirectory(localPath.getAbsoluteFile()).call();
                Map<String, Object> branchList = new HashMap<>();
                result.branchList().call().forEach(ref -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("objectId", ref.getObjectId());
                    map.put("peeledObjectId", ref.getPeeledObjectId());
                    map.put("leaf", ref.getLeaf().getName());
                    map.put("storage", ref.getStorage().name());
                    map.put("target", ref.getTarget().getName());
                    branchList.put(ref.getName(), map);
                });
                remoteGitDetail.put("BranchList", branchList);
                Map<String, Object> commitList = new HashMap<>();
                result.log().all().call().forEach(ref -> {
                    Map<String, Object> newMap = new HashMap<>();
                    newMap.put(ref.getCommitterIdent().getName(), ref.getCommitterIdent().getEmailAddress());
                    newMap.put("TimeCommit", ref.getCommitTime());
                    newMap.put("commitMessage", ref.getFullMessage());
                    newMap.put("FooterLine", ref.getFooterLines().stream().map(FooterLine::toString).collect(Collectors.toList()));
                    commitList.put(ref.getName(), newMap);
                });
                remoteGitDetail.put("CommitList", commitList);
                Map<String, String> authorName = new HashMap<>();
                result.log().call().forEach(ref -> {
                    authorName.put(ref.getAuthorIdent().getName(), ref.getAuthorIdent().getEmailAddress());
                });
                remoteGitDetail.put("AuthorList", authorName);
                Map<String, Object> remotBranchList = new HashMap<>();
                result.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call().forEach(ref -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("objectId", ref.getObjectId());
                    map.put("peeledObjectId", ref.getPeeledObjectId());
                    map.put("leaf", ref.getLeaf().getName());
                    map.put("storage", ref.getStorage().name());
                    map.put("target", ref.getTarget().getName());
                    remotBranchList.put(ref.getName(), map);
                });
                remoteGitDetail.put("RemoteBranchList", remotBranchList);
                Map<String, Object> gitStatus = new HashMap<>();
                gitStatus.put("Untracked", result.status().call().getUntracked());
                gitStatus.put("Added", result.status().call().getAdded());
                gitStatus.put("Untracked Folders", result.status().call().getUntrackedFolders());
                gitStatus.put("Uncommitted Changes", result.status().call().getUncommittedChanges());
                gitStatus.put("Changed", result.status().call().getChanged());
                gitStatus.put("Ignored Not in Index", result.status().call().getIgnoredNotInIndex());
                gitStatus.put("Removed", result.status().call().getRemoved());
                gitStatus.put("Conflicting", result.status().call().getConflicting());
                remoteGitDetail.put("GitStatus", gitStatus);
            } catch (Exception e) {
                remoteGitDetail.put(e.getMessage(), e.getCause());
            }
            FileUtils.deleteDirectory(localPath);
        }
        return remoteGitDetail;
    }

    public List<RevCommit> getCommits(String dirName) throws IOException, GitAPIException {
        List<RevCommit> revCommitList = new ArrayList<>();
        try (Git git = Git.open(new File(dirName))) {
//            String fileName = LocalDateTime.now().toString() + ".txt"; // Specify the file name
//            System.out.println(fileName);
            //BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
            LogCommand logCommand = git.log().all();
            long count = 1;
            for (RevCommit revCommit : logCommand.call()) {
                revCommitList.add(revCommit);
                //saveDetailsInFile(revCommit, count, writer);
                count++;
            }
            //writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return revCommitList;
    }

    public void printsAllCommits(String dirName, String fileName, boolean timestamp, boolean message, boolean email) throws IOException, GitAPIException {
        try (Git git = Git.open(new File(dirName))) {
            File file = new File("/home/rishabh/Downloads/" + fileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            LogCommand logCommand = git.log().all();
            long count = 1;
            for (RevCommit revCommit : logCommand.call()) {
                saveDetailsInFile(revCommit, count, writer, timestamp, message, email);
                count++;
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void printAuthorsCommits(String dirName, String fileName, boolean timestamp, boolean message, boolean email, String emailAddress) throws IOException, GitAPIException {
        try (Git git = Git.open(new File(dirName))) {
            File file = new File("/home/rishabh/Downloads/" + fileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            LogCommand logCommand = git.log().all();
            long count = 1;
            for (RevCommit revCommit : logCommand.call()) {
                if (revCommit.getCommitterIdent().getEmailAddress().equals(emailAddress)) {
                    saveDetailsInFile(revCommit, count, writer, timestamp, message, email);
                    count++;
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveDetailsInFile(RevCommit revCommit, long count, BufferedWriter bufferedWriter, boolean timestamp, boolean message, boolean email) throws IOException {
        String data = count + " - commitId: " + revCommit.getName();  // Generate the data for each iteration
        if (email) {
            data = data + "  Email: " + revCommit.getCommitterIdent().getEmailAddress();
        }
        if (message) {
            data = data + "  Message: " + revCommit.getFullMessage();
        }
        if (timestamp) {
            Instant commitInstant = Instant.ofEpochSecond(revCommit.getCommitTime());
            ZonedDateTime authorDateTime = ZonedDateTime.ofInstant(commitInstant, ZoneId.systemDefault());
            String gitDateTimeFormatString = "MMM dd HH:mm yyyy";
            data = data + "  Date: " + authorDateTime.format(DateTimeFormatter.ofPattern(gitDateTimeFormatString));
        }
        bufferedWriter.write(data); // Write the data to the file
        bufferedWriter.newLine();
    }

    public Status gitStatus(String dirName) throws IOException, GitAPIException {
        Git git = Git.open(new File(dirName));
        return git.status().call();
    }

    public List<Ref> gitBranch(String dirName) throws IOException, GitAPIException {
        Git git = Git.open(new File(dirName));
        return git.branchList().setListMode(ListBranchCommand.ListMode.ALL).call();
    }

    public List<Object> getGitConfig(String dirName) throws IOException, GitAPIException {
        Git git = Git.open(new File(dirName));
        List<Object> configList = new ArrayList<>();
        Config config1 = git.getRepository().getConfig();
        config1.getSections().forEach(s -> {
            configList.add(new HashMap<String, String>() {{
                put(s, String.valueOf(new ArrayList<String>(config1.getSubsections(s))));
            }});
        });
        configList.add(git.getRepository().getConfig());
        configList.add(config1.toText());
        return configList;
    }

    public List<RevCommit> getCommitDetails(String path, String commitId) throws IOException, GitAPIException {
        File directoryPath = new File(path);
        List<RevCommit> commitList = new ArrayList<>();
        if (directoryPath.isFile() || directoryPath.isDirectory()) {
            Git git = Git.open(directoryPath.getAbsoluteFile());
            git.log().all().call().forEach(ref -> {
                if (ref.getName().equals(commitId)) {
                    commitList.add(ref);
                }
            });
        }
        //changeCommitDetails(directoryPath.getAbsolutePath(),commitId);
        return commitList;
    }

    public void createBranch(String dirName, String newBranchName) throws IOException, GitAPIException {
        Git git = Git.open(new File(dirName));
        ObjectId commitObjectId = git.getRepository().resolve("HEAD");
        RevCommit commit = git.getRepository().parseCommit(commitObjectId);
        git.branchCreate().setName(newBranchName).setStartPoint(commit).call();
    }

    public void deleteBranch(String dirName, String branchName) throws IOException, GitAPIException {
        Git git = Git.open(new File(dirName));
        git.getRepository().findRef(branchName);
        git.branchDelete().setBranchNames(branchName).setForce(true).call();
    }

    public void gitLog(String dirName) throws IOException, GitAPIException {
        Git git = Git.open(new File(dirName));
        System.out.println(git.verifySignature().getVerifier().getName());
    }

    public void changeCommitDetails(String dirName, String commitId) throws IOException, GitAPIException {
        System.out.println(dirName);
        System.out.println(commitId);
        Git git = Git.open(new File(dirName));
        List<RevCommit> revCommit = new ArrayList<>();
        git.log().all().call().forEach(ref -> {
            if (ref.getName().equals(commitId)) {
                revCommit.add(ref);
            }
        });
        System.out.println(revCommit.get(0).toString());
        PersonIdent newAuthor = new PersonIdent("paras", "paras@example.com", revCommit.get(0).getAuthorIdent().getWhen(), TimeZone.getDefault());
        PersonIdent newCommitter = new PersonIdent("paras", "paras@example.com", revCommit.get(0).getCommitterIdent().getWhen(), TimeZone.getDefault());
        git.reset().setMode(ResetCommand.ResetType.HARD).setRef(commitId).call(); // Reset the branch to the commit
        //RevCommit newCommit = git.commit().setAuthor(newAuthor).setCommitter(newCommitter).setMessage(revCommit.get(0).getFullMessage()).call();
        //System.out.println(newCommit.toString());
    }

    public String showDiffParentAndChildCommit(String dirName, String commitId, String secondCommit) throws IOException, GitAPIException {
        Git git = Git.open(new File(dirName));
        RevWalk revWalk = new RevWalk(git.getRepository());
        ObjectId objectId = git.getRepository().resolve(commitId);
        RevCommit revCommitFirst = revWalk.parseCommit(objectId);
        RevWalk revWalkSecond = new RevWalk(git.getRepository());
        ObjectId objectIdSecond = git.getRepository().resolve(secondCommit);
        RevCommit revCommitSecond = revWalkSecond.parseCommit(objectIdSecond);
        ObjectReader reader = git.getRepository().newObjectReader();

        CanonicalTreeParser oldTreeParser = new CanonicalTreeParser();
        oldTreeParser.reset(reader, revCommitSecond.getTree());
        CanonicalTreeParser newTreeParser = new CanonicalTreeParser();
        newTreeParser.reset(reader, revCommitFirst.getTree());
        List<DiffEntry> diffs = git.diff().setNewTree(newTreeParser).setOldTree(oldTreeParser).call();
        ByteArrayOutputStream diffOutputStream = new ByteArrayOutputStream();
        try (DiffFormatter formatter = new DiffFormatter(diffOutputStream)) {
            formatter.setRepository(git.getRepository());
            for (DiffEntry entry : diffs) {
                DiffEntry.ChangeType changeType = entry.getChangeType();
                String oldPath = entry.getOldPath();
                String newPath = entry.getNewPath();
                // Print the information about the diff
                String change = "Change Type: " + changeType;
                String old = "Old Path: " + oldPath;
                String path = "New Path: " + newPath;
                System.out.println("Score: " + entry.getScore());
                String lineSeperator = "###########";
                diffOutputStream.write(change.getBytes(StandardCharsets.UTF_8));
                diffOutputStream.write(lineSeperator.getBytes(StandardCharsets.UTF_8));
                diffOutputStream.write(old.getBytes(StandardCharsets.UTF_8));
                diffOutputStream.write(lineSeperator.getBytes(StandardCharsets.UTF_8));
                diffOutputStream.write(path.getBytes(StandardCharsets.UTF_8));
                // Print the actual diff content
                diffOutputStream.write("==========".getBytes(StandardCharsets.UTF_8));
                formatter.format(entry);
                String seperator = "[newFile]";
                diffOutputStream.write(seperator.getBytes(StandardCharsets.UTF_8));
            }
        }
        return diffOutputStream.toString();
    }

    public void copyFile() throws IOException {
        Files.walk(Paths.get("BasiCrudOperation")).forEach(path -> {
            Path destination = Paths.get("BasicCrudOperation", path.toString());
            try {
                System.out.println(path);
                System.out.println(destination);
                Files.copy(path, destination);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void createFile() throws IOException {
        File gitFile = new File("Videos/first/src/last.txt");
        gitFile.createNewFile();
        System.out.println(gitFile.getName());
        System.out.println(gitFile.getAbsolutePath());
    }

    /*public String gitInitialize(String dirName) throws IOException, GitAPIException {
        Git git = Git.init().setDirectory(new File(""+dirName)).call();
        return git.getRepository().getBranch();
    }*/

    public Map<String, Set<String>> gitAddAndStatus(String dirName) throws IOException, GitAPIException {
        Git git = Git.open(new File("" + dirName));
        git.add().addFilepattern(".").call();
        git.checkout().getRepository().getBranch();
        Map<String, Set<String>> stringSetMap = new HashMap<>();
        Set<String> unTrackedFile = git.status().call().getUntracked();
        Set<String> getAdded = git.status().call().getAdded();
        Set<String> stringSet = git.status().call().getUntrackedFolders();
        stringSetMap.put("Uncommitted Changes", git.status().call().getUncommittedChanges());
        stringSetMap.put("Changed", git.status().call().getChanged());
        stringSetMap.put("Ignored Not in Index", git.status().call().getIgnoredNotInIndex());
        stringSetMap.put("Removed", git.status().call().getRemoved());
        stringSetMap.put("Conflicting", git.status().call().getConflicting());
        stringSetMap.put("UnTrackedFile", unTrackedFile);
        stringSetMap.put("Added", getAdded);
        stringSetMap.put("UntrackedFolders", stringSet);
        return stringSetMap;
    }

    /*public Map<String, Object> gitStatus(String dirName) throws IOException, GitAPIException {
        Git git = Git.open(new File(dirName));
        Map<String, Object> stringSetMap = new HashMap<>();
        stringSetMap.put("Untracked", git.status().call().getUntracked());
        stringSetMap.put("Added", git.status().call().getAdded());
        stringSetMap.put("Untracked Folders", git.status().call().getUntrackedFolders());
        stringSetMap.put("Local Branches", listAllLocalBranch(dirName));
        stringSetMap.put("Remote Branches", listAllRemoteBranch(dirName));
        stringSetMap.put("Uncommitted Changes", git.status().call().getUncommittedChanges());
        stringSetMap.put("Changed", git.status().call().getChanged());
        stringSetMap.put("Ignored Not in Index", git.status().call().getIgnoredNotInIndex());
        stringSetMap.put("Removed", git.status().call().getRemoved());
        stringSetMap.put("Conflicting", git.status().call().getConflicting());
        return stringSetMap;
    }*/

    public String gitCommit(String dirName, String commitMessage) throws IOException, GitAPIException {
        Git git = Git.open(new File("" + dirName));
        git.commit().setMessage(commitMessage).call();
        return git.commit().call().getFullMessage();
    }

    public List<String> listAllLocalBranch(String dirName) throws IOException, GitAPIException {
        Git git = Git.open(new File("" + dirName));
        List<String> branchList = new ArrayList<>();
        git.branchList().call().forEach(ref -> {
            branchList.add(ref.getName());
        });
        return branchList;
    }

    public List<String> listAllRemoteBranch(String dirName) throws IOException, GitAPIException {
        Git git = Git.open(new File("" + dirName));
        List<String> branchList = new ArrayList<>();
        git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call().forEach(ref -> {
            branchList.add(ref.getName());
        });
        return branchList;
    }

//    public List<Object> getGitConfig(String dirName) throws IOException, GitAPIException {
//        Git git = Git.open( new File( dirName ));
//        List<Object> configList = new ArrayList<>();
//        Config config1 = git.getRepository().getConfig();
//        config1.getSections().forEach(s -> {
//            configList.add(new HashMap<String, String>(){{
//                put(s, String.valueOf(new ArrayList<String>(config1.getSubsections(s))));
//            }});
//        });
//        configList.add(git.getRepository().getConfig());
//        return configList;
//    }

    public String gitRemoteAdd(String dirName) throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(new File("" + dirName)).readEnvironment().findGitDir().build();
        Git git = new Git(repository);
        try {
            RemoteAddCommand gitRemotAdd = git.remoteAdd();
            gitRemotAdd.setName("origin");
            gitRemotAdd.setUri(new URIish(""));
            gitRemotAdd.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Remote Added Success";
    }

    public Map<String, Object> gitRemoteDetail(String dirName) throws IOException, URISyntaxException, GitAPIException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(new File("" + dirName)).readEnvironment().findGitDir().build();
        Git git = new Git(repository);
        Map<String, Object> stringObjectMap = new HashMap<>();
        git.remoteList().call().forEach(remoteConfig -> {
            stringObjectMap.put(remoteConfig.getName(), remoteConfig.getURIs());
        });
        return stringObjectMap;
    }

    public String gitRemoteRemove(String dirName) throws IOException, GitAPIException {
        Git git = Git.open(new File("" + dirName));
        git.remoteRemove().setRemoteName("origin").call();
        return "Git Remote Remove Success";
    }

    public List<String> gitPush(String dirName) throws IOException, GitAPIException, URISyntaxException {
        Git git = Git.open(new File("" + dirName));
        List<String> stringList = new ArrayList<>();
        PushCommand pushCommand = git.push();
        pushCommand.setCredentialsProvider(new UsernamePasswordCredentialsProvider("", ""));
        pushCommand.call().forEach(pushResult -> {
            stringList.add(pushResult.getMessages());
        });
        return stringList;
    }

    public Map<String, Object> gitPull() throws GitAPIException, IOException {
        /*Git git = Git.cloneRepository().setURI("https://github.com/first.git").setDirectory(new File("Videos/first")).setCredentialsProvider(
                new UsernamePasswordCredentialsProvider("", "")
        ).call();*/
        Map<String, Object> stringObjectMap = new HashMap<>();
        /*git.remoteList().call().forEach(remoteConfig -> {
            stringObjectMap.put(remoteConfig.getName(), remoteConfig.getURIs());
        });*/
        return stringObjectMap;
    }

    public Map<String, Object> gitPullAndAddRemote(String dirName) throws GitAPIException, IOException {
        Map<String, Object> stringObjectMap = new HashMap<>();
        Git git = Git.cloneRepository().setURI("https://github.com//first.git").setCredentialsProvider(
                new UsernamePasswordCredentialsProvider("", "")).call();
        Git gitRemoteAdd = Git.open(new File("" + dirName));
        RemoteAddCommand gitAdd = gitRemoteAdd.remoteAdd();
        git.remoteList().call().forEach(remoteConfig -> {
            remoteConfig.getURIs().forEach(urIish -> {
                stringObjectMap.put(remoteConfig.getName(), remoteConfig.getURIs());
                gitAdd.setName(remoteConfig.getName());
                gitAdd.setUri(urIish);
            });
        });
        gitAdd.call();
        stringObjectMap.put("added", gitAdd.call().getURIs());
        return stringObjectMap;
    }

    public Object gitGetReference(String dirName) throws IOException {
        Repository existingRepo = new FileRepositoryBuilder().setGitDir(new File("/home/" + dirName + "/.git")).build();
        Ref master = existingRepo.findRef("master");
        return master.getName();
    }

    public Map<String, Object> listAllCommitWithTime(String dirName) throws IOException, GitAPIException {
        Git git = Git.open(new File("" + dirName));
        Map<String, Object> commitList = new HashMap<>();
        git.log().all().call().forEach(ref -> {
            commitList.put(ref.getShortMessage(), new ArrayList<>(Arrays.asList(ref.getCommitTime(), ref.getFullMessage())));
        });
        return commitList;
    }

    public Set<Path> getAllFilesFromDirectory(String dirName) throws IOException {
        File directoryPath = new File(dirName);
        Set<Path> filePath = new HashSet<>();
        List<File> listFiles = removeGitIgnoreFile(directoryPath);
        listFiles.forEach(file -> {
            if (file.isDirectory()) {
                File[] filesList = file.listFiles();
                Assert.notNull(filesList, "fileList is found null " + file.getName());
                for (File file1 : filesList) {
                    if (file1.isDirectory()) {
                        try (Stream<Path> files = Files.walk(Paths.get(file1.getPath()))) {
                            filePath.addAll(files.filter(path -> !path.toFile().isDirectory()).collect(Collectors.toList()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            } else {
                filePath.add(file.toPath());
            }
        });
        return filePath;
    }

    private List<File> removeGitIgnoreFile(File rootDir) throws IOException {
        List<String> removeFile = List.of("target", ".git", ".gitignore", "node_modules", "layers");
        FilenameFilter filenameFilter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".gitignore");
            }
        };
        File[] gitIgnoreFile = rootDir.listFiles(filenameFilter);
        Assert.notNull(gitIgnoreFile, ".gitIgnore file not found in " + rootDir.getName());
        File gitFile = gitIgnoreFile[0];
        List<String> fileContent = Files.readAllLines(gitFile.toPath());
        System.out.println(fileContent);
        File[] allFile = rootDir.listFiles();
        System.out.println(Arrays.toString(allFile));
        assert allFile != null;
        List<File> fileList = Arrays.asList(allFile);
        return fileList.stream().filter(file -> !removeFile.contains(file.getName())).filter(file -> !fileContent.contains(file.getName())).collect(Collectors.toList());
    }

    public List<String> getBlame(String dirName, String fileName, boolean print) throws IOException, GitAPIException {
        String file = fileName.replace(dirName + "/", "");
        Git git = Git.open(new File(dirName));
        final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        BlameResult blameResult = git.blame().setFilePath(file).setTextComparator(RawTextComparator.DEFAULT).call();
        blameResult.computeAll();
        RawText rawText = blameResult.getResultContents();
        List<String> rawTextList = new ArrayList<>();
        File file1;
        BufferedWriter writer = null;
        if (print) {
            file1 = new File("/home/rishabh/Downloads/" + fileName.substring(fileName.lastIndexOf("/") + 1, fileName.lastIndexOf(".")));
            writer = new BufferedWriter(new FileWriter(file1));
        }
        for (int i = 0; i < rawText.size(); i++) {
            PersonIdent personIdent = blameResult.getSourceAuthor(i);
            RevCommit revCommit = blameResult.getSourceCommit(i);
            String text = personIdent.getName() + (revCommit != null ? " - " + DATE_FORMAT.format(((long) revCommit.getCommitTime()) * 1000) + " - " + revCommit.getName() : "") + ": " + rawText.getString(i);
            rawTextList.add(text);
            if (print) {
                writer.write(text);
                writer.newLine();
            }
        }
        if (print) {
            writer.close();
        }
        return rawTextList;
    }

}
