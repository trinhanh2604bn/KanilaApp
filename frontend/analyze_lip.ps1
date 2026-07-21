Add-Type -AssemblyName System.Drawing
$path = 'd:\KanilaApp\frontend\app\src\main\assets\models\lipstick.png'
$img = [System.Drawing.Image]::FromFile($path)
$bmp = new-object System.Drawing.Bitmap($img)
$w = $img.Width
$h = $img.Height
$img.Dispose()

$rowSums = @{}
for ($y = 2500; $y -lt 3400; $y += 10) {
    $sum = 0
    for ($x = 1400; $x -lt 2800; $x += 10) {
        $c = $bmp.GetPixel($x, $y)
        $sum += $c.A
    }
    if ($sum -gt 0) {
        $rowSums[$y] = $sum
    }
}
$bmp.Dispose()

$keys = $rowSums.Keys | Sort-Object
foreach ($k in $keys) {
    $val = $rowSums[$k]
    $bar = "#" * ($val / 1000)
    Write-Host "$k : $val $bar"
}
