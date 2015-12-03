package com.chainstaysoftware.filechooser;

import com.chainstaysoftware.filechooser.preview.PreviewPane;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.File;

public interface FileChooserFx {
   ObservableList<FileChooser.ExtensionFilter> getExtensionFilters();

   ObjectProperty<FileChooser.ExtensionFilter> selectedExtensionFilterProperty();

   void setSelectedExtensionFilter(FileChooser.ExtensionFilter filter);

   FileChooser.ExtensionFilter getSelectedExtensionFilter();

   ObservableMap<String, Class<? extends PreviewPane>> getPreviewHandlers();

   void setInitialDirectory(File value);

   File getInitialDirectory();

   ObjectProperty<File> initialDirectoryProperty();

   void setInitialFileName(String value);

   String getInitialFileName();

   ObjectProperty<String> initialFileNameProperty();

   void setTitle(String value);

   String getTitle();

   StringProperty titleProperty();

   void setShowHiddenFiles(boolean value);

   boolean showHiddenFiles();

   BooleanProperty showHiddenFilesProperty();

   /**
    * List of directories to show in the Favorites list. As favorites are add and removed
    * by the user, the list is updated.
    */
   ObservableList<File> getFavoriteDirs();

   /**
    * Sets callbacks for when user wants to add and/or remove director favorites.
    * This method MUST be called with non-null {@link FavoritesCallback} instances
    * for the add/remove favorites buttons to be included in the displayed FileChooserFx
    * instance.
    */
   void setFavoriteDirsCallbacks(FavoritesCallback addFavorite, FavoritesCallback removeFavorite);

   /**
    * Sets the callback for the help button. This method MUST be called with a
    * non-null {@link HelpCallback} for the Help button to be included in
    * the displayed FileChooserFx instance.
    */
   void setHelpCallback(HelpCallback helpCallback);

   void showOpenDialog(Window ownerWindow, FileChooserCallback fileChooserCallback);

   void showSaveDialog(Window ownerWindow, FileChooserCallback fileChooserCallback);
}
