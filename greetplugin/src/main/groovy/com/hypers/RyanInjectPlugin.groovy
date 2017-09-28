package com.hypers

import com.android.build.api.transform.*
import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.build.gradle.internal.pipeline.TransformTask
import com.android.build.gradle.internal.transforms.ProGuardTransform
import groovy.io.FileType
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project

import javax.xml.crypto.dsig.TransformException

public class RyanInjectPlugin implements Plugin<Project> {
    //variant 对应的 混淆配置文件集合
    Project mProject
    Map<String, List<File>> mProguardConfigFile = new  HashMap<>()

    public void apply(Project project) {
        mProject = project
        project.afterEvaluate {
            def flavorsAndTypes = getProductFlavorsBuildTypes(project)
            flavorsAndTypes.each { item ->
                copyMappingFile(project, item)
//                doDex(project)
            }
        }
        project.tasks.create('printProguardConfigFile') << {
            mProguardConfigFile.each {
                logE("key = $it.key")
                it.value.each {
                    logE("value = $it.absolutePath")
                }
            }
        }
        project.extensions.create('RYAN', MyExtension, project)
        def isApp = project.plugins.hasPlugin(AppPlugin);
        if (isApp) {
            //找到app 而不是library
            def android = project.extensions.getByType(AppExtension)
            def transform = new MyTransform(project)
            android.registerTransform(transform)
        }
    }

    def logE(str) {
        if (true) {
            mProject.logger.error("log: $str")
        }
    }

    def log(str) {
        if (false) {
            mProject.logger.error("log: $str")
        }
    }


    def doDex(Project project) {
        def modifiedClassDir
        def flavorsAndTypes = getProductFlavorsBuildTypes(project)
        flavorsAndTypes.each {
            logE("flavorsAndTypes = $it")
        }
    }

    List getProductFlavorsBuildTypes(Project project) {
        def app = project.extensions.getByType(AppExtension)
        List composedList = []
        def flavors = app.productFlavors
        def types = app.getBuildTypes()
        if (flavors) {
            flavors.each { productFlavor ->
                if (types) {
                    types.each { buildType ->
                        composedList.add("$productFlavor.name\\$buildType.name")
                    }
                } else {
                    composedList.add("$productFlavor.name")
                }
            }
        } else {
            types.each { buildType ->
                composedList.add("$buildType.name")

            }
        }
        return composedList
    }

    def copyMappingFile(Project project, String flavorsAndTypes) {
        String[] arrays = flavorsAndTypes.split("\\\\")
        def partMappingPath = ""
        def i = 1
        def changedFlavorsAndTypes = ""
        arrays.each {

            changedFlavorsAndTypes += it.replace(it.substring(0, 1), it.substring(0, 1).toUpperCase())
            partMappingPath += it
            if (i < arrays.length) {
                partMappingPath += "/"
            }
            i++
        }
        logE("changed flavorsAndTypes = $changedFlavorsAndTypes")
        logE("partMappingPath = $partMappingPath")
        def proguardTask = project.tasks.findByName("transformClassesAndResourcesWithProguardFor${changedFlavorsAndTypes}")
        if (proguardTask) {
            logE("transformClassesAndResourcesWithProguardFor${changedFlavorsAndTypes} exsit")
            proguardTask.doLast {
                def mapFile = new File("$project.buildDir/outputs/mapping/$partMappingPath/mapping.txt")
                logE(mapFile.exists() ? "mapFile exist" : "mapFile not exist")
                logE("mapFile path = $mapFile.absolutePath")
                def mapCopyDir = new File("$project.buildDir/outputs/ryan/$partMappingPath")
                if (!mapCopyDir.exists()) {
                    mapCopyDir.mkdirs()
                }
                def mapCopyFile = new File("$mapCopyDir.absolutePath/mapping.txt")
                if (!mapCopyFile.exists()) {
                    mapCopyFile.createNewFile()
                }
                FileUtils.copyFile(mapFile, mapCopyFile)
                List<File> fileList = getProguardConfigFile(project, proguardTask, partMappingPath)
                mProguardConfigFile.put(flavorsAndTypes, fileList)
            }

        }
    }

    /**
     *
     * @param project
     * @param proguardTask 实际是一个TransformTask...(输出metaClass)
     * @return
     */
    def getProguardConfigFile(Project project, TransformTask proguardTask, String partPath) {
        def mExtension = project.extensions.findByName('RYAN') as MyExtension
        def oldDir = new File(mExtension.oldDir)
        if (oldDir.exists()) {
            logE("oldDir exists")
            def mappingFile = new File("$oldDir.absolutePath/$partPath/mapping.txt")
            //proguardTask 存在一个方法获取transform
            ProGuardTransform proGuardTransform = proguardTask.getTransform()
            //获取所有的混淆的配置文件。。 这个方法是ProguardTRansform 的接口中的方法
            //代码好像不提示。。。 但是 我打出来之后是可以使用
            proGuardTransform.getAllConfigurationFiles()
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
            logW("directoryInputs")
            transformInput.directoryInputs.findAll {
                DirectoryInput directoryInput ->
                    File dest = transformInvocation.outputProvider.getContentLocation(
                            directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY
                    )
                    HashMap<String, File> modifyMap = new HashMap<>();
                    log("directoryInput path = $directoryInput.file.absolutePath")
                    directoryInput.file.traverse(type: FileType.FILES, nameFilter: ~/.*\.class/) { File classFile ->
                        log("classFile = $classFile.name")
                        boolean needModify = filterFile(classFile)
                        if (needModify) {
                            try {
                                File modifiedFile = Inject.injectClass(directoryInput.file, classFile, transformInvocation.context)
                                modifyMap.put(classFile.absolutePath.replace(directoryInput.file.absolutePath, ""), modifiedFile)
                            } catch (Exception e) {
                                log(e.message)
                            }
                            log("over")
                        }
                    }
                    log("dest.absolute = $dest.absolutePath")
                    FileUtils.copyDirectory(directoryInput.file, dest);
                    modifyMap.each {
                        log("key = $it.key")
                        log("value = $it.value.absolutePath")
                        File targetFile = new File(dest.absolutePath + "\\$it.key")
                        if (targetFile.exists()) {
                            targetFile.delete()
                        }
                        FileUtils.copyFile(it.value, targetFile)
                    }
            }

            logW("jarInputs")
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
                FileUtils.copyFile(jarInput.file, dest)
            }

        }
    }

    boolean filterFile(File file) {
        def needDeal = false
        mExtension.includePkg.each { String item ->
            log("includePkg = $item")
            def replacedPath = file.absolutePath.replace("\\$file.name", "")
            def replacedItem = item.replace(".", "\\")
            log("replacedItem = $replacedItem")
            log("replacedPath = $replacedPath")
            //指定包名，并且不是以R开头的资源文件，不是BuildConfig.class
            //TODO 完善判断过滤文件的机制
            if (replacedPath.endsWith(replacedItem) &&
                    !file.name.startsWith("R\$") &&
                    !file.name.startsWith("BuildConfig") &&
                    !file.name.equals("R.class")) {
                log("file $file.name need modify")
                needDeal = true
                //排除忽略的类文件
                mExtension.excludeClass.each {
                    log("excludeClass = $it")
                    if (it.equals(file.name)) {
                        log("file in excludeClass")
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
