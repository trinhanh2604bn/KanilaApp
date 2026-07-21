Add-Type -AssemblyName System.Drawing
$path = 'd:\KanilaApp\frontend\app\src\main\assets\models\lipstick.png'
$img = [System.Drawing.Image]::FromFile($path)
Write-Host "Lipstick size: $($img.Width)x$($img.Height)"
