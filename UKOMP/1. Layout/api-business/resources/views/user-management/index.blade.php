@extends('layouts.master')

@section('title', 'User Management')

@section('breadcrumb')
<li class="inline-flex items-center">
    <span class="text-slate-400 dark:text-text-muted/50 mx-2">/</span>
    <span class="text-sm font-medium text-slate-600 dark:text-text-muted">Settings & Configuration</span>
</li>
<li class="inline-flex items-center">
    <span class="text-slate-400 dark:text-text-muted/50 mx-2">/</span>
    <span class="text-sm font-medium text-slate-900 dark:text-white">User Management</span>
</li>
@endsection

@section('content')
<main class="max-w-7xl mx-auto">
    <!-- Page Header -->
    <div class="flex flex-col md:flex-row justify-between items-start md:items-center gap-4 mb-8">
        <div class="flex flex-col gap-1">
            <h1 class="text-slate-900 dark:text-white text-3xl font-black leading-tight tracking-tight">User Management & Access Control</h1>
            <p class="text-slate-500 dark:text-text-muted text-base font-normal">Manage team members, roles, and granular security permissions.</p>
        </div>
        <div class="flex gap-2">
            <button class="flex items-center gap-2 px-4 py-2 rounded-lg border border-slate-200 dark:border-border-dark text-sm font-medium text-slate-700 dark:text-white hover:bg-slate-50 dark:hover:bg-slate-700 transition-colors">
                <span class="material-symbols-outlined text-base">vpn_key</span> Reset Password
            </button>
            <button class="flex items-center gap-2 px-4 py-2 rounded-lg border border-slate-200 dark:border-border-dark text-sm font-medium text-slate-700 dark:text-white hover:bg-slate-50 dark:hover:bg-slate-700 transition-colors">
                <span class="material-symbols-outlined text-base">manage_accounts</span> Manage Roles
            </button>
            <a href="/user-management/create" class="flex items-center gap-2 px-4 py-2 rounded-lg bg-primary text-background-dark text-sm font-bold hover:bg-opacity-90 transition-colors">
                <span class="material-symbols-outlined text-base">person_add</span> Add User
            </a>
        </div>
    </div>

    <!-- Stats Dashboard -->
    <div class="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-8">
        <div class="flex flex-col gap-2 rounded-xl p-6 border border-slate-200 dark:border-border-dark bg-white dark:bg-transparent">
            <div class="flex items-center justify-between">
                <p class="text-slate-500 dark:text-text-muted text-sm font-medium">Total Users</p>
                <span class="material-symbols-outlined text-slate-400 dark:text-slate-500">group</span>
            </div>
            <p class="text-slate-900 dark:text-white text-3xl font-bold leading-tight">{{ $stats['total'] ?? 0 }}</p>
            <div class="text-xs text-primary font-medium flex items-center gap-1">
                <span class="material-symbols-outlined text-xs">trending_up</span> +{{ $stats['admin'] ?? 0 }} this month
            </div>
        </div>
        <div class="flex flex-col gap-2 rounded-xl p-6 border border-slate-200 dark:border-border-dark bg-white dark:bg-transparent">
            <div class="flex items-center justify-between">
                <p class="text-slate-500 dark:text-text-muted text-sm font-medium">Active Now</p>
                <span class="material-symbols-outlined text-primary">online_prediction</span>
            </div>
            <p class="text-slate-900 dark:text-white text-3xl font-bold leading-tight">{{ $stats['admin'] ?? 0 }}</p>
            <div class="text-xs text-slate-400 dark:text-text-muted font-medium">Current active sessions</div>
        </div>
        <div class="flex flex-col gap-2 rounded-xl p-6 border border-slate-200 dark:border-border-dark bg-white dark:bg-transparent">
            <div class="flex items-center justify-between">
                <p class="text-slate-500 dark:text-text-muted text-sm font-medium">Pending Invites</p>
                <span class="material-symbols-outlined text-slate-400 dark:text-slate-500">mail</span>
            </div>
            <p class="text-slate-900 dark:text-white text-3xl font-bold leading-tight">{{ $stats['staff'] ?? 0 }}</p>
            <div class="text-xs text-amber-500 font-medium flex items-center gap-1">
                <span class="material-symbols-outlined text-xs">schedule</span> Requires attention
            </div>
        </div>
    </div>

    <!-- Tabs Navigation -->
    <div class="border-b border-slate-200 dark:border-border-dark mb-6">
        <div class="flex gap-8">
            <a class="flex items-center gap-2 border-b-[3px] border-primary text-slate-900 dark:text-white pb-3 pt-4 font-bold text-sm tracking-wide" href="#">
                <span class="material-symbols-outlined text-base">list_alt</span> User List
            </a>
            <a class="flex items-center gap-2 border-b-[3px] border-transparent text-slate-400 dark:text-text-muted pb-3 pt-4 font-bold text-sm tracking-wide hover:text-slate-600 dark:hover:text-white transition-colors" href="#">
                <span class="material-symbols-outlined text-base">grid_view</span> Permission Matrix
            </a>
            <a class="flex items-center gap-2 border-b-[3px] border-transparent text-slate-400 dark:text-text-muted pb-3 pt-4 font-bold text-sm tracking-wide hover:text-slate-600 dark:hover:text-white transition-colors" href="#">
                <span class="material-symbols-outlined text-base">history</span> Activity Logs
            </a>
        </div>
    </div>

    <!-- User Table -->
    <div class="bg-white dark:bg-background-dark rounded-xl border border-slate-200 dark:border-border-dark overflow-hidden">
        <div class="overflow-x-auto">
            <table class="w-full text-left border-collapse">
                <thead>
                    <tr class="bg-slate-50 dark:bg-slate-800">
                        <th class="px-6 py-4 text-slate-600 dark:text-white text-xs font-bold uppercase tracking-wider">User</th>
                        <th class="px-6 py-4 text-slate-600 dark:text-white text-xs font-bold uppercase tracking-wider">Email Address</th>
                        <th class="px-6 py-4 text-slate-600 dark:text-white text-xs font-bold uppercase tracking-wider">Role</th>
                        <th class="px-6 py-4 text-slate-600 dark:text-white text-xs font-bold uppercase tracking-wider">Status</th>
                        <th class="px-6 py-4 text-slate-600 dark:text-white text-xs font-bold uppercase tracking-wider">Last Login</th>
                        <th class="px-6 py-4 text-slate-600 dark:text-text-muted text-xs font-bold uppercase tracking-wider text-right">Actions</th>
                    </tr>
                </thead>
                <tbody class="divide-y divide-slate-200 dark:divide-border-dark">
                    @forelse($users as $user)
                    <tr class="hover:bg-slate-50 dark:hover:bg-primary/5 transition-colors group">
                        <td class="px-6 py-4">
                            <div class="flex items-center gap-3">
                                <div class="h-10 w-10 rounded-full bg-slate-200 dark:bg-slate-700 flex items-center justify-center">
                                    <span class="text-sm font-semibold text-slate-700 dark:text-white">
                                        {{ strtoupper(substr($user->name, 0, 2)) }}
                                    </span>
                                </div>
                                <div class="text-sm font-semibold text-slate-900 dark:text-white">{{ $user->name }}</div>
                            </div>
                        </td>
                        <td class="px-6 py-4 text-sm text-slate-500 dark:text-text-muted">{{ $user->email }}</td>
                        <td class="px-6 py-4">
                            @if($user->role == 'admin')
                                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-primary/20 text-primary border border-primary/30">Admin</span>
                            @elseif($user->role == 'manager')
                                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-blue-100 text-blue-700 dark:bg-blue-900/40 dark:text-blue-300 border border-blue-200 dark:border-blue-800">Manager</span>
                            @else
                                <span class="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-slate-100 text-slate-600 dark:bg-slate-700 dark:text-white border border-slate-200 dark:border-border-dark">Staff</span>
                            @endif
                        </td>
                        <td class="px-6 py-4">
                            <div class="flex items-center gap-2">
                                <div class="h-2 w-2 rounded-full bg-primary"></div>
                                <span class="text-sm text-slate-900 dark:text-white">Active</span>
                            </div>
                        </td>
                        <td class="px-6 py-4 text-sm text-slate-500 dark:text-text-muted">{{ $user->created_at->diffForHumans() }}</td>
                        <td class="px-6 py-4 text-right">
                            <div class="flex justify-end">
                                <div class="relative" x-data="{ open: false }" @click.away="open = false">
                                    <button @click="open = !open" class="text-slate-400 dark:text-text-muted hover:text-primary dark:hover:text-primary transition-colors">
                                        <span class="material-symbols-outlined">more_vert</span>
                                    </button>
                                    <div x-show="open" x-transition class="absolute right-0 top-full mt-1 w-48 bg-white dark:bg-background-dark border border-slate-200 dark:border-border-dark rounded-lg shadow-lg py-1 z-50">
                                        <a href="#" class="block px-4 py-2 text-sm text-slate-700 dark:text-white hover:bg-slate-100 dark:hover:bg-slate-700">
                                            <span class="material-symbols-outlined text-base mr-2 inline">visibility</span>View Details
                                        </a>
                                        <a href="#" class="block px-4 py-2 text-sm text-slate-700 dark:text-white hover:bg-slate-100 dark:hover:bg-slate-700">
                                            <span class="material-symbols-outlined text-base mr-2 inline">edit</span>Edit User
                                        </a>
                                        <a href="#" class="block px-4 py-2 text-sm text-slate-700 dark:text-white hover:bg-slate-100 dark:hover:bg-slate-700">
                                            <span class="material-symbols-outlined text-base mr-2 inline">vpn_key</span>Reset Password
                                        </a>
                                        <hr class="my-1">
                                        <a href="#" class="block px-4 py-2 text-sm text-red-600 dark:text-red-400 hover:bg-slate-100 dark:hover:bg-slate-700">
                                            <span class="material-symbols-outlined text-base mr-2 inline">delete</span>Delete
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </td>
                    </tr>
                    @empty
                    <tr>
                        <td colspan="6" class="px-6 py-16 text-center">
                            <div class="flex flex-col items-center justify-center text-slate-500 dark:text-slate-400">
                                <span class="material-symbols-outlined text-6xl mb-3 opacity-50">person_off</span>
                                <p class="text-lg font-medium">No users found</p>
                                <p class="text-sm">Get started by adding your first team member</p>
                            </div>
                        </td>
                    </tr>
                    @endforelse
                </tbody>
            </table>
        </div>
        
        @if($users->hasPages())
        <div class="px-6 py-4 flex items-center justify-between border-t border-slate-200 dark:border-border-dark">
            <p class="text-xs text-slate-500 dark:text-text-muted">
                Showing {{ $users->firstItem() }} - {{ $users->lastItem() }} of {{ $users->total() }} users
            </p>
            <div class="flex gap-2">
                {{ $users->links('pagination::tailwind') }}
            </div>
        </div>
        @endif
    </div>
</main>
@endsection

@push('scripts')
<script>
// Add Alpine.js functionality for dropdowns
document.addEventListener('alpine:init', () => {
    // Dropdown functionality handled by Alpine.js x-data
});

// Generic action handler for bulk operations
function handleAction(action, id = null) {
    const selected = Array.from(document.querySelectorAll('.row-checkbox:checked')).map(cb => cb.value);
    
    switch(action) {
        case 'bulk-delete':
            if (selected.length === 0) {
                alert('Pilih minimal 1 user untuk dihapus');
                return;
            }
            if (confirm(`Yakin ingin menghapus ${selected.length} user terpilih?`)) {
                console.log('Bulk delete:', selected);
                // TODO: Implement bulk delete API
            }
            break;
            
        case 'bulk-export':
            if (selected.length === 0) {
                alert('Pilih minimal 1 user untuk diexport');
                return;
            }
            console.log('Bulk export:', selected);
            // TODO: Implement bulk export
            break;
    }
}

// Checkbox select all functionality
document.getElementById('selectAll')?.addEventListener('change', function() {
    const checkboxes = document.querySelectorAll('.row-checkbox');
    checkboxes.forEach(cb => cb.checked = this.checked);
});
</script>
@endpush
