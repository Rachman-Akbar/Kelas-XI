# 📱 SI-UMKM Mobile - Dokumentasi

Dokumentasi lengkap aplikasi SI-UMKM (Sistem Informasi UMKM) Mobile berbasis Android dengan Jetpack Compose.

---

## 📚 Daftar Dokumentasi

### 📖 Getting Started

1. **[00-OVERVIEW.md](00-OVERVIEW.md)** - Ringkasan Proyek
   - Arsitektur aplikasi
   - Struktur folder
   - Tech stack & dependencies
   - Role-based system
   - Design system overview

### 🗂️ Technical Documentation

2. **[01-MODELS.md](01-MODELS.md)** - Data Models
   - Semua data class & entities
   - Field explanations
   - Business rules & validation
   - Model relationships
   - Usage examples

3. **[02-SCREENS.md](02-SCREENS.md)** - Screen Documentation
   - 35 screens di 7 modul
   - UI components per screen
   - Navigation flows
   - State management
   - Design patterns

4. **[03-COMPONENTS.md](03-COMPONENTS.md)** - Reusable Components
   - AppBottomNavigation
   - MaterialCard, ProductCard, SalesProductCard
   - Common UI patterns (search bars, badges, dropdowns)
   - Component props & usage

5. **[04-NAVIGATION.md](04-NAVIGATION.md)** - Navigation Architecture
   - Route definitions
   - Navigation structure
   - Role-based routing
   - Deep linking (future)
   - Back stack management

### 🎨 Design & UX

6. **[06-UI-FLOWS.md](06-UI-FLOWS.md)** - User Flows
   - Workflow per role
   - Daily routines
   - Complete transaction flows
   - Error handling flows
   - Navigation patterns

7. **[09-DESIGN-SYSTEM.md](09-DESIGN-SYSTEM.md)** - Design System
   - Color palette
   - Typography scale
   - Spacing system
   - Component specifications
   - Accessibility guidelines
   - Design tokens

### 🎯 Feature Documentation

8. **[05-FEATURES-PENJUALAN.md](05-FEATURES-PENJUALAN.md)** - Sales Role Features
   - Dashboard analytics
   - POS (Point of Sale) system
   - Customer management
   - Checkout process
   - Cart management
   - Business logic

9. **[07-FEATURES-PRODUKSI.md](07-FEATURES-PRODUKSI.md)** - Production Role Features
   - Production dashboard
   - Material management (restock, history)
   - Product management (composition, costing)
   - Production scheduling
   - Status tracking
   - Analytics & reports

10. **[08-FEATURES-KEUANGAN.md](08-FEATURES-KEUANGAN.md)** - Finance Role Features
    - Financial dashboard
    - HPP (Cost of Goods Sold) analysis
    - BEP (Break Even Point) calculator
    - Transaction management
    - Debt & receivable tracking
    - Financial reports

---

## 🚀 Quick Start

### Untuk Developer Baru

1. **Pahami Arsitektur**
   - Baca [00-OVERVIEW.md](00-OVERVIEW.md) untuk struktur proyek
   - Review [01-MODELS.md](01-MODELS.md) untuk data models
   - Lihat [04-NAVIGATION.md](04-NAVIGATION.md) untuk routing

2. **Pelajari Design System**
   - Baca [09-DESIGN-SYSTEM.md](09-DESIGN-SYSTEM.md)
   - Gunakan design tokens (jangan hardcode values)
   - Reuse existing components dari [03-COMPONENTS.md](03-COMPONENTS.md)

3. **Develop Feature**
   - Check [02-SCREENS.md](02-SCREENS.md) untuk screen yang sudah ada
   - Follow patterns dari feature docs (05, 07, 08)
   - Review [06-UI-FLOWS.md](06-UI-FLOWS.md) untuk alur user

### Untuk UI/UX Designer

1. Review [09-DESIGN-SYSTEM.md](09-DESIGN-SYSTEM.md) untuk:
   - Color palette & usage
   - Typography scale
   - Spacing system
   - Component specs

2. Lihat [06-UI-FLOWS.md](06-UI-FLOWS.md) untuk:
   - User journey per role
   - Screen flows
   - Interaction patterns

3. Reference [02-SCREENS.md](02-SCREENS.md) untuk existing screens

### Untuk Product Manager

1. [00-OVERVIEW.md](00-OVERVIEW.md) - Understand project scope
2. Feature docs (05, 07, 08) - Business logic & capabilities
3. [06-UI-FLOWS.md](06-UI-FLOWS.md) - User workflows

---

## 📊 Struktur Aplikasi

### Role-Based System

Aplikasi memiliki 3 role pengguna dengan dashboard & fitur masing-masing:

```
┌─────────────────────────────────────────────────┐
│              LOGIN SCREEN                        │
│                                                  │
│   [Produksi]  [Keuangan]  [Penjualan]          │
└─────────┬───────────┬───────────┬───────────────┘
          │           │           │
          ▼           ▼           ▼
    ┌─────────┐ ┌─────────┐ ┌─────────┐
    │Produksi │ │Keuangan │ │Penjualan│
    │Dashboard│ │Dashboard│ │Dashboard│
    └────┬────┘ └────┬────┘ └────┬────┘
         │           │           │
    4-tab nav   4-tab nav   3-tab nav
```

**Produksi** (4 tabs):

- 🏭 Production Dashboard
- 📦 Material Management
- 📋 Product Catalog
- 🔧 Production Schedule

**Keuangan** (4 tabs):

- 💰 Finance Dashboard
- 📊 HPP-BEP Analysis
- 💳 Debt & Receivable
- 🧾 Transaction History

**Penjualan** (3 tabs):

- 🏠 Sales Dashboard
- 💳 POS (Point of Sale)
- 👥 Customer Management

---

## 🛠️ Tech Stack

### Core

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Design System**: Material Design 3
- **Navigation**: Navigation Compose
- **Architecture**: MVVM

### Key Libraries

- Compose Material3
- Navigation Compose
- Coil (image loading)
- Accompanist (utilities)

### Development Tools

- Android Studio
- Gradle (Kotlin DSL)
- Git

---

## 📱 Screens Overview

### Total: 35 Screens

| Module         | Screens | Description                                                   |
| -------------- | ------- | ------------------------------------------------------------- |
| **Auth**       | 2       | Splash, Login                                                 |
| **Produksi**   | 6       | Dashboard, Materials (3), Products (2)                        |
| **Bahan Baku** | 5       | List, Form, Restock, History, Specific History                |
| **Produk**     | 4       | List, Form, Composition, History                              |
| **Produksi**   | 4       | List, Form, Detail, History                                   |
| **Keuangan**   | 7       | Dashboard, HPP-BEP, Debt/Receivable (3), Transactions (2)     |
| **Penjualan**  | 9       | Dashboard, POS, Checkout, History (2), Customers (3), Catalog |
| **Profile**    | 1       | User Profile                                                  |

Detail lengkap di [02-SCREENS.md](02-SCREENS.md)

---

## 🎨 Design Principles

1. **Simple & Clean**
   - Fokus pada fungsi utama
   - Minimal clutter
   - Clear hierarchy

2. **Consistent**
   - Same patterns across features
   - Reusable components
   - Unified terminology

3. **Accessible**
   - 48dp minimum touch targets
   - High contrast (4.5:1)
   - Screen reader support

4. **Responsive**
   - Works on all Android screen sizes
   - Adaptive layouts
   - Portrait & landscape

---

## 🔍 Find What You Need

### Scenario-Based Navigation

**"Saya ingin membuat screen baru"**
→ [02-SCREENS.md](02-SCREENS.md) - Lihat pattern existing screens  
→ [03-COMPONENTS.md](03-COMPONENTS.md) - Reuse components  
→ [09-DESIGN-SYSTEM.md](09-DESIGN-SYSTEM.md) - Follow design specs

**"Saya ingin menambah fitur pada role tertentu"**
→ [05-FEATURES-PENJUALAN.md](05-FEATURES-PENJUALAN.md) - Sales  
→ [07-FEATURES-PRODUKSI.md](07-FEATURES-PRODUKSI.md) - Production  
→ [08-FEATURES-KEUANGAN.md](08-FEATURES-KEUANGAN.md) - Finance

**"Saya ingin mengubah navigation"**
→ [04-NAVIGATION.md](04-NAVIGATION.md) - Navigation architecture  
→ [06-UI-FLOWS.md](06-UI-FLOWS.md) - User flows

**"Saya ingin tahu business logic"**
→ Feature docs (05, 07, 08) - Complete business rules  
→ [01-MODELS.md](01-MODELS.md) - Data validation

**"Saya ingin update UI design"**
→ [09-DESIGN-SYSTEM.md](09-DESIGN-SYSTEM.md) - Design tokens  
→ [03-COMPONENTS.md](03-COMPONENTS.md) - Component library

---

## 📐 Design Tokens Quick Reference

### Colors

```kotlin
Primary:    #197FE6  // Blue
Success:    #10B981  // Green
Warning:    #F59E0B  // Orange
Error:      #EF4444  // Red
Purple:     #8B5CF6  // Purple (customer VIP)
```

### Spacing

```kotlin
xs:   4dp
sm:   8dp
md:   12dp
lg:   16dp   // Default padding
xl:   24dp
xxl:  32dp
```

### Typography

```kotlin
headlineSmall:  24sp, Bold    // Screen titles
titleMedium:    16sp, SemiBold // Section headers
bodyMedium:     14sp, Normal   // Body text
bodySmall:      12sp, Normal   // Captions
labelLarge:     14sp, Medium   // Buttons
```

### Components

```kotlin
Button height:      48dp
Card radius:        12dp
Icon container:     36dp
Search bar height:  52dp
Badge radius:       6dp
```

---

## 🧪 Testing Guidelines

### Manual Testing Checklist

- [ ] All screens render correctly
- [ ] Navigation works (forward & back)
- [ ] Forms validate inputs
- [ ] Error states display properly
- [ ] Success/loading states work
- [ ] Bottom nav highlights active tab
- [ ] Role-based access works

### Accessibility Testing

- [ ] TalkBack compatibility
- [ ] Minimum touch targets (48dp)
- [ ] Color contrast ratios
- [ ] Content descriptions on icons

---

## 📝 Contribution Guidelines

### Coding Standards

1. **Follow existing patterns**
   - Check similar screens/features
   - Use established components
   - Maintain consistent naming

2. **Use design tokens**

   ```kotlin
   // ❌ DON'T
   .padding(16.dp)

   // ✅ DO
   .padding(Spacing.lg)
   ```

3. **Comment complex logic**

   ```kotlin
   // Calculate BEP (Break Even Point)
   // Formula: Fixed Costs / (Price - Variable Cost)
   val bep = fixedCosts / (sellingPrice - variableCost)
   ```

4. **Handle edge cases**
   - Empty states
   - Error states
   - Loading states
   - Network errors

### Documentation Updates

Saat menambah/mengubah fitur, update:

- [ ] Screen di [02-SCREENS.md](02-SCREENS.md)
- [ ] Component jika reusable di [03-COMPONENTS.md](03-COMPONENTS.md)
- [ ] Navigation route di [04-NAVIGATION.md](04-NAVIGATION.md)
- [ ] Feature doc yang relevan (05, 07, 08)
- [ ] UI flow jika ada perubahan workflow di [06-UI-FLOWS.md](06-UI-FLOWS.md)

---

## 🐛 Known Issues & Limitations

### Current Limitations

1. **Data Persistence**
   - Currently using in-memory data (dummy)
   - Todo: Implement Room database
   - Todo: API integration

2. **Authentication**
   - Role selection only (no real auth)
   - Todo: Implement proper login system

3. **Image Handling**
   - Placeholder icons only
   - Todo: Image upload & display

4. **Reports**
   - Basic analytics only
   - Todo: PDF/Excel export

### Future Enhancements

- [ ] Dark theme support
- [ ] Offline mode with sync
- [ ] Push notifications
- [ ] Multi-language (i18n)
- [ ] Advanced reporting & analytics
- [ ] Barcode scanning
- [ ] Receipt printing
- [ ] Cloud backup

---

## 📞 Support & Contact

### For Developers

- **Issues**: Create issue di repository
- **Questions**: Check documentation first, then ask team
- **Feature Requests**: Discuss with Product Manager

### Documentation Maintenance

Dokumentasi ini dikelola oleh Tim Development.  
Last updated: January 2026

---

## 📄 License

Proprietary - SI-UMKM Mobile App  
© 2026 UKOMP Team. All rights reserved.

---

## 🎯 Project Goals

### Vision

Mempermudah pengelolaan UMKM dengan sistem informasi yang:

- **Simple**: Mudah dipelajari & digunakan
- **Complete**: Mencakup produksi, keuangan, penjualan
- **Efficient**: Otomasi proses manual
- **Insightful**: Analytics untuk keputusan bisnis

### Success Metrics

- User adoption rate
- Time saved vs manual process
- Error reduction in data entry
- User satisfaction (NPS)

---

## 🗺️ Roadmap

### Phase 1 (Current) ✅

- [x] Core role-based navigation
- [x] Basic CRUD operations
- [x] Production management
- [x] Finance tracking
- [x] Sales POS

### Phase 2 (Q1 2026)

- [ ] Room database implementation
- [ ] Backend API integration
- [ ] User authentication
- [ ] Report generation

### Phase 3 (Q2 2026)

- [ ] Advanced analytics
- [ ] Notification system
- [ ] Barcode/QR support
- [ ] Receipt printing

### Phase 4 (Q3 2026)

- [ ] Multi-branch support
- [ ] Cloud sync
- [ ] Mobile web version
- [ ] Advanced reporting

---

**Happy Coding! 🚀**

Untuk pertanyaan atau feedback tentang dokumentasi ini, hubungi tim development.
