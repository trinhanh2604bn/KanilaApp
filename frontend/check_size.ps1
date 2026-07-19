Add-Type -AssemblyName System.Drawing
$path = 'd:\KanilaApp\frontend\app\src\main\assets\models\eyeShadow.png'
$img = [System.Drawing.Image]::FromFile($path)
Write-Host "$($img.Width)x$($img.Height)"
$img.Dispose()
