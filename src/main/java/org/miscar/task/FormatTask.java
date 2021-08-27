package org.miscar.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class FormatTask extends DefaultTask {
    public FormatTask() {
        super.setGroup("MisCar");
        super.setDescription("Formats the project using wpiformat");
    }

    @TaskAction
    void apply() {
        var command = "wpiformat";

        if (System.getProperty("os.name").startsWith("Windows")) {
            command = "py -m wpiformat";
        }

        try {
            var scanner = new Scanner(new InputStreamReader(Runtime.getRuntime().exec(command).getInputStream())).useDelimiter("\\A");
            if (scanner.hasNext()) {
                System.out.println(scanner.next());
                super.getState().addFailure(new TaskExecutionException(this, new Exception("Format issues detected")));
            }
        } catch (IOException exception) {
            super.getState().addFailure(new TaskExecutionException(this, exception));
        }
    }
}
