package com.chainstaysoftware.filechooser;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * JavaFx Service to update a {@link List} of {@link DirectoryListItem} with
 * the files/directories found at the passed in {@link DirectoryStream}. This code
 * is not run on the JavaFx thread so that the UI does not block while retreiving the
 * list of files from the OS.
 */
class UpdateDirectoryList extends Service<Void> {
   private static Logger logger = Logger.getLogger("com.chainstaysoftware.filechooser.UpdateDirectoryList");

   private final DirectoryStream<Path> dirStream;
   private final DirectoryStream<Path> unfilteredDirStream;
   private final List<DirectoryListItem> itemList;
   private final CountDownLatch latch = new CountDownLatch(1);

   /**
    * Constructor
    * @param dirStream Stream of all files/directories that matched the used file filter.
    * @param unfilteredDirStream Stream of all files/directories that did NOT match the used file
    *                            filter. This is used to find directories that were excluded
    *                            by the filter.
    * @param itemList List to update with results.
    */
   UpdateDirectoryList(final DirectoryStream<Path> dirStream,
                       final DirectoryStream<Path> unfilteredDirStream,
                       final List<DirectoryListItem> itemList) {
      this.dirStream = dirStream;
      this.unfilteredDirStream = unfilteredDirStream;
      this.itemList = itemList;
   }

   protected Task<Void> createTask() {
      return new UpdateListTask();
   }

   private class UpdateListTask extends Task<Void> {
      @Override
      protected Void call() throws Exception {
         update(dirStream, false);
         if (isCancelled()) {
            return null;
         }

         update(unfilteredDirStream, true);

         Platform.runLater(latch::countDown);

         latch.await();

         return null;
      }

      private void update(final DirectoryStream<Path> directoryStream,
                          final boolean dirOnly)  {
         final List<DirectoryListItem> itemsToAdd = new LinkedList<>();

         try {
            for (Path path: directoryStream) {
               if (isCancelled()) {
                  return;
               }

               if (!dirOnly || path.toFile().isDirectory()) {
                  final DirectoryListItem dirListItem = getDirListItem(path.toFile());
                  itemsToAdd.add(dirListItem);

                  if (shouldSchedule(itemsToAdd)) {
                     scheduleJavaFx(itemsToAdd);
                     itemsToAdd.clear();
                  }
               }
            }

            scheduleJavaFx(itemsToAdd);
         } finally {
            closeStream(dirStream);
         }
      }

      private boolean shouldSchedule(final List<DirectoryListItem> itemsToUpdate) {
         return itemsToUpdate.size() % 100 == 0;
      }

      private void scheduleJavaFx(final List<DirectoryListItem> itemsToUpdate) {
         final List<DirectoryListItem> temp = new LinkedList<>();
         temp.addAll(itemsToUpdate);

         Platform.runLater(() -> itemList.addAll(temp));
      }

      private DirectoryListItem getDirListItem(final File file) {
         return new DirectoryListItem(file);
      }

      private void closeStream(final DirectoryStream<Path> directoryStream) {
         try {
            directoryStream.close();
         } catch (IOException e) {
            logger.log(Level.WARNING, "Error closing directory stream", e);
         }
      }
   }
}
