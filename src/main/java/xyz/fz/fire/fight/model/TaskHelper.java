package xyz.fz.fire.fight.model;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class TaskHelper {

    private static String host = "";

    private static Map<String, String> sshUserMap = Maps.newHashMap();

    private static Map<String, Object> taskMap = Maps.newHashMap();

    private static List<String> taskList = Lists.newArrayList();

    public static void taskEasy(Task task) {
        host = task.getHost();
        for (Task.User user : task.getUsers()) {
            sshUserMap.put(user.getUserName(), user.getPassWord());
        }
        for (Task.ShellTask shellTask : task.getTasks()) {
            taskMap.put(shellTask.getKey(), shellTask);
            taskList.add(shellTask.getTitle() + "(" + shellTask.getDesc() + ")" + "【" + shellTask.getKey() + "】");
        }
    }

    public static String host() {
        return host;
    }

    public static String password(String userName) {
        return sshUserMap.get(userName);
    }

    public static Task.ShellTask shellTask(String key) {
        return (Task.ShellTask) taskMap.get(key);
    }

    public static List<String> taskList() {
        return taskList;
    }

}
