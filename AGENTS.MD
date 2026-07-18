# RenCarApp - Araç Kiralama Mobil Uygulaması

> Bu repository, **RenCarApp** araç kiralama mobil uygulamasının kaynak kodlarını içermektedir. Bu **AGENTS.md** dosyası, projede görev alan tüm geliştiriciler ve yapay zekâ destekli araçlar tarafından uyulması gereken temel geliştirme kurallarını tanımlar.

---

# 1) TEKNOLOJİ YAPISI

- **Mobil:** Android Jetpack Compose (Kotlin)
- **Backend:** Ayrı ekip tarafından geliştirilen RESTful API
- **Mimari:** MVI (Model - View - Intent)
- **Bağımlılık Yönetimi:** Hilt
- **Navigasyon:** Jetpack Compose Navigation

---

# 2) GELİŞTİRME PRENSİPLERİ

## 2.1) DOSYA DEĞİŞİKLİĞİ SINIRI

Her geliştirme adımı mümkün olduğunca küçük ve yönetilebilir olmalıdır.

- Tek seferde en fazla **5 ilişkili dosya** üzerinde işlem yapılmalıdır.
- Daha kapsamlı geliştirmeler birden fazla aşamaya bölünmelidir.
- Bu sınırın aşılması gerekiyorsa önce kullanıcı onayı alınmalıdır.

---

## 2.2) EKSİK BİLGİYİ TAHMİN ETME

Kod yazarken eksik bilgi, tanımsız yapı veya belirsiz bir gereksinim ile karşılaşılırsa:

- Varsayım yapılmamalıdır.
- Eksik bilgiler üretilmemelidir.
- Geliştirme durdurulmalı ve gerekli bilgiler kullanıcıdan istenmelidir.

---

## 2.3) PLANLAMA ZORUNLULUĞU

Kod geliştirmeye başlamadan önce aşağıdaki plan hazırlanmalıdır.

- Değiştirilecek dosyalar
- Oluşturulacak yeni dosyalar
- Silinecek dosyalar (varsa)
- Her dosya için kısa açıklama
- Yeni bağımlılık gerekiyorsa kullanılacak kütüphane ve gerekçesi

Plan kullanıcıya sunulmalı, onay alınmadan implementasyona başlanmamalıdır.

---

## 2.4) MİMARİ KARARLAR

Projeye ait teknik ve mimari kararlar **docs/decisions.md** dosyasında tutulur.

Yeni bir teknoloji seçildiğinde veya mevcut mimari kararlarda değişiklik yapıldığında ilgili doküman güncellenmelidir.

---

## 2.5) PROJE BAĞLAMI

Her yeni göreve başlamadan önce mevcut proje yapısı incelenmelidir.

Özellikle aşağıdaki dokümanlar referans alınmalıdır:

- `docs/decisions.md`

---

# 3) MİMARİ REFERANSLAR

Yeni bir ekran geliştirilmeden veya mevcut ekran güncellenmeden önce aşağıdaki mimari dokümanlar incelenmelidir.

- `docs/architecture/mvi-overview.md`
- `docs/architecture/mvi-contracts.md`
- `docs/architecture/mvi-viewmodel-rules.md`

MVI dışındaki teknik kararlar için:

- `docs/decisions.md`

Bu dokümanlarda belirtilen kurallar ihlal edilmemelidir.

---

# 4) ÇIKTI STANDARTLARI

Her planlama veya implementasyon sonunda aşağıdaki bilgiler sunulmalıdır.

## Dosya Dökümü

Aşağıdaki bilgiler verilmelidir.

- Oluşturulan dosyalar
- Güncellenen dosyalar
- Silinen dosyalar (varsa)
- Yapılan değişikliğin kısa açıklaması

---

## Test

Mümkün olduğu durumlarda ilgili geliştirme için kısa bir **Happy Path** test senaryosu hazırlanmalıdır.

---

## Dikkat Edilmesi Gerekenler

İlgili geliştirme sırasında karşılaşılabilecek yaygın hatalar ve öneriler maddeler halinde belirtilmelidir.

---

## Yazım Kuralları

- Resmi ve teknik bir dil kullanılmalıdır.
- Gereksiz açıklamalardan kaçınılmalıdır.
- Emoji kullanılmamalıdır.
- Mevcut proje mimarisi korunmalıdır.
- Gereksiz refactoring yapılmamalıdır.