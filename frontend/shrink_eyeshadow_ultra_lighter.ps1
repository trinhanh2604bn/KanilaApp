Add-Type -AssemblyName System.Drawing
$path = 'd:\KanilaApp\frontend\app\src\main\assets\models\eyeShadow.png'
$img = [System.Drawing.Image]::FromFile($path)
$w = $img.Width
$h = $img.Height
$bmp = new-object System.Drawing.Bitmap($img)
$newBmp = new-object System.Drawing.Bitmap($w, $h)
$img.Dispose()

$cX_left = 659
$cX_right = 1389
$cY = 764

for ($y = 0; $y -lt $h; $y++) {
    for ($x = 0; $x -lt $w; $x++) {
        $c = $bmp.GetPixel($x, $y)
        if ($c.A -gt 0) {
            $dx = 0
            $dy = $y - $cY
            
            if ($x -lt ($w / 2)) {
                $dx = $x - $cX_left
            } else {
                $dx = $x - $cX_right
            }
            
            $dist = [Math]::Sqrt([Math]::Pow($dx / 1.5, 2) + [Math]::Pow($dy / 0.5, 2))
            
            $multiplier = 1.0
            $innerRadius = 70
            $outerRadius = 160
            
            if ($dist -gt $outerRadius) {
                $multiplier = 0.0
            } elseif ($dist -gt $innerRadius) {
                $fade = ($dist - $innerRadius) / ($outerRadius - $innerRadius)
                $multiplier = 0.5 * (1.0 + [Math]::Cos($fade * [Math]::PI))
            }
            
            # Reduce the previous 0.9936 multiplier by another 8% (0.9936 * 0.92 = 0.9141)
            $multiplier = $multiplier * 0.9141
            
            $newA = [Math]::Min(255, [Math]::Max(0, [int]($c.A * $multiplier)))
            
            if ($newA -gt 0) {
                $newC = [System.Drawing.Color]::FromArgb($newA, 255, 255, 255)
                $newBmp.SetPixel($x, $y, $newC)
            }
        }
    }
}
$newBmp.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
$bmp.Dispose()
$newBmp.Dispose()
Write-Host 'Done shrinking eyeshadow (ultra lighter version)'
