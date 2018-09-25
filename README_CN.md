an android library for debugging what we care about directly in app.

# Pandora [![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14) [![platform](https://img.shields.io/badge/platform-android-brightgreen.svg)](https://developer.android.com/index.html)  [![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://github.com/whataa/pandora-no-op/blob/master/LICENSE) [![Build Status](https://travis-ci.org/whataa/pandora.svg?branch=master)](https://travis-ci.org/whataa/pandora)

Pandora 是一款无需ROOT、可以直接在 **应用内** 查看和修改包括网络、数据库、UI等的Android工具箱，适合开发和测试阶段的各种问题的快速定位。

## 功能
- 查看每条网络请求的详细日志，例如headers、response等；
- 查看自身应用的内部存储系统；
- 查看所有数据库，支持直接进行增删改查操作；
- 查看并编辑所有Shared Preference；
- 预览当前页面的视图层级、查看/修改常用控件的属性；
- 测量控件之间距离、检测是否对齐；
- 选中页面上的任意控件以移动位置、查看自身大小、显示相对关系；
- 更多功能期待大家探索；

部分效果如下：

展示图片依次为：网络、数据库、UI、文件

![image](https://note.youdao.com/yws/api/personal/file/WEB5d90fab5127f1cf2664a976380a89418?method=download&shareKey=a9f6caf76cc9abef7d17271b435ca030) ![image](https://note.youdao.com/yws/api/personal/file/WEB681b1401d6f40a7dcdf480b2aff33bef?method=download&shareKey=9e2596df7e42fad75ee3f4fe99766814)

![image](https://note.youdao.com/yws/api/personal/file/WEB46cceded39144f21327bbc113938eb42?method=download&shareKey=6a7a0a7e863a4c75a5f62fcd62d5092a) ![image](https://note.youdao.com/yws/api/personal/file/WEB710b73c107e189afab614b00428b4f7a?method=download&shareKey=d53c1f09302225d6aa293ae023f40d13)






 
## 集成 [![Release](https://jitpack.io/v/whataa/pandora.svg)](https://jitpack.io/#whataa/pandora)

1. 在root's build.gradle中加入Jitpack仓库：
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
2. 在app's build.gradle中（请使用最新版本）：
```
dependencies {
    ...
    debugImplementation 'com.github.whataa:pandora:${RELEASE}'
    releaseImplementation 'com.github.whataa:pandora-no-op:${RELEASE}'
}  
```

> 最新库版本号，具体更新内容请在 [RELEASE](https://github.com/whataa/pandora/releases) 中查看

library name | release version
---|---
pandora | [![Release](https://jitpack.io/v/whataa/pandora.svg)](https://jitpack.io/#whataa/pandora)
pandora-no-op | [![Release](https://jitpack.io/v/whataa/pandora-no-op.svg)](https://jitpack.io/#whataa/pandora-no-op)

## 使用

现在，无需添加任何代码，你可以直接在应用内 **“摇一摇”** 开始使用了。

> Pandora将以悬浮窗的形式展现功能面板，所以需要「悬浮窗」权限，请手动开启。


默认情况下，Pandora是以“摇一摇”打开的，如果该特性和你的应用有冲突，你可以在面板的设置中关闭它，并实现自己的触发方式然后调用以下方法来打开功能面板：

```
Pandora.get().open();
```

如果你的项目中使用了OKHttp作为底层网络库，可以为其添加以下拦截器开启网络调试模块的功能：

> 注意：请将Pandora作为最后一个拦截器，以防request-headers, request-params获取不到；

```
new OkHttpClient.Builder()
    ...
    .addInterceptor(xxx)
    .addInterceptor(Pandora.get().getInterceptor())
    .build()
```

## 扩展特性

### 让Http的response更具可读性
Http请求的结果通常是json格式，为了在查看json时更美观，可以通过以下方式为json增加formatter：

1. 实现`tech.linjiang.pandora.network.JsonFormatter`接口，可以参考app模块下的`JsonFormatterImpl`的具体实现：
```
public class JsonFormatterImpl implements JsonFormatter {
    @Override
    public String format(String result) {
        return JSON.toJSONString(JSON.parse(result));
    }
}
```

2. 将实现的formatter添加到Pandora：
```
Pandora.get().getInterceptor().setJsonFormatter(new JsonFormatterImpl());
```

### 查看View属性
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

### 查看Shared Preference
Pandora默认读取的是应用内默认的SP路径下（`data/data/<package-name>/shared_prefs/`）的XML文件，如果有其它非默认路径的SP文件，可以通过以下方式扩展：
1. 实现 `tech.linjiang.pandora.preference.protocol.IProvider`接口，返回对应的文件列表： 

(具体可参考库中的默认实现`SharedPrefProvider`)

2. 将新建的Provider添加到Pandora中即可：
```
Pandora.get().getSharedPref().addProvider(new XXProvider());
```



## 限制

- 最低支持的Android SDK版本为 **14** ；

- 网络调试模块：仅支持底层为OKHttp 3.x的网络库；

- 数据库调试模块：仅支持基于SQLite的数据库，且暂时不支持查看加密数据库；

- 其它

## 致谢

Pandora是站在巨人的肩膀上开发而来，非常感谢以下开源项目：

- 灵感来源于 Flipboard 开源的iOS平台调试工具 [FLEX](https://github.com/Flipboard/FLEX)；

- 项目中的数据库模块思路及部分源码来源于 facebook 的开源项目 [stetho](https://github.com/facebook/stetho)；

- 项目中的UI模块中的选择控件的思路及部分源码来源于 eleme 的开源项目 [UETool](https://github.com/eleme/UETool)；

- Demo中的演示网络请求的API来源于 jgilfelt 的开源项目 [chuck](https://github.com/jgilfelt/chuck) ；

## 开源协议
[Apache-2.0](https://opensource.org/licenses/Apache-2.0)

