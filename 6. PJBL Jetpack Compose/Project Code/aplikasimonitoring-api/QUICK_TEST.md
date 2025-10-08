# Quick Test - API Endpoints

## Test 1: Connection Test

```bash
curl http://127.0.0.1:8000/api/test
```

Expected Response:

```json
{
    "success": true,
    "message": "API Connected Successfully!",
    "data": "Laravel API is running"
}
```

## Test 2: Login

```bash
curl -X POST http://127.0.0.1:8000/api/login \
  -H "Content-Type: application/json" \
  -H "Accept: application/json" \
  -d '{"email":"admin@example.com","password":"password"}'
```

## Test 3: Get Gurus (with token)

```bash
# Ganti YOUR_TOKEN dengan token dari response login
curl http://127.0.0.1:8000/api/gurus \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Accept: application/json"
```

## PowerShell Version

### Test Connection

```powershell
Invoke-WebRequest -Uri "http://127.0.0.1:8000/api/test" -Method Get | Select-Object -ExpandProperty Content
```

### Test Login

```powershell
$body = @{
    email = "admin@example.com"
    password = "password"
} | ConvertTo-Json

$response = Invoke-WebRequest -Uri "http://127.0.0.1:8000/api/login" `
    -Method Post `
    -Body $body `
    -ContentType "application/json" `
    -Headers @{"Accept"="application/json"}

$response.Content
```

### Test Gurus Endpoint

```powershell
# Simpan token dari response login
$token = "PASTE_YOUR_TOKEN_HERE"

Invoke-WebRequest -Uri "http://127.0.0.1:8000/api/gurus" `
    -Method Get `
    -Headers @{
        "Authorization" = "Bearer $token"
        "Accept" = "application/json"
    } | Select-Object -ExpandProperty Content
```
