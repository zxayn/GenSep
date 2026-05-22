# GenSep (Generate Resep) 🍳

Aplikasi Android native untuk regenerasi resep masakan berbasis AI/API dengan antarmuka modern menggunakan Jetpack Compose.

## 🚀 Fitur Utama
- **UI Modern**: Menggunakan Material Design 3 dengan skema warna hijau yang segar.
- **Camera Dock**: Akses kamera cepat dengan animasi "Pop Air" (gelembung) yang interaktif.
- **Recipe Management**: Simpan dan sukai resep dengan transisi ikon yang halus (Crossfade).
- **Functional Search**: Pencarian resep secara real-time melalui ViewModel.

## 🏗️ Arsitektur (MVVM)
Project ini menggunakan pola **Model-View-ViewModel** untuk pemisahan logika dan UI:

1.  **View (Compose)**: Berada di folder per fitur (contoh: `beranda/BerandaScreen.kt`). Hanya fokus pada tampilan.
2.  **ViewModel**: Berada di folder fitur (contoh: `beranda/BerandaViewModel.kt`). Menangani state UI dan logika bisnis.
3.  **Model/Data**: (Next Step) Tempat penyimpanan data (Room Database) atau integrasi API (Retrofit).

---

## 💡 Panduan Pengembangan

### 1. Menambah Fitur Baru (ViewModel)
Jika ingin membuat layar baru (misal: *Catatan*):
- Buat `CatatanViewModel.kt`.
- Gunakan `mutableStateOf` untuk menghandle data agar UI otomatis terupdate saat data berubah.
- Hubungkan di `CatatanScreen.kt` menggunakan `viewModel()` dari library `lifecycle-viewmodel-compose`.

### 2. Implementasi Database (Room)
Untuk menyimpan resep secara offline:
- **Entity**: Buat class data dengan anotasi `@Entity` (misal: `RecipeEntity`).
- **DAO**: Buat Interface untuk Query (Insert, Delete, Get).
- **Repository**: Buat class penengah antara Database dan ViewModel agar kode tetap rapi.
- **ViewModel**: Panggil fungsi dari Repository untuk mengambil data dari Database.

### 3. Animasi Kustom
Aplikasi ini menggunakan:
- `AnimatedVisibility`: Untuk muncul/sembunyi elemen (seperti Camera Dock).
- `animateDpAsState`: Untuk transisi ukuran/posisi yang halus.
- `Crossfade`: Untuk pergantian ikon (Like/Save) agar tidak kaku.

---

## 🛠️ Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Navigation**: Compose Navigation (Type-safe routes)
- **Architecture**: MVVM
- **Icons**: Custom Drawables + Material Icons Extended

---

**GenSep** - *Masak jadi lebih mudah dengan teknologi.*
