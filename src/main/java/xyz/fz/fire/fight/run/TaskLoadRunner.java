package xyz.fz.fire.fight.run;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import xyz.fz.fire.fight.model.Task;
import xyz.fz.fire.fight.model.TaskHelper;
import xyz.fz.fire.fight.util.BaseUtil;

@Component
public class TaskLoadRunner implements CommandLineRunner {

    @Value(value = "classpath:task.json")
    private Resource taskResource;

    @Override
    public void run(String... strings) throws Exception {
        String taskJson = FileUtils.readFileToString(taskResource.getFile());
        Task task = BaseUtil.parseJson(taskJson, Task.class);
        TaskHelper.taskEasy(task);
    }
}
