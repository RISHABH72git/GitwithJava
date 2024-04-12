package com.jgit.gitwithjava.local.service;

import com.jgit.gitwithjava.DefaultCredentials;
import com.jgit.gitwithjava.custom.model.GitClone;
import com.jgit.gitwithjava.frontend.model.FileModel;
import lombok.extern.log4j.Log4j2;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.*;
import org.eclipse.jgit.patch.FileHeader;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.eclipse.jgit.util.Paths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
@Log4j2
public class LocalService {

    @Autowired
    GitServices gitServices;

    public List<FileModel> localAllFile(String path) {
        File file;
        List<FileModel> allFiles = new ArrayList<>(20);
        if (Objects.isNull(path)) {
            file = new File(DefaultCredentials.getRootFolder());
            for (File file1 : Objects.requireNonNull(file.listFiles())) {
                if (!file1.isHidden() && file1.isDirectory()) {
                    FileModel fileModel = new FileModel();
                    fileModel.setFileName(file1.getName());
                    fileModel.setFileParentName(file1.getParent());
                    fileModel.setIsDirectory(file1.isDirectory());
                    fileModel.setIsHidden(file1.isHidden());
                    boolean gitAvail = false;
                    if (file1.isDirectory()) {
                        gitAvail = Arrays.asList(Objects.requireNonNull(file1.list())).contains(".git");
                    }
                    fileModel.setHasGit(gitAvail);
                    allFiles.add(fileModel);
                }
            }
        } else {
            file = new File(DefaultCredentials.getRootFolder() + path);
            for (File file1 : Objects.requireNonNull(file.listFiles())) {
                if (!file1.isHidden()) {
                    FileModel fileModel = new FileModel();
                    fileModel.setFileName(file1.getName());
                    fileModel.setFileParentName(file1.getParent());
                    fileModel.setIsDirectory(file1.isDirectory());
                    fileModel.setIsHidden(file1.isHidden());
                    boolean gitAvail = false;
                    if (file1.isDirectory()) {
                        gitAvail = Arrays.asList(Objects.requireNonNull(file1.list())).contains(".git");
                    }
                    fileModel.setHasGit(gitAvail);
                    allFiles.add(fileModel);
                }
            }
        }
        return allFiles;
    }

    public LinkedList<Map<String, Object>> getParents(String path) {
        LinkedList<Map<String, Object>> stringLinkedList = new LinkedList<>();
        if (!Objects.isNull(path)) {
            File file = new File(path);
            Map<String, Object> stringObjectMap1 = new HashMap<>();
            stringObjectMap1.put("name", file.getName());
            stringObjectMap1.put("parentPath", file.getPath());
            stringObjectMap1.put("isDirectory", false);
            stringLinkedList.addFirst(stringObjectMap1);
            while (file.getParentFile() != null) {
                Map<String, Object> stringObjectMap = new HashMap<>();
                file = file.getParentFile();
                stringObjectMap.put("name", file.getName());
                stringObjectMap.put("parentPath", file.getPath());
                stringObjectMap.put("isDirectory", true);
                stringLinkedList.addFirst(stringObjectMap);
            }
        }
        return stringLinkedList;
    }

    private String getCurrentPath(String path) {
        if (path.contains(File.separator)) {
            int lastIndex = path.lastIndexOf(File.separator);
            return path.substring(lastIndex + 1);
        }
        return path;
    }

    private Map<String, String> getPathAndLastFolder(String path) {
        int lastIndex = path.lastIndexOf(File.separator);
        String current = path.substring(lastIndex + 1);
        String parent = path.substring(0, lastIndex);
        return Map.of("current", current, "parent", parent);
    }

    public void createRepository(String path) throws IOException, GitAPIException {
        File file = new File(DefaultCredentials.getRootFolder() + path);
        Git.init().setDirectory(file).setInitialBranch("master").call();
    }

    public List<Object> getCommits(String path) throws IOException, GitAPIException {
        Git git = Git.open(new File(DefaultCredentials.getRootFolder() + path));
        return gitServices.getCommits(git);
    }

    public List<Map<String, String>> getAuthors(String path) throws IOException, GitAPIException {
        Git git = Git.open(new File(DefaultCredentials.getRootFolder() + path));
        return gitServices.getAuthors(git);
    }

    public Map<Object, Object> getGraph(String path) throws IOException, GitAPIException {
        Git git = Git.open(new File(DefaultCredentials.getRootFolder() + path));
        LinkedList<RevCommit> commitLinkedList = new LinkedList<>();
        git.log().call().forEach(commitLinkedList::add);
        Map<Object, Integer> getAllCommitsByDate = getAllCommitsByDate(commitLinkedList);
        Map<String, Object> getLastDayCommits = getLastDayCommits(git);
        Map<String, Integer> getAuthorWithCommitCount = getAuthorWithCommitCount(commitLinkedList);
        return Map.of("commitsByDate", getAllCommitsByDate, "lastDayCommits", getLastDayCommits, "authorCommitsCount", getAuthorWithCommitCount);
    }

    public Map<Object, Integer> getAllCommitsByDate(LinkedList<RevCommit> commitLinkedList) {
        return commitLinkedList.stream().collect(Collectors.groupingBy(revCommit -> {
            Instant getDate = Instant.ofEpochSecond(revCommit.getCommitTime());
            ZonedDateTime firstCommitDate = ZonedDateTime.ofInstant(getDate, ZoneId.systemDefault());
            return firstCommitDate.toLocalDate();
        }, Collectors.summingInt(commit -> 1)));
    }

    public Map<String, Object> getLastDayCommits(Git git) throws GitAPIException {
        LinkedList<RevCommit> commitLinkedList = new LinkedList<>();
        git.log().call().forEach(revCommit -> {
            Instant commitInstant = Instant.ofEpochSecond(revCommit.getCommitTime());
            ZonedDateTime authorDateTime = ZonedDateTime.ofInstant(commitInstant, ZoneId.systemDefault());
            ZonedDateTime firstCommitDate = null;
            if (!commitLinkedList.isEmpty()) {
                Instant getDate = Instant.ofEpochSecond(commitLinkedList.getFirst().getCommitTime());
                firstCommitDate = ZonedDateTime.ofInstant(getDate, ZoneId.systemDefault());
            }
            if (commitLinkedList.isEmpty() || authorDateTime.toLocalDate().equals(firstCommitDate.toLocalDate())) {
                commitLinkedList.add(revCommit);
            }
        });
        Map<String, Integer> allCommits = commitLinkedList.stream().collect(Collectors.groupingBy(commit -> commit.getAuthorIdent().getName(), Collectors.summingInt(commit -> 1)));
        Instant getDate = Instant.ofEpochSecond(commitLinkedList.getFirst().getCommitTime());
        ZonedDateTime firstCommitDate = ZonedDateTime.ofInstant(getDate, ZoneId.systemDefault());
        return Map.of("commits", allCommits, "lastDate", firstCommitDate.toLocalDate());
    }

    public Map<String, Integer> getAuthorWithCommitCount(LinkedList<RevCommit> commitLinkedList) {
        Map<String, Integer> authorCommitCounts = new HashMap<>();
        commitLinkedList.forEach(commit -> {
            String author = commit.getAuthorIdent().getEmailAddress();
            authorCommitCounts.put(author, authorCommitCounts.getOrDefault(author, 0) + 1);
        });
        return authorCommitCounts;
    }

    public void createFolder(String path, String name) {
        File file;
        if (Objects.isNull(path)) {
            file = new File(DefaultCredentials.getRootFolder() + name);
        } else {
            file = new File(DefaultCredentials.getRootFolder() + path + "/" + name);
        }
        System.out.println("folder created - " + file.mkdir());
    }

    public void createFile(String path, String filename) throws IOException {
        File file;
        if (Objects.isNull(path)) {
            file = new File(DefaultCredentials.getRootFolder() + filename + ".txt");
        } else {
            file = new File(DefaultCredentials.getRootFolder() + path + "/" + filename + ".txt");
        }
        System.out.println("file created - " + file.createNewFile());
    }

    public Map<String, Set<String>> getGitStatus(String path) throws IOException, GitAPIException {
        Git git = Git.open(new File(DefaultCredentials.getRootFolder() + path));
        Status status = gitServices.getStatus(git);
        Map<String, Set<String>> stringSetMap = new HashMap<>();
        stringSetMap.put("added", status.getAdded());
        stringSetMap.put("modified", status.getModified());
        stringSetMap.put("remove", status.getRemoved());
        stringSetMap.put("untracked", status.getUntracked());
        stringSetMap.put("conflict", status.getConflicting());
        stringSetMap.put("change", status.getChanged());
        stringSetMap.put("missing", status.getMissing());
        stringSetMap.put("ignore", status.getIgnoredNotInIndex());
        return stringSetMap;
    }

    public List<Map<String, Object>> getBranch(String path) throws IOException, GitAPIException {
        Git git = Git.open(new File(DefaultCredentials.getRootFolder() + path));
        List<Ref> list = gitServices.getBranch(git);
        List<Map<String, Object>> branchList = new ArrayList<>();
        AtomicLong count = new AtomicLong(1);
        list.forEach(ref -> {
            Map<String, Object> objectMap = new HashMap<>();
            objectMap.put("no", count.getAndIncrement());
            objectMap.put("name", ref.getName());
            objectMap.put("objectName", ref.getObjectId().getName());
            objectMap.put("leafName", ref.getLeaf().getName());
            objectMap.put("targetName", ref.getTarget().getName());
            if (ref.getName().contains("remotes")) {
                objectMap.put("branchType", "remote");
            } else {
                objectMap.put("branchType", "local");
            }
            branchList.add(objectMap);
        });
        return branchList;
    }

    public Map<String, Object> getConfig(String path) throws GitAPIException, IOException {
        Git git = Git.open(new File(DefaultCredentials.getRootFolder() + path));
        StoredConfig storedConfig = gitServices.getConfig(git);
        Map<String, Object> objectMap = new HashMap<>();
        String name = storedConfig.getString("user", null, "name");
        String email = storedConfig.getString("user", null, "email");
        String remoteUrl = storedConfig.getString("remote", "origin", "url");
        objectMap.put("config", Map.of("name", name, "email", email, "remote", remoteUrl));
        Config config = storedConfig.getBaseConfig();
        String baseName = config.getString("user", null, "name");
        String baseEmail = config.getString("user", null, "email");
        objectMap.put("baseConfig", Map.of("name", baseName, "email", baseEmail));
        return objectMap;
    }

    public Map<String, Object> getCommit(String path, String commitId) throws IOException {
        Git git = Git.open(new File(DefaultCredentials.getRootFolder() + path));
        RevCommit revCommit = gitServices.getCommit(git, commitId);
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("message", revCommit.getFullMessage());
        objectMap.put("id", revCommit.getName());
        objectMap.put("name", revCommit.getCommitterIdent().getName());
        objectMap.put("email", revCommit.getCommitterIdent().getEmailAddress());
        objectMap.put("time", revCommit.getCommitTime());
        if (revCommit.getParentCount() > 0) {
            List<Object> parentCommits = new ArrayList<>();
            for (RevCommit revCommit1 : revCommit.getParents()) {
                parentCommits.add(revCommit1.getName());
            }
            objectMap.put("parent", parentCommits);
        }
        return objectMap;
    }

    public Map<DiffEntry.ChangeType, List<String>> commitDiffEntry(String path, String commitId) throws IOException {
        Git git = Git.open(new File(DefaultCredentials.getRootFolder() + path));
        RevCommit revCommit = gitServices.getCommit(git, commitId);
        Map<DiffEntry.ChangeType, List<String>> listMap = new HashMap<>();
        if (revCommit.getParentCount() > 0) {
            List<DiffEntry> diffEntries = gitServices.commitDiffEntry(git, revCommit);
            diffEntries.forEach(diffEntry -> {
                DiffEntry.ChangeType changeType = diffEntry.getChangeType();
                String filePath = (changeType == DiffEntry.ChangeType.DELETE) ? diffEntry.getOldPath() : diffEntry.getNewPath();
                listMap.computeIfAbsent(changeType, key -> new ArrayList<>()).add(filePath);
            });
//            listMap = diffEntries.stream().collect(Collectors.groupingBy(DiffEntry::getChangeType, Collectors.mapping(DiffEntry::getNewPath, Collectors.toList())));
        } else {
            log.error("This Commit {} has not Parent ", commitId);
        }
        return listMap;
    }

    public Map<String, Object> getFiles(String path) throws IOException, GitAPIException {
        Map<String, Object> stringObjectMap = new HashMap<>();
        List<Object> objectList = new ArrayList<>();
        String finalPath = DefaultCredentials.getRootFolder() + path;
        /*Git git = Git.open(new File(finalPath));
        Status status = gitServices.getStatus(git);
        Set<String> ignoreFiles = status.getIgnoredNotInIndex();*/
        File directoryPath = new File(finalPath);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (Objects.requireNonNull(directoryPath.list()).length > 0) {
            File[] listFiles = directoryPath.listFiles();
            for (File file : listFiles) {
                if (!file.isHidden()) {
                    Date date = new Date(file.lastModified());
                    Map<String, Object> fileMap = new HashMap<>();
                    fileMap.put("fileName", file.getName());
                    fileMap.put("isDirectory", file.isDirectory());
                    fileMap.put("lastModified", sdf.format(date));
                    objectList.add(fileMap);
                }
            }
        } else {
            log.error("This path [{}] has not any file ", path);
        }
        stringObjectMap.put("files", objectList);
        stringObjectMap.put("oldPath", path);
        return stringObjectMap;
    }

    public void cloneRepository(GitClone gitClone, String path) throws GitAPIException {
        if (path.isEmpty()) {
            gitClone.setFilePath(DefaultCredentials.getRootFolder() + path);
        } else {
            gitClone.setFilePath(DefaultCredentials.getRootFolder() + path + "/");
        }
        gitServices.cloneRepository(gitClone);
    }

    public void printCommits(String path, String fileName, boolean timestamp, boolean message, boolean email, String[] authors) throws IOException, GitAPIException {
        File gitFile = new File(DefaultCredentials.getRootFolder() + path);
        List<String> authorsLists = Arrays.asList(authors);
        try (Git git = Git.open(gitFile)) {
            File file = new File(DefaultCredentials.getRootFolder() + path + "/" + fileName);
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            LogCommand logCommand = git.log().all();
            long count = 1;
            for (RevCommit revCommit : logCommand.call()) {
                if (authorsLists.contains(revCommit.getCommitterIdent().getEmailAddress())) {
                    saveDetailsInFile(revCommit, count, writer, timestamp, message, email);
                    count++;
                }
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDetailsInFile(RevCommit revCommit, long count, BufferedWriter bufferedWriter, boolean timestamp, boolean message, boolean email) throws IOException {
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

    public Map<String, Object> getDiffByChildCommitAndParentCommit(String path, String commitId, String parentCommitId) throws IOException {
        Git git = Git.open(new File(DefaultCredentials.getRootFolder() + path));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        DiffFormatter diffFormatter = new DiffFormatter(outputStream);
        diffFormatter.setRepository(git.getRepository());
        diffFormatter.setContext(0);
        List<DiffEntry> diffs = diffFormatter.scan(ObjectId.fromString(parentCommitId), ObjectId.fromString(commitId));
        Map<DiffEntry.ChangeType, List<DiffEntry>> changeTypesList = diffs.stream().collect(Collectors.groupingBy(DiffEntry::getChangeType));
        Map<String, Object> stringObjectMap = new HashMap<>();
        changeTypesList.forEach((changeType, diffEntries) -> {
            try {
                diffFormatter.format(diffEntries);
                List<String> lines = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(outputStream.toByteArray()))).lines().collect(Collectors.toList());
                stringObjectMap.put(changeType.name(), lines);
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return stringObjectMap;
    }

    public List<Object> getCommitsByAuthorEmail(String path, String email) throws IOException, GitAPIException {
        Git git = Git.open(new File(DefaultCredentials.getRootFolder() + path));
        List<Object> revCommitList = new ArrayList<>();
        gitServices.getLog(git).forEachRemaining(commit -> {
            if (commit.getCommitterIdent().getEmailAddress().equals(email)) {
                Map<String, Object> singleCommit = new HashMap();
                singleCommit.put("commitId", commit.name());
                singleCommit.put("parentCount", commit.getParentCount());
                singleCommit.put("email", commit.getCommitterIdent().getEmailAddress());
                singleCommit.put("name", commit.getCommitterIdent().getName());
                singleCommit.put("timestamp", commit.getCommitTime());
                revCommitList.add(singleCommit);
            }
        });
        return revCommitList;
    }

    public List<Map<String, Object>> getBlame(String path, String filename) throws GitAPIException, IOException {
        Git git = Git.open(new File(DefaultCredentials.getRootFolder() + path));
        BlameResult blameResult = gitServices.getBlameResult(git, filename);
        RawText rawText = blameResult.getResultContents();
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (int i = 0; i < rawText.size(); i++) {
            PersonIdent personIdent = blameResult.getSourceAuthor(i);
            RevCommit revCommit = blameResult.getSourceCommit(i);
            Map<String, Object> stringObjectMap = new HashMap<>();
            stringObjectMap.put("name", personIdent.getName());
            stringObjectMap.put("date", revCommit.getCommitTime());
            stringObjectMap.put("commitId", revCommit.getName());
            stringObjectMap.put("line", blameResult.getSourceLine(i) + 1);
            stringObjectMap.put("text", rawText.getString(i));
            stringObjectMap.put("message", revCommit.getFullMessage());
            mapList.add(stringObjectMap);
        }
        return mapList;
    }

    public Map<String, Object> status(String path) throws IOException, GitAPIException {
        Git git = Git.open(new File(DefaultCredentials.getRootFolder() + path));
        Status status = gitServices.getStatus(git);
        Map<String, Object> stringSetMap = new HashMap<>();
        stringSetMap.put("MODIFY", Map.of("added", status.getAdded(), "modified", status.getModified()));
        return stringSetMap;
    }
}
