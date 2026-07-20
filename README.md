# Launcher Integration

> 基于 Zalith Launcher 2 开发的非官方修改版 Minecraft: Java Edition Android 启动器。

Launcher Integration 是面向 Android 设备的 Minecraft: Java Edition 启动器。本项目在
[Zalith Launcher 2](https://github.com/ZalithLauncher/ZalithLauncher2) 基础上进行修改，保留其
PojavLauncher 启动核心、Jetpack Compose 界面与运行环境管理能力，并增加 APK 内置游戏资源的
首次启动导入流程。

其实就是加了个把整合包解压到游戏目录的功能而已~~，后续可能会加些东西，待定哦~

## 内置整合包

把整合包的.minecraft文件内的资源打包为 整合包.zip然后放到，默认开启版本隔离的
注意不是直接打包.minecraft文件夹，是打包内部的文件

```text
ZalithLauncher/src/main/assets/整合包.zip
```

当该文件存在时，首次启动安装页面底部会显示：

```text
游戏资源
整合包资源导入
```

启动器会等待其他依赖安装完成，然后在后台线程将 ZIP 内容解压到：

```text
Android/data/<应用包名>/files/.minecraft
```

- Windows 10/11
- Android Studio（建议使用当前稳定版本）
- JDK 17 或更高版本（推荐 JDK 21）
- Android SDK 37
- Android NDK `25.2.9519653`

## 使用教程

不会用android studio的话
可以用打包器，下载地址：https://pan.quark.cn/s/db4960eae467



## 构建

### 使用 Android Studio

1. 克隆或下载本项目。
2. 使用 Android Studio 打开项目根目录，不要只打开 `ZalithLauncher` 子目录。
3. 等待 Gradle Sync，并按提示安装缺少的 SDK 与 NDK。
4. 如需内置整合包，将 `整合包.zip` 放到上述 assets 目录。
5. 选择 `Build > Build Bundle(s) / APK(s) > Build APK(s)`。

## 自定义信息

应用显示名称及版本信息位于：

```text
ZalithLauncher/gradle.properties
```

常用字段：

```properties
launcher_name=LauncherIntegration
launcher_app_name=Launcher Integration
launcher_short_name=LI
launcher_version_code=1
launcher_version_name=1.0.0
url_home=https://github.com/<你的用户名>/<仓库名>
```

Android 安装包 ID 位于 `ZalithLauncher/build.gradle.kts` 的 `zalithPackageName`。建议使用全小写且属于
自己的唯一包名，例如：

```kotlin
val zalithPackageName = "com.example.launcherintegration"
```

不要修改 `zalithNamespace`。
