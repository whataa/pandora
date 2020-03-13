## More feature

### 1. Add shortcuts to Pandora

Usually, we may hide some debugging switches in some pages to "switch the development environment", "check the Crash log" and so on. If you have similar needs, you can add a shortcut by：
1. Implement `tech.linjiang.pandora.function.IFunc` , return the icon, name and the action：

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

2. Call `Pandora.get().addFunc()` to add it.


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

#### 4. proguard-rules

> Even though it is recommended to use Pandora only in the dev and test stage, enable minify can be anywhere, so add the following rules if you need it:

```
-keep class tech.linjiang.pandora.**{*;}
```

#### 5. android-support or AndroidX ?
> Which version to implementation depends on your project, The two versions have completely consistent logic and synchronized updates, except for different dependencies.
> Although AndroidX is the trend, if your project cannot be migrated to AndroidX, please use android-support.
