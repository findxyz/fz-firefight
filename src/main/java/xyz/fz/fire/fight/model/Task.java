package xyz.fz.fire.fight.model;

import java.util.List;

public class Task {

    private String host;

    private List<User> users;

    private List<ShellTask> tasks;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<ShellTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<ShellTask> tasks) {
        this.tasks = tasks;
    }

    public static class User {

        private String userName;

        private String passWord;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getPassWord() {
            return passWord;
        }

        public void setPassWord(String passWord) {
            this.passWord = passWord;
        }
    }

    public static class ShellTask {

        private String key;

        private String title;

        private String desc;

        private List<Step> steps;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public List<Step> getSteps() {
            return steps;
        }

        public void setSteps(List<Step> steps) {
            this.steps = steps;
        }
    }

    public static class Step {

        private String userName;

        private String introduction;

        private String runCommand;

        private String monitorCommand;

        private String monitorWanted;

        private Integer monitorMaxSeconds;

        private Integer autoClosedAfterSeconds;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getIntroduction() {
            return introduction;
        }

        public void setIntroduction(String introduction) {
            this.introduction = introduction;
        }

        public String getRunCommand() {
            return runCommand;
        }

        public void setRunCommand(String runCommand) {
            this.runCommand = runCommand;
        }

        public String getMonitorCommand() {
            return monitorCommand;
        }

        public void setMonitorCommand(String monitorCommand) {
            this.monitorCommand = monitorCommand;
        }

        public String getMonitorWanted() {
            return monitorWanted;
        }

        public void setMonitorWanted(String monitorWanted) {
            this.monitorWanted = monitorWanted;
        }

        public Integer getMonitorMaxSeconds() {
            return monitorMaxSeconds;
        }

        public void setMonitorMaxSeconds(Integer monitorMaxSeconds) {
            this.monitorMaxSeconds = monitorMaxSeconds;
        }

        public Integer getAutoClosedAfterSeconds() {
            return autoClosedAfterSeconds;
        }

        public void setAutoClosedAfterSeconds(Integer autoClosedAfterSeconds) {
            this.autoClosedAfterSeconds = autoClosedAfterSeconds;
        }
    }
}
