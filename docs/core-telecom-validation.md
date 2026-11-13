# Core-Telecom 实现与待执行设备验证

2026-11-13。对应 `plan/core-telecom-migration`；计划修订基线 `0eb0ec4`。

## 当前实现范围

- 固定 Core-Telecom 1.0.1 和 coroutines-android 1.8.1，保留原编译工具、minSdk 26 和 targetSdk 34。
- 默认构建使用 Core-Telecom；`-PcoreTelecom=false` 构建选用保留的 LegacyCallTelecomController，不在单通电话建立失败后切换实现。
- 实现独立于服务端阶段和用户静音的本机保持，以及媒体许可合取。网络恢复／前台服务就绪不能越过平台保持；终态不允许重新启用媒体。
- 新外呼先等平台注册就绪，再下发远程拨号。传给本机 Telecom 的地址始终为 `gsm2sip:<callId>`，不传电话号码 URI。
- 进程级协程承载通话，断开完成后停止通话服务；提前挂断等待注册结果。库端点观察任务另行持有并在断开结果后取消，避免 1.0.1 兼容路径的 channel 未关闭导致父协程一直等待。
- 系统回调和界面操作分开处理；系统保持同步暂停本机媒体，不排在 HTTP 队列之后。只向库传 LOCAL／REMOTE／REJECTED／MISSED。系统结束尚未接听的来电只退出本机参与者，明确的用户拒接才沿用全局拒接语义。
- 使用具体端点 ID、名称及系统真实状态，提供外接设备选择，按需请求 Bluetooth 权限；移除直接设置 AudioManager 的竞争路径。
- 保留 CallStyle 通知、锁屏入口和通话麦克风前台服务；旧 ConnectionService 在默认构建中禁用，旧账户按自身 component/accountId 清理。
- Madoka 无 ROLE_DIALER、默认电话应用、系统签名或 root 要求。Homura 与服务器协议未修改。

## P2 四项证据状态

| 项目 | 源码／自动化能确认的部分 | 仍需设备确认 |
| --- | --- | --- |
| 协程持有与断开顺序 | 进程级持有；挂断等待 ready 和平台结果；没有用取消 addCall 代替断开 | API 33／34+ 的 dumpsys 无残留通话，服务销毁与快速挂断 |
| 接听回调时序 | 平台操作和业务接听分离；网络请求异步；业务 ACTIVE 仍等待服务端确认 | 系统／耳机回调期限、重复接听、服务端拒绝后的清理 |
| 外呼地址 | 单测覆盖 112、911、带特殊前缀和普通号码均不会进入 tel URI | 平台实际接受自定义 scheme，API 26/27 的 sip 地址分支，界面不跳到本机拨号器 |
| 后台接听与麦克风前台服务 | 保留 phoneCall／microphone 服务类型与权限检查 | API 34+ 锁屏／后台／蓝牙接听实际服务启动成功且双向有声 |

这些是候选实现，不表示 P2 的系统行为已验收，也不表示已达到发布条件。按用户要求，当前停在执行模拟器验证之前。真实蓝牙、蜂窝冲突和长时间保持仍按原计划需要真机验证。

## 构建与自动化命令

在当前云环境先 `source /workspace/.onboarding/env.sh`。其他开发环境使用已配置的 JDK 17 和 Android SDK 35。

```sh
./gradlew :app:assembleDebug :app:assembleDebugAndroidTest :app:testDebugUnitTest :app:lintDebug --no-daemon --max-workers=2
```

回退构建检查：

```sh
./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug -PcoreTelecom=false --no-daemon --max-workers=2
```

x86_64 联合测试包准备命令（只编译，不启动模拟器）：

```sh
./gradlew :app:assembleDebug :app:assembleDebugAndroidTest -PrtcLabAbi=x86_64 -PrtcLabRunner=com.callagent.lab.HostJointInstrumentation --no-daemon --max-workers=2
```

切回默认构建后再生成 arm64 候选 APK，避免误将回退 APK 用于 Core-Telecom 测试。新增单测覆盖媒体许可交错、保持不改变业务阶段／静音、终态拒绝恢复、迟到的激活结果不能覆盖系统保持、显式能力位、不透明外呼地址、断开原因和提前挂断等待平台结果。旧服务启动时序测试显式选择 legacy 后端，继续覆盖回退实现；它不能代替新后端的设备测试。

## 模拟器执行清单（尚未执行）

1. 准备 API 26/28、33 和 34+ 模拟器；每次使用明确的 `adb -s <serial>`，分别保留日志。默认构建只打包 arm64 原生库；x86_64 模拟器测试使用 `-PrtcLabAbi=x86_64`。该选项只允许 debug 实验构建，不允许 x86_64 发布包；不能把 ABI 安装失败误判为 Telecom 失败。
2. 使用已有本地 SoulGem／Homura 联合测试 fixtures 和 `HostJointInstrumentation` 跑真实授权／接听／WebRTC 流程。首次创建 test APK 时按现有联合测试说明设置 `-PrtcLabRunner=com.callagent.lab.HostJointInstrumentation`；不要在普通用户手机上写入 fixture 或运行 instrumentation。
3. 保存默认电话角色（`cmd role get-role-holders android.app.role.DIALER`，仅支持的系统），确认测试前后不变。检查应用未申请默认电话角色。
4. 覆盖来电、外呼、拒接、未接超时、其他主机先接、快速接听后挂断、平台注册中挂断；每次结束执行 `dumpsys telecom`、`dumpsys activity services com.callagent.host`，确认没有残留系统通话、通话服务和通知。
5. 以模拟器 console 注入本机 GSM 来电，接听后检查远程通话 held 状态；结束本机通话后经系统回调或用户点击恢复。模拟器的音频焦点结果不能代替真机双向音频隔离结果。
6. 保持中分别断网、恢复网络、远端挂断，检查没有意外开麦／播放／复活通话；恢复时原用户静音保持。
7. 关闭屏幕／锁屏／应用退到后台，从通知接听和挂断；检查 phoneCall／microphone 前台服务实际启动与停止，不能只看页面显示 ACTIVE。
8. 撤销麦克风、通知、全屏权限后重复对应操作，记录系统限制和正确退化；不通过 adb 强行授予普通安装无法获得的特权绕过结果。
9. 外呼地址只在受控测试后端截获。**不得实际拨打 112、911 等紧急服务验证**；确认传给本机 Telecom 的自定义 URI 后，使用测试网关／桩截获特殊号码，不让请求进入真实蜂窝拨号。
10. 使用同一签名、递增 versionCode 测试旧版 → 候选版 → 旧适配器回退版。不能用降级安装命令代替用户的覆盖安装流程。

至少记录构建提交、系统 API、默认电话角色、权限、步骤、CallRuntime 状态、平台 Telecom 状态、服务状态、日志和结果。失败时先修复再重复相关回归。

## 真机与发布边界

API 33 或更低及 API 34+ 各一台真机验证本机 SIM 冲突、蓝牙／有线实际声音、设备切换和锁屏后台行为；保持至少 10 分钟后恢复，确认 ICE、PCM 桥及服务端授权仍有效。未完成这些设备证据前，不宣称串音、蓝牙兼容或后台麦克风问题已解决，也不发布正式版。

## 本地验证结果（2026-11-13）

- Core 默认构建：assembleDebug、assembleDebugAndroidTest、testDebugUnitTest、lintDebug 全部成功；最终 x86_64 校验包含 **288 项测试，0 失败、0 错误、0 跳过**。
- 保留的旧适配器：`-PcoreTelecom=false` 的 assembleDebug、testDebugUnitTest、lintDebug 成功。
- 最终源代码已重新生成 arm64 与 x86_64 应用／测试 APK；测试 APK 指定 HostJointInstrumentation。静态检查确认相应 APK 内的原生库 ABI 正确。
- 检查合并 Manifest：默认构建禁用旧 ConnectionService、包含库的 JetpackConnectionService；未引入 READ_PHONE_STATE。回退构建启用旧 ConnectionService。
- `git diff --check` 通过。未启动模拟器、未运行设备 instrumentation、未触发正式发布。

当前云工作区产物保存在 `/workspace/shared/core-telecom/`：`core-arm64-debug.apk`、`core-arm64-androidTest.apk`、`core-x86_64-debug.apk`、`core-x86_64-androidTest.apk`，另有构建日志和 `SHA256SUMS`。这些是本地测试产物，不是正式发布包；回退临时 APK 也不用于覆盖安装验收，因为尚未配置递增 versionCode。

后续从上面的“模拟器执行清单”开始，不将本节的自动化结果替代 P2 四项设备证据。
