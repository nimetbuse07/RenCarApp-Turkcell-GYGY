# 00 · Color System — RenCarApp

Bu doküman RenCarApp uygulamasında kullanılacak renk tokenlarını tanımlar.
Uygulamadaki tüm renkler bu dosyada belirlenen kurallara göre kullanılmalıdır.

---

## Amaç

Renk kullanımını tek merkezden yönetmek ve uygulama genelinde görsel tutarlılığı korumaktır.

---

## Temel İlkeler

- Hex kodları ekran içerisinde doğrudan kullanılmamalıdır.
- Renkler yalnızca tema üzerinden okunmalıdır.
- Yeni bir renk gerektiğinde mevcut palette karşılığı araştırılmalı, zorunlu olmadıkça yeni renk eklenmemelidir.
- Light ve Dark tema aynı isimlendirmeyi kullanmalıdır.

---

## Ana Renk Paleti

### Primary

| Token | Açıklama |
|-------|----------|
| Blue50 | En açık mavi tonu |
| Blue100 | Açık container rengi |
| Blue500 | Dark tema primary |
| Blue600 | Light tema primary |
| Blue700 | Primary metin rengi |

---

### Neutral

Arka planlar, kartlar, metinler ve ayırıcı çizgiler için kullanılır.

| Token | Kullanım |
|-------|-----------|
| Neutral0 | Beyaz |
| Neutral100 | Sayfa arka planı |
| Neutral200 | Border |
| Neutral500 | İkincil metin |
| Neutral900 | Koyu yüzey |

---

### Semantic

Durum bildiren renklerdir.

- Success
- Error
- Warning

Bu renkler yalnızca ilgili anlamları için kullanılmalıdır.

---

## Material Theme Eşlemesi

Renkler aşağıdaki Material 3 alanlarına karşılık gelecek şekilde tanımlanmalıdır.

- primary
- background
- surface
- error
- outline
- onSurface
- onBackground

---

## Kullanım Kuralları

- Primary butonlar yalnızca `primary` rengini kullanmalıdır.
- Yardımcı metinler `onSurfaceVariant` üzerinden okunmalıdır.
- Divider ve border renkleri `outline` alanından gelmelidir.
- Yeni ekranlarda hardcoded renk kullanılmamalıdır.