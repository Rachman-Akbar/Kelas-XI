# PowerShell script untuk migrasi ViewModels
# Run this script from WavesOfFood directory

$viewModelPath = "app\src\main\java\com\komputerkit\wavesoffood\viewmodel"

Write-Host "=== Starting ViewModel Migration ===" -ForegroundColor Green

# Backup old ViewModels
Write-Host "`nStep 1: Backing up old ViewModels..." -ForegroundColor Yellow
$backupPath = "$viewModelPath\backup_old"
New-Item -ItemType Directory -Force -Path $backupPath | Out-Null

$oldFiles = @(
    "AuthViewModel.kt",
    "HomeViewModel.kt",
    "CartViewModel.kt",
    "ProductDetailViewModel.kt",
    "CheckoutViewModel.kt",
    "ProfileViewModel.kt",
    "OrdersViewModel.kt",
    "OrderDetailViewModel.kt"
)

foreach ($file in $oldFiles) {
    $source = "$viewModelPath\$file"
    if (Test-Path $source) {
        Move-Item -Path $source -Destination "$backupPath\$file" -Force
        Write-Host "  ✓ Backed up $file" -ForegroundColor Green
    }
}

# Rename New*ViewModel to original names
Write-Host "`nStep 2: Renaming New*ViewModel files..." -ForegroundColor Yellow

$renameMap = @{
    "NewAuthViewModel.kt"          = "AuthViewModel.kt"
    "NewHomeViewModel.kt"          = "HomeViewModel.kt"
    "NewCartViewModel.kt"          = "CartViewModel.kt"
    "NewProductDetailViewModel.kt" = "ProductDetailViewModel.kt"
    "NewCheckoutViewModel.kt"      = "CheckoutViewModel.kt"
    "NewProfileViewModel.kt"       = "ProfileViewModel.kt"
    "NewOrdersViewModel.kt"        = "OrdersViewModel.kt"
    "NewOrderDetailViewModel.kt"   = "OrderDetailViewModel.kt"
}

foreach ($oldName in $renameMap.Keys) {
    $newName = $renameMap[$oldName]
    $source = "$viewModelPath\$oldName"
    $destination = "$viewModelPath\$newName"
    
    if (Test-Path $source) {
        Move-Item -Path $source -Destination $destination -Force
        Write-Host "  ✓ Renamed $oldName → $newName" -ForegroundColor Green
    }
}

Write-Host "`n=== Migration Complete! ===" -ForegroundColor Green
Write-Host "Old ViewModels backed up to: $backupPath" -ForegroundColor Cyan
Write-Host "`nNext steps:" -ForegroundColor Yellow
Write-Host "1. Update all Activities with ViewModelFactory" -ForegroundColor White
Write-Host "2. Update import statements in Adapters" -ForegroundColor White
Write-Host "3. Test the application" -ForegroundColor White
