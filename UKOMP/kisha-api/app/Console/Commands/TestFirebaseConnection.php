<?php

namespace App\Console\Commands;

use App\Services\Firebase\FirestoreProductSyncService;
use Illuminate\Console\Command;

class TestFirebaseConnection extends Command
{
    protected $signature = 'firebase:test';
    protected $description = 'Test Firebase Firestore connection';

    public function __construct(private readonly FirestoreProductSyncService $syncService)
    {
        parent::__construct();
    }

    public function handle(): int
    {
        $this->info('Testing Firebase Firestore connection...');
        $this->newLine();

        // Check config
        $enabled = (bool) config('firebase.enabled');
        $projectId = config('firebase.project_id');
        $credentials = config('firebase.credentials');

        $this->line("FIREBASE_ENABLED: <fg=".($enabled ? 'green' : 'red').">{$enabled}</>");
        $this->line("FIREBASE_PROJECT_ID: <fg=".($projectId ? 'green' : 'red').">{$projectId}</>");
        $this->line("FIREBASE_CREDENTIALS: <fg=".($credentials ? 'green' : 'red').">{$credentials}</>");

        $this->newLine();

        if (! $enabled) {
            $this->error('Firebase is disabled in config. Enable it by setting FIREBASE_ENABLED=true in .env');
            return self::FAILURE;
        }

        if (! $projectId) {
            $this->error('FIREBASE_PROJECT_ID is not set in .env');
            return self::FAILURE;
        }

        if (! $credentials || ! file_exists($credentials)) {
            $this->error("Service account file not found: {$credentials}");
            return self::FAILURE;
        }

        // Test credentials file is valid JSON
        try {
            $json = json_decode(file_get_contents($credentials), true);
            if (! $json || ! isset($json['project_id'])) {
                throw new \Exception('Invalid service account JSON format');
            }
            $this->line("✓ Service account file is valid");
        } catch (\Throwable $e) {
            $this->error("Service account file error: {$e->getMessage()}");
            return self::FAILURE;
        }

        $this->line('Attempting to connect to Firestore...');

        $result = $this->syncService->testConnection();

        if (! ($result['ok'] ?? false)) {
            $this->error('Firestore connection failed: ' . ($result['message'] ?? 'Unknown error'));
            return self::FAILURE;
        }

        $this->line('✓ Firestore connection successful!');
        $this->line('✓ ' . ($result['message'] ?? 'Connection check passed.'));

        $this->newLine();
        $this->info('✓ Firebase Firestore is properly configured and connected!');

        return self::SUCCESS;
    }
}
