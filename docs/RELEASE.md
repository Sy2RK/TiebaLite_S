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

保持项目原有签名行为：没有配置发布密钥时，Release 构建使用默认调试签名。
构建摘要、signatures.txt 和标签 Release 描述会注明实际签名。
默认调试签名由构建环境生成，不能保证不同构建之间能够覆盖安装。
需要持续升级安装时，配置并长期保留同一份发布密钥。

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
