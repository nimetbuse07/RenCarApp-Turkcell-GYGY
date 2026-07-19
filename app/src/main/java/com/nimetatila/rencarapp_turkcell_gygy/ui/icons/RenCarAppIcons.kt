package com.nimetatila.rencarapp_turkcell_gygy.ui.icons

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.graphics.vector.addPathNodes
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Rencar ikon seti.
 *
 * Material Icons bağımlılığı eklemeden, ekranların ihtiyaç duyduğu glyph'leri
 * 24x24 viewport'lu [ImageVector] olarak tanımlar. Path'in dolgu rengi önemsizdir;
 * `Icon(...)` composable'ı `tint` ile üzerine yazar. Bu yüzden tüm path'ler
 * [Color.Black] ile doldurulur ve renk daima çağrı tarafında temadan okunur.
 */
object RenCarAppIcons {

    // ─────────────────────────────────────────────────────────────────────────
    // NAVİGASYON
    // ─────────────────────────────────────────────────────────────────────────

    /** Bottom nav — Harita sekmesi. */
    val Map: ImageVector by lazy {
        rencarIcon(
            name     = "Map",
            pathData = "M20.5,3l-0.16,0.03L15,5.1L9,3L3.36,4.9C3.15,4.97 3,5.15 3,5.38V20.5" +
                    "c0,0.28 0.22,0.5 0.5,0.5l0.16-0.03L9,18.9l6,2.1l5.64-1.9" +
                    "C20.85,19.03 21,18.85 21,18.62V3.5C21,3.22 20.78,3 20.5,3z" +
                    "M15,19l-6-2.11V5l6,2.11V19z",
        )
    }

    /** Bottom nav — Geçmiş sekmesi (saat ikonu). */
    val History: ImageVector by lazy {
        rencarIcon(
            name     = "History",
            pathData = "M13,3c-4.97,0-9,4.03-9,9H1l3.89,3.89 0.07,0.14L9,12H6" +
                    "c0-3.87 3.13-7 7-7s7,3.13 7,7-3.13,7-7,7" +
                    "c-1.93,0-3.68-0.79-4.94-2.06l-1.42,1.42C8.27,19.99 10.51,21 13,21" +
                    "c4.97,0 9-4.03 9-9s-4.03-9-9-9z" +
                    "M12,8v5l4.28,2.54 0.72-1.21-3.5-2.08V8H12z",
        )
    }

    /** Bottom nav — Cüzdan sekmesi (kart ikonu). */
    val Wallet: ImageVector by lazy {
        rencarIcon(
            name     = "Wallet",
            pathData = "M20,4H4C2.89,4 2,4.89 2,6V18c0,1.11 0.89,2 2,2H20" +
                    "c1.11,0 2-0.89 2-2V6C22,4.89 21.11,4 20,4z" +
                    "M20,18H4V12H20V18z" +
                    "M20,8H4V6H20V8z",
        )
    }

    /** Bottom nav — Profil sekmesi (kişi ikonu). */
    val Profile: ImageVector by lazy {
        rencarIcon(
            name     = "Profile",
            pathData = "M12,12c2.21,0 4-1.79 4-4s-1.79-4-4-4-4,1.79-4,4 1.79,4 4,4z" +
                    "M12,14c-2.67,0-8,1.34-8,4v2H20v-2C20,15.34 14.67,14 12,14z",
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // GEZİNME
    // ─────────────────────────────────────────────────────────────────────────

    /** TopBar geri butonu (sol ok). */
    val ArrowBack: ImageVector by lazy {
        rencarIcon(
            name     = "ArrowBack",
            pathData = "M20,11H7.83l5.59-5.59L12,4l-8,8 8,8 1.41-1.41L7.83,13H20V11z",
        )
    }

    /** Filtre ikonu (harita ve liste başlığı). */
    val Filter: ImageVector by lazy {
        rencarIcon(
            name     = "Filter",
            pathData = "M4.25,5.61C6.27,8.2 10,13 10,13V19c0,0.55 0.45,1 1,1H13" +
                    "c0.55,0 1-0.45 1-1V13c0,0 3.72-4.8 5.74-7.39" +
                    "C20.25,4.95 19.78,4 18.95,4H5.04C4.21,4 3.74,4.95 4.25,5.61z",
        )
    }

    /** Düzenle / kalem ikonu (profil). */
    val Edit: ImageVector by lazy {
        rencarIcon(
            name     = "Edit",
            pathData = "M3,17.25V21H6.75L17.81,9.94l-3.75-3.75L3,17.25z" +
                    "M20.71,7.04c0.39-0.39 0.39-1.02 0-1.41l-2.34-2.34" +
                    "c-0.39-0.39-1.02-0.39-1.41,0l-1.83,1.83 3.75,3.75 1.83-1.83z",
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ARAÇ / KİRALAMA
    // ─────────────────────────────────────────────────────────────────────────

    /** Araba ikonu — splash logo ve harita marker içi. */
    val Car: ImageVector by lazy {
        rencarIcon(
            name     = "Car",
            pathData = "M18.92,6.01C18.72,5.42 18.16,5 17.5,5h-11C5.84,5 5.29,5.42 5.08,6.01" +
                    "L3,12V20c0,0.55 0.45,1 1,1H5c0.55,0 1-0.45 1-1V19H18v1" +
                    "c0,0.55 0.45,1 1,1H20c0.55,0 1-0.45 1-1V12L18.92,6.01z" +
                    "M6.5,16C5.67,16 5,15.33 5,14.5S5.67,13 6.5,13 8,13.67 8,14.5 7.33,16 6.5,16z" +
                    "M17.5,16c-0.83,0-1.5-0.67-1.5-1.5S16.67,13 17.5,13 19,13.67 19,14.5 18.33,16 17.5,16z" +
                    "M5,11l1.5-4.5H17.5L19,11H5z",
        )
    }

    /** Kilit — kapalı (araç detay "Kilidi Aç" butonu). */
    val Lock: ImageVector by lazy {
        rencarIcon(
            name     = "Lock",
            pathData = "M18,8H17V6c0-2.76-2.24-5-5-5S7,3.24 7,6V8H6" +
                    "c-1.1,0-2,0.9-2,2V20c0,1.1 0.9,2 2,2H18" +
                    "c1.1,0 2-0.9 2-2V10C20,8.9 19.1,8 18,8z" +
                    "M12,17c-1.1,0-2-0.9-2-2s0.9-2 2-2 2,0.9 2,2-0.9,2-2,2z" +
                    "M15.1,8H8.9V6c0-1.71 1.39-3.1 3.1-3.1" +
                    "c1.71,0 3.1,1.39 3.1,3.1V8z",
        )
    }

    /** Kilit açık (aktif kiralama "Kilitle / Aç" butonu). */
    val LockOpen: ImageVector by lazy {
        rencarIcon(
            name     = "LockOpen",
            pathData = "M12,17c1.1,0 2-0.9 2-2s-0.9-2-2-2-2,0.9-2,2 0.9,2 2,2z" +
                    "M18,8H17V6c0-2.76-2.24-5-5-5-2.28,0-4.27,1.54-4.84,3.75" +
                    "l1.94,0.46C9.44,3.86 10.63,3 12,3c1.65,0 3,1.35 3,3V8H6" +
                    "c-1.1,0-2,0.9-2,2V20c0,1.1 0.9,2 2,2H18" +
                    "c1.1,0 2-0.9 2-2V10C20,8.9 19.1,8 18,8z" +
                    "M18,20H6V10H18V20z",
        )
    }

    /** Yakıt göstergesi ikonu (araç detay). */
    val Fuel: ImageVector by lazy {
        rencarIcon(
            name     = "Fuel",
            pathData = "M19.77,7.23l0.01-0.01-3.72-3.72L15,4.56l2.11,2.11" +
                    "c-0.94,0.36-1.61,1.26-1.61,2.33 0,1.38 1.12,2.5 2.5,2.5 0.36,0 0.69-0.08 1-0.21" +
                    "V19.5c0,0.28-0.22,0.5-0.5,0.5s-0.5-0.22-0.5-0.5V14" +
                    "c0-1.1-0.9-2-2-2H15V5c0-1.1-0.9-2-2-2H5C3.9,3 3,3.9 3,5V21H15V13.5H16.5V19.5" +
                    "c0,1.1 0.9,2 2,2s2-0.9 2-2V9C20.5,8.31 20.21,7.68 19.77,7.23z" +
                    "M13,11H5V5H13V11z" +
                    "M18,10c-0.55,0-1-0.45-1-1s0.45-1 1-1 1,0.45 1,1-0.45,1-1,1z",
        )
    }

    /** Vites ikonu (araç detay). */
    val Transmission: ImageVector by lazy {
        rencarIcon(
            name     = "Transmission",
            pathData = "M4,4H8V8H4V4z" +
                    "M10,4H14V8H10V4z" +
                    "M16,4H20V8H16V4z" +
                    "M4,16H8V20H4V16z" +
                    "M10,16H14V20H10V16z" +
                    "M6,8V16H8V8z" +
                    "M12,8V12H14V8z" +
                    "M12,12H18V14H12z",
        )
    }

    /** Koltuk / kişi kapasitesi ikonu (araç detay). */
    val Seat: ImageVector by lazy {
        rencarIcon(
            name     = "Seat",
            pathData = "M4,18V11.5C4,9.57 5.57,8 7.5,8S11,9.57 11,11.5V15H13V11.5" +
                    "C13,8.46 10.54,6 7.5,6S2,8.46 2,11.5V18H0V20H8V18H4z" +
                    "M22,18H18V11.5C18,9.57 16.43,8 14.5,8c-0.36,0-0.71,0.06-1.04,0.16" +
                    "C13.79,8.75 14,9.36 14,10c0,0.1-0.01,0.2-0.02,0.3" +
                    "C14.16,10.11 14.32,10 14.5,10c0.83,0 1.5,0.67 1.5,1.5V20H24V18H22z",
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // KONUM / HARİTA
    // ─────────────────────────────────────────────────────────────────────────

    /** Konum pin ikonu (harita, "En Yakın Aracı Bul" butonu). */
    val LocationPin: ImageVector by lazy {
        rencarIcon(
            name     = "LocationPin",
            pathData = "M12,2C8.13,2 5,5.13 5,9c0,5.25 7,13 7,13s7-7.75 7-13" +
                    "C19,5.13 15.87,2 12,2z" +
                    "M12,11.5c-1.38,0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5,1.12 2.5,2.5-1.12,2.5-2.5,2.5z",
        )
    }

    /** Mevcut konuma git / pusula (harita sağ üst köşe). */
    val MyLocation: ImageVector by lazy {
        rencarIcon(
            name     = "MyLocation",
            pathData = "M12,8c-2.21,0-4,1.79-4,4s1.79,4 4,4 4-1.79 4-4-1.79-4-4-4z" +
                    "M20.94,11C20.48,6.83 17.17,3.52 13,3.06V1H11V3.06" +
                    "C6.83,3.52 3.52,6.83 3.06,11H1V13H3.06" +
                    "C3.52,17.17 6.83,20.48 11,20.94V23H13V20.94" +
                    "C17.17,20.48 20.48,17.17 20.94,13H23V11H20.94z" +
                    "M12,19c-3.87,0-7-3.13-7-7s3.13-7 7-7 7,3.13 7,7-3.13,7-7,7z",
        )
    }

    /** Menzil / mesafe ikonu (araç detay). */
    val Range: ImageVector by lazy {
        rencarIcon(
            name     = "Range",
            pathData = "M12,2C8.13,2 5,5.13 5,9c0,5.25 7,13 7,13s7-7.75 7-13" +
                    "C19,5.13 15.87,2 12,2z" +
                    "M12,11.5c-1.38,0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5,1.12 2.5,2.5-1.12,2.5-2.5,2.5z",
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // İLETİŞİM / KİMLİK
    // ─────────────────────────────────────────────────────────────────────────

    /** SMS / mesaj gönder ikonu (giriş "Kod Gönder" butonu). */
    val Sms: ImageVector by lazy {
        rencarIcon(
            name     = "Sms",
            pathData = "M20,2H4C2.9,2 2,2.9 2,4V22l4-4H20c1.1,0 2-0.9 2-2V4" +
                    "C22,2.9 21.1,2 20,2z" +
                    "M9,11H7V9H9V11z" +
                    "M13,11H11V9H13V11z" +
                    "M17,11H15V9H17V11z",
        )
    }

    /** Telefon / mobil ikonu (OTP doğrulama başlığı). */
    val PhoneVerify: ImageVector by lazy {
        rencarIcon(
            name     = "PhoneVerify",
            pathData = "M17,1.01L7,1C5.9,1 5,1.9 5,3V21c0,1.1 0.9,2 2,2H17" +
                    "c1.1,0 2-0.9 2-2V3C19,1.9 18.1,1.01 17,1.01z" +
                    "M17,19H7V5H17V19z" +
                    "M12,17.5c0.83,0 1.5-0.67 1.5-1.5S12.83,14.5 12,14.5 10.5,15.17 10.5,16 11.17,17.5 12,17.5z",
        )
    }

    /** Kamera ikonu (araç teslim fotoğrafı). */
    val Camera: ImageVector by lazy {
        rencarIcon(
            name     = "Camera",
            pathData = "M12,15.5c1.93,0 3.5-1.57 3.5-3.5S13.93,8.5 12,8.5 8.5,10.07 8.5,12 10.07,15.5 12,15.5z" +
                    "M9,2L7.17,4H4C2.9,4 2,4.9 2,6V18c0,1.1 0.9,2 2,2H20" +
                    "c1.1,0 2-0.9 2-2V6c0-1.1-0.9-2-2-2H16.83L15,2H9z" +
                    "M12,17c-2.76,0-5-2.24-5-5s2.24-5 5-5 5,2.24 5,5-2.24,5-5,5z",
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ÖDEME
    // ─────────────────────────────────────────────────────────────────────────

    /** Kredi kartı ikonu (ödeme, cüzdan). */
    val CreditCard: ImageVector by lazy {
        rencarIcon(
            name     = "CreditCard",
            pathData = "M20,4H4C2.89,4 2,4.89 2,6V18c0,1.11 0.89,2 2,2H20" +
                    "c1.11,0 2-0.89 2-2V6C22,4.89 21.11,4 20,4z" +
                    "M20,18H4V12H20V18z" +
                    "M20,8H4V6H20V8z",
        )
    }

    /** Bakiye yükle / artı ikonu (cüzdan). */
    val AddBalance: ImageVector by lazy {
        rencarIcon(
            name     = "AddBalance",
            pathData = "M19,3H5C3.89,3 3,3.9 3,5V19c0,1.1 0.89,2 2,2H19" +
                    "c1.1,0 2-0.9 2-2V5C21,3.9 20.1,3 19,3z" +
                    "M17,13H13V17H11V13H7V11H11V7H13V11H17V13z",
        )
    }

    /** Onay tik ikonu (yolculuk tamamlandı ekranı). */
    val CheckCircle: ImageVector by lazy {
        rencarIcon(
            name     = "CheckCircle",
            pathData = "M12,2C6.48,2 2,6.48 2,12s4.48,10 10,10 10-4.48 10-10S17.52,2 12,2z" +
                    "M10,17l-5-5 1.41-1.41L10,14.17l7.59-7.59L19,8l-9,9z",
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // DURUM / GERİ BİLDİRİM
    // ─────────────────────────────────────────────────────────────────────────

    /** Uyarı üçgeni ikonu (araç teslim fotoğrafı uyarısı). */
    val Warning: ImageVector by lazy {
        rencarIcon(
            name     = "Warning",
            pathData = "M1,21H23L12,2L1,21z" +
                    "M13,18H11V16H13V18z" +
                    "M13,14H11V10H13V14z",
        )
    }

    /** Bilgi (i) ikonu (OTP ve ehliyet açıklamaları). */
    val Info: ImageVector by lazy {
        rencarIcon(
            name     = "Info",
            pathData = "M12,2C6.48,2 2,6.48 2,12s4.48,10 10,10 10-4.48 10-10S17.52,2 12,2z" +
                    "M13,17H11V11H13V17z" +
                    "M13,9H11V7H13V9z",
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PROFİL / AYARLAR
    // ─────────────────────────────────────────────────────────────────────────

    /** Ehliyet kalkan ikonu (profil, ehliyet doğrulama). */
    val Shield: ImageVector by lazy {
        rencarIcon(
            name     = "Shield",
            pathData = "M12,1L3,5V11c0,5.55 3.84,10.74 9,12 5.16-1.26 9-6.45 9-12V5L12,1z" +
                    "M10,17l-4-4 1.41-1.41L10,14.17l6.59-6.59L18,9l-8,8z",
        )
    }

    /** Ayarlar dişli ikonu (profil). */
    val Settings: ImageVector by lazy {
        rencarIcon(
            name     = "Settings",
            pathData = "M19.14,12.94c0.04-0.3 0.06-0.61 0.06-0.94s-0.02-0.64-0.07-0.94" +
                    "l2.03-1.58c0.18-0.14 0.23-0.41 0.12-0.61l-1.92-3.32" +
                    "c-0.12-0.22-0.37-0.29-0.59-0.22l-2.39,0.96" +
                    "c-0.5-0.38-1.03-0.7-1.62-0.94L14.4,2.81" +
                    "c-0.04-0.24-0.24-0.41-0.48-0.41H10.08" +
                    "c-0.24,0-0.43,0.17-0.47,0.41L9.25,5.35" +
                    "C8.66,5.59 8.12,5.92 7.63,6.29L5.24,5.33" +
                    "c-0.22-0.08-0.47,0-0.59,0.22L2.74,8.87" +
                    "C2.62,9.08 2.66,9.34 2.86,9.48l2.03,1.58" +
                    "C4.84,11.36 4.8,11.69 4.8,12s0.02,0.64 0.07,0.94" +
                    "l-2.03,1.58c-0.18,0.14-0.23,0.41-0.12,0.61l1.92,3.32" +
                    "c0.12,0.22 0.37,0.29 0.59,0.22l2.39-0.96" +
                    "c0.5,0.38 1.03,0.7 1.62,0.94l0.36,2.54" +
                    "c0.05,0.24 0.24,0.41 0.48,0.41H13.92" +
                    "c0.24,0 0.44-0.17 0.47-0.41l0.36-2.54" +
                    "c0.59-0.24 1.13-0.56 1.62-0.94l2.39,0.96" +
                    "c0.22,0.08 0.47,0 0.59-0.22l1.92-3.32" +
                    "c0.12-0.22 0.07-0.47-0.12-0.61L19.14,12.94z" +
                    "M12,15.6c-1.98,0-3.6-1.62-3.6-3.6s1.62-3.6 3.6-3.6 3.6,1.62 3.6,3.6-1.62,3.6-3.6,3.6z",
        )
    }

    /** Yardım & soru işareti ikonu (profil). */
    val Help: ImageVector by lazy {
        rencarIcon(
            name     = "Help",
            pathData = "M12,2C6.48,2 2,6.48 2,12s4.48,10 10,10 10-4.48 10-10S17.52,2 12,2z" +
                    "M13,19H11V17H13V19z" +
                    "M15.07,11.25l-0.9,0.92C13.45,12.9 13,13.5 13,15H11V14.5" +
                    "c0-1.1 0.45-2.1 1.17-2.83l1.24-1.26" +
                    "c0.37-0.36 0.59-0.86 0.59-1.41 0-1.1-0.9-2-2-2s-2,0.9-2,2H8" +
                    "c0-2.21 1.79-4 4-4s4,1.79 4,4" +
                    "C16,11.48 15.62,12.19 15.07,11.25z",
        )
    }

    /** Davet et / paylaş ikonu (profil "Davet et · ₺50 kazan"). */
    val Share: ImageVector by lazy {
        rencarIcon(
            name     = "Share",
            pathData = "M18,16.08c-0.76,0-1.44,0.3-1.96,0.77L8.91,12.7" +
                    "C8.96,12.47 9,12.24 9,12s-0.04-0.47-0.09-0.7l7.05-4.11" +
                    "c0.54,0.5 1.25,0.81 2.04,0.81 1.66,0 3-1.34 3-3s-1.34-3-3-3-3,1.34-3,3" +
                    "c0,0.24 0.04,0.47 0.09,0.7L8.04,9.81C7.5,9.31 6.79,9 6,9" +
                    "c-1.66,0-3,1.34-3,3s1.34,3 3,3c0.79,0 1.5-0.31 2.04-0.81" +
                    "l7.12,4.16c-0.05,0.21-0.08,0.43-0.08,0.65 0,1.61 1.31,2.92 2.92,2.92" +
                    "s2.92-1.31 2.92-2.92C20.92,17.39 19.61,16.08 18,16.08z",
        )
    }

    /** Çıkış yap ikonu (profil). */
    val Logout: ImageVector by lazy {
        rencarIcon(
            name     = "Logout",
            pathData = "M17,7l-1.41,1.41L18.17,11H8V13H18.17l-2.58,2.58L17,17l5-5L17,7z" +
                    "M4,5H12V3H4C2.9,3 2,3.9 2,5V19c0,1.1 0.9,2 2,2H12V19H4V5z",
        )
    }

    /** Sağa ok / chevron (profil liste item). */
    val ChevronRight: ImageVector by lazy {
        rencarIcon(
            name     = "ChevronRight",
            pathData = "M10,6L8.59,7.41 13.17,12l-4.58,4.59L10,18l6-6L10,6z",
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ARAMA
    // ─────────────────────────────────────────────────────────────────────────

    /** Arama büyüteci (harita search bar). */
    val Search: ImageVector by lazy {
        rencarIcon(
            name     = "Search",
            pathData = "M15.5,14H14.71l-0.27-0.27C15.41,12.59 16,11.11 16,9.5" +
                    "C16,5.91 13.09,3 9.5,3S3,5.91 3,9.5 5.91,16 9.5,16" +
                    "c1.61,0 3.09-0.59 4.23-1.57L14,14.71V15.5l4,3.99L19.49,18l-3.99-4z" +
                    "M9.5,14C7.01,14 5,11.99 5,9.5S7.01,5 9.5,5 14,7.01 14,9.5 11.99,14 9.5,14z",
        )
    }

    // ─────────────────────────────────────────────────────────────────────────
    // EHLİYET DOĞRULAMA
    // ─────────────────────────────────────────────────────────────────────────

    /** Yükle / kamera (ehliyet arka yüz). */
    val Upload: ImageVector by lazy {
        rencarIcon(
            name     = "Upload",
            pathData = "M19.35,10.04C18.67,6.59 15.64,4 12,4 9.11,4 6.6,5.64 5.35,8.04" +
                    "C2.34,8.36 0,10.91 0,14c0,3.31 2.69,6 6,6H19c2.76,0 5-2.24 5-5" +
                    "C24,11.36 21.95,9.22 19.35,10.04z" +
                    "M14,13V17H10V13H7L12,8l5,5H14z",
        )
    }

    /** Onay tik (ehliyet "Yüklendi" badge içindeki küçük tik). */
    val Check: ImageVector by lazy {
        rencarIcon(
            name     = "Check",
            pathData = "M9,16.17L4.83,12l-1.42,1.41L9,19 21,7l-1.41-1.41L9,16.17z",
        )
    }

    /** Şifre görünürlüğü - açık göz. */
    val Visibility: ImageVector by lazy {
        rencarIcon(
            name     = "Visibility",
            pathData = "M12,4.5C7,4.5 2.73,7.61 1,12c1.73,4.39 6,7.5 11,7.5s9.27,-3.11 11,-7.5c-1.73,-4.39 -6,-7.5 -11,-7.5zM12,17c-2.76,0 -5,-2.24 -5,-5s2.24,-5 5,-5 5,2.24 5,5 -2.24,5 -5,5zM12,9c-1.66,0 -3,1.34 -3,3s1.34,3 3,3 3,-1.34 3,-3 -1.34,-3 -3,-3z",
        )
    }

    /** Şifre gizliliği - kapalı göz. */
    val VisibilityOff: ImageVector by lazy {
        rencarIcon(
            name     = "VisibilityOff",
            pathData = "M12,7c2.76,0 5,2.24 5,5c0,0.65 -0.13,1.26 -0.36,1.83l2.92,2.92c1.51,-1.26 2.7,-2.89 3.43,-4.75c-1.73,-4.39 -6,-7.5 -11,-7.5c-1.4,0 -2.74,0.25 -3.98,0.7l2.16,2.16C10.74,7.13 11.35,7 12,7zM2,4.27l2.28,2.28l0.46,0.46C3.08,8.3 1.78,10.02 1,12c1.73,4.39 6,7.5 11,7.5c1.55,0 3.03,-0.3 4.38,-0.84l0.42,0.42L19.73,22L21,20.73L3.27,3L2,4.27zM7.53,9.8l1.55,1.55c-0.05,0.21 -0.08,0.43 -0.08,0.65c0,1.66 1.34,3 3,3c0.22,0 0.44,-0.03 0.65,-0.08l1.55,1.55c-0.67,0.33 -1.41,0.53 -2.2,0.53c-2.76,0 -5,-2.24 -5,-5c0,-0.79 0.2,-1.53 0.53,-2.2zM11.84,9.02l3.15,3.15l0.02,-0.15c0,-1.66 -1.34,-3 -3,-3l-0.17,0.01z",
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// BUILDER HELPER
// ─────────────────────────────────────────────────────────────────────────────

/**
 * 24×24 dp viewport'lu [ImageVector] oluşturur.
 * Tüm Rencar ikonları bu helper üzerinden tanımlanır.
 */
private fun rencarIcon(name: String, pathData: String): ImageVector =
    ImageVector.Builder(
        name = name,
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).addPath(
        pathData = PathParser().parsePathString(pathData).toNodes(),
        fill = SolidColor(Color.Black),
    ).build()