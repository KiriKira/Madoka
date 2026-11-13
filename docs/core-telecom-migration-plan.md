# Madoka Core-Telecom 迁移实现计划

日期：2026-11-13。状态：待实施；本分支只编写计划，未修改运行代码。同日经审阅修订，变更摘要见第 9 节。

分支：`plan/core-telecom-migration`。代码基线：`27823c2610b19072b1f2cf1f8dc652d0b76a8732`。

## 1. 目标与权限边界

将 Madoka 从自行维护的 self-managed `ConnectionService` 迁移到 Google 官方 AndroidX Jetpack `androidx.core:core-telecom`，同时交付本机电话冲突协调、音频设备管理和锁屏／外设接听链路改进。保留 Madoka 现有通话页面与远程 SIM 业务。

- **Madoka 不申请默认电话角色 `ROLE_DIALER`，不要求成为系统电话 App，不需要 root、系统签名或特权安装。**系统原有电话 App 继续处理本机 SIM 通话。
- 使用自管理通话所需的 `MANAGE_OWN_CALLS`，按需保留麦克风、通话前台服务、通知和全屏来电相关权限。默认电话角色与这些权限是不同机制。
- 不迁移到由系统拨号器承载 UI 的 managed ConnectionService 模式。
- Homura 通过默认电话角色管理真实 SIM 通话的现有要求保持原样；本计划不改变它。
- 不新增视频、会议、通话转移、跨设备音频流送或多路远程通话能力。不因库支持某项能力就对外声明支持。

完成标准是可验证的系统协调与音频行为改善，不只是替换依赖后能够编译。

本计划包含两类可分离的工作：**行为改进**（统一音频许可、本机保持）和**系统适配层替换**（换用 Core-Telecom）。前者不依赖后者，先在现有 ConnectionService 上交付并可独立发布，再替换适配层；顺序见第 5 节。

## 2. 当前实现与迁移依据

| 当前实现 | 对迁移的影响 |
| --- | --- |
| `CallTelecomController.kt` 注册 self-managed PhoneAccount，创建来去电并管理 Connection | 用 CallsManager 和每通电话的 CallControlScope 替换；同步处理旧账户清理 |
| `CallRuntime.onTelecomHold()` 为空；CallSessionCoordinator 没有保持状态 | 需要实现本机保持与恢复，不能直接声明支持保持 |
| `RtcCallEngine.enableAudio/suspendAudio` 最终控制 NativePeer 的录音、播放和发送轨道 | 可复用媒体暂停能力，但需要验证设备上实际释放／恢复音频的行为 |
| 网络恢复与前台服务就绪都可能重新启用音频 | 必须统一音频启用条件，不能用各入口直接开关来实现保持 |
| 音频路由以类型枚举表示；界面主要是听筒／扬声器切换 | 改为带标识、名称、类型的具体端点，处理多设备和切换结果 |
| CallRuntime 存在绕过 Telecom 直接设置 AudioManager 的兜底 | 在 Telecom 管理的通话中移除这条并行控制路径 |
| 已有 CallStyle 通知、全屏 Intent、前台服务和推送唤醒 | 复用 UI 与唤醒能力，重排注册、通知、接听和麦克风启用顺序 |
| 现有测试包含 Telecom、会话状态、通知、网络恢复与联合测试入口 | 保留业务回归；替换直接依赖旧 Connection 的测试，新增跨系统版本设备测试 |

Core-Telecom 默认在 API 26–33 使用兼容 ConnectionService 路径，在 API 34+ 使用平台 transactional call API。两条路径都必须验收。

### 换库换来什么

本机保持本身不需要换库。现有 `HostCallConnection` 只声明了 `CAPABILITY_MUTE`，所以用户接听本机 SIM 来电时系统只能结束 Madoka 通话；补上 `CAPABILITY_HOLD | CAPABILITY_SUPPORT_HOLD` 并实现 `onHold`／`onUnhold` 即可让位，这也正是库在 API 26–33 兼容路径上的做法。

换库得到的是：

- API 34+ 改用平台 transactional call API，不再自行维护 ConnectionService。
- 带标识、名称、类型的端点模型（`CallEndpointCompat`）及其在旧系统上的兼容实现。
- 兼容层由 Google 维护。

代价是：新增协程依赖；失去 `onShowIncomingCallUi` 与 `onPlayDtmfTone` 回调；需要验收两条平台路径而不是一条。

## 3. 版本与构建基线

Google Maven 上有两条稳定线。以下数据取自各自的 AAR 元数据与 POM：

| | 1.0.1 | 1.1.0 |
| --- | --- | --- |
| 编译 SDK 下限 | 34 | 36.1（`minCompileSdk=36`、`minCompileMinorSdk=1`） |
| AGP 下限 | 8.1.1 | 8.9.1（制品下限；实际还须支持 minor SDK 配置） |
| Kotlin 标准库（POM） | 2.0.21 | 2.2.20 |
| 协程库（POM） | 1.8.1 | 1.9.0 |
| 相对 1.0.1 的增量 | — | `registerAppWithTelecom` 的 `backwardsCompatSdkLevel`（可在 API 34+ 强制走兼容路径）；`DisconnectCause.ERROR` 自动重映射为 LOCAL；端点处理改动；通话类型切换等本计划不使用的能力 |

1.1.0 是当前最新稳定版，Maven 元数据最后更新于 2026-11-04。

当前项目为 compileSdk 35、AGP 8.7.2、Gradle 8.9、Kotlin 插件 2.1.20，**不改工具链即满足 1.0.1 的要求**，不满足 1.1.0。

**首次迁移采用 1.0.1。**本计划用到的公共 API（`CallsManager.addCall`、`CallControlScope` 的 `answer`／`setActive`／`setInactive`／`disconnect`／`requestEndpointChange`、`currentCallEndpoint`／`availableEndpoints`／`isMuted`、`SUPPORTS_SET_INACTIVE`）在 1.0.1 中都存在。这样迁移与工具链升级互不牵连。

升级到 1.1.0 作为独立的后续工作：先单独提交并发布一次无功能变化的工具链升级（选定并固定兼容 SDK 36.1 的 AGP、Gradle、Kotlin、JDK 组合，覆盖 app、rtcmedia、CI），再换库版本。只有当 P2 原型发现必须依赖 1.1.0 的修复或 `backwardsCompatSdkLevel` 时才把它提前，且仍作为迁移之前的独立提交，不混入迁移改动。

其余约束不变：协程依赖显式声明并保持版本收敛。继续支持 minSdk 26；不以提高 targetSdk 为前提。检查库引入的 `lifecycle-service` 与已有 lifecycle 严格版本约束、Robolectric 和签名发布任务的兼容性。发现依赖问题时先定位具体依赖，不通过全局强制版本或关闭元数据检查绕过。

## 4. 设计约束

### 4.1 生命周期与业务权威

保留 CallRuntime／HostCallService 的业务职责，引入窄接口的 Telecom 适配层，隔离 CallsManager、CallControlScope 和平台端点对象，便于测试。

- 每个业务 callId 对应一个平台通话会话和一个受管理的协程 Job；平台会话／端点标识仅在该通话生命周期内使用。
- 通话协程不由 Activity 持有，也不能随 HostCallService 销毁被直接取消：**取消 `addCall` 所在协程不会断开平台通话**（库在取消路径上没有断开逻辑），会留下占着音频焦点的残留通话。scope 放在进程级的 CallRuntime；结束通话时先调用 `disconnect()` 并在有界时间内等到结果，再取消观察任务。现有 `finishCall` 在断开后立即 `stopSelf()`，迁移时必须改成这个顺序。不将整个网络信令层一并重写为协程。
- `registerAppWithTelecom` 只在进程启动且没有通话时调用一次，不沿用现有"每次来去电前 `ensurePhoneAccount`"的做法；1.1.0 的实现会先注销再注册，文档明确不得在通话中调用。账户标签由库固定为 `Telecom-Jetpack`，现有"Madoka 远程 SIM"标签不再保留，P2 核查它是否出现在系统界面。
- `CallAttributesCompat.callCapabilities` 的**默认值就包含 `SUPPORTS_SET_INACTIVE`**。适配层必须显式传入该参数；保持行为未在新适配层上验证的构建传 `0`，否则系统会对一个不暂停媒体的通话下发保持并造成串音。
- 外呼地址沿用不透明的自定义地址（现有为 `gsm2sip:<callId>`），不把 `tel:<号码>` 交给库。库在兼容路径上直接以 `callAttributes.address` 调用 `placeCall`，且它注册的账户不声明 URI scheme；`tel:` 地址在 API 26–33 上可能触发系统对紧急号码等的拦截并转到系统拨号器。这一行为尚未验证，列为 P2 核查项。号码只用于 Madoka 自己的界面和通知。API 26／27 上库会把外呼地址替换为 `sip:<包名>`。
- 断开原因只使用库文档允许的四种，并在适配层集中映射：用户挂断或本机失败 → LOCAL；远端结束、其他主机先接 → REMOTE；用户拒接 → REJECTED；来电超时未接 → MISSED。现有代码传的 `ERROR` 在 1.0.1 不会被重映射，不得传入。
- `addCall()` 持续到通话结束；在其 scope 回调中报告注册就绪，不等待函数返回后才启动后续业务，也不阻塞现有串行 IO 队列。
- 区分 App 发起的 answer/setActive/setInactive/disconnect 与系统回调，避免回调再次调用相同平台操作形成循环。
- 检查所有 CallControlResult 与异常。注册失败时撤销待拨授权或退出本机来电参与者，沿用现有业务语义清理资源。
- 系统回调约 5 秒内必须完成相应本地动作。暂停录音／播放必须在保持回调成功返回前完成；不能只排队后报告成功。
- 接听保留服务器裁决：平台接受了接听操作不等于远端已接通。界面可显示“正在连接”，业务 ACTIVE 仍以现有服务端／网关确认链路为准。回调、服务器请求和失败补偿的时序需通过早期原型验证。
- 网络请求不无界占用系统回调。不能兑现的请求按库契约失败，及时断开和补偿；迟到的网络结果不得恢复终态电话。
- UI、通知、耳机／系统入口共享操作串行化与幂等处理。过期来电、其他主机先接、用户快速挂断、注册尚未完成等情况均不能产生重复接听或残留通话。

### 4.2 本机保持与音频许可

第一版保持是 **Madoka 本机保持**：远程蜂窝通话和 WebRTC 会话继续存续，暂停本机录音、发送和播放。远端通常听到静音，蜂窝通话仍可能计费。不向 SoulGem／Homura 新增运营商 hold/unhold 协议。

在会话状态中将“远端通话阶段”和“本机音频激活／保持状态”分别表示。用户静音也是独立状态，不用静音代替保持。

统一计算音频许可，至少包含以下条件：

1. 通话未结束，且业务阶段允许使用该音频路径；现有外呼回铃仍按其阶段处理。
2. 平台已授予当前通话激活状态，没有系统／用户保持原因。
3. 网络和服务器授权有效，没有网络暂停原因。
4. 麦克风权限与所需服务条件满足。

任一条件不满足就暂停相关媒体；解除一个暂停原因不能清除其他原因。远端已连接不代表平台已批准恢复。静音用户恢复通话后仍保持静音。

- `onSetInactive`：同步业务状态，完成音频暂停，更新通知／界面的“已保持”，保留会话、控制租约与终态事件处理。音频暂停在回调里直接调用（`suspendAudio` 是同步的），不排进 HostCallService 的串行 IO 队列；该队列会被 HTTP 请求占住，排队可能超过回调期限。
- `onSetActive`：校验仍是当前有效通话，再结合其他许可条件决定是否恢复媒体。
- 用户恢复入口：先请求系统 setActive，成功后再更新激活状态；失败时保留保持状态并给出可理解的提示。
- 本机 SIM 通话结束后，仅在收到系统恢复请求或用户恢复获系统批准时开麦，不承诺所有 ROM 都自动恢复。
- 保持期间允许挂断；保持期间的服务端 active 状态应视为合法，不被网络恢复校验误判成状态冲突。
- 保持期间远端挂断／其他终止事件仍正常结束，迟到的恢复、网络或服务回调不得开麦。
- 单路远程通话限制继续存在。接听／拒接另一通远程邀请不得因系统操作被误映射为全局拒接当前电话。

现有的 `suspendAudio` 路径是为短暂断网设计的，保持可能持续数分钟。需要实测长时间保持（至少 10 分钟）期间 ICE 连接、网关侧 PCM 桥和服务端通话授权都不会因为没有上行音频而结束通话；如果会，"协议无需变更"的前提不成立，须回到本节重新确定范围。

这些行为先在现有 ConnectionService 上实现（P1）：统一音频许可和会话状态与后端无关，适配层替换后只需把同一套逻辑接到新回调。只有上述行为实现并验证后，才声明保持能力——旧实现上是 `CAPABILITY_HOLD | CAPABILITY_SUPPORT_HOLD`，新适配层上是 `SUPPORTS_SET_INACTIVE`。

### 4.3 音频端点与界面

- 观察 currentCallEndpoint、availableEndpoints、isMuted；将具体端点 ID、名称、类型映射为 App 内部只读模型。
- 使用 requestEndpointChange 请求切换，处理成功、失败、设备已移除和迟到结果。界面显示以系统实际端点更新为准，不在发出请求时假定已切换。
- 无外接设备时保留快捷听筒／扬声器操作；有外接设备时提供端点选择，并正确显示蓝牙或有线设备，不把所有非扬声器设备标为“听筒”。
- API 26–33 的蓝牙信息和指定设备切换按平台能力降级，不能保证所有旧系统都能区分多个蓝牙设备。
- 库的 Manifest 会自动合入 `BLUETOOTH_CONNECT`、`MODIFY_AUDIO_SETTINGS` 和一个导出的 `MuteStateReceiver`，不需要也不能靠"不声明"来回避。保留 `BLUETOOTH_CONNECT` 声明，只在用户打开端点选择且存在蓝牙设备时请求运行时授权；未授权时库只给出当前活动的蓝牙设备，其余路由照常可用。如果决定不提供多蓝牙设备选择，用 `tools:node="remove"` 去掉该权限，而不是留着不用。
- 移除活动 Telecom 通话中直接调用 setCommunicationDevice／speakerphone／SCO 的并行兜底；切换失败显示真实状态并允许重试。
- 端点更新联动距离传感器灭屏逻辑；只有实际走听筒的适当通话阶段启用，不在蓝牙、扬声器或保持时误熄屏。
- 保留应用内 DTMF。旧 Connection 的 onPlayDtmfTone 在 1.1.0 公共接口中没有直接对应回调，系统／外设拨号盘透传作为迁移兼容核查项，不能宣称自动保留；不通过私有 API 补齐。

### 4.4 通知、锁屏与前台服务

- 继续使用 CallStyle、现有 Activity 和全屏 Intent，遵守通知／全屏权限设置；不能用直接后台启动 Activity 绕过限制。
- 将通话成功注册后的通知发布作为明确步骤，保证在官方要求的 5 秒窗口内发布，不依赖旧 onShowIncomingCallUi 回调。
- 盘点 HostCallService 与 CallForegroundService 的职责，确保 CallsManager 生命周期有正确的通话前台承载；不要因库提供 foreground 支持就直接删除 microphone 服务条件或权限检查。
- 分别验证普通 UI、锁屏通知、蓝牙按键触发的接听路径。振铃不提前录音，接听后服务与权限满足才启用所需音频。
- 保持、恢复、来电取消、重复通知和快速挂断时，前台服务启动／停止必须闭合，避免残留铃声、通知和启动超时。
- 推送、Reception v2、无推送 WSS 和待机策略继续使用现有实现；迁移不引入常驻通话服务或新的待机唤醒锁。

### 4.5 升级、清理与回退

- 新接入稳定后移除自定义 HostCallConnectionService 声明及旧实现，使用库合并进 Manifest 的必要组件；检查合并后的 Manifest。
- 只在无活动旧通话时清理本包旧 component/accountId 对应的 PhoneAccount，不删除其他账户或系统 SIM 账户。验证覆盖安装后的幂等行为。
- 同一通电话只进入一种 Telecom 实现，不同时注册两次。失败后不对正在建立的通话自动切换后端，以免重复拨号。
- 记录平台注册／激活／保持／端点切换／释放的结构化结果和延迟，不在日志写完整号码、凭据或 SDP。
- Android 不允许覆盖安装 versionCode 更低的 APK，所以回退只能是**用更高的 versionCode 重新发布旧实现**，不是装回旧包。实测"旧版 → 新版 → 回退版"的覆盖安装和账户重新注册。
- 旧实现在适配层替换后的第一个发布版本中保留在代码里，回退版只需切回旧适配器；确认新实现稳定后再删除。同一构建中只启用一种实现。
- 1.0.1 没有在 API 34+ 强制走兼容路径的开关（1.1.0 的 `backwardsCompatSdkLevel`）。如果 transactional 路径在某些机型上有问题而兼容路径正常，再按第 3 节的方式升级到 1.1.0。
- 计划可分阶段提交，但发布前必须完成下述设备验收。

## 5. 实施顺序与预计工作量

按一名熟悉项目的 Android 开发者估算，设备和现有测试链路可用时合计 **9–15 人日**，不含升级到 1.1.0 所需的工具链工作。这是估算，不是已经通过验证的工期。

| 阶段 | 工作与交付 | 完成条件 | 人日 |
| --- | --- | --- | ---: |
| P1 | 在现有 ConnectionService 上实现统一媒体许可、本机保持（声明 HOLD 能力，实现 `onHold`／`onUnhold`）、恢复入口与状态显示；可独立发布 | SIM 冲突让位；保持与断网互不覆盖；终态不复活；长时间保持不被网关／服务端结束 | 2–3 |
| P2 | 引入 1.0.1 与协程依赖；最小来去电原型，取得四项证据：协程持有与断开顺序、接听回调时序、外呼地址行为、后台接听后麦克风前台服务能否启动 | 编译／现有测试可执行；API 33 与 34+ 注册、来去电及清理成立；四项各有结论 | 1–2 |
| P3 | 替换 Telecom 适配层；接入业务控制、系统回调、注册失败补偿、断开原因映射；迁移旧账户；把 P1 的保持逻辑接到新回调 | 原来去电、应用内 DTMF、挂断、多主机裁决、保持／恢复均保持；无重复通话／残留通话 | 2–3 |
| P4 | 端点模型和选择器；通知、锁屏、前台服务整合；清理旧控制路径 | 真实设备状态驱动 UI；受限权限和切换失败有正确退化 | 1–2 |
| P5 | 自动化与设备联合回归、缺陷修复、覆盖安装／回退验证 | 验收矩阵通过并记录设备／版本／证据，未覆盖项明确列出 | 3–5 |

P1 不依赖换库，结束即交付本计划的主要用户价值，并且让统一媒体许可在换适配层之前就经过真机验证。P2 把高风险时序放在动业务代码之前验证；其中"后台接听后麦克风前台服务能否启动"在 API 34+ 上尤其要有证据，因为 transactional 路径不再有系统对 ConnectionService 的绑定。如果 P2 的任何一项没有可行方案，停在 P1 的成果上，不进入 P3。

若需要运营商保持、多路远程电话或广泛车机兼容，需另行扩大范围与估算。

主要改动位置：

- 构建：根及 app Gradle 配置、Gradle wrapper、CI 对应 SDK/JDK 配置、必要的 rtcmedia 编译配置。
- 系统适配：`CallTelecomController.kt` 或替代适配器、`AndroidManifest.xml`。
- 业务协调：`CallRuntime.kt`、`CallSessionCoordinator.kt`、`HostCallService.kt`、`RtcCallEngine.kt`；NativePeer 仅在实际媒体暂停行为需要时修改。
- 展示与服务：`CallActionActivity.kt`、MainActivity 的通话状态展示、`CallNotificationManager.kt`、`CallForegroundService.kt`、`ProximityScreenLock.kt`，以及阶段判断相关铃声／回铃处理。
- 测试：旧 Telecom 测试、会话／媒体许可单元测试、通知与恢复测试、现有 HostJointInstrumentation 及设备验收文档。

## 6. 测试与验收

### 自动化

对统一媒体许可和操作状态做有行为价值的测试，不用只验证转发了同名方法的测试替代设备验证。

- App 和系统两种入口的接听、保持、恢复、挂断，以及重复／交错操作。
- 注册失败、操作拒绝、回调超时、挂断先于注册完成、迟到服务端成功、其他主机先接。
- 保持 + 断网 → 网络恢复仍保持；断网 + 保持解除 → 网络未恢复仍暂停；静音 + 保持恢复 → 仍静音。
- 保持期间远端结束、账户切换、服务销毁；资源只释放一次，终态不被迟到事件覆盖。
- 端点连接／移除、切换失败、旧会话端点结果丢弃，以及距离传感器状态。
- 通知发布时序、服务启动失败、正在启动即挂断、拒绝麦克风／通知／全屏权限。
- 适配层构造通话属性时显式给出能力位；未启用保持的配置不含 `SUPPORTS_SET_INACTIVE`。
- 各业务终止原因映射到允许的四种断开原因，不出现 `ERROR`。
- 结束通话时先得到断开结果再取消通话协程；服务销毁先于断开完成时不留平台通话。
- 保留现有来电授权、全局拒接、多主机裁决、网络恢复和 Reception v2 相关回归。

### 设备矩阵

真机至少两台：一台 API 33 或更低（兼容路径），一台 API 34+（transactional 路径）。API 26–33 走同一条兼容路径，所以 API 26 与 28 用模拟器验证基础生命周期和库内按版本分支的端点／地址处理即可。蓝牙、真实蜂窝冲突、锁屏后台行为必须有真机证据；明确标注未覆盖版本，不用模拟器代替硬件验收。P1 发布前在旧实现上先跑一遍其中与保持相关的行。

| 场景 | 验收结果 |
| --- | --- |
| Madoka 不是默认电话 App | 来去电正常；系统默认电话角色不变；无默认电话授权提示 |
| 远程通话中接听本机 SIM 来电 | 远程媒体停止录音／播放，本机电话音频正常，不发生串音 |
| 本机 SIM 通话结束后恢复 | 系统请求或用户获准恢复后音频恢复；静音状态保持 |
| 已有本机通话时发起／接听远程电话 | 按系统许可处理，不能先开麦；拒绝时业务资源及时清理 |
| 保持时断网／切网／网络恢复 | 没有意外开麦；远程终态仍能正确结束本机通话 |
| 保持 10 分钟以上后恢复 | 通话未被网关或服务端结束；恢复后双向音频正常 |
| 经远程 SIM 拨打紧急号码及带特殊前缀的号码 | 系统不拦截、不跳转系统拨号器；通话按普通远程外呼建立 |
| 后台、锁屏或蓝牙按键接听 | 麦克风前台服务启动成功，接通后双向有声 |
| 蓝牙接听／挂断、连接／断开、有线插拔 | 实际声音和 UI 状态一致，切换失败不伪装成功 |
| 两个可用蓝牙设备 | 支持的平台可选具体设备；不支持的平台有明确退化，无崩溃 |
| 锁屏、屏幕关闭、App 在后台 | 通知及接听挂断可用；不提前录音，无残留铃声／通知 |
| 来电过期／其他主机先接／快速重复点击 | 不重复接听，不复活通话，不误触发全局拒接 |
| 旧版 → 新版 → 更高版本号的回退版，依次覆盖安装 | 账户注册正常，没有双重来电或遗留不可用账户影响通话 |
| 无通话待机 | 无新增常驻通话服务、麦克风使用或待机唤醒锁 |

设备记录包含型号、系统版本、蓝牙设备、权限状态、构建版本、测试步骤与结果；声音隔离必须通过真实收发音或可复现音频检测确认，不能只凭 UI 显示“已保持”。

## 7. 发布完成条件

- [ ] P1 的保持与统一媒体许可已在现有实现上通过真机验收（可先行发布）。
- [ ] 依赖、合并 Manifest（含库合入的权限与组件的取舍）和新旧账户迁移已验证。
- [ ] P2 的四项原型结论已记录；P3–P4 的系统接入、保持／恢复、端点和通知行为完整交付。
- [ ] 平台激活、媒体许可、服务器授权与业务显示状态一致。
- [ ] 既有关键业务回归通过，设备矩阵结果与未覆盖项已记录。
- [ ] Madoka 无默认电话角色依赖；Homura／服务端协议无需变更。
- [ ] 升级与以更高版本号发布的回退经过验证，文档更新为实际实施结果。

## 8. 官方依据

- [Core-Telecom 接入指南](https://developer.android.com/develop/connectivity/telecom/voip-app/telecom)：CallsManager、自管理权限、保持／恢复、端点、通知窗口和系统回调期限。
- [CallsManager API](https://developer.android.com/reference/kotlin/androidx/core/telecom/CallsManager)：应用注册与通话生命周期契约。
- [Google Maven 版本元数据](https://dl.google.com/dl/android/maven2/androidx/core/core-telecom/maven-metadata.xml)：评估时最新稳定版 1.1.0，上一条稳定线 1.0.1。
- [1.0.1 官方源码制品](https://dl.google.com/dl/android/maven2/androidx/core/core-telecom/1.0.1/core-telecom-1.0.1-sources.jar)、[AAR](https://dl.google.com/dl/android/maven2/androidx/core/core-telecom/1.0.1/core-telecom-1.0.1.aar)、[POM](https://dl.google.com/dl/android/maven2/androidx/core/core-telecom/1.0.1/core-telecom-1.0.1.pom)：首次迁移采用的版本；核实 API、构建要求和合并 Manifest。
- [1.1.0 官方源码制品](https://dl.google.com/dl/android/maven2/androidx/core/core-telecom/1.1.0/core-telecom-1.1.0-sources.jar)、[AAR](https://dl.google.com/dl/android/maven2/androidx/core/core-telecom/1.1.0/core-telecom-1.1.0.aar)、[POM](https://dl.google.com/dl/android/maven2/androidx/core/core-telecom/1.1.0/core-telecom-1.1.0.pom)：核实 API、兼容路径和构建要求；后续升级目标。

## 9. 审阅修订记录（2026-11-13）

依据：对照基线代码，并阅读 1.0.1 与 1.1.0 的 AAR 元数据、POM、合并 Manifest 和源码。未编译，未上设备；下列"待验证"项以 P2 的证据为准。

| 修订 | 原计划 | 现计划 | 依据 |
| --- | --- | --- | --- |
| 实施顺序 | 先换库，保持在第三阶段 | 保持与统一媒体许可先在现有 ConnectionService 上交付，再换适配层 | 保持只需 HOLD 能力位和 `onHold`／`onUnhold`，库的兼容路径也是这样实现的 |
| 库版本 | 1.1.0，第一阶段同时升级工具链到 SDK 36.1 | 首次迁移用 1.0.1，工具链不动；1.1.0 作为独立后续 | 1.0.1 只要求 compileSdk 34、AGP 8.1.1；所用 API 在 1.0.1 均存在 |
| 保持能力声明 | "验证后才声明" | 适配层必须显式传能力位 | `callCapabilities` 默认值即 `SUPPORTS_SET_INACTIVE` |
| 协程持有 | 由通话服务生命周期持有 | 进程级持有；先等断开结果再取消 | `addCall` 被取消时不会断开平台通话 |
| 外呼地址 | 未规定 | 沿用不透明自定义地址；`tel:` 行为待验证 | 兼容路径直接以该地址调用 `placeCall` |
| 应用注册 | 未规定 | 无通话时注册一次 | 1.1.0 注册前先注销，文档禁止通话中调用 |
| 断开原因 | 未规定 | 集中映射到四种允许值 | 库文档；`ERROR` 仅 1.1.0 重映射 |
| 回退 | 装回旧 APK | 以更高版本号发布旧实现；旧适配器保留一个版本 | Android 不允许降级覆盖安装 |
| 蓝牙权限 | 评估后按需请求 | 库已合入声明；明确保留或移除 | 库的 Manifest |
| 验收 | 四档系统版本均要真机 | 兼容路径与 transactional 路径各一台真机；新增长时间保持、紧急号码、后台接听三行 | 26–33 同一路径；`suspendAudio` 原为短暂断网设计 |
