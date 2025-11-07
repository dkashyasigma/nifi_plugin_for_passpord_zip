# 🔐 NiFi Custom Processor – Password-Protected ZIP Creator

<img width="781" height="389" alt="image" src="https://github.com/user-attachments/assets/abd49c22-0afd-48fe-b763-a63c27f485a4" />
<img width="848" height="388" alt="image" src="https://github.com/user-attachments/assets/29ae9509-f653-47a2-bdc7-bd8b9bfe2b29" />
<img width="776" height="394" alt="image" src="https://github.com/user-attachments/assets/454f0aff-243b-482b-883b-dae9f29e8f39" />
<img width="769" height="391" alt="image" src="https://github.com/user-attachments/assets/b08d4a03-2de0-476b-bbda-9f37dfb93127" />

A lightweight and secure **Apache NiFi custom processor** that generates **password-protected ZIP files** using the powerful **Zip4j** library.
This processor enables secure file packaging inside your NiFi dataflows without requiring external scripts or command-line tools.

## ✨ Features

✅ Creates **password-protected ZIP files**
✅ Supports **AES-128 / AES-256 encryption**
✅ Works with **large files**
✅ Pure Java implementation using **Zip4j**
✅ Fully compatible with **NiFi 1.x / 2.x**
✅ No external dependencies or Hadoop required
✅ Simple “drop-in” deployment via NiFi’s `/extensions` folder

---

## 📌 When to Use This Processor?

Use this processor whenever you need to:

* Protect sensitive files before transferring
* Apply AES encryption to ZIP archives
* Integrate secure file packaging into NiFi workflows
* Create automation pipelines without shell scripts

---

## 📂 Installation & Deployment

### ✅ 1. Build with Maven

```sh
cd nifi-zip_processor-bundle
mvn clean install -DskipTests
```

### ✅ 2. Locate NAR file

After build completes, find your NAR file at:

```
nifi-zip_processor-nar/target/*.nar
```

### ✅ 3. Deploy to NiFi

Copy the NAR file into:

```
<NIFI_HOME>/extensions
```

> ✅ Note: NiFi 1.x uses `/extensions`, NiFi 2.x uses `/extensions`.

### ✅ 4. Restart NiFi

On restart, NiFi automatically loads the new processor.

---

## 🛠️ How to Use in a Flow

1. Drag the processor into your NiFi canvas.
2. Open configuration.
3. Set:

   * **Input File Path / FlowFile Content**
   * **ZIP Password**
4. Connect downstream processors (Upload, Move, PutS3Object, PutAzureBlob, etc.).

---

## 🧩 Processor Capabilities

| Capability                   | Supported |
| ---------------------------- | --------- |
| ZIP creation                 | ✅         |
| Password protection          | ✅         |
| AES-128 / AES-256 encryption | ✅         |
| Multiple files               | ✅         |
| Streaming support            | ✅         |
| NiFi provenance tracking     | ✅         |
| FlowFile attributes          | ✅         |

---

## 📁 Project Structure

```
/nifi-zip_processor-bundle
   ├── nifi-zip_processor-processors/
   │      └── src/main/java/... (processor code)
   ├── nifi-zip_processor-nar/
   │      └── target/*.nar      (NiFi extension package)
   ├── pom.xml                  (root build file)
   ├── LICENSE
   └── README.md
```

---

## 🔧 Build Requirements

| Tool  | Version     |
| ----- | ----------- |
| Java  | 17+         |
| Maven | 3.8+        |
| NiFi  | 1.20+ / 2.x |
| Zip4j | 2.x         |


## 🔍 Keywords
Apache NiFi, NiFi Processor, Custom NiFi Processor, Password Protected Zip,
Zip4j, NiFi Extensions, NiFi Plugin, Encrypted Zip, AES Encryption, Java NiFi,
Data Pipeline Security, Secure File Packaging, NiFi Dataflow


---

## 📜 License

Licensed under the **Apache License 2.0**.
You are free to use, modify, and distribute this processor.

