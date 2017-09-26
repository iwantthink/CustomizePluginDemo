package com.hypers

import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project

import javax.xml.crypto.dsig.TransformException

public class RyanInjectPlugin implements Plugin<Project> {


    public void apply(Project project) {
//        project.afterEvaluate {
//            project.android.applicationVariants.each { variant ->
//                logE("variant.name = $variant.name")
//                def preDexTask = project.tasks.findByName("preDex${variant.name.capitalize()}")
//            }
//        }

        project.extensions.create('RYAN', MyExtension, project)
        def isApp = project.plugins.hasPlugin(AppPlugin);
        if (isApp) {
            //找到app 而不是library
            def android = project.extensions.getByType(AppExtension)
            def transform = new MyTransform(project)
            android.registerTransform(transform)
        }
    }
}

public class MyExtension {
    List<String> includePkg = []
    List<String> excludeClass = []
    String oldDir

    MyExtension(Project project) {

    }
}


class MyTransform extends Transform {
    boolean debug = true;
    Project mProject
    MyExtension mExtension;

    public MyTransform(Project project) {
        mProject = project;
        mExtension = mProject.getExtensions().getByName('RYAN')
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
        return true
    }

//    @Override
//    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs, TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
//        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental)
//    }

    @Override
    void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation)
        int i = 0;
        transformInvocation.inputs.each { TransformInput transformInput ->
            log("transformInput ${i++}")
            log("directoryInputs ========================================")
            transformInput.directoryInputs.findAll {
                DirectoryInput directoryInput ->
                    File dest = transformInvocation.outputProvider.getContentLocation(
                            directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY
                    )
                    String buildTypes = directoryInput.file.name
                    String productFlavors = directoryInput.file.parentFile.name
                    traverseFolder(directoryInput.file)
                    log "Copying \n${directoryInput.name} \nto \n${dest.absolutePath}"
                    FileUtils.copyDirectory(directoryInput.file, dest);
            }

            log("jarInputs ========================================")
            transformInput.jarInputs.findAll { JarInput jarInput ->
                String destName = jarInput.name
                def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath)
                if (destName.endsWith(".jar")) {
                    destName = destName.substring(0, destName.length() - 4)
                }
                File dest = transformInvocation.outputProvider.getContentLocation(
                        destName + "_" + hexName, jarInput.contentTypes, jarInput.scopes, Format.JAR
                )
                //TODO 处理JAR进行字节码注入
//                mProject.logger.error "Copying \n${jarInput.file.absolutePath} \nto \n${dest.absolutePath}"
                try {
                    FileUtils.copyFile(jarInput.file, dest)
                } catch (Exception e) {
                    mProject.logger.error("error = " + e.getMessage())
                }

            }

        }
    }

    def traverseFolder(File rooFile) {
        log("rootFile.name = $rooFile.name")
        if (rooFile != null && rooFile.exists()) {
            File[] files = rooFile.listFiles();
            if (files != null && files.length != 0) {
                files.each { File file ->
                    logW("file path = $file.absolutePath")
                    if (file.isDirectory()) {
                        log("<$file.name> is directory")
                        traverseFolder(file)
                    } else {
                        log("<$file.name> is file")
                        boolean needFilter = filterFile(file)
                        logW('file name = ' + file.name)
                        logW("needFilter = $needFilter")
                        if (needFilter) {
                            try {
                                Inject.injectClass(file.absolutePath, file.name, file.absolutePath.replace("\\$file.name", ""))
                            } catch (Exception e) {
                                logW("Exception = $e.getMessage()")
                            }

                        }
                    }
                }
            } else {
                log('files is empty')
            }
        }
    }

    boolean filterFile(File file) {
        def needDeal = false
        log("file.absolutePath = $file.absolutePath")
        mExtension.includePkg.each { String item ->
            log("includePkg = $item")
            def replacedPath = file.absolutePath.replace("\\$file.name", "")
            def replacedItem = item.replace(".", "\\")
            log("replacedItem = $replacedItem")
            log("replacedPath = $replacedPath")
            //指定包名，并且不是以R开头的资源文件，不是BuildConfig.class,
            if (replacedPath.endsWith(replacedItem) && !file.name.startsWith("R") && !file.name.startsWith("BuildConfig")) {
                logW("file $file.name need modify")
                needDeal = true
                //排除忽略的类文件
                mExtension.excludeClass.each {
                    logW("excludeClass = $it")
                    if (it.equals(file.name)) {
                        logW("file in excludeClass")
                        needDeal = false
                    }
                }
            }
        }
        return needDeal
    }


    def log(str) {
        if (false) {
            mProject.logger.error("log: $str")
        }
    }

    def logW(str) {
        if (true) {
            mProject.logger.error("log: $str")
        }
    }

}