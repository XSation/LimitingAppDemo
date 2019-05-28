1. 项目gradle节点添加插件classpath
```classpath 'com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.0'
    classpath 'com.github.dcendents:android-maven-gradle-plugin:2.0'
```
2. module gradle下apply插件
```
apply plugin: 'com.github.dcendents.android-maven'
apply plugin: 'com.jfrog.bintray'
```
3. 设置版本、group
```
version = "1.0.0"
group = "com.xk.library"
```
4. 添加项目门面
```
def siteUrl = 'https://github.com/XSation/LimitingAppDemo'    // project homepage
def gitUrl = 'https://github.com/XSation/LimitingAppDemo.git' // project git
```
5. 上传到jcenter至少需要四个文件，除了打包的aar之外，还需要pom和javadoc，source，否则是通不过jcenter的审核。这些我们都可以用脚本生成。
```
task sourcesJar(type: Jar) {
    from android.sourceSets.main.java.srcDirs
    classifier = 'sources'
}
task javadoc(type: Javadoc) {
    source = android.sourceSets.main.java.srcDirs
    classpath += project.files(android.getBootClasspath().join(File.pathSeparator))
}
task javadocJar(type: Jar, dependsOn: javadoc) {
    classifier = 'javadoc'
    from javadoc.destinationDir
}
artifacts {
    archives javadocJar
    archives sourcesJar
}
```
6. 添加如下配置
```
install {
    repositories.mavenInstaller {
        // This generates POM.xml with proper parameters
        pom {
            project {
                packaging 'aar'
                name 'limitapp 限制app使用'
                url siteUrl
                // Set your license
                licenses {
                    license {
                        name 'The Apache Software License, Version 2.0'
                        url 'http://www.apache.org/licenses/LICENSE-2.0.txt'
                    }
                }
                developers {
                    developer { //填写的一些基本信息
                        id 'xuekai'
                        name 'xuekai'
                        email '3440395@qq.com'
                    }
                }
                scm {
                    connection gitUrl
                    developerConnection gitUrl
                    url siteUrl
                }
            }
        }
    }
}


//配置bintray参数
Properties properties = new Properties()
properties.load(project.rootProject.file('local.properties').newDataInputStream())
bintray {
    user = properties.getProperty("bintrayUser")
    key = properties.getProperty("bintrayApiKey")
    configurations = ['archives']
    pkg {
        repo = "maven"               //Maven仓库的名字
        name = "LimitApp"                //com.xk.library:LimitApp:1.0.0
        websiteUrl = siteUrl
        vcsUrl = gitUrl
        licenses = ["Apache-2.0"]
        publish = true
    }
}
```
7. 项目gradle节点下
```
allprojects {
    repositories {
        google()
        jcenter()
        maven{
        //库地址 xxxxx/name/maven库名
            url "https://dl.bintray.com/xk3440395/maven"
        }
    }
    //用来解决注释中文乱码问题
    tasks.withType(Javadoc) {
        options {
            encoding "UTF-8"
            charSet 'UTF-8'
            links "http://docs.oracle.com/javase/7/docs/api"
            failOnError false
        }
    }
}
```
8. local.properties中
```
//https://bintray.com/login   xk3440395  123.
bintrayUser=xk3440395
bintrayApiKey=xxxxxx
```
9. 执行bintrayUpload命令。

10. push上去之后，去网站上addToJcenter
## 使用
1. 项目gralde根目录添加仓库地址
2. 依赖
