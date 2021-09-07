package org.miscar.task;

import edu.wpi.first.vscode.tooling.ToolChainGenerator;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class IntellisenseTask extends DefaultTask {
    public IntellisenseTask() {
        super.setGroup("MisCar");
        super.setDescription("Enables clangd intellisense");
    }

    @TaskAction
    void run() {
        var includeDirectories = ToolChainGenerator.generateToolChains(getProject()).iterator().next().getBinaries().iterator().next().getLibHeaders();

        StringBuilder compileFlagsBuilder = new StringBuilder();

        for (var includeDirectory : includeDirectories) {
            compileFlagsBuilder.append("-I").append(includeDirectory).append("\n");
        }

        compileFlagsBuilder.append("-xc++\n");
        compileFlagsBuilder.append("-std=c++17\n");

        var compileFlags = compileFlagsBuilder.toString();

        for (var includeDirectory : includeDirectories) {
            if (includeDirectory.contains(".gradle")) {
                try (var writer = new FileWriter(includeDirectory + "compile_flags.txt")) {
                    writer.write(compileFlags);
                } catch (IOException ignored) {
                }
            }
        }

        var buildDir = getProject().getBuildDir().toString();
        var projectDir = getProject().getProjectDir().toString();

        try (var writer = new FileWriter(Paths.get(buildDir, "compile_flags.txt").toString())) {
            writer.write(compileFlags);
        } catch (IOException ignored) {
        }

        try {
            Files.createLink(Paths.get(projectDir, "compile_flags.txt"), Paths.get(buildDir, "compile_flags.txt"));
        } catch (IOException ignored) {
        }
    }
}
