Add-Type -AssemblyName System.Drawing
$path = 'd:\KanilaApp\frontend\app\src\main\assets\models\lipstick.png'
$img = [System.Drawing.Image]::FromFile($path)
$w = $img.Width
$h = $img.Height
$bmp = new-object System.Drawing.Bitmap($img)
$newBmp = new-object System.Drawing.Bitmap($w, $h)
$img.Dispose()

$cX = 2097

for ($y = 0; $y -lt $h; $y++) {
    for ($x = 0; $x -lt $w; $x++) {
        $c = $bmp.GetPixel($x, $y)
        if ($c.A -gt 0) {
            $multiplierX = 1.0
            $multiplierY = 1.0
            
            # Soften vertical edges, but preserve thickness
            if ($y -lt 2845) {
                # Upper lip
                if ($y -ge 2730) {
                    $multiplierY = 1.0
                } elseif ($y -le 2670) {
                    $multiplierY = 0.0
                } else {
                    $fadeY = (2730 - $y) / (2730 - 2670)
                    $multiplierY = 0.5 * (1.0 + [Math]::Cos($fadeY * [Math]::PI))
                }
            } else {
                # Lower lip
                if ($y -le 3000) {
                    $multiplierY = 1.0
                } elseif ($y -ge 3060) {
                    $multiplierY = 0.0
                } else {
                    $fadeY = ($y - 3000) / (3060 - 3000)
                    $multiplierY = 0.5 * (1.0 + [Math]::Cos($fadeY * [Math]::PI))
                }
            }
            
            # Horizontal fade (corners of mouth)
            $dx = [Math]::Abs($x - $cX)
            if ($dx -lt 550) {
                $multiplierX = 1.0
            } elseif ($dx -gt 650) {
                $multiplierX = 0.0
            } else {
                $fadeX = ($dx - 550) / (650 - 550)
                $multiplierX = 0.5 * (1.0 + [Math]::Cos($fadeX * [Math]::PI))
            }
            
            $multiplier = $multiplierX * $multiplierY
            
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
Write-Host 'Done softening lipstick while preserving thickness'
