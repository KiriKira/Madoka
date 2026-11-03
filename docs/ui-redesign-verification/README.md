# Android 主机三标签 UI 验证

本报告由通过的 Android 35、7.6 英寸折叠屏 KVM UI smoke artifact 自动生成。截图是 artifact 中原始 PNG 字节的逐字节副本。

## 运行来源

- 仓库：`KiriKira/gsm2sip-client-android`
- 源提交：`c3c4a4c255760ea69dddf3175b64ea427dd7e59c`
- GitHub Actions run：[41681517803](https://github.com/KiriKira/gsm2sip-client-android/actions/runs/41681517803)
- artifact ID：`13254766031`
- artifact digest：`sha256:638c0fd9c6f658182d96d0acaca5ae2430a0f07e52420980cd7a4f72ff49293c`
- APK：`app-debug.apk`，SHA-256 `0ac75e9c73e233d37e3dd30a0f939c5073832c21aefb00ede44d71d3f6a898be`，22397924 字节
- artifact 内未配对 smoke：`pass`（2026-11-03T17:18:34.496130+00:00）
- artifact 内配对 fixture：`pass`（2026-11-03T17:22:50.072528+00:00）
- 报告生成时间：`2026-11-03T17:23:08Z`

## 验证范围

未配对流程覆盖电话记录页、拨号键盘、短信列表、设置与备份入口，以及窗口旋转、折叠、展开和挖孔安全区。配对 fixture 使用离线合成的会话、两张 SIM、三条短信和通话记录；新建短信草稿经历 IME、旋转、折叠和展开。

四张出站准备、通话中、通话键盘和来电截图来自进程内合成 coordinator UI 状态；没有启动 SIP、Telecom、音频或蜂窝通话，未发送或接收短信。配对 fixture 结果要求 `fixture_active_call_ui=pass`。

本报告只验证 Android Emulator 上的界面流程，不代表 Z Fold8 真机、真实 SIM 短信收发、SIP 注册或双向语音已完成设备验收。

## 截图与文件校验

| 页面 | PNG | 尺寸 | 文件 SHA-256 |
|---|---|---:|---|
| 三标签导航 | [`images/host_navigation_initial.png`](images/host_navigation_initial.png) | 1768 × 2208 | `0c38f9e772ad462d450db4c6a7d57c792b8a1533f97468e154aa8949fc8a65dc` |
| 未配对电话记录页 | [`images/host_call_history.png`](images/host_call_history.png) | 1768 × 2208 | `2ccbf279e186316d467a413844c704fd2205111167b58e7249cd6bc32d51e7b2` |
| 独立拨号键盘 | [`images/host_dialpad.png`](images/host_dialpad.png) | 1768 × 2208 | `667169ff53be6dd1db28255322df7a14b2277690e281844505a76cbaef278397` |
| 短信会话列表 | [`images/host_sms_thread_list.png`](images/host_sms_thread_list.png) | 1768 × 2208 | `839bf958199e69057d6b1af41d9a0f0f346c43bb3dfdb677214337f77c27e952` |
| 设置与配对表单 | [`images/host_settings_tab.png`](images/host_settings_tab.png) | 1768 × 2208 | `c3ca446150904f9269bea8993b815f55cdbc263bc36cc7a8be7bca6f8fe6bcac` |
| 设置中的短信备份入口 | [`images/host_settings_backup_entry.png`](images/host_settings_backup_entry.png) | 1768 × 2208 | `457ae255737bfbb4e131d5606923aa69959addb88c50d2fe4ff92c75e80b1fbb` |
| 主界面合拢窗口 | [`images/folded_ui.png`](images/folded_ui.png) | 1768 × 2208 | `89a8eafc0197828ed16f21cf366f81798508aca91c63b0bd2b1a078867e94f16` |
| 主界面展开窗口 | [`images/unfolded_ui.png`](images/unfolded_ui.png) | 1768 × 2208 | `1cb09b058eb5e284d1ff8b83eedc85223bf10e89b12cbdfbb8fd6dfa3dcc0a7b` |
| 显示挖孔时的主界面 | [`images/hole_cutout_enabled.png`](images/hole_cutout_enabled.png) | 1768 × 2208 | `4c309e00acb9a28e435a388dfb05410891d04219939a9f34bb976561f8245d8f` |
| 滚动后可见的备份入口 | [`images/main-backup-entry-host.png`](images/main-backup-entry-host.png) | 1768 × 2208 | `1f7a4b394f71aa0563d5b85fc8c72e1788409e4d69d6c2dfbfaff20fb95f2d65` |
| 短信备份与归档页面 | [`images/backup-overview.png`](images/backup-overview.png) | 1768 × 2208 | `399a4e6f37c642fadd75f259bd4a2fa94ca72646983b1f2486f56c93705da9b7` |
| 备份页面横屏 | [`images/backup-rotation-landscape.png`](images/backup-rotation-landscape.png) | 2208 × 1768 | `cf33a54c2883fdd748a63cac1c901c61172049da432c9fb774175deb5d8e3129` |
| 备份页面竖屏 | [`images/backup-rotation-portrait.png`](images/backup-rotation-portrait.png) | 1768 × 2208 | `f67e6ff8394ee515b720639466ad65b3186e2e07170a0ad126fc27158d9572dd` |
| 备份页面合拢 | [`images/backup-folded.png`](images/backup-folded.png) | 1768 × 2208 | `fe9323e8298c6c858257281ba04f96f6a51429cc9e2fc4fecf5934d174cb701b` |
| 备份页面展开 | [`images/backup-unfolded.png`](images/backup-unfolded.png) | 1768 × 2208 | `5d829258523ba6950965b3c0efbd01180d4792a0486ffb4e6af5268070478763` |
| 挖孔安全区中的备份页面 | [`images/backup-cutout.png`](images/backup-cutout.png) | 1768 × 2208 | `5d829258523ba6950965b3c0efbd01180d4792a0486ffb4e6af5268070478763` |
| 合成短信归档导入预览 | [`images/backup-import-preview.png`](images/backup-import-preview.png) | 1768 × 2208 | `cc3f694b0673c6b3c3371ee8c0c113baf43e22d8eec32d0a4f60bcfa858ccf59` |
| 导入后的归档历史 | [`images/backup-imported-history.png`](images/backup-imported-history.png) | 1768 × 2208 | `604dcbf0ed6c9e8e4737780613af88b6628782e148954ef0c0b7214908b524eb` |
| 配对状态下的三标签 | [`images/paired-fixture/paired_three_tabs.png`](images/paired-fixture/paired_three_tabs.png) | 1768 × 2208 | `81577af6184388c03d30632eb77b6e9cda4d7f7e900303a6ec21a03551467de5` |
| 双 SIM 合成通话记录 | [`images/paired-fixture/paired_call_history.png`](images/paired-fixture/paired_call_history.png) | 1768 × 2208 | `78b4164b2bc7be2a9ef6bdf87796464975da3aabbb22370a91e930ea5374747a` |
| 从通话记录预填号码的拨号页 | [`images/paired-fixture/paired_dialpad_from_history.png`](images/paired-fixture/paired_dialpad_from_history.png) | 1768 × 2208 | `2eda64df71a8bf0031bfe9b77063e3500bc1a99f44904d36e4d14c617461550c` |
| 配对状态下的独立拨号键盘 | [`images/paired-fixture/paired_dialpad.png`](images/paired-fixture/paired_dialpad.png) | 1768 × 2208 | `2eda64df71a8bf0031bfe9b77063e3500bc1a99f44904d36e4d14c617461550c` |
| 三条合成短信会话 | [`images/paired-fixture/paired_sms_threads.png`](images/paired-fixture/paired_sms_threads.png) | 1768 × 2208 | `b6e09d1b4a872308782238fe5826b294a87f2f6c0e6437c1080462108daf701c` |
| 合拢窗口中的短信会话 | [`images/paired-fixture/paired_sms_threads_folded.png`](images/paired-fixture/paired_sms_threads_folded.png) | 1768 × 2208 | `bd1e039c4fbf2a9947513325b9fb5301f185e177337b54ef7475619a86f1e077` |
| 展开窗口中的短信会话 | [`images/paired-fixture/paired_sms_threads_unfolded.png`](images/paired-fixture/paired_sms_threads_unfolded.png) | 1768 × 2208 | `de8e9c9273bd8621b3311bc3b2a2ee517eb0201bf5ea2477f0217dcc4c12bc2d` |
| 既有短信对话与回复框 | [`images/paired-fixture/paired_sms_conversation.png`](images/paired-fixture/paired_sms_conversation.png) | 1768 × 2208 | `c24dad7bb865e1a640eefd8a0c696223086d063051c322ca7cbd2339be851bf3` |
| 新短信编辑页 | [`images/paired-fixture/paired_new_message.png`](images/paired-fixture/paired_new_message.png) | 1768 × 2208 | `8acb2385f455cb9817d4c42eee469afaad07bec5ebefef19271b8d73ca069070` |
| 选择 SIM 2 的新短信页 | [`images/paired-fixture/paired_new_message_sim_b.png`](images/paired-fixture/paired_new_message_sim_b.png) | 1768 × 2208 | `cd0a7d322d926ce05f266937361c18d5fbd8c0abc1958fa68985ec36b6ec5e9d` |
| 横屏且键盘显示时的短信草稿 | [`images/paired-fixture/paired_new_message_landscape.png`](images/paired-fixture/paired_new_message_landscape.png) | 2208 × 1768 | `fd6ad835bcf6244a7665f76305f7fcc415baf62030d410d92859c896cf29aa50` |
| 合拢窗口中的短信草稿 | [`images/paired-fixture/paired_new_message_folded.png`](images/paired-fixture/paired_new_message_folded.png) | 1768 × 2208 | `eea86ab0e87e13ca91212f435cf8c3d4fb56f52c95f3ab03b8ebb2790c300b98` |
| 展开窗口中的短信草稿 | [`images/paired-fixture/paired_new_message_unfolded.png`](images/paired-fixture/paired_new_message_unfolded.png) | 1768 × 2208 | `76536f821fb7ff5906e88aff437520f7cbcf5f5ca8a4630d618f307993d335e3` |
| 配对状态下的设置 | [`images/paired-fixture/paired_settings.png`](images/paired-fixture/paired_settings.png) | 1768 × 2208 | `574a2452bfdaffe3dfa1aa3cb5024da737469d22de367d231b2bdb890fd0eeef` |
| 配对设置中的短信备份入口 | [`images/paired-fixture/paired_settings_backup.png`](images/paired-fixture/paired_settings_backup.png) | 1768 × 2208 | `e08ffaaf1902a0a146bb6cebe461d9c3fe16784c9d187725e32893e98a671dd9` |
| 合成出站呼叫准备界面 | [`images/paired-fixture/call-synthetic-dialing.png`](images/paired-fixture/call-synthetic-dialing.png) | 1768 × 2208 | `20a18a1b9f700115c6b17a2129968495b45e604e2e357f74efcd793ef2b3cd47` |
| 合成通话中界面 | [`images/paired-fixture/call-synthetic-active.png`](images/paired-fixture/call-synthetic-active.png) | 1768 × 2208 | `bad465dcc4bf85656e8779d22dfc7ac1e29185c4b7a22d5c857ecec5320f78a6` |
| 合成通话键盘界面 | [`images/paired-fixture/call-synthetic-keypad.png`](images/paired-fixture/call-synthetic-keypad.png) | 1768 × 2208 | `bec58886ca7e4c4a672af9aabeeede1d4e8034d81a6adfe37c0d641d736a64df` |
| 合成来电界面 | [`images/paired-fixture/call-synthetic-incoming.png`](images/paired-fixture/call-synthetic-incoming.png) | 1768 × 2208 | `ee91ddae35b8bd41054394b71cb4e0189caa3957aa39ae17682777edac4c347a` |

## 截图

### 未配对界面

#### 三标签导航

![三标签导航](images/host_navigation_initial.png)

### 电话与拨号

#### 未配对电话记录页

![未配对电话记录页](images/host_call_history.png)

#### 独立拨号键盘

![独立拨号键盘](images/host_dialpad.png)

### 短信

#### 短信会话列表

![短信会话列表](images/host_sms_thread_list.png)

### 设置

#### 设置与配对表单

![设置与配对表单](images/host_settings_tab.png)

#### 设置中的短信备份入口

![设置中的短信备份入口](images/host_settings_backup_entry.png)

### 窗口适配

#### 主界面合拢窗口

![主界面合拢窗口](images/folded_ui.png)

#### 主界面展开窗口

![主界面展开窗口](images/unfolded_ui.png)

#### 显示挖孔时的主界面

![显示挖孔时的主界面](images/hole_cutout_enabled.png)

### 短信备份

#### 滚动后可见的备份入口

![滚动后可见的备份入口](images/main-backup-entry-host.png)

#### 短信备份与归档页面

![短信备份与归档页面](images/backup-overview.png)

#### 备份页面横屏

![备份页面横屏](images/backup-rotation-landscape.png)

#### 备份页面竖屏

![备份页面竖屏](images/backup-rotation-portrait.png)

#### 备份页面合拢

![备份页面合拢](images/backup-folded.png)

#### 备份页面展开

![备份页面展开](images/backup-unfolded.png)

#### 挖孔安全区中的备份页面

![挖孔安全区中的备份页面](images/backup-cutout.png)

#### 合成短信归档导入预览

![合成短信归档导入预览](images/backup-import-preview.png)

#### 导入后的归档历史

![导入后的归档历史](images/backup-imported-history.png)

### 配对数据界面

#### 配对状态下的三标签

![配对状态下的三标签](images/paired-fixture/paired_three_tabs.png)

#### 双 SIM 合成通话记录

![双 SIM 合成通话记录](images/paired-fixture/paired_call_history.png)

#### 从通话记录预填号码的拨号页

![从通话记录预填号码的拨号页](images/paired-fixture/paired_dialpad_from_history.png)

#### 配对状态下的独立拨号键盘

![配对状态下的独立拨号键盘](images/paired-fixture/paired_dialpad.png)

#### 三条合成短信会话

![三条合成短信会话](images/paired-fixture/paired_sms_threads.png)

#### 合拢窗口中的短信会话

![合拢窗口中的短信会话](images/paired-fixture/paired_sms_threads_folded.png)

#### 展开窗口中的短信会话

![展开窗口中的短信会话](images/paired-fixture/paired_sms_threads_unfolded.png)

#### 既有短信对话与回复框

![既有短信对话与回复框](images/paired-fixture/paired_sms_conversation.png)

#### 新短信编辑页

![新短信编辑页](images/paired-fixture/paired_new_message.png)

#### 选择 SIM 2 的新短信页

![选择 SIM 2 的新短信页](images/paired-fixture/paired_new_message_sim_b.png)

#### 横屏且键盘显示时的短信草稿

![横屏且键盘显示时的短信草稿](images/paired-fixture/paired_new_message_landscape.png)

#### 合拢窗口中的短信草稿

![合拢窗口中的短信草稿](images/paired-fixture/paired_new_message_folded.png)

#### 展开窗口中的短信草稿

![展开窗口中的短信草稿](images/paired-fixture/paired_new_message_unfolded.png)

#### 配对状态下的设置

![配对状态下的设置](images/paired-fixture/paired_settings.png)

#### 配对设置中的短信备份入口

![配对设置中的短信备份入口](images/paired-fixture/paired_settings_backup.png)

### 合成通话界面

#### 合成出站呼叫准备界面

![合成出站呼叫准备界面](images/paired-fixture/call-synthetic-dialing.png)

#### 合成通话中界面

![合成通话中界面](images/paired-fixture/call-synthetic-active.png)

#### 合成通话键盘界面

![合成通话键盘界面](images/paired-fixture/call-synthetic-keypad.png)

#### 合成来电界面

![合成来电界面](images/paired-fixture/call-synthetic-incoming.png)

## 结果文件校验

| artifact 文件 | SHA-256 |
|---|---|
| `results.json` | `a2dd44deec71f79675b0173b65bd98522a3ab8d90de972dfa0db55c220729bb9` |
| `screenshot_manifest.json` | `9451c00810e127e2da24d6e70037e523e1cd6134c391eb14d3a9c188ac4ed5eb` |
| `paired-fixture/results.json` | `439df76edc65893cb6290efe1653069e7dbf4a5cd98041a0bb44e237f22cf312` |
| `paired-fixture/fixture_manifest.json` | `4b71131eff3c739111ed421b4c49043482529b565309eef02d30014e740da00b` |

初始评审材料保留在 `images-initial/`、`initial-evidence.json`、`initial-ui-results.json` 和 `local-checks.json`；它们与本次通过的 KVM 截图分开保存。机器可读运行信息见 [`verified-run.json`](verified-run.json)。
