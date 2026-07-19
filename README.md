<div align="center">

# RenCarApp
Araç Paylaşım Uygulaması
"Yakındaki aracı bul, dakikalar içinde yola çık."

</div>

## Ekran Görüntüleri

<table>
  <!-- 1. Satır -->
  <tr>
    <td align="center" width="25%">
      <img width="250" src="https://github.com/user-attachments/assets/8810f57b-d691-4abc-b75f-ca4030e33536" />
    </td>
    <td align="center" width="25%">
      <img width="250" src="https://github.com/user-attachments/assets/0d7f118e-133a-4e5e-806b-cfcd3f74687e" />
    </td>
    <td align="center" width="25%">
      <img width="250" src="https://github.com/user-attachments/assets/bfea3c8c-30d5-4685-821f-865da62eee30" />
    </td>
    <td align="center" width="25%">
      <img width="250" src="https://github.com/user-attachments/assets/76f9c816-f3f4-4425-b50a-18dce9feebf5" />
    </td>
  </tr>
  
  <!-- 2. Satır -->
  <tr>
    <td align="center" width="25%">
      <img width="250" src="https://github.com/user-attachments/assets/bd11a2a5-41d5-4ac3-8871-04d4881e6361" />
    </td>
    <td align="center" width="25%">
      <img width="250" src="https://github.com/user-attachments/assets/0a77f460-a0c4-474b-be42-76b4eb027443" />
    </td>
    <td align="center" width="25%">
      <img width="250" alt="Ekran Resmi 2026-07-19 21 30 21" src="https://github.com/user-attachments/assets/9757999f-b392-44ef-a812-0867430d8b1a" />
    </td>
    <td align="center" width="25%">
      <img width="250" src="https://github.com/user-attachments/assets/d2541c13-d8de-4402-b708-55e5f774f674" />
    </td>
  </tr>
  
  <!-- 3. Satır -->
  <tr>
    <td align="center" width="25%">
      <img width="250" src="https://github.com/user-attachments/assets/7e12e7ca-f793-4cd9-84c4-ae9ab041eb6d" />
    </td>
    <td align="center" width="25%">
      <img width="250" src="https://github.com/user-attachments/assets/b80c2352-4293-4a86-b6ff-b5cbf8f14f9a" />
    </td>
    <td align="center" width="25%">
      <img width="250" src="https://github.com/user-attachments/assets/c274f2f8-4141-4c37-a2d2-61ec0ad453a6" />
    </td>
    <td align="center" width="25%">
      <img width="250" src="https://github.com/user-attachments/assets/fffdee4a-f040-4861-992f-f08c770b1144" />
    </td>
  </tr>
</table>

## Proje Hakkında
RenCar, kullanıcıların yakınlarındaki araçları harita üzerinde görüp saniyeler içinde rezerve edebildiği, uzaktan kilit açma ile aracı teslim alıp dakika bazlı ücretlendirmeyle kiralayabildiği bir araç paylaşım mobil uygulamasıdır. Uygulama, native Android üzerinde Jetpack Compose ve MVI (Model-View-Intent) mimarisi kullanılarak geliştirilmiştir. Bu proje GYGY5 Kotlin bitirme projesi kapsamında geliştirilmiştir.

## Özellikler
* Telefon numarası ve SMS OTP ile giriş/kayıt
* Harita üzerinde yakındaki araçları gerçek zamanlı görüntüleme
* Araç detay ekranı (yakıt seviyesi, menzil, vites tipi, koltuk sayısı, fiyatlandırma)
* Dakikalık, saatlik ve günlük kiralama planları
* Uzaktan rezervasyon
* Aktif kiralama takibi (geçen süre, mesafe, anlık ücret)
* Kiralama sonu ödeme özeti ve fatura kalemleri
* Cüzdan yönetimi (bakiye yükleme, kayıtlı kartlar, işlem geçmişi)
* Kiralama geçmişi ve aylık harcama özeti
* Ehliyet doğrulama (ön/arka yüz yükleme ve selfie doğrulama)
* Açık / Koyu tema desteği
* Türkçe arayüz, Material Design 3 bileşenleri

## Kullanılan Teknolojiler
* **Dil:** Kotlin
* **UI:** Jetpack Compose, Material Design 3
* **Mimari:** MVI (Model-View-Intent), Tek Yönlü Veri Akışı (UDF)
* **Bağımlılık Enjeksiyonu:** Dagger Hilt
* **Ağ Katmanı:** Retrofit2 ve OkHttp
* **Serileştirme:** Kotlinx Serialization
* **Yerel Depolama:** Jetpack DataStore Preferences
* **Navigasyon:** Jetpack Navigation Compose
* **Asenkron İşlemler:** Kotlin Coroutines ve Flow
* **Kimlik Doğrulama:** JWT (Access Token + Refresh Token)

## Mimari
Uygulama, her ekran için Contract -> ViewModel -> Screen üçlüsünü ve veri katmanında Api -> Dto -> Repository -> RepositoryImpl yapısını izleyen tutarlı bir MVI mimarisi kullanır.

Her ekran kendi Contract dosyasında State, Intent ve Effect tanımlarını taşır. ViewModel bu kontrata göre state'i yönetir ve Screen bileşeni yalnızca state'i gözlemleyip intent gönderir. Bu ayrım ekranların test edilebilirliğini kolaylaştırır.

## Başlangıç

### Gereksinimler
* Android Studio (Güncel sürüm)
* JDK 17+
* Min SDK: 24
* Kotlin sürümü: 2.0.21

### Kurulum
1. Depoyu klonlayın: `git clone https://github.com/nimetbuse07/RenCarApp-Turkcell-GYGY.git`
2. Android Studio ile açın ve Gradle senkronizasyonunun tamamlanmasını bekleyin.
3. Backend adresini NetworkModule veya local.properties üzerinden yapılandırın.
4. Uygulamayı bir emülatör veya fiziksel cihazda çalıştırın.

## API Entegrasyonu
* **Ağ Katmanı:** Retrofit2 ve OkHttp, Kotlinx Serialization converter kullanılmaktadır.
* **Kimlik Doğrulama:** JWT kullanılır. AuthInterceptor, korumalı isteklere token bilgisini otomatik olarak ekler.
* **Yerel Kalıcılık:** Jetpack DataStore Preferences aracılığıyla token ve tema tercihleri saklanır.

## Mimari Kararlar
Proje boyunca alınan mimari ve tasarım kararları `docs/decisions.md` dosyasında (Architectural Decision Records) kayıt altına alınmıştır.

## Katkıda Bulunanlar

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/nimetbuse07">
        <sub><b>nimetbuse07</b></sub>
      </a>
    </td>
  </tr>
</table>

<div align="center">
<sub>RenCarApp · Kotlin ve Jetpack Compose ile geliştirildi</sub>
</div>
