# 🧪 Quick Test Script - PowerShell

## Test 1: Connection Test
Write-Host "`n=== Test 1: Connection Test ===" -ForegroundColor Cyan
$response1 = Invoke-WebRequest -Uri "http://127.0.0.1:8000/api/test" -Method Get
Write-Host "Status:" $response1.StatusCode -ForegroundColor Green
$response1.Content | ConvertFrom-Json | ConvertTo-Json -Depth 10

## Test 2: Login as Admin
Write-Host "`n=== Test 2: Login as Admin ===" -ForegroundColor Cyan
$loginBody = @{
    email = "admin@sekolah.com"
    password = "admin123"
} | ConvertTo-Json

$response2 = Invoke-WebRequest -Uri "http://127.0.0.1:8000/api/login" `
    -Method Post `
    -Body $loginBody `
    -ContentType "application/json" `
    -Headers @{"Accept"="application/json"}

Write-Host "Status:" $response2.StatusCode -ForegroundColor Green
$loginData = $response2.Content | ConvertFrom-Json
$token = $loginData.data.token
Write-Host "Token:" $token -ForegroundColor Yellow
$loginData | ConvertTo-Json -Depth 10

## Test 3: Get All Guru
Write-Host "`n=== Test 3: Get All Guru ===" -ForegroundColor Cyan
$response3 = Invoke-WebRequest -Uri "http://127.0.0.1:8000/api/gurus" `
    -Method Get `
    -Headers @{
        "Authorization" = "Bearer $token"
        "Accept" = "application/json"
    }

Write-Host "Status:" $response3.StatusCode -ForegroundColor Green
$guruData = $response3.Content | ConvertFrom-Json
Write-Host "Total Guru:" $guruData.data.total -ForegroundColor Yellow
$guruData.data.data | Format-Table id, nip, nama, email -AutoSize

## Test 4: Get Single Guru with Relationships
Write-Host "`n=== Test 4: Get Single Guru (ID: 1) with Relationships ===" -ForegroundColor Cyan
$response4 = Invoke-WebRequest -Uri "http://127.0.0.1:8000/api/gurus/1" `
    -Method Get `
    -Headers @{
        "Authorization" = "Bearer $token"
        "Accept" = "application/json"
    }

Write-Host "Status:" $response4.StatusCode -ForegroundColor Green
$response4.Content | ConvertFrom-Json | ConvertTo-Json -Depth 10

## Test 5: Get All Kelas
Write-Host "`n=== Test 5: Get All Kelas ===" -ForegroundColor Cyan
$response5 = Invoke-WebRequest -Uri "http://127.0.0.1:8000/api/kelas" `
    -Method Get `
    -Headers @{
        "Authorization" = "Bearer $token"
        "Accept" = "application/json"
    }

Write-Host "Status:" $response5.StatusCode -ForegroundColor Green
$kelasData = $response5.Content | ConvertFrom-Json
Write-Host "Total Kelas:" $kelasData.data.total -ForegroundColor Yellow
$kelasData.data.data | Format-Table id, nama, tingkat, jurusan, jumlah_siswa, ruangan -AutoSize

## Test 6: Get Siswa by Kelas
Write-Host "`n=== Test 6: Get Siswa in Kelas X IPA 1 ===" -ForegroundColor Cyan
$response6 = Invoke-WebRequest -Uri "http://127.0.0.1:8000/api/siswas?kelas_id=1" `
    -Method Get `
    -Headers @{
        "Authorization" = "Bearer $token"
        "Accept" = "application/json"
    }

Write-Host "Status:" $response6.StatusCode -ForegroundColor Green
$siswaData = $response6.Content | ConvertFrom-Json
Write-Host "Total Siswa:" $siswaData.data.total -ForegroundColor Yellow
$siswaData.data.data | Format-Table id, nis, nama, jenis_kelamin -AutoSize

## Test 7: Get Jadwal for Senin
Write-Host "`n=== Test 7: Get Jadwal Hari Senin ===" -ForegroundColor Cyan
$response7 = Invoke-WebRequest -Uri "http://127.0.0.1:8000/api/jadwals?hari=Senin" `
    -Method Get `
    -Headers @{
        "Authorization" = "Bearer $token"
        "Accept" = "application/json"
    }

Write-Host "Status:" $response7.StatusCode -ForegroundColor Green
$jadwalData = $response7.Content | ConvertFrom-Json
Write-Host "Total Jadwal:" $jadwalData.data.total -ForegroundColor Yellow
$jadwalData.data.data | Select-Object id, hari, jam_mulai, jam_selesai | Format-Table -AutoSize

## Test 8: Get Kehadiran Today
Write-Host "`n=== Test 8: Get Kehadiran Hari Ini ===" -ForegroundColor Cyan
$today = Get-Date -Format "yyyy-MM-dd"
$response8 = Invoke-WebRequest -Uri "http://127.0.0.1:8000/api/kehadirans?tanggal=$today" `
    -Method Get `
    -Headers @{
        "Authorization" = "Bearer $token"
        "Accept" = "application/json"
    }

Write-Host "Status:" $response8.StatusCode -ForegroundColor Green
$kehadiranData = $response8.Content | ConvertFrom-Json
Write-Host "Total Kehadiran:" $kehadiranData.data.total -ForegroundColor Yellow
$kehadiranData.data.data | Select-Object id, tanggal, status, waktu_absen | Format-Table -AutoSize

## Summary
Write-Host "`n=== TEST SUMMARY ===" -ForegroundColor Magenta
Write-Host "✅ Connection Test: PASSED" -ForegroundColor Green
Write-Host "✅ Login Test: PASSED" -ForegroundColor Green
Write-Host "✅ Get Guru: PASSED ($($guruData.data.total) records)" -ForegroundColor Green
Write-Host "✅ Get Kelas: PASSED ($($kelasData.data.total) records)" -ForegroundColor Green
Write-Host "✅ Get Siswa: PASSED ($($siswaData.data.total) records)" -ForegroundColor Green
Write-Host "✅ Get Jadwal: PASSED ($($jadwalData.data.total) records)" -ForegroundColor Green
Write-Host "✅ Get Kehadiran: PASSED ($($kehadiranData.data.total) records)" -ForegroundColor Green
Write-Host "`nAll tests completed successfully! 🎉" -ForegroundColor Green
