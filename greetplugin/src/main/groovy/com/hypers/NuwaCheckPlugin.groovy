package com.hypers

import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project

public class NuwaCheckPlugin implements Plugin<Project> {
    public void apply(Project project) {
//        project.afterEvaluate {
//            project.android.applicationVariants.each { variant ->
//                logE("variant.name = $variant.name")
//                def preDexTask = project.tasks.findByName("preDex${variant.name.capitalize()}")
//            }
//        }

        def isApp = project.plugins.hasPlugin(AppPlugin);
        if (isApp) {
            //找到app 而不是library
            def android = project.extensions.getByType(AppExtension)
            def transform = new MyTransform(project)
            android.registerTransform(transform)
        }

        def extension = project.extensions.create('RYAN', MyExtension, project)

        project.task('RYAN') << {
            project.logger.error("extension.excludeClass = $extension.excludeClass")
            project.logger.error("extension.includePkg = $extension.includePkg")
            project.logger.error("extension.oldDir = $extension.oldDir")
        }
    }
}

public class MyExtension {
    HashSet<String> includePkg = []
    HashSet<String> excludeClass = []
    String oldDir

    MyExtension(Project project) {

    }
}


class MyTransform extends Transform {

    Project mProject

    public MyTransform(Project project) {
        mProject = project;
    }


    @Override
    String getName() {
        return 'MyTransform'
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

//    @Override
//    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
//        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental)
//    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        mProject.logger.error '--------------------transform-----------------'

        transformInvocation.inputs.findAll { TransformInput input ->
            mProject.logger.error '--------------------process class-----------------'
            input.directoryInputs.findAll {
                DirectoryInput directoryInput ->
                    File dest = transformInvocation.outputProvider.getContentLocation(
                            directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY
                    )
                    String buildTypes = directoryInput.file.name
                    String productFlavors = directoryInput.file.parentFile.name
                    //TODO
                    processClass(directoryInput)

                    mProject.logger.error "Copying \n${directoryInput.name} \nto \n${dest.absolutePath}"
                    FileUtils.copyDirectory(directoryInput.file, dest);
            }
            mProject.logger.error '--------------------process jar-----------------'

            input.jarInputs.findAll { JarInput jarInput ->
                String destName = jarInput.name
                mProject.logger.error("destName = $destName")
                def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath)
                if (destName.endsWith(".jar")) {
                    destName = destName.substring(0, destName.length() - 4)
                }
                File dest = transformInvocation.outputProvider.getContentLocation(
                        destName + "_" + hexName, jarInput.contentTypes, jarInput.scopes, Format.JAR
                )
                //TODO 处理JAR进行字节码注入
                mProject.logger.error "Copying \n${jarInput.file.absolutePath} \nto \n${dest.absolutePath}"
                try {
                    FileUtils.copyFile(jarInput.file, dest)
                } catch (Exception e) {
                    mProject.logger.error("error = " + e.getMessage())
                }

            }

        }
    }

    def processClass(DirectoryInput directoryInput) {
        mProject.logger.error("directoryInput.name = $directoryInput.name")
    }

}