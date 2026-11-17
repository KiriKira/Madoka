# Core-Telecom 实现审阅与修复记录（2026-11-17）

本文是交给下一位审阅者（计划由 GPT 复审）的记录：审阅了什么、发现了什么、改了什么、为什么这样改、哪些没改、需要重点核对哪里。

- 分支：`plan/core-telecom-migration`
- 被审阅的实现提交：`8d9841b`（feat: migrate calls to Core-Telecom with hold and endpoint control）
- 审阅依据：该提交的全部运行代码改动；`androidx.core:core-telecom` 1.0.1 官方源码制品（`CallsManager`、`CallSession`、`CallSessionLegacy`、`JetpackConnectionService`）；修订后的 [core-telecom-migration-plan.md](core-telecom-migration-plan.md)。
- 本次修复提交：紧随 `8d9841b` 之后的一个提交（提交信息以 "fix: keep Core-Telecom opt-in" 开头）。

## 1. 总体结论

`8d9841b` 基本照修订计划实现，审阅中没有发现会直接造成串音或残留系统通话的代码错误。与计划一致的部分：

- Core-Telecom 1.0.1，工具链未动；显式传能力位；外呼地址固定为 `gsm2sip:<callId>`；断开原因只用 LOCAL／REMOTE／REJECTED／MISSED。
- 通话协程由进程级持有，先等平台断开结果再停前台服务和 `stopSelf`；注册中挂断会等注册结果，不以取消 `addCall` 代替断开。
- `registerAppWithTelecom` 每进程一次；只清理本包旧账户。
- `CallAudioPermit` 把"授权"和"平台激活"取交集，终态不可恢复；系统保持同步暂停媒体，不排在 IO 队列后。`PlatformActivation` 用版本号防止迟到的激活结果覆盖保持。
- 删除直接操作 AudioManager 的路径；距离传感器只在确认听筒时熄屏。
- 旧 ConnectionService 实现保留，并加入本机保持（相当于计划 P1）。

## 2. 发现与处理

| # | 发现 | 严重度 | 处理 |
| --- | --- | --- | --- |
| 1 | `coreTelecom` 默认 `true`，`release.yml` 的 `assembleRelease` 不传该参数，正式发布会直接带上未经设备验证的 Core-Telecom | 高 | **已修**：默认改为 `false` |
| 2 | 提交带 `[skip ci]`，没有 CI 结果；默认 CI 若只测一种后端，另一种无覆盖 | 高 | **已修**：本次提交不带 `[skip ci]`；CI 增加 `-PcoreTelecom=true` 的单测与 lint 步骤 |
| 3 | 界面接听先让平台接听再做业务检查；API 34+ 上平台接听可能先保持用户的 SIM 电话，随后发现来电已过期时，SIM 电话留在保持状态 | 中 | **已修**（仅过期／非响铃部分）：平台接听前先做本地可接听检查 |
| 4 | 外呼 `addCall` 失败走 `onTelecomIncomingFailed`，提示"无法添加来电" | 低 | **已修**：按方向区分 |
| 5 | 端点／静音观察任务出错会结束整通电话 | 低 | **已修**：只记日志并提示，不结束通话 |
| 6 | `HostCallService.onDestroy` 调 `coordinator.failed` 后未 `notifyChanged` | 低 | **已修** |
| 7 | 验证文档写入了实现者沙箱路径（`/workspace/...`） | 低 | **已修** |
| 8 | "其他主机先接"时，平台已接听而服务端拒绝，SIM 电话同样可能留在保持 | 中 | **未修**：本地无法预知，属于计划接受的"平台接听先于服务端确认"设计；列为设备验证项 |
| 9 | 与迁移无关的 `rtcLabAbi` x86_64 构建选项混在同一提交 | 低 | **未修**：有 release 保护；不改写已推送历史 |
| 10 | 只有通话页能恢复保持的通话，通知里没有恢复入口 | 低 | **未修**：功能增补，留待设备验证后决定 |
| 11 | 服务启动时序测试 `HostCallServiceStartupTest` 固定选旧后端，新后端在服务层面没有自动化覆盖 | 中 | **未修**：Robolectric 下无法真实驱动 `CallsManager`；靠设备验证清单覆盖 |

## 3. 改动明细

### 3.1 Core-Telecom 改为显式开启（发现 1）

`app/build.gradle.kts`：`coreTelecom` 属性默认值由 `true` 改为 `false`，`CORE_TELECOM` 与 `legacyTelecomEnabled` 共用同一个解析结果。

效果：默认构建、CI 上传的 debug APK、release 构建都使用 `LegacyCallTelecomController`（现有 ConnectionService + 本机保持 + 统一媒体许可），即计划中可先行发布的 P1；Core-Telecom 只在 `-PcoreTelecom=true` 时启用。改回默认值的条件：[core-telecom-validation.md](core-telecom-validation.md) 记录了 P2 四项设备证据。

### 3.2 CI 覆盖两种后端（发现 2）

`.github/workflows/android.yml` 末尾新增步骤 `./gradlew :app:testDebugUnitTest :app:lintDebug -PcoreTelecom=true`。放在上传 APK 之后，且不执行 `assemble`，所以上传的 APK 仍是默认后端。

### 3.3 接听前本地检查（发现 3）

- `CallSessionCoordinator.canAnswer(callId, now)`：与 `answer()` 共用同一判断（来电、正在响铃、未过期），但不改变状态。
- `CallRuntime.answerFromVisibleUser`：先 `canAnswer`；不可接听时走原有 `expireIncomingAnswerIfNeeded` 并返回 `false`，不调用平台接听。这恢复了迁移前"先业务检查"的行为，只是不提前进入 ANSWERING。
- `CoreTelecomAdapter.answer`：在等待注册和操作锁之后、`control.answer()` 之前再查一次；不可接听时直接返回，不调用平台接听，也不调用 `accepted()`。过期来电由原有的 `scheduleInboundExpiry` 计时器以 MISSED 结束。
- `CoreTelecomAdapter` 的系统 `onAnswer` 回调：原来是 `check(phase == INCOMING_RINGING)`，改为 `check(canAnswer)`，即同时检查是否过期。失败时的后续处理与原来的 `check` 失败相同：库拒绝这次平台接听，适配器 `catch` 分支断开平台通话并 `failCall`。
- 新增单测 `CallSessionCoordinatorTest.canAnswerMatchesAnswerWithoutChangingState`。

### 3.4 次要修复（发现 4–7）

- `CoreTelecomAdapter.add` 的失败分支：来电调 `onTelecomIncomingFailed`，外呼调 `onTelecomOutgoingFailed`。
- `CoreTelecomAdapter.watch`：观察任务异常只记日志并设置状态文案"系统音频状态暂时无法更新。"，不再结束通话。
- `HostCallService.onDestroy`：`coordinator.failed` 返回 `true` 时 `notifyChanged`。
- `core-telecom-validation.md`：默认后端说明、构建命令（Core-Telecom 设备测试必须带 `-PcoreTelecom=true`）、去掉沙箱路径。

## 4. 本次验证

见文末"验证结果"。云环境无设备，未跑模拟器或真机；设备验证仍从 [core-telecom-validation.md](core-telecom-validation.md) 的"模拟器执行清单"开始。

## 5. 请复审重点核对

1. **默认值翻转的副作用**：默认构建现在是旧后端。确认没有其他地方（文档、脚本、联合测试说明）仍假定默认是 Core-Telecom；确认旧后端加入保持后本身的行为（`HostCallConnection.onHold`／`onUnhold`、`LegacyCallTelecomController.setActive` 调 `onTelecomActive`）适合作为 P1 发布。
2. **`onAnswer` 回调对过期来电的处理**：原实现对过期来电先调用 `onTelecomAnswer`（会 `rejectCall("来电已超时。")`，以 LOCAL 断开），再因 `check(ANSWERING)` 失败抛异常；现在直接抛异常，结束原因变为适配器 `catch` 的 `failCall`（失败文案）。确认这一文案差异可接受，或建议改为以 MISSED 结束。
3. **`answer` 操作的提前返回**：不可接听时用户点击后没有任何即时反馈，依赖过期计时器在数百毫秒内结束通话。确认计时器在所有过期路径上都已安排（`scheduleInboundExpiry` 在来电注册时调用）。
4. **观察任务降级**：静音观察失败后，系统／车机静音不再同步到 Madoka。确认这比结束通话更可取，或提出更好的处理。
5. **CI 新步骤**：确认放在上传之后、只做单测与 lint，不会覆盖已上传 APK，也不会让 CI 时间不可接受。

## 验证结果

针对修复提交 `a96a9a5`：

| 检查 | 命令／位置 | 结果 |
| --- | --- | --- |
| CI 默认后端 | [run 531](https://github.com/KiriKira/Madoka/actions/runs/44085580407)："Assemble and run unit tests"（assembleDebug、testDebugUnitTest、lintDebug） | 通过 |
| CI Core-Telecom 后端 | run 531："Unit tests and lint with Core-Telecom backend"（`-PcoreTelecom=true`） | 通过 |
| CI UI 冒烟 | [Android KVM UI smoke run 209](https://github.com/KiriKira/Madoka/actions/runs/44085580490) | 通过 |
| 本地默认后端 | `./gradlew :app:assembleDebug :app:testDebugUnitTest :app:lintDebug`（JDK 21、SDK 35） | 通过；289 项单测，0 失败、0 错误、0 跳过 |
| 本地 Core-Telecom 后端 | `./gradlew :app:testDebugUnitTest :app:lintDebug -PcoreTelecom=true` | 通过；289 项单测，0 失败、0 错误、0 跳过 |

说明：本地首轮默认构建中 `FullBackupTest` 失败一次，原因是 Robolectric 无法经代理下载 `android-all-instrumented:9` 运行时（与代码无关），重试后通过。另有一次本地 Core-Telecom 运行与另一个构建在同一目录并发执行而失败，结果作废，已顺序重跑。

未做：模拟器、真机、instrumentation。P2 四项设备证据仍为空。
