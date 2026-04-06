# Design System Documentation: Organic Minimalism in Commerce

## 1. Overview & Creative North Star: "The Curated Canvas"
This design system moves away from the cluttered, high-density layouts typical of traditional marketplaces. Instead, we embrace **The Curated Canvas**—a philosophy that treats the mobile viewport as a high-end editorial spread rather than a digital catalog.

While inspired by the functional efficiency of Shopee and Tokopedia, this system breathes through **intentional asymmetry**, **exaggerated corner radii**, and **tonal depth**. We replace the "cheap" feel of thin lines and harsh shadows with a sophisticated layering of whites and greens, ensuring the product photography is the hero while the UI acts as a premium frame.

---

## 2. Colors: Tonal Architecture
We utilize a sophisticated Material Design 3 palette, shifting away from flat whites to a range of "Living Surfaces."

### The Palette
- **Primary (Vitality):** `#0d631b` (The anchor) & `#2e7d32` (The interaction).
- **Surface (The Foundation):** Base `#f8f9fa`.
- **Secondary (The Quiet):** `#5e5e5e`.

### The "No-Line" Rule
**Explicit Instruction:** Designers are prohibited from using 1px solid borders to define sections. Layout boundaries must be achieved through:
1.  **Background Shifts:** Place a `surface-container-low` component on a `surface` background to define a zone.
2.  **Negative Space:** Use the 16dp/24dp spacing scale to let elements "float" in their own logical groups.

### Surface Hierarchy & Nesting
Treat the UI as physical layers of fine paper.
- **Background:** `#f8f9fa` (The base).
- **Surface Container (Low/Lowest):** Use `#ffffff` for the most prominent cards to make them "pop" against the off-white background.
- **Surface Container (High):** Use `#e7e8e9` for recessed elements like search input backgrounds or inactive tabs.

### The "Glass & Gradient" Rule
To inject "soul" into the UI:
- **CTAs:** Use a subtle linear gradient from `primary` (#0d631b) to `primary_container` (#2e7d32) at a 135° angle.
- **Floating Elements:** For top navigation bars or floating action buttons, use a `surface` color with 80% opacity and a **24px Backdrop Blur**. This creates a "frosted glass" effect that feels integrated into the environment.

---

## 3. Typography: Editorial Authority
We use a dual-font approach to balance character with legibility.

| Level | Token | Font | Size | Weight | Intent |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Display** | display-lg | Plus Jakarta Sans | 3.5rem | 700 | Large marketing hero moments. |
| **Headline** | headline-md | Plus Jakarta Sans | 1.75rem | 600 | Category headers and page titles. |
| **Title** | title-md | Inter | 1.125rem | 600 | Product names in card listings. |
| **Body** | body-md | Inter | 0.875rem | 400 | Product descriptions and UI labels. |
| **Label** | label-sm | Inter | 0.6875rem | 500 | Metadata, tags, and small captions. |

**Hierarchy Note:** Always pair a `headline-sm` (Plus Jakarta) with a `body-md` (Inter). The contrast between the geometric flair of Jakarta and the neutral utility of Inter creates an "Editorial Modern" feel.

---

## 4. Elevation & Depth: Tonal Layering
Traditional drop shadows are often a sign of "default" design. This system uses **Ambient Light Logic**.

- **The Layering Principle:** Depth is achieved by stacking. A `surface-container-lowest` (#FFFFFF) card sitting on a `surface` (#F8F9FA) background creates a natural lift.
- **Ambient Shadows:** Only use shadows on floating elements (e.g., Bottom Nav, Modals).
    - **Shadow Token:** `0px 8px 24px rgba(25, 28, 29, 0.06)`. 
    - The shadow must be tinted with the `on_surface` color, never pure black.
- **The "Ghost Border":** If a container requires more definition (e.g., a white card on a white background), use a 1px inner stroke of `outline_variant` (#bfcaba) at **15% opacity**. It should be felt, not seen.

---

## 5. Components: Modern Primitives

### Buttons
- **Primary:** `xl` roundness (3rem). Gradient fill (Primary to Primary Container). No border.
- **Secondary:** `xl` roundness. `surface-container-high` fill with `on_surface` text.
- **Scale:** Minimum height 56dp for mobile accessibility.

### Card-Based Listings (Product Cards)
- **Visual Style:** `md` (1.5rem) or `lg` (2rem) corner radius. 
- **Structure:** No borders. The image should be top-aligned with the card edges.
- **Spacing:** Content inside the card must have a 16dp internal padding.
- **Price Tag:** Use `primary` green for the price text to draw the eye immediately.

### Search Bar
- **Style:** "Pill" shape (full roundness).
- **Fill:** `surface-container-low` (#f3f4f5). 
- **Interaction:** On tap, the bar should transition to a `surface` background with a subtle "Ghost Border" to indicate focus.

### Bottom Navigation
- **Style:** Glassmorphic. `surface` at 85% opacity with `20px backdrop-blur`.
- **Indicators:** Use a soft "pill" highlight behind the active icon using `primary_container` at 20% opacity.

---

## 6. Do’s and Don’ts

### Do
- **Do** use asymmetrical spacing (e.g., 24dp top margin, 16dp side margin) to create editorial rhythm.
- **Do** use `high roundness` (16dp+) on all containers to maintain the "Soft Minimalism" vibe.
- **Do** prioritize white space over dividers. If you feel you need a line, try adding 8dp more space instead.

### Don't
- **Don’t** use pure black (#000000) for text. Use `on_surface` (#191c1d) to keep the contrast high but the "vibe" soft.
- **Don’t** use standard Material 2 "Elevation" shadows. They are too heavy for this premium aesthetic.
- **Don’t** cram more than two product cards per row. This is a marketplace for "discovery," not "bulk browsing."
- **Don't** use 100% opaque borders. They break the fluid, organic feel of the layers.