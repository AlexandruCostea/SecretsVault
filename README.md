# SecretsVault

**Securely manage sensitive data with strong encryption, biometric authorization, and a privacy-focused architecture.**

SecretsVault is an Android application for managing private information, such as notes and login credentials. Designed with robust security at its core, the app leverages Android’s Keystore system, biometric authentication, and in-memory protection to ensure your secrets stay yours — and only yours.

---

## 🔐 Key Features

### Biometric-Backed Authorization

* **Every operation on sensitive data** (view, add, update, delete) requires biometric authentication (fingerprint, face, or device credentials). <br>
If authentication fails, decryption is blocked and access to the resource is denied.

### Secure Encryption with Android Keystore

* **Encryption keys** are securely stored and managed using the **Android Keystore**, ensuring they **never leave the secure hardware-backed environment**.
* Encryption and decryption operations are **only permitted after biometric authentication**, preventing any misuse even if the device is compromised.

### In-Memory Secure Data Representation

* Only **partial data (previews)** are kept in memory or shown in UI lists — secret titles and dates of the last update.
* Full secrets (e.g., login mail and password, note content) are decrypted **only at the moment of access**, reducing exposure in memory and improving runtime privacy.

### Periodic Re-Encryption Policy

* **Automatic key rotation** policy: secrets are **re-encrypted every 30 days** using a newly generated encryption key.
* This ensures long-term confidentiality, mitigating risks from compromised or stale keys.

### Simple and Secure UI

* Clean, intuitive Compose UI.
* Lightweight and performant app architecture.
* Every sensitive screen is protected by biometric gatekeeping.

---

## 🛡️ Security Guarantees

| Layer              | Mechanism                                                               |
| ------------------ | ----------------------------------------------------------------------- |
| Key Storage        | Android Keystore (hardware-backed, protected by biometric)              |
| Data Encryption    | AES encryption using Keystore-derived keys                              |
| Access Control     | Biometric prompt required for any cryptographic operation               |
| Memory Protection  | Only previews stored in memory; full secrets accessed only on demand    |
| Key Rotation       | Secrets are re-encrypted every 30 days with a fresh key                 |

---

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/AlexandruCostea/SecretsVault.git
```

### 2. Open in Android Studio

* Open the project folder in Android Studio.
* Let Gradle sync and ensure dependencies are resolved.

### 3. Run the App

* Use a device or emulator **with biometric authentication configured**.
* Run the app and begin managing secrets securely.

---

## 📄 License

This project is licensed under the MIT License. See the [LICENSE](https://github.com/AlexandruCostea/SecretsVault/blob/master/LICENSE) file for details.
