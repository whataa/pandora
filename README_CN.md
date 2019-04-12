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


#### ① 网络日志
- 查看网络请求的详细日志，例如Header、body、错误信息等；

- 支持基于OKHTTP、Android原生HttpURLConnection的所有网络库，涵盖大部分网络开发情况；

#### ② 沙盒文件
- 查看应用的私有存储目录，导出文件至SDcard；

- 支持浏览和编辑SQLite数据库、SharedPref文件；


#### ③ UI：选择视图、视图层级、基准线、网格线


- 查看、修改任意控件的属性，例如控件大小、颜色、文字内容等；

- 抓取和移动任意控件，查看控件间的边界和相对距离，检测对齐、布局等问题；

- 查看任意页面的层级结构，支持Activity、Dialog、PopupWindow等；



#### ④ 实用工具

- 实时显示当前Activity；

- 支持记录和查看应用层所有Crash，兼容第三方Crash库；

- 支持添加自定义功能入口；

- 支持快速跳转到应用内任意页面；

- 记录和查看应用生命期间所有的Activity历史记录；



## 集成和使用

1. 声明[Jitpack](https://jitpack.io/#whataa/pandora) 仓库并添加以下依赖:（版本更新日志请查看[Releases](https://github.com/whataa/pandora/releases))

	```
	debugImplementation 'com.github.whataa:pandora:v${RELEASE}'
	releaseImplementation 'com.github.whataa:pandora-no-op:v${RELEASE}'
	```

    library | version
    ---|---
    pandora | [![Release](https://jitpack.io/v/whataa/pandora.svg)](https://jitpack.io/#whataa/pandora)
    pandora-no-op | [![Release](https://jitpack.io/v/whataa/pandora-no-op.svg)](https://jitpack.io/#whataa/pandora-no-op)

2. （可选）如果你的项目使用了OKHttp作为网络库，请为其添加以下拦截器以支持网络日志：
	```
	Pandora.get().getInterceptor();
	```

2. 授予「悬浮窗」权限，并摇晃手机。


## 扩展功能

### 1. 添加自定义快捷入口

日常开发中，我们可能会在某些页面隐藏一些调试开关，用以“切换开发环境”、“查看Crash日志” 等，如果你有类似需求，可以通过以下方式在Pandora的面板中添加快捷入口：
1. 实现 `tech.linjiang.pandora.function.IFunc` 接口，返回相应的Icon、Name以及触发操作：

    ```
    private IFunc customFunc = new IFunc() {
        @Override
        public int getIcon() {
            return R.drawable.ic_launcher_round;
        }

        @Override
        public String getName() {
            return getString(R.string.pandora_click_me);
        }

        @Override
        public boolean onClick() {
            toast("I am the custom Function.");
            return false;
        }
    };
    ```

2. 调用 `Pandora.get().addFunc()` 方法传入上述IFunc对象。


### 2. 扩展对查看View属性的支持

Pandora默认支持动态查看和部分修改View、ViewGroup以及常见的TextView、ImageView控件的属性，如果想查看更多控件的属性，可以通过以下方式进行扩展：

1. 实现 `tech.linjiang.pandora.inspector.attribute.IParser` 接口并指定所关注的View类型，这里以已经实现的ImageView为例：
```
public class ImageViewParser implements IParser<ImageView> {

    @Override
    public List<Attribute> getAttrs(ImageView view) {
        List<Attribute> attributes = new ArrayList<>();
        // 添加所关心的属性并返回
        Attribute scaleTypeAttribute = new Attribute("scaleType", scaleTypeToStr(view.getScaleType()), Attribute.Edit.SCALE_TYPE);
        attributes.add(scaleTypeAttribute);
        return attributes;
    }
    ...
}
```
2. 将新建的Parser添加到Pandora中即可：
```
Pandora.get().getAttrFactory().addParser(new ImageViewParser());
```
在此之后，每次点击查看ImageView控件时，属性列表中会自动将我们所关注的属性值列举出来。

### 3. 查看自定义路径的SharedPref文件

Pandora默认读取的是应用内默认的SP路径下（`data/data/<package-name>/shared_prefs/`）的XML文件，如果有其它非默认路径的SP文件，可以通过以下方式扩展：
1. 实现 `tech.linjiang.pandora.preference.protocol.IProvider`接口，返回对应的文件列表：

(具体可参考库中的默认实现`SharedPrefProvider`)

2. 将新建的Provider添加到Pandora中即可：
```
Pandora.get().getSharedPref().addProvider(new XXProvider());
```

## 常见问题

#### 0. gradle添加依赖失败

> 1. 请检查是否声明了Jitpack仓库。
> 2. 所有版本号前面有一个`v` 符号，请检查是否遗漏。

#### 1. 网络日志里没有记录到Header等数据

> 建议将Pandora的拦截器添加为OKHttp的最后一个。

#### 2. 不想用摇一摇，和项目有冲突

> 可以在应用启动时调用 `Pandora.get().disableShakeSwitch();` 方法禁用摇一摇，
然后在需要的地方调用 `Pandora.get().open();` 手动打开。

#### 3. 摇一摇没反应，或者很难打开

> 由于Android机型众多，请手动前往权限中心检查是否授予了「悬浮窗」权限，
> 对于很难打开的情况，可以在「配置」功能里对触发系数进行调整，修改为最适合你手机的值。

#### 4. 混淆规则

> 即使建议将Pandora仅用在debug环境，但是无法约束大家在哪种BuildType下开启混淆，因此若有需求请添加以下规则：

```
-keep class tech.linjiang.pandora.cache.**{*;}
```

## 致谢

Pandora是站在巨人的肩膀上开发而来，非常感谢以下开源项目或作者：

- Logo及Icon由设计师 [Zularizal](https://github.com/zularizal) 制作。

- 灵感来源于 Flipboard 开源的iOS平台调试工具 [FLEX](https://github.com/Flipboard/FLEX)；

- 项目中的数据库模块思路及部分源码来源于 facebook 的开源项目 [stetho](https://github.com/facebook/stetho)；

- 项目中的UI模块中的选择控件的思路及部分源码来源于 eleme 的开源项目 [UETool](https://github.com/eleme/UETool)；

- Demo中的演示网络请求的API来源于 jgilfelt 的开源项目 [chuck](https://github.com/jgilfelt/chuck) ；

## 开源协议
[Apache-2.0](https://opensource.org/licenses/Apache-2.0)