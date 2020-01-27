package io.github.bennofs.wdumper;

import io.github.bennofs.wdumper.database.Database;
import kong.unirest.ProgressMonitor;

import java.io.Closeable;
import java.util.Timer;
import java.util.TimerTask;

class UploadProgressMonitor implements ProgressMonitor, Closeable {
    private final Database db;
    private final int id;
    private long progress;
    private final Timer timer;

    public UploadProgressMonitor(Database db, final int id) {
        this.db = db;
        this.id = id;
        this.progress = 0;
        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    db.setUploadProgress(id, progress);
                } catch(Exception e) {
                    System.out.println("error while updating upload progress, ignored");
                    e.printStackTrace();
                }
            }
        }, 0, 1000 * 20);
    }

    @Override
    public void accept(String field, String fileName, Long bytesWritten, Long totalBytes) {
        this.progress = bytesWritten;
    }

    public void close() {
        db.setUploadProgress(id, progress);
        this.timer.cancel();
    }
}
