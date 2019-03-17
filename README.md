EN | [中文](https://github.com/whataa/pandora/blob/master/README_CN.md)

<p align=center>
<img src="https://i.loli.net/2019/03/15/5c8b6158be01e.png" width=40%>
 [![Release](https://jitpack.io/v/whataa/pandora.svg)](https://jitpack.io/#whataa/pandora) [![API](https://img.shields.io/badge/API-14%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=14) [![platform](https://img.shields.io/badge/platform-android-brightgreen.svg)](https://developer.android.com/index.html)  [![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](https://github.com/whataa/pandora-no-op/blob/master/LICENSE) [![Build Status](https://travis-ci.org/whataa/pandora.svg?branch=master)](https://travis-ci.org/whataa/pandora) [![Demo](https://img.shields.io/badge/demo-youtube-red.svg)](https://youtu.be/pP9jVcxKPsE)
</p>


------------

Pandora is a tool box that allows you to inspect and modify what includes networks, databases, UIs, etc. directly in your application. It is suitable for rapid position of various problems in the development and testing stages.

## Demo

<p>
<img src="https://note.youdao.com/yws/api/personal/file/WEB5d90fab5127f1cf2664a976380a89418?method=download&shareKey=a9f6caf76cc9abef7d17271b435ca030" width=18%>  <img src="https://note.youdao.com/yws/api/personal/file/WEB681b1401d6f40a7dcdf480b2aff33bef?method=download&shareKey=9e2596df7e42fad75ee3f4fe99766814" width=18%>  <img src="https://note.youdao.com/yws/api/personal/file/WEB46cceded39144f21327bbc113938eb42?method=download&shareKey=6a7a0a7e863a4c75a5f62fcd62d5092a" width=18%>  <img src="https://note.youdao.com/yws/api/personal/file/WEB710b73c107e189afab614b00428b4f7a?method=download&shareKey=d53c1f09302225d6aa293ae023f40d13" width=18%>
</p>

## Feature
<p align=left>
<img src="https://i.loli.net/2019/03/16/5c8ca7c19c917.png" width=60%>
</p>


#### ① Network logs
- Check the detailed logs of network requests, such as headers, body, error messages, and so on.

- Support all network libraries based on OKHTTP and Android native HttpURLConnection, covering most network development situations.

#### ② Sandbox
- View the app's private storage directory, and can export files to SDcard.

- Supports browsing and editing SQLite databases, SharedPref files.


#### ③ UI：Select、Hierarchy、Baseline、Gridline


- View and modify properties of any Widget, such as the widget's size, color, text content, and so on.

- Grab and move any widget, view the boundaries and relative distance between widgets, detect alignment, layout and other issues.

- View the hierarchy of any UI, support Activity, Dialog, PopupWindow, etc.



#### ④ Other tools

- Show the current Activity in real time.

- Supports recording crash, compatible with third-party Crash libraries.

- You can add shortcut to Pandora.

- You can open any Activity of your app.

- You can view the lifecycle history of Activities.



## Usage

1. Declare [Jitpack](https://jitpack.io/#whataa/pandora) repository and add dependencies：

	```
	debugImplementation 'com.github.whataa:pandora:v${RELEASE}'
	releaseImplementation 'com.github.whataa:pandora-no-op:v${RELEASE}'
	```

    library | version
    ---|---
    pandora | [![Release](https://jitpack.io/v/whataa/pandora.svg)](https://jitpack.io/#whataa/pandora)
    pandora-no-op | [![Release](https://jitpack.io/v/whataa/pandora-no-op.svg)](https://jitpack.io/#whataa/pandora-no-op)

2. （Optional）If your project use OKHttp as a network library, add the following interceptor to support network logging：
	```
	Pandora.get().getInterceptor();
	```

2. Grant permission to "Overlay Windows" and shake your device.


## More feature

### 1. Add shortcuts to Pandora

Usually, we may hide some debugging switches in some pages to "switch the development environment", "check the Crash log" and so on. If you have similar needs, you can add a shortcut by：
1. Implement `tech.linjiang.pandora.function.IFunc` , , return the icon, name and the action：

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

2. Call `Pandora.get().addFunc()` to add it。


### 2. Let "view properties" support more.

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


### 3. Edit the SharedPref located in custom path:

Pandora reads by default the XML file in the default SP path in the application（`data/data/<package-name>/shared_prefs/`），If there exist other SP files that are not in the default path, they can be extended in the following ways:

1. implement `tech.linjiang.pandora.preference.protocol.IProvider` interface，and return the corresponding file list：

(Specific details can refer to the default implementation in the library`SharedPrefProvider`)

2. Add new Provider to Pandora：
```
Pandora.get().getSharedPref().addProvider(new XXProvider());
```

## Problems

#### 0. Failed when add the dependencies

> 1. Check to see if the Jitpack repository is declared.
> 2. There exists a 'v' symbol in the start of version number.

#### 1. No data or incomplete data in the network logs

> It is recommended that the Pandora interceptor be added as the last of the OKHttp interceptors.

#### 2. Don't want to use shake, which is in conflict with my app

> You can call `Pandora.get().disableShakeSwitch();` to disable it，
and call `Pandora.get().open();` to open directly.

#### 3. No react when shaking, or it's hard to open it.

> Due to the large number of Android phones, please manually go to the permission center to check whether the permission of "overlay window" is granted.

> In cases where it's hard to open, you can change the trigger factor in the "config" modify the value that works best for your phone.


## Thanks

Pandora was developed on the shoulders of giants. Thanks to the following open source projects or person:

- Logo and Icon are produced by the designer [Zularizal](https://github.com/zularizal).

- Inspired by Flipboard's open source iOS platform debugging tool [FLEX](https://github.com/Flipboard/FLEX)；

- Project database module ideas and part of the source code from Facebook's open source project [stetho](https://github.com/facebook/stetho)；

- The idea of selecting views in the UI module of the project and part of the source code from eleme's open source project [UETool](https://github.com/eleme/UETool)；

- The request API in the Demo module comes from jgilfelt's open source project [chuck](https://github.com/jgilfelt/chuck) ；

## License
[Apache-2.0](https://opensource.org/licenses/Apache-2.0)