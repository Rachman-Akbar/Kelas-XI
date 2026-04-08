import { NumberedList } from '@/components/pages';

interface AdminTaskCardProps {
  tasks: string[];
}

export function AdminTaskCard({ tasks }: AdminTaskCardProps) {
  return (
    <div className="rounded-[1.75rem] border border-white/10 bg-white/5 p-6">
      <p className="text-sm uppercase tracking-[0.25em] text-slate-400">Three admin priorities</p>
      <h2 className="mt-2 text-2xl font-semibold tracking-tight text-white">Core scaffolding for MVP governance</h2>
      <div className="mt-6 space-y-4">
        {tasks.map((task, index) => (
          <div key={task} className="flex gap-4 rounded-2xl border border-white/10 bg-slate-900/70 p-4">
            <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-emerald-500 text-sm font-semibold text-white">
              {String(index + 1).padStart(2, '0')}
            </div>
            <p className="text-sm leading-6 text-slate-300">{task}</p>
          </div>
        ))}
      </div>
    </div>
  );
}
