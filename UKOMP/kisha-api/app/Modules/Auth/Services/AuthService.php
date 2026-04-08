<?php

namespace App\Modules\Auth\Services;

use App\Models\User;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Password;
use Illuminate\Support\Str;
use Illuminate\Validation\ValidationException;

class AuthService
{
    /**
     * Register User
     */
    public function register(array $payload): array
    {
        // cek email sudah ada
        if (User::where('email', $payload['email'])->exists()) {
            throw ValidationException::withMessages([
                'email' => ['Email already registered.'],
            ]);
        }

        $user = User::query()->create([
            'name'       => $payload['name'],
            'email'      => $payload['email'],
            'password'   => Hash::make($payload['password']), // ✅ hash password
            'role'       => $payload['role'],
            'village_id' => $payload['village_id'] ?? null,
            'is_active'  => true,
        ]);

        // hapus token lama (optional)
        $user->tokens()->delete();

        $token = $user->createToken(
            $payload['device_name'] ?? 'web'
        )->plainTextToken;

        return [
            $user->load('village'),
            $token
        ];
    }

    /**
     * Login User
     */
    public function login(array $payload): array
    {
        $user = User::where('email', $payload['email'])->first();

        if (! $user || ! Hash::check($payload['password'], $user->password)) {
            throw ValidationException::withMessages([
                'email' => ['Invalid credentials.'],
            ]);
        }

        if (! $user->is_active) {
            throw ValidationException::withMessages([
                'email' => ['User is inactive. Please contact admin.'],
            ]);
        }

        // optional: cek verifikasi email
        // if (! $user->hasVerifiedEmail()) {
        //     throw ValidationException::withMessages([
        //         'email' => ['Please verify your email first.'],
        //     ]);
        // }

        // hapus token lama
        $user->tokens()->delete();

        $token = $user->createToken(
            $payload['device_name'] ?? 'web'
        )->plainTextToken;

        return [
            $user->load('village'),
            $token
        ];
    }

    /**
     * Forgot Password
     */
    public function forgotPassword(string $email): string
    {
        return Password::sendResetLink([
            'email' => $email,
        ]);
    }

    /**
     * Reset Password
     */
    public function resetPassword(array $payload): string
    {
        return Password::reset(
            $payload,
            function (User $user, string $password): void {

                $user->forceFill([
                    'password' => Hash::make($password), // ✅ wajib hash
                    'remember_token' => Str::random(60),
                ])->save();

                // logout semua device
                $user->tokens()->delete();
            }
        );
    }
}