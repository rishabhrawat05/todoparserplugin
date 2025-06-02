# TODO Finder IntelliJ Plugin

An IntelliJ IDEA plugin that scans Kotlin (`.kt`) files in your project for TODO comments and displays them in a tool window. The plugin updates the TODO list in real-time as you modify, create, or delete files.

---

## Features

- **Real-time TODO scanning:** Automatically scans all Kotlin files in the project for TODO comments.
- **Search filter:** Quickly filter TODO items using the search box.
- **Live updates:** Updates the TODO list when files are edited, created, or deleted.
- **Lightweight UI:** Simple tool window with a searchable list.

---

## How It Works

- When the tool window opens, the plugin scans the project's content roots recursively.
- It reads all `.kt` files line by line, searching for lines containing "todo" (case-insensitive).
- Matches are displayed as entries in the tool window with the file name and line number.
- The TODO list automatically updates on file changes thanks to IntelliJ's Virtual File System listener.

---

## Code Highlights

- Uses `VirtualFileVisitor` to recursively visit files in the project.
- Uses `VirtualFileListener` (deprecated API) to detect file changes and trigger rescanning.
- Runs file scanning on a background thread (`executeOnPooledThread`) to keep UI responsive.
- Uses Swing components (`JBList`, `JTextField`) for displaying and filtering TODOs.

---

## Usage

1. Open the tool window labeled **TODOs** in IntelliJ IDEA.
2. View the list of TODO comments found in all Kotlin files.
3. Use the search bar to filter TODO entries by keyword.
4. Edit your Kotlin files, and the TODO list updates automatically.

---


## Sample Screenshots
- Saves the Todo on the todo panel in the right
  
<br/>

![Screenshot 2025-06-02 182020](https://github.com/user-attachments/assets/01e94faf-5ffe-4c73-b2a0-37a2b9c1c3a1)

<br/>

- Update the list with new TODO

<br/>

![Screenshot 2025-06-02 182120](https://github.com/user-attachments/assets/1844beaf-5102-49e1-b468-be8534290478)

<br/>

- Search a TODO based on keyword

<br/>

![Screenshot 2025-06-02 182137](https://github.com/user-attachments/assets/9bdeb13d-f65a-4838-ad11-6bd9ffca2605)




