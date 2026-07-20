Add-Type -AssemblyName System.Drawing
$path = 'd:\KanilaApp\frontend\app\src\main\assets\models\blush.png'
$w = 1024
$h = 1024
$newBmp = new-object System.Drawing.Bitmap($w, $h)

# The user liked the inward-shifted, slightly lower position
$cX_left = $w * 0.25
$cX_right = $w * 0.75
$cY = $h * 0.53

# The user wants it rounder, not flat/long. 
# We'll use a circular radius. 
$radius = $w * 0.15

for ($y = 0; $y -lt $h; $y++) {
    for ($x = 0; $x -lt $w; $x++) {
        $distLeft = [Math]::Sqrt([Math]::Pow($x - $cX_left, 2) + [Math]::Pow($y - $cY, 2))
        $distRight = [Math]::Sqrt([Math]::Pow($x - $cX_right, 2) + [Math]::Pow($y - $cY, 2))
        
        $dist = [Math]::Min($distLeft, $distRight)
        
        if ($dist -lt $radius) {
            # Smooth fade out, but keep the center very opaque so it's darker
            # Normalized distance (0 to 1)
            $nd = $dist / $radius
            
            # Using smoothstep-like curve: we want the center 40% to be quite solid, then fade smoothly
            if ($nd -lt 0.3) {
                $alphaRatio = 1.0
            } else {
                # Fade from 1.0 to 0.0 between nd=0.3 and nd=1.0
                $fade = ($nd - 0.3) / 0.7
                # Cosine fade for smoothness
                $alphaRatio = 0.5 * (1.0 + [Math]::Cos($fade * [Math]::PI))
            }
            
            $newA = [Math]::Min(255, [Math]::Max(0, [int](255 * $alphaRatio)))
            # The texture should be white, tinted by shader
            $newC = [System.Drawing.Color]::FromArgb($newA, 255, 255, 255)
            $newBmp.SetPixel($x, $y, $newC)
        } else {
            $newC = [System.Drawing.Color]::FromArgb(0, 0, 0, 0)
            $newBmp.SetPixel($x, $y, $newC)
        }
    }
}
$newBmp.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
$newBmp.Dispose()
Write-Host 'Generated perfectly round, darker blush'
