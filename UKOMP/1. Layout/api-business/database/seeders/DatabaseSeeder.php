<?php

namespace Database\Seeders;

use App\Models\User;
use Illuminate\Database\Console\Seeds\WithoutModelEvents;
use Illuminate\Database\Seeder;
use Illuminate\Support\Facades\Hash;

class DatabaseSeeder extends Seeder
{
    use WithoutModelEvents;

    /**
     * Seed the application's database.
     */
    public function run(): void
    {
        // ========================================
        // AKUN DENGAN FORMAT: [role]@gmail.com
        // PASSWORD: 123456
        // ========================================

        // Owner Account
        User::create([
            'name' => 'Owner',
            'email' => 'owner@gmail.com',
            'password' => Hash::make('123456'),
            'role' => User::ROLE_OWNER,
            'status' => User::STATUS_AKTIF,
            'no_telepon' => '081234567801',
        ]);

        // Admin Account
        User::create([
            'name' => 'Admin',
            'email' => 'admin@gmail.com',
            'password' => Hash::make('123456'),
            'role' => User::ROLE_ADMIN,
            'status' => User::STATUS_AKTIF,
            'no_telepon' => '081234567802',
        ]);

        // Manager Account
        User::create([
            'name' => 'Manager',
            'email' => 'manager@gmail.com',
            'password' => Hash::make('123456'),
            'role' => User::ROLE_MANAGER,
            'status' => User::STATUS_AKTIF,
            'no_telepon' => '081234567803',
        ]);

        // Produksi Account
        User::create([
            'name' => 'Produksi',
            'email' => 'produksi@gmail.com',
            'password' => Hash::make('123456'),
            'role' => User::ROLE_PRODUKSI,
            'status' => User::STATUS_AKTIF,
            'no_telepon' => '081234567804',
        ]);

        // Kasir Account
        User::create([
            'name' => 'Kasir',
            'email' => 'kasir@gmail.com',
            'password' => Hash::make('123456'),
            'role' => User::ROLE_KASIR,
            'status' => User::STATUS_AKTIF,
            'no_telepon' => '081234567805',
        ]);

        // Keuangan Account
        User::create([
            'name' => 'Keuangan',
            'email' => 'keuangan@gmail.com',
            'password' => Hash::make('123456'),
            'role' => User::ROLE_KEUANGAN,
            'status' => User::STATUS_AKTIF,
            'no_telepon' => '081234567806',
        ]);

        // Call other seeders
        $this->call([
            MaterialSeeder::class,
            ProductSeeder::class,
            CustomerSeeder::class,
            ProductCompositionSeeder::class,
            ProductionSeeder::class,
            OrderSeeder::class,
            TransactionSeeder::class,
            ActivityLogSeeder::class,
        ]);
    }
}
