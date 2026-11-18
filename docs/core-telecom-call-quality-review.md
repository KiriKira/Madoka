# 合并 main 后的通话质量审阅

2026-11-18。审阅基线为 Madoka 合并提交 `b084b4f`（main `1ac32f9`）和 [Homura main `0866675`](https://github.com/KiriKira/Homura/tree/0866675c4127852c65aeeb55aa5664b4c288a54c)。以下结论来自源码。用户报告的双讲断续尚缺对应设备、音频路由和同一通话的两端日志。

## 双方同时讲话时，Madoka 听到的蜂窝端声音断续

优先检查 Homura 使用的设备配置和实际录音源。Madoka 用户讲话时，Homura 把这路音频注入蜂窝上行。对标记为播放会泄漏到采集的机型，Homura 逐个 20 ms 帧比较蜂窝采集音量与估算的回声。低于双讲门限时它向 Madoka 发送静音。较轻的辅音和词尾可能被交替截掉，RTP 仍然正常到达。msm8930 和 sm6150 的内置配置已经关闭此门控，需按实际使用的配置判断。[Homura 判断及计数](https://github.com/KiriKira/Homura/blob/0866675c4127852c65aeeb55aa5664b4c288a54c/app/src/main/java/com/callagent/gateway/audio/CellularAudioSession.kt#L970)

用户在内置音乐测试来电中暂未听到断续，之前报告的现象发生在真实通话的双方同时讲话时。[测试来电送帧处](https://github.com/KiriKira/Homura/blob/0866675c4127852c65aeeb55aa5664b4c288a54c/app/src/main/java/com/callagent/gateway/rtc/TestCallRunner.kt#L243)直接把解码后的音乐帧送入 PCM 桥。音乐测试经过 WebRTC 和 Madoka 播放链路，但绕过 Homura 的蜂窝音频采集，因此优先查真实 SIM 通话的采集源、逐帧回声判断和 PCM 送帧。实际根因仍需对齐同一次断续前后的两端日志。对比这通电话中单讲和双讲片段的计数增量：

- Homura 的原始采集电平仍有语音，双讲时 `gates:echo` 增量陡升，Madoka 的 `concealed_pct` 和丢包保持低位，优先处理网关误判。验证纯下行采集的机型可绕过门控；有真实回灌的机型应单独评估带迟滞的双讲检测和渐变衰减，同时检查回声是否变响。
- 若 Madoka 的 `rx_loss_pct`、`concealed_pct` 或缓冲延迟同步上升，比较 UDP 直连与实际选中的 TURN 路径。若网络指标稳定而 Homura 的 `pcm_underruns` 持续增加，检查旧机 PCM 送帧时序。若仅免提复现，再检查 Madoka 设备音效与路由。采集计数和电平即可，无需记录通话内容。[现有质量日志](relay-source-foundation.md)

## 接听到第一声的延迟

前台麦克风服务就绪后，业务接听先 GET 当前 revision、再 POST accept，成功后顺序执行媒体准入、中继准备、native/offer 初始化和信令交换。响铃期已有中继预取。接听阶段的 resolve 仍可能在冷缓存下为刷新和探测花掉共享的 1.5 秒预算。这个预算不是每通电话的实测开销。[接听入口](../app/src/main/java/com/callagent/host/calls/RtcCallEngine.kt) · [操作与重试](../rtcmedia/src/main/java/com/callagent/rtcmedia/RtcCallControl.kt) · [中继选择](../rtcmedia/src/main/java/com/callagent/rtcmedia/RelayRepository.kt)

先用单调时钟按通话关联点击、平台接听完成、前台就绪、accept 成功、准入、offer 发出、首个有效接收音频帧。按冷／热缓存和网络分组比较 P50、P95，并分别记录通话接通率。现有 native 和 relay 日志可复用。若控制往返占主导，首次 accept 可带最近一次已授权快照的 revision，遇版本冲突再读取，保留现有幂等与多端抢接语义。若中继等待占主导，已有满足策略且凭据有效的来源时优先复用本网络探测结果，保留仅中继网络和首次使用的覆盖。响铃时提前选媒体会消耗服务端协商窗口，预热限定在不占用媒体腿、不打开麦克风的工作。

## 弱网与切网

服务层给网络丢失 30 秒宽限。媒体层另有已连接后断连 10 秒、信令读取失败约 10 秒的退出路径，ICE FAILED 可直接终止。当前只接受 generation 1，首次连通后封存本代本地候选。Wi-Fi 切蜂窝时，服务还在等待，媒体腿可能已结束。[媒体轮询](../rtcmedia/src/main/java/com/callagent/rtcmedia/RtcAudioSession.kt) · [ICE 回调](../rtcmedia/src/main/java/com/callagent/rtcmedia/NativePeer.kt)

先对 Wi-Fi/蜂窝互切、短时丢包和仅信令不可达分别测量首个丢音到恢复的时长、授权有效期和结束原因。将明确撤权、远端挂断和可恢复的传输错误分开处理，在有效授权窗口内统一恢复期限。需要更换地址才能恢复时，两端和信令协议必须共同支持按代隔离的 ICE restart。旧代候选不可复用。调整抖动缓冲和 Opus 参数前，先看实际路径上的隐藏采样占比与缓冲延迟，以免改善丢字却增加交谈延迟。

审阅弱网恢复时核对了现有的省电修复。已注册网络监听且离线空闲时，RtcCallEngine 会释放持续唤醒锁并跳过请求。兼容模式的空闲轮询在连续失败时退避至最长 60 秒；Reception v2 的空闲兜底是 15 分钟的非唤醒定时器。WebSocket 短暂连上又掉线会沿用带抖动的退避，连接稳定至少 60 秒后才在重连路径重置计数。弱网通话的恢复可以由当前媒体会话合并事件并在结束时取消任务，沿用这些待机行为。以上是源码审阅结果，实际耗电未在本次测量。[RTC 调度](../app/src/main/java/com/callagent/host/calls/RtcCallEngine.kt#L157) · [重连退避](../app/src/main/java/com/callagent/host/background/HostBackgroundService.kt#L629)

继续沿网络回调追踪到轮询任务后，有两处会直接影响恢复设计：

- 网络可用、丢失或重新通过验证会触发 systemWake，先申请短时工作锁再唤醒轮询。正在执行的 poll 最多合并一次紧随其后的补查；若每次 poll 期间都有新事件，补查仍可连续发生。恢复中的定时重试若也反复调用这个入口，就可能跳过原本的退避。[网络回调](../app/src/main/java/com/callagent/host/calls/RtcCallEngine.kt#L36) · [补查调度](../app/src/main/java/com/callagent/host/calls/CoalescedCallPoll.kt#L37)
- hasLiveCall 同时看媒体会话和已上报的非终态。残留任意一项都会选中活跃通话分支，继续使用 750 ms 控制轮询和通话唤醒锁。恢复任务应跟随原会话的终态清理。另有一条独立路径：启用 releaseDuringFailure 且连续失败至少三次、已经空闲时，代码会安排可唤醒设备的恢复闹钟。v2 的 15 分钟间隔仅描述常规空闲兜底。[活跃判断与持锁](../app/src/main/java/com/callagent/host/calls/RtcCallEngine.kt#L35) · [失败分支](../app/src/main/java/com/callagent/host/calls/RtcCallEngine.kt#L258) · [恢复闹钟](../app/src/main/java/com/callagent/host/calls/RtcFailureRecovery.kt#L31)

## 合并后的媒体许可

还有一个与上次两项 P1 独立的并发窗口。RtcAudioSession 发布 native peer 后，在工作线程读取 desiredAudio／desiredMute，再分别调用原生方法。若线程读到允许后暂停，此时保持／静音已在另一线程写入并应用，工作线程恢复时可用旧值覆盖新值。CloseableSlot 保护安装和关闭，无法保证状态应用顺序。应串行化发布、初始状态回放和后续更新，并用可控线程交错覆盖“初始化遇到保持／静音”。[初始化](../rtcmedia/src/main/java/com/callagent/rtcmedia/RtcAudioSession.kt#L119) · [运行时开关](../rtcmedia/src/main/java/com/callagent/rtcmedia/RtcAudioSession.kt#L262)

进入 Core-Telecom 设备验证前，连同此前评审的静音订阅失效、断开超时误报完成一起处理。音质与延迟改动分别对照基线验收，避免多个变量同时改变。
