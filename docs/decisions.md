# decisions.md

Bu doküman, proje geliştirme sürecinde alınan temel mimari ve teknik kararları kayıt altına almak amacıyla hazırlanmıştır. Yeni kararlar alındıkça ilgili başlıklar güncellenmeli ve değişikliklerin gerekçeleri eklenmelidir.

---

## Dependency Injection

- **Tercih:** Hilt
- **Karar Tarihi:**
- **Alternatif:** Koin
- **Not:** Projede bağımlılık yönetimi için Hilt tercih edilmiştir. Bu yapı, Android ekosistemiyle uyumlu olması ve ölçeklenebilir bir mimari sunması nedeniyle benimsenmiştir.

---

## Navigation

- **Tercih:** Jetpack Compose Navigation
- **Karar Tarihi:**
- **Alternatifler:** Voyager, Decompose

**Gerekçe**

- Compose ile doğal uyumluluk sağlaması
- Route tabanlı ekran geçişlerini desteklemesi
- Resmi Android çözümü olması

---

## Screen Architecture

- **Tercih:** MVI (Model - View - Intent)
- **Karar Tarihi:**
- **Alternatif:** MVVM

**Gerekçe**

- Tek yönlü veri akışını desteklemesi
- State yönetimini daha öngörülebilir hale getirmesi
- Her ekran için ortak bir geliştirme standardı oluşturması
- Test edilebilirliği artırması

**Referans**

`docs/architecture/mvi-overview.md`

---

## Annotation Processing

- **Tercih:** KSP (Kotlin Symbol Processing)
- **Karar Tarihi:**
- **Alternatif:** KAPT

**Gerekçe**

- Kotlin'in güncel sürümleriyle uyumlu çalışması
- Daha hızlı derleme süreleri sunması
- Modern Android projeleri için önerilen yaklaşım olması

---

## UI Framework

- **Tercih:** Jetpack Compose
- **Karar Tarihi:**
- **Alternatif:** XML View System

**Gerekçe**

- Declarative UI geliştirme modeli
- Daha okunabilir ekran yapıları
- State yönetimiyle kolay entegrasyon sağlaması

---

## Theme Management

- **Tercih:** Material 3 Theme
- **Karar Tarihi:**

**Gerekçe**

- Material Design standartlarını takip etmek
- Açık ve koyu tema desteğini ortak tema dosyaları üzerinden yönetebilmek
- Renk ve tipografi kullanımını merkezi hale getirmek

---

## Genel Kurallar

Projede alınacak yeni teknik kararlar aşağıdaki bilgilerle birlikte bu dosyaya eklenmelidir.

- Seçilen teknoloji veya yaklaşım
- Karar tarihi
- Değerlendirilen alternatifler
- Tercih edilme nedeni
- İlgili doküman veya referans (varsa)