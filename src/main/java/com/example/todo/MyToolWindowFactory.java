package com.example.todo;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.*;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBList;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MyToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        DefaultListModel<String> allTodos = new DefaultListModel<>();
        DefaultListModel<String> filteredTodos = new DefaultListModel<>();
        JBList<String> todoList = new JBList<>(filteredTodos);
        JTextField searchField = new JTextField(20);

        Runnable filter = () -> {
            filteredTodos.clear();
            String keyword = searchField.getText().trim().toLowerCase();
            for (int i = 0; i < allTodos.size(); i++) {
                String todo = allTodos.get(i);
                if (todo.toLowerCase().contains(keyword)) {
                    filteredTodos.addElement(todo);
                }
            }
        };

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                filter.run();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                filter.run();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                filter.run();
            }
        });

        BorderLayout borderLayout = new BorderLayout();
        JPanel panel = new JPanel(borderLayout);
        JBScrollPane jbScrollPane = new JBScrollPane(todoList);
        panel.add(searchField, BorderLayout.NORTH);
        panel.add(jbScrollPane, BorderLayout.CENTER);

        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(panel, "TODOs", false);
        toolWindow.getContentManager().addContent(content);

        Runnable scanTodos = () -> {
            DefaultListModel<String> tempTodos = new DefaultListModel<>();

            VirtualFile[] contentRoots = ProjectRootManager.getInstance(project).getContentRoots();

            for (VirtualFile contentRoot : contentRoots) {
                VfsUtilCore.visitChildrenRecursively(contentRoot, new VirtualFileVisitor<Void>() {
                    @Override
                    public boolean visitFile(@NotNull VirtualFile file) {
                        if (!file.isDirectory() && file.getName().endsWith(".kt")) {
                            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                                String line;
                                int lineNumber = 1;
                                while ((line = reader.readLine()) != null) {
                                    if (line.toLowerCase().contains("todo")) {
                                        String todo = "File: " + file.getName() + " | Line " + lineNumber + ": " + line.trim();
                                        tempTodos.addElement(todo);
                                    }
                                    lineNumber++;
                                }
                            } catch (Exception e) {
                                tempTodos.addElement("Failed to read file: " + file.getName());
                            }
                        }
                        return true;
                    }
                });

            };



            ApplicationManager.getApplication().invokeLater(() -> {
                allTodos.clear();
                for (int i = 0; i < tempTodos.size(); i++) {
                    allTodos.addElement(tempTodos.get(i));
                }

                filter.run();
            });
        };

        ApplicationManager.getApplication().executeOnPooledThread(scanTodos);

        VirtualFileManager.getInstance().addVirtualFileListener(new VirtualFileListener(){
            @Override
            public void contentsChanged(@NotNull VirtualFileEvent event) {
                VirtualFile file = event.getFile();
                if (!file.isDirectory() && file.getName().endsWith(".kt")) {
                    ApplicationManager.getApplication().executeOnPooledThread(scanTodos);
                }
            }

            @Override
            public void fileCreated(@NotNull VirtualFileEvent event) {
                VirtualFile file = event.getFile();
                if (!file.isDirectory() && file.getName().endsWith(".kt")) {
                    ApplicationManager.getApplication().executeOnPooledThread(scanTodos);
                }
            }

            @Override
            public void fileDeleted(@NotNull VirtualFileEvent event) {
                ApplicationManager.getApplication().executeOnPooledThread(scanTodos);
            }

        }, toolWindow.getDisposable());
    }

    @Override
    public boolean isApplicable(@NotNull Project project) {
        return true;
    }
}
