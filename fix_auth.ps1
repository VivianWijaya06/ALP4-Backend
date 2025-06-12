# PowerShell script to fix authentication code in HTML files
$sourceDir = "c:\Users\LOQ\Desktop\ALP4-Backend\prod\src\main\resources\static"
$filesToFix = @("goreng.html", "knife.html", "frying.html", "guide.html", "baking.html", "boiling.html", "salad.html", "RekomendasiHarian.html", "sayurSop.html", "SupAyamJahe.html")

foreach ($file in $filesToFix) {
    $filePath = Join-Path $sourceDir $file
    if (Test-Path $filePath) {
        Write-Host "Processing: $file"
        $content = Get-Content $filePath -Raw
        
        # Fix unsafe JSON parsing
        $content = $content -replace 'const userData = localStorage\.getItem\(''cookeasyUser''\);', 'const userDataString = localStorage.getItem(''cookeasyUser'');
    const userData = userDataString ? JSON.parse(userDataString) : null;'
        
        $content = $content -replace 'const user = JSON\.parse\(userData\);', '// Parse userData safely'
        
        Set-Content $filePath $content -NoNewline
        Write-Host "Fixed: $file"
    } else {
        Write-Host "File not found: $file"
    }
}

Write-Host "Done fixing authentication code in HTML files"