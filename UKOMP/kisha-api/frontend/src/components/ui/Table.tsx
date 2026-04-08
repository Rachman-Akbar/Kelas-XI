import type { ReactNode } from 'react';

interface TableProps {
  columns: Array<{ key: string; label: string; className?: string }>;
  data: Record<string, ReactNode>[];
  className?: string;
}

export function Table({ columns, data, className = '' }: TableProps) {
  return (
    <div className={`overflow-x-auto rounded-lg border border-slate-200 ${className}`}>
      <table className="min-w-full divide-y divide-slate-200">
        <thead className="bg-slate-50">
          <tr>
            {columns.map((col) => (
              <th key={col.key} className={`px-4 py-3 text-left text-xs font-medium uppercase tracking-wider text-slate-500 ${col.className ?? ''}`}>
                {col.label}
              </th>
            ))}
          </tr>
        </thead>
        <tbody className="divide-y divide-slate-200 bg-white">
          {data.map((row, rowIndex) => (
            <tr key={rowIndex} className="transition hover:bg-slate-50">
              {columns.map((col) => (
                <td key={`${rowIndex}-${col.key}`} className="whitespace-nowrap px-4 py-3 text-sm text-slate-700">
                  {row[col.key]}
                </td>
              ))}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
