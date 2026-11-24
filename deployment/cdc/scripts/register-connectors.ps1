param (
    [string]$ConnectHost = "localhost",
    [int]$ConnectPort = 8083,
    [ValidateSet("all", "oracle", "csv")]
    [string]$Pipeline = "all"
)

$ConnectUrl = "http://${ConnectHost}:${ConnectPort}"
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ConnectorsDir = Join-Path (Split-Path -Parent $ScriptDir) "connectors"

Write-Host "Waiting for Kafka Connect at $ConnectUrl ..." -ForegroundColor Cyan
$ready = $false
for ($i = 0; $i -lt 30; $i++) {
    try {
        $res = Invoke-RestMethod -Uri "$ConnectUrl/connectors" -Method Get -TimeoutSec 3 -ErrorAction Stop
        $ready = $true
        break
    } catch {
        Start-Sleep -Seconds 2
    }
}

if (-not $ready) {
    Write-Host "Kafka Connect is not responding at $ConnectUrl" -ForegroundColor Red
    exit 1
}
Write-Host "Kafka Connect is ready." -ForegroundColor Green

function Register-Or-Update-Connector {
    param (
        [string]$Name,
        [string]$FilePath
    )
    if (-not (Test-Path $FilePath)) {
        Write-Host "Connector config file not found: $FilePath" -ForegroundColor Red
        return
    }
    Write-Host "`nRegistering connector: $Name" -ForegroundColor Yellow
    $rawContent = Get-Content $FilePath -Raw
    $jsonObj = $rawContent | ConvertFrom-Json

    # Check if connector exists
    $exists = $false
    try {
        $existing = Invoke-RestMethod -Uri "$ConnectUrl/connectors/$Name" -Method Get -ErrorAction Stop
        $exists = $true
    } catch {}

    if ($exists) {
        Write-Host "   Connector exists - updating config..." -ForegroundColor Gray
        $configObj = if ($jsonObj.PSObject.Properties['config']) { $jsonObj.config } else { $jsonObj }
        $configJson = $configObj | ConvertTo-Json -Depth 10
        $result = Invoke-RestMethod -Uri "$ConnectUrl/connectors/$Name/config" -Method Put -Body $configJson -ContentType "application/json"
        Write-Host "   Updated: $($result.name)" -ForegroundColor Green
    } else {
        Write-Host "   Creating new connector..." -ForegroundColor Gray
        $result = Invoke-RestMethod -Uri "$ConnectUrl/connectors" -Method Post -Body $rawContent -ContentType "application/json"
        Write-Host "   Created: $($result.name)" -ForegroundColor Green
    }
}

if ($Pipeline -eq "oracle" -or $Pipeline -eq "all") {
    Register-Or-Update-Connector -Name "oracle-cdc-source" -FilePath (Join-Path $ConnectorsDir "oracle-source-connector.json")
    Register-Or-Update-Connector -Name "postgres-cdc-sink"  -FilePath (Join-Path $ConnectorsDir "postgres-sink-connector.json")
}

if ($Pipeline -eq "csv" -or $Pipeline -eq "all") {
    Register-Or-Update-Connector -Name "postgres-csv-source" -FilePath (Join-Path $ConnectorsDir "postgres-csv-source-connector.json")
    Register-Or-Update-Connector -Name "postgres-csv-sink"   -FilePath (Join-Path $ConnectorsDir "postgres-csv-sink-connector.json")
}

Write-Host "`nConnector Status:" -ForegroundColor Cyan
$status = Invoke-RestMethod -Uri "$ConnectUrl/connectors?expand=status" -Method Get
$status.PSObject.Properties | ForEach-Object {
    $c = $_.Value.status
    $taskState = if ($c.tasks.Count -gt 0) { $c.tasks[0].state } else { "N/A" }
    Write-Host " - $($c.name): $($c.connector.state) | task 0: $taskState" -ForegroundColor Green
}
