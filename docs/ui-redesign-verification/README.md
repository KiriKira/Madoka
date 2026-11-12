# Android 主机三标签 UI 验证

本报告由通过的 Android 35、7.6 英寸折叠屏 KVM UI smoke artifact 自动生成。截图是 artifact 中原始 PNG 字节的逐字节副本。

## 运行来源

- 仓库：`KiriKira/Madoka`
- 源提交：`07d131c09b90fb1a3f7827b90cc6799ca174c9bc`
- GitHub Actions run：[43209641434](https://github.com/KiriKira/Madoka/actions/runs/43209641434)
- artifact ID：`13942420083`
- artifact digest：`sha256:a44f59c2070d7ff4a703d6c764d2f1274342f40f44c2ab0f30715275782ad45c`
- APK：`app-debug.apk`，SHA-256 `3d97aa9cb0fa3eb4c34201147151245601f7913ebe149cf22cfc3bab634ae43e`，22414356 字节
- artifact 内未配对 smoke：`pass`（2026-11-12T11:38:18.891049+00:00）
- artifact 内配对 fixture：`pass`（2026-11-12T11:42:06.366625+00:00）
- 报告生成时间：`2026-11-12T11:42:24Z`

## 验证范围

未配对流程覆盖电话记录页、拨号键盘、短信列表、设置与备份入口，以及窗口旋转、折叠、展开和挖孔安全区。配对 fixture 使用离线合成的会话、两张 SIM、三条短信和通话记录；新建短信草稿经历 IME、旋转、折叠和展开。

四张出站准备、通话中、通话键盘和来电截图来自进程内合成 coordinator UI 状态；没有启动 SIP、Telecom、音频或蜂窝通话，未发送或接收短信。配对 fixture 结果要求 `fixture_active_call_ui=pass`。

本报告只验证 Android Emulator 上的界面流程，不代表 Z Fold8 真机、真实 SIM 短信收发、SIP 注册或双向语音已完成设备验收。

## 截图与文件校验

| 页面 | PNG | 尺寸 | 文件 SHA-256 |
|---|---|---:|---|
| 三标签导航 | [`images/host_navigation_initial.png`](images/host_navigation_initial.png) | 1768 × 2208 | `163b524bc841ff0a390c8e6516e8708adb0bd6076e0a242d0b0916c2a2fd13c8` |
| 未配对电话记录页 | [`images/host_call_history.png`](images/host_call_history.png) | 1768 × 2208 | `ef3e43bdcb391f20e1c2da6b22c93fb2c44bbf7aad8b48e7586c9f16263fe12b` |
| 独立拨号键盘 | [`images/host_dialpad.png`](images/host_dialpad.png) | 1768 × 2208 | `aa0b3b73f9857fd51b4f80cef5960cb1a27c727ea153fc9f279a8e6448ba9163` |
| 短信会话列表 | [`images/host_sms_thread_list.png`](images/host_sms_thread_list.png) | 1768 × 2208 | `b70616051f1a7085ba0fddf8ba632f61a49381e369c1fccc80d7c5d1f033fd31` |
| 设置与配对表单 | [`images/host_settings_tab.png`](images/host_settings_tab.png) | 1768 × 2208 | `67d4d6a9366a615f8121045d0aaf8629b60ceedec49d0f34489f74089751297f` |
| 设置中的短信备份入口 | [`images/host_settings_backup_entry.png`](images/host_settings_backup_entry.png) | 1768 × 2208 | `accab5ed6b13f9a754e085f01af2b94adbf3a055991dc02a2e9db853f8464f69` |
| 主界面合拢窗口 | [`images/folded_ui.png`](images/folded_ui.png) | 1768 × 2208 | `dc162a94505597d63574fa0e2c0de6be3e893e01257a2fd6ed514410f48c488f` |
| 主界面展开窗口 | [`images/unfolded_ui.png`](images/unfolded_ui.png) | 1768 × 2208 | `730cf8dccbcb57b8a7d136d11bdc28a623d6d7b4bf87220f1e6310d28dcff7a5` |
| 显示挖孔时的主界面 | [`images/hole_cutout_enabled.png`](images/hole_cutout_enabled.png) | 1768 × 2208 | `346f7d8ca3b55071f804cb67c8a60ef2c5aa0dc27241348d8d4ab0c8f5fd77e6` |
| 滚动后可见的备份入口 | [`images/main-backup-entry-host.png`](images/main-backup-entry-host.png) | 1768 × 2208 | `85c199cc838e12840e4fcead4d329f28bcfb1aec73bb320cb230e231787262f5` |
| 短信备份与归档页面 | [`images/backup-overview.png`](images/backup-overview.png) | 1768 × 2208 | `2e5a2953a05d20033c9fc318f5b8359e2d0817fbec4940a8f3bb8fbb5490f7c7` |
| 备份页面横屏 | [`images/backup-rotation-landscape.png`](images/backup-rotation-landscape.png) | 2208 × 1768 | `28e1b721e9d1788ac025e5bb5f19a462ab0ac90646263097880e7fc64084ce05` |
| 备份页面竖屏 | [`images/backup-rotation-portrait.png`](images/backup-rotation-portrait.png) | 1768 × 2208 | `dda19f9467befe52596c6f6d8035b2c171bab2ce2545477d9a4517dbbce1d8d3` |
| 备份页面合拢 | [`images/backup-folded.png`](images/backup-folded.png) | 1768 × 2208 | `7e2a0fba0ae35ffb0b7508c38ae7b046b4d50d4f667e84fd62b53c2d0753f912` |
| 备份页面展开 | [`images/backup-unfolded.png`](images/backup-unfolded.png) | 1768 × 2208 | `dda19f9467befe52596c6f6d8035b2c171bab2ce2545477d9a4517dbbce1d8d3` |
| 挖孔安全区中的备份页面 | [`images/backup-cutout.png`](images/backup-cutout.png) | 1768 × 2208 | `dda19f9467befe52596c6f6d8035b2c171bab2ce2545477d9a4517dbbce1d8d3` |
| 合成短信归档导入预览 | [`images/backup-import-preview.png`](images/backup-import-preview.png) | 1768 × 2208 | `2767fd093914b82d82d3d642bdb709781a0a42e4ba8dc8ae14b6fd8b21540f18` |
| 导入后的归档历史 | [`images/backup-imported-history.png`](images/backup-imported-history.png) | 1768 × 2208 | `ea30b1ea5a7a74f2c8b2f77a758daec0436434c6891482f0b5eea960e3bdb18c` |
| 配对状态下的三标签 | [`images/paired-fixture/paired_three_tabs.png`](images/paired-fixture/paired_three_tabs.png) | 1768 × 2208 | `093b8011592c43ef48dc4fb6bb6e5695c94cff7330ba96fe6c959284afe080cb` |
| 双 SIM 合成通话记录 | [`images/paired-fixture/paired_call_history.png`](images/paired-fixture/paired_call_history.png) | 1768 × 2208 | `467b628c6b82e449eab513f2c87ee674a1f695d2cf6927a565768e4b1cd95227` |
| 从通话记录预填号码的拨号页 | [`images/paired-fixture/paired_dialpad_from_history.png`](images/paired-fixture/paired_dialpad_from_history.png) | 1768 × 2208 | `41397c0f7ddec7a92833b04c1a30c59d2c670259d9d220aeb07fb410ea74fd5e` |
| 配对状态下的独立拨号键盘 | [`images/paired-fixture/paired_dialpad.png`](images/paired-fixture/paired_dialpad.png) | 1768 × 2208 | `41397c0f7ddec7a92833b04c1a30c59d2c670259d9d220aeb07fb410ea74fd5e` |
| 三条合成短信会话 | [`images/paired-fixture/paired_sms_threads.png`](images/paired-fixture/paired_sms_threads.png) | 1768 × 2208 | `695e143db984b8814a6b46903797634123b183c7b77fdce796249d424dd15c4c` |
| 合拢窗口中的短信会话 | [`images/paired-fixture/paired_sms_threads_folded.png`](images/paired-fixture/paired_sms_threads_folded.png) | 1768 × 2208 | `eb8f65ce61815c9511444ae1e4466d29260982e3dd34986c076b3a380fc71366` |
| 展开窗口中的短信会话 | [`images/paired-fixture/paired_sms_threads_unfolded.png`](images/paired-fixture/paired_sms_threads_unfolded.png) | 1768 × 2208 | `695e143db984b8814a6b46903797634123b183c7b77fdce796249d424dd15c4c` |
| 既有短信对话与回复框 | [`images/paired-fixture/paired_sms_conversation.png`](images/paired-fixture/paired_sms_conversation.png) | 1768 × 2208 | `c5ba2923e975cfc0550e05acc4ed374888371c1889787419e12c6d85e5335b7b` |
| 新短信编辑页 | [`images/paired-fixture/paired_new_message.png`](images/paired-fixture/paired_new_message.png) | 1768 × 2208 | `bae49b0d50ca45a28074f66d3ea26776efe3cad46a99a7d7093cba892f5a442f` |
| 选择 SIM 2 的新短信页 | [`images/paired-fixture/paired_new_message_sim_b.png`](images/paired-fixture/paired_new_message_sim_b.png) | 1768 × 2208 | `60e69f18de6f0759ec334acebd94545abdaa9c2acc64639e1a33aff849691db4` |
| 横屏且键盘显示时的短信草稿 | [`images/paired-fixture/paired_new_message_landscape.png`](images/paired-fixture/paired_new_message_landscape.png) | 2208 × 1768 | `71874006a3fc969df17736cf5027b36a075d09697b2bb66d14697fc17b25c31e` |
| 合拢窗口中的短信草稿 | [`images/paired-fixture/paired_new_message_folded.png`](images/paired-fixture/paired_new_message_folded.png) | 1768 × 2208 | `b6529843e6d8c567cccf56adff5c3117563c43743739179e27d34bf3f8e1825e` |
| 展开窗口中的短信草稿 | [`images/paired-fixture/paired_new_message_unfolded.png`](images/paired-fixture/paired_new_message_unfolded.png) | 1768 × 2208 | `60bb4497813e0ead48d4fc7837bd666a27e48edf39e39d1a0ac85b3e1e3cad54` |
| 配对状态下的设置 | [`images/paired-fixture/paired_settings.png`](images/paired-fixture/paired_settings.png) | 1768 × 2208 | `ef1f5a018554ca17690f91b301eea0660e53d6082c84030cab4e5f0516ade056` |
| 配对设置中的短信备份入口 | [`images/paired-fixture/paired_settings_backup.png`](images/paired-fixture/paired_settings_backup.png) | 1768 × 2208 | `5d3181291305af38b74229a0d92623c33327b8187d9a2294a6a7e8bf244267c5` |
| 合成出站呼叫准备界面 | [`images/paired-fixture/call-synthetic-dialing.png`](images/paired-fixture/call-synthetic-dialing.png) | 1768 × 2208 | `5ee0985702941e851373a1d6f9a0d7dd452212076f4bdec0f7dac4e1c75387bb` |
| 合成通话中界面 | [`images/paired-fixture/call-synthetic-active.png`](images/paired-fixture/call-synthetic-active.png) | 1768 × 2208 | `c5ef51a05f9d1dc720c8f4b50e284038e495c7235d8e3b78632b603d2dc8570e` |
| 合成通话键盘界面 | [`images/paired-fixture/call-synthetic-keypad.png`](images/paired-fixture/call-synthetic-keypad.png) | 1768 × 2208 | `09aaf2fad5d70c9faca0895890b76b93ccbde58b3b8dde0b77f4067b08ad7c2c` |
| 合成来电界面 | [`images/paired-fixture/call-synthetic-incoming.png`](images/paired-fixture/call-synthetic-incoming.png) | 1768 × 2208 | `f0c14c3de62d4d4e1bf05f9c8e55950e6a71f1f67c9e768674a718a370a7d446` |

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
| `results.json` | `5b1e22c4959e9a72577fcb9cf7fdd642d0e2b0b3ad32d3dc9ba92fa02a42ddb9` |
| `screenshot_manifest.json` | `4d9f929a45e8bbf169eedcf86fc5e9e6e23da753a10b5bc442a27f96be5494fe` |
| `paired-fixture/results.json` | `f61e54a7d1258d6b3d7b544cda5ed5d5aefeeafac13abfe416b4309c20179e00` |
| `paired-fixture/fixture_manifest.json` | `b00eb8146faaa821732d5cc4feb43d9d8e5d57999722b6a209edeacd775efea7` |

初始评审材料保留在 `images-initial/`、`initial-evidence.json`、`initial-ui-results.json` 和 `local-checks.json`；它们与本次通过的 KVM 截图分开保存。机器可读运行信息见 [`verified-run.json`](verified-run.json)。
