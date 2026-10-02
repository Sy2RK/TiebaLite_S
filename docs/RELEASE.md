# Release APK 构建与发布

工作流：Actions → Release APK。

| 触发方式 | 结果 |
| --- | --- |
| 提交到 4.0-dev | 自动运行单元测试、构建 Release APK，并上传 Artifacts |
| Run workflow | 为选定分支或标签执行同样的构建 |
| 推送 v* 标签 | 构建完成后自动创建或更新该标签的 GitHub Release |

在成功运行的 Artifacts 中下载 release-apk-*，解压后安装 APK。
包中还包含 SHA-256 校验文件、签名证书信息与版本元数据。
release-mapping-* 保存 R8 混淆映射，便于排查该版本的崩溃。
Artifacts 保留 90 天；标签发布的文件也保存在 GitHub Releases。

## 签名

自动 Release 构建必须使用 Fork 专用的固定发布密钥。
工作流会在构建前校验 Keystore 证书，并在构建后核对 APK 签名指纹。
密钥配置缺失、不完整或证书不匹配时，构建会明确报错。

预期证书 SHA-256：

    07a7a780ae0a089543d516a2ada2ff78a6a1a0364f7e1e6eae8582894359196e

长期保留同一份发布密钥，后续版本才能覆盖安装。
本地 Gradle 的原有签名逻辑不变；上述限制只用于自动 Release 工作流。

在仓库 Settings → Secrets and variables → Actions 配置：

| 类型 | 名称 | 内容 |
| --- | --- | --- |
| Secret | KEYSTORE | Keystore 文件的 Base64 内容 |
| Secret | RELEASESTOREPASSWORD | Keystore 密码 |
| Secret | RELEASEKEYPASSWORD | 私钥密码 |
| Variable | RELEASE_KEY_ALIAS | 私钥别名 |

这四项需一起配置；不完整的配置会明确报错。
构建时自动生成临时 release.keystore 与 keystore.properties，结束后清理。
无需设置 RELEASE_KEYSTORE，工作流使用固定的临时文件名。
私钥和密码不能提交到仓库。

## 版本

版本信息来自 application.properties。
修改 versionName、versionCode 和预发布字段后再推送版本标签。
isPreRelease=true 时，GitHub Release 标记为预发布。
CI 生成的版本名还会附带提交 SHA 的前七位。

例如，将目标提交标记并推送：

    git tag v4.0.0-beta.2
    git push origin v4.0.0-beta.2
