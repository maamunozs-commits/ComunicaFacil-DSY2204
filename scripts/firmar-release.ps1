param([string]$CarpetaFirma = (Join-Path $env:USERPROFILE '.android\comunicafacil-firma'))
$ErrorActionPreference = 'Stop'
$env:JAVA_HOME = 'C:\Program Files\Android\Android Studio\jbr'
$env:ANDROID_HOME = Join-Path $env:LOCALAPPDATA 'Android\Sdk'
$cred = Import-Clixml -LiteralPath (Join-Path $CarpetaFirma 'firma.xml')
$env:COMUNICAFACIL_KEYSTORE = Join-Path $CarpetaFirma 'release.jks'
$env:COMUNICAFACIL_STORE_PASSWORD = $cred.GetNetworkCredential().Password
$env:COMUNICAFACIL_KEY_PASSWORD = $env:COMUNICAFACIL_STORE_PASSWORD
try {
    & (Join-Path $PSScriptRoot '..\gradlew.bat') assembleRelease --console=plain
    if ($LASTEXITCODE -ne 0) { throw 'Falló la compilación release.' }
} finally {
    Remove-Item Env:\COMUNICAFACIL_STORE_PASSWORD,Env:\COMUNICAFACIL_KEY_PASSWORD -ErrorAction SilentlyContinue
}
