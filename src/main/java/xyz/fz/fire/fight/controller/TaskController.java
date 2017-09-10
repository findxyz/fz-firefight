package xyz.fz.fire.fight.controller;

import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import xyz.fz.fire.fight.model.TaskHelper;
import xyz.fz.fire.fight.util.SSHUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static xyz.fz.fire.fight.util.SSHUtil.CONSOLE;
import static xyz.fz.fire.fight.util.SSHUtil.RUNNING;
import static xyz.fz.fire.fight.util.SSHUtil.cache;

@Controller
public class TaskController implements InitializingBean {

    private static String PASS_CODE = "";

    private static String FINAL_ANSWER = "";

    private static final String DATE_FORMAT = "yyyyMMdd";

    private static final String SUCCESS = "success";

    private static final String BLANK_COUNT = "blankCount";

    @Value("${task.passCode}")
    private String taskPassCode;

    @Value("${task.finalAnswer}")
    private String taskFinalAnswer;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @RequestMapping("/list")
    @ResponseBody
    public List<String> list(@RequestParam(required = false, defaultValue = "") String passCode) {
        String todayPassCode = passCode();
        if (StringUtils.equals(todayPassCode, passCode)) {
            return TaskHelper.taskList();
        } else {
            return null;
        }
    }

    @RequestMapping("/execute")
    @ResponseBody
    public Map<String, Object> execute(@RequestParam(required = false, defaultValue = "") String cmd) {

        Map<String, Object> result = Maps.newHashMap();
        String todayPassCode = passCode();
        if (StringUtils.startsWith(cmd, todayPassCode)) {
            if (cache.getIfPresent(RUNNING) == null) {
                cache.put(RUNNING, true);
                String finalCmd = cmd.replace(todayPassCode, "");
                executorService.execute(() -> {
                    try {
                        SSHUtil.execute(TaskHelper.shellTask(finalCmd));
                    } catch (Exception e) {
                        cache.invalidate(RUNNING);
                    }
                });
                result.put(SUCCESS, true);
            } else {
                result.put(SUCCESS, false);
            }
        } else {
            result.put(SUCCESS, false);
        }
        return result;
    }

    @RequestMapping("/console")
    @ResponseBody
    public String console(@RequestParam(required = false, defaultValue = "") String passCode) {

        if (!StringUtils.equals(passCode(), passCode)) {
            return "";
        }

        Object console = cache.getIfPresent(CONSOLE);
        if (console != null) {
            cache.invalidate(CONSOLE);
            cache.invalidate(BLANK_COUNT);
            StringBuilder consoleBuilder = (StringBuilder) console;
            return consoleBuilder.toString();
        } else {
            Object blankCountObject = cache.getIfPresent(BLANK_COUNT);
            Integer blankCount;
            if (blankCountObject == null) {
                blankCount = 0;
                cache.put(BLANK_COUNT, blankCount);
            } else {
                blankCount = (Integer) blankCountObject;
                blankCount = blankCount + 1;
                cache.put(BLANK_COUNT, blankCount);
            }
            if (blankCount > 5) {
                return "CONSOLE_OVER";
            } else {
                return "";
            }
        }
    }

    private String passCode() {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return PASS_CODE + sdf.format(new Date()) + FINAL_ANSWER;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        PASS_CODE = taskPassCode;
        FINAL_ANSWER = taskFinalAnswer;
    }
}
