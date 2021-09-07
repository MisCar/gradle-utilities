package org.miscar.task;

import edu.wpi.first.toolchain.NativePlatforms;
import org.gradle.api.DefaultTask;

public class TestTask extends DefaultTask {
    private String capitalize(String x) {
        return x.substring(0, 1).toUpperCase() + x.substring(1);
    }

    public TestTask() {
        super.setGroup("MisCar");
        super.setDescription("Run the unit tests");
        super.getDependsOn().add("runTest" + capitalize(NativePlatforms.desktop) + "ReleaseGoogleTestExe");
    }
}
