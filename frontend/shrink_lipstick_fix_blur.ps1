Add-Type -AssemblyName System.Drawing
$path = 'd:\KanilaApp\frontend\app\src\main\assets\models\lipstick.png'
$img = [System.Drawing.Image]::FromFile($path)
$w = $img.Width
$h = $img.Height
$bmp = new-object System.Drawing.Bitmap($img)
$newBmp = new-object System.Drawing.Bitmap($w, $h)
$img.Dispose()

$cX = 2097
$cY = 2936

for ($y = 0; $y -lt $h; $y++) {
    for ($x = 0; $x -lt $w; $x++) {
        $c = $bmp.GetPixel($x, $y)
        if ($c.A -gt 0) {
            $dx = [Math]::Abs($x - $cX)
            $dy = [Math]::Abs($y - $cY)
            
            $multiplierX = 1.0
            $multiplierY = 1.0
            
            # Less aggressive vertical fade: preserve more of the solid lip body
            if ($dy -lt 150) {
                $multiplierY = 1.0
            } elseif ($dy -gt 350) {
                $multiplierY = 0.0
            } else {
                $fadeY = ($dy - 150) / (350 - 150)
                $multiplierY = 0.5 * (1.0 + [Math]::Cos($fadeY * [Math]::PI))
            }
            
            # Less aggressive horizontal fade
            if ($dx -lt 450) {
                $multiplierX = 1.0
            } elseif ($dx -gt 650) {
                $multiplierX = 0.0
            } else {
                $fadeX = ($dx - 450) / (650 - 450)
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
Write-Host 'Done reshaping lipstick (sharper)'
