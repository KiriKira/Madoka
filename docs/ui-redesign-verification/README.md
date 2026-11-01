# Android 主机三标签 UI 验证

本报告由通过的 Android 35、7.6 英寸折叠屏 KVM UI smoke artifact 自动生成。截图是 artifact 中原始 PNG 字节的逐字节副本。

## 运行来源

- 仓库：`KiriKira/gsm2sip-client-android`
- 源提交：`62f325758deb87dbf1d33f87b1e4885a4ba263f9`
- GitHub Actions run：[41302760069](https://github.com/KiriKira/gsm2sip-client-android/actions/runs/41302760069)
- artifact ID：`13081619102`
- artifact digest：`sha256:44a8687f420c30327da0e4a8c8a446b63e1680ef47b890cc5f9f08a964650d93`
- APK：`app-debug.apk`，SHA-256 `5015085487f946e3b928990b9447c7ea0e1256c68f0d73959047a900f3c3a34a`，22381492 字节
- artifact 内未配对 smoke：`pass`（2026-11-01T11:47:19.957695+00:00）
- artifact 内配对 fixture：`pass`（2026-11-01T11:51:20.542986+00:00）
- 报告生成时间：`2026-11-01T11:51:39Z`

## 验证范围

未配对流程覆盖电话记录页、拨号键盘、短信列表、设置与备份入口，以及窗口旋转、折叠、展开和挖孔安全区。配对 fixture 使用离线合成的会话、两张 SIM、三条短信和通话记录；新建短信草稿经历 IME、旋转、折叠和展开。

四张出站准备、通话中、通话键盘和来电截图来自进程内合成 coordinator UI 状态；没有启动 SIP、Telecom、音频或蜂窝通话，未发送或接收短信。配对 fixture 结果要求 `fixture_active_call_ui=pass`。

本报告只验证 Android Emulator 上的界面流程，不代表 Z Fold8 真机、真实 SIM 短信收发、SIP 注册或双向语音已完成设备验收。

## 截图与文件校验

| 页面 | PNG | 尺寸 | 文件 SHA-256 |
|---|---|---:|---|
| 三标签导航 | [`images/host_navigation_initial.png`](images/host_navigation_initial.png) | 1768 × 2208 | `071c801c6fcf50392657665d5d6071c986884ff3ae3c712fac70e756842f12ea` |
| 未配对电话记录页 | [`images/host_call_history.png`](images/host_call_history.png) | 1768 × 2208 | `27919e81716a8f80566e37f8c427c625344a9bd72471b489010eaa5f97f52fd8` |
| 独立拨号键盘 | [`images/host_dialpad.png`](images/host_dialpad.png) | 1768 × 2208 | `afee6e2bbcab091046c4623ca0472faa2ff0b347995c3976e55da03eb2b01e14` |
| 短信会话列表 | [`images/host_sms_thread_list.png`](images/host_sms_thread_list.png) | 1768 × 2208 | `a249fae7d07496d2d926bc6ffd57e0e206f536728545f731ae308d6035605aee` |
| 设置与配对表单 | [`images/host_settings_tab.png`](images/host_settings_tab.png) | 1768 × 2208 | `0d9f65ca2cf06f817d11abf096fb0da78c8104689b4ba9742388720684729781` |
| 设置中的短信备份入口 | [`images/host_settings_backup_entry.png`](images/host_settings_backup_entry.png) | 1768 × 2208 | `b93c91ce33a0d18ae5a946ec1d7fd6552eee2c1e9435ab209e4e61a5fbbc716f` |
| 主界面合拢窗口 | [`images/folded_ui.png`](images/folded_ui.png) | 1768 × 2208 | `25baea5149e4b0ee6a02cd2f223e8e10921f33779df739bafd7d31141bc39723` |
| 主界面展开窗口 | [`images/unfolded_ui.png`](images/unfolded_ui.png) | 1768 × 2208 | `7608cc368f69b2bc10e7fb865367c8c31bff35b02d86b51f7e75ec60073892b1` |
| 显示挖孔时的主界面 | [`images/hole_cutout_enabled.png`](images/hole_cutout_enabled.png) | 1768 × 2208 | `025aa3353caa863981d265681ab595cb5a296ae0524ce01b8c0a36a558b6b640` |
| 滚动后可见的备份入口 | [`images/main-backup-entry-host.png`](images/main-backup-entry-host.png) | 1768 × 2208 | `74774c594b882ea2bb5d19d8b7e2cf9b96cc57f701a943d932675cf2daec61ba` |
| 短信备份与归档页面 | [`images/backup-overview.png`](images/backup-overview.png) | 1768 × 2208 | `b608a0af602e03f672167fda505ce761d6efe0db5398ffcf7eaf26d40448c5d2` |
| 备份页面横屏 | [`images/backup-rotation-landscape.png`](images/backup-rotation-landscape.png) | 2208 × 1768 | `191e3925b4ade740197757894fb41507c4ff31cf994727ec5d576382b8db019d` |
| 备份页面竖屏 | [`images/backup-rotation-portrait.png`](images/backup-rotation-portrait.png) | 1768 × 2208 | `a4533fab1a9efd2a84a6bfe5f0701edbb1d6e47ca13a5bbb6b0a1a44047eb378` |
| 备份页面合拢 | [`images/backup-folded.png`](images/backup-folded.png) | 1768 × 2208 | `7b1788d3eaa98bfbd0c4d7c291b12eea571937ac4d0633278defd5a380082c04` |
| 备份页面展开 | [`images/backup-unfolded.png`](images/backup-unfolded.png) | 1768 × 2208 | `a4533fab1a9efd2a84a6bfe5f0701edbb1d6e47ca13a5bbb6b0a1a44047eb378` |
| 挖孔安全区中的备份页面 | [`images/backup-cutout.png`](images/backup-cutout.png) | 1768 × 2208 | `a4533fab1a9efd2a84a6bfe5f0701edbb1d6e47ca13a5bbb6b0a1a44047eb378` |
| 合成短信归档导入预览 | [`images/backup-import-preview.png`](images/backup-import-preview.png) | 1768 × 2208 | `044714fd6c3bf0d9568fde5bab91a4ff230ef6b19268a9cabe2aadd010070a85` |
| 导入后的归档历史 | [`images/backup-imported-history.png`](images/backup-imported-history.png) | 1768 × 2208 | `f9c503780b31cfd37392b52396b22d7bb21429bd77ae1f2eb19a3fb45dae9dd4` |
| 配对状态下的三标签 | [`images/paired-fixture/paired_three_tabs.png`](images/paired-fixture/paired_three_tabs.png) | 1768 × 2208 | `bce81398b6af8b14328550d7481a96df915847054318af1452b4a9ada2238b24` |
| 双 SIM 合成通话记录 | [`images/paired-fixture/paired_call_history.png`](images/paired-fixture/paired_call_history.png) | 1768 × 2208 | `6311812fec5c4b9544d09505d3ed7fbecc778bdfd09991f307061ebe8eb98f83` |
| 从通话记录预填号码的拨号页 | [`images/paired-fixture/paired_dialpad_from_history.png`](images/paired-fixture/paired_dialpad_from_history.png) | 1768 × 2208 | `e02bfa0f05d47a63658912d01d1f6eb742a82b1a8f0e39b4ee6fdb55040b53c9` |
| 配对状态下的独立拨号键盘 | [`images/paired-fixture/paired_dialpad.png`](images/paired-fixture/paired_dialpad.png) | 1768 × 2208 | `b6b2da45af8f47660bf5ef66c2313b9c0b279833aaaac84f1e995a294b8d9329` |
| 三条合成短信会话 | [`images/paired-fixture/paired_sms_threads.png`](images/paired-fixture/paired_sms_threads.png) | 1768 × 2208 | `63a4a91b6037e343a34e5da23818e89736cbcd1fa62e5e99f99e580567a913cf` |
| 合拢窗口中的短信会话 | [`images/paired-fixture/paired_sms_threads_folded.png`](images/paired-fixture/paired_sms_threads_folded.png) | 1768 × 2208 | `cef0fa30f0bc0a836feafa4aa8360fbb92c27088fba59cfda67bf21f071cfda2` |
| 展开窗口中的短信会话 | [`images/paired-fixture/paired_sms_threads_unfolded.png`](images/paired-fixture/paired_sms_threads_unfolded.png) | 1768 × 2208 | `63a4a91b6037e343a34e5da23818e89736cbcd1fa62e5e99f99e580567a913cf` |
| 既有短信对话与回复框 | [`images/paired-fixture/paired_sms_conversation.png`](images/paired-fixture/paired_sms_conversation.png) | 1768 × 2208 | `40f6c0f0e2fea284b4b9c0261f7408eb42505e2bc54a6cd5db4ff6f8812d5db7` |
| 新短信编辑页 | [`images/paired-fixture/paired_new_message.png`](images/paired-fixture/paired_new_message.png) | 1768 × 2208 | `6bc031b1ac2dac6f0953e3ec95bee4ed983a9f709b8327d5109b3a68e65e9f66` |
| 选择 SIM 2 的新短信页 | [`images/paired-fixture/paired_new_message_sim_b.png`](images/paired-fixture/paired_new_message_sim_b.png) | 1768 × 2208 | `d5de8d3854eaab595057afe1324c050bf5cf07f4d20d6e539a666d62ef29be6f` |
| 横屏且键盘显示时的短信草稿 | [`images/paired-fixture/paired_new_message_landscape.png`](images/paired-fixture/paired_new_message_landscape.png) | 2208 × 1768 | `b5a9ac1b51e05273d2b4e774a8149143fd80e94865bd87fd6afe772a25a1a4e8` |
| 合拢窗口中的短信草稿 | [`images/paired-fixture/paired_new_message_folded.png`](images/paired-fixture/paired_new_message_folded.png) | 1768 × 2208 | `e5874c419377c8ccd2c4ab8453e279a85d0fb666deceae7fec87320b0c5a4292` |
| 展开窗口中的短信草稿 | [`images/paired-fixture/paired_new_message_unfolded.png`](images/paired-fixture/paired_new_message_unfolded.png) | 1768 × 2208 | `157402728ddf928efccda6079beb18da9ac29165f32bc720f4653def6dcce017` |
| 配对状态下的设置 | [`images/paired-fixture/paired_settings.png`](images/paired-fixture/paired_settings.png) | 1768 × 2208 | `f0bb30c35efb4d20750cc9b2ed9cdf1b8182fb09bbe7a1c1ddf9a3f1add68d86` |
| 配对设置中的短信备份入口 | [`images/paired-fixture/paired_settings_backup.png`](images/paired-fixture/paired_settings_backup.png) | 1768 × 2208 | `60c1514ac533ec8afea0615b07235b65d14dcb75a51d3b50f91a0c5ffd9d83cc` |
| 合成出站呼叫准备界面 | [`images/paired-fixture/call-synthetic-dialing.png`](images/paired-fixture/call-synthetic-dialing.png) | 1768 × 2208 | `cab0945ce1c5aff866dae291c5f571299fecbf46e9f7001b6acf60a18b5847f1` |
| 合成通话中界面 | [`images/paired-fixture/call-synthetic-active.png`](images/paired-fixture/call-synthetic-active.png) | 1768 × 2208 | `7e74d987072655bb32882e46e0c687b4238be830f625f1969a6321a56ecfee2f` |
| 合成通话键盘界面 | [`images/paired-fixture/call-synthetic-keypad.png`](images/paired-fixture/call-synthetic-keypad.png) | 1768 × 2208 | `91b858fc468ebf54878daa8cf8e75a74e85a17eb733c0028fb3ae10383bce5dd` |
| 合成来电界面 | [`images/paired-fixture/call-synthetic-incoming.png`](images/paired-fixture/call-synthetic-incoming.png) | 1768 × 2208 | `1e3a9ba2af56a609b4c0e26eda99c9157a1c45e6e1478726629b8580303d5f65` |

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
| `results.json` | `8bbcd8f0105ea839053ecaa94c76c8df84cc7891edcaea8d50ba86785d5f2b63` |
| `screenshot_manifest.json` | `9ae6e7afa8d86222fd4a341d192215d4f0fc5ce00e7802d6f19a4772beba1c6a` |
| `paired-fixture/results.json` | `d1dc87b92f258ef9e2e659684f6c19a524cad13ab1af51d32a96047207c1daa2` |
| `paired-fixture/fixture_manifest.json` | `9f5b622c98bdd9491449db80db6c4862fad751cad04d145e1b2a24749814fed3` |

初始评审材料保留在 `images-initial/`、`initial-evidence.json`、`initial-ui-results.json` 和 `local-checks.json`；它们与本次通过的 KVM 截图分开保存。机器可读运行信息见 [`verified-run.json`](verified-run.json)。
