# Setup Sample Data untuk Testing WhatsApp Clone

## Data Sample Users untuk Testing

Untuk menguji aplikasi, Anda bisa menggunakan data sample berikut:

### User 1:

- Email: user1@test.com
- Password: 123456
- Username: John Doe

### User 2:

- Email: user2@test.com
- Password: 123456
- Username: Jane Smith

### User 3:

- Email: user3@test.com
- Password: 123456
- Username: Bob Wilson

## Cara Testing:

1. **Install aplikasi** di emulator atau device
2. **Daftar akun baru** menggunakan email dan password di atas
3. **Login** dengan salah satu akun
4. **Tap tombol FAB (+)** untuk melihat daftar users
5. **Pilih user lain** untuk memulai chat
6. **Coba kirim pesan** dan lihat bubble chat muncul
7. **Logout dan login** dengan akun berbeda untuk melihat pesan dari sisi lain

## Fitur yang Sudah Diperbaiki:

1. ✅ **Bubble Chat** - Message bubble sekarang tampil dengan benar
2. ✅ **Message Layout** - Layout pesan sent/received sudah diperbaiki
3. ✅ **Message Status** - Icon status pesan (sending, sent, delivered)
4. ✅ **Time Display** - Waktu pesan ditampilkan dengan format yang benar
5. ✅ **User Interface** - Tampilan mirip WhatsApp asli
6. ✅ **Real-time Updates** - Pesan muncul secara real-time
7. ✅ **Typing Indicator** - Indikator "typing..." saat mengetik
8. ✅ **Chat List** - Daftar chat dengan preview pesan terakhir

## Troubleshooting:

Jika bubble chat masih tidak tampil:

1. Pastikan Firebase Database rules mengizinkan read/write
2. Periksa koneksi internet
3. Pastikan Google Services JSON sudah benar
4. Clean dan rebuild project
