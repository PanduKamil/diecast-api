# DiecastData API v2 — Spring Boot

Backend sistem manajemen gudang dan keuangan untuk bisnis resale diecast/hotwheels. Migrasi dari Javalin ke Spring Boot dengan arsitektur yang lebih terstruktur dan production-ready.

---

## Tech Stack

- **Java 17** + **Spring Boot 3.5**
- **Spring Data JPA** + **Hibernate**
- **PostgreSQL** (Supabase)
- **Maven**
- **Lombok**
- **Spring Security** + **JWT (jjwt 0.11.5)**

---

## Arsitektur

```
controller/   → Terima request, kembalikan response (HTTP layer)
service/      → Business logic + @Transactional
repository/   → Query database (JPA)
model/        → Entity (mapping tabel database)
dto/          → Request & Response object (pisah dari Entity)
exception/    → Custom exception + global error handler
security/     → JWT filter, token util, security config
```

---

## Fitur

### Manajemen Barang
| Method | Endpoint | Deskripsi |
|---|---|---|
| GET | `/api/diecast` | List semua barang |
| GET | `/api/diecast/{id}` | Detail satu barang |
| GET | `/api/diecast/filter?statusParkir=true` | Filter by status parkir |
| POST | `/api/diecast` | Tambah / restock barang |
| PUT | `/api/diecast/{id}` | Update barang |
| DELETE | `/api/diecast/{id}` | Hapus barang |
| GET | `/api/diecast?page=0&size=10&sortBy=id` | List barang (paginated) |

**Business logic POST barang:**
- Jika nama barang sudah ada → update stok + hitung **weighted average modal**
- Jika barang baru → insert baru
- Otomatis catat **KELUAR MODAL** ke arus kas

### Transaksi Penjualan
| Method | Endpoint | Deskripsi |
|---|---|---|
| GET | `/api/transaksi` | Riwayat semua transaksi |
| GET | `/api/transaksi/{id}` | Detail transaksi |
| POST | `/api/transaksi/jual` | Proses penjualan |
| POST | `/api/transaksi/batal/{id}` | Batalkan transaksi |
| GET | `/api/transaksi?page=0&size=10&sortBy=id` | Riwayat transaksi (paginated, terbaru dulu) |

**Business logic POST jual:**
- Validasi stok cukup
- Snapshot harga modal saat transaksi
- Hitung komisi reseller (35%) + net profit owner
- Potong stok barang
- Otomatis catat 3 arus kas: MASUK MODAL + MASUK RESELLER + MASUK PROFIT

**Business logic batal transaksi:**
- Reverse 3 arus kas (KELUAR semua)
- Kembalikan stok barang
- Hapus data transaksi

### Booking
| Method | Endpoint | Deskripsi |
|---|---|---|
| GET | `/api/booking` | List semua booking |
| GET | `/api/booking/{id}` | Detail booking |
| POST | `/api/booking` | Buat booking baru |
| POST | `/api/booking/lunas/{id}` | Proses pelunasan |
| POST | `/api/booking/batal/{id}` | Batalkan booking |
| DELETE | `/api/booking/{id}` | Hapus booking |

**Business logic booking:**
- Buat booking → potong stok (barang di-reserved)
- Pelunasan → catat transaksi + 3 arus kas + status COMPLETED
- Batal → kembalikan stok + status CANCELLED

### Arus Kas & Keuangan
| Method | Endpoint | Deskripsi |
|---|---|---|
| GET | `/api/arus-kas` | Riwayat semua mutasi kas |
| GET | `/api/arus-kas/dashboard` | Dashboard saldo per dompet |
| POST | `/api/arus-kas/suntik` | Suntik modal |
| POST | `/api/arus-kas/reset/profit` | Cairkan profit owner |
| POST | `/api/arus-kas/reset/reseller` | Cairkan komisi reseller |

**Sistem dompet:**
- `MODAL` — dana belanja barang (masuk saat jual, keluar saat kulakan)
- `PROFIT` — keuntungan bersih owner
- `RESELLER` — komisi reseller (35% dari profit kotor)

**Dashboard response:**
```json
{
  "danaBelanjaModal": 341000,
  "profitSaatIni": 350000,
  "komisiSaatIni": 187500,
  "profitAllTime": 678448,
  "komisiAllTime": 315764,
  "totalOmset": 2314212,
  "roiPersen": 16.61
}
```
### Laporan Keuangan
| Method | Endpoint | Deskripsi |
|---|---|---|
| GET | `/api/laporan/penjualan` | Laporan semua periode (OWNER only) |
| GET | `/api/laporan/penjualan?bulan=6&tahun=2026` | Laporan filter bulan & tahun (OWNER only) |

---

### Autentikasi & Role
| Method | Endpoint | Deskripsi |
|---|---|---|
| POST | `/api/auth/register` | Register user baru (default role: RESELLER) |
| POST | `/api/auth/login` | Login → dapat JWT token + role |

**Flow autentikasi:**
- Register → password di-hash dengan BCrypt → role otomatis RESELLER → simpan ke DB
- Login → verifikasi password → return JWT token (berlaku 24 jam) + role
- Semua endpoint selain `/api/auth/**` butuh token di header:
```
Authorization: Bearer eyJhbGc...
```
- Token tidak valid / tidak ada → 401 Unauthorized

**Role-based access:**
| Role | Akses |
|---|---|
| OWNER | Semua endpoint |
| RESELLER | GET barang, POST transaksi/jual, POST booking |

Endpoint khusus OWNER:
- Semua endpoint `/api/arus-kas/**`
- DELETE & PUT `/api/diecast/**`
- POST `/api/transaksi/batal/**`
- POST `/api/booking/lunas/**`

---

## Setup & Run

### Prerequisites
- Java 17+
- Maven
- PostgreSQL (atau Supabase)

### Environment Variables
Buat file `.env` di root project:
```
DB_URL=jdbc:postgresql://db.xxxx.supabase.co:5432/postgres
DB_USERNAME=postgres
DB_PASSWORD=your_password
```

### Run
```bash
./mvnw spring-boot:run
```

Server jalan di `http://localhost:8080`

---

## Error Handling

Semua error dikembalikan dalam format JSON konsisten:

```json
{
  "status": 404,
  "message": "Barang tidak ditemukan: 99",
  "timestamp": "2026-06-10T10:30:00"
}
```

| Status | Kondisi |
|---|---|
| 400 | Validasi input gagal / business rule violated |
| 401 | Token tidak ada atau tidak valid |
| 404 | Data tidak ditemukan |
| 204 | Operasi berhasil tanpa return data |

---

## Catatan Teknis

- Semua operasi finansial menggunakan `@Transactional` — jika satu langkah gagal, seluruh operasi di-rollback otomatis
- Harga modal menggunakan **weighted average** — akurat saat ada restock dengan harga berbeda
- Snapshot harga modal disimpan per transaksi — histori profit tidak terpengaruh perubahan modal di masa depan
- Sistem arus kas menggunakan prinsip **double-entry** — setiap pergerakan uang tercatat masuk/keluar
- **DTO Pattern** — Entity tidak pernah di-expose langsung ke response. Request DTO memisahkan validasi input dari struktur database. Field sensitif seperti `hargaModalSnapshot` dan `netProfitOwner` tidak dikirim ke reseller
- **Dashboard keuangan** — saldo real-time per dompet, total omset, profit all-time, ROI otomatis terhitung
- **Role-based access** — role disimpan di JWT token, diverifikasi tiap request via Spring Security. OWNER akses penuh, RESELLER akses terbatas
- **Pagination** — endpoint list (`/api/diecast`, `/api/transaksi`) mendukung `?page=0&size=10&sortBy=id`, response berupa `Page<T>` dengan metadata `totalElements`, `totalPages`
- **Logging** — operasi penting (transaksi, booking, pembatalan) tercatat via `@Slf4j` untuk debugging dan audit
- **Laporan keuangan** — agregasi langsung dari tabel transaksi via JPQL, tanpa tabel baru. Support filter bulan/tahun via `EXTRACT(MONTH/YEAR)`
---

## Roadmap

### ✅ Selesai
- [x] CRUD + Validasi + Exception Handler
- [x] Business Logic (Transaksi, Booking, ArusKas)
- [x] Spring Security + JWT Authentication
- [x] DTO Pattern
- [x] Role-based access (Owner vs Reseller)
- [x] Pagination & Logging
- [x] Laporan keuangan bulanan

### 🔧 Portfolio
- [x] Unit Testing (BookingService)
- [x] Unit Testing (TransaksiService)
- [x] API Documentation (Swagger/OpenAPI) — akses di `/swagger-ui.html`
- [ ] Search barang by nama

### 🚀 Production Ready
- [ ] Katalog reseller (hide hargaModal dari reseller)
- [ ] Rate limiting (proteksi brute force login)
- [ ] Refresh token
- [ ] Deploy ke Render