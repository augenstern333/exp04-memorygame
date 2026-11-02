# 实验四：翻牌游戏应用

Android 移动应用开发实验，使用 Kotlin、XML 布局、ViewBinding 和 MVC。
应用标题为“刘胜敏2024110523”，包含三个 Activity。

## 页面与功能

- `MainActivity`：显示姓名、性别、学号、电话、邮箱、兴趣和当前最高分，进入编辑页与翻牌游戏。
- `EditInfoActivity`：编辑资料，性别单选、兴趣多选；保存后通过 Activity Result 返回，取消保留原资料。姓名和学号必填，电话、邮箱填写后检查格式。
- `MemoryGameActivity`：4 × 4 扑克牌，显示分数和配对进度，支持重开、退出及本局结算。
- 资料、最高分与牌局通过实例状态保存，屏幕旋转或 Activity 重建后恢复；玩家资料不写入文件，符合实验要求。
- 三页采用统一的蓝白配色，表单支持滚动和键盘避让。

## 配对与评分

模型沿用附件 `reference_model/model` 的 MVC 结构，并按当前需求调整判定和计分：牌面只显示花色，不显示点数；两张牌花色相同即配对成功，每对加 10 分；失败扣 2 分，单次翻牌不扣分。

失败时两张牌保持翻开约 600 毫秒，再一起收起，期间暂停接受新的翻牌操作。每局 16 张牌，四种花色各 4 张，保证剩余牌始终能按花色配对；完成 8 对后结算，可重新开始。

## 代码结构

```text
app/src/main/java/com/example/liushimin2024110523/exp04/
  MainActivity.kt             玩家资料与 Activity 结果
  EditInfoActivity.kt         编辑资料与表单验证
  MemoryGameActivity.kt       游戏控制器
  Card.kt / Deck.kt           牌与随机牌堆
  CardMatchingGame.kt         选择、匹配、评分与牌局状态
  PlayingCardView.kt          原生绘制花色卡牌和翻牌反馈
  MemoryArtworkView.kt        首页本地矢量插图
  UiSupport.kt                系统边距与交互辅助
app/src/main/res/layout/      三个页面布局
app/src/main/res/values/      字符串、色彩及主题
app/src/test/                 游戏模型单元测试
app/src/androidTest/          Espresso 页面流程测试
```

## 构建与运行

用 Android Studio 打开当前项目目录，安装 Android SDK 37，使用 JDK 17 或更新的兼容版本（推荐 Android Studio 内置 JDK）。项目现有配置为 AGP 9.3.0、Gradle 9.5.0，最低 Android 10 / API 29。

```powershell
.\gradlew.bat assembleDebug
```

APK 位于 `app/build/outputs/apk/debug/app-debug.apk`。可连接开启 USB 调试的手机或启动模拟器后在 Android Studio 运行。

## 测试

本地模型单元测试覆盖花色配对、成功加分和失败扣分、选择状态、已配对牌保护、发牌平衡及牌局恢复。

```powershell
.\gradlew.bat testDebugUnitTest
```

Espresso UI 测试覆盖资料保存与取消、资料和编辑草稿在 Activity 重建后保留、空姓名的字段内错误、翻牌后牌面与分数恢复，以及新局重开与退出。运行 UI 测试前需连接 API 29 以上的设备或模拟器。

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

本次源码验证：应用编译通过，16 项模型单元测试通过，6 项 UI 测试已编译通过。UI 测试尚未在设备上运行，未执行安装。报告目录为 `app/build/reports/tests/` 与 `app/build/reports/androidTests/connected/`。

## 实验提交

将本项目提交到自己的 Gitee 仓库，并在实验报告中附上仓库地址、主要代码说明及最终运行截图。
