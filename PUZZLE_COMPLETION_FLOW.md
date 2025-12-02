# Puzzle Completion Flow

This document explains the complete flow when a puzzle is finished.

## Step-by-Step Flow

### 1. User Completes Last Question
**Location:** `TicketMachinePuzzleView.completeQuiz()`
- User answers the last question correctly
- Clicks "Complete Quiz" button
- `completeQuiz()` method is called

### 2. Check if All Answers Correct
**Location:** `TicketMachinePuzzleView.completeQuiz()` (line 364)
```java
if (ticketPuzzle.isAllCorrect()) {
```
- Checks if all quiz answers were correct
- If yes, proceeds to solve puzzle
- If no, shows retry option

### 3. Call Puzzle's interact() Method
**Location:** `TicketMachinePuzzleView.completeQuiz()` (line 375)
```java
InteractionResult result = ticketPuzzle.interact("complete", null);
```
- Calls `TicketPuzzle.interact("complete", null)`
- This is where puzzle logic executes

### 4. Puzzle Logic Executes
**Location:** `TicketPuzzle.interact()` (line 46-58)
- Checks if all answers correct AND ticket not already obtained
- Sets `ticketObtained = true`
- Sets `solved = true`
- Creates `InteractionResult` with:
  - Type: `PUZZLE_SOLVED`
  - View update: `"enableHotspot:securityDoor"`
  - Dialogue: `"You got ticket number X!"`
- Returns the `InteractionResult`

### 5. Show Ticket Overlay
**Location:** `TicketMachinePuzzleView.completeQuiz()` (line 377-378)
```java
if (result.getType() == InteractionResult.ResultType.PUZZLE_SOLVED) {
    showTicketItemOverlay();
```
- Displays ticket item image overlay
- Shows "Ticket Obtained!" message
- Overlay fades in with animation

### 6. Schedule Event Trigger (After 3 seconds)
**Location:** `TicketMachinePuzzleView.completeQuiz()` (line 381-386)
```java
javafx.animation.Timeline timeline = new javafx.animation.Timeline(
    new javafx.animation.KeyFrame(javafx.util.Duration.seconds(3), e -> {
        puzzle.OnPuzzleSolved.invoke(puzzle, new Puzzle.OnPuzzleSolvedEventArgs(puzzle.getId(), result));
    })
);
timeline.play();
```
- Waits 3 seconds (to show overlay)
- Then triggers `puzzle.OnPuzzleSolved` event
- Passes puzzle ID and InteractionResult

### 7. Event Handler in GameController
**Location:** `GameController.showPuzzleView()` (line 232-234)
```java
puzzle.OnPuzzleSolved.addListener((sender, args) -> {
    handleInteractionResult(args.result);
});
```
- Listener was set up when puzzle view was created
- Now receives the event
- Calls `handleInteractionResult(args.result)`

### 8. Process Interaction Result
**Location:** `GameController.handleInteractionResult()` (line 98-142)
- Switches on `result.getType()`
- For `PUZZLE_SOLVED`:
  - **Line 108:** `view.hidePuzzleView()` - Removes puzzle overlay
  - **Line 110:** `view.applyViewUpdates(result.getViewUpdates())` - Applies view changes

### 9. Hide Puzzle View
**Location:** `GameView.hidePuzzleView()` (line 253-258)
```java
public void hidePuzzleView() {
    if (currentPuzzleView != null) {
        getChildren().remove(currentPuzzleView);
        currentPuzzleView = null;
    }
}
```
- Removes puzzle view from the scene
- User can now see the game room again

### 10. Apply View Updates
**Location:** `GameView.applyViewUpdates()` (line 263-276)
```java
public void applyViewUpdates(java.util.List<String> updates) {
    for (String update : updates) {
        if (update.startsWith("enableHotspot:")) {
            String hotspotId = update.substring("enableHotspot:".length());
            updateHotspot(hotspotId, true);
        }
        // ... other update types
    }
}
```
- Processes each view update string
- For `"enableHotspot:securityDoor"`:
  - Extracts `"securityDoor"` as hotspot ID
  - Calls `updateHotspot("securityDoor", true)`
  - Enables the security door hotspot button

### 11. Update Hotspot State
**Location:** `GameView.updateHotspot()` (line 229-234)
```java
public void updateHotspot(String hotspotId, boolean enabled) {
    Button button = hotspotButtons.get(hotspotId);
    if (button != null) {
        button.setDisable(!enabled);
    }
}
```
- Finds the hotspot button by ID
- Enables/disables the button
- User can now click the security door

## Summary

**Complete Flow:**
1. User completes quiz → `TicketMachinePuzzleView.completeQuiz()`
2. View calls → `ticketPuzzle.interact("complete", null)`
3. Puzzle logic → `TicketPuzzle.interact()` sets solved=true, returns InteractionResult
4. View shows overlay → `showTicketItemOverlay()`
5. After 3 seconds → `puzzle.OnPuzzleSolved.invoke()` (event triggered)
6. Controller receives event → `GameController.handleInteractionResult()`
7. Controller hides puzzle → `view.hidePuzzleView()`
8. Controller applies updates → `view.applyViewUpdates()`
9. View enables hotspot → `updateHotspot("securityDoor", true)`
10. User can now interact with security door!

## Key Components

- **View Layer:** `TicketMachinePuzzleView` - UI and user interaction
- **Model Layer:** `TicketPuzzle` - Puzzle logic and state
- **Controller Layer:** `GameController` - Coordinates view and model
- **Event System:** Events in `Puzzle` class connect model to controller
- **View Updates:** String-based commands to modify game view


