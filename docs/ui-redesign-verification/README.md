# Android 主机三标签 UI 验证

本报告由通过的 Android 35、7.6 英寸折叠屏 KVM UI smoke artifact 自动生成。截图是 artifact 中原始 PNG 字节的逐字节副本。

## 运行来源

- 仓库：`KiriKira/gsm2sip-client-android`
- 源提交：`995a448a9b2b890b23dd3d08245a16608e8517c6`
- GitHub Actions run：[42397415367](https://github.com/KiriKira/gsm2sip-client-android/actions/runs/42397415367)
- artifact ID：`13584192382`
- artifact digest：`sha256:59701aa36e6a77882e4a7be9f96cfe17e571dd3907d385ad0b0f56ebb3ac6fa5`
- APK：`app-debug.apk`，SHA-256 `a0f349009e3fa34ab444f49264c16ec890b14af763ed89cb874b31b1587f9c9b`，22397924 字节
- artifact 内未配对 smoke：`pass`（2026-11-07T17:48:45.684707+00:00）
- artifact 内配对 fixture：`pass`（2026-11-07T17:52:41.130791+00:00）
- 报告生成时间：`2026-11-07T17:52:59Z`

## 验证范围

未配对流程覆盖电话记录页、拨号键盘、短信列表、设置与备份入口，以及窗口旋转、折叠、展开和挖孔安全区。配对 fixture 使用离线合成的会话、两张 SIM、三条短信和通话记录；新建短信草稿经历 IME、旋转、折叠和展开。

四张出站准备、通话中、通话键盘和来电截图来自进程内合成 coordinator UI 状态；没有启动 SIP、Telecom、音频或蜂窝通话，未发送或接收短信。配对 fixture 结果要求 `fixture_active_call_ui=pass`。

本报告只验证 Android Emulator 上的界面流程，不代表 Z Fold8 真机、真实 SIM 短信收发、SIP 注册或双向语音已完成设备验收。

## 截图与文件校验

| 页面 | PNG | 尺寸 | 文件 SHA-256 |
|---|---|---:|---|
| 三标签导航 | [`images/host_navigation_initial.png`](images/host_navigation_initial.png) | 1768 × 2208 | `ba895b14c4217151f58c77b6effd9d9d7607f52fe2ce01704907d91066139e14` |
| 未配对电话记录页 | [`images/host_call_history.png`](images/host_call_history.png) | 1768 × 2208 | `fa32e6e1a903284056603c723d497455d1ca35ddc8c616c5bcb19d0f13b5f534` |
| 独立拨号键盘 | [`images/host_dialpad.png`](images/host_dialpad.png) | 1768 × 2208 | `c4d41d11c77cf9b45883d6f00b85217fcda0542dbc382374c3fa02f5a4f37b4d` |
| 短信会话列表 | [`images/host_sms_thread_list.png`](images/host_sms_thread_list.png) | 1768 × 2208 | `687f81bbcacb81a09da4024a1b98fcfe2643fd767524a89d5d39e54497a812bb` |
| 设置与配对表单 | [`images/host_settings_tab.png`](images/host_settings_tab.png) | 1768 × 2208 | `689c1b32f85a2f37410da4a7f34bf7d0b880a79c306ace665a0f12e959929896` |
| 设置中的短信备份入口 | [`images/host_settings_backup_entry.png`](images/host_settings_backup_entry.png) | 1768 × 2208 | `e302cf221008361e5bb0bcdd0776237d9c5e665414cda7d1b3deae0b35d6a4bc` |
| 主界面合拢窗口 | [`images/folded_ui.png`](images/folded_ui.png) | 1768 × 2208 | `e0ca5d28bde74f62fbb168524bef89d8e3c02e079099ab2b23abe0b8549d1261` |
| 主界面展开窗口 | [`images/unfolded_ui.png`](images/unfolded_ui.png) | 1768 × 2208 | `c9b9d30381da10e0ed382843835888c72070bc97f9c7f854d5adf676b94355df` |
| 显示挖孔时的主界面 | [`images/hole_cutout_enabled.png`](images/hole_cutout_enabled.png) | 1768 × 2208 | `0ba8c2619199912f632583521918a275cb0d18820f2eeac3d635c99900c0cd2c` |
| 滚动后可见的备份入口 | [`images/main-backup-entry-host.png`](images/main-backup-entry-host.png) | 1768 × 2208 | `fdd7ef4dcfef130e34e09bb22acf1117c9dd1b30734e9f3cb53cbc97b989a95c` |
| 短信备份与归档页面 | [`images/backup-overview.png`](images/backup-overview.png) | 1768 × 2208 | `96182a0fe8c616f45b59ee950f09ffd70dfe20aca9d75f879c61e4ffda96fe8a` |
| 备份页面横屏 | [`images/backup-rotation-landscape.png`](images/backup-rotation-landscape.png) | 2208 × 1768 | `f96ffe9bbd2caeb1db05fa0ba291474ae04f1cc45278e46b9726e7128c442937` |
| 备份页面竖屏 | [`images/backup-rotation-portrait.png`](images/backup-rotation-portrait.png) | 1768 × 2208 | `3a1e2db7428b893f1e70f8124cbaf9fe82081fa3419d740939b0c03ce5ea7db5` |
| 备份页面合拢 | [`images/backup-folded.png`](images/backup-folded.png) | 1768 × 2208 | `5e7fa6f162480a4a427ca01856d39d213df889f2352350dd8324732299111b38` |
| 备份页面展开 | [`images/backup-unfolded.png`](images/backup-unfolded.png) | 1768 × 2208 | `98f06e76fffc81c393a42533e6e80dd3b007d1611bee8a03721fa73946b69a75` |
| 挖孔安全区中的备份页面 | [`images/backup-cutout.png`](images/backup-cutout.png) | 1768 × 2208 | `98f06e76fffc81c393a42533e6e80dd3b007d1611bee8a03721fa73946b69a75` |
| 合成短信归档导入预览 | [`images/backup-import-preview.png`](images/backup-import-preview.png) | 1768 × 2208 | `3d11277b8b0058ea82ec01d44464ee081a1f0104b9513eb59202f77c1bde9d86` |
| 导入后的归档历史 | [`images/backup-imported-history.png`](images/backup-imported-history.png) | 1768 × 2208 | `4eda51e13d628a54f806ef75c96324ff2c3126a66ad72d7e56de6f10380ef8b9` |
| 配对状态下的三标签 | [`images/paired-fixture/paired_three_tabs.png`](images/paired-fixture/paired_three_tabs.png) | 1768 × 2208 | `eba10991c6df8adb3f872876046433ee7df665656869d87819aad9bd6e022455` |
| 双 SIM 合成通话记录 | [`images/paired-fixture/paired_call_history.png`](images/paired-fixture/paired_call_history.png) | 1768 × 2208 | `bc8d313a5578c8f1da3714720d9cb367990d3f777b071c5efd2d00c0f86e8bfb` |
| 从通话记录预填号码的拨号页 | [`images/paired-fixture/paired_dialpad_from_history.png`](images/paired-fixture/paired_dialpad_from_history.png) | 1768 × 2208 | `07b8869e60c14635edcf477ad8f728190ed79f47468a23e56a0a3304a1d3b85f` |
| 配对状态下的独立拨号键盘 | [`images/paired-fixture/paired_dialpad.png`](images/paired-fixture/paired_dialpad.png) | 1768 × 2208 | `07b8869e60c14635edcf477ad8f728190ed79f47468a23e56a0a3304a1d3b85f` |
| 三条合成短信会话 | [`images/paired-fixture/paired_sms_threads.png`](images/paired-fixture/paired_sms_threads.png) | 1768 × 2208 | `8066207774c864c499f0853079a729d79ac70826bdd88a78fdccb719e7cca7ee` |
| 合拢窗口中的短信会话 | [`images/paired-fixture/paired_sms_threads_folded.png`](images/paired-fixture/paired_sms_threads_folded.png) | 1768 × 2208 | `43b5532d69dcab332ab9b5a9d0bc71d95c57bf708357fd8fb0fb6fe764022bdf` |
| 展开窗口中的短信会话 | [`images/paired-fixture/paired_sms_threads_unfolded.png`](images/paired-fixture/paired_sms_threads_unfolded.png) | 1768 × 2208 | `0f94b0285eb1711ef73179ec697c3d65ce88ff50c3809c4edd89a960317f90b7` |
| 既有短信对话与回复框 | [`images/paired-fixture/paired_sms_conversation.png`](images/paired-fixture/paired_sms_conversation.png) | 1768 × 2208 | `7910f9ad55bc74dc1f9107e4fe738aa162fd6daa1b5c7988299eaa8cb54bfe64` |
| 新短信编辑页 | [`images/paired-fixture/paired_new_message.png`](images/paired-fixture/paired_new_message.png) | 1768 × 2208 | `3d7e7cd8c1300afa11c56f18debcc6e84f535c57019cb6c654bf1162700a78cc` |
| 选择 SIM 2 的新短信页 | [`images/paired-fixture/paired_new_message_sim_b.png`](images/paired-fixture/paired_new_message_sim_b.png) | 1768 × 2208 | `92fd5866b4f8670fb1a58293589d974248c17dd952625116b3cde815cbda1efb` |
| 横屏且键盘显示时的短信草稿 | [`images/paired-fixture/paired_new_message_landscape.png`](images/paired-fixture/paired_new_message_landscape.png) | 2208 × 1768 | `f1d387fe3c968175153ecadbf950ddc100fed8d2fa4a56d7775412207cb0eaa9` |
| 合拢窗口中的短信草稿 | [`images/paired-fixture/paired_new_message_folded.png`](images/paired-fixture/paired_new_message_folded.png) | 1768 × 2208 | `23cf76740105e59651953d57209b4b82ad47d27231880e10ba4392ee46154c53` |
| 展开窗口中的短信草稿 | [`images/paired-fixture/paired_new_message_unfolded.png`](images/paired-fixture/paired_new_message_unfolded.png) | 1768 × 2208 | `325db5e28015fc9aa7f98609ce689b6aefc6a78a21f8b9c0aa398041987d75f1` |
| 配对状态下的设置 | [`images/paired-fixture/paired_settings.png`](images/paired-fixture/paired_settings.png) | 1768 × 2208 | `55db28e201ec8f6ffc2face32d647c69d2dc5ea65ed4d2954b8ea042f33fb5f4` |
| 配对设置中的短信备份入口 | [`images/paired-fixture/paired_settings_backup.png`](images/paired-fixture/paired_settings_backup.png) | 1768 × 2208 | `2a39da862b53bb7db7d54028ffd2762ee6ad0afd1e2c0d4e577b0186599984a5` |
| 合成出站呼叫准备界面 | [`images/paired-fixture/call-synthetic-dialing.png`](images/paired-fixture/call-synthetic-dialing.png) | 1768 × 2208 | `527a6b7bc0245767e343dc29b76988f1923930f673491c6bb7a36c50f08ae6e2` |
| 合成通话中界面 | [`images/paired-fixture/call-synthetic-active.png`](images/paired-fixture/call-synthetic-active.png) | 1768 × 2208 | `92948878b0ec31b442e5716bad8f064b2d8b7010c732e67672f9935b32e2f9cc` |
| 合成通话键盘界面 | [`images/paired-fixture/call-synthetic-keypad.png`](images/paired-fixture/call-synthetic-keypad.png) | 1768 × 2208 | `120972aab898199c9f29d3444ae53ad01e2d815ee6d3823274e6838189e35abf` |
| 合成来电界面 | [`images/paired-fixture/call-synthetic-incoming.png`](images/paired-fixture/call-synthetic-incoming.png) | 1768 × 2208 | `dfd01f9ed5e9bcc88b8c56c14b45e555923eef9d683a5443ceabcc86526bf078` |

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
| `results.json` | `699a9a3c4d17f146bda47f67cb5f53fa8de15ab27d045cb9d4d19032b031f22d` |
| `screenshot_manifest.json` | `3b6aaebb62edda3b3585042dbe5c9f928a24d4af687bea2383d926cdaf7148c9` |
| `paired-fixture/results.json` | `59beddfea97b4a9c8fee6f168bb56d733cbefaa945ba0a42db78e05be08cd45b` |
| `paired-fixture/fixture_manifest.json` | `c2f5c5086a79f2218186728283b18f80e842544289e90835b10e36b78a4eb749` |

初始评审材料保留在 `images-initial/`、`initial-evidence.json`、`initial-ui-results.json` 和 `local-checks.json`；它们与本次通过的 KVM 截图分开保存。机器可读运行信息见 [`verified-run.json`](verified-run.json)。
