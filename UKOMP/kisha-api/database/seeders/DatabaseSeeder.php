<?php

namespace Database\Seeders;

use App\Models\User;
use App\Modules\Village\Models\Village;
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
        $village = Village::query()->firstOrCreate([
            'name' => 'Desa Sukamaju',
        ], [
            'district' => 'Kecamatan Sukamaju',
            'city' => 'Kabupaten Contoh',
            'province' => 'Jawa Barat',
            'is_active' => true,
        ]);

        foreach (
            [
                [
                    'name' => 'Admin Desa',
                    'email' => 'admin@lokal.test',
                    'role' => 'admin',
                ],
                [
                    'name' => 'Seller Desa',
                    'email' => 'seller@lokal.test',
                    'role' => 'seller',
                ],
                [
                    'name' => 'Buyer Desa',
                    'email' => 'buyer@lokal.test',
                    'role' => 'buyer',
                ],
            ] as $userData
        ) {
            User::query()->updateOrCreate(
                ['email' => $userData['email']],
                [
                    'name' => $userData['name'],
                    'password' => Hash::make('123'),
                    'role' => $userData['role'],
                    'village_id' => $village->id,
                    'is_active' => true,
                ]
            );
        }
    }
}
