export function SellerFooter() {
  return (
    <footer className="border-t border-slate-200 bg-white px-6 py-4">
      <div className="flex items-center justify-between text-xs text-slate-500">
        <p>&copy; {new Date().getFullYear()} Seller Marketplace. All rights reserved.</p>
        <p>Version 1.0.0</p>
      </div>
    </footer>
  );
}
