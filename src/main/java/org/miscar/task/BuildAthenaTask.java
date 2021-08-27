package org.miscar.task;

import org.gradle.api.DefaultTask;

public class BuildAthenaTask extends DefaultTask {
    public BuildAthenaTask() {
        super.setGroup("MisCar");
        super.setDescription("Build the project for the roboRIO");
        super.getDependsOn().add("programLinuxathenaReleaseExecutable");
    }
}
