package com.hypers

import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.pipeline.TransformManager
import groovy.io.FileType
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
    boolean debug = true
    Project mProject
    MyExtension mExtension

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
        return false
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
            log("directoryInputs")
            transformInput.directoryInputs.findAll {
                DirectoryInput directoryInput ->
                    File dest = transformInvocation.outputProvider.getContentLocation(
                            directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY
                    )
                    HashMap<String, File> modifyMap = new HashMap<>();
                    logW("directoryInput path = $directoryInput.file.absolutePath")
                    directoryInput.file.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) { File classFile ->
                        boolean needModify = filterFile(classFile)
                        if (needModify) {
                            try {
                                File modifiedFile = Inject.injectClass(directoryInput.file, classFile, transformInvocation.context)
                                modifyMap.put(classFile.absolutePath.replace(directoryInput.file.absolutePath, ""), modifiedFile)
                            } catch (Exception e) {
                                logW(e.message)
                            }
                            logW("over")
                        }
                    }
                    logW("dest.absolute = $dest.absolutePath")
                    FileUtils.copyDirectory(directoryInput.file, dest);
                    modifyMap.each {
                        logW("key = $it.key")
                        logW("value = $it.value.absolutePath")
                        File targetFile = new File(dest.absolutePath + "\\$it.key")
                        if (targetFile.exists()) {
                            targetFile.delete()
                        }
                        FileUtils.copyFile(it.value, targetFile)
                    }
            }

            log("jarInputs")
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

//    def traverseFolder(File rooFile, String buildTypes, String productFlavors, String patchDir) {
//        log("rootFile.name = $rooFile.name")
//        if (rooFile != null && rooFile.exists()) {
//            File[] files = rooFile.listFiles();
//            if (files != null && files.length != 0) {
//                files.each { File file ->
//                    logW("file path = $file.absolutePath")
//                    if (file.isDirectory()) {
//                        log("<$file.name> is directory")
//                        traverseFolder(file, buildTypes, productFlavors, patchDir)
//                    } else {
//                        log("<$file.name> is file")
//                        boolean needModify = filterFile(file)
//                        logW('file name = ' + file.name)
//                        logW("needModify = $needModify")
//                        if (needModify) {
//                            try {
//                                String splitStr = "$productFlavors\\\\$buildTypes\\\\"
//                                def classFile = file.absolutePath.split(splitStr)[1]
//                                logW("buildTypes = $buildTypes")
//                                logW("productFlavors = $productFlavors")
//                                logW("classFIle = $classFile")
//                                logW("patchDir = $patchDir")
//                                def outputFile = new File("${patchDir}\\${classFile}")
//                                outputFile.getParentFile().mkdirs()
//                                logW("outputFile = $outputFile.absolutePath")
//
//                                Inject.injectClass(file.absolutePath, outputFile)
//                            } catch (Exception e) {
//                                logW("Exception = $e.message")
//                            }
//
//                        }
//                    }
//                }
//            } else {
//                log('files is empty')
//            }
//        }
//    }

    boolean filterFile(File file) {
        def needDeal = false
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