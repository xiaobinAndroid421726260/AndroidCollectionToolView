package com.dbz.view.view.panel;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import androidx.annotation.Nullable;
import androidx.core.os.BuildCompat;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;


import com.vanniktech.emoji.EmojiEditText;

import java.util.ArrayList;
import java.util.List;

public class AtUserEditText extends EmojiEditText {

    private List<OnSelectionChangeListener> onSelectionChangeListeners;

    private OnSelectionChangeListener onSelectionChangeListener;

    private OnCopyAndPasteEventListener onCopyAndPasteEventListener;

    public AtUserEditText(Context context) {
        super(context);
    }

    public AtUserEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTextContextMenuItem(int id) {
        switch (id) {
            // 剪切
            case android.R.id.cut:
                if (onCopyAndPasteEventListener != null) {
                    onCopyAndPasteEventListener.onCutEvent();
                }
                break;
            // 复制
            case android.R.id.copy:
                if (onCopyAndPasteEventListener != null) {
                    onCopyAndPasteEventListener.onCopyEvent();
                }
                break;
            // 粘贴
            case android.R.id.paste:
                if (onCopyAndPasteEventListener != null) {
                    onCopyAndPasteEventListener.onPasteEvent();
                }
                break;
        }
        return super.onTextContextMenuItem(id);
    }

    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        super.onSelectionChanged(selStart, selEnd);
        if (onSelectionChangeListener != null) {
            onSelectionChangeListener.onSelectionChange(selStart, selEnd);
        }
        if (onSelectionChangeListeners != null) {
            for (int i = 0; i < onSelectionChangeListeners.size(); i++) {
                onSelectionChangeListeners.get(i).onSelectionChange(selStart, selEnd);
            }
        }
    }

    public void addOnSelectionChangeListener(OnSelectionChangeListener onSelectionChangeListener) {
        if (onSelectionChangeListeners == null) {
            onSelectionChangeListeners = new ArrayList<>();
        }
        onSelectionChangeListeners.add(onSelectionChangeListener);
    }

    public void removeOnSelectionChangedListener(OnSelectionChangeListener onSelectionChangeListener) {
        if (onSelectionChangeListeners != null) {
            onSelectionChangeListeners.remove(onSelectionChangeListener);
        }
    }

    public void clearOnSelectionChangedListener() {
        if (onSelectionChangeListeners != null) {
            onSelectionChangeListeners.clear();
        }
    }

    public void setOnSelectionChangeListener(OnSelectionChangeListener onSelectionChangeListener) {
        this.onSelectionChangeListener = onSelectionChangeListener;
    }

    public void setOnCopyAndPasteEventListener(OnCopyAndPasteEventListener listener) {
        this.onCopyAndPasteEventListener = listener;
    }

    public interface OnSelectionChangeListener {
        void onSelectionChange(int selStart, int selEnd);
    }

    public interface OnCopyAndPasteEventListener {
        void onCutEvent();

        void onCopyEvent();

        void onPasteEvent();
    }

    @Nullable
    @Override
    public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
        final InputConnection ic = super.onCreateInputConnection(editorInfo);
        EditorInfoCompat.setContentMimeTypes(editorInfo,
                new String [] {"image/png"});

        final InputConnectionCompat.OnCommitContentListener callback =
                new InputConnectionCompat.OnCommitContentListener() {
                    @Override
                    public boolean onCommitContent(InputContentInfoCompat inputContentInfo,
                                                   int flags, Bundle opts) {
                        // read and display inputContentInfo asynchronously
                        if (BuildCompat.isAtLeastNMR1() && (flags &
                                InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0) {
                            try {
                                inputContentInfo.requestPermission();
                            }
                            catch (Exception e) {
                                return false; // return false if failed
                            }
                        }

                        // read and display inputContentInfo asynchronously.
                        // call inputContentInfo.releasePermission() as needed.

                        return true;  // return true if succeeded
                    }
                };
        return InputConnectionCompat.createWrapper(ic, editorInfo, callback);
    }
}