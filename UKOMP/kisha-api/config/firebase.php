<?php

return [
    'enabled' => (bool) env('FIREBASE_ENABLED', false),
    'project_id' => env('FIREBASE_PROJECT_ID'),
    'database_url' => env('FIREBASE_DATABASE_URL'),
    'credentials' => env('FIREBASE_CREDENTIALS', base_path('storage/app/firebase/service-account.json')),
    'firestore' => [
        'products_collection' => env('FIREBASE_FIRESTORE_PRODUCTS_COLLECTION', 'products'),
    ],
];
