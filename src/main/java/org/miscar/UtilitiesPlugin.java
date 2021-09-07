package org.miscar;

import edu.wpi.first.gradlerio.frc.FRCExtension;
import edu.wpi.first.gradlerio.frc.RoboRIO;
import edu.wpi.first.gradlerio.wpi.WPIExtension;
import edu.wpi.first.gradlerio.wpi.dependencies.WPIDepsExtension;
import edu.wpi.first.nativeutils.NativeUtilsExtension;
import edu.wpi.first.toolchain.NativePlatforms;
import jaci.gradle.deploy.DeployExtension;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.language.base.LanguageSourceSet;
import org.gradle.language.cpp.CppSourceSet;
import org.gradle.language.cpp.internal.DefaultCppSourceSet;
import org.gradle.nativeplatform.NativeExecutableBinarySpec;
import org.gradle.nativeplatform.NativeExecutableSpec;
import org.gradle.nativeplatform.internal.DefaultNativeExecutableSpec;
import org.gradle.platform.base.BinarySpec;
import org.gradle.platform.base.VariantComponentSpec;
import org.miscar.task.*;

import java.io.IOException;


public class UtilitiesPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        var miscar = project.getExtensions().create("miscar", MisCarExtension.class);

        project.getTasks().register("intellisense", IntellisenseTask.class);
        project.getTasks().register("buildAthena", BuildAthenaTask.class);
        project.getTasks().register("format", FormatTask.class);
        project.getTasks().register("test", TestTask.class);

        project.afterEvaluate(p -> {
            if (miscar.getUseSubmodules().getOrElse(true)) {
                try {
                    Runtime.getRuntime().exec("git submodule init").waitFor();
                    var process = Runtime.getRuntime().exec("git submodule update --recursive --remote");
                    process.waitFor();
                    if (process.getErrorStream().readAllBytes().length != 0) {
                        System.out.println("[MisCar] Warning: Could not update submodules.");
                    } else {
                        System.out.println("[MisCar] Submodules updated successfully.");
                    }
                } catch (IOException | InterruptedException exception) {
                    System.out.println("[MisCar] Warning: Could not update submodules.");
                    System.out.println(exception);
                }
            }

            var frc = p.getExtensions().getByType(FRCExtension.class);
            var deploy = p.getExtensions().getByType(DeployExtension.class);
            deploy.getTargets().target("robot", RoboRIO.class, robot -> {
                var rio = (RoboRIO)robot;
                rio.setTeam(frc.getTeamNumber());
            });

            var artifacts = deploy.getArtifacts();
            artifacts.nativeArtifact("code", artifact -> {
                artifact.getTargets().add("robot");
                artifact.setComponent("program");
                artifact.setBuildType(frc.getDebugOrDefault(false) ? "Debug" : "Release");
            });
            artifacts.fileTreeArtifact("staticFiles", artifact -> {
               artifact.getTargets().add("robot");
               artifact.setFiles(p.fileTree("src/main/deploy"));
               artifact.setDirectory("/home/lvuser/deploy");
            });

            var platformConfigs = p.getExtensions().getByType(NativeUtilsExtension.class).getPlatformConfigs();

            // CTRE Phoenix
            var windows = platformConfigs.named("windowsx86-64").getOrNull();
            if (windows != null) {
                windows.getCppCompiler().getArgs().add("/wd4250");
            }
            // REV Robotics
            platformConfigs.all(config -> {
                if (!config.getPlatformPath().equals("windows/x86-64")) {
                    config.getCppCompiler().getArgs().add("-Wno-attributes");
                }
            });

//            var program = p.getObjects().named(VariantComponentSpec.class, "program");
//            program.getBinaries().create("cpp", NativeExecutableBinarySpec.class);
//            System.out.println(program.getBinaries());
//            var program = new DefaultNativeExecutableSpec();
//            program.targetPlatform(NativePlatforms.roborio);
//            program.targetPlatform(NativePlatforms.desktop);
//            System.out.println("Doing");
//            var cpp = p.getObjects().named(CppSourceSet.class, "cpp");
//            System.out.println("Done");
//            System.out.println(cpp.getClass());
//            var source = cpp.getSource();
//            source.srcDir("src/main/cpp");
//            source.srcDir("libmiscar/src/main/cpp/miscar");
//            source.include("**/*.cpp");
//            var exportedHeaders = cpp.getExportedHeaders();
//            exportedHeaders.srcDir("src/main/cpp");
//            exportedHeaders.srcDir("libmiscar/src/main/cpp");
//            var deps = p.getExtensions().getByType(WPIDepsExtension.class);
//            deps.wpilib(program);
//            deps.getVendor().cpp(program);
//            program.getSources().put("cpp", cpp);
//            programProperty.set(program);
        });
    }
}
