# GameView Class - Complete Explanation

## **Overview**

`GameView` is the main UI component that displays the game. It extends JavaFX's `StackPane` and manages all visual elements: room backgrounds, clickable hotspots, puzzles, inventory, and UI controls.

---

## **Class Structure**

### **Key Components (Private Fields)**

```java
- backgroundImage: ImageView          // Room background image
- roomImageMapper: RoomImageMapper    // Loads room images
- directionControllerView: DirectionControllerView  // Direction buttons (N/S/E/W)
- hotspotsLayer: Pane                 // Layer containing clickable hotspots
- currentPuzzleView: PuzzleView       // Currently displayed puzzle overlay
- inventoryView: InventoryView       // Inventory overlay
- inventoryButton: Button             // "i" button to open inventory
- saveIndicatorLabel: Label           // "Game saved" indicator
- hotspotButtons: Map<String, Button> // Tracks hotspot buttons by ID
```

### **Public Event**

```java
public Event<OnHotspotClickedEventArgs> OnHotspotClicked
```

This event is fired when a hotspot is clicked. The controller subscribes to it to handle interactions.

---

## **Public Methods Used by Controller**

### **1. `showRoom(RoomViewModel viewModel)`**
**Called by:** `GameController.loadAndShowRoom()`

**Purpose:** Displays a new room with its background, hotspots, and updates direction buttons.

**What it does:**
1. Loads and displays the room's background image
2. Clears old hotspots
3. Adds new hotspots from the viewModel
4. Updates direction buttons based on available exits

**Usage:**
```java
RoomViewModel viewModel = roomDataService.loadRoom(room);
view.showRoom(viewModel);
```

---

### **2. `updateInventory(Inventory playerInventory, Inventory roomInventory)`**
**Called by:** `GameController` (multiple places after inventory changes)

**Purpose:** Updates the inventory view with current player and room inventories.

**What it does:**
- Passes both inventories to the `InventoryView` to display current items

**Usage:**
```java
view.updateInventory(player.getInventory(), getCurrentRoom().getInventory());
```

---

### **3. `setInventoryChangeCallback(BiConsumer<Item, Boolean> callback)`**
**Called by:** `GameController` constructor

**Purpose:** Sets up a callback that fires when items are moved between inventories.

**What it does:**
- Connects the `InventoryView`'s item movement events to the controller
- The callback receives: `(item, fromRoomInventory)` where `fromRoomInventory` is `true` if item came from room inventory

**Usage:**
```java
view.setInventoryChangeCallback((item, fromRoomInventory) -> {
    // Handle item movement (e.g., banana peel slip mechanic)
    if (item.getName().equals("bananaPeel") && !fromRoomInventory) {
        handleBananaPeelSlip();
    }
    view.updateInventory(player.getInventory(), getCurrentRoom().getInventory());
});
```

---

### **4. `showPuzzleView(PuzzleView puzzleView)`**
**Called by:** `GameController.showPuzzleView()`

**Purpose:** Displays a puzzle overlay on top of the room view.

**What it does:**
- Removes any existing puzzle view
- Adds the new puzzle view as an overlay (on top of everything)

**Usage:**
```java
PuzzleView puzzleView = new GuardPuzzleView(puzzle);
view.showPuzzleView(puzzleView);
```

---

### **5. `hidePuzzleView()`**
**Called by:** `GameController.handleInteractionResult()` when puzzle is solved

**Purpose:** Removes the current puzzle overlay.

**What it does:**
- Removes the puzzle view from the display
- Sets `currentPuzzleView` to null

**Usage:**
```java
if (result.getType() == InteractionResult.ResultType.PUZZLE_SOLVED) {
    view.hidePuzzleView();
}
```

---

### **6. `applyViewUpdates(List<String> updates)`**
**Called by:** `GameController.handleInteractionResult()`

**Purpose:** Applies view changes requested by puzzles/interactions (e.g., enable/disable hotspots).

**What it does:**
- Parses update strings like:
  - `"enableHotspot:securityDoor"` → Enables a hotspot
  - `"disableHotspot:ticketMachine"` → Disables a hotspot
  - `"playAnimation:doorOpen"` → Plays an animation (TODO)

**Usage:**
```java
// Puzzle returns view updates when solved
InteractionResult result = puzzle.interact("complete", null);
view.applyViewUpdates(result.getViewUpdates());
```

---

### **7. `showSaveIndicator()`**
**Called by:** `GameController.movePlayer()` after auto-save

**Purpose:** Shows a "Game saved" message in the top-right corner.

**What it does:**
- Fades in the indicator
- Shows for 2 seconds
- Fades out automatically

**Usage:**
```java
if (saveService.saveGame(getCurrentRoom(), player.getInventory())) {
    view.showSaveIndicator();
}
```

---

### **8. `showMessageOverlay(String message)`**
**Called by:** `GameController.handleBananaPeelSlip()` and other places

**Purpose:** Displays a temporary message overlay (e.g., "Someone slipped on the banana peel!").

**What it does:**
- Creates a semi-transparent overlay with the message
- Includes an "OK" button to dismiss
- Fades in when shown

**Usage:**
```java
view.showMessageOverlay(
    "Someone slipped on the banana peel!\n" +
    "They dropped a document.\n" +
    "You pick it up."
);
```

---

### **9. `getDirectionControllerView()`**
**Called by:** `GameController.setupEventSubscriptions()`

**Purpose:** Returns the direction controller so the controller can subscribe to button events.

**What it does:**
- Returns the `DirectionControllerView` instance
- Controller uses it to listen for direction button clicks

**Usage:**
```java
view.getDirectionControllerView().OnDirectionButtonPressed.addListener(
    (sender, args) -> onDirectionButtonPressed(sender, args)
);
```

---

### **10. `updateHotspot(String hotspotId, boolean enabled)`**
**Called by:** `GameView.applyViewUpdates()` (indirectly via controller)

**Purpose:** Enables or disables a specific hotspot.

**What it does:**
- Finds the hotspot button by ID
- Enables/disables it (disabled hotspots can't be clicked)

**Usage:**
```java
// Called internally by applyViewUpdates()
view.updateHotspot("securityDoor", true);  // Enable
view.updateHotspot("ticketMachine", false); // Disable
```

---

### **11. `toggleInventory()`**
**Called by:** Keyboard handler (pressing 'i') and inventory button click

**Purpose:** Shows/hides the inventory overlay.

**What it does:**
- Toggles the `InventoryView` visibility

**Usage:**
```java
// Called internally when 'i' key is pressed or button clicked
view.toggleInventory();
```

---

## **Event System**

### **OnHotspotClicked Event**

**Definition:**
```java
public Event<OnHotspotClickedEventArgs> OnHotspotClicked = new Event<>();

public class OnHotspotClickedEventArgs extends EventArgs {
    public final String hotspotId;  // The ID of the clicked hotspot
}
```

**How it works:**
1. User clicks a hotspot button
2. `addHotspot()` sets up a click handler that fires the event:
   ```java
   hotspotButton.setOnAction(e -> {
       OnHotspotClicked.invoke(this, new OnHotspotClickedEventArgs(hotspot.getId()));
   });
   ```
3. Controller subscribes to this event:
   ```java
   view.OnHotspotClicked.addListener(
       (sender, args) -> onHotspotClicked(sender, args)
   );
   ```
4. Controller receives the hotspot ID and processes the interaction

---

## **Internal Methods (Private)**

These are used internally by `GameView` but not called by other classes:

- `initializeUI()` - Sets up the UI structure
- `addHotspot(HotspotViewModel)` - Creates a clickable button for a hotspot
- `clearHotspots()` - Removes all hotspots when changing rooms
- `handleKeyPress(KeyEvent)` - Handles keyboard input (currently just 'i' for inventory)
- `updateRoomBackground(Room)` - Updates just the background (not used much)

---

## **Layering System**

The view uses a `StackPane` with layers stacked in this order (bottom to top):

1. **Background Image** - Room background
2. **Direction Controller** - Direction buttons (bottom-left)
3. **Inventory Button** - "i" button (bottom-right)
4. **Save Indicator** - "Game saved" label (top-right)
5. **Inventory View** - Inventory overlay (when visible)
6. **Hotspots Layer** - Clickable hotspots (on top so they receive clicks)
7. **Puzzle View** - Puzzle overlays (on top of everything when active)
8. **Message Overlays** - Temporary messages (on top when shown)

---

## **Key Design Patterns**

### **1. Event-Driven Architecture**
- View fires events (e.g., `OnHotspotClicked`)
- Controller subscribes and handles events
- Keeps view and controller decoupled

### **2. View Model Pattern**
- `RoomViewModel` contains all data needed to display a room
- View receives view models, not raw model objects
- Separates presentation data from business logic

### **3. Callback Pattern**
- `setInventoryChangeCallback()` allows controller to react to inventory changes
- View doesn't need to know about game logic

---

## **Summary: Controller's View of GameView**

From the controller's perspective, `GameView` is a **display manager** that:

1. **Shows rooms** - `showRoom()` displays backgrounds and hotspots
2. **Manages puzzles** - `showPuzzleView()` / `hidePuzzleView()` for puzzle overlays
3. **Updates UI** - `updateInventory()`, `applyViewUpdates()`, `showSaveIndicator()`
4. **Fires events** - `OnHotspotClicked` notifies controller of user clicks
5. **Accepts callbacks** - `setInventoryChangeCallback()` for inventory changes

The controller tells the view **what to display**, and the view tells the controller **what the user did** (via events).

