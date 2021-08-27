package org.miscar;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.miscar.task.*;


public class UtilitiesPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        project.getTasks().register("intellisense", IntellisenseTask.class);
        project.getTasks().register("buildAthena", BuildAthenaTask.class);
        project.getTasks().register("format", FormatTask.class);
        project.getTasks().register("updateSubmodules", UpdateSubmodulesTask.class);
        project.getTasks().register("test", TestTask.class);
    }
}
