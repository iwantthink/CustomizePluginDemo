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
import org.apache.tools.ant.taskdefs.condition.Os
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import proguard.*

import javax.xml.crypto.dsig.TransformException

public class RyanInjectPlugin implements Plugin<Project> {
    //variant 对应的 混淆配置文件集合
    Map<String, List<File>> mProguardConfigFile = new HashMap<>()

    Project mProject

    public void apply(Project project) {
        mProject = project
        MyTransform transform
        project.afterEvaluate {
            def flavorsAndTypes = getProductFlavorsBuildTypes(project)
            flavorsAndTypes.each { item ->
                copyMappingFile(project, item)
            }
        }
        logE("====================apply==========================")
        Task ryanDex = project.tasks.create('ryanDex') {
            group 'ryan'
            description 'dex specified class'
        }
        ryanDex.doLast {
            prepareDex(project)
        }
        project.extensions.create('RYAN', MyExtension, project)
        def isApp = project.plugins.hasPlugin(AppPlugin);
        if (isApp) {
            //找到app 而不是library
            def android = project.extensions.getByType(AppExtension)
            transform = new MyTransform(project)
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

    String capitalize(String flavorsAndTypes) {
        String[] arrays = flavorsAndTypes.split("\\\\")
//        def partMappingPath = ""
//        def i = 1
        def changedFlavorsAndTypes = ""
        arrays.each {
//            changedFlavorsAndTypes += it.replace(it.substring(0, 1), it.substring(0, 1).toUpperCase())
            changedFlavorsAndTypes += it.capitalize()
            logE("capitalize = $changedFlavorsAndTypes")
//            partMappingPath += it
//            if (i < arrays.length) {
//                partMappingPath += "/"
//            }
//            i++
        }
        changedFlavorsAndTypes
    }

    def copyMappingFile(Project project, String flavorsAndTypes) {
        def changedFlavorsAndTypes = capitalize(flavorsAndTypes)
        logE("changedFlavorsAndTypes = $changedFlavorsAndTypes")
        logE("flavorsAndTypes = $flavorsAndTypes")
        def proguardTask = project.tasks.findByName("transformClassesAndResourcesWithProguardFor${changedFlavorsAndTypes}")
        if (proguardTask) {
            logE("transformClassesAndResourcesWithProguardFor${changedFlavorsAndTypes} can be find")
            proguardTask.doLast {
                def mapFile = new File("$project.buildDir/outputs/mapping/$flavorsAndTypes/mapping.txt")
                logE(mapFile.exists() ? "mapFile exist" : "mapFile not exist")
                logE("mapFile path = $mapFile.absolutePath")
                def mapCopyDir = new File("$project.buildDir/outputs/ryan/$flavorsAndTypes")
                if (!mapCopyDir.exists()) {
                    mapCopyDir.mkdirs()
                }
                def mapCopyFile = new File("$mapCopyDir.absolutePath/mapping.txt")
                if (!mapCopyFile.exists()) {
                    mapCopyFile.createNewFile()
                }
                FileUtils.copyFile(mapFile, mapCopyFile)
                List<File> fileList = getProguardConfigFile(proguardTask)
                mProguardConfigFile.put(flavorsAndTypes, fileList)
            }

        }
    }

    /**
     *
     * @param proguardTask 实际是一个TransformTask...(输出metaClass)
     * @return List < File >
     */
    def getProguardConfigFile(TransformTask proguardTask) {
        //proguardTask 存在一个方法获取transform
        ProGuardTransform proGuardTransform = proguardTask.getTransform()
        //获取所有的混淆的配置文件。。 这个方法是ProguardTRansform 的接口中的方法
        //代码好像不提示。。。 但是打出来之后是可以使用
        proGuardTransform.getAllConfigurationFiles()
    }

    def getMappingFile(Project project, String flavorAndType) {
        def TAG = "getMappingFile :"
        def mExtension = project.extensions.findByName('RYAN') as MyExtension
        def mappingDir = new File(mExtension.oldDir)
        if (mappingDir.exists()) {
            logE("$TAG mappingDir exist")
            logE("$TAG $mappingDir.absolutePath/$flavorAndType/mapping.txt")
            def mappingFile = new File("$mappingDir.absolutePath/$flavorAndType/mapping.txt")
            logE("$TAG mappingFile path = $mappingFile.absolutePath")
            mappingFile
        }

    }

    def prepareDex(Project project) {
        def TAG = 'method dex:  '
        List<String> flavorsAndTypes = getProductFlavorsBuildTypes(project)
        flavorsAndTypes.each { item ->
            File classDir = new File("$project.buildDir\\tmp\\$item")
            if (classDir.exists() && classDir.listFiles().size() > 0) {
                logE("$TAG $item classDir has subFile or dir")
                def sdkDir = getSdkDir(project)
                logE("$TAG sdkDir = $sdkDir")
                if (sdkDir) {
                    def changedFlavorsAndTypes = capitalize(item)
                    def proguardTask = project.tasks.
                            findByName("transformClassesAndResourcesWithProguardFor${changedFlavorsAndTypes}")
                    if (proguardTask) {
                        logE("$TAG proguardTask can be find")
                        def mappingFile = getMappingFile(project, item)
                        //混淆的配置
                        Configuration configuration = new Configuration()
                        //使用混合的类名，这样不同的类混淆后将使用同一类名
                        configuration.useMixedCaseClassNames = false
                        configuration.programJars = new ClassPath()
                        configuration.libraryJars = new ClassPath()
                        //应用mapping文件
                        configuration.applyMapping = mappingFile
                        //打开日志
                        configuration.verbose = true
                        //输出配置文件
                        configuration.printConfiguration = new File("$classDir.absolutePath/dump.txt")
                        //不过滤没有引用的文件....应该是我们打的这些都都没有引用的文件，所以必须不过滤
                        configuration.shrink = false
                        //将android.jar和apache库加入依赖
                        def compileSdkVersion = project.android.compileSdkVersion
                        logE("compileSdkVersion = $compileSdkVersion")
                        ClassPathEntry androidEntry = new ClassPathEntry(new File("$sdkDir/platforms/$compileSdkVersion/android.jar"), false)
                        configuration.libraryJars.add(androidEntry)

                        File apacheFile = new File("$sdkDir/$compileSdkVersion/platforms/optional/org.apache.http.legacy.jar")
                        //android-23 以下才存在apache包
                        if (apacheFile.exists()) {
                            ClassPathEntry apacheEntry = new ClassPathEntry(apacheFile, false)
                            configuration.libraryJars.add(apacheFile)
                        }
                        List<File> proguardLibFiles = getProguardLibFiles()
                        //将MyTransform的所有输入文件都添加到混淆依赖jar
                        if (proguardLibFiles) {
                            ClassPathEntry jarFile
                            proguardLibFiles.findAll { file ->
                                jarFile = new ClassPathEntry(file, false)
                                configuration.libraryJars.add(jarFile)
                            }
                        }

                        //设置待dex未混淆的目录
                        ClassPathEntry classPathEntry = new ClassPathEntry(classDir, false)
                        configuration.programJars.add(classPathEntry)

                        //定义混淆输出路径
                        File proguardOutPut = new File("$project.buildDir.absolutePath/tmp/$item/proguard")
                        //第二个参数表示是输出
                        ClassPathEntry classPathEntryOut = new ClassPathEntry(proguardOutPut, true)
                        configuration.programJars.add(classPathEntryOut)

                        //外部定义的混淆文件的获取并应用
                        def file = mProguardConfigFile.get(item)
                        file.findAll { proguardFile ->
                            logE("$TAG proguard外部定义的混淆文件 = $proguardFile.absolutePath")
                            ConfigurationParser proguardParser = new ConfigurationParser(proguardFile, System.getProperties())
                            try {
                                proguardParser.parse(configuration)
                            } catch (Exception e) {
                                logE(e.message)
                            }
                        }

                        //执行混淆
                        ProGuard proGuard = new ProGuard(configuration)
                        proGuard.execute()

                        classDir = proguardOutPut

                    }

                    dex(project, sdkDir, classDir)


                } else {
                    logE("$TAG android sdk dir not defined")
                }
            }
        }
    }

    def dex(Project project, String sdkDir, File classDir) {
        def TAG = "dex :　"
        logE("$TAG dex begining")
        def cmdExt = Os.isFamily(Os.FAMILY_WINDOWS) ? '.bat' : ''
        def stdout = new ByteArrayOutputStream()
        project.exec {
            commandLine "${sdkDir}/build-tools/${project.android.buildToolsVersion}/dx${cmdExt}",
                    '--dex',
                    "--output=${new File(classDir, 'ryan_dex.jar').absolutePath}",
                    "${classDir.absolutePath}"
            standardOutput = stdout
        }
        def error = stdout.toString().trim()
        if (error) {
            logE("$TAG dex error = $error")
        }
    }

    def getProguardLibFiles() {
        List<File> proguardLibFiles = new ArrayList<>()
        File output = new File(mProject.buildDir.absolutePath + "\\tmp\\libFiles.txt")
        output.withDataInputStream { input ->
            input.eachLine { path ->
                logE("proguardLibFiels = $path")
                proguardLibFiles.add(new File(path))
            }
        }
        proguardLibFiles
    }

    def getSdkDir(Project project) {
        //确定sdkDir的路径
        def sdkDir
        Properties properties = new Properties()
        File localProps = project.rootProject.file('local.properties')
        if (localProps.exists()) {
            properties.load(localProps.newDataInputStream())
            sdkDir = properties.getProperty('sdk.dir')
        } else {
            sdkDir = System.getenv('ANDROID_HOME')
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
    String TAG = com.hypers.MyTransform.class.getSimpleName()
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
        List<File> proguardLibfiles = new ArrayList<>();

        transformInvocation.inputs.each { TransformInput transformInput ->
            log("transformInput ${i++}")
            logW("directoryInputs")
            transformInput.directoryInputs.findAll { DirectoryInput directoryInput ->
                File dest = transformInvocation.outputProvider.getContentLocation(
                        directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY
                )
                HashMap<String, File> modifyMap = new HashMap<>();
                logW("$TAG directoryInput path = $directoryInput.file.absolutePath")
                proguardLibfiles.add(directoryInput.file)
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
                logW("$TAG jarinput path = $jarInput.file.absolutePath")
                proguardLibfiles.add(jarInput.file)
                FileUtils.copyFile(jarInput.file, dest)
            }
        }

        if (proguardLibfiles && proguardLibfiles.size()) {
            File output = new File(mProject.buildDir.absolutePath + "\\tmp\\libFiles.txt")
            if (!output.exists()) {
                output.createNewFile()
            }
            output.withDataOutputStream { strem ->
                proguardLibfiles.findAll { File file ->
                    strem.write("$file.absolutePath\n".getBytes())
                    strem.flush()
                }
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
