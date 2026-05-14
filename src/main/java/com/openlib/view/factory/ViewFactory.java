package com.openlib.view.factory;

import javafx.scene.Scene;

public interface ViewFactory {
    Scene createView(String viewType);
}
