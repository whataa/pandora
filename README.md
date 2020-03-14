EN | [中文](https://github.com/whataa/pandora/blob/master/README_CN.md)

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

Pandora is a tool box that allows you to inspect and modify what includes networks, databases, UIs, etc. directly in your application. It is suitable for rapid position of various problems in the development and testing stages.

## Demo

<p>
<img src="https://note.youdao.com/yws/api/personal/file/WEB5d90fab5127f1cf2664a976380a89418?method=download&shareKey=a9f6caf76cc9abef7d17271b435ca030" width=18%>  <img src="https://note.youdao.com/yws/api/personal/file/WEB681b1401d6f40a7dcdf480b2aff33bef?method=download&shareKey=9e2596df7e42fad75ee3f4fe99766814" width=18%>  <img src="https://note.youdao.com/yws/api/personal/file/WEB46cceded39144f21327bbc113938eb42?method=download&shareKey=6a7a0a7e863a4c75a5f62fcd62d5092a" width=18%>  <img src="https://note.youdao.com/yws/api/personal/file/WEB710b73c107e189afab614b00428b4f7a?method=download&shareKey=d53c1f09302225d6aa293ae023f40d13" width=18%>
</p>

## Feature
</p>


#### Network logs
- Check the detailed logs of network requests, such as headers, body, error messages, and so on.
- Support all network libraries based on OKHTTP and Android native HttpURLConnection, covering most network development situations.

#### Sandbox
- View the app's private storage directory, and can export files to SDcard.
- Supports browsing and editing SQLite databases, SharedPref files.


#### UI：Select、Hierarchy、Baseline、Gridline


- View and modify properties of any Widget, such as the widget's size, color, text content, and so on.
- Grab and move any widget, view the boundaries and relative distance between widgets, detect alignment, layout and other issues.
- View the hierarchy of any UI, support Activity, Dialog, PopupWindow, etc.



#### Other tools

- Show the current Activity in real time.
- Supports recording crash, compatible with third-party Crash libraries.
- You can add shortcut to Pandora.
- You can open any Activity of your app.
- You can view the lifecycle history of Activities.



## Usage

1. Declare [Jitpack](https://jitpack.io/#whataa/pandora) repository and add dependencies：

	```
	// android-support
	debugImplementation 'com.github.whataa:pandora:v${RELEASE}'
    // or androidX
    debugImplementation 'com.github.whataa:pandora:androidx_v${RELEASE}'

    // No matter android-support or AndroidX
    releaseImplementation 'com.github.whataa:pandora-no-op:v${RELEASE}'
	```

    library | version
    ---|---
    pandora | [![Release](https://jitpack.io/v/whataa/pandora.svg)](https://jitpack.io/#whataa/pandora)
    pandora-no-op | [![Release](https://jitpack.io/v/whataa/pandora-no-op.svg)](https://jitpack.io/#whataa/pandora-no-op)

2. （Optional）If your project use OKHttp as a network library, interceptor can be injected into OKhttp by `pandora-plugin` [ ![Download](https://api.bintray.com/packages/yanglssc/maven/pandora-plugin/images/download.svg) ](https://bintray.com/yanglssc/maven/pandora-plugin/_latestVersion)：
	```
	// in your project's gradle
	buildscript {
        dependencies {
            ...
            classpath 'com.github.whataa:pandora-plugin:1.0.0'
        }
    }

    // in your app's gradle
    apply plugin: 'com.android.application'
    apply plugin: 'pandora-plugin'
	```

3. Grant permission to "Overlay Windows" and shake your device.

## [Feature APIs and Problems](https://github.com/whataa/pandora/blob/master/READMORE.md)


## Thanks

Pandora was developed on the shoulders of giants. Thanks to the following open source projects or person:

- Logo and Icon are produced by the designer [Zularizal](https://github.com/zularizal).

- Inspired by Flipboard's open source iOS platform debugging tool [FLEX](https://github.com/Flipboard/FLEX)；

- Project database module ideas and part of the source code from Facebook's open source project [stetho](https://github.com/facebook/stetho)；

- The idea of selecting views in the UI module of the project and part of the source code from eleme's open source project [UETool](https://github.com/eleme/UETool)；

- The request API in the Demo module comes from jgilfelt's open source project [chuck](https://github.com/jgilfelt/chuck) ；

## License
[Apache-2.0](https://opensource.org/licenses/Apache-2.0)