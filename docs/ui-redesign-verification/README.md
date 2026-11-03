# Android 主机三标签 UI 验证

本报告由通过的 Android 35、7.6 英寸折叠屏 KVM UI smoke artifact 自动生成。截图是 artifact 中原始 PNG 字节的逐字节副本。

## 运行来源

- 仓库：`KiriKira/gsm2sip-client-android`
- 源提交：`88c0fe29a7f0d8e5afca1ed9629c10c6b1efaa30`
- GitHub Actions run：[41652507828](https://github.com/KiriKira/gsm2sip-client-android/actions/runs/41652507828)
- artifact ID：`13238848147`
- artifact digest：`sha256:0f6d8580c0c978a5a19ed2df0ee1745b18e4dc416c6cfcca0d9d7115d6b916fe`
- APK：`app-debug.apk`，SHA-256 `4ceff7849efa15fbbbc2190cfd700975a3294db489303eff1b64c27729107024`，22397924 字节
- artifact 内未配对 smoke：`pass`（2026-11-03T14:28:15.393016+00:00）
- artifact 内配对 fixture：`pass`（2026-11-03T14:32:21.655732+00:00）
- 报告生成时间：`2026-11-03T14:32:37Z`

## 验证范围

未配对流程覆盖电话记录页、拨号键盘、短信列表、设置与备份入口，以及窗口旋转、折叠、展开和挖孔安全区。配对 fixture 使用离线合成的会话、两张 SIM、三条短信和通话记录；新建短信草稿经历 IME、旋转、折叠和展开。

四张出站准备、通话中、通话键盘和来电截图来自进程内合成 coordinator UI 状态；没有启动 SIP、Telecom、音频或蜂窝通话，未发送或接收短信。配对 fixture 结果要求 `fixture_active_call_ui=pass`。

本报告只验证 Android Emulator 上的界面流程，不代表 Z Fold8 真机、真实 SIM 短信收发、SIP 注册或双向语音已完成设备验收。

## 截图与文件校验

| 页面 | PNG | 尺寸 | 文件 SHA-256 |
|---|---|---:|---|
| 三标签导航 | [`images/host_navigation_initial.png`](images/host_navigation_initial.png) | 1768 × 2208 | `4f4cd4cf8bdd00c17acd8afa028be761fd2a908e0b2c6f62548b2af97f98c9a7` |
| 未配对电话记录页 | [`images/host_call_history.png`](images/host_call_history.png) | 1768 × 2208 | `15e292958d9bd3107e6d624f62d2cca308bc29ec129be24c59ed67d01d2069d0` |
| 独立拨号键盘 | [`images/host_dialpad.png`](images/host_dialpad.png) | 1768 × 2208 | `f7aa55f7dc490bf451e51500cd733fa74aee1c530b6bf2c2b79bffa5f6edcaae` |
| 短信会话列表 | [`images/host_sms_thread_list.png`](images/host_sms_thread_list.png) | 1768 × 2208 | `fb37c6f4879e25f82cb00925a7a31837274ecdb8ad207472a606a723feff2d3d` |
| 设置与配对表单 | [`images/host_settings_tab.png`](images/host_settings_tab.png) | 1768 × 2208 | `f380a5289e9baa60813735716c2be51bad2e91e8d96603e8610cef6731f8bede` |
| 设置中的短信备份入口 | [`images/host_settings_backup_entry.png`](images/host_settings_backup_entry.png) | 1768 × 2208 | `bd6c936b4a1bb9e9f4d77a9dd169ec7301f37af5bdee79c104c8320257d22391` |
| 主界面合拢窗口 | [`images/folded_ui.png`](images/folded_ui.png) | 1768 × 2208 | `6454cbd48f033ffcadd188d17f35480482596eae7776a18e3b6d5457bd3bbf22` |
| 主界面展开窗口 | [`images/unfolded_ui.png`](images/unfolded_ui.png) | 1768 × 2208 | `a845a3a249ede8c694b4ffd9687796e11835bb7b86cff0dcef865f722d035a4b` |
| 显示挖孔时的主界面 | [`images/hole_cutout_enabled.png`](images/hole_cutout_enabled.png) | 1768 × 2208 | `624a3bac10759b2b67a786e7d8f60f70cc91c8070e08ce8d611c04ad20963fd4` |
| 滚动后可见的备份入口 | [`images/main-backup-entry-host.png`](images/main-backup-entry-host.png) | 1768 × 2208 | `1e80c02cfada5ed23f43db37af2ec1d041cc969e2a57b9d6bf2e3f2ad38fd35a` |
| 短信备份与归档页面 | [`images/backup-overview.png`](images/backup-overview.png) | 1768 × 2208 | `0a46555dba07613fc0b749f9bda3471ab6eeec1d69ff6b0b4b7a82faba0061f5` |
| 备份页面横屏 | [`images/backup-rotation-landscape.png`](images/backup-rotation-landscape.png) | 2208 × 1768 | `78e1e4c8f7bbb04e1a789e0c0ecf6d2f65a7c5c1e2f096c507df9f5c430ff8d9` |
| 备份页面竖屏 | [`images/backup-rotation-portrait.png`](images/backup-rotation-portrait.png) | 1768 × 2208 | `755fc340e12002e2a59d182175a613b95ff75160c843bf3b6241a70f80e723cf` |
| 备份页面合拢 | [`images/backup-folded.png`](images/backup-folded.png) | 1768 × 2208 | `626e1bd57d9bd072ce0d47abdb3864f9db846a196351b3c93a65a9510ee5af56` |
| 备份页面展开 | [`images/backup-unfolded.png`](images/backup-unfolded.png) | 1768 × 2208 | `755fc340e12002e2a59d182175a613b95ff75160c843bf3b6241a70f80e723cf` |
| 挖孔安全区中的备份页面 | [`images/backup-cutout.png`](images/backup-cutout.png) | 1768 × 2208 | `755fc340e12002e2a59d182175a613b95ff75160c843bf3b6241a70f80e723cf` |
| 合成短信归档导入预览 | [`images/backup-import-preview.png`](images/backup-import-preview.png) | 1768 × 2208 | `0095c170570ff064f92dcdbf00ced2dda1f4e29db31b1cc9f0a3a64db1ceeaf5` |
| 导入后的归档历史 | [`images/backup-imported-history.png`](images/backup-imported-history.png) | 1768 × 2208 | `0f3600b2ddddc1ab978a8c243087e7cdef415add0110ef04f9444d6fcff4f25e` |
| 配对状态下的三标签 | [`images/paired-fixture/paired_three_tabs.png`](images/paired-fixture/paired_three_tabs.png) | 1768 × 2208 | `4db00042ac2d80e4f018cd07ac69863def4b6003b7466eb673c5072be307c97e` |
| 双 SIM 合成通话记录 | [`images/paired-fixture/paired_call_history.png`](images/paired-fixture/paired_call_history.png) | 1768 × 2208 | `cff376d9e09b8eef4e0feb4b89e6d7e4c9e0a68dabebf06b2910c002528afd82` |
| 从通话记录预填号码的拨号页 | [`images/paired-fixture/paired_dialpad_from_history.png`](images/paired-fixture/paired_dialpad_from_history.png) | 1768 × 2208 | `d3336ee748bd41f6b17886f71d6ed6457665cd382e3504de1e87c3cc2d05b4ff` |
| 配对状态下的独立拨号键盘 | [`images/paired-fixture/paired_dialpad.png`](images/paired-fixture/paired_dialpad.png) | 1768 × 2208 | `eb1cfefef86654d08c69f8a28590ef2abf34fe5da5e0bbb2530e89e4b6d14449` |
| 三条合成短信会话 | [`images/paired-fixture/paired_sms_threads.png`](images/paired-fixture/paired_sms_threads.png) | 1768 × 2208 | `462273b85d0d9b0c882e8990e8dd71aceaccabb1f8aaa752ee503278509efe5e` |
| 合拢窗口中的短信会话 | [`images/paired-fixture/paired_sms_threads_folded.png`](images/paired-fixture/paired_sms_threads_folded.png) | 1768 × 2208 | `aa4e716d03c9197f10e5f1bf92dab247bf1efa1d2c4822960b0e45f76a2a8dab` |
| 展开窗口中的短信会话 | [`images/paired-fixture/paired_sms_threads_unfolded.png`](images/paired-fixture/paired_sms_threads_unfolded.png) | 1768 × 2208 | `462273b85d0d9b0c882e8990e8dd71aceaccabb1f8aaa752ee503278509efe5e` |
| 既有短信对话与回复框 | [`images/paired-fixture/paired_sms_conversation.png`](images/paired-fixture/paired_sms_conversation.png) | 1768 × 2208 | `cf4bf116b55fbf8fe97376c04d540dbddbf786226a7d10ebfa880e0e1e5bbcb5` |
| 新短信编辑页 | [`images/paired-fixture/paired_new_message.png`](images/paired-fixture/paired_new_message.png) | 1768 × 2208 | `4b8ac420411edf4d785e069d9a71c003326f831b27b977828d054343860c0a3f` |
| 选择 SIM 2 的新短信页 | [`images/paired-fixture/paired_new_message_sim_b.png`](images/paired-fixture/paired_new_message_sim_b.png) | 1768 × 2208 | `3da16dc189a10e59d93ba9dc4d33d28ad52a9b44160505cfff74d5846678ee46` |
| 横屏且键盘显示时的短信草稿 | [`images/paired-fixture/paired_new_message_landscape.png`](images/paired-fixture/paired_new_message_landscape.png) | 2208 × 1768 | `85769c14a4d9568567a39ee0fa814880a21c2230fc65f7d23f3db9a817b503c9` |
| 合拢窗口中的短信草稿 | [`images/paired-fixture/paired_new_message_folded.png`](images/paired-fixture/paired_new_message_folded.png) | 1768 × 2208 | `e9f16e978404fc409c763dd499cc8598a44cf440b4b09b86a8cad991a5815336` |
| 展开窗口中的短信草稿 | [`images/paired-fixture/paired_new_message_unfolded.png`](images/paired-fixture/paired_new_message_unfolded.png) | 1768 × 2208 | `c4534624357383c338387370187a632a590ba8134a1a779a67f1d9595ae61022` |
| 配对状态下的设置 | [`images/paired-fixture/paired_settings.png`](images/paired-fixture/paired_settings.png) | 1768 × 2208 | `b8614f5de0b70232da463b63c7607a6010b6eca5be53bb5371dc347e073d8dbe` |
| 配对设置中的短信备份入口 | [`images/paired-fixture/paired_settings_backup.png`](images/paired-fixture/paired_settings_backup.png) | 1768 × 2208 | `cf12d7df108fd1e87f9cb9517c9a9f37fe9e98d716071de54a304de5c109ab84` |
| 合成出站呼叫准备界面 | [`images/paired-fixture/call-synthetic-dialing.png`](images/paired-fixture/call-synthetic-dialing.png) | 1768 × 2208 | `5d440432f316d928a04fa01a26cd5af64b9add5841599b3c9dca786e62d9b7be` |
| 合成通话中界面 | [`images/paired-fixture/call-synthetic-active.png`](images/paired-fixture/call-synthetic-active.png) | 1768 × 2208 | `096266cfc3bb792cef6f3031649897883d0d169051dc61ff4b0e411512ef480a` |
| 合成通话键盘界面 | [`images/paired-fixture/call-synthetic-keypad.png`](images/paired-fixture/call-synthetic-keypad.png) | 1768 × 2208 | `d1dc298fcf99065b5f8166870f761eec5f77bd1ed55fdd2cb65c750401044a1e` |
| 合成来电界面 | [`images/paired-fixture/call-synthetic-incoming.png`](images/paired-fixture/call-synthetic-incoming.png) | 1768 × 2208 | `ca8890b4d805427b14222e90fbb81cd34c46c70c1968f2f883c703b072f2c7f9` |

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
| `results.json` | `e124c87f8fbfeb5a5da0f7bb6e95ab1bd69d6e1d1ea60b799fb2186e8f36d96c` |
| `screenshot_manifest.json` | `886e7ba3b6e1dadc4ab7108397346eaa3bf91c7972fbc96df14a9c724bcd4869` |
| `paired-fixture/results.json` | `368d57c93125e87504ed9d1472bfe6cb423887288e1d38194733a41ea4cdd13a` |
| `paired-fixture/fixture_manifest.json` | `0cdb7a972f206f0340c78792755215f5621f7993d0cbabc1b0fdbcfc1626856f` |

初始评审材料保留在 `images-initial/`、`initial-evidence.json`、`initial-ui-results.json` 和 `local-checks.json`；它们与本次通过的 KVM 截图分开保存。机器可读运行信息见 [`verified-run.json`](verified-run.json)。
