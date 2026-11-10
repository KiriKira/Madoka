# Android 主机三标签 UI 验证

本报告由通过的 Android 35、7.6 英寸折叠屏 KVM UI smoke artifact 自动生成。截图是 artifact 中原始 PNG 字节的逐字节副本。

## 运行来源

- 仓库：`KiriKira/Madoka`
- 源提交：`ed2459c818def3ee32863aa289dcc89169822fc0`
- GitHub Actions run：[42827669911](https://github.com/KiriKira/Madoka/actions/runs/42827669911)
- artifact ID：`13771525435`
- artifact digest：`sha256:d5c17f5ef3bba08ff7b155f22522129c13c3269d09ba608f838109cbb40a5065`
- APK：`app-debug.apk`，SHA-256 `3aace54237c0df0ef1318a3e4fbfe29f7e917c0e1bdd60fa4c51dd20e6316aa7`，22414356 字节
- artifact 内未配对 smoke：`pass`（2026-11-10T13:54:51.693062+00:00）
- artifact 内配对 fixture：`pass`（2026-11-10T13:58:37.611692+00:00）
- 报告生成时间：`2026-11-10T13:58:51Z`

## 验证范围

未配对流程覆盖电话记录页、拨号键盘、短信列表、设置与备份入口，以及窗口旋转、折叠、展开和挖孔安全区。配对 fixture 使用离线合成的会话、两张 SIM、三条短信和通话记录；新建短信草稿经历 IME、旋转、折叠和展开。

四张出站准备、通话中、通话键盘和来电截图来自进程内合成 coordinator UI 状态；没有启动 SIP、Telecom、音频或蜂窝通话，未发送或接收短信。配对 fixture 结果要求 `fixture_active_call_ui=pass`。

本报告只验证 Android Emulator 上的界面流程，不代表 Z Fold8 真机、真实 SIM 短信收发、SIP 注册或双向语音已完成设备验收。

## 截图与文件校验

| 页面 | PNG | 尺寸 | 文件 SHA-256 |
|---|---|---:|---|
| 三标签导航 | [`images/host_navigation_initial.png`](images/host_navigation_initial.png) | 1768 × 2208 | `d6969ce2de57bb7c3e1665941a8e11fb9d1cace35d9677f1adc34322bd18ec05` |
| 未配对电话记录页 | [`images/host_call_history.png`](images/host_call_history.png) | 1768 × 2208 | `0cfa9726eb3d6e293dac975161d15ff52b657af063503a28f6833d763a49456d` |
| 独立拨号键盘 | [`images/host_dialpad.png`](images/host_dialpad.png) | 1768 × 2208 | `a61d904d33761460b62aedf247855f92c6d665206901a7fdd93366e9cb4fef21` |
| 短信会话列表 | [`images/host_sms_thread_list.png`](images/host_sms_thread_list.png) | 1768 × 2208 | `5c4b94639a0dfee7989889b3ec1efb402be52ca47e1e88db88ccb9337149bfd7` |
| 设置与配对表单 | [`images/host_settings_tab.png`](images/host_settings_tab.png) | 1768 × 2208 | `dfcbcca409815ee22b3d653ebbd72ea58d5712e976bf3aa75379e6bd23d48fd5` |
| 设置中的短信备份入口 | [`images/host_settings_backup_entry.png`](images/host_settings_backup_entry.png) | 1768 × 2208 | `54f797247af2d4d361b83f5a0612597e05641428a0b8824546e10f25a413ca31` |
| 主界面合拢窗口 | [`images/folded_ui.png`](images/folded_ui.png) | 1768 × 2208 | `a1643a26f3f2204ee0cc1e0ced7f9320a957179cfebd9bdcc818d04f7ebd428a` |
| 主界面展开窗口 | [`images/unfolded_ui.png`](images/unfolded_ui.png) | 1768 × 2208 | `2b7958e1b4f3b5197f483ec517c4de3d003964cd919ac00fbcb74ad8c9afe0ab` |
| 显示挖孔时的主界面 | [`images/hole_cutout_enabled.png`](images/hole_cutout_enabled.png) | 1768 × 2208 | `b188d30aebb402b7478cb79742da0e3e07e95d41e20b0d3169d88a55f301afe3` |
| 滚动后可见的备份入口 | [`images/main-backup-entry-host.png`](images/main-backup-entry-host.png) | 1768 × 2208 | `ad9ebbd861e87f84beec25be2d0a8edf75ea78a20a6c6e7ad8c23d6cc6390fc9` |
| 短信备份与归档页面 | [`images/backup-overview.png`](images/backup-overview.png) | 1768 × 2208 | `685d29a313877d8f6d00c2605b655bbc052f580a13faf42e6c90e100e69cf886` |
| 备份页面横屏 | [`images/backup-rotation-landscape.png`](images/backup-rotation-landscape.png) | 2208 × 1768 | `c0151799a98bc423dd08957a62682600599a4c45f680c30f9b5978b42796a151` |
| 备份页面竖屏 | [`images/backup-rotation-portrait.png`](images/backup-rotation-portrait.png) | 1768 × 2208 | `5e710ab1c751ee150586844aa0490ed4a2c7db894fa35e514239cabb629cd241` |
| 备份页面合拢 | [`images/backup-folded.png`](images/backup-folded.png) | 1768 × 2208 | `7c94ec2fd57c44452c4ad4827b404be6b1d4e110e32c68fe10587bbbdca17d5f` |
| 备份页面展开 | [`images/backup-unfolded.png`](images/backup-unfolded.png) | 1768 × 2208 | `5e710ab1c751ee150586844aa0490ed4a2c7db894fa35e514239cabb629cd241` |
| 挖孔安全区中的备份页面 | [`images/backup-cutout.png`](images/backup-cutout.png) | 1768 × 2208 | `5e710ab1c751ee150586844aa0490ed4a2c7db894fa35e514239cabb629cd241` |
| 合成短信归档导入预览 | [`images/backup-import-preview.png`](images/backup-import-preview.png) | 1768 × 2208 | `7e6612893e11929bf00705bf2492300eebbcb0305234808a6b98c94b037a5157` |
| 导入后的归档历史 | [`images/backup-imported-history.png`](images/backup-imported-history.png) | 1768 × 2208 | `2195fda419c3fa2825752a22e37dada03cb8522759152609732b43036076de57` |
| 配对状态下的三标签 | [`images/paired-fixture/paired_three_tabs.png`](images/paired-fixture/paired_three_tabs.png) | 1768 × 2208 | `886f98cf86b42b0a21219f338fec7293e8a8717475f1228abb346dfeece6a43a` |
| 双 SIM 合成通话记录 | [`images/paired-fixture/paired_call_history.png`](images/paired-fixture/paired_call_history.png) | 1768 × 2208 | `6e50e2531bde66b79ad443fa61d4d97b15954ae86001a77194e765d3b64a42e9` |
| 从通话记录预填号码的拨号页 | [`images/paired-fixture/paired_dialpad_from_history.png`](images/paired-fixture/paired_dialpad_from_history.png) | 1768 × 2208 | `dd171aaea90252ff2e8a3014ebf8df1308a8a40fb47a11e42fd87992728ba1b0` |
| 配对状态下的独立拨号键盘 | [`images/paired-fixture/paired_dialpad.png`](images/paired-fixture/paired_dialpad.png) | 1768 × 2208 | `dd171aaea90252ff2e8a3014ebf8df1308a8a40fb47a11e42fd87992728ba1b0` |
| 三条合成短信会话 | [`images/paired-fixture/paired_sms_threads.png`](images/paired-fixture/paired_sms_threads.png) | 1768 × 2208 | `402e61e045e6630caca73261e7d988be635a4552e97ff4ed43669fd81ffb57af` |
| 合拢窗口中的短信会话 | [`images/paired-fixture/paired_sms_threads_folded.png`](images/paired-fixture/paired_sms_threads_folded.png) | 1768 × 2208 | `79266b4f745dfc9105e291135a78927a9dc14b1420324061e8429b1cc2bfd114` |
| 展开窗口中的短信会话 | [`images/paired-fixture/paired_sms_threads_unfolded.png`](images/paired-fixture/paired_sms_threads_unfolded.png) | 1768 × 2208 | `c86a7f4d0ab7b37638e4fafe93ec2353c7de5ea47f3e4f8b813e7269bb3fe8f5` |
| 既有短信对话与回复框 | [`images/paired-fixture/paired_sms_conversation.png`](images/paired-fixture/paired_sms_conversation.png) | 1768 × 2208 | `e82f3062a9ca052381498c3c97503ceec2ec9405963fab5a0a9a923a270f0ee9` |
| 新短信编辑页 | [`images/paired-fixture/paired_new_message.png`](images/paired-fixture/paired_new_message.png) | 1768 × 2208 | `6ae74ace25972b7477b175dc56e4506e3db15513c4a140f7a954c8ed44015365` |
| 选择 SIM 2 的新短信页 | [`images/paired-fixture/paired_new_message_sim_b.png`](images/paired-fixture/paired_new_message_sim_b.png) | 1768 × 2208 | `d3c0bef6633f277cd6dac8e99fcd4cdca0b2a3a1d1287924d9d64f2621bbda77` |
| 横屏且键盘显示时的短信草稿 | [`images/paired-fixture/paired_new_message_landscape.png`](images/paired-fixture/paired_new_message_landscape.png) | 2208 × 1768 | `e81e042552e36b0ebe8cd764aeaa150da2381610af1b880d0f41480287d87b3e` |
| 合拢窗口中的短信草稿 | [`images/paired-fixture/paired_new_message_folded.png`](images/paired-fixture/paired_new_message_folded.png) | 1768 × 2208 | `cb4d6c697ebe51a9a6f1b8eb0ba59142197dfa2aa088e8438f0bda8faea55603` |
| 展开窗口中的短信草稿 | [`images/paired-fixture/paired_new_message_unfolded.png`](images/paired-fixture/paired_new_message_unfolded.png) | 1768 × 2208 | `55212fc0fa5b021aa473a4a58bc3d88b7fd6072c94f06d9767f30c3ea6216247` |
| 配对状态下的设置 | [`images/paired-fixture/paired_settings.png`](images/paired-fixture/paired_settings.png) | 1768 × 2208 | `7ed9d2e0255972d5292e728f74a88077ca638fbad2c1b950c21cbaf9416ec70a` |
| 配对设置中的短信备份入口 | [`images/paired-fixture/paired_settings_backup.png`](images/paired-fixture/paired_settings_backup.png) | 1768 × 2208 | `3791e71ccc77bae90b293322de94a7313c052e6793aabc718436e51d9bf14623` |
| 合成出站呼叫准备界面 | [`images/paired-fixture/call-synthetic-dialing.png`](images/paired-fixture/call-synthetic-dialing.png) | 1768 × 2208 | `36c3f61ced8bbb699ffe7fd9f9e3ba05eae04118138c70124f642d741d5935ec` |
| 合成通话中界面 | [`images/paired-fixture/call-synthetic-active.png`](images/paired-fixture/call-synthetic-active.png) | 1768 × 2208 | `67d3e36c99ea65d31ba060abcc6a5af700e4353c6bfa3c52a126613389634c17` |
| 合成通话键盘界面 | [`images/paired-fixture/call-synthetic-keypad.png`](images/paired-fixture/call-synthetic-keypad.png) | 1768 × 2208 | `2ad06f37487924a560771000a65e9e6ad08ba8ebcabf9c551cfc7218f97acd33` |
| 合成来电界面 | [`images/paired-fixture/call-synthetic-incoming.png`](images/paired-fixture/call-synthetic-incoming.png) | 1768 × 2208 | `27f9b3007cb0e2d5a07518049679ed5baf50a46ca3a368f52e79a0edb6aaeeba` |

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
| `results.json` | `db55def464341a0c5ed7808d63d873c5ffa03269a3c64f143251895f6ee6d5fe` |
| `screenshot_manifest.json` | `5cfb15125a98bf00fb491014010f2ec8b9b45412e3b6d13569b9fd816d754158` |
| `paired-fixture/results.json` | `3eb3874a7563ad0ba870aaf4e193592262d11f914a5916cc42385bd6c1e08342` |
| `paired-fixture/fixture_manifest.json` | `814e32fbba38e38f23569f2ee76a31f3c5eca1a37d361d4034969b1053bd483d` |

初始评审材料保留在 `images-initial/`、`initial-evidence.json`、`initial-ui-results.json` 和 `local-checks.json`；它们与本次通过的 KVM 截图分开保存。机器可读运行信息见 [`verified-run.json`](verified-run.json)。
