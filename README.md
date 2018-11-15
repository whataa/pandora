an android library for debugging what we care about directly in app.

 [[点击查看中文版]](https://github.com/whataa/pandora/blob/master/README_CN.md) 



# Pandora [![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14) [![platform](https://img.shields.io/badge/platform-android-brightgreen.svg)](https://developer.android.com/index.html)  [![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://github.com/whataa/pandora-no-op/blob/master/LICENSE) [![Build Status](https://travis-ci.org/whataa/pandora.svg?branch=master)](https://travis-ci.org/whataa/pandora)

<h1 align=center>
<img src="pandora-logo/horizontal.png" width=40%>
</h1>

Pandora is a tool box that allows you to inspect and modify what includes networks, databases, UIs, etc. directly in your application. It is suitable for rapid position of various problems in the development and testing stages.

## Feature

- Inspect the detailed log of each network request, such as headers, response, etc.
- View the internal storage system of own app;

- View all databases, and support ADD, DELETE, UPDATE, QUERY operations;

- View and edit all Shared Preference;

- Preview the current view Hierarchy, and can view/modify the properties of widgets;

- Measure the distance between the views and detect whether the alignment is correct
- You can select any view on the Activity to move the position, get the size of itself, display the relative relationship;

- More features look forward to you exploring;

Some of the effects are as follows:

The display pictures are: network, database, UI, file

![image](https://note.youdao.com/yws/api/personal/file/WEB5d90fab5127f1cf2664a976380a89418?method=download&shareKey=a9f6caf76cc9abef7d17271b435ca030) ![image](https://note.youdao.com/yws/api/personal/file/WEB681b1401d6f40a7dcdf480b2aff33bef?method=download&shareKey=9e2596df7e42fad75ee3f4fe99766814)

![image](https://note.youdao.com/yws/api/personal/file/WEB46cceded39144f21327bbc113938eb42?method=download&shareKey=6a7a0a7e863a4c75a5f62fcd62d5092a) ![image](https://note.youdao.com/yws/api/personal/file/WEB710b73c107e189afab614b00428b4f7a?method=download&shareKey=d53c1f09302225d6aa293ae023f40d13)

## Set-up

1. Add the JitPack repository to your root build file：
```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```
2. Add the dependency to your app build.gradle (please use the latest version)：
```
dependencies {
    ...
    debugImplementation 'com.github.whataa:pandora:${RELEASE}'
    releaseImplementation 'com.github.whataa:pandora-no-op:${RELEASE}'
}  
```

> the latest version name, Please check the [RELEASE](https://github.com/whataa/pandora/releases) for specific updates.

library name | release version
---|---
pandora | [![Release](https://jitpack.io/v/whataa/pandora.svg)](https://jitpack.io/#whataa/pandora)
pandora-no-op | [![Release](https://jitpack.io/v/whataa/pandora-no-op.svg)](https://jitpack.io/#whataa/pandora-no-op)


## Usage

Now, without adding any code, you can start using it directly in the app by shaking the device.

> Pandora will display the function panel in the form of a floating window, so it needs the "floating window" permission, please open it manually.


By default, Pandora is opened with a "shake", if this feature conflicts with your application, you can solve it by the following two ways and then implement your own trigger method invoking `Pandora.get().open()` to open the function panel：

1. Turn it off in the settings of the panel;

2. Or directly call the following method in the application's onCreate: (the corresponding switch of the panel will be invalid)
    ```
    Pandora.get().disableShakeSwitch();
    ```


If your project uses OKHttp as the underlying network library, you can add the following interceptor to enable the function of the network debugging module：

> Note: Please use Pandora as the last interceptor to prevent request-headers and request-params from getting;
```
new OkHttpClient.Builder()
    ...
    .addInterceptor(xxx)
    .addInterceptor(Pandora.get().getInterceptor())
    .build()
```

## Extended features

### Make the response of Http more readable
The result of Http request is usually the json format. In order to be more beautiful when viewing json, you can add a formatter to json in the following way:

1. implement `tech.linjiang.pandora.network.JsonFormatter` interface，You can refer to the specific implementation of `JsonFormatterImpl` in the app-module:
```
public class JsonFormatterImpl implements JsonFormatter {
    @Override
    public String format(String result) {
        return JSON.toJSONString(JSON.parse(result));
    }
}
```

2. Add new formatter to Pandora：
```
Pandora.get().getInterceptor().setJsonFormatter(new JsonFormatterImpl());
```

### Inspect the View property
Pandora supports viewing and partially modifying the properties of View, ViewGroup, and common TextView and ImageView by default. If you want to inspect more view attributes, you can expand them in the following ways:

1. implement `tech.linjiang.pandora.inspector.attribute.IParser` interface and specify the type of View that you are interested in. Here is an example of an already implemented ImageView：
```
public class ImageViewParser implements IParser<ImageView> {

    @Override
    public List<Attribute> getAttrs(ImageView view) {
        List<Attribute> attributes = new ArrayList<>();
        // Add the property of interest and return
        Attribute scaleTypeAttribute = new Attribute("scaleType", scaleTypeToStr(view.getScaleType()), Attribute.Edit.SCALE_TYPE);
        attributes.add(scaleTypeAttribute);
        return attributes;
    }
    ...
}
```
2. Add new Parser to Pandora：
```
Pandora.get().getAttrFactory().addParser(new ImageViewParser());
```
After this, every time you click on the ImageView, the property list will automatically enumerate the values of the properties we are interested in.。

### Inspect Shared Preference
Pandora reads by default the XML file in the default SP path in the application（`data/data/<package-name>/shared_prefs/`），If there exist other SP files that are not in the default path, they can be extended in the following ways: 

1. implement `tech.linjiang.pandora.preference.protocol.IProvider` interface，and return the corresponding file list： 

(Specific details can refer to the default implementation in the library`SharedPrefProvider`)

2. Add new Provider to Pandora：
```
Pandora.get().getSharedPref().addProvider(new XXProvider());
```



## Limit

- Minimum supported Android SDK version is **14** ；

- Network debugging module: only supports the network library with OKHttp 3.x  as the underlying network library;

- Database debugging module: Only SQLite-based databases are supported, and viewing encrypted databases is temporarily not supported；

- others;

## Thanks

Pandora was developed on the shoulders of giants. Thanks to the following open source projects or person:

- Logo and Icon are produced by the designer [Zularizal](https://github.com/zularizal).

- Inspired by Flipboard's open source iOS platform debugging tool [FLEX](https://github.com/Flipboard/FLEX)；

- Project database module ideas and part of the source code from Facebook's open source project [stetho](https://github.com/facebook/stetho)；

- The idea of selecting views in the UI module of the project and part of the source code from eleme's open source project [UETool](https://github.com/eleme/UETool)；

- The request API in the Demo module comes from jgilfelt's open source project [chuck](https://github.com/jgilfelt/chuck) ；

## License
[Apache-2.0](https://opensource.org/licenses/Apache-2.0)
