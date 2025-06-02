package com.example.todo;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.markup.*;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.awt.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TodoHighlighter implements FileEditorManagerListener {

    private static final Pattern TODO_PATTERN = Pattern.compile("//\\s*TODO.*", Pattern.CASE_INSENSITIVE);

    @Override
    public void selectionChanged(FileEditorManagerEvent event) {
        VirtualFile file = event.getNewFile();
        Project project = event.getManager().getProject();

        if (file != null && file.getName().endsWith(".kt")) {
            Editor[] editors = EditorFactory.getInstance().getEditors(event.getManager().getSelectedTextEditor().getDocument(), project);

            for (Editor editor : editors) {
                highlightTodos(editor.getDocument(), editor);
            }
        }
    }

    private void highlightTodos(Document document, Editor editor) {
        MarkupModel markupModel = editor.getMarkupModel();
        markupModel.removeAllHighlighters();

        String text = document.getText();
        Matcher matcher = TODO_PATTERN.matcher(text);

        while (matcher.find()) {
            int startOffset = matcher.start();
            int endOffset = matcher.end();

            TextAttributes attributes = new TextAttributes();
            attributes.setBackgroundColor(Color.YELLOW);
            attributes.setForegroundColor(Color.RED);
            attributes.setEffectType(EffectType.LINE_UNDERSCORE);
            attributes.setEffectColor(Color.RED);

            markupModel.addRangeHighlighter(
                    startOffset,
                    endOffset,
                    HighlighterLayer.WARNING,
                    attributes,
                    HighlighterTargetArea.EXACT_RANGE
            );
        }
    }
}