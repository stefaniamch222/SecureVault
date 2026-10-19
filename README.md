SecureVault API

A secure, lightweight RESTful API built with Spring Boot and Java 25 to manage encrypted secrets (such as API keys, database passwords, and tokens). The application encrypts sensitive values at rest using AES-GCM encryption with a master key injected via environment variables and stores them in a local SQLite database.

🚀 Features

Authenticated Access: Secured via Spring Security (HTTP Basic Authentication).

At-Rest Encryption: Dynamic AES-GCM encryption for stored secrets using an external master key.

Local Persistence: Zero-configuration embedded storage with SQLite.

Externalized Configuration: Loads environment variables from a local .env file.

Health Monitoring: Integrated Spring Boot Actuator endpoints for liveness and readiness checks.

📋 Prerequisites

Before running the project, ensure you have the following installed on your system:

Java Development Kit (JDK 25) or compatible LTS version.

Apache Maven (3.8+ recommended).

PowerShell (for Windows users) or a Bash terminal (Linux/macOS).

🛠️ Environment Configuration

Create a .env file in the root directory of the project (at the same level as pom.xml) containing the required master encryption key and application settings:

VAULT_ENCRYPTION_KEY=QuestaEUnaChiaveSegretaMoltoLungaPerIlVaultDiTest2026!
DB_PASSWORD=super-segreta-123


🏃 Getting Started & How to Run

Step 1: Open Terminal & Navigate to Project Directory

Open PowerShell and navigate to the project root directory:

cd C:\Users\SecureVault


Step 2: Load Environment Variables

Execute the following command in PowerShell to inject .env parameters into your active session environment:

Get-Content .env | Where-Object { $_ -match '=' -and $_ -notmatch '^\s*#' } | ForEach-Object { $name, $value = $_.Split('=', 2); [Environment]::SetEnvironmentVariable($name.Trim(), $value.Trim()) }


You can verify that the environment variable was loaded correctly:

echo $env:VAULT_ENCRYPTION_KEY


Step 3: Launch the Application

Run the Spring Boot application using Maven:

mvn spring-boot:run


Once you see the following log entry, the application is ready and listening on port 8080:

Started SecureVaultApplication in X.XXX seconds


🧪 Testing the Application

Test 1: Actuator Health Check

Verify that the application and database are operational.

URL: http://localhost:8080/actuator/health

Method: GET

Expected Response:

{
  "groups": ["liveness", "readiness"],
  "status": "UP"
}


Test 2: Retrieve Encrypted Secret via PowerShell

Open a second PowerShell window (keeping the server running in the first window) and execute:

$pair = "admin:password123"
$encoded = [System.Convert]::ToBase64String([System.Text.Encoding]::ASCII.GetBytes($pair))
$headers = @{ Authorization = "Basic $encoded" }

Invoke-RestMethod -Uri "http://localhost:8080/api/vault/secrets/DB_PASSWORD" -Method Get -Headers $headers


Expected Response:

value             name       
-----             ----       
super-segreta-123 DB_PASSWORD


Test 3: Test via Browser (Incognito Mode)

Open a Private/Incognito Window in your browser.

Navigate to http://localhost:8080/api/vault/secrets.

Enter your credentials in the Spring Security prompt:

Username: admin

Password: password123

Expected Response: A JSON array listing the available secret keys (e.g., ["DB_PASSWORD"]).

Test 4: Security Verification (Unauthorized Access)

Attempting to query an endpoint without credentials should be rejected by Spring Security:

Invoke-RestMethod -Uri "http://localhost:8080/api/vault/secrets/DB_PASSWORD" -Method Get


Expected Response: 401 Unauthorized.

🛑 Stopping the Server

To stop the Spring Boot server gracefully, switch back to the PowerShell terminal running the application and press:

Ctrl + C
