Add-Type -AssemblyName System.Drawing
$path = 'd:\KanilaApp\frontend\app\src\main\assets\models\blush.png'
$img = [System.Drawing.Image]::FromFile($path)
$w = $img.Width
$h = $img.Height
$bmp = new-object System.Drawing.Bitmap($img)
$newBmp = new-object System.Drawing.Bitmap($w, $h)
$img.Dispose()

$shiftX = [int]($w * 0.10)
$shiftY = [int]($h * 0.03)

for ($y = 0; $y -lt $h; $y++) {
    for ($x = 0; $x -lt $w; $x++) {
        $c = $bmp.GetPixel($x, $y)
        if ($c.A -gt 0) {
            $newY = $y + $shiftY
            if ($newY -lt $h) {
                $relY = $newY / $h
                $multiplier = 1.0
                if ($relY -gt 0.65) {
                    $multiplier = [Math]::Max(0.0, 1.0 - ($relY - 0.65) / 0.15)
                    $multiplier = $multiplier * $multiplier
                }
                
                $newA = [Math]::Min(255, [int]($c.A * $multiplier * 1.5))
                $newC = [System.Drawing.Color]::FromArgb($newA, $c.R, $c.G, $c.B)
                
                if ($x -lt ($w / 2)) {
                    $newX = $x + $shiftX
                    if ($newX -lt ($w / 2)) {
                        $newBmp.SetPixel($newX, $newY, $newC)
                    }
                } else {
                    $newX = $x - $shiftX
                    if ($newX -ge ($w / 2)) {
                        $newBmp.SetPixel($newX, $newY, $newC)
                    }
                }
            }
        }
    }
}
$newBmp.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
$bmp.Dispose()
$newBmp.Dispose()
Write-Host 'Done'
