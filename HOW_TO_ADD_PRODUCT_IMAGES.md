# How to Add Product Images

## Current Available Images
- `powerdrill.jpg` - Power Drill
- `anglegrinder.jpeg` - Angle Grinder  
- `electricsaw.jpg` - Electric Saw
- `impactwrench.jpg` - Impact Wrench
- `powerplaner.jpg` - Power Planer

## Steps to Add Product Images

### Step 1: Prepare Your Images
1. Find or download product images for each tool
2. Recommended format: **JPG** or **PNG**
3. Recommended size: **400x400 pixels** or larger (square format works best)
4. Name files descriptively (e.g., `jigsaw.jpg`, `circularsaw.jpg`)

### Step 2: Add Images to Project
1. Open Android Studio
2. Navigate to: `app/src/main/res/drawable/`
3. Copy your image files into this folder
4. Make sure filenames are lowercase with underscores (e.g., `hammer_drill.jpg`)

### Step 3: Update Product List
After adding images, update `ProductListActivity.kt` to use the new drawable resources.

For example, if you add `jigsaw.jpg`:
- Change: `R.drawable.ic_product`
- To: `R.drawable.jigsaw`

## Image Naming Convention
Use lowercase with underscores:
- ✅ `jigsaw.jpg`
- ✅ `circular_saw.jpg`
- ✅ `hammer_drill.jpg`
- ❌ `Jigsaw.jpg` (uppercase)
- ❌ `circular-saw.jpg` (hyphens not recommended)

## Where to Get Images
1. **Manufacturer websites** (Bosch, Makita, Dewalt, etc.)
2. **Stock photo sites** (Unsplash, Pexels - search for "power tools")
3. **E-commerce sites** (Amazon, Flipkart product images)
4. **Create your own** using design tools

## Quick Reference: Products Needing Images

### Cutting Tools
- Jigsaw
- Circular Saw
- Reciprocating Saw
- Chainsaw
- Tile Cutter
- Bench Grinder
- Rotary Tool
- Band Saw
- Miter Saw
- Table Saw

### Drilling Tools
- Hammer Drill
- Cordless Drill
- Drill Press
- Screwdriver
- Impact Driver
- Right Angle Drill
- Magnetic Drill

### Finishing Tools
- Router
- Orbital Sander
- Belt Sander
- Polisher
- Detail Sander
- Random Orbital Sander
- Edge Sander
- Buffer
- Heat Gun
- Multi-Tool
- Nail Gun
- Staple Gun

## After Adding Images
1. Rebuild the project
2. The images will automatically appear in the app
3. No code changes needed if you follow the naming convention

