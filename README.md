# 实验五：RecyclerView 翻牌游戏

基于实验四，保留玩家档案、编辑资料与最高分回传。应用标题：刘胜敏2024110523。

## 本次功能

- MainActivity 使用 Toolbar 菜单，点击“关于”弹出对话框。
- RecyclerView + GridLayoutManager + CardAdapter/ViewHolder 显示 24 张牌。
- 竖屏 4 列 × 6 行，横屏 6 列 × 4 行；页面可滚动，旋转后保留牌序、选牌、分数和尝试次数。
- MVC：Card/CardMatchingGame 为模型，PlayingCardView 和 XML 为视图，Activity 为控制器；Adapter 回调交给控制器处理。
- 四种花色各 6 张，依次翻 3 张。三张同花色加 10 分，否则扣 2 分，600ms 后统一盖回，等待期间禁止继续翻牌。前两张可再次点击取消选择。已配对牌不可再选。
- 完成 8 组后结算；重新开始调用模型 reset() 重新发牌，清空本局状态，保留最高分。
- 用户可见文字使用 strings.xml；布局列数使用 values/integers.xml 和 values-land/integers.xml。

## 构建与验证

Android Studio 打开本目录；SDK 37、兼容 JDK（当前使用 JDK 24），Gradle 9.5 / AGP 9.3。

```powershell
.\gradlew.bat assembleDebug testDebugUnitTest assembleDebugAndroidTest
# 连接设备后执行界面测试
.\gradlew.bat connectedDebugAndroidTest
```

安装包：app/build/outputs/apk/debug/app-debug.apk。

单元测试涵盖三牌匹配、失败锁定、取消选牌、完整通关、reset、序列化恢复，并保留双牌模式回归测试。界面测试覆盖资料保存/取消、草稿与牌局恢复、重新开始、退出以及关于菜单。

## 分支与目录

实验四保留在 main，本次实验五在 Dev 分支。沿用实验四的同一个 GitHub 仓库：https://github.com/augenstern333/exp04-memorygame 。老师课堂已明确允许使用 GitHub。

本地实验四目录为 D:/AndroidDev_Homework/exp04/Exp04MemoryGame，实验五单独整理到 D:/AndroidDev_Homework/exp05/Exp05MemoryGame，方便分别打开。保留原 applicationId，手机安装实验五时会更新实验四应用。

实验五代码已推送至 Dev 分支，PR：https://github.com/augenstern333/exp04-memorygame/pull/1 。main 已启用 PR 审核保护，要求至少 1 次审核并对管理员生效；目前 PR 待审核，尚未合并。

实验报告保存在项目目录外的 D:/AndroidDev_Homework/exp05/实验五+RecyclerView翻牌游戏APP_刘胜敏2024110523.docx，包含核心代码、真机截图、测试结果及问题分析。

## 本次验证

应用与测试 APK 构建成功；25 项模型单元测试通过；在 MAG-AN00 手机上通过 7 项界面流程测试。
