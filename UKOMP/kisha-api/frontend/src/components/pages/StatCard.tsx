interface StatCardProps {
  label: string;
  value: string;
  badgeText: string;
  badgeVariant?: 'emerald' | 'amber' | 'slate' | 'rose';
}

const badgeStyles: Record<string, string> = {
  emerald: 'bg-emerald-50 text-emerald-700',
  amber: 'bg-amber-50 text-amber-700',
  slate: 'bg-slate-100 text-slate-600',
  rose: 'bg-rose-50 text-rose-700',
};

/**
 * Reusable stat card for dashboards.
 * Shows a label, large value, and colored badge.
 */
export function StatCard({ label, value, badgeText, badgeVariant = 'emerald' }: StatCardProps) {
  return (
    <div className="rounded-2xl border border-slate-200 bg-white p-5 shadow-sm">
      <p className="text-sm text-slate-500">{label}</p>
      <p className="mt-2 text-3xl font-semibold text-slate-900">{value}</p>
      <span className={`mt-1 inline-block rounded-full px-2 py-0.5 text-xs ${badgeStyles[badgeVariant]}`}>
        {badgeText}
      </span>
    </div>
  );
}
