package org.miscar.task;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskExecutionException;

import java.io.IOException;

public class UpdateSubmodulesTask extends DefaultTask {
    public UpdateSubmodulesTask() {
        super.setGroup("MisCar");
        super.setDescription("Updates all git submodules");
    }

    @TaskAction
    void apply() {
        try {
            Runtime.getRuntime().exec("git submodule init");
            Runtime.getRuntime().exec("git submodule update");
        } catch (IOException exception) {
            super.getState().addFailure(new TaskExecutionException(this, exception));
        }
    }
}
