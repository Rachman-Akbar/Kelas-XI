# 🗄️ MySQL Database Setup Guide

## Database Information

-   **Database Name:** `aplikasimonitoringkelas`
-   **Character Set:** `utf8mb4`
-   **Collation:** `utf8mb4_unicode_ci`

## Setup Methods

### Method 1: MySQL Command Line (Recommended)

1. **Open Command Prompt or PowerShell**

2. **Login to MySQL:**
    ```bash
    mysql -u root -p
    ```
3. **Create Database:**

    ```sql
    CREATE DATABASE aplikasimonitoringkelas
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
    ```

4. **Verify Database Created:**

    ```sql
    SHOW DATABASES;
    ```

    You should see `aplikasimonitoringkelas` in the list.

5. **Exit MySQL:**

    ```sql
    EXIT;
    ```

6. **Run Laravel Migrations:**
    ```bash
    cd aplikasimonitoring-api
    php artisan migrate:fresh --seed
    ```

### Method 2: Using SQL Script

1. **Use the provided SQL file:**

    ```bash
    mysql -u root -p < create-database.sql
    ```

2. **Run Laravel Migrations:**
    ```bash
    php artisan migrate:fresh --seed
    ```

### Method 3: PHPMyAdmin (GUI)

1. **Open PHPMyAdmin** in your browser (usually http://localhost/phpmyadmin)

2. **Click "New" or "Databases"**

3. **Create Database:**

    - **Database name:** `aplikasimonitoringkelas`
    - **Collation:** `utf8mb4_unicode_ci`
    - Click **"Create"**

4. **Run Laravel Migrations:**
    ```bash
    cd aplikasimonitoring-api
    php artisan migrate:fresh --seed
    ```

### Method 4: Using Helper Script (Windows)

1. **Run the batch file:**
    ```bash
    create-database.bat
    ```
2. **Enter MySQL password when prompted**

3. **Run Laravel Migrations:**
    ```bash
    php artisan migrate:fresh --seed
    ```

## Configuration

### .env File

The `.env` file is already configured for MySQL:

```ini
DB_CONNECTION=mysql
DB_HOST=127.0.0.1
DB_PORT=3306
DB_DATABASE=aplikasimonitoringkelas
DB_USERNAME=root
DB_PASSWORD=
```

### If You Have MySQL Password

Update the `.env` file:

```ini
DB_PASSWORD=your_mysql_password
```

### If You Want Different Port

```ini
DB_PORT=3307
```

## Verification

### Check Database Connection

```bash
php artisan db:show
```

Expected output:

```
MySQL 8.x ............................................. 127.0.0.1:3306
Database ........................................... aplikasimonitoringkelas
```

### Check Tables Created

After running migrations:

```bash
php artisan db:table --database=mysql
```

Or via MySQL:

```sql
mysql -u root -p
USE aplikasimonitoringkelas;
SHOW TABLES;
```

Expected tables:

-   `cache`
-   `cache_locks`
-   `failed_jobs`
-   `job_batches`
-   `jobs`
-   `migrations`
-   `password_reset_tokens`
-   `personal_access_tokens`
-   `sessions`
-   `users`

### Check Test Data

```sql
SELECT * FROM users;
```

Should return 7 users with different roles.

## Common Issues

### Error: Database already exists

**Solution:**

```sql
DROP DATABASE IF EXISTS aplikasimonitoringkelas;
CREATE DATABASE aplikasimonitoringkelas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Error: Access denied for user 'root'

**Solutions:**

1. **Check password:**
   Update `.env` with correct password

2. **Create new user:**

    ```sql
    CREATE USER 'monitoring'@'localhost' IDENTIFIED BY 'your_password';
    GRANT ALL PRIVILEGES ON aplikasimonitoringkelas.* TO 'monitoring'@'localhost';
    FLUSH PRIVILEGES;
    ```

    Update `.env`:

    ```ini
    DB_USERNAME=monitoring
    DB_PASSWORD=your_password
    ```

### Error: MySQL server is not running

**Solutions:**

**Windows:**

```bash
# Start MySQL via Services
net start MySQL80

# Or via XAMPP Control Panel
# Click "Start" for MySQL
```

**Check MySQL Status:**

```bash
# Via XAMPP
Check XAMPP Control Panel

# Via Services
services.msc → Look for MySQL
```

### Error: Connection refused

**Solutions:**

1. **Check MySQL is running:**

    ```bash
    netstat -an | findstr :3306
    ```

2. **Check firewall:**
   Allow MySQL port 3306

3. **Verify host:**
   Try `localhost` instead of `127.0.0.1`:
    ```ini
    DB_HOST=localhost
    ```

## Database Management

### Reset Database (Keep Structure)

```bash
php artisan migrate:refresh --seed
```

### Reset Database (Fresh Start)

```bash
php artisan migrate:fresh --seed
```

### Backup Database

```bash
mysqldump -u root -p aplikasimonitoringkelas > backup.sql
```

### Restore Database

```bash
mysql -u root -p aplikasimonitoringkelas < backup.sql
```

### Drop All Tables

```bash
php artisan db:wipe
```

## Production Setup

For production environment:

1. **Create production database:**

    ```sql
    CREATE DATABASE aplikasimonitoringkelas_production
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
    ```

2. **Create dedicated user:**

    ```sql
    CREATE USER 'monitoring_prod'@'localhost' IDENTIFIED BY 'secure_password';
    GRANT SELECT, INSERT, UPDATE, DELETE ON aplikasimonitoringkelas_production.*
    TO 'monitoring_prod'@'localhost';
    FLUSH PRIVILEGES;
    ```

3. **Update production `.env`:**
    ```ini
    DB_DATABASE=aplikasimonitoringkelas_production
    DB_USERNAME=monitoring_prod
    DB_PASSWORD=secure_password
    ```

## Switching Between SQLite and MySQL

### Switch to SQLite

1. **Update `.env`:**

    ```ini
    DB_CONNECTION=sqlite
    # Comment out MySQL config
    # DB_HOST=127.0.0.1
    # DB_PORT=3306
    # DB_DATABASE=aplikasimonitoringkelas
    # DB_USERNAME=root
    # DB_PASSWORD=
    ```

2. **Create SQLite file:**

    ```bash
    touch database/database.sqlite
    ```

3. **Run migrations:**
    ```bash
    php artisan migrate:fresh --seed
    ```

### Switch to MySQL

1. **Update `.env`:**

    ```ini
    DB_CONNECTION=mysql
    DB_HOST=127.0.0.1
    DB_PORT=3306
    DB_DATABASE=aplikasimonitoringkelas
    DB_USERNAME=root
    DB_PASSWORD=
    ```

2. **Ensure database exists**

3. **Run migrations:**
    ```bash
    php artisan migrate:fresh --seed
    ```

## Useful Commands

```bash
# Test database connection
php artisan db:show

# Show database info
php artisan db

# List all tables
php artisan db:table

# Monitor database queries
php artisan db:monitor

# Check migration status
php artisan migrate:status

# Rollback last migration
php artisan migrate:rollback

# Rollback all migrations
php artisan migrate:reset
```

## Need Help?

-   Check MySQL logs: Usually in `C:\xampp\mysql\data\mysql_error.log` (XAMPP)
-   Laravel logs: `storage/logs/laravel.log`
-   Test connection: `php artisan tinker` then `DB::connection()->getPdo()`

---

**Database setup complete! Ready to use with Laravel API.** 🚀
