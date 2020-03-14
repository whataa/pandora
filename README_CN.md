<p align=center>
<img src="https://i.loli.net/2019/03/15/5c8b6158be01e.png" width=40%>
</p>

<p align=center>
<a href="https://youtu.be/pP9jVcxKPsE">
    <img src="https://img.shields.io/badge/demo-youtube-red.svg">
</a>
<a href="https://jitpack.io/#whataa/pandora">
    <img src="https://jitpack.io/v/whataa/pandora.svg">
</a>
<a href="https://android-arsenal.com/api?level=14">
    <img src="https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat">
</a>
<a href="https://developer.android.com/index.html">
    <img src="https://img.shields.io/badge/platform-android-brightgreen.svg">
</a>
<a href="https://github.com/whataa/pandora-no-op/blob/master/LICENSE">
    <img src="https://img.shields.io/badge/license-Apache%202.0-blue.svg">
</a>
<a href="https://travis-ci.org/whataa/pandora">
    <img src="https://travis-ci.org/whataa/pandora.svg?branch=master">
</a>
</p>

Pandora 是一款无需ROOT、可以直接在应用内查看和修改包括网络、数据库、UI等的工具箱，适合开发和测试阶段的各种问题的快速定位。


## 效果

<p>
<img src="https://note.youdao.com/yws/api/personal/file/WEB5d90fab5127f1cf2664a976380a89418?method=download&shareKey=a9f6caf76cc9abef7d17271b435ca030" width=18%>  <img src="https://note.youdao.com/yws/api/personal/file/WEB681b1401d6f40a7dcdf480b2aff33bef?method=download&shareKey=9e2596df7e42fad75ee3f4fe99766814" width=18%>  <img src="https://note.youdao.com/yws/api/personal/file/WEB46cceded39144f21327bbc113938eb42?method=download&shareKey=6a7a0a7e863a4c75a5f62fcd62d5092a" width=18%>  <img src="https://note.youdao.com/yws/api/personal/file/WEB710b73c107e189afab614b00428b4f7a?method=download&shareKey=d53c1f09302225d6aa293ae023f40d13" width=18%>
</p>

## 功能
<p align=left>
<img src="https://i.loli.net/2019/03/16/5c8ca7c19c917.png" width=60%>
</p>


#### 网络日志
- 查看网络请求的详细日志，例如Header、body、错误信息等；
- 支持基于OKHTTP、Android原生HttpURLConnection的所有网络库，涵盖大部分网络开发情况；

#### 沙盒文件
- 查看应用的私有存储目录，导出文件至SDcard；
- 支持浏览和编辑SQLite数据库、SharedPref文件；


#### UI：选择视图、视图层级、基准线、网格线


- 查看、修改任意控件的属性，例如控件大小、颜色、文字内容等；
- 抓取和移动任意控件，查看控件间的边界和相对距离，检测对齐、布局等问题；
- 查看任意页面的层级结构，支持Activity、Dialog、PopupWindow等；



#### 实用工具

- 实时显示当前Activity；
- 支持记录和查看应用层所有Crash，兼容第三方Crash库；
- 支持添加自定义功能入口；
- 支持快速跳转到应用内任意页面；
- 记录和查看应用生命期间所有的Activity历史记录；



## 集成和使用

1. 声明[Jitpack](https://jitpack.io/#whataa/pandora) 仓库并添加以下依赖:（版本更新日志请查看[Releases](https://github.com/whataa/pandora/releases))

	```
	debugImplementation 'com.github.whataa:pandora:v${RELEASE}'
	// 如果你的项目使用的是AndroidX, 替换为以下方式
	debugImplementation 'com.github.whataa:pandora:androidx_v${RELEASE}'

	// 不区分android-support和AndroidX
	releaseImplementation 'com.github.whataa:pandora-no-op:v${RELEASE}'
	```

    library | version
    ---|---
    pandora | [![Release](https://jitpack.io/v/whataa/pandora.svg)](https://jitpack.io/#whataa/pandora)
    pandora-no-op | [![Release](https://jitpack.io/v/whataa/pandora-no-op.svg)](https://jitpack.io/#whataa/pandora-no-op)

2. （可选）如果你的项目使用了OKHttp作为网络库，添加 `pandora-plugin` 可自动将日志拦截注入到所有OKHttp对象中 [ ![Download](https://api.bintray.com/packages/yanglssc/maven/pandora-plugin/images/download.svg) ](https://bintray.com/yanglssc/maven/pandora-plugin/_latestVersion)：
	```
    // project's gradle
    buildscript {
        dependencies {
            ...
            classpath 'com.github.whataa:pandora-plugin:1.0.0'
        }
    }

    // app's gradle
    apply plugin: 'com.android.application'
    apply plugin: 'pandora-plugin'
    ```

3. 授予「悬浮窗」权限，并摇晃手机。

## [Feature APIs and Problems](https://github.com/whataa/pandora/blob/master/READMORE.md)

## 致谢

Pandora是站在巨人的肩膀上开发而来，非常感谢以下开源项目或作者：

- Logo及Icon由设计师 [Zularizal](https://github.com/zularizal) 制作。

- 灵感来源于 Flipboard 开源的iOS平台调试工具 [FLEX](https://github.com/Flipboard/FLEX)；

- 项目中的数据库模块思路及部分源码来源于 facebook 的开源项目 [stetho](https://github.com/facebook/stetho)；

- 项目中的UI模块中的选择控件的思路及部分源码来源于 eleme 的开源项目 [UETool](https://github.com/eleme/UETool)；

- Demo中的演示网络请求的API来源于 jgilfelt 的开源项目 [chuck](https://github.com/jgilfelt/chuck) ；

## 开源协议
[Apache-2.0](https://opensource.org/licenses/Apache-2.0)