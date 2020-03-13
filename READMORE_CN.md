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
-keep class tech.linjiang.pandora.**{*;}
```

#### 5. android-support还是AndroidX ？
> 依赖哪种版本取决于你的项目，Pandora提供的两种版本的除了依赖不同，所有逻辑完全一致并保持同步更新；
> 虽然AndroidX是趋势，但是如果你的项目无法迁移到AndroidX还是请使用android-support的方式
