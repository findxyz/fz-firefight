package xyz.fz.fire.fight.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.fz.fire.fight.model.Task;
import xyz.fz.fire.fight.model.TaskHelper;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.StreamTokenizer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class SSHUtil {

    private static Logger logger = LoggerFactory.getLogger(SSHUtil.class);

    public static final String RUNNING = "running";

    public static final String CONSOLE = "console";

    public static Cache<Object, Object> cache = CacheBuilder.newBuilder().expireAfterWrite(120, TimeUnit.SECONDS).initialCapacity(2).build();

    public static void execute(Task.ShellTask shellTask) {

        if (shellTask == null) {
            cache.invalidate(RUNNING);
            return;
        }

        cacheConsoleContent("任务标题：" + shellTask.getTitle() + "\n");
        cacheConsoleContent("任务描述：" + shellTask.getDesc() + "\n");

        for (Task.Step step : shellTask.getSteps()) {
            execute(TaskHelper.host(), step.getUserName(), TaskHelper.password(step.getUserName()), step);
        }

        cache.invalidate(RUNNING);
        cacheConsoleContent("任务执行完成");
    }

    private static void execute(String host, String username, String password, Task.Step step) {

        String introduction = step.getIntroduction();
        String runCommand = step.getRunCommand();
        String monitorCommand = step.getMonitorCommand();
        String monitorWanted = step.getMonitorWanted();
        Integer monitorMaxSeconds = step.getMonitorMaxSeconds();
        Integer autoClosedAfterSeconds = step.getAutoClosedAfterSeconds();

        logger.warn("任务阶段：{}", introduction);
        cacheConsoleContent("任务阶段：" + introduction + "\n");
        execute(host, username, password, runCommand, autoClosedAfterSeconds);

        if (StringUtils.isNotBlank(monitorCommand)) {
            DateTime maxTime = null;
            if (monitorMaxSeconds > 0) {
                maxTime = new DateTime().plusSeconds(monitorMaxSeconds);
            }
            while (true) {
                try {
                    Thread.sleep(5000L);
                } catch (InterruptedException e) {
                    // e.printStackTrace();
                }
                if (maxTime != null) {
                    DateTime now = new DateTime();
                    if (maxTime.isBefore(now)) {
                        logger.error("想要结果获取超时，本次任务执行终止");
                        cacheConsoleContent("想要结果获取超时，本次任务执行终止\n");
                        throw new RuntimeException("获取想要结果超时");
                    }
                    String monitorResult = execute(host, username, password, monitorCommand, autoClosedAfterSeconds);
                    logger.warn("Monitor result: {}", monitorResult);
                    cacheConsoleContent("检测返回结果：" + monitorResult + "\n");
                    if (StringUtils.contains(monitorResult, monitorWanted)) {
                        break;
                    }
                }
            }
        }
    }

    private static String execute(String host, String username, String password, String cmd, Integer timeout) {

        StringBuilder result = new StringBuilder();
        JSch jsch = new JSch();
        Session session = null;
        ChannelExec channel = null;
        try {
            // connect session
            session = jsch.getSession(username, host);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();

            // exec command remotely
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(cmd);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PrintStream printStream = new PrintStream(outputStream);
            channel.setErrStream(printStream);
            channel.connect();

            DateTime maxTime = null;
            if (timeout > 0) {
                maxTime = new DateTime().plusSeconds(timeout);
            }

            // get output
            InputStream in = channel.getInputStream();
            int length = 1024 * 8;
            byte[] buffer = new byte[length];
            while (true) {
                Thread.sleep(1000L);
                if (maxTime != null) {
                    DateTime now = new DateTime();
                    if (maxTime.isBefore(now)) {
                        logger.warn("执行程序长时间阻塞，超时自动退出");
                        cacheConsoleContent("执行程序长时间阻塞，超时自动退出（一般为正常情况）\n");
                        break;
                    }
                }
                while (in.available() > 0) {
                    int i = in.read(buffer, 0, length);
                    if (i == StreamTokenizer.TT_EOF) {
                        break;
                    }
                    String bufferStr = new String(buffer, 0, i);
                    result.append(bufferStr);
                    logger.info("服务器输出：\n{}", bufferStr);
                    cacheConsoleContent("服务器输出：\n");
                    cacheConsoleContent(bufferStr);
                }
                if (channel.isClosed()) {
                    printStream.flush();
                    String message = new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
                    if (StringUtils.isNotBlank(message)) {
                        logger.error("Message: {}", message);
                        cacheConsoleContent("服务器结果：" + message + "\n");
                    }
                    break;
                }
            }
        } catch (Exception e) {
            logger.error(BaseUtil.getExceptionStackTrace(e));
            cacheConsoleContent("发生异常：" + e.getMessage() + "\n");
        } finally {
            // close connect
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
        return result.toString();
    }

    private static void cacheConsoleContent(String consoleContent) {
        StringBuilder consoleBuilder;
        Object console = cache.getIfPresent(CONSOLE);
        if (console == null) {
            consoleBuilder = new StringBuilder();
        } else {
            consoleBuilder = (StringBuilder) console;
        }
        consoleBuilder.append(consoleContent);
        cache.put(CONSOLE, consoleBuilder);
    }
}
