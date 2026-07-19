Add-Type -AssemblyName System.Drawing
$path = 'd:\KanilaApp\frontend\app\src\main\assets\models\blush.png'
$img = [System.Drawing.Image]::FromFile($path)
$w = $img.Width
$h = $img.Height
$bmp = new-object System.Drawing.Bitmap($img)
$newBmp = new-object System.Drawing.Bitmap($w, $h)
$img.Dispose()

$shiftX = [int]($w * 0.12)

for ($y = 0; $y -lt $h; $y++) {
    for ($x = 0; $x -lt $w; $x++) {
        $c = $bmp.GetPixel($x, $y)
        if ($c.A -gt 0) {
            if ($x -lt ($w / 2)) {
                $newX = $x + $shiftX
                if ($newX -lt ($w / 2)) {
                    $newBmp.SetPixel($newX, $y, $c)
                }
            } else {
                $newX = $x - $shiftX
                if ($newX -ge ($w / 2)) {
                    $newBmp.SetPixel($newX, $y, $c)
                }
            }
        }
    }
}
$newBmp.Save($path, [System.Drawing.Imaging.ImageFormat]::Png)
$bmp.Dispose()
$newBmp.Dispose()
Write-Host 'Done shifting blush inwards'
