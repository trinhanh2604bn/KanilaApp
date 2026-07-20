Add-Type -AssemblyName System.Drawing
$path = 'd:\KanilaApp\frontend\app\src\main\assets\models\eyeShadow.png'
$img = [System.Drawing.Image]::FromFile($path)
$w = $img.Width
$h = $img.Height
$bmp = new-object System.Drawing.Bitmap($img)
$img.Dispose()

$minX = $w; $maxX = 0; $minY = $h; $maxY = 0;
for ($y = 0; $y -lt $h; $y++) {
    for ($x = 0; $x -lt ($w/2); $x++) {
        $c = $bmp.GetPixel($x, $y)
        if ($c.A -gt 10) {
            if ($x -lt $minX) { $minX = $x }
            if ($x -gt $maxX) { $maxX = $x }
            if ($y -lt $minY) { $minY = $y }
            if ($y -gt $maxY) { $maxY = $y }
        }
    }
}
Write-Host "Left eye bounds: X: $minX - $maxX, Y: $minY - $maxY"
$bmp.Dispose()
