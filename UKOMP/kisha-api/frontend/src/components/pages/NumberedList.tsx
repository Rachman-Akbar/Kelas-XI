interface NumberedListProps {
  sectionLabel?: string;
  title: string;
  items: string[];
}

/**
 * Reusable numbered list section for dashboards.
 * Renders a list of items with numbered circles.
 */
export function NumberedList({ sectionLabel, title, items }: NumberedListProps) {
  return (
    <section className="mt-8 rounded-[1.5rem] border border-slate-200 bg-white p-6 shadow-sm">
      {sectionLabel && (
        <p className="text-sm uppercase tracking-[0.25em] text-slate-400">{sectionLabel}</p>
      )}
      <h2 className="mt-2 text-xl font-semibold tracking-tight text-slate-900">{title}</h2>
      <div className="mt-6 space-y-4">
        {items.map((item, index) => (
          <div key={item} className="flex gap-4 rounded-2xl border border-slate-200 bg-slate-50 p-4">
            <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-full bg-emerald-500 text-sm font-semibold text-white">
              {String(index + 1).padStart(2, '0')}
            </div>
            <p className="text-sm leading-6 text-slate-600">{item}</p>
          </div>
        ))}
      </div>
    </section>
  );
}
