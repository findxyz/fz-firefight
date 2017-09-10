package xyz.fz.fire.fight.run;

import org.apache.commons.io.IOUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import xyz.fz.fire.fight.model.Task;
import xyz.fz.fire.fight.model.TaskHelper;
import xyz.fz.fire.fight.util.BaseUtil;

import java.io.InputStream;

@Component
public class TaskLoadRunner implements CommandLineRunner {

    @Override
    public void run(String... strings) throws Exception {
        InputStream is = this.getClass().getResourceAsStream("/task.json");
        String taskJson = IOUtils.toString(is);
        Task task = BaseUtil.parseJson(taskJson, Task.class);
        TaskHelper.taskEasy(task);
    }
}
