<p align="center">
  <img src="icon.png" width="112" alt="Madoka">
</p>
<h1 align="center">Madoka</h1>
<p align="center"><b>出门在外，远程收发短信接打电话都搞定</b></p>
<p align="center">
  <a href="https://github.com/KiriKira/Madoka/releases">下载 APK</a> ·
  <a href="#-快速上手">快速配对</a> ·
  <a href="#-系列组件分工">全家桶架构</a> ·
  <a href="docs/">文档</a>
</p>
<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white" alt="Android">
  <img src="https://img.shields.io/badge/Voice-WebRTC-blue?style=flat-square" alt="WebRTC">
  <img src="https://img.shields.io/badge/Version-1.0.1-ff69b4?style=flat-square" alt="Version">
  <img src="https://img.shields.io/badge/License-MIT-blue?style=flat-square" alt="MIT License">
</p>

外出不想带两台手机，或者有些 SIM 卡只放在固定设备上？**Madoka** 是 MahoGSM 系列的主力手机端 App。通过自建服务端（**SoulGem** / **GriefSeed**），你可以直接在日常使用的 Android 手机上远程接打电话、收发双卡短信，界面和原生电话、信息一样顺手。

## ✨ 为什么用 Madoka？

- **主力机免插卡，双卡照常收发。** 短信按远端 SIM 卡槽区分会话，回复时自动沿用原卡槽，不会串卡；验证码通知自带一键复制按钮。
- **高清远程通话，彻底告别 SIP。** 通话全面采用原生 WebRTC（DTLS-SRTP 加密 + Opus 音频），无需折腾 Asterisk 或复杂端口转发，点开就能打。
- **多台主力机同时待命。** 同一套账号可以配对多台主力设备；来电时全部振铃，谁先接听归谁，一端挂断其余同步停止。
- **多节点容灾，网络抖了也不丢消息。** 支持添加多个服务器节点并拖动排序，主节点断开或延迟过高时自动切换，同一条短信跨节点只提醒一次。
- **待机省电，来电秒唤醒。** 支持 Reception v2 与 [Kyubey](https://github.com/KiriKira/Kyubey) 无内容推送唤醒，平时不靠高频心跳硬扛；没有 Google 推送的环境会自动回退长连接。
- **原生 Material 3 体验。** 电话 / 短信 / 设置三栏布局，适配折叠屏内外屏切换、横竖屏与挖孔屏；支持加密备份、JSON 与 SMS Backup & Restore XML 导入导出。

## 🧩 系列组件分工

| 组件 | 角色 | 说明 |
| --- | --- | --- |
| **Madoka**（本仓库） | 主力手机端 | 装在你随身携带的手机上，用来打电话、看短信 |
| [Homura](https://github.com/KiriKira/Homura) | SIM 网关端 | 装在插有 SIM 卡的备用机上，负责接管蜂窝通信 |
| [SoulGem](https://github.com/KiriKira/SoulGem) | 自建服务端 | Go + PostgreSQL 中枢，负责配对、消息同步与通话信令 |
| [GriefSeed](https://github.com/KiriKira/GriefSeed) | 无服务器节点 | 跑在 Cloudflare Workers 上的单人节点，附带 TURN 中继 |
| [Kyubey](https://github.com/KiriKira/Kyubey) | 推送唤醒中继 | 无状态 FCM 中继，让服务端无需 Firebase 密钥也能唤醒主机 |

## 🚀 快速上手

1. **准备服务端与网关：** 先部署 [SoulGem](https://github.com/KiriKira/SoulGem)（或 [GriefSeed](https://github.com/KiriKira/GriefSeed)），并在备用机上配对好 [Homura](https://github.com/KiriKira/Homura)。
2. **安装并配对：** 从 [Releases](https://github.com/KiriKira/Madoka/releases) 下载 APK 安装，打开「设置」，填入服务器地址和 `client` 角色的一次性配对码（局域网或 Tailscale 内网可直接用 `http://`）。
3. **开始使用：** 配对后自动开启后台同步与来电监听；麦克风权限仅在真正拨打或接听时才会申请。

## 🔧 自行构建与发布

需要 JDK 17 与 Android SDK Platform 35。包名 `com.callagent.host`，签名与历史版本一致时可直接覆盖升级或回退。

```sh
./gradlew :app:assembleDebug :app:testDebugUnitTest --no-daemon --max-workers=2
```

启用推送唤醒：把你的 Firebase 项目的 `google-services.json` 放进 `app/`，并设环境变量 `MAHOGSM_PUSH_RELAY_URL` 为你部署的 [Kyubey](https://github.com/KiriKira/Kyubey) 地址（须为 `https://`）。两者都不设也能构建，只是没有推送唤醒，会退回长连接。

更多文档：[WebRTC 通话实现](docs/webrtc-implementation.md) · [多主机协同](docs/multi-host.md) · [多服务器节点](docs/mesh-nodes.md) · [短信备份与恢复](docs/sms-backup.md) · [后台运行与权限说明](docs/android-ui-and-background.md)

## 📄 许可证

[MIT](LICENSE)
