## Target Mechanics (What We’re Emulating)
- **4 carried slots** for small items; switch active slot with mouse wheel. Large items require both hands. [[2]](https://www.ign.com/wikis/lethal-company/Lethal_Company_Tips_and_Tricks)
- **While holding a large item:** you can’t pick up anything else or use other items; you must drop it to pocket a small item, then re-pick it up. [[2]](https://www.ign.com/wikis/lethal-company/Lethal_Company_Tips_and_Tricks)
- **Ship storage** is separate from player slots and has a hard capacity limit (often cited as 45). [[1]](https://game8.co/games/Lethal-Company/archives/437501)

## Current State (What Your Mod Already Does)
- 4-slot custom inventory exists and is rendered via a custom HUD: [CompanyHudRenderer.java](file:///c:/Users/tyron/OneDrive/Desktop/LethalComapny/src/main/java/dev/saturn/lethalcompany/client/CompanyHudRenderer.java)
- Scroll clamping to 0–3 is implemented: [PlayerInventoryMixin.java](file:///c:/Users/tyron/OneDrive/Desktop/LethalComapny/src/main/java/dev/saturn/lethalcompany/mixin/PlayerInventoryMixin.java)
- Two-handed items are tag-driven: [two_handed.json](file:///c:/Users/tyron/OneDrive/Desktop/LethalComapny/src/main/resources/data/lethalcompany/tags/items/two_handed.json)

## Gaps / Bugs To Fix
- **Hand/use desync:** Minecraft item use is driven by `PlayerInventory.getMainHandStack()`, but the mod currently only redirects rendering via `getEquippedStack`, so items can appear selected but not behave correctly.
- **Multiplayer/client desync risk:** CompanyInventory has `markDirty()` empty, so server-side changes (pickup/drop) may not reliably sync to client HUD.
- **Large item rules not strict enough:** small item pickup can still succeed while “holding” a large item, which conflicts with the LC rule.
- **Large item slot locking can be bypassed:** scrolling is blocked, but number-key selection may still allow switching away from the large item depending on vanilla input paths.

## Implementation Plan (Edits After You Confirm)
1. **Make the held/usable item match the 4-slot system**
   - Inject into `PlayerInventory.getMainHandStack()` so that in survival it returns:
     - the selected vanilla stack if it’s two-handed, else
     - the selected Company slot stack.
   - This makes item use, interaction, and rendering agree.

2. **Enforce true two-handed behavior**
   - Block changing selected slot when a two-handed item is held (cover scroll + number-key selection).
   - Block picking up small items while holding a two-handed item (must drop first).
   - Keep offhand empty in survival.

3. **Fix sync so the HUD always matches reality**
   - Add a small server→client sync payload that sends the 4 Company slots when they change.
   - Trigger sync when CompanyInventory changes (pickup/drop and screen interactions).

4. **Optional Phase (If you want LC ship storage too)**
   - Add a shared, persistent “Ship Storage” inventory with a configurable 45-slot cap, stored as world/server persistent data.
   - Add a block/UI hook to access it (terminal/block), and optionally a “sell” interaction later.

## Verification (After Implementation)
- Confirm: picking up small item fills 4 slots and updates HUD immediately.
- Confirm: using selected item works (right/left click behavior matches held item).
- Confirm: holding a two-handed item prevents pickups + slot switching until dropped.
- Confirm: multiplayer client HUD stays synced after pickups/drops.

If you confirm, I’ll implement steps 1–3 immediately; step 4 only if you want ship storage included now.